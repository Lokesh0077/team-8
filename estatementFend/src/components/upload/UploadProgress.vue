<template>
  <transition name="fade">
    <div v-if="progress > 0 && progress < 100" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 space-y-4 animate-slide-in">
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <svg class="w-5 h-5 text-blue-600 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <span class="font-medium text-gray-900">Uploading file...</span>
        </div>
        <span class="text-sm font-semibold text-blue-600">{{ Math.round(progress) }}%</span>
      </div>

      <!-- Progress Bar -->
      <div class="space-y-2">
        <div class="w-full bg-gray-200 rounded-full h-2.5 overflow-hidden">
          <div 
            class="bg-gradient-to-r from-blue-500 to-blue-600 h-2.5 rounded-full transition-all duration-300"
            :style="{ width: progress + '%' }"
          ></div>
        </div>

        <!-- Details -->
        <div class="grid grid-cols-2 gap-4 text-sm">
          <div class="bg-gray-50 rounded-lg p-2">
            <p class="text-gray-600">Status</p>
            <p class="font-medium text-gray-900">In Progress</p>
          </div>
          <div class="bg-gray-50 rounded-lg p-2">
            <p class="text-gray-600">Speed</p>
            <p class="font-medium text-gray-900">{{ getUploadSpeed() }}</p>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  progress: {
    type: Number,
    default: 0
  }
})

const startTime = ref(null)
const startProgress = ref(0)

onMounted(() => {
  startTime.value = Date.now()
  startProgress.value = props.progress
})

const getUploadSpeed = () => {
  if (!startTime.value || props.progress <= startProgress.value) {
    return 'Calculating...'
  }

  const elapsed = (Date.now() - startTime.value) / 1000
  const progressMade = props.progress - startProgress.value
  const speed = (progressMade / elapsed).toFixed(1)

  if (speed < 1) return `${(speed * 1000).toFixed(0)} %/s`
  return `${speed}%/s`
}
</script>

<style scoped>
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-slide-in {
  animation: slideIn 0.3s ease-out;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>