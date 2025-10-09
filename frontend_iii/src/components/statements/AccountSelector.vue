<template>
  <div class="account-selector">
    <label for="accountSelect" class="selector-label">
      <span class="label-icon">🏦</span>
      Select Account
    </label>
    
    <select
      id="accountSelect"
      v-model="selectedAccount"
      @change="handleAccountChange"
      class="account-select"
    >
      <option value="">All Accounts</option>
      <option
        v-for="account in accounts"
        :key="account.number"
        :value="account.number"
      >
        {{ account.name }} ({{ formatAccountNumber(account.number) }})
      </option>
    </select>
  </div>
</template>

<script>
import { formatAccountNumber, formatCurrency } from '@/utils/formatters'

export default {
  name: 'AccountSelector',
  props: {
    accounts: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      selectedAccount: ''
    }
  },
  methods: {
    handleAccountChange() {
      this.$emit('account-change', this.selectedAccount)
    },
    formatAccountNumber(number) {
      return formatAccountNumber(number)
    },
    formatCurrency(amount) {
      return formatCurrency(amount)
    }
  }
}
</script>

<style scoped>
.account-selector {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 16px;
  backdrop-filter: blur(10px);
  min-width: 280px;
}

.selector-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 600;
  font-size: 0.9rem;
  color: white;
}

.label-icon {
  font-size: 1.1rem;
}

.account-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.account-select:focus {
  outline: none;
  border-color: white;
}

.account-select option {
  background: #2d3748;
  color: white;
}

@media (max-width: 768px) {
  .account-selector {
    min-width: auto;
    width: 100%;
  }
}
</style>