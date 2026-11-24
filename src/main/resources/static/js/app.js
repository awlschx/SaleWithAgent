/* 应用入口：注册全局属性与组件，挂载后恢复身份并加载服务端数据。 */
const app = Vue.createApp({
  data: () => ({ q: '', hots: ['耳机', 'T恤', '保温杯', '咖啡', '跑鞋', '台灯'], dark: matchMedia('(prefers-color-scheme: dark)').matches }),
  computed: { view() { return { home: 'HomeView', detail: 'Detail', me: 'Me', login: 'LoginView' }[Store.route.name]; } },
  watch: { dark(v) { document.documentElement.dataset.theme = v ? 'dark' : 'light'; } },
  methods: {
    search() {
      Store.q = this.q.trim();
      Store.cat = '全部';
      go('#/');
      setTimeout(() => document.querySelector('.tabs')?.scrollIntoView({ behavior: 'smooth' }), 80);
    },
    hot(h) { this.q = h; this.search(); },
    reset() { this.q = ''; Store.q = ''; Store.cat = '全部'; go('#/'); },
    /* 导航栏「首页」：回到首页并重新拉取首页数据
       已经在首页时 hash 不会变化、组件不会重建，必须手动调接口，否则看到的是旧数据 */
    home() {
      const already = Store.route.name === 'home';
      this.reset();
      if (already) {
        Catalog.load();
        Catalog.loadFlash();
        Catalog.loadBanners();
      }
    },
    logout() { return Auth.logout(); },
  },
  mounted() {
    document.documentElement.dataset.theme = this.dark ? 'dark' : 'light';
    addEventListener('keydown', e => {
      if (e.key === 'Escape') {
        if (Store.co && !Store.co.submitting) Store.co = null;
        Store.cartOpen = false;
      }
    });
  },
});
Object.assign(app.config.globalProperties, { S: Store, byId, sold, stars, fmtTime, day, go, toast, Catalog, Cart, Coupons, Addr, Orders, Fav, Auth });
Object.entries(Comps).forEach(([k, v]) => app.component(k, v));
app.mount('#app');
bootstrap();
