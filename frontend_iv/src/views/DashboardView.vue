<template>
  <div class="min-h-screen bg-gray-50 p-4 md:p-8">
    <!-- Page Header -->
    <header class="mb-8">
      <h1 class="text-2xl md:text-3xl font-bold text-slate-900">Upload Account Statement</h1>
      <p class="text-slate-700">Drag & drop your .csv file here, or browse to upload transaction data.</p>
    </header>

    <!-- Main Content Area -->
    <main class="max-w-4xl mx-auto">
      <!-- File Upload Card -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-8">
        <div 
          @drop.prevent="handleDrop"
          @dragover.prevent="dragover = true"
          @dragleave.prevent="dragover = false"
          :class="{'border-primary-500 bg-primary-50': dragover, 'border-gray-300': !dragover}"
          class="border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors duration-200"
          @click="triggerFileInput"
        >
          <input 
            type="file" 
            ref="fileInput"
            @change="onFileSelect"
            accept=".csv"
            class="hidden" 
          />
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>
          <p class="mt-4 text-lg font-medium text-gray-900">Drag & drop your CSV file here</p>
          <p class="text-gray-600">or</p>
          <button type="button" class="mt-2 px-4 py-2 bg-primary-500 text-white font-medium rounded-md hover:bg-primary-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500">
            Browse Files
          </button>
          <p class="mt-2 text-sm text-gray-500">Only .csv files are accepted</p>
        </div>

        <!-- Upload Button -->
        <div class="mt-6 flex justify-end">
          <button 
            @click="uploadFile" 
            :disabled="!selectedFile || isUploading"
            :class="{'opacity-50 cursor-not-allowed': !selectedFile || isUploading, 'hover:bg-primary-600': selectedFile && !isUploading}"
            class="px-6 py-2 bg-primary-500 text-white font-medium rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-colors duration-200"
          >
            <span v-if="!isUploading">Upload</span>
            <span v-else>Uploading...</span>
          </button>
        </div>

        <!-- Progress Bar -->
        <div v-if="isUploading" class="mt-6">
          <div class="w-full bg-gray-200 rounded-full h-2.5">
            <div class="bg-primary-600 h-2.5 rounded-full" :style="{ width: uploadProgress + '%' }"></div>
          </div>
          <p class="text-xs text-gray-600 mt-1 text-right">{{ Math.round(uploadProgress) }}%</p>
        </div>
      </div>

      <!-- Success/Error Message -->
      <div v-if="message.text" 
           :class="{'bg-green-50 text-green-800 border-green-200': message.type === 'success', 'bg-red-50 text-red-800 border-red-200': message.type === 'error'}"
           class="p-4 rounded-md border mb-8">
        {{ message.text }}
      </div>

      <!-- Sample Table (Static, based on your image) - Just for visual context -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <h2 class="text-lg font-semibold bg-slate-500 text-white p-4">Account Statement - Sample Data</h2>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-100">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">Txn Ref Number</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">Date Time</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">Description</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">Withdrawals</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">Credit</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">Running Balance</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">BNK0001</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">1-Dec-2020 01:01:45</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">POS Transaction</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">2000</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"></td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">8000</td>
              </tr>
              <!-- More rows can be added here -->
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// Reactive data
const fileInput = ref(null);
const selectedFile = ref(null);
const isUploading = ref(false);
const uploadProgress = ref(0);
const dragover = ref(false);
const message = ref({ text: '', type: '' }); // type can be 'success' or 'error'

// Methods
const triggerFileInput = () => {
  fileInput.value.click();
};

const onFileSelect = (event) => {
  // Reset message
  message.value = { text: '', type: '' };
  const file = event.target.files[0];
  validateAndSetFile(file);
};

const handleDrop = (event) => {
  dragover.value = false;
  // Reset message
  message.value = { text: '', type: '' };
  const file = event.dataTransfer.files[0];
  validateAndSetFile(file);
};

const validateAndSetFile = (file) => {
  if (file) {
    // Check if the file is a CSV
    if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
      selectedFile.value = file;
      console.log('File selected:', file.name);
    } else {
      showMessage('Error: Please select a valid .csv file.', 'error');
      selectedFile.value = null;
    }
  }
};

const uploadFile = async () => {
  if (!selectedFile.value) return;

  isUploading.value = true;
  uploadProgress.value = 0;
  message.value = { text: '', type: '' };

  // Create a FormData object to send the file
  const formData = new FormData();
  formData.append('csvFile', selectedFile.value);

  try {
    // ** IMPORTANT: Replace this URL with your actual backend API endpoint **
    const apiUrl = 'http://your-backend-api/upload'; // e.g., 'http://localhost:8080/api/upload'

    const response = await axios.post(apiUrl, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total) {
          uploadProgress.value = (progressEvent.loaded / progressEvent.total) * 100;
        }
      },
    });

    // Simulate a delay to see the progress bar (optional, backend might be fast)
    // await new Promise(resolve => setTimeout(resolve, 1000));

    // Handle success response from backend
    showMessage('File processed successfully!', 'success');
    console.log('Upload successful:', response.data);

    // Reset form after successful upload
    selectedFile.value = null;
    if (fileInput.value) {
      fileInput.value.value = '';
    }

  } catch (error) {
    // Handle error response from backend
    console.error('Upload error:', error);
    const errorMsg = error.response?.data?.message || 'An error occurred during upload.';
    showMessage(`Error: ${errorMsg}`, 'error');
  } finally {
    isUploading.value = false;
    uploadProgress.value = 0;
  }
};

const showMessage = (text, type) => {
  message.value = { text, type };
  // Auto-hide success message after 5 seconds
  if (type === 'success') {
    setTimeout(() => {
      message.value = { text: '', type: '' };
    }, 5000);
  }
};
</script>