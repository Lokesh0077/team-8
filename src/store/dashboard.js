import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import uploadService from '../services/uploadService'

export const useDashboardStore = defineStore('dashboard', () => {
  // State
  const uploadProgress = ref(0)
  const isUploading = ref(false)
  const uploadHistory = ref([])
  const recentUploads = ref([])
  const uploadError = ref(null)
  const uploadSuccess = ref(null)

  // Getters
  const hasUploadHistory = computed(() => uploadHistory.value.length > 0)
  const recentUploadsCount = computed(() => recentUploads.value.length)

  // Actions
  const uploadFile = async (file) => {
    isUploading.value = true
    uploadProgress.value = 0
    uploadError.value = null
    uploadSuccess.value = null

    // Validate file first
    const validation = uploadService.validateCSVFile(file)
    if (!validation.isValid) {
      uploadError.value = validation.error
      isUploading.value = false
      return { success: false, error: validation.error }
    }

    try {
      const result = await uploadService.uploadCSVFile(file, (progress) => {
        uploadProgress.value = progress
      })

      if (result.success) {
        uploadSuccess.value = result.message
        // Add to recent uploads
        const newUpload = {
          id: Date.now(),
          fileName: file.name,
          uploadedAt: new Date().toISOString(),
          status: 'success',
          size: file.size
        }
        recentUploads.value.unshift(newUpload)
        uploadHistory.value.unshift(newUpload)
      } else {
        uploadError.value = result.error
      }

      return result
    } catch (error) {
      uploadError.value = 'Upload failed. Please try again.'
      return { success: false, error: 'Upload failed. Please try again.' }
    } finally {
      isUploading.value = false
      uploadProgress.value = 0
    }
  }

  const clearMessages = () => {
    uploadError.value = null
    uploadSuccess.value = null
  }

  const loadUploadHistory = async () => {
    try {
      // This would be implemented when backend is ready
      // const history = await uploadService.getUploadHistory()
      // uploadHistory.value = history
      
      // For now, use mock data
      uploadHistory.value = [
        {
          id: 1,
          fileName: 'transactions_q1_2024.csv',
          uploadedAt: new Date('2024-01-15').toISOString(),
          status: 'success',
          size: 24576
        },
        {
          id: 2,
          fileName: 'customer_statements.csv',
          uploadedAt: new Date('2024-01-10').toISOString(),
          status: 'success',
          size: 15360
        }
      ]
    } catch (error) {
      console.error('Error loading upload history:', error)
    }
  }

  return {
    // State
    uploadProgress,
    isUploading,
    uploadHistory,
    recentUploads,
    uploadError,
    uploadSuccess,
    
    // Getters
    hasUploadHistory,
    recentUploadsCount,
    
    // Actions
    uploadFile,
    clearMessages,
    loadUploadHistory
  }
})