<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse, 'is-dark': isDarkSidebar }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo" alt="仓链" />
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <div class="brand-lockup">
          <img :src="logo" class="sidebar-logo" alt="仓链" />
          <div class="brand-text">
            <h1 class="sidebar-title">{{ title }}</h1>
            <span class="sidebar-badge">智能供应链云</span>
          </div>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import logo from '@/assets/logo/logo.svg'
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = import.meta.env.VITE_APP_TITLE
const settingsStore = useSettingsStore()

const isDarkSidebar = computed(() => {
  if (settingsStore.isDark) return true
  if (settingsStore.navType == 3) return false
  return settingsStore.sideTheme === 'theme-dark'
})
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 0.25s ease;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 58px;
  background: transparent;
  overflow: hidden;
  border-bottom: 1px solid #eceef2;
  display: flex;
  align-items: center;

  &.is-dark {
    border-bottom-color: rgba(148, 163, 184, 0.14);

    .sidebar-title {
      color: #f8fafc;
    }

    .sidebar-badge {
      color: #94a3b8;
    }
  }

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex !important;
    align-items: center;
    padding: 0 16px;
    text-decoration: none;
  }

  .brand-lockup {
    display: flex;
    align-items: center;
    gap: 11px;
    min-width: 0;
  }

  .sidebar-logo {
    width: 34px;
    height: 34px;
    border-radius: 9px;
    flex-shrink: 0;
    display: block;
    box-shadow: 0 2px 8px rgba(37, 99, 235, 0.18);
  }

  .brand-text {
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;
    gap: 2px;
  }

  .sidebar-title {
    margin: 0;
    color: #111827;
    font-weight: 700;
    font-size: 15px;
    line-height: 1.2;
    letter-spacing: 0.01em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .sidebar-badge {
    display: block;
    font-size: 11px;
    line-height: 1.2;
    font-weight: 500;
    color: #9ca3af;
    letter-spacing: 0.03em;
  }

  &.collapse {
    & .sidebar-logo-link {
      justify-content: center;
      padding: 0;
    }
  }
}
</style>
