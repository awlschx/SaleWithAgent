/* 极简 hash 路由：#/  #/item/3  #/me/orders|address|coupons */
const parseRoute = () => {
  const [a, b] = location.hash.slice(2).split('/');
  Store.route = a === 'item' && byId(+b) ? { name: 'detail', id: +b }
    : a === 'me' ? { name: 'me', tab: ['orders', 'address', 'coupons'].includes(b) ? b : 'orders' } : { name: 'home' };
  scrollTo(0, 0);
};
addEventListener('hashchange', parseRoute);
parseRoute();
