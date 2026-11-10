Comps.ProductCard = {
  props: { p: Object },
  methods: { quick() { Cart.add(this.p.id, 1, OPT[this.p.c][0]); toast('已加入购物车'); }, fav() { Fav.toggle(this.p.id); } },
  template: `<article class="card" @click="go('#/item/'+p.id)"><thumb :p="p"></thumb>
    <button class="fav" :class="{on:S.favs.includes(p.id)}" @click.stop="fav">{{ S.favs.includes(p.id) ? '已收藏' : '收藏' }}</button>
    <div class="nm">{{ p.n }}</div><div class="tg"><em>包邮</em><em v-if="p.id%2">次日达</em></div>
    <div class="pr"><small>¥</small><b>{{ p.p }}</b><s>¥{{ p.o }}</s><span class="sd">已售 {{ sold(p.s) }}</span></div>
    <button class="add" @click.stop="quick">加入购物车</button></article>`,
};
