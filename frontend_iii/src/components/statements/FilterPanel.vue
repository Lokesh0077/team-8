<template>
  <div class="filter-panel">
    <div class="filter-header">
      <h5>Quick Filters</h5>
      <button @click="clearFilters" class="clear-btn">
        Clear All
      </button>
    </div>

    <div class="filter-groups">
      <div class="filter-group">
        <label>Transaction Type</label>
        <div class="filter-chips">
          <button
            v-for="type in transactionTypes"
            :key="type.value"
            @click="toggleTransactionType(type.value)"
            class="filter-chip"
            :class="{ active: activeFilters.type === type.value }"
          >
            {{ type.label }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FilterPanel',
  data() {
    return {
      activeFilters: {
        type: 'all'
      },
      transactionTypes: [
        { value: 'all', label: 'All' },
        { value: 'debit', label: 'Debit Only' },
        { value: 'credit', label: 'Credit Only' }
      ]
    }
  },
  methods: {
    toggleTransactionType(type) {
      this.activeFilters.type = type
      this.emitFilters()
    },
    clearFilters() {
      this.activeFilters.type = 'all'
      this.emitFilters()
    },
    emitFilters() {
      this.$emit('filter-change', this.activeFilters)
    }
  }
}
</script>

<style scoped>
.filter-panel {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 16px;
  backdrop-filter: blur(10px);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-header h5 {
  margin: 0;
  font-weight: 600;
  font-size: 1rem;
  color: white;
}

.clear-btn {
  background: none;
  border: none;
  color: #fff;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.clear-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.filter-groups {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 0.875rem;
  color: white;
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-chip {
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-chip:hover {
  background: rgba(255, 255, 255, 0.2);
}

.filter-chip.active {
  background: white;
  border-color: white;
  color: #667eea;
}

@media (max-width: 768px) {
  .filter-chips {
    gap: 6px;
  }
  
  .filter-chip {
    padding: 4px 10px;
    font-size: 0.7rem;
  }
}
</style>