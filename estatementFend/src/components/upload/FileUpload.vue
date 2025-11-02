<template>
  <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start ">

    <div class="space-y-6 ">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
        <label
          for="file-upload-input"
          @drop.prevent="handleDrop"
          @dragover.prevent="onDragOver"
          @dragleave.prevent="dragover = false"
          :class="{
            'border-blue-500 bg-blue-50': dragover && !isUploading,
            'border-gray-300 bg-white': !dragover,
            'opacity-60 cursor-not-allowed': isUploading
          }"
          class="block border-2 border-dashed rounded-lg p-12 text-center cursor-pointer transition-all duration-200 hover:border-blue-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
          tabindex="0"
        >
          <input
            id="file-upload-input"
            type="file"
            @change="onFileSelect"
            accept=".csv"
            class="hidden"
            :disabled="isUploading"
          />

          <svg v-if="!isUploading" xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>

          <div v-if="isUploading" class="flex justify-center mb-4">
            <svg class="h-16 w-16 text-blue-600 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>

          <p class="text-lg font-semibold text-gray-900">
            {{ isUploading ? 'Uploading...' : 'Drag & drop your CSV file here' }}
          </p>
          <p class="text-sm text-gray-600 mt-1">or click to browse</p>

          <span v-if="!isUploading" class="mt-4 inline-block px-6 py-2 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2">
            Browse Files
          </span>

          <p class="mt-3 text-xs text-gray-500">
            Supported format: CSV (.csv) • Max file size: 10MB
          </p>
        </label>

        <div v-if="selectedFile && !isUploading" class="mt-6 p-4 bg-gray-50 rounded-lg border border-gray-200 animate-slide-in">
          <div class="flex items-start gap-4">
            <div class="flex-shrink-0">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div class="flex-1">
              <p class="font-medium text-gray-900">{{ selectedFile.name }}</p>
              <p class="text-sm text-gray-600 mt-1">{{ formatFileSize(selectedFile.size) }}</p>
            </div>
            <button
              @click="clearSelection"
              type="button"
              class="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>

          <div class="mt-4 flex gap-3">
            <button
              @click="uploadFile"
              :disabled="isUploading"
              type="button"
              class="flex-1 px-4 py-2 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span class="flex items-center justify-center gap-2">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                Upload CSV
              </span>
            </button>
            <button
              @click="clearSelection"
              type="button"
              class="px-4 py-2 bg-gray-200 text-gray-700 font-medium rounded-lg hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-offset-2 transition-colors"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-blue-50 border border-blue-200 rounded-lg p-6 h-fit sticky top-8">
        <p class="font-semibold text-blue-900 mb-4 flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            CSV File Format Requirements:
        </p>
        <ul class="text-sm text-blue-800 space-y-2 ml-4 list-disc list-outside">
            <li>Required columns: <code class="text-xs bg-blue-100 px-1 rounded">Txn Ref Number</code>, <code class="text-xs bg-blue-100 px-1 rounded">Account Number</code>, <code class="text-xs bg-blue-100 px-1 rounded">Date Time</code>, <code class="text-xs bg-blue-100 px-1 rounded">Description</code>, <code class="text-xs bg-blue-100 px-1 rounded">Withdrawals</code>, <code class="text-xs bg-blue-100 px-1 rounded">Credit</code></li>
            <li>Date format: <code class="text-xs bg-blue-100 px-1 rounded">DD-MM-YYYY HH:mm</code> (e.g., 01-12-2020 01:01)</li>
            <li>Amounts should be numeric values (e.g., <code class="text-xs bg-blue-100 px-1 rounded">123.45</code>)</li>
            <li>Empty cells allowed for <code class="text-xs bg-blue-100 px-1 rounded">Withdrawals</code> or <code class="text-xs bg-blue-100 px-1 rounded">Credit</code> (but not both on the same row)</li>
        </ul>
    </div>

  </div> </template>

<script setup>
import { ref } from 'vue'
import uploadService from '@/services/uploadService'
import { useDashboardStore } from '@/store/dashboard'

const emit = defineEmits(['file-uploaded', 'upload-error'])

const selectedFile = ref(null)
const isUploading = ref(false)
const dragover = ref(false)
const dashboardStore = useDashboardStore()

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const onFileSelect = (event) => {
  const file = event.target.files[0]
  validateAndSetFile(file)
}

const onDragOver = (event) => {
  if (isUploading.value) return
  dragover.value = true
}

const handleDrop = (event) => {
  if (isUploading.value) return
  dragover.value = false
  const file = event.dataTransfer.files[0]
  validateAndSetFile(file)
}

const validateAndSetFile = (file) => {
  if (file) {
    if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
      if (file.size > 10 * 1024 * 1024) {
        dashboardStore.setError('File size exceeds 10MB limit')
        selectedFile.value = null
      } else {
        selectedFile.value = file
        dashboardStore.clearError()
        dashboardStore.clearSuccess()
      }
    } else {
      dashboardStore.setError('Please select a valid CSV file (.csv)')
      selectedFile.value = null
    }
  }
}

const clearSelection = () => {
  selectedFile.value = null
}

const uploadFile = async () => {
  if (!selectedFile.value) return

  isUploading.value = true
  dashboardStore.clearError()
  dashboardStore.clearSuccess()

  try {
    const result = await uploadService.uploadCSVFile(selectedFile.value, (progress) => {
      dashboardStore.setProgress(progress)
    })

    if (result.success) {
      dashboardStore.setSuccess({
        message: result.message,
        recordCount: result.data?.recordCount || 0
      })
      selectedFile.value = null
      emit('file-uploaded')
    } else {
      dashboardStore.setError(result.error)
      emit('upload-error', result.error)
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || 'Upload failed. Please try again.'
    dashboardStore.setError(errorMsg)
    emit('upload-error', errorMsg)
  } finally {
    isUploading.value = false
    dashboardStore.setProgress(0)
  }
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
</style>