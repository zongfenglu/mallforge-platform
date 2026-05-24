<template>
  <view class="product-grid" :style="{ '--cols': props.cols }">
    <view
      v-for="product in products"
      :key="product.id"
      class="product-card"
      @click="goDetail(product.id)"
    >
      <image :src="product.coverImage" mode="aspectFill" class="product-img" />
      <view class="product-info">
        <text class="product-name">{{ product.name }}</text>
        <view class="product-price-row">
          <text class="product-price">¥{{ product.salePrice }}</text>
          <text class="product-sales">已售 {{ product.salesCount }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { get } from '@/utils/request'

const p = defineProps<{ props: {
  cols: number
  count: number
  sourceType: string
  sourceValue: string
}}>()

const products = ref<any[]>([])

const loadProducts = async () => {
  try {
    const res: any = await get('/api/v1/products', {
      size: p.props.count,
      status: 1,
      sort: p.props.sourceValue === 'recommend' ? 'sales_count,desc' : 'created_at,desc'
    }, true)
    products.value = res.data || []
  } catch (e) {}
}

const goDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

onMounted(loadProducts)
</script>

<style>
.product-grid {
  display: grid;
  grid-template-columns: repeat(var(--cols, 2), 1fr);
  gap: 8rpx;
  padding: 8rpx;
}
.product-card {
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}
.product-img { width: 100%; aspect-ratio: 1; }
.product-info { padding: 12rpx; }
.product-name {
  font-size: 26rpx;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
.product-price-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: 8rpx;
}
.product-price { font-size: 30rpx; font-weight: 700; color: #EF4444; }
.product-sales { font-size: 22rpx; color: #9CA3AF; }
</style>
