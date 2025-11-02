import { defineStore } from 'pinia';
import { ref } from 'vue';
import adminService from '@/services/adminService';
import { useAuthStore } from './auth';

export const useAdminStore = defineStore('admin', () => {
  // State
  const users = ref([]);
  const userPage = ref({
    content: [],
    number: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  });
  
  const auditLogs = ref([]);
  const logPage = ref({
    content: [],
    number: 0,
    size: 50,
    totalElements: 0,
    totalPages: 0,
  });

  const isLoading = ref(false);
  const error = ref(null);

  // Actions
  async function fetchUsers(page = 0, size = 20) {
    isLoading.value = true;
    error.value = null;
    try {
      const data = await adminService.getAllUsers(page, size);
      userPage.value = data; // Store the entire page object
      users.value = data.content; // For easy access to just the list
    } catch (err) {
      error.value = err.message || 'Failed to fetch users';
    } finally {
      isLoading.value = false;
    }
  }

  async function updateUserStatus(userId, isActive) {
    try {
      const updatedUser = await adminService.setUserStatus(userId, isActive);
      // Find and update the user in the local list
      const index = users.value.findIndex(u => u.id === userId);
      if (index !== -1) {
        users.value[index] = updatedUser;
      }
    } catch (err) {
      error.value = err.message || 'Failed to update user status';
    }
  }
  
  // Placeholder for fetching audit logs
  async function fetchAuditLogs(page = 0, size = 50) {
    isLoading.value = true;
    error.value = null;
    try {
      // We will create this adminService.getAuditLogs method later
      // const data = await adminService.getAuditLogs(page, size);
      // logPage.value = data;
      // auditLogs.value = data.content;
      console.log('fetchAuditLogs action called, but API is not yet built.');
    } catch (err) {
      error.value = err.message || 'Failed to fetch audit logs';
    } finally {
      isLoading.value = false;
    }
  }

  return {
    users,
    userPage,
    auditLogs,
    logPage,
    isLoading,
    error,
    fetchUsers,
    updateUserStatus,
    fetchAuditLogs,
  };
});