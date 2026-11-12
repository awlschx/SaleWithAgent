Comps.CartDrawer = {
  computed: {
    allOn: { get() { return Store.cart.length > 0 && Store.cart.every(i => i.on); }, set(v) { Cart.selectAll(v); } },
    selCount() { return Cart.sel().reduce((a, i) => a + i.qty, 0); },
    total() { return Cart.sum(Cart.sel()); },
  },
  methods: {
    del(k) { Store.cart = Store.cart.filter(i => i.k !== k); },
    delSel() { return Cart.deleteSelected(); },
    checkout() { Store.cartOpen = false; Store.co = { items: Cart.sel().map(({ id, spec, qty }) => ({ id, spec, qty })), fromCart: true }; },
  },
  template: `<div><div class="ov" v-if="S.cartOpen" @click="S.cartOpen=false"></div>
  <aside class="dw" :class="{on:S.cartOpen}">
    <div class="dh"><span>购物车（{{ Cart.count() }}）</span><button @click="S.cartOpen=false" style="font-size:22px">×</button></div>
    <div class="dl"><div class="empty" v-if="!S.cart.length">购物车还是空的<br><a @click="S.cartOpen=false">去逛逛</a></div>
      <div class="ci" v-for="it in S.cart" :key="it.k"><input type="checkbox" v-model="it.on"><thumb :p="byId(it.id)"></thumb>
        <div><div class="nm" @click="S.cartOpen=false;go('#/item/'+it.id)">{{ byId(it.id).n }}</div><div class="sp">规格：{{ it.spec }}</div>
          <div class="ln"><b style="color:var(--pr)">¥{{ byId(it.id).p }}</b>
            <div class="stp"><button @click="it.qty>1&&it.qty--">-</button><span>{{ it.qty }}</span><button @click="it.qty<99&&it.qty++">+</button></div>
            <button class="rm" @click="del(it.k)">删除</button></div></div></div></div>
    <div class="df" v-if="S.cart.length"><label><input type="checkbox" v-model="allOn">全选</label><button class="rm" @click="delSel" style="color:var(--mu)">删除选中</button>
      <div class="tt"><div style="font-size:12px;color:var(--mu)">已选 {{ selCount }} 件</div>合计 <b>¥{{ total }}</b></div>
      <button class="go" :disabled="!selCount" @click="checkout">结算</button></div>
  </aside></div>`,
};
