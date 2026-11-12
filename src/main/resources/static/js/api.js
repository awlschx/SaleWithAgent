/* 数据接口层：所有后端请求集中在这里，组件不直接 fetch。
   接口契约见 docs/API-接口清单与后端开发指南.md。
   兼容两种响应：统一响应体 { code, message, data } 与现有的裸 JSON。
   组件统一使用前端字段（n/c/p/o/s/h/f/pct、qty、on、def…），映射在本文件完成。 */
const idempotencyKey = () => 'k' + Date.now().toString(36) + Math.random().toString(36).slice(2, 8);

const API = (() => {
  const BASE = '/api';
  const TIMEOUT = 4000;

  const num = v => (v == null || v === '' ? 0 : Number(v));
  const toMs = v => {
    if (v == null || v === '') return Date.now();
    if (typeof v === 'number') return v < 1e12 ? v * 1000 : v;
    const t = Date.parse(v);
    return Number.isNaN(t) ? Date.now() : t;
  };

  const qs = params => {
    const usp = new URLSearchParams();
    Object.entries(params || {}).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') usp.append(k, v);
    });
    const s = usp.toString();
    return s ? '?' + s : '';
  };

  async function request(path, { method = 'GET', body, headers = {}, timeout = TIMEOUT } = {}) {
    const ctrl = new AbortController();
    const timer = setTimeout(() => ctrl.abort(), timeout);
    try {
      const res = await fetch(BASE + path, {
        method,
        headers: {
          Accept: 'application/json',
          ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
          ...headers,
        },
        body: body !== undefined ? JSON.stringify(body) : undefined,
        signal: ctrl.signal,
      });
      const text = await res.text();
      let json = null;
      if (text) {
        try { json = JSON.parse(text); } catch (e) { json = text; }
      }
      if (!res.ok) {
        const err = new Error((json && typeof json === 'object' && (json.message || json.error)) || ('HTTP ' + res.status));
        err.status = res.status;
        err.code = json && typeof json === 'object' ? json.code : undefined;
        throw err;
      }
      // 统一响应体：code 0（文档）或 200（现有 Result.ok）都算成功
      if (json && typeof json === 'object' && 'code' in json && 'data' in json) {
        if (json.code !== 0 && json.code !== 200) {
          const err = new Error(json.message || ('业务错误 ' + json.code));
          err.code = json.code;
          throw err;
        }
        return json.data;
      }
      return json;
    } finally {
      clearTimeout(timer);
    }
  }

  /* ---------- 后端字段 → 前端组件字段 ---------- */
  const mapProduct = p => ({
    id: p.id,
    n: p.name,
    c: p.category,
    p: num(p.price),
    o: num(p.originalPrice),
    s: num(p.sales),
    h: p.hue,
    f: num(p.flashPrice),
    pct: p.flashPercent,
  });

  const mapAddress = a => ({
    id: a.id,
    name: a.name,
    phone: a.phone,
    region: a.region,
    detail: a.detail,
    def: !!(a.isDefault ?? a.def),
  });

  const mapAddressSnapshot = snap => {
    if (!snap) return { name: '', phone: '', region: '', detail: '' };
    if (typeof snap === 'object') return mapAddress(snap);
    try {
      const o = JSON.parse(snap);
      if (o && typeof o === 'object') return mapAddress(o);
    } catch (e) {}
    return { name: '', phone: '', region: String(snap), detail: '' };
  };

  const mapTemplate = c => ({
    id: c.id,
    t: c.title ?? c.t ?? c.name ?? '',
    min: num(c.minAmount ?? c.min),
    off: num(c.offAmount ?? c.off),
    cat: c.category ?? c.cat ?? '',
  });

  const mapUserCoupon = c => {
    const tpl = c.template || {};
    return {
      cid: c.templateId ?? c.cid ?? tpl.id,
      uid: c.id ?? c.uid,
      t: c.title ?? c.t ?? tpl.title ?? '',
      min: num(c.minAmount ?? c.min ?? tpl.minAmount),
      off: num(c.offAmount ?? c.off ?? tpl.offAmount),
      cat: c.category ?? c.cat ?? tpl.category ?? '',
      used: !!(c.used ?? c.isUsed),
      exp: toMs(c.expireAt ?? c.exp),
    };
  };

  const mapCart = c => {
    const items = (c?.items || []).map(i => ({
      k: (i.productId ?? i.id) + '|' + i.spec,
      id: i.productId ?? i.id,
      rid: i.id,
      spec: i.spec,
      qty: num(i.quantity ?? i.qty),
      on: !!(i.selected ?? i.on),
    }));
    return {
      items,
      totalQuantity: c?.totalQuantity ?? items.reduce((a, i) => a + i.qty, 0),
      selectedAmount: num(c?.selectedAmount),
    };
  };

  const mapOrder = o => ({
    no: o.orderNo ?? o.no,
    t: toMs(o.createdAt ?? o.t),
    st: o.status ?? o.st,
    addr: mapAddressSnapshot(o.addressSnapshot ?? o.addr),
    sub: num(o.itemAmount ?? o.sub),
    ship: num(o.shipFee ?? o.ship),
    off: num(o.discount ?? o.off),
    pay: num(o.payAmount ?? o.pay),
    cu: o.userCouponId ?? o.cu ?? null,
    items: (o.items || []).map(i => ({
      id: i.productId ?? i.id,
      spec: i.spec,
      qty: num(i.quantity ?? i.qty),
      price: num(i.price),
    })),
  });

  return {
    /* 商品 */
    async getProducts(params) {
      const list = await request('/products' + qs(params));
      if (!Array.isArray(list)) throw new Error('商品接口返回格式不正确');
      return list.map(mapProduct);
    },
    async getProduct(id) {
      return mapProduct(await request('/products/' + id));
    },

    /* 购物车 */
    async getCart() { return mapCart(await request('/cart')); },
    async getCartCount() { const r = await request('/cart/count'); return (r && r.count) || 0; },
    async addCartItem(item) {
      return mapCart(await request('/cart/items', { method: 'POST', body: item }));
    },
    /* PUT 全量更新：quantity 和 selected 必须同时传 */
    async updateCartItem(itemId, patch) {
      return mapCart(await request('/cart/items/' + itemId, {
        method: 'PUT',
        body: { quantity: patch.quantity, selected: patch.selected },
      }));
    },
    async selectAllCart(selected) {
      return mapCart(await request('/cart/selected', { method: 'PUT', body: { selected } }));
    },
    async removeCartItem(itemId) {
      return mapCart(await request('/cart/items/' + itemId, { method: 'DELETE' }));
    },
    async removeSelectedCart() {
      return mapCart(await request('/cart/selected', { method: 'DELETE' }));
    },
    async reorder(orderNo) {
      return mapCart(await request('/cart/reorder/' + encodeURIComponent(orderNo), { method: 'POST' }));
    },

    /* 收货地址 */
    async getAddresses() {
      const list = await request('/addresses');
      return (list || []).map(mapAddress);
    },
    async createAddress(a) {
      return mapAddress(await request('/addresses', {
        method: 'POST',
        body: { name: a.name, phone: a.phone, region: a.region, detail: a.detail, isDefault: !!a.def },
      }));
    },
    async updateAddress(id, a) {
      return mapAddress(await request('/addresses/' + id, {
        method: 'PUT',
        body: { name: a.name, phone: a.phone, region: a.region, detail: a.detail, isDefault: !!a.def },
      }));
    },
    async deleteAddress(id) {
      return request('/addresses/' + id, { method: 'DELETE' });
    },
    async setDefaultAddress(id) {
      const list = await request('/addresses/' + id + '/default', { method: 'PUT' });
      return (list || []).map(mapAddress);
    },

    /* 优惠券 */
    async getAvailableCoupons() {
      const list = await request('/coupons/available');
      return (list || []).map(mapTemplate);
    },
    async getMyCoupons(status = 'all') {
      const list = await request('/coupons/mine' + qs({ status }));
      return (list || []).map(mapUserCoupon);
    },
    async claimCoupon(templateId) {
      return mapUserCoupon(await request('/coupons/' + templateId + '/claim', { method: 'POST' }));
    },

    /* 订单 */
    async getOrders(status) {
      const list = await request('/orders' + qs(status && status !== '全部' ? { status } : {}));
      return (list || []).map(mapOrder);
    },
    async getOrder(orderNo) {
      return mapOrder(await request('/orders/' + encodeURIComponent(orderNo)));
    },
    async createOrder(payload) {
      return mapOrder(await request('/orders', {
        method: 'POST',
        body: payload,
        headers: { 'Idempotency-Key': idempotencyKey() },
      }));
    },
    async cancelOrder(orderNo) {
      return mapOrder(await request('/orders/' + encodeURIComponent(orderNo) + '/cancel', { method: 'POST' }));
    },
    async shipOrder(orderNo) {
      return mapOrder(await request('/orders/' + encodeURIComponent(orderNo) + '/ship', { method: 'POST' }));
    },
    async receiveOrder(orderNo) {
      return mapOrder(await request('/orders/' + encodeURIComponent(orderNo) + '/receive', { method: 'POST' }));
    },

    /* 收藏 */
    async getFavorites() {
      const list = await request('/favorites');
      return (list || []).map(p => p.id ?? p.productId);
    },
    async toggleFavorite(productId) {
      const r = await request('/favorites/' + productId + '/toggle', { method: 'POST' });
      return !!(r && (r.favorited ?? r.fav));
    },
  };
})();
