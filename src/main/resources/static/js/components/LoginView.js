/* 登录 / 注册页：#/login */
Comps.LoginView = {
  data: () => ({ mode: 'login', phone: '', password: '', nickname: '', busy: false, err: '' }),
  methods: {
    switchMode(m) {
      this.mode = m;
      this.err = '';
    },
    async submit() {
      if (this.busy) return;
      this.err = '';
      if (!/^1\d{10}$/.test(this.phone)) {
        this.err = '请输入正确的 11 位手机号';
        return;
      }
      if (this.password.length < 6) {
        this.err = '密码至少 6 位';
        return;
      }
      this.busy = true;
      try {
        if (this.mode === 'login') {
          await Auth.login(this.phone, this.password);
          toast('登录成功');
          go('#/');
        } else {
          const data = await Auth.register(this.phone, this.password, this.nickname);
          if (data && (data.token || data.userId)) {
            toast('注册成功，已自动登录');
            go('#/');
          } else {
            this.mode = 'login';
            this.err = '注册成功，请登录';
          }
        }
      } catch (e) {
        this.err = e.message || '操作失败，请稍后重试';
      } finally {
        this.busy = false;
      }
    },
  },
  template: `<div class="w pg"><div class="box login-box">
    <div class="tabs">
      <a :class="{on:mode==='login'}" @click="switchMode('login')">登录</a>
      <a :class="{on:mode==='register'}" @click="switchMode('register')">注册</a>
    </div>
    <div class="login-welcome" v-if="S.user">
      当前已登录：<b>{{ S.user.nickname || S.user.phone }}</b>
      <a @click="Auth.logout()">退出登录</a>
    </div>
    <form class="fm" @submit.prevent="submit">
      <input type="text" v-model.trim="phone" maxlength="11" placeholder="手机号" autocomplete="username">
      <input type="password" v-model="password" maxlength="32" placeholder="密码（至少 6 位）" autocomplete="current-password">
      <input v-if="mode==='register'" type="text" v-model.trim="nickname" maxlength="20" placeholder="昵称（可选）">
      <div class="login-err" v-if="err">{{ err }}</div>
      <button class="b2 login-btn" type="submit" :disabled="busy">{{ busy ? '请稍候…' : (mode === 'login' ? '登录' : '注册') }}</button>
      <small class="login-tip">
        {{ mode === 'login' ? '还没有账号？' : '已有账号？' }}
        <a @click="switchMode(mode === 'login' ? 'register' : 'login')">{{ mode === 'login' ? '去注册' : '去登录' }}</a>
      </small>
    </form>
  </div></div>`,
};
