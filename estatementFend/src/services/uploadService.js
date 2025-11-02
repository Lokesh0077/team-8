import axios from 'axios';
import { useAuthStore } from '@/store/auth.js'; // Import the Pinia auth store

class UploadService {
  constructor() {
    this.apiClient = axios.create({
      timeout: 30000, 
    });

    // Add an interceptor to include the auth token in every request
    this.apiClient.interceptors.request.use((config) => {
      const authStore = useAuthStore();
      const token = authStore.token;
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    }, (error) => {
      return Promise.reject(error);
    });
  }

  async uploadCSVFile(file, onProgress = null) {
    const formData = new FormData();
    // The backend @RequestParam("file") expects the key to be 'file'
    formData.append('file', file); 

    try {
      const response = await this.apiClient.post('/api/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          if (onProgress && progressEvent.total) {
            const progress = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            onProgress(progress);
          }
        },
      });

      return {
        success: true,
        data: response.data,
        message: response.data.message || 'File uploaded successfully'
      };
    } catch (error) {
      console.error('Upload error:', error);
      return {
        success: false,
        error: error.response?.data?.message || 'Upload failed. Please try again.'
      };
    }
  }

  async getUploadHistory() {
    try {
      // This would call your backend API to get upload history
      const response = await this.apiClient.get('/api/files/history'); // Corrected endpoint
      return response.data;
    } catch (error) {
      console.error('Error fetching upload history:', error);
      throw error;
    }
  }

  // This client-side validation logic is excellent and remains unchanged.
  validateCSVFile(file) {
    const validTypes = ['text/csv', 'application/vnd.ms-excel'];
    const maxSize = 10 * 1024 * 1024; // 10MB

    if (!file) {
      return { isValid: false, error: 'No file selected' };
    }
    if (!validTypes.includes(file.type) && !file.name.endsWith('.csv')) {
      return { isValid: false, error: 'Please select a valid CSV file' };
    }
    if (file.size > maxSize) {
      return { isValid: false, error: 'File size must be less than 10MB' };
    }
    return { isValid: true };
  }
}

export default new UploadService();