// stores/cart.ts
import { defineStore } from 'pinia'
import { post, get } from '@/utils/request'

interface CartItem {
  skuId: number
  productId: number
  productName: string
  productImage: string
  specDesc: string
  price: number
  quantity: number
  selected: boolean
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [] as CartItem[],
  }),

  getters: {
    totalCount:    (s) => s.items.reduce((sum, i) => sum + i.quantity, 0),
    selectedItems: (s) => s.items.filter(i => i.selected),
    totalPrice: (s) =>
      s.items.filter(i => i.selected)
             .reduce((sum, i) => sum + i.price * i.quantity, 0),
  },

  actions: {
    addItem(item: Omit<CartItem, 'selected'>) {
      const existing = this.items.find(i => i.skuId === item.skuId)
      if (existing) {
        existing.quantity += item.quantity
      } else {
        this.items.push({ ...item, selected: true })
      }
      this.syncToServer()
    },

    removeItem(skuId: number) {
      this.items = this.items.filter(i => i.skuId !== skuId)
      this.syncToServer()
    },

    updateQuantity(skuId: number, quantity: number) {
      const item = this.items.find(i => i.skuId === skuId)
      if (item) { item.quantity = quantity; this.syncToServer() }
    },

    toggleSelect(skuId: number) {
      const item = this.items.find(i => i.skuId === skuId)
      if (item) item.selected = !item.selected
    },

    selectAll(val: boolean) { this.items.forEach(i => i.selected = val) },

    async syncToServer() {
      // 后端 Redis 同步（防抖处理）
      try {
        await post('/api/v1/cart/sync', { items: this.items })
      } catch (e) { /* 同步失败不影响本地操作 */ }
    },

    async fetchFromServer() {
      try {
        const res: any = await get('/api/v1/cart')
        if (res.data?.items) this.items = res.data.items
      } catch (e) { /* 离线时使用本地数据 */ }
    },
  },
})
