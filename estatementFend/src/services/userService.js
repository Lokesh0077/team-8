import axios from 'axios';
import { useAuthStore } from '@/store/auth';

// Get the API URL from your environment variables, or default to localhost
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Helper function to get the auth token from your auth store
const getAuthHeaders = () => {
  const authStore = useAuthStore();
  const token = authStore.token;
  if (!token) {
    throw new Error('No authentication token found. Please log in.');
  }
  return { Authorization: `Bearer ${token}` };
};

/**
 * Fetches the currently logged-in user's profile information.
 */
const getProfile = async () => {
  try {
    const headers = getAuthHeaders();
    const response = await axios.get(`${API_URL}/api/user/profile`, { headers });
    return response.data;
  } catch (error) {
    console.error('Error fetching user profile:', error);
    // Re-throw a cleaner error message for the component to catch
    throw error.response?.data || new Error('Failed to fetch profile');
  }
};

export default {
  getProfile,
};