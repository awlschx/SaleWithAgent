/* 应用入口：注册全局属性与组件，挂载根实例 */
const app = Vue.createApp({
  data: () => ({ q: '', hots: ['耳机', 'T恤', '保温杯', '咖啡', '跑鞋', '台灯'], dark: matchMedia('(prefers-color-scheme: dark)').matches }),
  computed: { view() { return { home: 'HomeView', detail: 'Detail', me: 'Me' }[Store.route.name]; } },
  watch: { dark(v) { document.documentElement.dataset.theme = v ? 'dark' : 'light'; } },
  methods: {
    search() { Store.q = this.q.trim(); Store.cat = '全部'; go('#/'); setTimeout(() => { const e = document.querySelector('.tabs'); e && e.scrollIntoView({ behavior: 'smooth' }); }, 80); },
    hot(h) { this.q = h; this.search(); },
    reset() { this.q = ''; Store.q = ''; Store.cat = '全部'; go('#/'); },
  },
  mounted() {
    document.documentElement.dataset.theme = this.dark ? 'dark' : 'light';
    addEventListener('keydown', e => { if (e.key === 'Escape') { Store.co = null; Store.cartOpen = false; } });
  },
});
Object.assign(app.config.globalProperties, { S: Store, byId, sold, stars, fmtTime, day, go, toast, Cart, Coupons, Addr, Orders, Fav });
Object.entries(Comps).forEach(([k, v]) => app.component(k, v));

/* 启动顺序：先拉商品接口，再挂载应用。
   这样首屏渲染时 PRODUCTS 已经是后端数据，不需要额外做响应式替换。
   接口失败（后端没启动/断网）则退回内置演示数据，页面照常可用。
   其余业务数据（购物车/地址/券/订单/收藏）在挂载后并行同步，不阻塞首屏。 */
(async () => {
  try {
    const list = await API.getProducts();
    applyProducts(list);
    console.info('[拾光百货] 商品数据来自后端 /api/products，共 ' + list.length + ' 件');
  } catch (e) {
    console.warn('[拾光百货] 后端商品接口不可用，使用内置演示数据：' + e.message);
  }
  app.mount('#app');
  bootstrap();
})();
