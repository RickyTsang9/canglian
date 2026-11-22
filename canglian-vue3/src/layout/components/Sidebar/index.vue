<template>
  <div :class="[{ 'has-logo': showLogo }, effectiveSideTheme]" class="sidebar-container">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="transparent"
        :text-color="getMenuTextColor"
        :unique-opened="true"
        :active-text-color="getActiveTextColor"
        :collapse-transition="false"
        mode="vertical"
        :class="effectiveSideTheme"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/assets/styles/variables.module.scss'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

const sidebarRouters = computed(() => permissionStore.sidebarRouters)
const showLogo = computed(() => settingsStore.sidebarLogo)
const effectiveSideTheme = computed(() => settingsStore.isDark ? 'theme-dark' : settingsStore.sideTheme)
const theme = computed(() => settingsStore.theme)
const isCollapse = computed(() => !appStore.sidebar.opened)

const getMenuTextColor = computed(() => {
  return effectiveSideTheme.value === 'theme-dark' ? variables.menuText : variables.menuLightText
})

const getActiveTextColor = computed(() => {
  return effectiveSideTheme.value === 'theme-dark' ? '#ffffff' : theme.value
})

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  .el-menu {
    border: none;
    height: 100%;
    width: 100% !important;
  }
}
</style>
