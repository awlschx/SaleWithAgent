/* 全局状态与业务操作（购物车 / 地址 / 优惠券），数据持久化到 localStorage */
const Store = Vue.reactive({
  cart: [], favs: [], orders: [], addrs: [], coupons: [],
  route: { name: 'home' }, cartOpen: false, co: null, msg: '', q: '', cat: '全部',
});
['cart', 'favs', 'orders', 'addrs', 'coupons'].forEach(k => {
  try { Store[k] = JSON.parse(localStorage.getItem('sg_' + k)) || []; } catch (e) {}
  Vue.watch(() => Store[k], v => { try { localStorage.setItem('sg_' + k, JSON.stringify(v)); } catch (e) {} }, { deep: true });
});

const toast = m => { Store.msg = m; clearTimeout(toast.t); toast.t = setTimeout(() => (Store.msg = ''), 1800); };
const go = h => { location.hash = h; };

const Cart = {
  add(id, qty, spec) {
    const k = id + '|' + spec, it = Store.cart.find(i => i.k === k);
    it ? (it.qty = Math.min(99, it.qty + qty)) : Store.cart.push({ k, id, spec, qty, on: true });
  },
  count: () => Store.cart.reduce((a, i) => a + i.qty, 0),
  sel: () => Store.cart.filter(i => i.on),
  sum: items => items.reduce((a, i) => a + byId(i.id).p * i.qty, 0),
};

const Coupons = {
  owned: c => Store.coupons.some(x => x.cid === c.id),
  claim(c) {
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
  save(a) {
    if (!a.id) { a.id = 'a' + Date.now(); Store.addrs.push(a); } else Object.assign(Store.addrs.find(x => x.id === a.id), a);
    if (a.def || Store.addrs.length === 1) Store.addrs.forEach(x => (x.def = x.id === a.id));
    return a.id;
  },
  del(id) { Store.addrs = Store.addrs.filter(a => a.id !== id); if (Store.addrs.length && !Store.addrs.some(a => a.def)) Store.addrs[0].def = true; },
};
