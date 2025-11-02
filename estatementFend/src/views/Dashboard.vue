<template>
  <DashboardLayout>
    <div class="max-w-7xl mx-auto space-y-8">
      
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900">Dashboard Overview</h1>
        <p class="text-slate-600 mt-2">Welcome back. Here's a summary of your account activity.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard 
          icon="document" 
          label="Total Uploads" 
          :value="dashboardStore.uploadHistory.length"
          color="blue"
        />
        <StatCard 
          icon="check" 
          label="Successful" 
          :value="dashboardStore.successfulUploads"
          color="green"
        />
        <StatCard 
          icon="clock" 
          label="Recent Activity" 
          :value="dashboardStore.recentUploadsCount"
          color="amber"
        />
      </div>

      <div>
        <UploadHistory :uploads="dashboardStore.uploadHistory" />
      </div>

    </div>
  </DashboardLayout>
</template>

<script setup>
import { onMounted } from 'vue'
import { useDashboardStore } from '../store/dashboard'
import DashboardLayout from '../components/dashboard/DashboardLayout.vue'
import UploadHistory from '../components/upload/UploadHistory.vue'
import StatCard from '../components/dashboard/StatCard.vue'

const dashboardStore = useDashboardStore()

onMounted(() => {
  dashboardStore.loadUploadHistory()
})
</script>
