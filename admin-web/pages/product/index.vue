<template>
  <div>
    <div class="mf-page-header">
      <span class="mf-page-title">商品管理</span>
      <el-button type="primary" @click="$router.push('/product/create')">
        <el-icon><Plus /></el-icon> 添加商品
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="mf-card mb-4">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名称/品牌" clearable style="width:220px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable style="width:160px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadProducts">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 商品表格 -->
    <div class="mf-card">
      <el-table v-loading="loading" :data="products" style="width:100%">
        <el-table-column type="selection" width="48" />
        <el-table-column label="商品" min-width="280">
          <template #default="{ row }">
            <div class="product-cell">
              <el-image
                :src="row.coverImage"
                style="width:56px;height:56px;border-radius:8px;flex-shrink:0"
                fit="cover"
              >
                <template #error>
                  <div class="img-placeholder">暂无图</div>
                </template>
              </el-image>
              <div>
                <div class="product-name">{{ row.name }}</div>
                <div class="product-meta">{{ row.brand }} · {{ row.unit }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="salePrice"   label="售价"   width="100">
          <template #default="{ row }">¥{{ row.salePrice }}</template>
        </el-table-column>
        <el-table-column prop="totalStock"  label="库存"   width="80" />
        <el-table-column prop="salesCount"  label="销量"   width="80" />
        <el-table-column prop="status"      label="状态"   width="90">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              @change="toggleStatus(row)"
              active-text="上架"
              inactive-text="下架"
              inline-prompt
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt"   label="创建时间"  width="170" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button text size="small" @click="$router.push(`/product/${row.id}`)">编辑</el-button>
            <el-popconfirm title="确认删除？" @confirm="deleteProduct(row.id)">
              <template #reference>
                <el-button text size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @change="loadProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { $http } = useNuxtApp()

const query = reactive({ keyword: '', status: null as number | null, categoryId: null, page: 1, size: 20 })
const products   = ref<any[]>([])
const categories = ref<any[]>([])
const total      = ref(0)
const loading    = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const res: any = await ($http as any).get('/api/v1/products', { params: query })
    products.value = res.data
    total.value    = res.pagination?.total || 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res: any = await ($http as any).get('/api/v1/categories')
  categories.value = res.data || []
}

const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 2 : 1
  await ($http as any).patch(`/api/v1/products/${row.id}/status`, null, { params: { status: newStatus } })
  row.status = newStatus
}

const deleteProduct = async (id: number) => {
  await ($http as any).delete(`/api/v1/products/${id}`)
  ElMessage.success('已删除')
  await loadProducts()
}

const resetQuery = () => {
  Object.assign(query, { keyword: '', status: null, categoryId: null, page: 1 })
  loadProducts()
}

onMounted(() => { loadProducts(); loadCategories() })
</script>

<style scoped>
.mb-4 { margin-bottom: 16px; }
.product-cell { display: flex; align-items: center; gap: 12px; }
.product-name { font-weight: 500; font-size: 14px; }
.product-meta { font-size: 12px; color: var(--mf-text-muted); margin-top: 2px; }
.img-placeholder {
  width: 56px; height: 56px; border-radius: 8px;
  background: #F3F4F6;
  display: grid; place-items: center;
  font-size: 11px; color: #9CA3AF;
}
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
