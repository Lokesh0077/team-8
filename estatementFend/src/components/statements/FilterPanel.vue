<template>
  <div class="filter-panel bg-white rounded-lg shadow-md p-4 mb-4">
    <div class="filter-header flex justify-between items-center mb-4 pb-3 border-b border-gray-200">
      <h5 class="text-lg font-semibold text-gray-800">Quick Filters</h5>
      <button 
        @click="clearFilters" 
        class="clear-btn px-3 py-1 text-sm bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
        :disabled="disabled"
      >
        Clear All
      </button>
    </div>

    <div class="filter-groups space-y-4">
      <!-- Transaction Type Filter -->
      <div class="filter-group">
        <label class="block text-sm font-medium text-gray-700 mb-2">Transaction Type</label>
        <div class="filter-chips flex gap-2 flex-wrap">
          <button
            v-for="type in transactionTypes"
            :key="type.value"
            @click="toggleTransactionType(type.value)"
            class="filter-chip px-4 py-2 rounded-full text-sm font-medium transition-all"
            :class="getChipClass(type.value)"
            :disabled="disabled"
          >
            {{ type.label }}
          </button>
        </div>
      </div>

      <!-- Amount Range Filter -->
      <div class="filter-group">
        <label class="block text-sm font-medium text-gray-700 mb-2">Amount Range</label>
        <div class="amount-filters flex gap-2">
          <input
            v-model.number="minAmount"
            type="number"
            placeholder="Min amount"
            step="0.01"
            class="w-1/2 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            :disabled="disabled"
            @input="applyFilters"
          >
          <input
            v-model.number="maxAmount"
            type="number"
            placeholder="Max amount"
            step="0.01"
            class="w-1/2 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            :disabled="disabled"
            @input="applyFilters"
          >
        </div>
      </div>

      <!-- Status Info -->
      <div v-if="hasActiveFilters" class="filter-status bg-blue-50 border border-blue-200 rounded p-3 text-sm text-blue-700">
        <span v-if="activeFilters.type !== 'all'">
          Type: <strong>{{ getTypeName(activeFilters.type) }}</strong>
        </span>
        <span v-if="minAmount || maxAmount" class="ml-2">
          Amount: <strong>{{ minAmount || '0' }} - {{ maxAmount || '∞' }}</strong>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed } from 'vue';
import './../../styles/FilterPanel.css';

export default defineComponent({
  name: 'FilterPanel',
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['filter-change'],
  setup(props, { emit }) {
    const activeFilters = ref({
      type: 'all'
    });

    const minAmount = ref(null);
    const maxAmount = ref(null);

    const transactionTypes = [
      { value: 'all', label: 'All Transactions' },
      { value: 'debit', label: 'Debits Only' },
      { value: 'credit', label: 'Credits Only' }
    ];

    const hasActiveFilters = computed(() => {
      return activeFilters.value.type !== 'all' || minAmount.value || maxAmount.value;
    });

    const getChipClass = (type) => {
      const isActive = activeFilters.value.type === type;
      let baseClass = 'filter-chip px-4 py-2 rounded-full text-sm font-medium transition-all ';
      
      if (isActive) {
        return baseClass + 'bg-indigo-600 text-white shadow-md hover:bg-indigo-700';
      } else {
        return baseClass + 'bg-gray-100 text-gray-700 hover:bg-gray-200';
      }
    };

    const getTypeName = (type) => {
      const found = transactionTypes.find(t => t.value === type);
      return found ? found.label : type;
    };

    const toggleTransactionType = (type) => {
      activeFilters.value.type = type;
      applyFilters();
    };

    const clearFilters = () => {
      activeFilters.value.type = 'all';
      minAmount.value = null;
      maxAmount.value = null;
      applyFilters();
    };

    const applyFilters = () => {
      const filters = {
        type: activeFilters.value.type,
        minAmount: minAmount.value,
        maxAmount: maxAmount.value
      };

      console.log('Applying client-side filters:', filters);
      emit('filter-change', filters);
    };

    return {
      activeFilters,
      minAmount,
      maxAmount,
      transactionTypes,
      hasActiveFilters,
      getChipClass,
      getTypeName,
      toggleTransactionType,
      clearFilters,
      applyFilters
    };
  }
});
</script>

