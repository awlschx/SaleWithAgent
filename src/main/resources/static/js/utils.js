/* 通用工具 */
const Comps = {}; // 组件注册表，由 app.js 统一注册
const pad = n => String(n).padStart(2, '0');
const sold = s => (s >= 1e4 ? (s / 1e4).toFixed(1) + ' 万' : s);
const stars = n => '★'.repeat(n) + '☆'.repeat(5 - n);
const fmtTime = t => { const d = new Date(t); return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`; };
const day = t => fmtTime(t).slice(0, 10);
