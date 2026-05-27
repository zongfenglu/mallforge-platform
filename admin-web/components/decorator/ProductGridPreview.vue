<template>
  <div class="preview-product-grid" :style="{ '--cols': props.cols || 2 }">
    <template v-if="products.length">
      <div v-for="p in products" :key="p.id" class="product-item">
        <img v-if="p.coverImage" :src="p.coverImage" class="product-img" />
        <div v-else class="product-img-placeholder">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="product-info">
          <div class="product-name">{{ p.name }}</div>
          <div class="product-price">¥{{ p.salePrice }}</div>
        </div>
      </div>
    </template>
    <template v-else>
      <div v-for="i in (props.count || 4)" :key="i" class="product-item">
        <div class="product-img-placeholder">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="product-info">
          <div class="product-name-bar"></div>
          <div class="product-price">¥ --</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ props: any }>()
const { $http } = useNuxtApp()
const products = ref<any[]>([])

const loadProducts = async () => {
  try {
    const res: any = await ($http as any).get('/api/v1/products', {
      params: { size: props.props.count || 4, status: 1 }
    })
    products.value = res.data?.records || res.data || []
  } catch (_) {}
}

onMounted(loadProducts)
watch(() => props.props.count, loadProducts)
</script>

<style scoped>
.preview-product-grid {
  display: grid;
  grid-template-columns: repeat(var(--cols, 2), 1fr);
  gap: 6px; padding: 6px; background: #F5F5F5;
}
.product-item { background: #fff; border-radius: 8px; overflow: hidden; }
.product-img { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; }
.product-img-placeholder {
  width: 100%; aspect-ratio: 1;
  background: #F3F4F6;
  display: grid; place-items: center; color: #D1D5DB; font-size: 28px;
}
.product-info { padding: 6px 8px; }
.product-name { font-size: 12px; color: #374151; line-height: 1.3; display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; }
.product-name-bar { height: 10px; background: #E5E7EB; border-radius: 4px; margin-bottom: 6px; }
.product-price { font-size: 13px; font-weight: 600; color: #EF4444; margin-top: 2px; }
</style>
