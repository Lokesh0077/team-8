<template>
  <DashboardLayout>
    <div class="max-w-7xl mx-auto space-y-8">
      <!-- Page Header -->
      <div class="mb-8">
        <h1 class="text-2xl md:text-3xl font-bold text-slate-900">Upload Account Statement</h1>
        <p class="text-slate-700 mt-2">Drag & drop your .csv file here, or browse to upload transaction data.</p>
      </div>

      <!-- Upload Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- File Upload -->
        <div class="space-y-6">
          <FileUpload 
            @upload-progress="handleUploadProgress"
            @upload-complete="handleUploadComplete"
            @upload-error="handleUploadError"
          />
          
          <UploadProgress :progress="dashboardStore.uploadProgress" />
          
          <!-- Success/Error Messages -->
          <div v-if="dashboardStore.uploadSuccess" class="p-4 bg-green-50 text-green-800 border border-green-200 rounded-md">
            {{ dashboardStore.uploadSuccess }}
          </div>
          <div v-if="dashboardStore.uploadError" class="p-4 bg-red-50 text-red-800 border border-red-200 rounded-md">
            {{ dashboardStore.uploadError }}
          </div>
        </div>

        <!-- Upload History -->
        <div>
          <UploadHistory :uploads="dashboardStore.uploadHistory" />
        </div>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-primary-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Uploads</p>
              <p class="text-2xl font-semibold text-gray-900">{{ dashboardStore.uploadHistory.length }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-green-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Successful</p>
              <p class="text-2xl font-semibold text-gray-900">
                {{ dashboardStore.uploadHistory.filter(u => u.status === 'success').length }}
              </p>
            </div>
          </div>
        </div>
        
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-blue-100 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Recent Activity</p>
              <p class="text-2xl font-semibold text-gray-900">{{ dashboardStore.recentUploadsCount }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup>
import { onMounted } from 'vue'
import { useDashboardStore } from '../store/dashboard'
import DashboardLayout from '../components/dashboard/DashboardLayout.vue'
import FileUpload from '../components/upload/FileUpload.vue'
import UploadProgress from '../components/upload/UploadProgress.vue'
import UploadHistory from '../components/upload/UploadHistory.vue'

const dashboardStore = useDashboardStore()

const handleUploadProgress = (progress) => {
  dashboardStore.uploadProgress = progress
}

const handleUploadComplete = (uploadData) => {
  dashboardStore.uploadSuccess = `File "${uploadData.fileName}" uploaded successfully!`
  // Reload history to show the new upload
  dashboardStore.loadUploadHistory()
}

const handleUploadError = (error) => {
  dashboardStore.uploadError = error
}

onMounted(() => {
  dashboardStore.loadUploadHistory()
})
</script>