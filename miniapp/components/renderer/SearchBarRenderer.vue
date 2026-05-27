<template>
  <view class="search-bar-wrapper">
    <view class="search-bar" @click="handleTap">
      <text class="search-icon">🔍</text>
      <text class="search-placeholder">{{ props.placeholder || '搜索商品' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  placeholder: string
}}>()

const { props } = p

const handleTap = () => {
  uni.navigateTo({ url: '/pages/product/search' })
}
</script>

<style>
.search-bar-wrapper {
  padding: 16rpx 24rpx;
}
.search-bar {
  display: flex;
  align-items: center;
  background: #F3F4F6;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
}
.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}
.search-placeholder {
  font-size: 26rpx;
  color: #9CA3AF;
}
</style>
