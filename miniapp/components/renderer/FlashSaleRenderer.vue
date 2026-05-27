<template>
  <view class="flash-sale">
    <!-- Header -->
    <view class="flash-header">
      <view class="flash-header-left">
        <text class="flash-title">限时抢购</text>
        <view v-if="props.showCountdown" class="flash-countdown">
          <text class="countdown-block">{{ countdown.hours }}</text>
          <text class="countdown-colon">:</text>
          <text class="countdown-block">{{ countdown.minutes }}</text>
          <text class="countdown-colon">:</text>
          <text class="countdown-block">{{ countdown.seconds }}</text>
        </view>
      </view>
      <text class="flash-more">更多 ></text>
    </view>

    <!-- Banner style -->
    <view v-if="props.style === 'banner'" class="flash-banner">
      <scroll-view class="flash-scroll" scroll-x :show-scrollbar="false">
        <view class="flash-banner-list">
          <view
            v-for="(item, index) in displayProducts"
            :key="index"
            class="flash-banner-item"
            @click="goDetail(item.id)"
          >
            <image :src="item.coverImage" mode="aspectFill" class="flash-banner-img" />
            <view class="flash-banner-info">
              <text class="flash-product-name">{{ item.name }}</text>
              <view class="flash-price-row">
                <text class="flash-price">¥{{ item.salePrice }}</text>
                <text class="flash-original-price">¥{{ item.originalPrice }}</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- List style -->
    <view v-else class="flash-list">
      <view
        v-for="(item, index) in displayProducts"
        :key="index"
        class="flash-list-item"
        @click="goDetail(item.id)"
      >
        <image :src="item.coverImage" mode="aspectFill" class="flash-list-img" />
        <view class="flash-list-info">
          <text class="flash-product-name">{{ item.name }}</text>
          <view class="flash-price-row">
            <text class="flash-price">¥{{ item.salePrice }}</text>
            <text class="flash-original-price">¥{{ item.originalPrice }}</text>
          </view>
          <view class="flash-progress-bar">
            <view class="flash-progress-fill" :style="{ width: item.progress + '%' }" />
          </view>
          <text class="flash-progress-text">已抢{{ item.progress }}%</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  style: 'banner' | 'list'
  showCountdown: boolean
}}>()

const { props } = p

// Placeholder products
const displayProducts = ref([
  { id: 1, name: '限时特惠商品A', coverImage: '', salePrice: 99, originalPrice: 299, progress: 72 },
  { id: 2, name: '限时特惠商品B', coverImage: '', salePrice: 199, originalPrice: 599, progress: 45 },
  { id: 3, name: '限时特惠商品C', coverImage: '', salePrice: 59, originalPrice: 159, progress: 88 }
])

// Countdown timer
const countdown = ref({ hours: '23', minutes: '59', seconds: '59' })
let countdownTimer: any = null

const startCountdown = () => {
  let total = 23 * 3600 + 59 * 60 + 59
  countdownTimer = setInterval(() => {
    if (total <= 0) {
      clearInterval(countdownTimer)
      return
    }
    total--
    const h = Math.floor(total / 3600)
    const m = Math.floor((total % 3600) / 60)
    const s = total % 60
    countdown.value = {
      hours: String(h).padStart(2, '0'),
      minutes: String(m).padStart(2, '0'),
      seconds: String(s).padStart(2, '0')
    }
  }, 1000)
}

const goDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

onMounted(() => {
  if (props.showCountdown) {
    startCountdown()
  }
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style>
.flash-sale {
  background: #FEF2F2;
}

/* Header */
.flash-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  border-bottom: 2rpx solid #FEE2E2;
}
.flash-header-left {
  display: flex;
  align-items: center;
}
.flash-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #DC2626;
  margin-right: 16rpx;
}
.flash-countdown {
  display: flex;
  align-items: center;
}
.countdown-block {
  background: #1F2937;
  color: #ffffff;
  font-size: 22rpx;
  font-weight: bold;
  padding: 4rpx 8rpx;
  border-radius: 4rpx;
  min-width: 40rpx;
  text-align: center;
}
.countdown-colon {
  font-size: 22rpx;
  font-weight: bold;
  color: #1F2937;
  margin: 0 4rpx;
}
.flash-more {
  font-size: 24rpx;
  color: #9CA3AF;
}

/* Banner style */
.flash-scroll {
  white-space: nowrap;
}
.flash-banner-list {
  display: inline-flex;
  padding: 16rpx 12rpx;
  gap: 12rpx;
}
.flash-banner-item {
  width: 240rpx;
  background: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
  flex-shrink: 0;
}
.flash-banner-img {
  width: 240rpx;
  height: 240rpx;
}
.flash-banner-info {
  padding: 12rpx;
}

/* List style */
.flash-list {
  padding: 12rpx 16rpx;
}
.flash-list-item {
  display: flex;
  background: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
}
.flash-list-img {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
}
.flash-list-info {
  flex: 1;
  padding: 16rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* Common */
.flash-product-name {
  font-size: 26rpx;
  color: #1F2937;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  line-height: 1.4;
}
.flash-price-row {
  display: flex;
  align-items: baseline;
  margin-top: 8rpx;
}
.flash-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #DC2626;
}
.flash-original-price {
  font-size: 22rpx;
  color: #9CA3AF;
  text-decoration: line-through;
  margin-left: 8rpx;
}

/* Progress bar (list style only) */
.flash-progress-bar {
  width: 100%;
  height: 16rpx;
  background: #FEE2E2;
  border-radius: 8rpx;
  margin-top: 12rpx;
  overflow: hidden;
}
.flash-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #DC2626, #EF4444);
  border-radius: 8rpx;
}
.flash-progress-text {
  font-size: 20rpx;
  color: #DC2626;
  margin-top: 4rpx;
}
</style>
