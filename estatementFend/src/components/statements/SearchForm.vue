<template>
  <div class="search-form p-6 bg-white rounded-xl shadow-lg mb-6">
    <div class="search-header flex justify-between items-center mb-5 border-b border-gray-200 pb-4">
      <h4 class="text-xl font-semibold text-gray-700">Search Transactions</h4>
      <button 
        @click="toggleAdvanced" 
        class="toggle-advanced px-4 py-2 bg-indigo-600 text-white rounded-lg font-medium text-sm transition-all duration-300 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        :class="{ 'bg-purple-700 hover:bg-purple-800': showAdvanced }"
        :disabled="isEmptyDatabase"
      >
        {{ showAdvanced ? 'Basic Search' : 'Advanced Search' }}
      </button>
    </div>

    <form @submit.prevent="handleSearch" class="search-fields space-y-6">
      <div class="basic-fields grid grid-cols-1 md:grid-cols-3 gap-4">
        
        <div class="field-group relative">
          <label for="accountSearch" class="block text-sm font-medium text-gray-600">Account Number</label>
          
          <input
            id="accountSearch"
            v-model="searchParams.accountNumber"
            type="text"
            placeholder="Type or select account number..."
            class="search-input w-full mt-1"
            :class="{ 'border-red-500': v$.accountNumber.$error }"
            @input="v$.accountNumber.$touch; showAccountSuggestions = true"
            @blur="hideSuggestionsWithDelay"
            @focus="showAccountSuggestions = true"
            :disabled="isEmptyDatabase || loadingAccounts"
            autocomplete="off"
          >
          
          <span v-if="v$.accountNumber.$error" class="text-xs text-red-500">Account number is required.</span>

          <ul 
            v-if="showAccountSuggestions && filteredAccounts.length > 0" 
            
            class="absolute top-full left-0 right-0 z-20 bg-white border border-gray-300 rounded-md mt-1 max-h-48 overflow-y-auto shadow-xl"
            >
            <li 
              @mousedown.prevent="selectAccount('')"
              class="px-3 py-2 cursor-pointer bg-gray-100 text-gray-600 text-sm border-b border-gray-200 hover:bg-gray-200"
            >
              -- Clear Account --
            </li>
            <li 
              v-for="account in filteredAccounts" 
              :key="account"
              @mousedown.prevent="selectAccount(account)"
              class="px-3 py-2 cursor-pointer hover:bg-indigo-100 text-sm text-gray-800"
            >
              {{ account }}
            </li>
          </ul>
        </div>
        <div class="field-group">
          <label for="dateFrom" class="block text-sm font-medium text-gray-600">From Date</label>
          <input
            id="dateFrom"
            v-model="searchParams.startDate"
            type="date"
            class="search-input w-full mt-1"
            :class="{ 'border-red-500': v$.startDate.$error }"
            @input="v$.startDate.$touch"
            :disabled="isEmptyDatabase"
          >
          <span v-if="v$.startDate.$error" class="text-xs text-red-500">Start date is required.</span>
        </div>

        <div class="field-group">
          <label for="dateTo" class="block text-sm font-medium text-gray-600">To Date</label>
          <input
            id="dateTo"
            v-model="searchParams.endDate"
            type="date"
            class="search-input w-full mt-1"
            :class="{ 'border-red-500': v$.endDate.$error }"
            @input="v$.endDate.$touch"
            :disabled="isEmptyDatabase"
          >
          <span v-if="v$.endDate.$error" class="text-xs text-red-500">End date is required.</span>
        </div>
      </div>

      <div v-if="showAdvanced" class="advanced-fields p-4 bg-gray-50 rounded-lg border border-gray-200 space-y-4">
        <div class="field-group">
          <label for="description" class="block text-sm font-medium text-gray-600">Description (Optional)</label>
          <input
            id="description"
            v-model="searchParams.description"
            type="text"
            placeholder="Search in descriptions... e.g., POS, Salary"
            class="search-input w-full mt-1"
            :disabled="isEmptyDatabase"
          >
        </div>

        <div class="field-group">
          <label for="txnRefNumber" class="block text-sm font-medium text-gray-600">Transaction Reference (Optional)</label>
          <input
            id="txnRefNumber"
            v-model="searchParams.txnRefNumber"
            type="text"
            placeholder="Enter transaction reference number..."
            class="search-input w-full mt-1"
            :disabled="isEmptyDatabase"
          >
        </div>
      </div>

      <div class="form-actions flex flex-col sm:flex-row gap-4 mt-6 pt-4 border-t border-gray-200">
        <button 
          type="submit" 
          class="search-btn primary flex items-center justify-center gap-2 px-4 py-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-lg font-medium hover:from-indigo-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50 transition-all" 
          :disabled="isEmptyDatabase || loadingAccounts"
        >
          <span class="btn-icon">🔍</span>
          <span>Search</span>
        </button>
        <button 
          type="button" 
          @click="handleReset" 
          class="search-btn secondary flex items-center justify-center gap-2 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg font-medium hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400 transition-all" 
          :disabled="isEmptyDatabase || loadingAccounts"
        >
          <span class="btn-icon">🔄</span>
          Reset
        </button>
      </div>

      <div v-if="error" class="mt-4 p-3 bg-red-50 border border-red-200 rounded text-sm text-red-700">
        {{ error }}
      </div>

      <div v-if="isEmptyDatabase" class="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded text-sm text-yellow-700">
        No transactions available in the database. Please upload a CSV file to proceed.
      </div>
    </form>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue';
import { useVuelidate } from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import axios from 'axios';
import './../../styles/Pagination.css';
import './../../styles/SearchForm.css';

export default defineComponent({
  name: 'SearchForm',
  emits: ['search'], 
  setup(props, { emit }) {
    const showAdvanced = ref(false);
    const loadingAccounts = ref(false);
    const error = ref(null);
    const isEmptyDatabase = ref(false);
    const availableAccounts = ref([]);
    
    const showAccountSuggestions = ref(false); 

    const searchParams = ref({
      accountNumber: '',
      startDate: '',
      endDate: '',
      description: '',
      txnRefNumber: '',
    });

    const rules = {
      accountNumber: { required },
      startDate: { required },
      endDate: { required }
    };

    const v$ = useVuelidate(rules, searchParams);

    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';

    const getAuthHeaders = () => {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('Authentication required');
      return { Authorization: `Bearer ${token}` };
    };

    const loadAvailableAccounts = async () => {
      loadingAccounts.value = true;
      try {
        const headers = getAuthHeaders();
        const response = await axios.get(`${apiUrl}/api/transactions/accounts`, { headers });
        availableAccounts.value = (response.data || []).map(String);
        
        if (availableAccounts.value.length === 0) {
          isEmptyDatabase.value = true;
        }
      } catch (err) {
        console.error('Failed to load accounts:', err);
        isEmptyDatabase.value = true;
        error.value = 'Failed to load available accounts.';
      } finally {
        loadingAccounts.value = false;
      }
    };

    const toggleAdvanced = () => {
      showAdvanced.value = !showAdvanced.value;
    };

    const convertDateToDateTime = (dateString, isEndDate = false) => {
      if (!dateString) return null;
      return isEndDate ? `${dateString}T23:59:59` : `${dateString}T00:00:00`;
    };

    // === MODIFIED COMPUTED PROPERTY FOR FILTERING ===
    const filteredAccounts = computed(() => {
        const query = searchParams.value.accountNumber?.trim().toLowerCase();
        
        // If the query is empty OR the suggestions are explicitly visible (i.e., user focused the box), show all accounts.
        if (!query || showAccountSuggestions.value) {
            // If query is empty, just return the whole list.
            if (!query) return availableAccounts.value;
        }
        
        // If query exists, filter based on the query.
        return availableAccounts.value.filter(account =>
            account.toLowerCase().includes(query)
        );
    });
    // ===============================================

    const selectAccount = (accountNumber) => {
        searchParams.value.accountNumber = accountNumber;
        showAccountSuggestions.value = false;
        v$.value.accountNumber.$touch();
    };

    const hideSuggestionsWithDelay = () => {
        setTimeout(() => {
            showAccountSuggestions.value = false;
        }, 150);
    };
    
    const handleSearch = async () => {
      const isValid = await v$.value.$validate();
      if (!isValid) return;

      error.value = null;

      const criteria = {
        accountNumber: searchParams.value.accountNumber?.trim() || null,
        startDate: convertDateToDateTime(searchParams.value.startDate, false),
        endDate: convertDateToDateTime(searchParams.value.endDate, true),
        description: searchParams.value.description?.trim() || null,
        txnRefNumber: searchParams.value.txnRefNumber?.trim() || null,
      };

      emit('search', criteria);
    };

    const handleReset = () => {
      searchParams.value = {
        accountNumber: '',
        startDate: '',
        endDate: '',
        description: '',
        txnRefNumber: '',
      };
      v$.value.$reset();
      error.value = null;
      emit('search', { 
          accountNumber: null,
          startDate: null,
          endDate: null
      });
    };

    onMounted(() => {
      loadAvailableAccounts();
    });

    return {
      showAdvanced,
      loadingAccounts,
      error,
      searchParams,
      v$,
      availableAccounts,
      toggleAdvanced,
      handleSearch,
      handleReset,
      isEmptyDatabase,
      showAccountSuggestions,
      filteredAccounts,
      selectAccount,
      hideSuggestionsWithDelay
    };
  }
});
</script>