<template>
  <button 
    @click="handleLogout"
    class="w-full flex items-center space-x-3 px-4 py-2 text-sm font-medium text-gray-600 hover:text-red-600 hover:bg-red-100 rounded-lg transition-colors duration-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:ring-offset-2"
  >
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5.636 5.636a9 9 0 1 0 12.728 0M12 3v9" />
    </svg>
    <span>Logout</span>
  </button>
</template>

<script setup>
import { useAuthStore } from '@/store/auth.js'; // Make sure this path is correct
import { useUserStore } from '@/store/user.js'; // Import user store
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const userStore = useUserStore(); // Get user store instance
const router = useRouter();

const handleLogout = () => {
  // 1. Call the logout action from the auth store
  authStore.logout();
  
  // 2. Clear the user profile data
  userStore.clearProfile(); 

  // 3. Redirect the user to the login page
  router.push('/login'); 
};
</script>