/* 商品详情：商品、规格、库存和评价只展示服务端数据。 */
Comps.Detail = {
  data: () => ({
    vi: 0, sel: '', qty: 1, tab: 'info', detail: null, loading: false, loadError: '',
    /* 评价：列表按页取，统计只取一次；loaded 用来避免每次切 tab 都重新请求 */
    rv: { items: [], total: 0, page: 1, size: 5, loading: false, error: '', loaded: false },
    rvSeq: 0,
    sum: null, sumLoading: false,
    rFilter: 'all',
    rvFilters: [
      { k: 'all', n: '全部' }, { k: '5', n: '5星' }, { k: '4', n: '4星' },
      { k: '3', n: '3星' }, { k: '2', n: '2星' }, { k: '1', n: '1星' },
    ],
  }),
  computed: {
    p() { return this.detail || byId(Store.route.id); },
    opts() { return this.p?.specs || []; },
    /* 星级分布：后端给 { "5": 21, "4": 10 ... }，这里补齐没有的星级和百分比 */
    distRows() {
      const dist = this.sum?.dist || {};
      const total = this.sum?.total || 0;
      return [5, 4, 3, 2, 1].map(star => {
        const n = Number(dist[star] ?? 0) || 0;
        return { star, n, pct: total ? Math.round(n / total * 100) : 0 };
      });
    },
  },
  watch: {
    'S.route.id': { immediate: true, handler() { this.load(); this.resetReviews(); } },
    p(p) { this.vi = 0; this.qty = 1; this.sel = p?.specs?.[0] || ''; },
    /* 评价按需加载：第一次切到这个 tab 才发请求 */
    tab(t) { if (t === 'rv' && !this.rv.loaded) { this.loadSummary(); this.loadReviews(); } },
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
    /* 评价列表：append=true 表示「加载更多」，其余都是重新取第一页 */
    async loadReviews(append = false) {
      if (append && this.rv.loading) return;
      const seq = ++this.rvSeq;   // 连续切筛选时只认最后一次返回
      const page = append ? this.rv.page + 1 : 1;
      this.rv.loading = true;
      this.rv.error = '';
      try {
        const res = await API.getReviews(Store.route.id, {
          page, size: this.rv.size,
          rating: this.rFilter === 'all' ? undefined : Number(this.rFilter),
        });
        if (seq !== this.rvSeq) return;
        this.rv.items = append ? [...this.rv.items, ...res.items] : res.items;
        this.rv.total = res.total;
        this.rv.page = res.page;
        this.rv.loaded = true;
      } catch (e) {
        if (seq !== this.rvSeq) return;
        if (!append) { this.rv.items = []; this.rv.total = 0; }
        this.rv.error = e.message || '评价加载失败';
      } finally {
        if (seq === this.rvSeq) this.rv.loading = false;
      }
    },
    /* 评价统计：平均分和星级分布只取一次，不跟列表一起翻页 */
    async loadSummary() {
      if (this.sumLoading) return;
      this.sumLoading = true;
      try { this.sum = await API.getReviewSummary(Store.route.id); }
      catch (e) { this.sum = null; }
      finally { this.sumLoading = false; }
    },
    setFilter(k) {
      if (this.rFilter === k) return;
      this.rFilter = k;
      this.loadReviews();
    },
    resetReviews() {
      this.rvSeq++;   // 让还在路上的旧请求失效
      this.rv = { items: [], total: 0, page: 1, size: 5, loading: false, error: '', loaded: false };
      this.sum = null;
      this.rFilter = 'all';
      if (this.tab === 'rv') { this.loadSummary(); this.loadReviews(); }
    },
    /* 有用：先改界面再发请求，失败回滚，避免点击后没反应 */
    async useful(r) {
      if (r.upped) return;
      if (!Auth.require()) return;
      const beforeUp = r.up;
      r.upped = true;
      r.up = beforeUp + 1;
      try {
        const res = await API.markReviewUseful(r.id);
        const n = Number(res?.usefulCount);
        if (Number.isFinite(n)) r.up = n;
        if (typeof res?.useful === 'boolean') r.upped = res.useful;
      } catch (e) {
        r.up = beforeUp;
        r.upped = false;
        toast(e.message || '操作失败，请稍后重试');
      }
    },
    /* 评价的时间与规格拼成一行，缺哪个都不留空点 */
    meta(r) {
      const parts = [];
      if (r.t) parts.push(day(r.t));
      if (r.spec) parts.push(r.spec);
      return parts.join(' · ');
    },
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
  <div v-else>
    <div class="sum" v-if="sum">
      <b>{{ sum.avg.toFixed(1) }}</b>
      <div>
        <div class="star" style="font-size:15px">{{ stars(Math.round(sum.avg)) }}</div>
        <div class="sp" style="margin-top:3px">共 {{ sum.total }} 条评价</div>
      </div>
      <div class="rvdist" v-if="sum.total">
        <div class="rvbar" v-for="d in distRows" :key="d.star"><span>{{ d.star }}星</span><i><em :style="{width:d.pct+'%'}"></em></i><span>{{ d.n }}</span></div>
      </div>
    </div>
    <div class="empty" v-else-if="sumLoading">正在加载评价统计…</div>

    <div class="chips" v-if="rv.loaded"><a v-for="f in rvFilters" :key="f.k" :class="{on:rFilter===f.k}" @click="setFilter(f.k)">{{ f.n }}</a></div>

    <div class="rv" v-for="r in rv.items" :key="r.id">
      <div class="av">{{ (r.u || '匿').slice(0, 1) }}</div>
      <div>
        <div><span class="who">{{ r.u }}</span> <span class="star">{{ stars(r.star) }}</span></div>
        <small>{{ meta(r) }}</small>
        <div class="tx">{{ r.c }}</div>
        <a class="up" :class="{on:r.upped}" @click="useful(r)">有用 {{ r.up }}</a>
      </div>
    </div>

    <div class="empty" v-if="rv.loading && !rv.items.length">正在加载评价…</div>
    <div class="empty" v-else-if="rv.error">{{ rv.error }} <a @click="loadReviews()">重新加载</a></div>
    <div class="empty" v-else-if="rv.loaded && !rv.items.length">还没有评价，买过的用户可以说说体验。</div>
    <button class="more" v-if="rv.items.length && rv.items.length < rv.total" :disabled="rv.loading" @click="loadReviews(true)">{{ rv.loading ? '加载中…' : '加载更多评价' }}</button>
  </div></div>
  <div class="box" v-else><div class="empty">{{ loading ? '正在加载商品…' : (loadError || '商品不存在') }} <a @click="go('#/')">返回首页</a></div></div></div>`,
};
