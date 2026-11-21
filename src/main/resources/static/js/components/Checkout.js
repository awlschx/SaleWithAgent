/* 结算弹窗：只收集提交参数；价格、运费、优惠和库存均由服务端核验。 */
Comps.Checkout = {
  data() { return { aid: (Store.addrs.find(a => a.def) || Store.addrs[0] || {}).id, cid: '', adding: false, busy: false }; },
  computed: {
    items() { return (Store.co && Store.co.items) || []; },
    coupons() { return Store.coupons.filter(c => !c.used && c.exp > Date.now()); },
    cp() { return this.coupons.find(c => String(c.uid) === String(this.cid)) || null; },
  },
  methods: {
    close() { if (!this.busy) Store.co = null; },
    async addAddr(a) {
      const id = await Addr.save(a);
      if (id != null) { this.aid = id; this.adding = false; }
    },
    async submit() {
      if (this.busy || !Auth.require()) return;
      const address = Store.addrs.find(x => String(x.id) === String(this.aid));
      if (!address) return toast('请先选择收货地址');
      if (!this.items.length) return toast('结算商品为空');
      const context = Store.co;
      const payload = {
        addressId: address.id,
        couponId: this.cp ? this.cp.uid : null,
        items: this.items.map(i => ({ productId: i.id, spec: i.spec, quantity: i.qty })),
        source: context.fromCart ? 'cart' : 'buyNow',
      };
      this.busy = true;
      context.submitting = true;
      try {
        const order = await Orders.create(payload, context.key);
        await Promise.allSettled([Coupons.loadMine(), context.fromCart ? Cart.load() : Promise.resolve()]);
        if (Store.co === context) Store.co = null;
        toast('订单已提交，应付 ¥' + order.pay);
        go('#/me/orders');
      } catch (e) {
        errorMessage('提交订单', e);
      } finally {
        context.submitting = false;
        this.busy = false;
      }
    },
  },
  template: `<div class="mask" @click.self="close"><div class="dlg co" role="dialog"><button class="x" :disabled="busy" @click="close" aria-label="关闭">×</button>
    <h2 style="font-size:18px">确认订单</h2>
    <h4>收货地址</h4>
    <div class="ai" :class="{on:String(aid)===String(a.id)}" v-for="a in S.addrs" :key="a.id" @click="aid=a.id"><input type="radio" :checked="String(aid)===String(a.id)"><div><b>{{ a.name }}</b> {{ a.phone }}<div class="sp">{{ a.region }} {{ a.detail }}</div></div></div>
    <addr-form v-if="adding||!S.addrs.length" @save="addAddr" @cancel="adding=false"></addr-form>
    <a v-else style="color:var(--pr)" @click="adding=true">+ 使用新地址</a>
    <h4>商品清单</h4>
    <div class="sm" v-for="i in items" :key="i.id+i.spec"><span>{{ i.n || byId(i.id)?.n || '商品 '+i.id }}（{{ i.spec }}）× {{ i.qty }}</span></div>
    <h4>优惠券</h4>
    <div class="fm" style="margin:0"><select v-model="cid"><option value="">不使用优惠券</option><option v-for="c in coupons" :key="c.uid" :value="c.uid">{{ c.t }}</option></select></div>
    <div class="sp" style="margin-top:6px">优惠券是否适用、订单金额与库存以服务端核验结果为准。</div>
    <div style="margin-top:14px"><div class="sm t"><span>订单金额</span><b>提交后由服务端计算</b></div></div>
    <button class="go" style="margin-top:14px;width:100%" :disabled="busy||!aid" @click="submit">{{ busy ? '正在提交…' : '提交订单' }}</button></div></div>`,
};
