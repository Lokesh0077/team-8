<template>
  <div class="search-form">
    <div class="search-header">
      <h4>Search Transactions</h4>
      <button 
        @click="toggleAdvanced" 
        class="toggle-advanced"
        :class="{ active: showAdvanced }"
      >
        {{ showAdvanced ? 'Basic Search' : 'Advanced Search' }}
      </button>
    </div>

    <form @submit.prevent="handleSearch" class="search-fields">
      <!-- Basic Search -->
      <div class="basic-fields">
        <div class="field-group">
          <label for="accountSearch">Account Number</label>
          <input
            id="accountSearch"
            v-model="searchParams.accountNumber"
            type="text"
            placeholder="Enter account number..."
            class="search-input"
          >
        </div>

        <div class="field-group">
          <label for="dateFrom">From Date</label>
          <input
            id="dateFrom"
            v-model="searchParams.dateFrom"
            type="date"
            class="search-input"
          >
        </div>

        <div class="field-group">
          <label for="dateTo">To Date</label>
          <input
            id="dateTo"
            v-model="searchParams.dateTo"
            type="date"
            class="search-input"
          >
        </div>
      </div>

      <!-- Advanced Search -->
      <div v-if="showAdvanced" class="advanced-fields">
        <div class="field-group">
          <label for="description">Description</label>
          <input
            id="description"
            v-model="searchParams.description"
            type="text"
            placeholder="Search in descriptions..."
            class="search-input"
          >
        </div>

        <div class="field-row">
          <div class="field-group">
            <label for="minAmount">Min Amount</label>
            <input
              id="minAmount"
              v-model="searchParams.minAmount"
              type="number"
              placeholder="0.00"
              step="0.01"
              class="search-input"
            >
          </div>

          <div class="field-group">
            <label for="maxAmount">Max Amount</label>
            <input
              id="maxAmount"
              v-model="searchParams.maxAmount"
              type="number"
              placeholder="0.00"
              step="0.01"
              class="search-input"
            >
          </div>
        </div>

        <div class="field-group">
          <label>Transaction Type</label>
          <div class="radio-group">
            <label class="radio-option">
              <input
                v-model="searchParams.type"
                type="radio"
                value="all"
              >
              <span>All</span>
            </label>
            <label class="radio-option">
              <input
                v-model="searchParams.type"
                type="radio"
                value="debit"
              >
              <span>Debit</span>
            </label>
            <label class="radio-option">
              <input
                v-model="searchParams.type"
                type="radio"
                value="credit"
              >
              <span>Credit</span>
            </label>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" class="search-btn primary">
          <span class="btn-icon">🔍</span>
          Search
        </button>
        <button type="button" @click="handleReset" class="search-btn secondary">
          <span class="btn-icon">🔄</span>
          Reset
        </button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  name: 'SearchForm',
  data() {
    return {
      showAdvanced: false,
      searchParams: {
        accountNumber: '',
        dateFrom: '',
        dateTo: '',
        description: '',
        minAmount: null,
        maxAmount: null,
        type: 'all'
      }
    }
  },
  methods: {
    handleSearch() {
      this.$emit('search', { ...this.searchParams })
    },
    handleReset() {
      this.searchParams = {
        accountNumber: '',
        dateFrom: '',
        dateTo: '',
        description: '',
        minAmount: null,
        maxAmount: null,
        type: 'all'
      }
      this.$emit('search', { ...this.searchParams })
    },
    toggleAdvanced() {
      this.showAdvanced = !this.showAdvanced
    }
  }
}
</script>

<style scoped>
.search-form {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.search-header h4 {
  margin: 0;
  color: #2d3748;
  font-weight: 600;
  font-size: 1.25rem;
}

.toggle-advanced {
  background: #667eea;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-advanced:hover {
  background: #5a6fd8;
  transform: translateY(-1px);
}

.toggle-advanced.active {
  background: #764ba2;
}

.search-fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.basic-fields {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.advanced-fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-group label {
  font-weight: 500;
  color: #4a5568;
  font-size: 0.875rem;
}

.search-input {
  padding: 10px 12px;
  border: 1px solid #cbd5e0;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s ease;
  background: white;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.radio-group {
  display: flex;
  gap: 16px;
  margin-top: 4px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  color: #4a5568;
}

.radio-option input {
  margin: 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.search-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.search-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.search-btn.secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.search-btn.secondary:hover {
  background: #cbd5e0;
}

.btn-icon {
  font-size: 1rem;
}

@media (max-width: 768px) {
  .search-form {
    padding: 16px;
  }
  
  .search-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .basic-fields {
    grid-template-columns: 1fr;
  }
  
  .field-row {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>