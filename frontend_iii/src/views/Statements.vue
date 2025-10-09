<template>
  <div class="statements-view">
    <div class="view-header">
      <h1>Account Statements</h1>
      <p>View and analyze your transaction history</p>
    </div>

    <div class="view-content">
      <!-- Search Section -->
      <SearchForm @search="handleSearch" />
      
      <!-- Stats Overview -->
      <div class="stats-overview" v-if="!isLoading && hasTransactions">
        <div class="stat-card">
          <div class="stat-icon debit">↓</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatCurrency(stats.totalDebits) }}</div>
            <div class="stat-label">Total Debits</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon credit">↑</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatCurrency(stats.totalCredits) }}</div>
            <div class="stat-label">Total Credits</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon" :class="netFlowClass">↔</div>
          <div class="stat-info">
            <div class="stat-value" :class="netFlowClass">
              {{ formatCurrency(stats.netFlow) }}
            </div>
            <div class="stat-label">Net Flow</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon neutral">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.transactionCount }}</div>
            <div class="stat-label">Transactions</div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Loading transactions...</p>
      </div>

      <!-- Transactions Grid -->
      <TransactionGrid
        v-if="!isLoading && hasTransactions"
        :transactions="filteredTransactions"
        :accounts="accounts"
      />

      <!-- Empty State -->
      <div v-if="!isLoading && !hasTransactions" class="empty-state">
        <div class="empty-icon">💳</div>
        <h3>No transactions found</h3>
        <p>Upload a CSV file or adjust your search criteria to see transactions</p>
        <button @click="resetSearch" class="action-btn">Reset Search</button>
      </div>
    </div>
  </div>
</template>

<script>
import SearchForm from '@/components/statements/SearchForm.vue'
import TransactionGrid from '@/components/statements/TransactionGrid.vue'
import { searchService } from '@/services/searchService'
import { formatCurrency } from '@/utils/formatters'

export default {
  name: 'Statements',
  components: {
    SearchForm,
    TransactionGrid
  },
  data() {
    return {
      isLoading: false,
      transactions: [],
      filteredTransactions: [],
      accounts: [],
      searchParams: {},
      stats: {
        totalDebits: 0,
        totalCredits: 0,
        netFlow: 0,
        transactionCount: 0
      }
    }
  },
  computed: {
    hasTransactions() {
      return this.filteredTransactions.length > 0
    },
    netFlowClass() {
      return this.stats.netFlow >= 0 ? 'positive' : 'negative'
    }
  },
  async mounted() {
    await this.loadInitialData()
  },
  methods: {
    async loadInitialData() {
      this.isLoading = true
      try {
        const [accountsResponse, transactionsResponse] = await Promise.all([
          searchService.getAccounts(),
          searchService.searchTransactions()
        ])
        
        this.accounts = accountsResponse
        this.transactions = transactionsResponse.transactions
        this.filteredTransactions = transactionsResponse.transactions
        this.calculateStats(this.transactions)
      } catch (error) {
        console.error('Failed to load data:', error)
      } finally {
        this.isLoading = false
      }
    },
    
    async handleSearch(searchParams) {
      this.isLoading = true
      this.searchParams = searchParams
      
      try {
        const response = await searchService.searchTransactions(searchParams)
        this.filteredTransactions = response.transactions
        this.calculateStats(this.filteredTransactions)
      } catch (error) {
        console.error('Search failed:', error)
      } finally {
        this.isLoading = false
      }
    },
    
    calculateStats(transactions) {
      const stats = {
        totalDebits: 0,
        totalCredits: 0,
        netFlow: 0,
        transactionCount: transactions.length
      }
      
      transactions.forEach(transaction => {
        if (transaction.withdrawals > 0) {
          stats.totalDebits += transaction.withdrawals
        }
        if (transaction.credit > 0) {
          stats.totalCredits += transaction.credit
        }
      })
      
      stats.netFlow = stats.totalCredits - stats.totalDebits
      this.stats = stats
    },
    
    resetSearch() {
      this.handleSearch({})
    },
    
    formatCurrency(amount) {
      return formatCurrency(amount)
    }
  }
}
</script>

<style scoped>
.statements-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.view-header {
  text-align: center;
  margin-bottom: 32px;
}

.view-header h1 {
  margin: 0 0 8px 0;
  color: #2d3748;
  font-weight: 700;
  font-size: 2.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.view-header p {
  margin: 0;
  color: #718096;
  font-size: 1.1rem;
}

.view-content {
  max-width: 1400px;
  margin: 0 auto;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
}

.stat-icon.debit {
  background: #fff5f5;
  color: #e53e3e;
}

.stat-icon.credit {
  background: #f0fff4;
  color: #38a169;
}

.stat-icon.positive {
  background: #f0fff4;
  color: #38a169;
}

.stat-icon.negative {
  background: #fff5f5;
  color: #e53e3e;
}

.stat-icon.neutral {
  background: #ebf8ff;
  color: #3182ce;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 4px;
  font-family: 'Monaco', 'Consolas', monospace;
}

.stat-value.positive {
  color: #38a169;
}

.stat-value.negative {
  color: #e53e3e;
}

.stat-label {
  font-size: 0.875rem;
  color: #718096;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  margin: 0;
  color: #718096;
  font-size: 1rem;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  margin: 0 0 12px 0;
  color: #2d3748;
  font-weight: 600;
  font-size: 1.5rem;
}

.empty-state p {
  margin: 0 0 24px 0;
  color: #718096;
  font-size: 1rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.action-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .statements-view {
    padding: 16px;
  }
  
  .view-header h1 {
    font-size: 2rem;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 1.25rem;
  }
}
</style>