<template>
  <div class="preview-product-grid" :style="{ '--cols': props.cols || 2 }">
    <div v-for="i in (props.count || 4)" :key="i" class="product-item">
      <div class="product-img-placeholder">
        <el-icon><Goods /></el-icon>
      </div>
      <div class="product-info">
        <div class="product-name-bar"></div>
        <div class="product-price">¥ --</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-product-grid {
  display: grid;
  grid-template-columns: repeat(var(--cols, 2), 1fr);
  gap: 6px; padding: 6px; background: #F5F5F5;
}
.product-item { background: #fff; border-radius: 8px; overflow: hidden; }
.product-img-placeholder {
  width: 100%; aspect-ratio: 1;
  background: #F3F4F6;
  display: grid; place-items: center; color: #D1D5DB; font-size: 28px;
}
.product-info { padding: 6px 8px; }
.product-name-bar { height: 10px; background: #E5E7EB; border-radius: 4px; margin-bottom: 6px; }
.product-price { font-size: 13px; font-weight: 600; color: #EF4444; }
</style>
