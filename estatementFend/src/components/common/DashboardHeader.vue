<template>
  <header class="bg-white shadow-sm border-b border-gray-200">
    <div class="max-w-8xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <div class="flex items-center">
          <h1 class="text-xl font-semibold text-slate-900">Account Statement Processing</h1>
        </div>
        <div class="flex items-center space-x-4">
          <UserProfile />
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import UserProfile from './UserProfile.vue'
</script>