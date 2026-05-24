// stores/auth.ts
import { defineStore } from 'pinia'

interface UserInfo {
  userId: number
  tenantId: number
  username: string
  realName: string
  avatar: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token:       (process.client ? localStorage.getItem('mf_token') : null) as string | null,
    refreshToken:(process.client ? localStorage.getItem('mf_refresh') : null) as string | null,
    userInfo:    (process.client ? JSON.parse(localStorage.getItem('mf_user') || 'null') : null) as UserInfo | null,
  }),

  getters: {
    isLoggedIn: (s) => !!s.token,
    tenantId:   (s) => s.userInfo?.tenantId,
    userId:     (s) => s.userInfo?.userId,
  },

  actions: {
    setAuth(data: { accessToken: string; refreshToken: string; userInfo: UserInfo }) {
      this.token        = data.accessToken
      this.refreshToken = data.refreshToken
      this.userInfo     = data.userInfo
      if (process.client) {
        localStorage.setItem('mf_token',   data.accessToken)
        localStorage.setItem('mf_refresh', data.refreshToken)
        localStorage.setItem('mf_user',    JSON.stringify(data.userInfo))
      }
    },
    logout() {
      this.token = null
      this.refreshToken = null
      this.userInfo = null
      if (process.client) {
        localStorage.removeItem('mf_token')
        localStorage.removeItem('mf_refresh')
        localStorage.removeItem('mf_user')
      }
    },
  },
})
