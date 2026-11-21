/* 商品图片；未提供图片时使用不含业务含义的占位图。 */
Comps.Thumb = {
  props: { p: Object, v: { type: Number, default: 0 } },
  computed: {
    hue() { return Number(this.p?.h) || 0; },
    label() { return this.p?.c?.[0] || '商'; },
  },
  template: `<div class="th" :style="{background:'hsl('+(hue+v*14)+' 30% '+(92-v*3)+'%)'}"><img v-if="p&&p.img" :src="p.img" :alt="p.n||'商品图片'" style="width:100%;height:100%;object-fit:cover"><span v-else :style="{background:'hsl('+(hue+v*14)+' 38% 62%)',borderRadius:['22%','50%','8%','40% 10%'][v]}">{{ v ? '正侧细'[v-1] : label }}</span></div>`,
};
