/* 首页：分类 + 轮播 + 领券 + 秒杀 + 商品列表 */
Comps.HomeView = {
  data: () => ({ cats: CATS, slides: SLIDES, pool: Store.couponPool, flash: PRODUCTS.slice(0, 5), si: 0, sort: 'def', asc: true, n: 8, t: { h: '00', m: '00', s: '00' } }),
  computed: {
    list() {
      const { cat, q } = Store;
      let l = PRODUCTS.filter(p => (cat === '全部' || p.c === cat) && (!q || p.n.includes(q) || p.c.includes(q)));
      if (this.sort === 'sales') l = [...l].sort((a, b) => b.s - a.s);
      if (this.sort === 'price') l = [...l].sort((a, b) => (this.asc ? a.p - b.p : b.p - a.p));
      return l;
    },
    shown() { return this.list.slice(0, this.n); },
  },
  watch: { 'S.cat'() { this.n = 8; }, 'S.q'() { this.n = 8; } },
  methods: {
    pick(c) { Store.cat = c; Store.q = ''; this.$nextTick(() => this.$refs.list.scrollIntoView({ behavior: 'smooth' })); },
    setSort(k) { this.asc = k === 'price' && this.sort === 'price' ? !this.asc : true; this.sort = k; },
    turn(d) { this.si = (this.si + d + this.slides.length) % this.slides.length; },
    tick() {
      const d = new Date(), e = new Date(d); e.setHours(Math.floor(d.getHours() / 2) * 2 + 2, 0, 0, 0);
      const r = Math.floor((e - d) / 1000); this.t = { h: pad(r / 3600 | 0), m: pad(r % 3600 / 60 | 0), s: pad(r % 60) };
    },
  },
  mounted() { this.tick(); this.timers = [setInterval(this.tick, 1000), setInterval(() => this.turn(1), 5000)]; },
  unmounted() { this.timers.forEach(clearInterval); },
  template: `<div class="w">
  <section class="hero">
    <ul class="cats"><li v-for="c in cats" :key="c.n" :class="{on:S.cat===c.n}" @click="pick(c.n)"><b>{{ c.n }}</b><span>{{ c.s }}</span></li></ul>
    <div class="car">
      <div class="sl" v-for="(s,i) in slides" :key="i" :class="{on:si===i}" :style="{background:s.bg}"><h2>{{ s.t }}</h2><p>{{ s.d }}</p><button @click="pick(s.cat)">{{ s.b }}</button></div>
      <button class="ar l" @click="turn(-1)" aria-label="上一张">&lt;</button><button class="ar r" @click="turn(1)" aria-label="下一张">&gt;</button>
      <div class="dots"><i v-for="(s,i) in slides" :key="i" :class="{on:si===i}" @click="si=i"></i></div>
    </div>
    <aside class="me">
      <div class="u"><div class="av">客</div><div><div>Hi，欢迎回来</div><small style="color:var(--mu)">登录后查看专属优惠</small></div></div>
      <div class="q"><a @click="go('#/me/orders')"><b>{{ S.orders.length }}</b>订单</a><a @click="go('#/me/address')"><b>{{ S.addrs.length }}</b>地址</a><a @click="go('#/me/coupons')"><b>{{ S.coupons.filter(c=>!c.used&&c.exp>Date.now()).length }}</b>优惠券</a></div>
      <div class="note">全场满 99 元包邮，7 天无理由退换。</div>
    </aside>
  </section>
  <section class="box"><div class="bh"><h3>领券中心</h3><small>领取后 7 天内有效，结算时自动选择最优券</small></div>
    <div class="cpl"><coupon-card v-for="c in pool" :key="c.id" :c="c"><button :disabled="Coupons.owned(c)" @click="Coupons.claim(c)">{{ Coupons.owned(c) ? '已领取' : '立即领取' }}</button></coupon-card></div></section>
  <section class="box">
    <div class="bh"><h3>限时秒杀</h3><span class="cd"><em>{{ t.h }}</em> : <em>{{ t.m }}</em> : <em>{{ t.s }}</em></span><small>距本场结束</small></div>
    <div class="flash"><div class="fi" v-for="p in flash" :key="p.id" @click="go('#/item/'+p.id)"><thumb :p="p"></thumb><div class="nm">{{ p.n }}</div>
      <div class="fp">¥{{ p.f }}<s>¥{{ p.p }}</s></div><div class="bar"><i :style="{width:p.pct+'%'}"></i><span>已抢 {{ p.pct }}%</span></div></div></div>
  </section>
  <section class="box" ref="list">
    <div class="chips mchips"><a v-for="c in cats" :key="c.n" :class="{on:S.cat===c.n}" @click="pick(c.n)">{{ c.n }}</a></div>
    <div class="tabs"><a v-for="s in [['def','综合'],['sales','销量'],['price','价格']]" :key="s[0]" :class="{on:sort===s[0]}" @click="setSort(s[0])">{{ s[1] }}<template v-if="s[0]==='price'">{{ asc ? ' ↑' : ' ↓' }}</template></a>
      <span class="cnt">{{ S.q ? '“'+S.q+'” · ' : '' }}{{ S.cat==='全部' ? '' : S.cat + ' · ' }}共 {{ list.length }} 件商品</span></div>
    <div class="grid"><product-card v-for="p in shown" :key="p.id" :p="p"></product-card></div>
    <div class="empty" v-if="!list.length">没有找到相关商品，换个关键词试试，或 <a @click="S.q='';S.cat='全部'">查看全部商品</a></div>
    <button class="more" v-if="shown.length<list.length" @click="n+=8">加载更多</button>
  </section></div>`,
};
