<template>
  <div class="preview-image-ad">
    <template v-if="props.images?.length">
      <div v-for="(img, i) in props.images" :key="i" class="ad-img">
        <img :src="img.url" alt="" />
      </div>
    </template>
    <div v-else class="ad-placeholder">
      <el-icon :size="28"><PictureFilled /></el-icon>
      <span>图文广告</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-image-ad { background: #fff; }
.ad-img img { width: 100%; display: block; }
.ad-placeholder {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 24px; color: #9CA3AF; font-size: 13px;
  background: linear-gradient(135deg, #FEF3C7, #FDE68A);
}
</style>
