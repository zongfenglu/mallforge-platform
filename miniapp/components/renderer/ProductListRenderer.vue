<template>
  <view class="product-list">
    <!-- Card style: vertical list -->
    <view v-if="props.style === 'card'" class="card-list">
      <view
        v-for="product in products"
        :key="product.id"
        class="card-item"
        @click="goDetail(product.id)"
      >
        <image :src="product.coverImage" mode="aspectFill" class="card-img" />
        <view class="card-info">
          <text class="card-name">{{ product.name }}</text>
          <view class="card-bottom">
            <text class="card-price">¥{{ product.salePrice }}</text>
            <text class="card-sales">已售{{ product.salesCount }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Horizontal style: scrollable cards -->
    <scroll-view v-else class="horizontal-scroll" scroll-x :show-scrollbar="false">
      <view class="horizontal-list">
        <view
          v-for="product in products"
          :key="product.id"
          class="horizontal-item"
          @click="goDetail(product.id)"
        >
          <image :src="product.coverImage" mode="aspectFill" class="horizontal-img" />
          <text class="horizontal-name">{{ product.name }}</text>
          <text class="horizontal-price">¥{{ product.salePrice }}</text>
          <text class="horizontal-sales">已售{{ product.salesCount }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- Loading / empty state -->
    <view v-if="products.length === 0 && loaded" class="empty-state">
      <text class="empty-text">暂无商品</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { get } from '@/utils/request'

const p = defineProps<{ props: {
  style: 'card' | 'horizontal'
  count: number
}}>()

const { props } = p

const products = ref<any[]>([])
const loaded = ref(false)

const loadProducts = async () => {
  try {
    const res: any = await get('/api/v1/products', {
      size: props.count || 10,
      status: 1
    }, true)
    products.value = res.data || []
  } catch (e) {
    // fail silently
  } finally {
    loaded.value = true
  }
}

const goDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

onMounted(loadProducts)
</script>

<style>
.product-list {
  background: #ffffff;
}

/* Card style */
.card-list {
  padding: 8rpx;
}
.card-item {
  display: flex;
  background: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}
.card-img {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
}
.card-info {
  flex: 1;
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.card-name {
  font-size: 28rpx;
  color: #1F2937;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
.card-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12rpx;
}
.card-price {
  font-size: 32rpx;
  font-weight: 700;
  color: #EF4444;
}
.card-sales {
  font-size: 22rpx;
  color: #9CA3AF;
}

/* Horizontal style */
.horizontal-scroll {
  white-space: nowrap;
}
.horizontal-list {
  display: inline-flex;
  padding: 12rpx 8rpx;
}
.horizontal-item {
  width: 240rpx;
  margin-right: 12rpx;
  background: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
}
.horizontal-img {
  width: 240rpx;
  height: 240rpx;
}
.horizontal-name {
  font-size: 24rpx;
  color: #1F2937;
  padding: 8rpx 12rpx 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  line-height: 1.4;
  white-space: normal;
}
.horizontal-price {
  font-size: 28rpx;
  font-weight: 700;
  color: #EF4444;
  padding: 4rpx 12rpx 0;
  display: block;
}
.horizontal-sales {
  font-size: 20rpx;
  color: #9CA3AF;
  padding: 4rpx 12rpx 12rpx;
  display: block;
}

/* Empty state */
.empty-state {
  padding: 40rpx;
  text-align: center;
}
.empty-text {
  font-size: 26rpx;
  color: #9CA3AF;
}
</style>
