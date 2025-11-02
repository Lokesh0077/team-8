<template>
  <div class="flex items-center space-x-4">
    <div class="flex items-center space-x-3">
      <div class="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
        <span class="text-white text-sm font-medium">{{ userInitials }}</span>
      </div>
      <div class="hidden sm:block">
        <p class="text-sm font-medium text-gray-900">{{ capitalizedUsername }}</p>
      </div>
    </div>
    <LogoutButton />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/store/auth.js'; // Correct path to your store
import LogoutButton from './LogoutButton.vue';

// Initialize the store
const authStore = useAuthStore();

// Create a computed property to get the username safely
const username = computed(() => authStore.username);

// Create a computed property to capitalize the username
const capitalizedUsername = computed(() => {
  if (!username.value || username.value === 'Guest') {
    return 'Guest User';
  }
  return username.value.charAt(0).toUpperCase() + username.value.slice(1);
});

// Create a computed property to get the user's initials
const userInitials = computed(() => {
  if (!username.value || username.value === 'Guest') {
    return 'G';
  }
  return username.value.substring(0, 1).toUpperCase();
});
</script>