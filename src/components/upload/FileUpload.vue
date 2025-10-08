<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
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
  </div>
</template>

<script setup>
import { ref } from 'vue';

const fileInput = ref(null);
const selectedFile = ref(null);
const isUploading = ref(false);
const dragover = ref(false);
const emit = defineEmits(['upload-progress', 'upload-complete', 'upload-error']);

const triggerFileInput = () => {
  fileInput.value.click();
};

const onFileSelect = (event) => {
  const file = event.target.files[0];
  validateAndSetFile(file);
};

const handleDrop = (event) => {
  dragover.value = false;
  const file = event.dataTransfer.files[0];
  validateAndSetFile(file);
};

const validateAndSetFile = (file) => {
  if (file) {
    if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
      selectedFile.value = file;
    } else {
      emit('upload-error', 'Error: Please select a valid .csv file.');
      selectedFile.value = null;
    }
  }
};

const uploadFile = async () => {
  if (!selectedFile.value) return;

  isUploading.value = true;
  
  // Simulate upload progress (will be replaced with actual service)
  for (let progress = 0; progress <= 100; progress += 10) {
    emit('upload-progress', progress);
    await new Promise(resolve => setTimeout(resolve, 200));
  }
  
  emit('upload-complete', { fileName: selectedFile.value.name });
  isUploading.value = false;
  selectedFile.value = null;
  if (fileInput.value) fileInput.value.value = '';
};
</script>