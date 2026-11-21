/* 首页：分类导航、领券、真实秒杀数据与服务端商品查询。 */
Comps.HomeView = {
  data: () => ({ cats: CATS, slides: SLIDES, si: 0 }),
  computed: {
    pool() { return Store.couponPool; },
    shown() { return Store.catalogView; },
    flash() { return Store.flash; },
  },
  watch: {
    'S.cat'() { Catalog.load(); },
    'S.q'() { Catalog.load(); },
  },
  methods: {
    pick(c) {
      Store.cat = c;
      Store.q = '';
      this.$nextTick(() => this.$refs.list?.scrollIntoView({ behavior: 'smooth' }));
    },
    setSort(k) {
      Store.catalogAsc = k === 'price' && Store.catalogSort === 'price' ? !Store.catalogAsc : true;
      Store.catalogSort = k;
      Catalog.load();
    },
    turn(d) { this.si = (this.si + d + this.slides.length) % this.slides.length; },
  },
  mounted() {
    Catalog.load();
    Catalog.loadFlash();
    this.timer = setInterval(() => this.turn(1), 5000);
  },
  unmounted() { clearInterval(this.timer); },
  template: `<div class="w">
  <section class="hero">
    <ul class="cats"><li v-for="c in cats" :key="c.n" :class="{on:S.cat===c.n}" @click="pick(c.n)"><b>{{ c.n }}</b><span>{{ c.s }}</span></li></ul>
    <div class="car">
      <div class="sl" v-for="(s,i) in slides" :key="i" :class="{on:si===i}" :style="{background:s.bg}"><h2>{{ s.t }}</h2><p>{{ s.d }}</p><button @click="pick(s.cat)">{{ s.b }}</button></div>
      <button class="ar l" @click="turn(-1)" aria-label="上一张">&lt;</button><button class="ar r" @click="turn(1)" aria-label="下一张">&gt;</button>
      <div class="dots"><i v-for="(s,i) in slides" :key="i" :class="{on:si===i}" @click="si=i"></i></div>
    </div>
    <aside class="me">
      <div class="u">
        <div class="av">{{ S.user ? (S.user.nickname || S.user.phone || '我').slice(0, 1) : '客' }}</div>
        <div v-if="S.user"><div>Hi，{{ S.user.nickname || S.user.phone }}</div><small style="color:var(--mu)"><a @click="Auth.logout()">退出登录</a></small></div>
        <div v-else style="cursor:pointer" @click="go('#/login')"><div>Hi，欢迎回来</div><small style="color:var(--mu)">登录 / 注册查看专属优惠</small></div>
      </div>
      <div class="q"><a @click="Auth.go('#/me/orders')"><b>{{ S.orders.length }}</b>订单</a><a @click="Auth.go('#/me/address')"><b>{{ S.addrs.length }}</b>地址</a><a @click="Auth.go('#/me/coupons')"><b>{{ S.coupons.filter(c=>!c.used&&c.exp>Date.now()).length }}</b>优惠券</a></div>
      <div class="note">商品、优惠与订单信息以服务端返回为准。</div>
    </aside>
  </section>
  <section class="box"><div class="bh"><h3>领券中心</h3><small>领取资格、有效期与使用规则由服务端校验</small></div>
    <div class="cpl"><coupon-card v-for="c in pool" :key="c.id" :c="c"><button :disabled="S.pending.coupon||Coupons.owned(c)" @click="Coupons.claim(c)">{{ Coupons.owned(c) ? '已领取' : '立即领取' }}</button></coupon-card></div>
    <div class="empty" v-if="!pool.length">暂无可以领取的优惠券</div></section>
  <section class="box" v-if="flash.length">
    <div class="bh"><h3>限时秒杀</h3><small>活动价格与进度以服务端数据为准</small></div>
    <div class="flash"><div class="fi" v-for="p in flash" :key="p.id" @click="go('#/item/'+p.id)"><thumb :p="p"></thumb><div class="nm">{{ p.n }}</div>
      <div class="fp">¥{{ p.f }}<s>¥{{ p.p }}</s></div><div class="bar" v-if="p.pct!=null"><i :style="{width:p.pct+'%'}"></i><span>已抢 {{ p.pct }}%</span></div></div></div>
  </section>
  <section class="box" ref="list">
    <div class="chips mchips"><a v-for="c in cats" :key="c.n" :class="{on:S.cat===c.n}" @click="pick(c.n)">{{ c.n }}</a></div>
    <div class="tabs"><a v-for="s in [['def','综合'],['sales','销量'],['price','价格']]" :key="s[0]" :class="{on:S.catalogSort===s[0]}" @click="setSort(s[0])">{{ s[1] }}<template v-if="s[0]==='price'">{{ S.catalogAsc ? ' ↑' : ' ↓' }}</template></a>
      <span class="cnt">{{ S.q ? '“'+S.q+'” · ' : '' }}{{ S.cat==='全部' ? '' : S.cat + ' · ' }}共 {{ S.catalogTotal }} 件商品</span></div>
    <div class="grid"><product-card v-for="p in shown" :key="p.id" :p="p"></product-card></div>
    <div class="empty" v-if="S.catalogLoading">正在加载商品…</div>
    <div class="empty" v-else-if="S.catalogError">商品加载失败：{{ S.catalogError }}</div>
    <div class="empty" v-else-if="!shown.length">没有找到相关商品，换个关键词试试，或 <a @click="S.q='';S.cat='全部'">查看全部商品</a></div>
    <button class="more" v-if="shown.length<S.catalogTotal" :disabled="S.catalogLoading" @click="Catalog.loadMore()">{{ S.catalogLoading ? '加载中…' : '加载更多' }}</button>
  </section></div>`,
};
