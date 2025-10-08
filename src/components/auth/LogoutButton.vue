<template>
  <button 
    @click="handleLogout"
    class="flex items-center space-x-2 px-3 py-2 text-sm text-gray-700 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors duration-200"
  >
    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
    </svg>
    <span class="hidden sm:inline">Logout</span>
  </button>
</template>

<script setup>
const handleLogout = () => {
  // This will be connected to auth store
  console.log('Logout clicked')
  // In real app: Clear user data, tokens, redirect to login
}
</script>