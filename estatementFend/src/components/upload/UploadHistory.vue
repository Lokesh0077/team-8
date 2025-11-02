<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden flex flex-col h-96">
    <div class="px-6 py-4 bg-gradient-to-r from-blue-50 to-indigo-50 border-b border-gray-200 flex-shrink-0">
      <div class="flex items-center justify-between">
        <h3 class="text-lg font-semibold text-slate-900 flex items-center gap-2">
          <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          Upload History
        </h3>
        <span class="text-xs font-medium text-gray-600 bg-white px-3 py-1 rounded-full">
          {{ uploads.length }} files
        </span>
      </div>
    </div>

    <div v-if="!uploads || uploads.length === 0" class="flex-grow flex items-center justify-center p-8">
      <div class="text-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-300 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <p class="text-gray-500 font-medium">No upload history yet</p>
        <p class="text-gray-400 text-sm mt-1">Upload your first CSV file to get started</p>
      </div>
    </div>

    <div v-else class="flex-grow overflow-y-auto">
      <div class="divide-y divide-gray-100">
        <div 
          v-for="upload in uploads" 
          :key="upload.id"
          class="px-6 py-4 hover:bg-gray-50 transition-colors duration-150 flex items-center justify-between gap-4"
        >
          <div class="flex items-center gap-3 flex-grow min-w-0">
            <div class="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center flex-shrink-0">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>

            <div class="min-w-0 flex-grow">
              <p class="font-medium text-gray-900 truncate text-sm" :title="upload.fileName">
                {{ upload.fileName }}
              </p>
              <p class="text-xs text-gray-500 mt-0.5">
                {{ formatDate(upload.uploadedAt || upload.uploadTime) }}
              </p>
            </div>
          </div>

          <div class="flex-shrink-0">
            <span 
              :class="getStatusClasses(upload.status)"
              class="px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap flex items-center gap-1"
            >
              <span v-if="upload.status === 'success'" class="w-2 h-2 bg-current rounded-full"></span>
              <span v-else-if="upload.status === 'FAILED'" class="w-2 h-2 bg-current rounded-full"></span>
              <span v-else class="w-2 h-2 bg-current rounded-full animate-pulse"></span>
              {{ formatStatus(upload.status) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  uploads: {
    type: Array,
    required: true,
    default: () => []
  }
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatStatus = (status) => {
  const statusMap = {
    'success': 'Success',
    'FAILED': 'Failed',
    'UPLOADED': 'Processing',
    'PROCESSING': 'Processing'
  }
  return statusMap[status] || status
}

const getStatusClasses = (status) => {
  const classes = {
    'success': 'bg-green-100 text-green-700',
    'FAILED': 'bg-red-100 text-red-700',
    'UPLOADED': 'bg-amber-100 text-amber-700',
    'PROCESSING': 'bg-amber-1D00 text-amber-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}
</script>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>