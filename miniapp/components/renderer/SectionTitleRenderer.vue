<template>
  <view class="section-title">
    <view class="title-left">
      <view class="accent-bar" />
      <text class="title-text">{{ props.title }}</text>
    </view>
    <view v-if="props.showMore" class="title-right" @click="handleMore">
      <text class="more-text">更多 ></text>
    </view>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  title: string
  showMore: boolean
}}>()

const { props } = p

const handleMore = () => {
  uni.showToast({ title: '查看更多', icon: 'none' })
}
</script>

<style>
.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  padding: 20rpx 24rpx;
}
.title-left {
  display: flex;
  align-items: center;
}
.accent-bar {
  width: 4rpx;
  height: 28rpx;
  background: #7C3AED;
  border-radius: 2rpx;
  margin-right: 12rpx;
}
.title-text {
  font-size: 30rpx;
  font-weight: bold;
  color: #1F2937;
}
.title-right {
  padding-left: 16rpx;
}
.more-text {
  font-size: 24rpx;
  color: #9CA3AF;
}
</style>
