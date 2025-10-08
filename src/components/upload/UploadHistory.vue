<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
    <h3 class="text-lg font-semibold text-slate-900 mb-4">Upload History</h3>
    <div v-if="uploads.length === 0" class="text-center text-gray-500 py-8">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <p>No upload history yet</p>
    </div>
    <div v-else class="space-y-3">
      <div 
        v-for="upload in uploads" 
        :key="upload.id"
        class="flex items-center justify-between p-3 border border-gray-100 rounded-lg hover:bg-gray-50"
      >
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <div>
            <p class="font-medium text-gray-900">{{ upload.fileName }}</p>
            <p class="text-sm text-gray-500">{{ formatDate(upload.uploadedAt) }}</p>
          </div>
        </div>
        <span 
          :class="{
            'bg-green-100 text-green-800': upload.status === 'success',
            'bg-red-100 text-red-800': upload.status === 'error',
            'bg-yellow-100 text-yellow-800': upload.status === 'processing'
          }"
          class="px-2 py-1 rounded-full text-xs font-medium"
        >
          {{ upload.status }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const uploads = ref([])

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// This will be populated from the upload service
</script>