/* API 边界：只负责传输、错误规范化和 DTO → 视图模型映射，不实现业务规则。 */
const idempotencyKey = () => globalThis.crypto?.randomUUID?.()
  || 'k' + Date.now().toString(36) + Math.random().toString(36).slice(2, 10);

const API = (() => {
  const BASE = '/api';
  const TIMEOUT = 10000;
  const num = (v, fallback = 0) => {
    const n = Number(v);
    return Number.isFinite(n) ? n : fallback;
  };
  const optionalNum = v => v == null || v === '' ? null : num(v, null);
  const toMs = v => {
    if (v == null || v === '') return 0;
    if (typeof v === 'number') return v < 1e12 ? v * 1000 : v;
    const t = Date.parse(v);
    return Number.isNaN(t) ? 0 : t;
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
      let token = '';
      try { token = sessionStorage.getItem('sg_token') || ''; } catch (e) {}
      const res = await fetch(BASE + path, {
        method,
        credentials: 'same-origin',
        headers: {
          Accept: 'application/json',
          ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
          ...(token ? { Authorization: 'Bearer ' + token } : {}),
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
        if (res.status === 401 && !/^\/auth\/(login|register)$/.test(path) && typeof Auth !== 'undefined') {
          Auth.onUnauthorized();
        }
        throw err;
      }
      if (json && typeof json === 'object' && 'code' in json) {
        if (json.code !== 0 && json.code !== 200) {
          const err = new Error(json.message || ('业务错误 ' + json.code));
          err.code = json.code;
          err.status = res.status;
          throw err;
        }
        return json.data;
      }
      return json;
    } catch (e) {
      if (e.name === 'AbortError') throw new Error('请求超时，请稍后重试');
      throw e;
    } finally {
      clearTimeout(timer);
    }
  }

  const mapProduct = p => ({
    id: p.id,
    n: p.name ?? '',
    c: p.category ?? '',
    p: num(p.price),
    o: optionalNum(p.originalPrice),
    s: num(p.sales),
    h: optionalNum(p.hue),
    stock: optionalNum(p.stock),
    status: p.status ?? '',
    img: p.imageUrl ?? p.image ?? p.avatar ?? '',
    freeShipping: !!p.freeShipping,
    nextDay: !!p.nextDay,
    f: optionalNum(p.flashPrice),
    pct: optionalNum(p.flashPercent),
    /* 秒杀场次：0 表示后端没返回，前端据此退回旧的百分比展示 */
    fStart: toMs(p.flashStartAt),
    fEnd: toMs(p.flashEndAt),
    fStock: optionalNum(p.flashStock),
    specs: Array.isArray(p.specs) ? p.specs.map(s => typeof s === 'string' ? s : (s.spec ?? s.name)).filter(Boolean) : [],
  });
  /* 轮播配置：后端已过滤档期并按 sort 排好，这里只做字段改名，不再排序 */
  const mapBanner = b => ({
    id: b.id,
    t: b.title ?? '',
    d: b.description ?? '',
    b: b.buttonText ?? '',
    bg: b.bgColor ?? '#ff5000',
    img: b.image ?? b.imageUrl ?? b.avatar ?? '',
    lt: b.linkType ?? 'CATEGORY',
    lv: b.linkTarget ?? '',
  });
  const mapAddress = a => ({
    id: a.id,
    name: a.name ?? '',
    phone: a.phone ?? '',
    region: a.region ?? '',
    detail: a.detail ?? '',
    def: !!(a.isDefault ?? a.def),
  });
  const mapAddressSnapshot = snap => {
    if (!snap) return {};
    if (typeof snap === 'object') return mapAddress(snap);
    try { return mapAddress(JSON.parse(snap)); } catch (e) { return { region: String(snap) }; }
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
  const mapCart = c => ({
    items: (c?.items || []).map(i => ({
      k: (i.productId ?? i.id) + '|' + i.spec,
      id: i.productId ?? i.id,
      rid: i.id,
      n: i.name ?? i.productName ?? '',
      spec: i.spec ?? '',
      qty: num(i.quantity ?? i.qty),
      on: !!(i.selected ?? i.on),
      price: optionalNum(i.price),
      subtotal: optionalNum(i.subtotal),
      stock: optionalNum(i.stock),
    })),
    totalQuantity: num(c?.totalQuantity),
    selectedAmount: num(c?.selectedAmount),
  });
  const mapOrder = o => ({
    no: o.orderNo ?? o.no,
    t: toMs(o.createdAt ?? o.t),
    st: (o.status ?? o.st ?? '').trim(),
    addr: mapAddressSnapshot(o.addressSnapshot ?? o.addr),
    sub: num(o.itemAmount ?? o.sub),
    ship: num(o.shipFee ?? o.ship),
    off: num(o.discount ?? o.off),
    pay: num(o.payAmount ?? o.pay),
    cu: o.userCouponId ?? o.cu ?? null,
    items: (o.items || []).map(i => ({
      id: i.productId ?? i.id,
      n: i.productName ?? i.name ?? '',
      spec: i.spec ?? '',
      qty: num(i.quantity ?? i.qty),
      price: num(i.price),
      reviewed: !!i.reviewed,
    })),
  });
  /* 列表响应统一成 { items, total, page, size, hasMore, serverPaged }
     数组 = 后端还没实现分页参数、返回的是完整集合；对象 = 服务端已分页，前端直接信任 */
  const toPage = (data, params, mapper) => {
    const plain = Array.isArray(data);
    const rows = plain ? data : (data?.items || data?.records || data?.list || []);
    const total = plain ? rows.length : num(data?.total, rows.length);
    const page = plain ? 1 : num(data?.page, params?.page || 1);
    const size = plain ? rows.length : num(data?.size, params?.size || rows.length);
    /* 后端给了 hasMore 就以它为准，没给才用 page*size 和 total 推算 */
    const hasMore = plain ? false
      : (typeof data?.hasMore === 'boolean' ? data.hasMore : page * size < total);
    return { items: rows.map(mapper), total, page, size, hasMore, serverPaged: !plain };
  };
  const productPage = (data, params) => toPage(data, params, mapProduct);

  /* 评价：星级、有用数、平均分都来自服务端，前端只做展示，不参与计算 */
  const mapReview = v => ({
    id: v.id,
    u: v.userName ?? v.username ?? v.nickname ?? '匿名用户',
    av: v.avatar ?? '',
    star: num(v.rating),
    c: v.content ?? '',
    spec: v.spec ?? '',
    t: toMs(v.createdAt),
    up: num(v.usefulCount),
    upped: !!v.useful,
    imgs: Array.isArray(v.images) ? v.images.filter(Boolean) : [],
  });
  const mapReviewSummary = s => ({
    avg: num(s?.average),
    total: num(s?.total),
    dist: s?.distribution || {},
  });
  /* 消息中心 */
  const mapMessage = m => ({
    id: m.id,
    t: m.title ?? '',
    c: m.content ?? '',
    ty: m.type ?? '',
    read: !!m.read,
    at: toMs(m.createdAt),
  });

  return {
    async getProducts(params = {}) { return productPage(await request('/products' + qs(params)), params); },
    async getProduct(id) { return mapProduct(await request('/products/' + encodeURIComponent(id))); },
    async getFlashProducts(params = {}) { return productPage(await request('/products/flash' + qs(params)), params); },
    /* 轮播数量有限，正常应是裸数组；同时兼容被包成 {items|records|list} 的情况 */
    async getBanners() {
      const data = await request('/banners');
      const rows = Array.isArray(data) ? data : (data?.items || data?.records || data?.list || []);
      return rows.map(mapBanner);
    },

    /* 评价列表：rating 为空表示不筛选，其余按页取 */
    async getReviews(productId, params = {}) {
      return toPage(await request('/products/' + encodeURIComponent(productId) + '/reviews' + qs(params)), params, mapReview);
    },
    /* 评价统计：平均分、总数、各星级条数，只取一次 */
    async getReviewSummary(productId) {
      return mapReviewSummary(await request('/products/' + encodeURIComponent(productId) + '/reviews/summary'));
    },
    /* 标记评价有用：服务端返回最新的 {usefulCount, useful} */
    async markReviewUseful(reviewId) {
      return request('/reviews/' + encodeURIComponent(reviewId) + '/useful', { method: 'POST' });
    },
    /* 提交评价：评价的对象是「订单项」，所以挂在订单下面 */
    async createReview(orderNo, payload) {
      return request('/orders/' + encodeURIComponent(orderNo) + '/reviews', { method: 'POST', body: payload });
    },

    /* 帮助中心：客服联系方式 */
    async getSupportContact() { return request('/support/contact'); },
    /* 消息中心：列表 + 全部已读 */
    async getMessages(params = {}) {
      return toPage(await request('/messages' + qs(params)), params, mapMessage);
    },
    async readAllMessages() { return request('/messages/read-all', { method: 'POST' }); },
    /* 举报与意见反馈 */
    async submitReport(payload) { return request('/reports', { method: 'POST', body: payload }); },
    async submitFeedback(payload) { return request('/feedback', { method: 'POST', body: payload }); },

    async getCart() { return mapCart(await request('/cart')); },
    async addCartItem(item) { return mapCart(await request('/cart/items', { method: 'POST', body: item })); },
    async updateCartItem(itemId, patch) {
      return mapCart(await request('/cart/items/' + itemId, {
        method: 'PUT', body: { quantity: patch.quantity, selected: patch.selected },
      }));
    },
    async selectAllCart(selected) { return mapCart(await request('/cart/selected', { method: 'PUT', body: { selected } })); },
    async removeCartItem(itemId) { return mapCart(await request('/cart/items/' + itemId, { method: 'DELETE' })); },
    async removeSelectedCart() { return mapCart(await request('/cart/selected', { method: 'DELETE' })); },
    async reorder(orderNo) { return mapCart(await request('/cart/reorder/' + encodeURIComponent(orderNo), { method: 'POST' })); },

    async getAddresses() { return ((await request('/addresses')) || []).map(mapAddress); },
    async createAddress(a) {
      return mapAddress(await request('/addresses', { method: 'POST', body: {
        name: a.name, phone: a.phone, region: a.region, detail: a.detail, isDefault: !!a.def,
      } }));
    },
    async updateAddress(id, a) {
      return mapAddress(await request('/addresses/' + id, { method: 'PUT', body: {
        name: a.name, phone: a.phone, region: a.region, detail: a.detail, isDefault: !!a.def,
      } }));
    },
    async deleteAddress(id) { return request('/addresses/' + id, { method: 'DELETE' }); },
    async setDefaultAddress(id) { return ((await request('/addresses/' + id + '/default', { method: 'PUT' })) || []).map(mapAddress); },

    async getAvailableCoupons() { return ((await request('/coupons/available')) || []).map(mapTemplate); },
    async getMyCoupons(status = 'all') { return ((await request('/coupons/mine' + qs({ status }))) || []).map(mapUserCoupon); },
    async claimCoupon(templateId) { return mapUserCoupon(await request('/coupons/' + templateId + '/claim', { method: 'POST' })); },

    async getOrders(status) { return ((await request('/orders' + qs(status && status !== '全部' ? { status } : {}))) || []).map(mapOrder); },
    async previewOrder(payload) { return mapOrder(await request('/orders/preview', { method: 'POST', body: payload })); },
    async createOrder(payload, key) {
      return mapOrder(await request('/orders', {
        method: 'POST', body: payload, headers: { 'Idempotency-Key': key || idempotencyKey() },
      }));
    },
    async cancelOrder(orderNo) { return mapOrder(await request('/orders/' + encodeURIComponent(orderNo) + '/cancel', { method: 'POST' })); },
    async receiveOrder(orderNo) { return mapOrder(await request('/orders/' + encodeURIComponent(orderNo) + '/receive', { method: 'POST' })); },

    async getFavorites() { return ((await request('/favorites')) || []).map(p => p.id ?? p.productId); },
    async toggleFavorite(productId) {
      const r = await request('/favorites/' + productId + '/toggle', { method: 'POST' });
      return !!(r && (r.favorited ?? r.favorite));
    },

    async login(phone, password) { return request('/auth/login', { method: 'POST', body: { phone, password } }); },
    async register(phone, password, nickname) { return request('/auth/register', { method: 'POST', body: { phone, password, nickname } }); },
    async getMe() { return request('/auth/me'); },
    async logout() { return request('/auth/logout', { method: 'POST' }); },
  };
})();
