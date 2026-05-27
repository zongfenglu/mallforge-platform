<template>
  <view class="coupon-group">
    <!-- Slide style: horizontal scroll -->
    <scroll-view v-if="props.style === 'slide'" class="coupon-scroll" scroll-x :show-scrollbar="false">
      <view class="coupon-slide-list">
        <view v-for="(coupon, index) in coupons" :key="index" class="coupon-card coupon-card-slide">
          <view class="coupon-left">
            <text class="coupon-symbol">¥</text>
            <text class="coupon-amount">{{ coupon.amount }}</text>
          </view>
          <view class="coupon-divider-v" />
          <view class="coupon-right">
            <text class="coupon-condition">{{ coupon.condition }}</text>
            <view class="coupon-btn" @click="handleReceive(coupon)">
              <text class="coupon-btn-text">领取</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- List style: vertical stack -->
    <view v-else class="coupon-list">
      <view v-for="(coupon, index) in coupons" :key="index" class="coupon-card coupon-card-list">
        <view class="coupon-left">
          <text class="coupon-symbol">¥</text>
          <text class="coupon-amount">{{ coupon.amount }}</text>
        </view>
        <view class="coupon-divider-v" />
        <view class="coupon-right">
          <text class="coupon-condition">{{ coupon.condition }}</text>
          <view class="coupon-btn" @click="handleReceive(coupon)">
            <text class="coupon-btn-text">领取</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  style: 'slide' | 'list'
}}>()

const { props } = p

// Placeholder coupons (API integration later)
const coupons = ref([
  { amount: 50, condition: '满200可用', received: false },
  { amount: 100, condition: '满500可用', received: false }
])

const handleReceive = (coupon: any) => {
  if (coupon.received) {
    uni.showToast({ title: '已领取', icon: 'none' })
    return
  }
  coupon.received = true
  uni.showToast({ title: '领取成功', icon: 'success' })
}
</script>

<style>
.coupon-group {
  background: #F5F5F5;
  padding: 16rpx;
}

/* Slide style */
.coupon-scroll {
  white-space: nowrap;
}
.coupon-slide-list {
  display: inline-flex;
  gap: 16rpx;
}

/* List style */
.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

/* Coupon card base */
.coupon-card {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
}
.coupon-card-slide {
  width: 400rpx;
  flex-shrink: 0;
}

/* Left section - purple gradient */
.coupon-left {
  display: flex;
  align-items: baseline;
  justify-content: center;
  padding: 24rpx 20rpx;
  background: linear-gradient(135deg, #7C3AED, #A78BFA);
  min-width: 160rpx;
}
.coupon-symbol {
  font-size: 24rpx;
  font-weight: bold;
  color: #ffffff;
}
.coupon-amount {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  margin-left: 4rpx;
}

/* Divider */
.coupon-divider-v {
  width: 2rpx;
  height: 80rpx;
  background: #E5E7EB;
  flex-shrink: 0;
}

/* Right section */
.coupon-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 20rpx;
}
.coupon-condition {
  font-size: 24rpx;
  color: #6B7280;
}
.coupon-btn {
  background: #7C3AED;
  border-radius: 24rpx;
  padding: 8rpx 24rpx;
}
.coupon-btn-text {
  font-size: 22rpx;
  color: #ffffff;
}
</style>
