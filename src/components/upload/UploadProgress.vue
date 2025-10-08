<template>
  <div v-if="progress > 0" class="mt-4">
    <div class="flex justify-between text-sm text-gray-600 mb-1">
      <span>Uploading...</span>
      <span>{{ Math.round(progress) }}%</span>
    </div>
    <div class="w-full bg-gray-200 rounded-full h-2">
      <div 
        class="bg-primary-500 h-2 rounded-full transition-all duration-300"
        :style="{ width: progress + '%' }"
      ></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  progress: {
    type: Number,
    default: 0
  }
})
</script>