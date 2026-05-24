// stores/user.ts
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token:    uni.getStorageSync('mf_token')    as string | null,
    userInfo: uni.getStorageSync('mf_userInfo') as any | null,
    tenantId: uni.getStorageSync('mf_tenantId') as number | null,
  }),

  getters: {
    isLoggedIn: (s) => !!s.token,
  },

  actions: {
    setAuth(data: { accessToken: string; userInfo: any }) {
      this.token    = data.accessToken
      this.userInfo = data.userInfo
      this.tenantId = data.userInfo?.tenantId
      uni.setStorageSync('mf_token',    data.accessToken)
      uni.setStorageSync('mf_userInfo', data.userInfo)
      uni.setStorageSync('mf_tenantId', data.userInfo?.tenantId)
    },
    logout() {
      this.token    = null
      this.userInfo = null
      uni.removeStorageSync('mf_token')
      uni.removeStorageSync('mf_userInfo')
    },
  },
})
