<template>
  <DashboardLayout>
    <div class="max-w-7xl mx-auto space-y-6">
      
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-slate-900">Upload Statement</h1>
        <p class="text-slate-600 mt-2">Upload a new .csv file to process transactions.</p>
      </div>

      <div class="space-y-3">
        <div v-if="dashboardStore.uploadSuccess" class="p-4 bg-green-50 text-green-800 border border-green-200 rounded-lg flex items-center gap-3 animate-slide-in">
          <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" /></svg>
          <div class="flex-1">
            <p class="font-medium">Upload Successful!</p>
            <p class="text-sm">{{ dashboardStore.uploadSuccess.recordCount || 0 }} records processed</p>
          </div>
          <button @click="dashboardStore.clearSuccess()" class="text-green-600 hover:text-green-800"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" /></svg></button>
        </div>

        <div v-if="dashboardStore.uploadError" class="p-4 bg-red-50 text-red-800 border border-red-200 rounded-lg flex items-center gap-3 animate-slide-in">
          <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" /></svg>
          <div class="flex-1">
            <p class="font-medium">Upload Failed</p>
            <p class="text-sm">{{ dashboardStore.uploadError }}</p>
          </div>
          <button @click="dashboardStore.clearError()" class="text-red-600 hover:text-red-800"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" /></svg></button>
        </div>
      </div>

      <FileUpload @file-uploaded="onFileUploaded" />
      
    </div>
  </DashboardLayout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useDashboardStore } from '@/store/dashboard'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import FileUpload from '@/components/upload/FileUpload.vue'

const router = useRouter()
const dashboardStore = useDashboardStore()

const onFileUploaded = () => {
  // When upload is done, reload the dashboard's history
  dashboardStore.loadUploadHistory()
  // Optional: redirect back to the dashboard
  // router.push('/dashboard')
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