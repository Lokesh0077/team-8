import axios from 'axios';
import { useAuthStore } from '@/store/auth'; // Assuming this path from your project structure

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Helper function to get auth token from your Pinia store
const getAuthHeaders = () => {
  const authStore = useAuthStore();
  const token = authStore.token;
  if (!token) {
    throw new Error('No authentication token found. Please log in.');
  }
  return { Authorization: `Bearer ${token}` };
};

/**
 * Tries to get the filename from the Content-Disposition header.
 * Provides a fallback name if the header is missing.
 */
const getFilenameFromResponse = (response) => {
  const header = response.headers['content-disposition'];
  if (header && header.includes('attachment')) {
    const filenamePart = header.split('filename=')[1];
    if (filenamePart) {
      return filenamePart.replace(/"/g, ''); // Clean up quotes
    }
  }

  // Fallback filename
  const extension = response.config.url.includes('pdf') ? 'pdf' : 'xlsx';
  return `transactions-export-${new Date().toISOString().split('T')[0]}.${extension}`;
};

/**
 * Calls the backend to export transactions based on search criteria.
 * @param {object} searchCriteria - The same criteria object from the search form.
 * @param {'pdf' | 'excel'} format - The desired file format.
 */
const exportTransactions = async (searchCriteria, format) => {
  const validFormats = {
    pdf: 'application/pdf',
    excel: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  };

  if (!validFormats[format]) {
    throw new Error('Invalid export format specified.');
  }

  try {
    const headers = getAuthHeaders();
    
    // We POST the search criteria to the backend's export endpoint
    const response = await axios.post(
      `${API_URL}/api/export/${format}`, // e.g., /api/export/pdf
      searchCriteria,
      {
        headers: {
          ...headers,
          'Content-Type': 'application/json',
          'Accept': validFormats[format],
        },
        responseType: 'blob' // CRITICAL: This tells axios to download a file
      }
    );

    // 1. Get filename from server response
    const filename = getFilenameFromResponse(response);

    // 2. Create a blob URL from the response data
    const blob = new Blob([response.data], { type: validFormats[format] });
    const url = window.URL.createObjectURL(blob);

    // 3. Create a temporary link to trigger the download
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();

    // 4. Clean up the temporary link and URL
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    return { success: true, filename: filename };

  } catch (error) {
    console.error('Export service failed:', error);
    
    // If the error response is JSON, we need to parse it differently
    if (error.response && error.response.data.type === 'application/json') {
      try {
        const errorData = JSON.parse(await error.response.data.text());
        throw new Error(errorData.message || 'Export failed due to a server error.');
      } catch (e) {
        throw new Error('An unknown server error occurred during export.');
      }
    }
    
    throw new Error('Export service failed. Please check your connection.');
  }
};

export default {
  exportTransactions,
};