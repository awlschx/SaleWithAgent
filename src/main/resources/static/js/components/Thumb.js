/* 商品占位图：v 为 0-3，对应主图与三张细节图 */
Comps.Thumb = {
  props: { p: Object, v: { type: Number, default: 0 } },
  template: `<div class="th" :style="{background:'hsl('+(p.h+v*14)+' 30% '+(92-v*3)+'%)'}"><span :style="{background:'hsl('+(p.h+v*14)+' 38% 62%)',borderRadius:['22%','50%','8%','40% 10%'][v]}">{{ v ? '正侧细'[v-1] : p.c[0] }}</span></div>`,
};
