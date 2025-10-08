<template>
  <div class="flex items-center space-x-4">
    <div class="flex items-center space-x-3">
      <div class="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center">
        <span class="text-white text-sm font-medium">IU</span>
      </div>
      <div class="hidden sm:block">
        <p class="text-sm font-medium text-gray-900">Iniya User</p>
        <p class="text-xs text-gray-500">Bank Agent</p>
      </div>
    </div>
    <LogoutButton />
  </div>
</template>

<script setup>
import LogoutButton from '../auth/LogoutButton.vue'
</script>