import { defineStore } from 'pinia';
import { ref } from 'vue';
import userService from '@/services/userService';

export const useUserStore = defineStore('user', () => {
  // State
  const profile = ref(null);
  const isLoading = ref(false);
  const error = ref(null);

  // Actions
  async function fetchProfile() {
    isLoading.value = true;
    error.value = null;
    try {
      const data = await userService.getProfile();
      profile.value = data;
    } catch (err) {
      error.value = err.message || 'Failed to load profile';
    } finally {
      isLoading.value = false;
    }
  }

  // Action to clear profile on logout
  function clearProfile() {
    profile.value = null;
    error.value = null;
  }

  return {
    profile,
    isLoading,
    error,
    fetchProfile,
    clearProfile,
  };
});