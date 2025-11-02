<template>
  <DashboardLayout>

    <div class="max-w-7xl mx-auto space-y-8">
      <div class="view-header mb-8">
        <h1 class="text-4xl font-bold text-gray-900">Account Statements</h1>
        <p class="text-gray-600 mt-2">View and analyze your transaction history</p>
      </div>

      <SearchForm @search="handleSearch" />

      <div class="px-6 py-4 bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="flex flex-col sm:flex-row justify-between items-center gap-4">
          <h3 class="text-lg font-semibold text-gray-800">Export Results</h3>
          <ExportButtons :search-criteria="lastSearchParams" />
        </div>
      </div>
      <div v-if="!isLoading && hasTransactions" class="stats-overview grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="stat-card bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium">Total Debits</p>
              <p class="text-2xl font-bold text-red-600 mt-2">{{ formatCurrency(stats.totalDebits) }}</p>
            </div>
            <div class="text-4xl text-red-200">↓</div>
          </div>
        </div>
        <div class="stat-card bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium">Total Credits</p>
              <p class="text-2xl font-bold text-green-600 mt-2">{{ formatCurrency(stats.totalCredits) }}</p>
            </div>
            <div class="text-4xl text-green-200">↑</div>
          </div>
        </div>
        <div class="stat-card bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium">Net Flow</p>
              <p class="text-2xl font-bold" :class="stats.netFlow >= 0 ? 'text-green-600' : 'text-red-600'" mt-2>
                {{ formatCurrency(stats.netFlow) }}
              </p>
            </div>
            <div class="text-4xl" :class="stats.netFlow >= 0 ? 'text-green-200' : 'text-red-200'">↔</div>
          </div>
        </div>
        <div class="stat-card bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium">Transactions</p>
              <p class="text-2xl font-bold text-blue-600 mt-2">{{ stats.transactionCount }}</p>
            </div>
            <div class="text-4xl text-blue-200">📊</div>
          </div>
        </div>
      </div>

      <div v-if="isLoading" class="loading-state flex flex-col items-center justify-center py-12">
        <div class="animate-spin mb-4">
          <span class="text-4xl">⟳</span>
        </div>
        <p class="text-gray-600 font-medium">Loading transactions...</p>
      </div>

      <TransactionGrid
        ref="transactionGrid" 
        @update-search-results="handleGridUpdate"
      />

      <div v-if="!isLoading && !hasTransactions && searchPerformed" class="empty-state bg-white rounded-lg shadow p-12 text-center">
        <div class="empty-icon text-6xl mb-4">💳</div>
        <h3 class="text-2xl font-bold text-gray-800 mb-2">No transactions found</h3>
        <p class="text-gray-600 mb-6">Adjust your search criteria or upload a new CSV file to see transactions</p>
        <button 
          @click="resetSearch" 
          class="px-6 py-2 bg-indigo-600 text-white rounded-lg font-medium hover:bg-indigo-700 transition"
        >
          Reset Search
        </button>
      </div>
    </div>

  </DashboardLayout>
</template>

<script>
import { defineComponent, ref } from 'vue';
import SearchForm from '@/components/statements/SearchForm.vue';
import TransactionGrid from '@/components/statements/TransactionGrid.vue';
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue';
import ExportButtons from '@/components/statements/ExportButtons.vue'; // <-- 1. IMPORTED

export default defineComponent({
  name: 'Statements',
  components: {
    SearchForm,
    TransactionGrid,
    DashboardLayout,
    ExportButtons // <-- 2. REGISTERED
  },
  setup() {
    const isLoading = ref(false); 
    const searchPerformed = ref(false);
    const transactionGrid = ref(null); 
    const lastSearchParams = ref(null); // <-- 3. ADDED REF FOR EXPORT PROPS
    
    const stats = ref({
      totalDebits: 0,
      totalCredits: 0,
      netFlow: 0,
      transactionCount: 0
    });

    const hasTransactions = ref(false); 

    const formatCurrency = (amount) => {
      if (amount === null || amount === undefined) return '0.00';
      const num = parseFloat(amount);
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(num);
    };

    const calculateStats = (transactions) => {
      const newStats = {
        totalDebits: 0,
        totalCredits: 0,
        netFlow: 0,
        transactionCount: transactions.length
      };

      transactions.forEach(transaction => {
        const withdrawals = parseFloat(transaction.withdrawals) || 0;
        const credit = parseFloat(transaction.credit) || 0;

        if (withdrawals > 0) {
          newStats.totalDebits += withdrawals;
        }
        if (credit > 0) {
          newStats.totalCredits += credit;
        }
      });

      newStats.netFlow = newStats.totalCredits - newStats.totalDebits;
      stats.value = newStats;
      hasTransactions.value = transactions.length > 0;
    };

    const handleSearch = (searchParams) => { 
      console.log('Statements received search parameters:', searchParams);
      searchPerformed.value = true; 
      lastSearchParams.value = searchParams; // <-- 4. UPDATED SEARCH PARAMS
      
      if (transactionGrid.value && transactionGrid.value.fetchTransactions) {
        transactionGrid.value.fetchTransactions(searchParams); 
      } else {
        console.error("TransactionGrid ref not available or fetchTransactions method missing");
      }
    };

    const handleGridUpdate = (searchData) => {
      console.log('Statements received grid update with data:', searchData);
      calculateStats(searchData.content || []);
    };

    const resetSearch = () => {
      searchPerformed.value = false; 
      hasTransactions.value = false; 
      lastSearchParams.value = null; // <-- 5. RESET SEARCH PARAMS
      stats.value = {
        totalDebits: 0,
        totalCredits: 0,
        netFlow: 0,
        transactionCount: 0
      };

      // Tell the grid to fetch all transactions (empty search)
      if (transactionGrid.value && transactionGrid.value.fetchTransactions) {
         transactionGrid.value.fetchTransactions({}); // Pass empty params
      }
      // Note: This does not reset the SearchForm component.
      // A better implementation might be to have SearchForm handle its own reset
      // and emit a new, empty search.
    };

    return {
      isLoading,
      searchPerformed,
      transactionGrid,
      stats,
      hasTransactions,
      lastSearchParams, // <-- 6. RETURNED FOR TEMPLATE
      formatCurrency,
      handleSearch,
      handleGridUpdate,
      resetSearch
    };
  }
});
</script>

<style scoped>
/* Your existing styles are unchanged */
.view-header {
  margin-bottom: 2rem;
}

.view-header h1 {
  font-size: 2.25rem;
  font-weight: 900;
  color: #111827;
}

.view-header p {
  color: #6b7280;
  margin-top: 0.5rem;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: box-shadow 0.3s;
}

.stat-card:hover {
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
}

.empty-state {
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 3rem;
  text-align: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #111827;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 1.5rem;
}

@media (max-width: 768px) {
  .stats-overview {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }

  .view-header h1 {
    font-size: 1.875rem;
  }
}
</style>