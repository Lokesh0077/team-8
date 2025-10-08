import axios from 'axios'

class UploadService {
  constructor() {
    this.apiClient = axios.create({
      baseURL: 'http://your-backend-api', // Will be replaced with actual backend URL
      timeout: 30000, // 30 seconds timeout for large files
    })
  }

  async uploadCSVFile(file, onProgress = null) {
    const formData = new FormData()
    formData.append('csvFile', file)

    try {
      const response = await this.apiClient.post('/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          if (onProgress && progressEvent.total) {
            const progress = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            )
            onProgress(progress)
          }
        },
      })

      return {
        success: true,
        data: response.data,
        message: 'File uploaded successfully'
      }
    } catch (error) {
      console.error('Upload error:', error)
      return {
        success: false,
        error: error.response?.data?.message || 'Upload failed. Please try again.'
      }
    }
  }

  async getUploadHistory() {
    try {
      // This would call your backend API to get upload history
      const response = await this.apiClient.get('/upload/history')
      return response.data
    } catch (error) {
      console.error('Error fetching upload history:', error)
      throw error
    }
  }

  validateCSVFile(file) {
    const validTypes = ['text/csv', 'application/vnd.ms-excel']
    const maxSize = 10 * 1024 * 1024 // 10MB

    if (!file) {
      return { isValid: false, error: 'No file selected' }
    }

    if (!validTypes.includes(file.type) && !file.name.endsWith('.csv')) {
      return { isValid: false, error: 'Please select a valid CSV file' }
    }

    if (file.size > maxSize) {
      return { isValid: false, error: 'File size must be less than 10MB' }
    }

    return { isValid: true }
  }
}

export default new UploadService()