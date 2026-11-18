/* 手机端底部导航（≤640px 显示） */
Comps.BottomNav = {
  methods: {
    cls(k) { const r = Store.route.name; return { on: Store.cartOpen ? k === 'cart' : k === 'me' ? r === 'me' : k === 'home' ? r === 'home' : false }; },
    toCats() { go('#/'); setTimeout(() => { const e = document.querySelector('.mchips'); e && e.scrollIntoView({ behavior: 'smooth' }); }, 80); },
  },
  template: `<nav class="bn" aria-label="移动端主导航">
    <a :class="cls('home')" @click="S.cartOpen=false;go('#/')"><span class="bi"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path d="m3 11 9-8 9 8"></path><path d="M5 10v10h14V10M9 20v-6h6v6"></path></svg></span>首页</a>
    <a @click="S.cartOpen=false;toCats()"><span class="bi"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><rect x="3" y="3" width="7" height="7" rx="2"></rect><rect x="14" y="3" width="7" height="7" rx="2"></rect><rect x="3" y="14" width="7" height="7" rx="2"></rect><rect x="14" y="14" width="7" height="7" rx="2"></rect></svg></span>分类</a>
    <a :class="cls('cart')" @click="S.cartOpen=true"><span class="bi"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path d="M3 3h2l2.2 11.2a2 2 0 0 0 2 1.6h7.9a2 2 0 0 0 2-1.6L20.5 7H6"></path><circle cx="10" cy="20" r="1"></circle><circle cx="18" cy="20" r="1"></circle></svg><i v-if="Cart.count()">{{ Cart.count() }}</i></span>购物车</a>
    <a :class="cls('me')" @click="S.cartOpen=false;Auth.go('#/me/orders')"><span class="bi"><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><circle cx="12" cy="8" r="4"></circle><path d="M4 21a8 8 0 0 1 16 0"></path></svg></span>我的</a>
  </nav>`,
};
