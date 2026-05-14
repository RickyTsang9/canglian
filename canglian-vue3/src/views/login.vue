<template>
  <div class="login">
    <div class="login-shell">
      <section class="login-brand">
        <div class="brand-mark">仓</div>
        <p class="brand-kicker">仓储 · 财务 · 经营分析</p>
        <h1>{{ title }}</h1>
        <p class="brand-desc">围绕库存、往来、单据和报表形成一体化工作台，帮助业务人员快速完成日常处理和风险查看。</p>
        <div class="brand-points">
          <div class="brand-point">
            <span>库存预警</span>
            <strong>实时识别</strong>
          </div>
          <div class="brand-point">
            <span>业务链路</span>
            <strong>单据追踪</strong>
          </div>
          <div class="brand-point">
            <span>财务报表</span>
            <strong>集中查看</strong>
          </div>
        </div>
      </section>

      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-heading">
          <h3>账号登录</h3>
          <span>请输入账号信息进入系统</span>
        </div>
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="账号"
          >
            <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            size="large"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter="handleLogin"
          >
            <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input
              v-model="loginForm.code"
              size="large"
              auto-complete="off"
              placeholder="验证码"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
            </el-input>
            <button class="login-code" type="button" @click="getCode">
              <img :src="codeUrl" class="login-code-img" alt="验证码"/>
            </button>
          </div>
        </el-form-item>
        <div class="form-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
        </div>
        <el-form-item>
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            class="login-button"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from "@/utils/jsencrypt"
import useUserStore from '@/store/modules/user'
import defaultSettings from '@/settings'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loginForm = ref({
  username: "admin",
  password: "admin123",
  rememberMe: false,
  code: "",
  uuid: ""
})

const loginRules = computed(() => ({
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: captchaEnabled.value ? [{ required: true, trigger: "change", message: "请输入验证码" }] : []
}))

const codeUrl = ref("")
const loading = ref(false)
// 验证码开关
const captchaEnabled = ref(true)
// 注册开关
const register = ref(false)
const redirect = ref(undefined)

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

// 登录系统
function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true
      // 勾选了需要记住密码设置在 cookie 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 })
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 })
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 })
      } else {
        // 否则移除
        Cookies.remove("username")
        Cookies.remove("password")
        Cookies.remove("rememberMe")
      }
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((accumulatedQuery, currentQueryKey) => {
          if (currentQueryKey !== "redirect") {
            accumulatedQuery[currentQueryKey] = query[currentQueryKey]
          }
          return accumulatedQuery
        }, {})
        router.push({ path: redirect.value || "/", query: otherQueryParams })
      }).catch(() => {
        loading.value = false
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

// 获取验证码
function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      loginForm.value.uuid = res.uuid
    } else {
      codeUrl.value = ""
      loginForm.value.code = ""
      loginForm.value.uuid = ""
    }
  })
}

// 读取记住密码信息
function getCookie() {
  const username = Cookies.get("username")
  const password = Cookies.get("password")
  const rememberMe = Cookies.get("rememberMe")
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
    code: loginForm.value.code,
    uuid: loginForm.value.uuid
  }
}

getCode()
getCookie()
</script>

<style scoped>
.login {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  overflow: hidden;
  padding: 48px 24px;
  background:
    linear-gradient(135deg, rgba(12, 31, 55, 0.94), rgba(18, 71, 90, 0.86)),
    url("../assets/images/login-background.jpg") center center no-repeat;
  background-size: cover;
}

.login::before {
  position: absolute;
  inset: 0;
  content: "";
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.06) 1px, transparent 1px);
  background-size: 42px 42px;
  mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0.75), transparent 75%);
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(360px, 1fr) 430px;
  gap: 56px;
  width: min(1040px, 100%);
  align-items: center;
}

.login-brand {
  color: #ffffff;
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  margin-bottom: 28px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 24px;
  font-weight: 700;
}

.brand-kicker {
  margin: 0 0 14px;
  color: rgba(218, 241, 255, 0.78);
  font-size: 14px;
}

.login-brand h1 {
  margin: 0;
  color: #ffffff;
  font-size: 42px;
  line-height: 1.18;
  font-weight: 700;
  letter-spacing: 0;
}

.brand-desc {
  max-width: 560px;
  margin: 18px 0 32px;
  color: rgba(255, 255, 255, 0.78);
  font-size: 16px;
  line-height: 1.8;
}

.brand-points {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  max-width: 560px;
}

.brand-point {
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
}

.brand-point span {
  display: block;
  color: rgba(255, 255, 255, 0.64);
  font-size: 13px;
}

.brand-point strong {
  display: block;
  margin-top: 8px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 650;
}

.form-heading {
  margin-bottom: 26px;
  text-align: left;
}

.form-heading h3 {
  margin: 0;
  color: var(--login-heading, #172033);
  font-size: 24px;
  font-weight: 650;
}

.form-heading span {
  display: block;
  margin-top: 8px;
  color: var(--login-muted, #7b8494);
  font-size: 14px;
}

.login-form {
  width: 100%;
  padding: 34px 32px 18px;
  border: 1px solid var(--login-card-border, rgba(255, 255, 255, 0.72));
  border-radius: 12px;
  background: var(--login-card-bg, rgba(255, 255, 255, 0.94));
  box-shadow: 0 24px 60px rgba(6, 22, 39, 0.24);
  backdrop-filter: blur(14px);
}

.login-form :deep(.el-input) {
  height: 44px;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 44px;
}

.login-form :deep(.el-input__inner) {
  height: 44px;
}

.login-form .input-icon {
  width: 14px;
  height: 43px;
  margin-left: 0;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 118px;
  gap: 12px;
  width: 100%;
}

.login-code {
  height: 44px;
  padding: 0;
  overflow: hidden;
  border: 1px solid var(--login-code-border, #dcdfe6);
  border-radius: 4px;
  background: var(--login-code-bg, #f7fafc);
  cursor: pointer;
}

.login-code img {
  vertical-align: middle;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -2px 0 24px;
}

.login-button {
  width: 100%;
  height: 44px;
  border: none;
  background: linear-gradient(135deg, #1677ff, #0f8f8f);
  font-weight: 650;
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.78);
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 0;
}
.login-code-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 900px) {
  .login {
    align-items: flex-start;
    padding: 32px 18px 72px;
  }

  .login-shell {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .login-brand h1 {
    font-size: 32px;
  }

  .brand-desc {
    margin-bottom: 22px;
  }
}

@media (max-width: 560px) {
  .login-brand {
    display: none;
  }

  .login-shell {
    width: 100%;
  }

  .login-form {
    padding: 28px 22px 12px;
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }

  .login-code {
    width: 100%;
  }
}
</style>
