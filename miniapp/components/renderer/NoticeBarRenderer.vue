<template>
  <view class="notice-bar">
    <text class="notice-icon">🔔</text>
    <view class="notice-content">
      <text class="notice-text">{{ props.text }}</text>
    </view>
    <text class="notice-arrow">></text>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  text: string
}}>()

const { props } = p
</script>

<style>
.notice-bar {
  display: flex;
  align-items: center;
  background: #FFFBEB;
  padding: 16rpx 24rpx;
}
.notice-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
  flex-shrink: 0;
}
.notice-content {
  flex: 1;
  overflow: hidden;
}
.notice-text {
  font-size: 24rpx;
  color: #92400E;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.notice-arrow {
  font-size: 24rpx;
  color: #92400E;
  margin-left: 12rpx;
  flex-shrink: 0;
}
</style>
