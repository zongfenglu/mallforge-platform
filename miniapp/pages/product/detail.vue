<template>
  <view class="detail-page">
    <!-- 图片轮播 -->
    <swiper :circular="true" :indicator-dots="true" style="height:375px">
      <swiper-item v-for="(img, i) in images" :key="i">
        <image :src="img" mode="aspectFill" style="width:100%;height:375px" />
      </swiper-item>
    </swiper>

    <!-- 价格区 -->
    <view class="price-section">
      <view class="price-row">
        <text class="price">¥{{ selectedSku?.price || product.salePrice }}</text>
        <text class="market-price" v-if="product.marketPrice">¥{{ product.marketPrice }}</text>
        <view class="stock-tag">库存 {{ selectedSku?.stock ?? product.totalStock }}</view>
      </view>
      <view class="product-title">{{ product.name }}</view>
      <view class="sales-info">已售 {{ product.salesCount }} {{ product.unit }}</view>
    </view>

    <!-- SKU 选择器 -->
    <view class="sku-section" v-if="skus.length > 0">
      <view class="sku-label">选择规格</view>
      <view class="sku-list">
        <view
          v-for="sku in skus"
          :key="sku.id"
          class="sku-tag"
          :class="{ selected: selectedSkuId === sku.id, disabled: sku.stock === 0 }"
          @click="selectSku(sku)"
        >
          {{ formatSpec(sku.specJson) }}
        </view>
      </view>
    </view>

    <!-- 商品详情 -->
    <view class="desc-section">
      <view class="section-title">商品详情</view>
      <rich-text :nodes="product.description" />
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bar-action" @click="goCart">
        <uni-icons type="cart" size="24" />
        <text>购物车</text>
        <view class="cart-badge" v-if="cartStore.totalCount > 0">{{ cartStore.totalCount }}</view>
      </view>
      <button class="btn-cart" @click="addToCart">加入购物车</button>
      <button class="btn-buy"  @click="buyNow">立即购买</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { get } from '@/utils/request'
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()
const route  = useRoute?.() ?? { query: {} as any }

const productId    = ref(Number(getCurrentPages().at(-1)?.options?.id || 0))
const product      = ref<any>({})
const skus         = ref<any[]>([])
const selectedSkuId = ref<number | null>(null)

const images       = computed(() => {
  try { return JSON.parse(product.value.images || '[]') } catch { return [] }
})
const selectedSku  = computed(() => skus.value.find(s => s.id === selectedSkuId.value))

const loadProduct = async () => {
  const res: any = await get(`/api/v1/products/${productId.value}`, {}, true)
  product.value = res.data
  const skuRes: any = await get(`/api/v1/products/${productId.value}/skus`, {}, true)
  skus.value = skuRes.data || []
  if (skus.value.length) selectedSkuId.value = skus.value[0].id
}

const selectSku = (sku: any) => {
  if (sku.stock === 0) return
  selectedSkuId.value = sku.id
}

const formatSpec = (json: string) => {
  try {
    return Object.values(JSON.parse(json || '{}')).join(' / ')
  } catch { return '' }
}

const addToCart = () => {
  if (!selectedSkuId.value && skus.value.length > 0) {
    uni.showToast({ title: '请选择规格', icon: 'none' }); return
  }
  cartStore.addItem({
    skuId:        selectedSkuId.value || 0,
    productId:    productId.value,
    productName:  product.value.name,
    productImage: product.value.coverImage,
    specDesc:     selectedSku.value ? formatSpec(selectedSku.value.specJson) : '',
    price:        selectedSku.value?.price || product.value.salePrice,
    quantity:     1,
  })
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

const buyNow = () => {
  addToCart()
  goCart()
}

const goCart = () => uni.switchTab({ url: '/pages/cart/index' })

onMounted(loadProduct)
</script>

<style>
.detail-page { background: #F5F5F5; padding-bottom: 120rpx; }

.price-section {
  background: #fff; padding: 24rpx; margin-bottom: 16rpx;
}
.price-row { display: flex; align-items: center; gap: 16rpx; margin-bottom: 12rpx; }
.price { font-size: 48rpx; font-weight: 700; color: #EF4444; }
.market-price { font-size: 28rpx; color: #9CA3AF; text-decoration: line-through; }
.stock-tag { margin-left: auto; font-size: 24rpx; color: #6B7280; }
.product-title { font-size: 34rpx; font-weight: 600; line-height: 1.5; }
.sales-info { font-size: 26rpx; color: #9CA3AF; margin-top: 8rpx; }

.sku-section { background: #fff; padding: 24rpx; margin-bottom: 16rpx; }
.sku-label { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx; }
.sku-list { display: flex; flex-wrap: wrap; gap: 16rpx; }
.sku-tag {
  padding: 12rpx 24rpx; border-radius: 8rpx;
  border: 2rpx solid #E5E7EB;
  font-size: 26rpx;
}
.sku-tag.selected { border-color: #6C3AFA; color: #6C3AFA; background: #EDE9FE; }
.sku-tag.disabled { opacity: 0.4; }

.desc-section { background: #fff; padding: 24rpx; }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 16rpx; }

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  display: flex; align-items: center; gap: 16rpx;
  padding: 16rpx 24rpx; background: #fff;
  border-top: 1rpx solid #F3F4F6;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
}
.bar-action {
  display: flex; flex-direction: column; align-items: center;
  font-size: 22rpx; color: #6B7280; position: relative; flex-shrink: 0;
}
.cart-badge {
  position: absolute; top: -8rpx; right: -8rpx;
  background: #EF4444; color: #fff; font-size: 18rpx;
  width: 32rpx; height: 32rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.btn-cart {
  flex: 1; background: #F59E0B; color: #fff;
  border-radius: 48rpx; font-size: 28rpx; border: none;
}
.btn-buy {
  flex: 1; background: linear-gradient(135deg, #6C3AFA, #8B5CF6);
  color: #fff; border-radius: 48rpx; font-size: 28rpx; border: none;
}
</style>
