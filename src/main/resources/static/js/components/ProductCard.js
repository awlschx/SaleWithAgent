Comps.ProductCard = {
  props: { p: Object },
  methods: {
    quick() { return Cart.add(this.p.id, 1, this.p.specs[0]); },
    fav() { return Fav.toggle(this.p.id); },
  },
  template: `<article class="card" @click="go('#/item/'+p.id)"><thumb :p="p"></thumb>
    <button class="fav" :class="{on:S.favs.some(id=>String(id)===String(p.id))}" @click.stop="fav">{{ S.favs.some(id=>String(id)===String(p.id)) ? '已收藏' : '收藏' }}</button>
    <div class="nm">{{ p.n }}</div><div class="tg" v-if="p.freeShipping||p.nextDay"><em v-if="p.freeShipping">包邮</em><em v-if="p.nextDay">次日达</em></div>
    <div class="pr"><small>¥</small><b>{{ p.p }}</b><s v-if="p.o!=null">¥{{ p.o }}</s><span class="sd">已售 {{ sold(p.s) }}</span></div>
    <button class="add" :disabled="p.stock===0||S.pending.cart" @click.stop="quick">加入购物车</button></article>`,
};
