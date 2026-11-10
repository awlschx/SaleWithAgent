Comps.Me = {
  data: () => ({ tabs: [['orders', '我的订单'], ['address', '收货地址'], ['coupons', '优惠券']], map: { orders: 'OrderPanel', address: 'AddressPanel', coupons: 'CouponPanel' } }),
  template: `<div class="w pg"><div class="box"><div class="tabs"><a v-for="t in tabs" :key="t[0]" :class="{on:S.route.tab===t[0]}" @click="go('#/me/'+t[0])">{{ t[1] }}</a></div>
    <component :is="map[S.route.tab]"></component></div></div>`,
};
