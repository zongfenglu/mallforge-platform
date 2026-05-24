// utils/request.ts
import { useUserStore } from '@/stores/user'

const BASE_URL = 'https://api.yourdomain.com' // 替换为实际域名

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  data?: any
  params?: Record<string, any>
  noAuth?: boolean
}

export function request<T = any>(options: RequestOptions): Promise<T> {
  const { url, method = 'GET', data, params, noAuth = false } = options

  const userStore = useUserStore()

  // 处理 query 参数
  let fullUrl = BASE_URL + url
  if (params) {
    const qs = Object.entries(params)
      .filter(([, v]) => v !== null && v !== undefined)
      .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
      .join('&')
    if (qs) fullUrl += '?' + qs
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...(noAuth ? {} : { Authorization: `Bearer ${userStore.token}` }),
      },
      success(res: any) {
        if (res.statusCode === 401) {
          uni.navigateTo({ url: '/pages/user/login' })
          reject(new Error('未登录'))
          return
        }
        const body = res.data
        if (body.code !== 0) {
          uni.showToast({ title: body.message || '请求失败', icon: 'none' })
          reject(new Error(body.message))
          return
        }
        resolve(body as T)
      },
      fail(err) {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      },
    })
  })
}

export const get  = <T>(url: string, params?: any, noAuth = false) =>
  request<T>({ url, method: 'GET', params, noAuth })
export const post = <T>(url: string, data?: any) =>
  request<T>({ url, method: 'POST', data })
export const put  = <T>(url: string, data?: any) =>
  request<T>({ url, method: 'PUT', data })
export const patch = <T>(url: string, data?: any) =>
  request<T>({ url, method: 'PATCH', data })
