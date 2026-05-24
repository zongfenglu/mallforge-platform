<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">M</div>
        <h1>MallForge</h1>
        <p>多租户 SaaS 商城管理平台</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { useAuthStore } from '~/stores/auth'

definePageMeta({ layout: false })

const { $http } = useNuxtApp()
const auth   = useAuthStore()
const router = useRouter()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码',   trigger: 'blur' }],
}

const handleLogin = async () => {
  await formRef.value?.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await ($http as any).post('/api/v1/auth/login', form)
      auth.setAuth(res.data)
      ElMessage.success('登录成功')
      await router.push('/dashboard')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1E1B4B 0%, #312E81 50%, #6C3AFA 100%);
  display: grid;
  place-items: center;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  width: 380px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.login-header { text-align: center; margin-bottom: 32px; }
.logo {
  width: 56px; height: 56px;
  background: #6C3AFA;
  border-radius: 14px;
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  display: grid;
  place-items: center;
  margin: 0 auto 16px;
}
.login-header h1 { font-size: 24px; font-weight: 700; margin: 0 0 4px; }
.login-header p  { font-size: 14px; color: #6B7280; margin: 0; }

.login-btn { width: 100%; }
</style>
