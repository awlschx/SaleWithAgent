Comps.CouponPanel = {
  data: () => ({ f: 'ok', pool: COUPON_POOL }),
  computed: {
    ok() { return Store.coupons.filter(c => !c.used && c.exp > Date.now()); },
    dead() { return Store.coupons.filter(c => c.used || c.exp <= Date.now()); },
  },
  template: `<div><div class="chips"><a v-for="t in [['ok','可使用'],['dead','已使用 / 已过期'],['pool','领券中心']]" :key="t[0]" :class="{on:f===t[0]}" @click="f=t[0]">{{ t[1] }}</a></div>
    <div class="cpl two" v-if="f==='ok'"><coupon-card v-for="c in ok" :key="c.uid" :c="c"></coupon-card></div>
    <div class="empty" v-if="f==='ok'&&!ok.length">暂无可用优惠券，<a @click="f='pool'">去领券中心看看</a></div>
    <div class="cpl two" v-if="f==='dead'"><coupon-card v-for="c in dead" :key="c.uid" :c="c" dead></coupon-card></div>
    <div class="empty" v-if="f==='dead'&&!dead.length">没有已使用或已过期的优惠券</div>
    <div class="cpl two" v-if="f==='pool'"><coupon-card v-for="c in pool" :key="c.id" :c="c"><button :disabled="Coupons.owned(c)" @click="Coupons.claim(c)">{{ Coupons.owned(c) ? '已领取' : '立即领取' }}</button></coupon-card></div></div>`,
};
