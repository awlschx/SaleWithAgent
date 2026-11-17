/* 极简 hash 路由：#/  #/item/3  #/me/orders|address|coupons  #/login */
const parseRoute = () => {
  const [a, b] = location.hash.slice(2).split('/');
  // 未登录访问「我的」相关页时，跳转到登录页
  if (a === 'me' && !Store.user) {
    if (location.hash !== '#/login') location.hash = '#/login';
    return;
  }
  Store.route = a === 'login' ? { name: 'login' }
    : a === 'item' && byId(+b) ? { name: 'detail', id: +b }
    : a === 'me' ? { name: 'me', tab: ['orders', 'address', 'coupons'].includes(b) ? b : 'orders' } : { name: 'home' };
  scrollTo(0, 0);
};
addEventListener('hashchange', parseRoute);
parseRoute();
