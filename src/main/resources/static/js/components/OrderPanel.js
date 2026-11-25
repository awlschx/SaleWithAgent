/* 消费者订单列表。订单状态只能由服务端动作接口推进。 */
Comps.OrderPanel = {
  data: () => ({ f: '全部', busy: '' }),
  computed: { list() { return Store.orders.filter(o => this.f === '全部' || o.st === this.f); } },
  methods: {
    async receive(o) {
      if (this.busy) return;
      this.busy = o.no;
      try { await Orders.receive(o); toast('已确认收货'); }
      catch (e) { errorMessage('确认收货', e); }
      finally { this.busy = ''; }
    },
    async cancel(o) {
      if (this.busy) return;
      this.busy = o.no;
      try { await Orders.cancel(o); toast('订单已取消'); }
      catch (e) { errorMessage('取消订单', e); }
      finally { this.busy = ''; }
    },
    async again(o) {
      if (this.busy) return;
      this.busy = o.no;
      try { await Orders.reorder(o); Store.cartOpen = true; }
      catch (e) { errorMessage('再次购买', e); }
      finally { this.busy = ''; }
    },
    /* 打开评价弹窗：一个订单项对应一条评价
       spec 必须原样带回（不要兜底成“默认”），否则数据库里 spec 为 NULL/空串的订单项会定位不到 */
    openReview(o, i) {
      if (this.busy || i.reviewed) return;
      if (!Auth.require()) return;
      Store.rv = {
        orderNo: o.no,
        productId: i.id,
        spec: i.spec,
        name: i.n || byId(i.id)?.n || ('商品 ' + i.id),
      };
    },
  },
  template: `<div><div class="chips"><a v-for="s in ['全部','待发货','待收货','已完成','已取消']" :key="s" :class="{on:f===s}" @click="f=s">{{ s }}</a></div>
    <div class="empty" v-if="!list.length">暂无订单，<a @click="go('#/')">去逛逛</a></div>
    <div class="od" v-for="o in list" :key="o.no">
      <div class="oh"><span>{{ fmtTime(o.t) }}　订单号 {{ o.no }}</span><b :class="'st-'+o.st">{{ o.st }}</b></div>
      <div class="oi" v-for="i in o.items" :key="i.id+i.spec" @click="go('#/item/'+i.id)"><thumb :p="byId(i.id)||{h:0,c:'商品'}"></thumb><div><div>{{ i.n || byId(i.id)?.n || '商品 '+i.id }}</div><div class="sp">{{ i.spec }}</div><a v-if="o.st==='已完成'" class="rvlink" :class="{off:i.reviewed}" @click.stop="openReview(o,i)">{{ i.reviewed ? '已评价' : '评价' }}</a></div><div>¥{{ i.price }}<br><span class="sp">× {{ i.qty }}</span></div></div>
      <div class="of"><span>{{ o.addr.name }} {{ o.addr.phone }} {{ o.addr.region }}{{ o.addr.detail }}</span><span>运费 ¥{{ o.ship }} · 优惠 ¥{{ o.off }}　实付 <b>¥{{ o.pay }}</b></span></div>
      <div class="ob"><button :disabled="!!busy" v-if="o.st==='待发货'" @click="cancel(o)">取消订单</button>
        <button :disabled="!!busy" v-if="o.st==='待收货'" class="pri" @click="receive(o)">确认收货</button><button :disabled="!!busy" v-if="o.st==='已完成'||o.st==='已取消'" @click="again(o)">再次购买</button></div></div></div>`,
};
