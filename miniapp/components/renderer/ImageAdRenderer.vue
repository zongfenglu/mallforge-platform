<template>
  <view class="image-ad">
    <view v-if="props.images && props.images.length > 0">
      <view
        v-for="(img, index) in props.images"
        :key="index"
        class="image-ad-item"
        @click="handleClick(img.link)"
      >
        <image :src="img.url" mode="widthFix" class="image-ad-img" />
      </view>
    </view>
    <view v-else class="image-ad-placeholder">
      <text class="placeholder-text">图文广告</text>
    </view>
  </view>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  images: Array<{ url: string; link: string }>
}}>()

const { props } = p

const handleClick = (link: string) => {
  if (!link) return
  if (link.startsWith('/pages')) {
    uni.navigateTo({ url: link })
  } else if (link.startsWith('http')) {
    uni.navigateTo({ url: `/pages/webview/index?url=${encodeURIComponent(link)}` })
  }
}
</script>

<style>
.image-ad {
  width: 100%;
}
.image-ad-item {
  width: 100%;
}
.image-ad-img {
  width: 100%;
  display: block;
}
.image-ad-placeholder {
  width: 100%;
  height: 300rpx;
  background: linear-gradient(135deg, #FDE68A, #FCD34D);
  display: flex;
  align-items: center;
  justify-content: center;
}
.placeholder-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #92400E;
}
</style>
