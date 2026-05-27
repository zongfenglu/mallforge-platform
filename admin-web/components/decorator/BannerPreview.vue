<template>
  <div class="preview-banner" :style="{ height: (props.height || 180) + 'px' }">
    <el-carousel
      v-if="props.images?.length"
      :autoplay="props.autoplay !== false"
      :interval="(props.interval || 3) * 1000"
      :height="(props.height || 180) + 'px'"
      indicator-position="none"
      arrow="never"
    >
      <el-carousel-item v-for="(img, i) in props.images" :key="i">
        <img :src="img.url" class="banner-img" />
      </el-carousel-item>
    </el-carousel>
    <div v-else class="banner-placeholder" style="background: linear-gradient(135deg,#667eea,#764ba2)">
      <el-icon :size="32" color="#fff"><PictureFilled /></el-icon>
      <span>轮播图 — 拖入后在右侧添加图片</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-banner { overflow: hidden; }
.banner-img { width: 100%; height: 100%; object-fit: cover; }
.banner-placeholder {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 8px; color: #fff; font-size: 13px; height: 100%;
}
</style>
