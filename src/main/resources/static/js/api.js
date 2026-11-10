/* 数据接口层：所有后端请求集中在这里，组件不直接 fetch。
   当前只有商品列表；购物车、订单、优惠券后续都加在这个对象里。 */
const API = {
  /** 拉取商品列表，并把后端字段映射成前端组件使用的形状 */
  async getProducts() {
    // 4 秒超时：后端没起或卡住时快速失败，回退到内置演示数据
    const ctrl = new AbortController();
    const timer = setTimeout(() => ctrl.abort(), 4000);
    try {
      const res = await fetch('/api/products', {
        headers: { Accept: 'application/json' },
        signal: ctrl.signal,
      });
      if (!res.ok) throw new Error('商品接口异常 HTTP ' + res.status);
      const list = await res.json();
      if (!Array.isArray(list)) throw new Error('商品接口返回格式不正确');
      return list.map(p => ({
        id: p.id,
        n: p.name,
        c: p.category,
        p: p.price,
        o: p.originalPrice,
        s: p.sales,
        h: p.hue,
        f: p.flashPrice,
        pct: p.flashPercent,
      }));
    } finally {
      clearTimeout(timer);
    }
  },
};
