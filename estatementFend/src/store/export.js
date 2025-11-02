import { defineStore } from 'pinia';
import { ref } from 'vue';
import exportService from '@/services/exportService';

export const useExportStore = defineStore('export', () => {
  // --- State ---
  
  // Use refs to track loading status for each format independently
  const isExportingPdf = ref(false);
  const isExportingExcel = ref(false);
  
  // Store any errors that occur
  const exportError = ref(null);

  // --- Actions ---

  /**
   * Main action to handle the export process.
   * @param {object} searchCriteria - The criteria from the statements store or search form.
   * @param {'pdf' | 'excel'} format - The format to export.
   */
  async function performExport(searchCriteria, format) {
    // 1. Set the correct loading flag
    if (format === 'pdf') {
      isExportingPdf.value = true;
    } else {
      isExportingExcel.value = true;
    }
    exportError.value = null;

    try {
      // 2. Call the service
      const result = await exportService.exportTransactions(searchCriteria, format);
      console.log(`Successfully exported ${result.filename}`);
      return true; // Indicate success
      
    } catch (err) {
      // 3. Store the error
      exportError.value = err.message || 'An unknown error occurred during export.';
      console.error(`Export failed for ${format}:`, exportError.value);
      return false; // Indicate failure
      
    } finally {
      // 4. Clear the correct loading flag
      if (format === 'pdf') {
        isExportingPdf.value = false;
      } else {
        isExportingExcel.value = false;
      }
    }
  }

  // --- Getters (optional, but good practice) ---
  
  // A helper to check if *any* export is in progress
  const isExporting = () => {
    return isExportingPdf.value || isExportingExcel.value;
  };

  return {
    // State
    isExportingPdf,
    isExportingExcel,
    exportError,

    // Actions
    performExport,

    // Getters
    isExporting,
  };
});