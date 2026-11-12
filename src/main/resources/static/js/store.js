/* 全局状态与业务操作（购物车 / 地址 / 优惠券 / 订单 / 收藏）
   服务端优先：接口可用时用服务端数据覆盖本地，写操作同步到后端；
   接口不可用（后端未启动 / 尚未实现）时回退 localStorage 演示模式，页面不报错。 */
const Store = Vue.reactive({
  cart: [], favs: [], orders: [], addrs: [], coupons: [],
  couponPool: COUPON_POOL.map(c => ({ ...c })),
  route: { name: 'home' }, cartOpen: false, co: null, msg: '', q: '', cat: '全部',
});
['cart', 'favs', 'orders', 'addrs', 'coupons'].forEach(k => {
  try { Store[k] = JSON.parse(localStorage.getItem('sg_' + k)) || []; } catch (e) {}
  Vue.watch(() => Store[k], v => { try { localStorage.setItem('sg_' + k, JSON.stringify(v)); } catch (e) {} }, { deep: true });
});

/* 各模块接口是否可用：启动时探测一次；写操作失败后自动降级，避免每次点击都等超时 */
const Net = Vue.reactive({ cart: false, addr: false, coupons: false, orders: false, fav: false });
const netWarn = (() => {
  const seen = new Set();
  return (key, msg) => {
    if (seen.has(key)) return;
    seen.add(key);
    console.warn('[拾光百货] ' + msg);
  };
})();

const toId = v => (/^\d+$/.test(String(v)) ? Number(v) : v);

const toast = m => { Store.msg = m; clearTimeout(toast.t); toast.t = setTimeout(() => (Store.msg = ''), 1800); };
const go = h => { location.hash = h; };

const Cart = {
  _last: new Map(), _timer: null, _applying: false,
  add(id, qty, spec) {
    const k = id + '|' + spec, it = Store.cart.find(i => i.k === k);
    it ? (it.qty = Math.min(99, it.qty + qty)) : Store.cart.push({ k, id, spec, qty, on: true });
  },
  count: () => Store.cart.reduce((a, i) => a + i.qty, 0),
  sel: () => Store.cart.filter(i => i.on),
  sum: items => items.reduce((a, i) => a + byId(i.id).p * i.qty, 0),

  async load() {
    try {
      this.apply(await API.getCart());
      Net.cart = true;
    } catch (e) {
      Net.cart = false;
      netWarn('cart', '购物车接口不可用，使用本地购物车：' + e.message);
    }
  },
  /* 用服务端购物车覆盖本地（也是服务端写操作的统一回写入口） */
  apply(cart) {
    clearTimeout(this._timer);
    this._applying = true;
    Store.cart = cart.items.map(i => ({ ...i }));
    this.snapshot();
    Vue.nextTick(() => { this._applying = false; });
  },
  snapshot() {
    this._last = new Map(Store.cart.filter(i => i.rid != null).map(i => [String(i.rid), { qty: i.qty, on: i.on }]));
  },
  _schedule() {
    if (!Net.cart || this._applying) return;
    clearTimeout(this._timer);
    this._timer = setTimeout(() => this._push(), 300);
  },
  /* 删除选中：服务端优先，成功后再用服务端数据刷新本地 */
  async deleteSelected() {
    if (!Store.cart.some(i => i.on)) return;
    // 后端购物车不可用时保持本地演示模式
    if (!Net.cart) { Store.cart = Store.cart.filter(i => !i.on); return; }
    if (this._deleting) return;
    this._deleting = true;
    try {
      await API.removeSelectedCart();
      await this.load();   // 成功后以服务端购物车为准
      if (!Net.cart) {
        // 删除成功但刷新失败：本地同步移除，避免显示已删除的条目
        Store.cart = Store.cart.filter(i => !i.on);
        this.snapshot();
      }
    } catch (e) {
      netWarn('cart-del-selected', '删除选中失败：' + e.message);
      toast('删除失败，请重试');
    } finally {
      this._deleting = false;
    }
  },
  /* 全选/取消全选：调批量接口，不再逐条 PUT */
  async selectAll(on) {
    if (!Store.cart.length) return;
    Store.cart.forEach(i => (i.on = on));
    if (!Net.cart) return;
    clearTimeout(this._timer);
    this._applying = true;   // 阻止 watcher 逐条推送
    try {
      await API.selectAllCart(on);
      this.snapshot();
      Vue.nextTick(() => { this._applying = false; });
    } catch (e) {
      netWarn('cart-select-all', '全选接口不可用，改为逐条同步：' + e.message);
      Vue.nextTick(() => { this._applying = false; this._schedule(); });
    }
  },
  /* 购物车抽屉里的数量 / 勾选 / 删除都是直接改本地数组，这里做差异同步 */
  async _push() {
    if (!Net.cart || this._applying) return;
    try {
      const items = Store.cart.map(i => ({ ...i }));
      const live = new Set(items.filter(i => i.rid != null).map(i => String(i.rid)));
      for (const rid of [...this._last.keys()]) {
        if (!live.has(rid)) await API.removeCartItem(rid);
      }
      let needReload = false;
      for (const it of items) {
        if (it.rid == null) {
          await API.addCartItem({ productId: it.id, spec: it.spec, quantity: it.qty, selected: it.on });
          needReload = true;
          continue;
        }
        const prev = this._last.get(String(it.rid)) || {};
        if (prev.qty !== it.qty || prev.on !== it.on) {
          await API.updateCartItem(it.rid, { quantity: it.qty, selected: it.on });
        }
      }
      if (needReload) await this.load();
      else this.snapshot();
    } catch (e) {
      Net.cart = false;
      netWarn('cart-sync', '购物车同步失败，改为本地模式：' + e.message);
    }
  },
};
Vue.watch(() => Store.cart, () => Cart._schedule(), { deep: true });

const Coupons = {
  owned: c => Store.coupons.some(x => String(x.cid) === String(c.id)),

  async loadAvailable() {
    try {
      const list = await API.getAvailableCoupons();
      Store.couponPool.splice(0, Store.couponPool.length, ...list);
    } catch (e) {
      netWarn('coupons-tpl', '领券中心接口不可用，使用本地券模板：' + e.message);
    }
  },
  async loadMine() {
    try {
      Store.coupons = await API.getMyCoupons('all');
      Net.coupons = true;
    } catch (e) {
      Net.coupons = false;
      netWarn('coupons', '优惠券接口不可用，使用本地优惠券：' + e.message);
    }
  },
  claim(c) {
    if (Coupons.owned(c)) return toast('这张券已经领过了');
    if (!Net.coupons) return Coupons.claimLocal(c);
    return API.claimCoupon(c.id).then(uc => {
      if (!Coupons.owned(c)) Store.coupons.push(uc);
      toast('领取成功，已放入我的优惠券');
    }).catch(e => {
      if (e.code === 40900) return toast('这张券已经领过了');
      Net.coupons = false;
      netWarn('coupon-claim', '领券接口不可用，改为本地领取：' + e.message);
      Coupons.claimLocal(c);
    });
  },
  claimLocal(c) {
    if (Coupons.owned(c)) return toast('这张券已经领过了');
    Store.coupons.push({ ...c, cid: c.id, uid: 'u' + Date.now(), used: false, exp: Date.now() + 7 * 864e5 });
    toast('领取成功，已放入我的优惠券');
  },
  // 可用条件：未使用、未过期，且（指定品类的）商品金额达到门槛
  ok(c, items) {
    const base = items.filter(i => !c.cat || byId(i.id).c === c.cat).reduce((a, i) => a + byId(i.id).p * i.qty, 0);
    return !c.used && c.exp > Date.now() && base >= c.min;
  },
};

const Addr = {
  async load() {
    try {
      Store.addrs = await API.getAddresses();
      Net.addr = true;
    } catch (e) {
      Net.addr = false;
      netWarn('addr', '地址接口不可用，使用本地地址：' + e.message);
    }
  },
  async save(a) {
    if (!a.id) {
      if (Net.addr) {
        try {
          const created = await API.createAddress(a);
          this._merge(created);
          return created.id;
        } catch (e) {
          Net.addr = false;
          netWarn('addr-save', '新增地址接口不可用，改为本地保存：' + e.message);
        }
      }
      a.id = 'a' + Date.now();
      Store.addrs.push(a);
      this._default(a);
      return a.id;
    }
    if (Net.addr) {
      try {
        const updated = await API.updateAddress(a.id, a);
        this._merge(updated);
        return updated.id;
      } catch (e) {
        Net.addr = false;
        netWarn('addr-save', '编辑地址接口不可用，改为本地保存：' + e.message);
      }
    }
    const cur = Store.addrs.find(x => String(x.id) === String(a.id));
    if (cur) Object.assign(cur, a); else Store.addrs.push(a);
    this._default(cur || a);
    return a.id;
  },
  async del(id) {
    Store.addrs = Store.addrs.filter(a => String(a.id) !== String(id));
    if (Store.addrs.length && !Store.addrs.some(a => a.def)) Store.addrs[0].def = true;
    if (!Net.addr) return;
    try {
      await API.deleteAddress(id);
      Store.addrs = await API.getAddresses();
    } catch (e) {
      Net.addr = false;
      netWarn('addr-del', '删除地址接口不可用，保持本地状态：' + e.message);
    }
  },
  async setDefault(id) {
    Store.addrs.forEach(a => (a.def = String(a.id) === String(id)));
    if (!Net.addr) return;
    try {
      Store.addrs = await API.setDefaultAddress(id);
    } catch (e) {
      Net.addr = false;
      netWarn('addr-default', '默认地址接口不可用，保持本地状态：' + e.message);
    }
  },
  _merge(addr) {
    const i = Store.addrs.findIndex(x => String(x.id) === String(addr.id));
    if (i >= 0) Store.addrs.splice(i, 1, addr); else Store.addrs.push(addr);
    this._default(addr);
  },
  _default(a) {
    if (Store.addrs.length === 1) a.def = true;
    if (a.def) Store.addrs.forEach(x => (x.def = String(x.id) === String(a.id)));
  },
};

const Orders = {
  async load() {
    try {
      Store.orders = await API.getOrders('全部');
      Net.orders = true;
    } catch (e) {
      Net.orders = false;
      netWarn('orders', '订单接口不可用，使用本地订单：' + e.message);
    }
  },
  async create(payload) {
    const o = await API.createOrder(payload);
    if (!Store.orders.some(x => x.no === o.no)) Store.orders.unshift(o);
    return o;
  },
  async cancel(o) {
    const r = await API.cancelOrder(o.no);
    Object.assign(o, r);
    Coupons.loadMine();
    return o;
  },
  async ship(o) {
    const r = await API.shipOrder(o.no);
    Object.assign(o, r);
    return o;
  },
  async receive(o) {
    const r = await API.receiveOrder(o.no);
    Object.assign(o, r);
    return o;
  },
  async reorder(o) {
    const cart = await API.reorder(o.no);
    Cart.apply(cart);
    return cart;
  },
};

const Fav = {
  async load() {
    try {
      Store.favs = await API.getFavorites();
      Net.fav = true;
    } catch (e) {
      Net.fav = false;
      netWarn('fav', '收藏接口不可用，使用本地收藏：' + e.message);
    }
  },
  toggle(id) {
    const i = Store.favs.indexOf(id);
    const want = i < 0;
    i < 0 ? Store.favs.push(id) : Store.favs.splice(i, 1);
    if (!Net.fav) return;
    API.toggleFavorite(id).then(on => {
      const j = Store.favs.indexOf(id);
      if (on && j < 0) Store.favs.push(id);
      if (!on && j >= 0) Store.favs.splice(j, 1);
    }).catch(e => {
      Net.fav = false;
      netWarn('fav-toggle', '收藏同步失败，保持本地状态：' + e.message);
    });
  },
};

/* 启动后在后台同步服务端数据，不阻塞首屏渲染 */
const bootstrap = () => Promise.allSettled([
  Cart.load(), Addr.load(), Coupons.loadAvailable(), Coupons.loadMine(), Orders.load(), Fav.load(),
]);
