/* 订单列表：待发货 → 待收货 → 已完成（演示用手动推进状态） */
Comps.OrderPanel = {
  data: () => ({ f: '全部' }),
  computed: { list() { return Store.orders.filter(o => this.f === '全部' || o.st === this.f); } },
  methods: {
    async next(o) {
      if (Net.orders) {
        try {
          const updated = o.st === '待发货' ? await Orders.ship(o) : await Orders.receive(o);
          toast(updated.st === '待收货' ? '商家已发货' : '已确认收货');
          return;
        } catch (e) {
          Net.orders = false;
          netWarn('order-next', '订单状态接口不可用，改为本地演示状态：' + e.message);
        }
      }
      o.st = o.st === '待发货' ? '待收货' : '已完成';
      toast(o.st === '待收货' ? '商家已发货' : '已确认收货');
    },
    async cancel(o) {
      if (Net.orders) {
        try {
          await Orders.cancel(o);
          toast('订单已取消，优惠券已退回');
          return;
        } catch (e) {
          Net.orders = false;
          netWarn('order-cancel', '取消订单接口不可用，改为本地演示状态：' + e.message);
        }
      }
      o.st = '已取消';
      const c = Store.coupons.find(x => String(x.uid) === String(o.cu));
      if (c) c.used = false;
      toast('订单已取消，优惠券已退回');
    },
    async again(o) {
      if (Net.orders) {
        try {
          await Orders.reorder(o);
          Store.cartOpen = true;
          return;
        } catch (e) {
          Net.orders = false;
          netWarn('order-again', '再次购买接口不可用，改为本地加购：' + e.message);
        }
      }
      o.items.forEach(i => Cart.add(i.id, i.qty, i.spec));
      Store.cartOpen = true;
    },
  },
  template: `<div><div class="chips"><a v-for="s in ['全部','待发货','待收货','已完成','已取消']" :key="s" :class="{on:f===s}" @click="f=s">{{ s }}</a></div>
    <div class="empty" v-if="!list.length">暂无订单，<a @click="go('#/')">去逛逛</a></div>
    <div class="od" v-for="o in list" :key="o.no">
      <div class="oh"><span>{{ fmtTime(o.t) }}　订单号 {{ o.no }}</span><b :class="'st-'+o.st">{{ o.st }}</b></div>
      <div class="oi" v-for="i in o.items" :key="i.id+i.spec" @click="go('#/item/'+i.id)"><thumb :p="byId(i.id)"></thumb><div><div>{{ byId(i.id).n }}</div><div class="sp">{{ i.spec }}</div></div><div>¥{{ i.price }}<br><span class="sp">× {{ i.qty }}</span></div></div>
      <div class="of"><span>{{ o.addr.name }} {{ o.addr.phone }} {{ o.addr.region }}{{ o.addr.detail }}</span><span>运费 ¥{{ o.ship }} · 优惠 ¥{{ o.off }}　实付 <b>¥{{ o.pay }}</b></span></div>
      <div class="ob"><button v-if="o.st==='待发货'" @click="cancel(o)">取消订单</button><button v-if="o.st==='待发货'" class="pri" @click="next(o)">模拟发货</button>
        <button v-if="o.st==='待收货'" class="pri" @click="next(o)">确认收货</button><button v-if="o.st==='已完成'||o.st==='已取消'" @click="again(o)">再次购买</button></div></div></div>`,
};
