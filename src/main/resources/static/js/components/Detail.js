/* 商品详情：商品、规格、库存和评价只展示服务端数据。 */
Comps.Detail = {
  data: () => ({ vi: 0, sel: '', qty: 1, tab: 'info', detail: null, loading: false, loadError: '' }),
  computed: {
    p() { return this.detail || byId(Store.route.id); },
    opts() { return this.p?.specs || []; },
  },
  watch: {
    'S.route.id': { immediate: true, handler() { this.load(); } },
    p(p) { this.vi = 0; this.qty = 1; this.sel = p?.specs?.[0] || ''; },
  },
  methods: {
    async load() {
      /* 详情页要实时价格与库存：总是请求；缓存只做「加载时先画出来」和「失败兜底」 */
      this.detail = null;
      this.loading = true;
      this.loadError = '';
      try {
        this.detail = await API.getProduct(Store.route.id);
      } catch (e) {
        if (!byId(Store.route.id)) {
          this.detail = null;
          this.loadError = e.message || '商品不存在或暂不可售';
        }
      } finally {
        this.loading = false;
      }
    },
    add() { return Cart.add(this.p.id, this.qty, this.sel); },
    buy() {
      if (!Auth.require()) return;
      Store.co = { items: [{ id: this.p.id, spec: this.sel || '默认', qty: this.qty, n: this.p.n }], fromCart: false, key: idempotencyKey(), submitting: false };
    },
    fav() { return Fav.toggle(this.p.id); },
  },
  template: `<div class="w pg"><div class="box" v-if="p">
  <div class="crumb"><a @click="go('#/')">首页</a> / <a @click="S.cat=p.c;go('#/')">{{ p.c }}</a> / {{ p.n }}</div>
  <div class="dtop">
    <div class="gal"><thumb :p="p" :v="vi"></thumb><div class="gth"><thumb v-for="i in 4" :key="i" :p="p" :v="i-1" :class="{on:vi===i-1}" @click="vi=i-1"></thumb></div></div>
    <div class="dr"><h2>{{ p.n }}</h2>
      <div class="rt">已售 {{ sold(p.s) }}<span v-if="p.stock!=null"> · 库存 {{ p.stock }}</span></div>
      <div class="pb">价格 <b>¥{{ p.p }}</b><s v-if="p.o!=null">¥{{ p.o }}</s></div>
      <div class="row"><span>规格</span><div class="opts"><button v-for="o in opts" :key="o" :class="{on:sel===o}" @click="sel=o">{{ o }}</button><span v-if="!opts.length" style="color:var(--mu)">等待后端提供商品规格</span></div></div>
      <div class="row"><span>数量</span><div class="stp"><button :disabled="qty<=1" @click="qty--">-</button><span>{{ qty }}</span><button :disabled="p.stock!=null&&qty>=p.stock" @click="qty++">+</button></div></div>
      <div class="btns"><button class="b1" :disabled="p.stock===0||S.pending.cart" @click="add">加入购物车</button><button class="b2" :disabled="p.stock===0" @click="buy">立即购买</button><button class="b1" style="flex:0 0 90px" :disabled="S.pending.favorite" @click="fav">{{ S.favs.some(id=>String(id)===String(p.id)) ? '已收藏' : '收藏' }}</button></div>
    </div>
  </div>
  <div class="tabs" style="margin-top:24px"><a :class="{on:tab==='info'}" @click="tab='info'">商品详情</a><a :class="{on:tab==='rv'}" @click="tab='rv'">用户评价</a></div>
  <div v-if="tab==='info'">
    <div class="spec"><div>品类</div><div>{{ p.c }}</div><div v-if="p.stock!=null">库存</div><div v-if="p.stock!=null">{{ p.stock }}</div></div>
    <div class="imgs"><thumb v-for="i in 4" :key="i" :p="p" :v="i-1"></thumb></div>
  </div>
  <div v-else><div class="empty">暂无评价数据，评价列表与统计需由后端接口提供。</div></div></div>
  <div class="box" v-else><div class="empty">{{ loading ? '正在加载商品…' : (loadError || '商品不存在') }} <a @click="go('#/')">返回首页</a></div></div></div>`,
};
