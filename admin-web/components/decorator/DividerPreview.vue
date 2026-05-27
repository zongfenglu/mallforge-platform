<template>
  <div
    class="preview-divider"
    :style="{
      height: (props.height || 12) + 'px',
      background: props.color || '#F5F5F5'
    }"
  ></div>
</template>

<script setup lang="ts">
defineProps<{ props: any }>()
</script>

<style scoped>
.preview-divider { width: 100%; }
</style>
