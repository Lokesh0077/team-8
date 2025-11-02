<template>
  <div class="transaction-grid">

    <div class="table-container bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden">
      
      <div v-if="loading" class="loading-state text-center py-12">
        <div class="animate-spin inline-block">
          <span class="text-3xl text-indigo-600">⟳</span>
        </div>
        <p class="mt-3 text-gray-600">Loading transactions...</p>
      </div>

      <div v-else-if="error" class="error-state p-8 text-center">
        <p class="text-red-600 font-medium">{{ error }}</p>
        <button 
          @click="fetchTransactions()" 
          class="mt-4 px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 transition"
        >
          Retry
        </button>
      </div>

      <div v-else-if="transactions.length === 0" class="empty-state p-12 text-center">
        <div class="empty-icon text-5xl mb-4 text-gray-400">📄</div>
        <h4 class="text-xl font-semibold text-gray-700 mb-2">No transactions found</h4>
        <p class="text-gray-500">
          {{ isEmptyDatabase ? 'The database is empty. Please upload a CSV file.' : 'No transactions match your search criteria.' }}
        </p>
      </div>

      <div v-else class="table-responsive overflow-x-auto">
        <table class="transaction-table w-full">
          <thead class="bg-gray-50 border-b border-gray-200">
            <tr>
              <th scope="col" class="th-cell cursor-pointer" @click="handleSort('txnRefNumber')">
                <div class="flex items-center gap-2">
                  <span>Ref Number</span>
                  <span v-if="sortField === 'txnRefNumber'" class="sort-icon">
                    {{ sortDirection === 'ASC' ? '↑' : '↓' }}
                  </span>
                </div>
              </th>
              <th scope="col" class="th-cell cursor-pointer" @click="handleSort('dateTime')">
                <div class="flex items-center gap-2">
                  <span>Date & Time</span>
                  <span v-if="sortField === 'dateTime'" class="sort-icon">
                    {{ sortDirection === 'ASC' ? '↑' : '↓' }}
                  </span>
                </div>
              </th>
              <th scope="col" class="th-cell text-right cursor-pointer" @click="handleSort('withdrawals')">
                <div class="flex items-center justify-end gap-2">
                  <span>Withdrawals</span>
                  <span v-if="sortField === 'withdrawals'" class="sort-icon">
                    {{ sortDirection === 'ASC' ? '↑' : '↓' }}
                  </span>
                </div>
              </th>
              <th scope="col" class="th-cell text-right cursor-pointer" @click="handleSort('credit')">
                <div class="flex items-center justify-end gap-2">
                  <span>Credit</span>
                  <span v-if="sortField === 'credit'" class="sort-icon">
                    {{ sortDirection === 'ASC' ? '↑' : '↓' }}
                  </span>
                </div>
              </th>
              <th scope="col" class="th-cell text-right cursor-pointer" @click="handleSort('runningBalance')">
                <div class="flex items-center justify-end gap-2">
                  <span>Balance</span>
                  <span v-if="sortField === 'runningBalance'" class="sort-icon">
                    {{ sortDirection === 'ASC' ? '↑' : '↓' }}
                  </span>
                </div>
              </th>
              <th scope="col" class="th-cell">Description</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <TransactionRow
              v-for="transaction in transactions"
              :key="transaction.id"
              :transaction="transaction"
            />
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="!isEmptyDatabase && transactions.length > 0" class="mt-6">
      <Pagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-items="totalItems"
        :page-size="pageSize"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script>
import './../../styles/TransactionGrid.css';
import { defineComponent, ref, computed, onMounted } from 'vue';
import TransactionRow from './TransactionRow.vue';
import Pagination from './Pagination.vue';
import axios from 'axios';

export default defineComponent({
  name: 'TransactionGrid',
  components: {
    TransactionRow,
    Pagination
  },
  emits: ['update-search-results'],
  setup(props, { emit, expose }) {
    const loading = ref(false);
    const error = ref(null);
    const transactions = ref([]);
    const isEmptyDatabase = ref(false);

    const currentPage = ref(0); 
    const pageSize = ref(20);
    const totalItems = ref(0);

    const sortField = ref('dateTime');
    const sortDirection = ref('DESC');

    const lastSearchParams = ref({});

    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';

    const getAuthHeaders = () => {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('Authentication required');
      return { Authorization: `Bearer ${token}` };
    };

    const fetchTransactions = async (searchParams = null) => {
      loading.value = true;
      error.value = null;

      if (searchParams) {
        currentPage.value = 0;
        lastSearchParams.value = searchParams;
      }

      try {
        const headers = getAuthHeaders();
        const params = lastSearchParams.value;

        const criteria = {
          accountNumber: params.accountNumber || null,
          startDate: params.startDate || null,
          endDate: params.endDate || null,
          description: params.description || null,
          txnRefNumber: params.txnRefNumber || null,
          page: currentPage.value,
          size: pageSize.value,
          sortBy: sortField.value,
          sortDirection: sortDirection.value
        };

        console.log('Fetching transactions with criteria:', criteria);

        const response = await axios.post(`${apiUrl}/api/search`, criteria, { headers });
        const data = response.data;

        transactions.value = data.content || [];
        totalItems.value = data.totalElements || 0;

        // Correctly handle page and size from backend
        currentPage.value = data.currentPage || 0;
        pageSize.value = data.pageSize || 20;

        const noFilter = !params.accountNumber && !params.startDate && !params.endDate;
        isEmptyDatabase.value = totalItems.value === 0 && noFilter;

        emit('update-search-results', data);
      } catch (err) {
        console.error('Fetch error:', err);
        error.value = err.response?.data?.message || 'Failed to fetch transactions. Please try again.';
        transactions.value = [];
        totalItems.value = 0;
        emit('update-search-results', { content: [], totalElements: 0 });
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchTransactions();
    });

    const handleSort = (field) => {
      if (sortField.value === field) {
        sortDirection.value = sortDirection.value === 'ASC' ? 'DESC' : 'ASC';
      } else {
        sortField.value = field;
        sortDirection.value = 'DESC';
      }
      currentPage.value = 0;
      fetchTransactions();
    };

    const handlePageChange = (newPage) => {
      // Convert 1-based UI page to 0-based backend page
      currentPage.value = newPage - 1;
      fetchTransactions();
    };

    const handlePageSizeChange = (newPageSize) => {
      pageSize.value = newPageSize;
      currentPage.value = 0;
      fetchTransactions();
    };

    const totalPages = computed(() => {
      return totalItems.value === 0 ? 1 : Math.ceil(totalItems.value / pageSize.value);
    });

    expose({ fetchTransactions });

    return {
      loading,
      error,
      transactions,
      isEmptyDatabase,
      sortField,
      sortDirection,
      currentPage,
      pageSize,
      totalItems,
      totalPages,
      handleSort,
      handlePageChange,
      handlePageSizeChange,
      fetchTransactions
    };
  }
});
</script>
