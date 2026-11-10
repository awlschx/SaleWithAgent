/* 优惠券卡片，右侧操作区通过默认插槽传入 */
Comps.CouponCard = {
  props: { c: Object, dead: Boolean },
  template: `<div class="cp" :class="{dead}"><div class="cl"><b>¥{{ c.off }}</b><small>满{{ c.min }}可用</small></div>
    <div class="cr"><div>{{ c.t }}</div><small>{{ c.cat ? c.cat + '类商品' : '全场通用' }}<template v-if="c.exp"> · 至 {{ day(c.exp) }}</template></small><slot></slot></div></div>`,
};
