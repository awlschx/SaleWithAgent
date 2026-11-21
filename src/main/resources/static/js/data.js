/* 纯展示与导航配置。商品、规格、优惠券、评价等动态业务数据只能来自后端。 */
const CATS = [
  { n: '全部', s: '所有商品' }, { n: '服饰', s: '服装与配饰' },
  { n: '数码', s: '数码与配件' }, { n: '家居', s: '家居用品' },
  { n: '美妆', s: '个护与美妆' }, { n: '食品', s: '食品与饮品' },
  { n: '运动', s: '运动与健身' },
];
const SLIDES = [
  { t: '服饰精选', d: '浏览服饰分类商品', b: '去选购', bg: '#ff5000', cat: '服饰' },
  { t: '数码精选', d: '浏览数码分类商品', b: '看数码', bg: '#35506b', cat: '数码' },
  { t: '食品精选', d: '浏览食品分类商品', b: '去选购', bg: '#5b6b34', cat: '食品' },
];

/* 保留原引用，供现有组件按 ID 查找当前已加载的商品。 */
const PRODUCTS = [];
const byId = id => PRODUCTS.find(p => String(p.id) === String(id));

function applyProducts(list) {
  PRODUCTS.splice(0, PRODUCTS.length, ...(Array.isArray(list) ? list : []));
}
