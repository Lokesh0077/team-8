<template>
  <div class="account-selector bg-white rounded-lg shadow-md p-4">
    <label for="accountSelect" class="block text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
      <span class="text-lg">🏦</span>
      Select Account
    </label>
    
    <select
      id="accountSelect"
      v-model="selectedAccount"
      @change="handleAccountChange"
      class="account-select w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
      :class="{ 'opacity-50 cursor-not-allowed': disabled || loading }"
      :disabled="disabled || loading"
    >
      <option value="">
        {{ loading ? 'Loading accounts...' : 'All Accounts' }}
      </option>
      <optgroup v-if="accounts.length > 0" label="Available Accounts">
        <option
          v-for="account in accounts"
          :key="account"
          :value="account"
        >
          Account: {{ formatAccountNumber(account) }}
        </option>
      </optgroup>
      <optgroup v-if="accounts.length === 0 && !loading" label="Info">
        <option disabled>No accounts available</option>
      </optgroup>
    </select>

    <!-- Selected Account Info -->
    <div v-if="selectedAccount" class="mt-3 p-3 bg-indigo-50 border border-indigo-200 rounded text-sm text-indigo-700">
      <strong>Selected Account:</strong> {{ formatAccountNumber(selectedAccount) }}
    </div>

    <!-- Empty State -->
    <div v-if="accounts.length === 0 && !loading" class="mt-3 p-3 bg-yellow-50 border border-yellow-200 rounded text-sm text-yellow-700">
      No accounts available. Please upload a CSV file with transaction data.
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';

export default defineComponent({
  name: 'AccountSelector',
  props: {
    accounts: {
      type: Array,
      default: () => []
    },
    disabled: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['account-change'],
  setup(props, { emit }) {
    const selectedAccount = ref('');

    const formatAccountNumber = (accountNumber) => {
      if (!accountNumber) return '';
      
      // If it's already formatted (has spaces), return as is
      if (accountNumber.includes(' ')) {
        return accountNumber;
      }
      
      // Format as: XXX XXXXX XXXXX (common format for 15-digit accounts)
      // Adjust pattern based on your actual account number format
      const str = String(accountNumber).replace(/\D/g, ''); // Remove non-digits
      
      if (str.length === 13) {
        // Format: 0077098942 3 -> 0077 0989 42 3
        return `${str.substring(0, 4)} ${str.substring(4, 9)} ${str.substring(9, 13)}`;
      } else if (str.length === 15) {
        // Format: XXXXXXXXXXXXXXX -> XXXX XXXXX XXXXX
        return `${str.substring(0, 4)} ${str.substring(4, 9)} ${str.substring(9, 15)}`;
      }
      
      // If different length, just add spaces every 4 digits
      return str.replace(/(\d{4})(?=\d)/g, '$1 ');
    };

    const handleAccountChange = () => {
      console.log('Account changed to:', selectedAccount.value);
      emit('account-change', selectedAccount.value);
    };

    const clearSelection = () => {
      selectedAccount.value = '';
      handleAccountChange();
    };

    return {
      selectedAccount,
      formatAccountNumber,
      handleAccountChange,
      clearSelection
    };
  }
});
</script>
