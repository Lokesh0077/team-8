import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from './auth'

export const useDashboardStore = defineStore('dashboard', () => {
  const isSidebarOpen = ref(true)

  const toggleSidebar = () => {
    isSidebarOpen.value = !isSidebarOpen.value
  }
  // State
  const uploadProgress = ref(0)
  const isUploading = ref(false)
  const uploadHistory = ref([])
  const uploadError = ref(null)
  const uploadSuccess = ref(null)

  // Getters
  const recentUploadsCount = computed(() => {
    const sevenDaysAgo = new Date()
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7)
    return uploadHistory.value.filter(
      upload => new Date(upload.uploadedAt || upload.uploadTime) > sevenDaysAgo
    ).length
  })

  const successfulUploads = computed(() => {
    const successStatuses = ['success', 'COMPLETED']; // <-- Add backend status here
    return uploadHistory.value.filter(u => successStatuses.includes(u.status)).length;
  })

  // Actions
  const setProgress = (progress) => {
    uploadProgress.value = progress
  }

  const setError = (error) => {
    uploadError.value = error
    // Auto-clear error after 5 seconds
    setTimeout(() => {
      uploadError.value = null
    }, 5000)
  }

  const clearError = () => {
    uploadError.value = null
  }

  const setSuccess = (data) => {
    uploadSuccess.value = data
    // Auto-clear success after 5 seconds
    setTimeout(() => {
      uploadSuccess.value = null
    }, 5000)
  }

  const clearSuccess = () => {
    uploadSuccess.value = null
  }

  const loadUploadHistory = async () => {
    const authStore = useAuthStore()
    try {
      const response = await axios.get('/api/files/history', {
        headers: { 'Authorization': `Bearer ${authStore.token}` }
      })

      uploadHistory.value = response.data.map(item => ({
        ...item,
        fileName: item.originalFilename || item.fileName,
        uploadedAt: item.uploadTime || item.uploadedAt,
        id: item.id || `${item.originalFilename}-${item.uploadTime}`
      }))
    } catch (error) {
      console.error('Error loading upload history:', error)
      setError('Could not load upload history.')
    }
  }

  return {
    // State
    uploadProgress,
    isUploading,
    uploadHistory,
    uploadError,
    uploadSuccess,
    isSidebarOpen,
    
    // Getters
    recentUploadsCount,
    successfulUploads,
    
    // Actions
    setProgress,
    setError,
    clearError,
    setSuccess,
    clearSuccess,
    loadUploadHistory,
    toggleSidebar
  }
})