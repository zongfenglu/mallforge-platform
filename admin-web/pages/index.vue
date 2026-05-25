<template>
  <div></div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

const auth = useAuthStore()

// 根路由：已登录跳转 dashboard，未登录跳转 login
if (auth.isLoggedIn) {
  await navigateTo('/dashboard')
} else {
  await navigateTo('/login')
}
</script>
