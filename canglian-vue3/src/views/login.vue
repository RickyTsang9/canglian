<template>
  <div class="login">
    <div class="login-shell">
      <section class="login-brand">
        <div class="brand-lockup">
          <img :src="logoImg" class="brand-logo" alt="仓链" />
          <span class="brand-pill">智能供应链云 · Enterprise ERP</span>
        </div>
        <h1>{{ title }}</h1>
        <p class="brand-desc">
          围绕采购、销售、多仓批次效期、SN 序列号溯源、往来核销与总账凭证打造纯净高效的一体化经营工作台。
        </p>
        <div class="brand-points">
          <div class="brand-point">
            <div class="point-icon teal"><el-icon :size="17"><Box /></el-icon></div>
            <span>仓储效期管控</span>
            <strong>批次 FEFO · SN 溯源</strong>
          </div>
          <div class="brand-point">
            <div class="point-icon blue"><el-icon :size="17"><Connection /></el-icon></div>
            <span>业财全链路闭环</span>
            <strong>签收确收 · 真实成本</strong>
          </div>
          <div class="brand-point">
            <div class="point-icon amber"><el-icon :size="17"><DataAnalysis /></el-icon></div>
            <span>实时决策分析</span>
            <strong>利润账龄 · 复式凭证</strong>
          </div>
        </div>
      </section>

      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-heading">
          <h3>欢迎登录</h3>
          <span>请输入账号与密码进入仓链系统</span>
        </div>
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="请输入账号"
          >
            <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            size="large"
            show-password
            auto-complete="off"
            placeholder="请输入密码"
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
              <img :src="codeUrl" class="login-code-img" alt="验证码" />
            </button>
          </div>
        </el-form-item>
        <div class="form-options">
          <el-checkbox v-model="loginForm.rememberMe">记住账号密码</el-checkbox>
          <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
        </div>
        <el-form-item style="margin-bottom: 10px">
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            class="login-button"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading">登录系统</span>
            <span v-else>正在安全验证...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import logoImg from "@/assets/logo/logo.svg"
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
const captchaEnabled = ref(true)
const register = ref(false)
const redirect = ref(undefined)

watch(route, (newRoute) => {
  redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 })
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 })
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 })
      } else {
        Cookies.remove("username")
        Cookies.remove("password")
        Cookies.remove("rememberMe")
      }
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, k) => {
          if (k !== "redirect") acc[k] = query[k]
          return acc
        }, {})
        router.push({ path: redirect.value || "/", query: otherQueryParams })
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) getCode()
      })
    }
  })
}

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

<style scoped lang="scss">
.login {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  overflow: hidden;
  padding: 48px 24px;
  background:
    radial-gradient(640px circle at 14% 12%, rgba(37, 99, 235, 0.06), transparent 60%),
    radial-gradient(560px circle at 88% 86%, rgba(13, 148, 136, 0.06), transparent 60%),
    #f8f9fb;
}

.login::before {
  position: absolute;
  inset: 0;
  content: "";
  background-image:
    linear-gradient(rgba(15, 23, 42, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(15, 23, 42, 0.03) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: radial-gradient(ellipse at 50% 45%, rgba(0, 0, 0, 0.75), transparent 80%);
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(380px, 1fr) 420px;
  gap: 64px;
  width: min(1060px, 100%);
  align-items: center;
}

.brand-lockup {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 22px;
}

.brand-logo {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.brand-pill {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  color: #4b5563;
  background: #ffffff;
  border: 1px solid #e5e7eb;
}

.login-brand h1 {
  margin: 0;
  color: #111827;
  font-size: 38px;
  line-height: 1.2;
  font-weight: 750;
  letter-spacing: -0.02em;
}

.brand-desc {
  max-width: 520px;
  margin: 16px 0 30px;
  color: #6b7280;
  font-size: 15px;
  line-height: 1.78;
}

.brand-points {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  max-width: 540px;
}

.brand-point {
  padding: 15px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.03);

  .point-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px;

    &.teal { background: #f0fdfa; color: #0d9488; }
    &.blue { background: #eff6ff; color: #2563eb; }
    &.amber { background: #fffbeb; color: #d97706; }
  }

  span {
    display: block;
    color: #6b7280;
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 4px;
    color: #111827;
    font-size: 13.5px;
    font-weight: 600;
  }
}

.login-form {
  width: 100%;
  padding: 34px 32px 22px;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 10px 32px rgba(15, 23, 42, 0.06);
}

.form-heading {
  margin-bottom: 24px;

  h3 {
    margin: 0;
    color: #111827;
    font-size: 22px;
    font-weight: 700;
  }

  span {
    display: block;
    margin-top: 6px;
    color: #6b7280;
    font-size: 13.5px;
  }
}

.login-form :deep(.el-input__wrapper) {
  min-height: 44px;
  border-radius: 9px;
}

.login-form .input-icon {
  width: 15px;
  height: 42px;
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
  border: 1px solid #d1d5db;
  border-radius: 9px;
  background: #f9fafb;
  cursor: pointer;
}

.login-code-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -2px 0 20px;
}

.login-button {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 9px;
  background: #2563eb;
  font-size: 14.5px;
  font-weight: 600;
  letter-spacing: 0.02em;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.22);

  &:hover {
    background: #1d4ed8;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #9ca3af;
  font-size: 12px;
}

@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .login-brand h1 {
    font-size: 30px;
  }
}

@media (max-width: 560px) {
  .login-brand {
    display: none;
  }

  .login-form {
    padding: 28px 22px 16px;
  }
}
</style>
