<template>
  <DashboardLayout>
    <div class="max-w-7xl mx-auto space-y-8">
      
      <!-- Page Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900">Admin Panel</h1>
        <p class="text-slate-600 mt-2">Manage users and system settings.</p>
      </div>

      <!-- Admin content -->
      <div class="space-y-8">
        
        <!-- User Management Component -->
        <UserManagement />

        <!-- Audit Logs Component -->
        <AuditLogViewer />

      </div>

    </div>
  </DashboardLayout>
</template>

<script setup>
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue';
import UserManagement from '@/components/admin/UserManagement.vue';
import AuditLogViewer from '@/components/admin/AuditLogViewer.vue';
</script>
