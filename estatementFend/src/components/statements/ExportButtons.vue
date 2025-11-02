<template>
  <div class="export-container space-y-3">
    <div class="flex flex-col sm:flex-row gap-3">
      <button
        @click="handleExport('pdf')"
        :disabled="isButtonDisabled"
        class="export-btn pdf"
        :class="{ 'success': successState === 'pdf' }"
      >
        <svg v-if="exportStore.isExportingPdf" class="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        
        <svg v-else-if="successState === 'pdf'" class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        
        <svg v-else class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>

        <span v-if="exportStore.isExportingPdf">Exporting PDF...</span>
        <span v-else-if="successState === 'pdf'">Done!</span>
        <span v-else>Export PDF</span>
      </button>

      <button
        @click="handleExport('excel')"
        :disabled="isButtonDisabled"
        class="export-btn excel"
        :class="{ 'success': successState === 'excel' }"
      >
        <svg v-if="exportStore.isExportingExcel" class="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        
        <svg v-else-if="successState === 'excel'" class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>

        <svg v-else class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
           <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h12a2 2 0 012 2v12a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM10 6v12M14 6v12M6 10h12M6 14h12" />
        </svg>
        
        <span v-if="exportStore.isExportingExcel">Exporting Excel...</span>
        <span v-else-if="successState === 'excel'">Done!</span>
        <span v-else>Export Excel</span>
      </button>
    </div>

    <p v-if="!canExport" class="text-xs text-gray-500 italic">
      Please perform a search with an account, start date, and end date to enable exports.
    </p>

    <div v-if="exportStore.exportError" class="p-3 bg-red-50 text-red-700 border border-red-200 rounded-lg text-sm">
      <strong>Export Failed:</strong> {{ exportStore.exportError }}
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useExportStore } from '@/store/export';

const props = defineProps({
  searchCriteria: {
    type: Object,
    default: () => null
  }
});

const exportStore = useExportStore();
const successState = ref(null); // 'pdf', 'excel', or null

const isExporting = computed(() =>
  exportStore.isExportingPdf || exportStore.isExportingExcel
);

// Buttons are disabled if:
// 1. Criteria aren't valid
// 2. An export is already running
// 3. The success message is being shown
const isButtonDisabled = computed(() =>
  !canExport.value || isExporting.value || successState.value !== null
);

const canExport = computed(() =>
  props.searchCriteria &&
  props.searchCriteria.accountNumber &&
  props.searchCriteria.startDate &&
  props.searchCriteria.endDate
);

const handleExport = async (format) => {
  if (isButtonDisabled.value) return;

  const success = await exportStore.performExport(props.searchCriteria, format);

  if (success) {
    // Show success state for 2 seconds
    successState.value = format;
    setTimeout(() => {
      successState.value = null;
    }, 2000);
  }
};
</script>

<style scoped>
.export-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.625rem 1rem; /* 10px 16px */
  border: 1px solid transparent;
  border-radius: 0.5rem; /* rounded-lg */
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); /* shadow-sm */
  font-size: 0.875rem; /* text-sm */
  font-weight: 500; /* font-medium */
  color: #fff;
  transition: background-color 200ms ease, opacity 200ms ease;
  outline: none;
}

/* On desktop, buttons are auto-width */
@media (min-width: 640px) {
  .export-btn {
    width: auto;
  }
}

/* Focus ring style */
.export-btn:focus-visible {
  outline: 2px solid transparent;
  outline-offset: 2px;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2); /* indigo focus ring */
}

/* Disabled state */
.export-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* PDF button colors */
.export-btn.pdf {
  background-color: #dc2626; /* red-600 */
}
.export-btn.pdf:hover:not(:disabled) {
  background-color: #b91c1c; /* red-700 */
}

/* Excel button colors */
.export-btn.excel {
  background-color: #16a34a; /* green-600 */
}
.export-btn.excel:hover:not(:disabled) {
  background-color: #15803d; /* green-700 */
}

/* --- NEW SUCCESS STATE --- */
/* Both buttons turn green on success */
.export-btn.success {
  background-color: #16a34a; /* green-600 */
}
</style>