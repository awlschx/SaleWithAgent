/* 商品详情页：多图画廊、规格数量、商品详情 / 用户评价 */
Comps.Detail = {
  data: () => ({ vi: 0, sel: '', qty: 1, tab: 'info', rf: 'all', liked: {} }),
  computed: {
    p() { return byId(Store.route.id); },
    opts() { return OPT[this.p.c]; },
    rvs() { return reviewsOf(this.p); },
    shownRv() { const f = this.rf; return this.rvs.filter(r => f === 'all' || (f === 'good' ? r.s >= 4 : f === 'mid' ? r.s === 3 : r.s <= 2)); },
    avg() { return (this.rvs.reduce((a, r) => a + r.s, 0) / this.rvs.length).toFixed(1); },
    good() { return Math.round(this.rvs.filter(r => r.s >= 4).length / this.rvs.length * 100); },
  },
  watch: { p: { immediate: true, handler(p) { this.vi = 0; this.qty = 1; this.tab = 'info'; this.sel = OPT[p.c][0]; } } },
  methods: {
    add() { Cart.add(this.p.id, this.qty, this.sel); toast('已加入购物车'); },
    buy() { Store.co = { items: [{ id: this.p.id, spec: this.sel, qty: this.qty }], fromCart: false }; },
    fav() { Fav.toggle(this.p.id); },
  },
  template: `<div class="w pg"><div class="box">
  <div class="crumb"><a @click="go('#/')">首页</a> / <a @click="S.cat=p.c;go('#/')">{{ p.c }}</a> / {{ p.n }}</div>
  <div class="dtop">
    <div class="gal"><thumb :p="p" :v="vi"></thumb><div class="gth"><thumb v-for="i in 4" :key="i" :p="p" :v="i-1" :class="{on:vi===i-1}" @click="vi=i-1"></thumb></div></div>
    <div class="dr"><h2>{{ p.n }}</h2>
      <div class="rt"><b class="star">{{ stars(Math.round(avg)) }}</b> {{ avg }} 分 · {{ rvs.length }} 条评价 · 已售 {{ sold(p.s) }}</div>
      <div class="pb">价格 <b>¥{{ p.p }}</b><s>¥{{ p.o }}</s></div>
      <div class="row"><span>规格</span><div class="opts"><button v-for="o in opts" :key="o" :class="{on:sel===o}" @click="sel=o">{{ o }}</button></div></div>
      <div class="row"><span>数量</span><div class="stp"><button @click="qty>1&&qty--">-</button><span>{{ qty }}</span><button @click="qty<99&&qty++">+</button></div></div>
      <div class="row"><span>服务</span><div style="color:var(--mu)">满 99 元包邮 · 7 天无理由退换 · 可用优惠券</div></div>
      <div class="btns"><button class="b1" @click="add">加入购物车</button><button class="b2" @click="buy">立即购买</button><button class="b1" style="flex:0 0 90px" @click="fav">{{ S.favs.includes(p.id) ? '已收藏' : '收藏' }}</button></div>
    </div>
  </div>
  <div class="tabs" style="margin-top:24px"><a :class="{on:tab==='info'}" @click="tab='info'">商品详情</a><a :class="{on:tab==='rv'}" @click="tab='rv'">用户评价（{{ rvs.length }}）</a></div>
  <div v-if="tab==='info'">
    <div class="spec"><div>品牌</div><div>拾光优选</div><div>品类</div><div>{{ p.c }}</div><div>发货地</div><div>浙江 杭州</div><div>售后</div><div>7 天无理由</div></div>
    <div class="imgs"><thumb v-for="i in 4" :key="i" :p="p" :v="i-1"></thumb></div>
  </div>
  <div v-else>
    <div class="sum"><b>{{ avg }}</b><div><div class="star">{{ stars(Math.round(avg)) }}</div><div>好评率 {{ good }}%</div></div></div>
    <div class="chips"><a v-for="f in [['all','全部'],['good','好评'],['mid','中评'],['bad','差评']]" :key="f[0]" :class="{on:rf===f[0]}" @click="rf=f[0]">{{ f[1] }}</a></div>
    <div class="empty" v-if="!shownRv.length">这个分类下暂无评价</div>
    <div class="rv" v-for="r in shownRv" :key="r.id"><div class="av">{{ r.u[0] }}</div>
      <div><div>{{ r.u }} <span class="star">{{ stars(r.s) }}</span></div><div>{{ r.t }}</div><small>{{ r.d }} · 规格：{{ r.sp }}</small>
        <div class="up" :class="{on:liked[r.id]}" style="cursor:pointer" @click="liked[r.id]=!liked[r.id]">有用（{{ r.up + (liked[r.id] ? 1 : 0) }}）</div></div></div>
  </div></div></div>`,
};
