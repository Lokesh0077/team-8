<template>
  <div class="transaction-grid">
    <div class="grid-header">
      <h3>Account Statements</h3>
      <div class="grid-controls">
        <FilterPanel @filter-change="handleFilterChange" />
        <AccountSelector 
          :accounts="accounts" 
          @account-change="handleAccountChange"
        />
      </div>
    </div>

    <div class="table-container">
      <div class="table-responsive">
        <table class="transaction-table">
          <thead>
            <tr>
              <th @click="sortBy('jxnRefNumber')" class="sortable">
                Ref Number
                <span class="sort-icon" v-if="sortField === 'jxnRefNumber'">
                  {{ sortOrder === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
              <th @click="sortBy('dateTime')" class="sortable">
                Date & Time
                <span class="sort-icon" v-if="sortField === 'dateTime'">
                  {{ sortOrder === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
              <th>Description</th>
              <th @click="sortBy('withdrawals')" class="sortable amount-col">
                Withdrawals
                <span class="sort-icon" v-if="sortField === 'withdrawals'">
                  {{ sortOrder === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
              <th @click="sortBy('credit')" class="sortable amount-col">
                Credit
                <span class="sort-icon" v-if="sortField === 'credit'">
                  {{ sortOrder === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
              <th @click="sortBy('runningBalance')" class="sortable amount-col">
                Balance
                <span class="sort-icon" v-if="sortField === 'runningBalance'">
                  {{ sortOrder === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <TransactionRow
              v-for="transaction in paginatedTransactions"
              :key="transaction.jxnRefNumber"
              :transaction="transaction"
            />
          </tbody>
        </table>
      </div>

      <div v-if="transactions.length === 0" class="empty-state">
        <div class="empty-icon">📄</div>
        <h4>No transactions found</h4>
        <p>Upload a CSV file or adjust your search filters</p>
      </div>
    </div>

    <Pagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="filteredTransactions.length"
      :page-size="itemsPerPage"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    />
  </div>
</template>

<script>
import TransactionRow from './TransactionRow.vue'
import FilterPanel from './FilterPanel.vue'
import AccountSelector from './AccountSelector.vue'
import Pagination from './Pagination.vue'

export default {
  name: 'TransactionGrid',
  components: {
    TransactionRow,
    FilterPanel,
    AccountSelector,
    Pagination
  },
  props: {
    transactions: {
      type: Array,
      default: () => []
    },
    accounts: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      sortField: 'dateTime',
      sortOrder: 'desc',
      currentPage: 1,
      itemsPerPage: 5,
      filters: {
        type: 'all'
      },
      selectedAccount: ''
    }
  },
  computed: {
    filteredTransactions() {
      let filtered = this.transactions

      // Filter by account number
      if (this.selectedAccount) {
        filtered = filtered.filter(t => 
          t.accountNumber === this.selectedAccount
        )
      }

      // Filter by transaction type
      if (this.filters.type !== 'all') {
        filtered = filtered.filter(t => {
          if (this.filters.type === 'debit') return t.withdrawals > 0
          if (this.filters.type === 'credit') return t.credit > 0
          return true
        })
      }

      // Sort transactions
      return filtered.sort((a, b) => {
        let aValue = a[this.sortField]
        let bValue = b[this.sortField]

        if (this.sortField === 'dateTime') {
          aValue = new Date(aValue)
          bValue = new Date(bValue)
        }

        if (aValue < bValue) return this.sortOrder === 'asc' ? -1 : 1
        if (aValue > bValue) return this.sortOrder === 'asc' ? 1 : -1
        return 0
      })
    },
    totalPages() {
      return Math.ceil(this.filteredTransactions.length / this.itemsPerPage)
    },
    paginatedTransactions() {
      const start = (this.currentPage - 1) * this.itemsPerPage
      const end = start + this.itemsPerPage
      return this.filteredTransactions.slice(start, end)
    }
  },
  methods: {
    sortBy(field) {
      if (this.sortField === field) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortField = field
        this.sortOrder = 'asc'
      }
    },
    handleFilterChange(filters) {
      this.filters = { ...this.filters, ...filters }
      this.currentPage = 1
    },
    handleAccountChange(accountNumber) {
      this.selectedAccount = accountNumber
      this.currentPage = 1
    },
    handlePageChange(page) {
      this.currentPage = page
    },
    handlePageSizeChange(pageSize) {
      this.itemsPerPage = pageSize
      this.currentPage = 1
    }
  }
}
</script>

<style scoped>
.transaction-grid {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.grid-header {
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.grid-header h3 {
  margin: 0 0 16px 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.grid-controls {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.table-container {
  overflow-x: auto;
}

.transaction-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;
}

.transaction-table th {
  background: #f8f9fa;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  color: #2d3748;
  border-bottom: 2px solid #e2e8f0;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.transaction-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #e2e8f0;
  transition: background-color 0.2s ease;
}

.transaction-table tbody tr:hover {
  background-color: #f7fafc;
}

.sortable {
  cursor: pointer;
  user-select: none;
  transition: color 0.2s ease;
}

.sortable:hover {
  color: #667eea;
}

.sort-icon {
  margin-left: 4px;
  font-weight: bold;
}

.amount-col {
  text-align: right;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  color: #4a5568;
  font-weight: 600;
}

.empty-state p {
  margin: 0;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .grid-header {
    padding: 16px;
  }
  
  .grid-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .transaction-table th,
  .transaction-table td {
    padding: 12px 8px;
    font-size: 0.8rem;
  }
}
</style>