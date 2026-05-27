<template>
  <div class="preview-notice-bar">
    <el-icon color="#F59E0B"><Bell /></el-icon>
    <span class="notice-text">{{ props.text || '欢迎光临本店' }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-notice-bar {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 12px; background: #FFFBEB;
  color: #92400E; font-size: 13px;
}
.notice-text { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
</style>
