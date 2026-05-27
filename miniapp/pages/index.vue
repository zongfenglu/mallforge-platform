<template>
  <view class="index-page" :style="{ background: pageData.background }">
    <view v-if="loading" class="loading-wrap">
      <uni-load-more status="loading" />
    </view>
    <template v-else>
      <view
        v-for="comp in sortedComponents"
        :key="comp.id"
      >
        <!-- 动态渲染引擎：按 type 映射组件 -->
        <BannerRenderer      v-if="comp.type === 'banner'"         :props="comp.props" />
        <SearchBarRenderer   v-if="comp.type === 'search-bar'"     :props="comp.props" />
        <NoticeBarRenderer   v-if="comp.type === 'notice-bar'"     :props="comp.props" />
        <NavGridRenderer     v-if="comp.type === 'nav-grid'"       :props="comp.props" />
        <SectionTitleRenderer v-if="comp.type === 'section-title'" :props="comp.props" />
        <DividerRenderer     v-if="comp.type === 'divider'"        :props="comp.props" />
        <ImageAdRenderer     v-if="comp.type === 'image-ad'"       :props="comp.props" />
        <ProductGridRenderer  v-if="comp.type === 'product-grid'"  :props="comp.props" />
        <ProductGridRenderer  v-if="comp.type === 'product-recommend'" :props="comp.props" />
        <ProductGridRenderer  v-if="comp.type === 'flash-product'" :props="comp.props" />
        <ProductListRenderer  v-if="comp.type === 'product-list'"  :props="comp.props" />
        <CouponGroupRenderer  v-if="comp.type === 'coupon-group'"  :props="comp.props" />
        <FlashSaleRenderer    v-if="comp.type === 'flash-sale'"    :props="comp.props" />
        <!-- 未知类型：跳过（容错） -->
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { get } from '@/utils/request'
import { useUserStore } from '@/stores/user'

// 渲染组件引入
import BannerRenderer       from '@/components/renderer/BannerRenderer.vue'
import SearchBarRenderer    from '@/components/renderer/SearchBarRenderer.vue'
import NoticeBarRenderer    from '@/components/renderer/NoticeBarRenderer.vue'
import NavGridRenderer      from '@/components/renderer/NavGridRenderer.vue'
import SectionTitleRenderer from '@/components/renderer/SectionTitleRenderer.vue'
import DividerRenderer      from '@/components/renderer/DividerRenderer.vue'
import ImageAdRenderer      from '@/components/renderer/ImageAdRenderer.vue'
import ProductGridRenderer  from '@/components/renderer/ProductGridRenderer.vue'
import ProductListRenderer  from '@/components/renderer/ProductListRenderer.vue'
import CouponGroupRenderer  from '@/components/renderer/CouponGroupRenderer.vue'
import FlashSaleRenderer    from '@/components/renderer/FlashSaleRenderer.vue'

interface PageData {
  version: number
  name: string
  background: string
  components: Array<{ id: string; type: string; order: number; props: any }>
}

const DEFAULT_PAGE: PageData = {
  version: 1, name: '默认首页', background: '#F5F5F5', components: []
}

const userStore = useUserStore()
const loading   = ref(true)
const pageData  = ref<PageData>(DEFAULT_PAGE)

const sortedComponents = computed(() =>
  [...pageData.value.components].sort((a, b) => a.order - b.order)
)

const loadPage = async () => {
  try {
    const tenantId = userStore.tenantId || uni.getStorageSync('mf_tenantId')
    const res: any = await get('/api/v1/pages/published', { tenantId }, true)
    const json = res.data?.pageJson
    if (json) {
      pageData.value = typeof json === 'string' ? JSON.parse(json) : json
    }
  } catch (e) {
    // 加载失败使用默认
    pageData.value = DEFAULT_PAGE
  } finally {
    loading.value = false
  }
}

onMounted(loadPage)
// 每次进入页面刷新
onShow(loadPage)
</script>

<style>
.index-page { min-height: 100vh; }
.loading-wrap { display: flex; justify-content: center; padding: 40rpx 0; }
</style>
