<template>
  <div class="preview-section-title">
    <div class="title-bar">
      <div class="title-accent"></div>
      <span class="title-text">{{ props.title || '标题栏' }}</span>
    </div>
    <span v-if="props.showMore" class="more-link">更多 ›</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-section-title {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 12px; background: #fff;
}
.title-bar { display: flex; align-items: center; gap: 6px; }
.title-accent { width: 3px; height: 16px; background: #6C3AFA; border-radius: 2px; }
.title-text { font-size: 15px; font-weight: 600; }
.more-link { font-size: 12px; color: #9CA3AF; }
</style>
