<template>
  <view class="nav-grid" :style="gridStyle">
    <view
      v-for="(item, index) in displayItems"
      :key="index"
      class="nav-item"
      @click="handleTap(item)"
    >
      <view v-if="item.icon" class="nav-icon-wrap">
        <image :src="item.icon" mode="aspectFit" class="nav-icon-img" />
      </view>
      <view v-else class="nav-icon-placeholder" :style="{ background: placeholderColors[index % placeholderColors.length] }">
        <text class="nav-icon-letter">{{ item.name.charAt(0) }}</text>
      </view>
      <text class="nav-name">{{ item.name }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  cols: number
  items: Array<{ name: string; icon: string; link: string }>
}}>()

const { props } = p

const placeholderColors = ['#7C3AED', '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#06B6D4', '#F97316']

const defaultItems = [
  { name: '分类', icon: '', link: '/pages/category/index' },
  { name: '新品', icon: '', link: '' },
  { name: '热卖', icon: '', link: '' },
  { name: '优惠券', icon: '', link: '' },
  { name: '限时抢', icon: '', link: '' }
]

const displayItems = computed(() => {
  return props.items && props.items.length > 0 ? props.items : defaultItems
})

const gridStyle = computed(() => {
  return `grid-template-columns: repeat(${props.cols || 4}, 1fr)`
})

const handleTap = (item: { name: string; icon: string; link: string }) => {
  if (!item.link) return
  if (item.link.startsWith('/pages')) {
    uni.navigateTo({ url: item.link })
  } else if (item.link.startsWith('http')) {
    uni.navigateTo({ url: `/pages/webview/index?url=${encodeURIComponent(item.link)}` })
  }
}
</script>

<style>
.nav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 20rpx 16rpx;
  gap: 16rpx 0;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.nav-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 8rpx;
}
.nav-icon-img {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
}
.nav-icon-placeholder {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8rpx;
}
.nav-icon-letter {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
}
.nav-name {
  font-size: 24rpx;
  color: #374151;
  text-align: center;
}
</style>
