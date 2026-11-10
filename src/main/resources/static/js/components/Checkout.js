/* 结算弹窗：地址 → 优惠券 → 汇总 → 提交订单 */
Comps.Checkout = {
  data() { return { aid: (Store.addrs.find(a => a.def) || Store.addrs[0] || {}).id, cid: '', adding: false }; },
  computed: {
    items() { return Store.co.items; },
    sub() { return Cart.sum(this.items); },
    ship() { return this.sub >= 99 ? 0 : 8; },
    avail() { return Store.coupons.filter(c => Coupons.ok(c, this.items)); },
    cp() { return this.avail.find(c => c.uid === this.cid) || null; },
    off() { return this.cp ? this.cp.off : 0; },
    pay() { return Math.max(0, this.sub + this.ship - this.off); },
  },
  created() { const b = [...this.avail].sort((x, y) => y.off - x.off)[0]; this.cid = b ? b.uid : ''; },
  methods: {
    addAddr(a) { this.aid = Addr.save(a); this.adding = false; },
    submit() {
      const a = Store.addrs.find(x => x.id === this.aid);
      if (!a) return toast('请先选择收货地址');
      Store.orders.unshift({ no: 'SG' + Date.now(), t: Date.now(), st: '待发货', addr: { ...a }, sub: this.sub, ship: this.ship, off: this.off, pay: this.pay, cu: this.cp ? this.cp.uid : null,
        items: this.items.map(i => ({ id: i.id, spec: i.spec, qty: i.qty, price: byId(i.id).p })) });
      if (this.cp) this.cp.used = true;
      if (Store.co.fromCart) Store.cart = Store.cart.filter(i => !i.on);
      Store.co = null; toast('订单已提交，应付 ¥' + this.pay + '（演示）'); go('#/me/orders');
    },
  },
  template: `<div class="mask" @click.self="S.co=null"><div class="dlg co" role="dialog"><button class="x" @click="S.co=null" aria-label="关闭">×</button>
    <h2 style="font-size:18px">确认订单</h2>
    <h4>收货地址</h4>
    <div class="ai" :class="{on:aid===a.id}" v-for="a in S.addrs" :key="a.id" @click="aid=a.id"><input type="radio" :checked="aid===a.id"><div><b>{{ a.name }}</b> {{ a.phone }}<div class="sp">{{ a.region }} {{ a.detail }}</div></div></div>
    <addr-form v-if="adding||!S.addrs.length" @save="addAddr" @cancel="adding=false"></addr-form>
    <a v-else style="color:var(--pr)" @click="adding=true">+ 使用新地址</a>
    <h4>商品清单</h4>
    <div class="sm" v-for="i in items" :key="i.id+i.spec"><span>{{ byId(i.id).n }}（{{ i.spec }}）× {{ i.qty }}</span><span>¥{{ byId(i.id).p * i.qty }}</span></div>
    <h4>优惠券</h4>
    <div class="fm" style="margin:0"><select v-model="cid"><option value="">不使用优惠券</option><option v-for="c in avail" :key="c.uid" :value="c.uid">{{ c.t }}：满{{ c.min }}减{{ c.off }}</option></select></div>
    <div class="sp" v-if="!avail.length" style="margin-top:4px">当前订单暂无可用优惠券，<a style="color:var(--pr)" @click="S.co=null;go('#/me/coupons')">去领券</a></div>
    <div style="margin-top:14px"><div class="sm"><span>商品金额</span><span>¥{{ sub }}</span></div><div class="sm"><span>运费（满 99 包邮）</span><span>¥{{ ship }}</span></div><div class="sm"><span>优惠券</span><span>-¥{{ off }}</span></div>
      <div class="sm t"><span>应付</span><b>¥{{ pay }}</b></div></div>
    <button class="go" style="margin-top:14px;width:100%" @click="submit">提交订单</button></div></div>`,
};
