/* 地址表单：地址页与结算弹窗共用 */
Comps.AddrForm = {
  props: { value: Object }, emits: ['save', 'cancel'],
  data() { return { f: { name: '', phone: '', region: '', detail: '', def: false, ...this.value } }; },
  methods: {
    submit() {
      const f = this.f;
      if (!f.name.trim()) return toast('请填写收货人姓名');
      if (!/^1\d{10}$/.test(f.phone)) return toast('请输入 11 位手机号');
      if (!f.region.trim() || !f.detail.trim()) return toast('请填写完整的所在地区和详细地址');
      this.$emit('save', { ...f, name: f.name.trim(), region: f.region.trim(), detail: f.detail.trim() });
    },
  },
  template: `<div class="fm"><input type="text" v-model="f.name" placeholder="收货人姓名"><input type="text" v-model="f.phone" maxlength="11" placeholder="手机号码">
    <input type="text" v-model="f.region" placeholder="所在地区，如 浙江省 杭州市 西湖区"><input type="text" v-model="f.detail" placeholder="详细地址，如 文三路 100 号 5 栋 301">
    <label class="ck"><input type="checkbox" v-model="f.def">设为默认地址</label>
    <div class="btns"><button class="b2" @click="submit">保存地址</button><button class="b1" @click="$emit('cancel')">取消</button></div></div>`,
};
