<template>
  <div class="admin-layout">
    <!-- ── 侧边栏 ───────────────────────────────────────── -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <!-- Logo -->
      <div class="sidebar-logo">
        <div class="logo-icon">M</div>
        <span v-if="!sidebarCollapsed" class="logo-text">MallForge</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="sidebar-nav">
        <NuxtLink
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="active"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span v-if="!sidebarCollapsed">{{ item.label }}</span>
        </NuxtLink>
      </nav>
    </aside>

    <!-- ── 主体 ──────────────────────────────────────────── -->
    <div class="main-wrapper">
      <!-- 顶栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <el-button
            text
            @click="sidebarCollapsed = !sidebarCollapsed"
            :icon="sidebarCollapsed ? 'Expand' : 'Fold'"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRouteTitle">{{ currentRouteTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" :src="auth.userInfo?.avatar">
                {{ auth.userInfo?.realName?.[0] }}
              </el-avatar>
              <span class="username">{{ auth.userInfo?.realName || auth.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

const auth   = useAuthStore()
const router = useRouter()
const route  = useRoute()
const sidebarCollapsed = ref(false)

const navItems = [
  { path: '/dashboard',         label: '数据概览',   icon: 'DataBoard' },
  { path: '/product',           label: '商品管理',   icon: 'Goods' },
  { path: '/order',             label: '订单管理',   icon: 'List' },
  { path: '/marketing',         label: '营销中心',   icon: 'Present' },
  { path: '/decorator',         label: '店铺装修',   icon: 'MagicStick' },
  { path: '/logistics/waybill', label: '面单打印',   icon: 'Printer' },
  { path: '/settings/payment',  label: '支付设置',   icon: 'CreditCard' },
  { path: '/settings/logistics',label: '物流设置',   icon: 'Van' },
  { path: '/settings/system',   label: '系统设置',   icon: 'Setting' },
]

const currentRouteTitle = computed(
  () => navItems.find(n => route.path.startsWith(n.path))?.label
)

const handleLogout = async () => {
  auth.logout()
  await router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ── 侧边栏 ─────────────────────────────────────────── */
.sidebar {
  width: var(--mf-sidebar-width);
  background: var(--mf-sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width 0.25s;
  flex-shrink: 0;
  overflow: hidden;
}
.sidebar.collapsed { width: 64px; }

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo-icon {
  width: 32px; height: 32px;
  border-radius: 8px;
  background: var(--mf-primary);
  color: #fff;
  display: grid; place-items: center;
  font-weight: 700; font-size: 16px;
  flex-shrink: 0;
}
.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
}

.sidebar-nav { padding: 12px 8px; flex: 1; }

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  color: var(--mf-sidebar-text);
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 4px;
  transition: all 0.15s;
  white-space: nowrap;
  overflow: hidden;
}
.nav-item:hover { background: var(--mf-sidebar-active); color: #fff; }
.nav-item.active { background: var(--mf-sidebar-active); color: #fff; }
.nav-item .el-icon { font-size: 18px; flex-shrink: 0; }

/* ── 主体 ────────────────────────────────────────────── */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.topbar {
  height: var(--mf-topbar-h);
  background: var(--mf-topbar-bg);
  backdrop-filter: var(--mf-topbar-blur);
  border-bottom: 1px solid rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
  position: sticky; top: 0; z-index: 100;
}
.topbar-left, .topbar-right { display: flex; align-items: center; gap: 16px; }

.user-info {
  display: flex; align-items: center; gap: 8px; cursor: pointer;
  color: var(--mf-text-primary); font-size: 14px;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
