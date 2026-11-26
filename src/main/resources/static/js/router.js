/* Hash 路由只负责页面导航；前端守卫不是安全边界，接口仍必须由后端鉴权。 */
const parseRoute = () => {
  const [a, rawId] = location.hash.slice(2).split('/');
  if (a === 'me' && !Store.user && (Store.authReady || !Store.token)) {
    if (location.hash !== '#/login') location.hash = '#/login';
    return;
  }
  let id = rawId;
  try { id = decodeURIComponent(rawId); } catch (e) {}
  Store.route = a === 'login' ? { name: 'login' }
    : a === 'support' ? { name: 'support' }
    : a === 'messages' ? { name: 'messages' }
    : a === 'favorites' ? { name: 'favorites', tab: ['goods', 'shops'].includes(rawId) ? rawId : 'goods' }
    : a === 'item' && id ? { name: 'detail', id }
    : a === 'me' ? { name: 'me', tab: ['orders', 'address', 'coupons'].includes(rawId) ? rawId : 'orders' }
    : { name: 'home' };
  scrollTo(0, 0);
};
addEventListener('hashchange', parseRoute);
parseRoute();
