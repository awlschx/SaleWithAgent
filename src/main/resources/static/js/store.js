/* 服务端驱动的页面状态与用例编排。这里不持久化业务数据，也不伪造接口失败后的成功状态。 */
const readSessionToken = () => { try { return sessionStorage.getItem('sg_token') || ''; } catch (e) { return ''; } };
const Store = Vue.reactive({
  cart: [], cartCount: 0, cartSelectedAmount: 0,
  favs: [], orders: [], addrs: [], coupons: [], couponPool: [], flash: [], banners: [],
  user: null, token: readSessionToken(), authReady: false,
  catalogTotal: 0, catalogPage: 1, catalogSize: 8, catalogView: [], catalogHasMore: false,
  catalogLoading: false, catalogError: '',
  catalogSort: 'def', catalogAsc: true,
  route: { name: 'home' }, cartOpen: false, co: null, rv: null, help: null, msg: '', q: '', cat: '全部',
  pending: { cart: false, coupon: false, address: false, favorite: false },
});

/* 清理旧版本遗留在浏览器中的令牌和私有业务数据。 */
const clearLegacyStorage = () => {
  try {
    ['sg_token', 'sg_user', 'sg_cart', 'sg_favs', 'sg_orders', 'sg_addrs', 'sg_coupons']
      .forEach(k => localStorage.removeItem(k));
  } catch (e) {}
};
clearLegacyStorage();

const toast = m => { Store.msg = m; clearTimeout(toast.t); toast.t = setTimeout(() => (Store.msg = ''), 2200); };
const go = h => { location.hash = h; };
const errorMessage = (action, e) => toast(action + '失败：' + (e.message || '请稍后重试'));

const Catalog = {
  /* 契约过渡：后端 GET /api/products 暂不支持 q/cat/sort/page 参数，返回完整目录。
     完整数据已在客户端时，关键词/分类/排序属于纯展示层视图变换（不涉及价格、
     权限等业务规则）；后端实现查询参数后，serverPaged 为真，此函数即被绕过。 */
  localView(list) {
    let view = list;
    if (Store.q) view = view.filter(p => p.n.includes(Store.q) || p.c.includes(Store.q));
    if (Store.cat !== '全部') view = view.filter(p => p.c === Store.cat);
    if (Store.catalogSort === 'sales') view = [...view].sort((a, b) => (a.s - b.s) * (Store.catalogAsc ? 1 : -1));
    if (Store.catalogSort === 'price') view = [...view].sort((a, b) => (a.p - b.p) * (Store.catalogAsc ? 1 : -1));
    return view;
  },
  async load(append = false) {
    if (Store.catalogLoading) return;
    const page = append ? Store.catalogPage + 1 : 1;
    Store.catalogLoading = true;
    Store.catalogError = '';
    try {
      const result = await API.getProducts({
        page, size: Store.catalogSize, q: Store.q,
        cat: Store.cat === '全部' ? '' : Store.cat,
        sort: Store.catalogSort,
        order: Store.catalogSort === 'price' ? (Store.catalogAsc ? 'asc' : 'desc') : '',
      });
      if (result.serverPaged) {
        applyProducts(append ? [...PRODUCTS, ...result.items] : result.items);
        /* 加载更多要把新一页接在后面，直接覆盖会导致第一页消失、按钮永远不消失 */
        Store.catalogView = append ? [...Store.catalogView, ...result.items] : result.items;
        Store.catalogTotal = result.total;
        Store.catalogPage = result.page;
        Store.catalogHasMore = result.hasMore;
      } else {
        applyProducts(result.items);
        Store.catalogView = this.localView(PRODUCTS);
        Store.catalogTotal = Store.catalogView.length;
        Store.catalogPage = 1;
        Store.catalogHasMore = false;
      }
    } catch (e) {
      if (!append) { applyProducts([]); Store.catalogView = []; Store.catalogTotal = 0; Store.catalogHasMore = false; }
      Store.catalogError = e.message || '商品加载失败';
    } finally {
      Store.catalogLoading = false;
    }
  },
  loadMore() {
    return this.load(true);
  },
  async loadFlash() {
    try {
      const result = await API.getFlashProducts({ page: 1, size: 5 });
      Store.flash = result.items;
    } catch (e) {
      Store.flash = [];
    }
  },
  /* 轮播配置：接口失败保持空数组，由视图回退到内置 SLIDES，首页顶部不会空 */
  async loadBanners() {
    try {
      Store.banners = await API.getBanners();
    } catch (e) {
      /* 回退会掩盖错误，这里必须留下痕迹，否则"接口没生效"看起来像"样式问题" */
      console.warn('[banners] 加载失败，已回退到内置 SLIDES：', e.message);
      Store.banners = [];
    }
  },
};

const Cart = {
  count: () => Store.cartCount,
  sel: () => Store.cart.filter(i => i.on),
  apply(cart) {
    Store.cart = (cart?.items || []).map(i => ({ ...i }));
    Store.cartCount = cart?.totalQuantity || 0;
    Store.cartSelectedAmount = cart?.selectedAmount || 0;
  },
  async load() {
    if (!Store.user) { this.apply(null); return; }
    this.apply(await API.getCart());
  },
  async add(id, qty, spec) {
    if (!Auth.require()) return false;
    /* 契约过渡：ProductsVO 暂无 specs 字段时以中性标签提交，
       后端提供真实规格列表后（mapProduct 已支持 p.specs）自动替换。 */
    return this._write(() => API.addCartItem({ productId: id, spec: spec || '默认', quantity: qty, selected: true }), '已加入购物车');
  },
  async update(item, patch) {
    if (!item?.rid || Store.pending.cart) return false;
    return this._write(() => API.updateCartItem(item.rid, {
      quantity: patch.quantity ?? item.qty, selected: patch.selected ?? item.on,
    }));
  },
  async remove(item) {
    if (!item?.rid) return false;
    return this._write(() => API.removeCartItem(item.rid), '商品已移出购物车');
  },
  async selectAll(on) {
    if (!Store.cart.length) return false;
    return this._write(() => API.selectAllCart(on));
  },
  async deleteSelected() {
    if (!Store.cart.some(i => i.on)) return false;
    return this._write(() => API.removeSelectedCart(), '选中商品已删除');
  },
  async _write(operation, success) {
    if (Store.pending.cart) return false;
    Store.pending.cart = true;
    try {
      this.apply(await operation());
      if (success) toast(success);
      return true;
    } catch (e) {
      errorMessage('购物车操作', e);
      return false;
    } finally {
      Store.pending.cart = false;
    }
  },
};

const Coupons = {
  owned: c => Store.coupons.some(x => String(x.cid) === String(c.id) && !x.used && x.exp > Date.now()),
  async loadAvailable() {
    try { Store.couponPool = await API.getAvailableCoupons(); }
    catch (e) { Store.couponPool = []; }
  },
  async loadMine() {
    if (!Store.user) { Store.coupons = []; return; }
    Store.coupons = await API.getMyCoupons('all');
  },
  async claim(c) {
    if (!Auth.require() || Store.pending.coupon) return false;
    Store.pending.coupon = true;
    try {
      const coupon = await API.claimCoupon(c.id);
      Store.coupons.push(coupon);
      toast('领取成功，已放入我的优惠券');
      return true;
    } catch (e) {
      errorMessage('领取优惠券', e);
      return false;
    } finally {
      Store.pending.coupon = false;
    }
  },
};

const Addr = {
  async load() {
    if (!Store.user) { Store.addrs = []; return; }
    Store.addrs = await API.getAddresses();
  },
  async save(a) {
    if (Store.pending.address) return null;
    Store.pending.address = true;
    try {
      const saved = a.id ? await API.updateAddress(a.id, a) : await API.createAddress(a);
      await this.load();
      return saved.id;
    } catch (e) {
      errorMessage('保存地址', e);
      return null;
    } finally {
      Store.pending.address = false;
    }
  },
  async del(id) {
    if (Store.pending.address) return;
    Store.pending.address = true;
    try { await API.deleteAddress(id); await this.load(); toast('地址已删除'); }
    catch (e) { errorMessage('删除地址', e); }
    finally { Store.pending.address = false; }
  },
  async setDefault(id) {
    if (Store.pending.address) return;
    Store.pending.address = true;
    try { Store.addrs = await API.setDefaultAddress(id); toast('默认地址已更新'); }
    catch (e) { errorMessage('设置默认地址', e); }
    finally { Store.pending.address = false; }
  },
};

const Orders = {
  async load() {
    if (!Store.user) { Store.orders = []; return; }
    Store.orders = await API.getOrders('全部');
  },
  async create(payload, key) {
    const order = await API.createOrder(payload, key);
    if (!Store.orders.some(x => x.no === order.no)) Store.orders.unshift(order);
    return order;
  },
  /* 预结算：只算金额，不落库；和下单共用同一份 payload */
  async preview(payload) {
    return API.previewOrder(payload);
  },
  async cancel(o) {
    const updated = await API.cancelOrder(o.no);
    await Promise.allSettled([this.load(), Coupons.loadMine()]);
    return updated;
  },
  async receive(o) {
    const updated = await API.receiveOrder(o.no);
    await this.load();
    return updated;
  },
  async reorder(o) {
    const cart = await API.reorder(o.no);
    Cart.apply(cart);
    return cart;
  },
};

const Fav = {
  async load() {
    if (!Store.user) { Store.favs = []; return; }
    Store.favs = await API.getFavorites();
  },
  async toggle(id) {
    if (!Auth.require() || Store.pending.favorite) return;
    Store.pending.favorite = true;
    try {
      const on = await API.toggleFavorite(id);
      const index = Store.favs.findIndex(x => String(x) === String(id));
      if (on && index < 0) Store.favs.push(id);
      if (!on && index >= 0) Store.favs.splice(index, 1);
    } catch (e) {
      errorMessage('收藏操作', e);
    } finally {
      Store.pending.favorite = false;
    }
  },
};

const Auth = {
  require() {
    if (Store.user) return true;
    toast(Store.token && !Store.authReady ? '正在校验登录状态' : '请先登录');
    if (Store.authReady || !Store.token) go('#/login');
    return false;
  },
  go(h) { if (this.require()) go(h); },
  apply(data, phone) {
    const d = data || {};
    const token = d.token || d.accessToken || Store.token;
    const u = d.user || d.login || d;
    if (!token || (u.id ?? u.userId) == null) throw new Error('认证响应缺少 token 或用户信息');
    Store.token = token;
    Store.user = {
      id: u.id ?? u.userId,
      phone: u.phone ?? phone ?? '',
      nickname: u.nickname ?? u.nickName ?? u.username ?? '',
    };
    try { sessionStorage.setItem('sg_token', token); } catch (e) {}
  },
  async login(phone, password) {
    const data = await API.login(phone, password);
    this.apply(data, phone);
    await this.reload();
    return data;
  },
  async register(phone, password, nickname) {
    /* 注册只创建账号；即使接口返回 token，也不在这里写入登录态。 */
    return API.register(phone, password, nickname);
  },
  clearPrivateState() {
    Store.user = null;
    Store.token = '';
    Store.cartOpen = false;
    Store.co = null;
    Cart.apply(null);
    Store.favs = [];
    Store.orders = [];
    Store.addrs = [];
    Store.coupons = [];
    try { sessionStorage.removeItem('sg_token'); } catch (e) {}
    clearLegacyStorage();
  },
  onUnauthorized() {
    if (!Store.user && !Store.token) return;
    this.clearPrivateState();
    Store.authReady = true;
    toast('登录状态已失效，请重新登录');
    go('#/login');
  },
  async reload() {
    const token = Store.token;
    const user = Store.user;
    this.clearPrivateState();
    Store.token = token;
    Store.user = user;
    try { sessionStorage.setItem('sg_token', token); } catch (e) {}
    await Promise.allSettled([Cart.load(), Addr.load(), Coupons.loadMine(), Orders.load(), Fav.load()]);
  },
  async logout() {
    try { await API.logout(); } catch (e) {}
    finally {
      this.clearPrivateState();
      go('#/');
      toast('已退出登录');
    }
  },
};

const bootstrap = async () => {
  const availableCoupons = Coupons.loadAvailable();
  if (Store.token) {
    try {
      const me = await API.getMe();
      Auth.apply({ token: Store.token, user: me?.user || me?.login || me });
    } catch (e) {
      Auth.clearPrivateState();
    }
  }
  Store.authReady = true;
  const tasks = [availableCoupons];
  if (Store.user) tasks.push(Cart.load(), Addr.load(), Coupons.loadMine(), Orders.load(), Fav.load());
  await Promise.allSettled(tasks);
};
