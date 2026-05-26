<template>
  <div class="preview-coupon-group">
    <div v-for="i in 2" :key="i" class="coupon-item">
      <div class="coupon-left">
        <div class="coupon-amount">¥50</div>
        <div class="coupon-condition">满200可用</div>
      </div>
      <div class="coupon-right">
        <div class="coupon-name">满减券</div>
        <button class="coupon-btn">领取</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-coupon-group { padding: 8px; background: #F5F5F5; display: flex; flex-direction: column; gap: 6px; }
.coupon-item {
  display: flex; border-radius: 8px; overflow: hidden;
  background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.coupon-left {
  background: linear-gradient(135deg,#6C3AFA,#8B5CF6);
  padding: 12px 16px; display: flex; flex-direction: column;
  align-items: center; justify-content: center; min-width: 80px;
}
.coupon-amount { font-size: 22px; font-weight: 700; color: #fff; }
.coupon-condition { font-size: 10px; color: rgba(255,255,255,0.8); }
.coupon-right {
  flex: 1; padding: 12px; display: flex;
  align-items: center; justify-content: space-between;
}
.coupon-name { font-size: 13px; font-weight: 500; }
.coupon-btn {
  background: #6C3AFA; color: #fff; border: none;
  border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer;
}
</style>
