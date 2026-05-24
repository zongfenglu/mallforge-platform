// plugins/axios.ts
import axios from 'axios'
import { useAuthStore } from '~/stores/auth'

export default defineNuxtPlugin((nuxtApp) => {
  const config  = useRuntimeConfig()
  const baseURL = config.public.apiBase

  const http = axios.create({ baseURL, timeout: 15000 })

  // ── Request: 注入 JWT Token ────────────────────────────────
  http.interceptors.request.use((req) => {
    const auth = useAuthStore()
    if (auth.token) {
      req.headers.Authorization = `Bearer ${auth.token}`
    }
    return req
  })

  // ── Response: 统一错误处理 ─────────────────────────────────
  http.interceptors.response.use(
    (res) => {
      // 业务错误（code != 0）
      if (res.data && res.data.code !== 0 && res.data.code !== undefined) {
        ElMessage.error(res.data.message || '请求失败')
        return Promise.reject(new Error(res.data.message))
      }
      return res.data
    },
    async (error) => {
      const status = error.response?.status
      if (status === 401) {
        const auth = useAuthStore()
        auth.logout()
        await navigateTo('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status >= 500) {
        ElMessage.error('服务器错误，请稍后重试')
      } else {
        ElMessage.error(error.response?.data?.message || '网络错误')
      }
      return Promise.reject(error)
    }
  )

  // 注入全局
  nuxtApp.provide('http', http)
})
