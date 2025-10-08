<template>
  <div class="min-h-screen bg-gray-50">
    <DashboardHeader />
    <div class="flex">
      <!-- Sidebar would go here if needed -->
      <main class="flex-1 p-6">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import DashboardHeader from './DashboardHeader.vue'
</script>