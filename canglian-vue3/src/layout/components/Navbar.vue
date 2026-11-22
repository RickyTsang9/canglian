<template>
  <div class="navbar" :class="'nav' + settingsStore.navType">
    <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <breadcrumb v-if="settingsStore.navType == 1" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="settingsStore.navType == 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="settingsStore.navType == 3">
      <logo v-show="settingsStore.sidebarLogo" :collapse="false"></logo>
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <template v-if="appStore.device !== 'mobile'">
        <header-search id="header-search" class="right-menu-item hover-effect" />

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="切换明暗主题" effect="dark" placement="bottom">
          <div class="right-menu-item hover-effect theme-switch-wrapper" @click="toggleTheme">
            <svg-icon v-if="settingsStore.isDark" icon-class="sunny" />
            <svg-icon v-if="!settingsStore.isDark" icon-class="moon" />
          </div>
        </el-tooltip>

        <el-tooltip content="布局密度" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <el-dropdown @command="handleCommand" class="avatar-container right-menu-item" trigger="hover">
        <div class="avatar-wrapper">
          <div class="avatar-box">
            <img :src="userStore.avatar" class="user-avatar" alt="avatar" />
            <span class="status-dot" />
          </div>
          <span class="user-nickname">{{ userStore.nickName }}</span>
          <el-icon class="avatar-caret"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/user/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item command="setLayout" v-if="settingsStore.showSettings">
              <span>界面布局设置</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import HeaderSearch from '@/components/HeaderSearch'
import useAppStore from '@/store/modules/app'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'

const appStore = useAppStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

function toggleSideBar() {
  appStore.toggleSideBar()
}

function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout()
      break
    case "logout":
      logout()
      break
    default:
      break
  }
}

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logOut().then(() => {
      location.href = '/index'
    })
  }).catch(() => { })
}

const emits = defineEmits(['setLayout'])
function setLayout() {
  emits('setLayout')
}

async function toggleTheme(event) {
  const x = event?.clientX || window.innerWidth / 2
  const y = event?.clientY || window.innerHeight / 2
  const wasDark = settingsStore.isDark

  const isReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches
  const isSupported = document.startViewTransition && !isReducedMotion

  if (!isSupported) {
    settingsStore.toggleTheme()
    return
  }

  try {
    const transition = document.startViewTransition(async () => {
      await new Promise((resolve) => setTimeout(resolve, 10))
      settingsStore.toggleTheme()
      await nextTick()
    })
    await transition.ready

    const endRadius = Math.hypot(Math.max(x, window.innerWidth - x), Math.max(y, window.innerHeight - y))
    const clipPath = [`circle(0px at ${x}px ${y}px)`, `circle(${endRadius}px at ${x}px ${y}px)`]
    document.documentElement.animate(
      {
        clipPath: !wasDark ? [...clipPath].reverse() : clipPath
      }, {
        duration: 500,
        easing: "cubic-bezier(0.4, 0, 0.2, 1)",
        fill: "forwards",
        pseudoElement: !wasDark ? "::view-transition-old(root)" : "::view-transition-new(root)"
      }
    )
    await transition.finished
  } catch (error) {
    settingsStore.toggleTheme()
  }
}
</script>

<style lang='scss' scoped>
.navbar.nav3 {
  .hamburger-container {
    display: none !important;
  }
}

.navbar {
  height: 58px;
  overflow: hidden;
  position: relative;
  background: var(--navbar-bg);
  border-bottom: 1px solid var(--navbar-border, #e2e8f0);
  box-shadow: none;
  display: flex;
  align-items: center;
  padding: 0 16px 0 10px;
  backdrop-filter: blur(14px);
  box-sizing: border-box;

  .hamburger-container {
    width: 36px;
    height: 36px;
    line-height: 36px;
    border-radius: 9px;
    cursor: pointer;
    transition: background 0.18s ease, color 0.18s ease;
    -webkit-tap-highlight-color: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin-right: 10px;
    color: var(--navbar-text, #334155);

    &:hover {
      background: var(--primary-soft-bg, #eff6ff);
      color: var(--brand-primary, #2563eb);
    }
  }

  .breadcrumb-container {
    flex-shrink: 0;
  }

  .topmenu-container {
    position: absolute;
    left: 52px;
  }

  .topbar-container {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    overflow: hidden;
    margin-left: 8px;
  }

  .right-menu {
    height: 100%;
    display: flex;
    align-items: center;
    gap: 6px;
    margin-left: auto;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 9px;
      height: 36px;
      border-radius: 9px;
      font-size: 16px;
      color: var(--navbar-text, #475569);
      transition: background 0.18s ease, color 0.18s ease;

      &.hover-effect {
        cursor: pointer;

        &:hover {
          background: var(--primary-soft-bg, #eff6ff);
          color: var(--brand-primary, #2563eb);
        }
      }

      &.theme-switch-wrapper {
        svg {
          transition: transform 0.25s ease;

          &:hover {
            transform: rotate(18deg) scale(1.1);
          }
        }
      }
    }

    .avatar-container {
      margin-left: 6px;
      padding: 0;
      height: auto;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        gap: 8px;
        height: 38px;
        padding: 0 12px 0 6px;
        border: 1px solid var(--navbar-border, #e2e8f0);
        border-radius: 999px;
        background: var(--navbar-avatar-bg, #f8fafc);
        cursor: pointer;
        transition: border-color 0.18s ease, background 0.18s ease, box-shadow 0.18s ease;

        &:hover {
          border-color: #bfdbfe;
          background: var(--panel-bg, #ffffff);
          box-shadow: 0 2px 10px rgba(37, 99, 235, 0.08);
        }

        .avatar-box {
          position: relative;
          display: flex;
          align-items: center;
        }

        .user-avatar {
          width: 28px;
          height: 28px;
          border-radius: 50%;
          object-fit: cover;
        }

        .status-dot {
          position: absolute;
          right: -1px;
          bottom: -1px;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: #10b981;
          border: 1.5px solid var(--panel-bg, #ffffff);
        }

        .user-nickname {
          font-size: 13.5px;
          font-weight: 600;
          line-height: 1;
          color: var(--panel-heading, #0f172a);
        }

        .avatar-caret {
          font-size: 12px;
          color: var(--panel-muted, #64748b);
        }
      }
    }
  }
}
</style>
