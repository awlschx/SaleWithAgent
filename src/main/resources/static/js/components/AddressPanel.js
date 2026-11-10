Comps.AddressPanel = {
  data: () => ({ editing: null }),
  methods: { save(a) { Addr.save(a); this.editing = null; toast('地址已保存'); }, del: id => Addr.del(id), setDef(a) { Addr.save({ ...a, def: true }); } },
  template: `<div><div class="empty" v-if="!S.addrs.length&&!editing">还没有收货地址，添加一个吧</div>
    <div class="ads"><div class="ad" :class="{def:a.def}" v-for="a in S.addrs" :key="a.id"><div><b>{{ a.name }}</b> {{ a.phone }}<em v-if="a.def">默认</em></div><div>{{ a.region }} {{ a.detail }}</div>
      <div class="ab"><a @click="editing=a">编辑</a><a @click="del(a.id)">删除</a><a v-if="!a.def" @click="setDef(a)">设为默认</a></div></div></div>
    <addr-form v-if="editing" :key="editing.id||'new'" :value="editing" @save="save" @cancel="editing=null"></addr-form>
    <button class="b2" style="padding:8px 24px;border-radius:4px" v-else @click="editing={}">新增收货地址</button></div>`,
};
