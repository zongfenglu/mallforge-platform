<template>
  <view
    class="divider"
    :style="{
      height: (props.height ? props.height * 2 : 24) + 'rpx',
      background: props.color || '#F5F5F5'
    }"
  />
</template>

<script setup lang="ts">
const p = defineProps<{ props: {
  height: number
  color: string
}}>()

const { props } = p
</script>

<style>
.divider {
  width: 100%;
}
</style>
