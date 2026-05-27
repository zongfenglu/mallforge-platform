<template>
  <div class="preview-search-bar">
    <div class="search-inner">
      <el-icon><Search /></el-icon>
      <span>{{ props.placeholder || '搜索商品' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-search-bar { padding: 8px 12px; background: #fff; }
.search-inner {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 12px; background: #F3F4F6;
  border-radius: 20px; color: #9CA3AF; font-size: 13px;
}
</style>
