<template>
  <div class="preview-banner" :style="{ height: props.height + 'px', background: 'linear-gradient(135deg,#667eea,#764ba2)' }">
    <div class="banner-placeholder">
      <el-icon :size="32" color="#fff"><PictureFilled /></el-icon>
      <span>轮播图 {{ props.images?.length || 0 }} 张</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-banner {
  display: flex; align-items: center; justify-content: center;
  min-height: 120px;
}
.banner-placeholder {
  display: flex; flex-direction: column; align-items: center;
  gap: 8px; color: #fff; font-size: 13px;
}
</style>
