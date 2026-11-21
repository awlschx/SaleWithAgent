Comps.CartDrawer = {
  computed: {
    allOn: { get() { return Store.cart.length > 0 && Store.cart.every(i => i.on); }, set(v) { Cart.selectAll(v); } },
    selCount() { return Cart.sel().reduce((a, i) => a + i.qty, 0); },
    total() { return Store.cartSelectedAmount; },
  },
  methods: {
    del(item) { return Cart.remove(item); },
    delSel() { return Cart.deleteSelected(); },
    change(item, patch) { return Cart.update(item, patch); },
    checkout() {
      if (!Auth.require()) return;
      const items = Cart.sel().map(({ id, spec, qty, n }) => ({ id, spec, qty, n }));
      if (!items.length) return;
      Store.cartOpen = false;
      Store.co = { items, fromCart: true, key: idempotencyKey(), submitting: false };
    },
  },
  template: `<div><div class="ov" v-if="S.cartOpen" @click="S.cartOpen=false"></div>
  <aside class="dw" :class="{on:S.cartOpen}">
    <div class="dh"><span>购物车（{{ Cart.count() }}）</span><button @click="S.cartOpen=false" style="font-size:22px">×</button></div>
    <div class="dl"><div class="empty" v-if="!S.cart.length">购物车还是空的<br><a @click="S.cartOpen=false;go('#/')">去逛逛</a></div>
      <div class="ci" v-for="it in S.cart" :key="it.rid"><input type="checkbox" :checked="it.on" :disabled="S.pending.cart" @change="change(it,{selected:$event.target.checked})"><thumb :p="byId(it.id)||{h:0,c:'商品'}"></thumb>
        <div><div class="nm" @click="S.cartOpen=false;go('#/item/'+it.id)">{{ it.n || byId(it.id)?.n || '商品 '+it.id }}</div><div class="sp">规格：{{ it.spec }}</div>
          <div class="ln"><b style="color:var(--pr)">¥{{ it.price }}</b>
            <div class="stp"><button :disabled="S.pending.cart||it.qty<=1" @click="change(it,{quantity:it.qty-1})">-</button><span>{{ it.qty }}</span><button :disabled="S.pending.cart||(it.stock!=null&&it.qty>=it.stock)" @click="change(it,{quantity:it.qty+1})">+</button></div>
            <button class="rm" :disabled="S.pending.cart" @click="del(it)">删除</button></div></div></div></div>
    <div class="df" v-if="S.cart.length"><label><input type="checkbox" v-model="allOn" :disabled="S.pending.cart">全选</label><button class="rm" :disabled="S.pending.cart" @click="delSel" style="color:var(--mu)">删除选中</button>
      <div class="tt"><div style="font-size:12px;color:var(--mu)">已选 {{ selCount }} 件</div>合计 <b>¥{{ total }}</b></div>
      <button class="go" :disabled="!selCount||S.pending.cart" @click="checkout">结算</button></div>
  </aside></div>`,
};
