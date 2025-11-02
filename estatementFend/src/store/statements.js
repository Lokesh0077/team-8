import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import searchService from '@/services/searchService';

export const useStatementsStore = defineStore('statements', () => {
  // --- STATE ---
  const transactions = ref([]);
  const isLoading = ref(false);
  const error = ref(null);
  
  const filters = ref({
    description: '',
    startDate: '',
    endDate: '',
    minAmount: null,
    maxAmount: null,
    type: 'all', // 'all', 'DEBIT', 'CREDIT'
  });

  const pagination = ref({
    page: 1,
    size: 10,
    totalItems: 0,
    totalPages: 1,
  });

  const sort = ref({
    field: 'date',
    order: 'desc',
  });

  // --- GETTERS ---
  const hasTransactions = computed(() => transactions.value.length > 0);

  // --- ACTIONS ---
  async function getTransactions() {
    isLoading.value = true;
    error.value = null;

    try {
      const params = {
        page: pagination.value.page - 1, 
        size: pagination.value.size,
        sortBy: sort.value.field,
        sortOrder: sort.value.order,
        ...filters.value,
      };

      const data = await searchService.fetchTransactions(params);

      transactions.value = data.content;
      pagination.value.totalItems = data.totalElements;
      pagination.value.totalPages = data.totalPages;
      
    } catch (e) {
      error.value = 'Failed to fetch transactions.';
      console.error(e);
    } finally {
      isLoading.value = false;
    }
  }

  function applyFiltersAndSearch() {
    pagination.value.page = 1; // Reset to the first page for a new search
    getTransactions();
  }

  function changePage(newPage) {
    if (newPage > 0 && newPage <= pagination.value.totalPages) {
      pagination.value.page = newPage;
      getTransactions();
    }
  }

  function updateSort(field) {
    if (sort.value.field === field) {
      sort.value.order = sort.value.order === 'asc' ? 'desc' : 'asc';
    } else {
      sort.value.field = field;
      sort.value.order = 'desc';
    }
    applyFiltersAndSearch();
  }

  return {
    transactions,
    isLoading,
    error,
    filters,
    pagination,
    sort,
    hasTransactions,
    getTransactions,
    applyFiltersAndSearch,
    changePage,
    updateSort,
  };
});