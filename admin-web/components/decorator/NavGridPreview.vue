<template>
  <div class="preview-nav-grid" :style="{ '--cols': props.cols || 5 }">
    <div v-for="i in (props.cols || 5)" :key="i" class="nav-item">
      <div class="nav-icon">
        <el-icon><Grid /></el-icon>
      </div>
      <span class="nav-label">{{ props.items?.[i - 1]?.name || '导航' + i }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-nav-grid {
  display: grid;
  grid-template-columns: repeat(var(--cols, 5), 1fr);
  gap: 4px; padding: 12px 8px; background: #fff;
}
.nav-item {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
}
.nav-icon {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #6C3AFA, #8B5CF6);
  display: grid; place-items: center; color: #fff; font-size: 16px;
}
.nav-label { font-size: 11px; color: #6B7280; text-align: center; }
</style>
