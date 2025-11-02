import axios from 'axios';
import { useAuthStore } from '@/store/auth';

const getAuthHeaders = () => {
  const authStore = useAuthStore();
  const token = authStore.token;
  if (!token) {
    throw new Error('No authentication token found.');
  }
  return { Authorization: `Bearer ${token}` };
};

/**
 * Fetches a paginated list of all users.
 * @param {number} page - The page number to retrieve (0-indexed)
 * @param {number} size - The number of items per page
 */
const getAllUsers = async (page = 0, size = 20) => {
  try {
    const headers = getAuthHeaders();
    // We will build this backend endpoint later
    const response = await axios.get('/api/admin/users', {
      headers,
      params: { page, size }
    });
    return response.data; // Expects a Spring Page object { content: [], ... }
  } catch (error) {
    console.error('Error fetching users:', error);
    throw error.response?.data || new Error('Failed to fetch user list.');
  }
};

/**
 * Updates a user's active status.
 * @param {number} userId - The ID of the user to update
 * @param {boolean} isActive - The new status
 */
const setUserStatus = async (userId, isActive) => {
  try {
    const headers = getAuthHeaders();
    const response = await axios.put(
      `/api/admin/users/${userId}/status`,
      { isActive }, // Send the new status in the request body
      { headers }
    );
    return response.data;
  } catch (error) {
    console.error(`Error updating user ${userId}:`, error);
    throw error.response?.data || new Error('Failed to update user status.');
  }
};

export default {
  getAllUsers,
  setUserStatus,
};
