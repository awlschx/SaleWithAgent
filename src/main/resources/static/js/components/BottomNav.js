/* 手机端底部导航（≤640px 显示） */
Comps.BottomNav = {
  methods: {
    cls(k) { const r = Store.route.name; return { on: Store.cartOpen ? k === 'cart' : k === 'me' ? r === 'me' : k === 'home' ? r === 'home' : false }; },
    toCats() { go('#/'); setTimeout(() => { const e = document.querySelector('.mchips'); e && e.scrollIntoView({ behavior: 'smooth' }); }, 80); },
  },
  template: `<nav class="bn"><a :class="cls('home')" @click="S.cartOpen=false;go('#/')">首页</a><a @click="S.cartOpen=false;toCats()">分类</a>
    <a :class="cls('cart')" @click="S.cartOpen=true">购物车<i v-if="Cart.count()">{{ Cart.count() }}</i></a><a :class="cls('me')" @click="S.cartOpen=false;go('#/me/orders')">我的</a></nav>`,
};
