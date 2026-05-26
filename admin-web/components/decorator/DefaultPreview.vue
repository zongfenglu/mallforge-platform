<template>
  <div class="preview-default">
    <el-icon><Grid /></el-icon>
    <span>{{ type }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any; type?: string }>()
</script>

<style scoped>
.preview-default {
  display: flex; align-items: center; gap: 8px;
  padding: 12px; background: #F9FAFB;
  color: #9CA3AF; font-size: 13px;
  border: 1px dashed #E5E7EB; margin: 4px;
  border-radius: 6px;
}
</style>
