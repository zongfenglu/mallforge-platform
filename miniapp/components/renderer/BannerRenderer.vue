<template>
  <swiper
    :autoplay="props.autoplay"
    :interval="props.interval * 1000"
    :circular="true"
    :indicator-dots="true"
    indicator-color="rgba(255,255,255,0.5)"
    indicator-active-color="#ffffff"
    :style="{ height: props.height + 'px' }"
  >
    <swiper-item
      v-for="(img, i) in props.images"
      :key="i"
      @click="handleClick(img.link)"
    >
      <image
        :src="img.url"
        mode="aspectFill"
        style="width:100%;height:100%"
      />
    </swiper-item>
  </swiper>
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  height: number
  autoplay: boolean
  interval: number
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
