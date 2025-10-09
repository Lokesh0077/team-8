<template>
  <tr class="transaction-row" :class="rowClass">
    <td class="ref-number">
      <span class="ref-badge">{{ transaction.jxnRefNumber }}</span>
    </td>
    <td class="date-time">
      {{ formatDateTime(transaction.dateTime) }}
    </td>
    <td class="description">
      <span class="desc-text" :class="descriptionClass">
        {{ transaction.description }}
      </span>
    </td>
    <td class="withdrawals amount-cell">
      <span v-if="transaction.withdrawals" class="amount debit">
        -{{ formatCurrency(transaction.withdrawals) }}
      </span>
      <span v-else class="amount zero">-</span>
    </td>
    <td class="credit amount-cell">
      <span v-if="transaction.credit" class="amount credit">
        +{{ formatCurrency(transaction.credit) }}
      </span>
      <span v-else class="amount zero">-</span>
    </td>
    <td class="balance amount-cell">
      <span class="amount balance">
        {{ formatCurrency(transaction.runningBalance) }}
      </span>
    </td>
  </tr>
</template>

<script>
import { formatCurrency, formatDateTime } from '@/utils/formatters'

export default {
  name: 'TransactionRow',
  props: {
    transaction: {
      type: Object,
      required: true
    }
  },
  computed: {
    rowClass() {
      return {
        'debit-row': this.transaction.withdrawals > 0,
        'credit-row': this.transaction.credit > 0
      }
    },
    descriptionClass() {
      const desc = this.transaction.description.toLowerCase()
      if (desc.includes('pos')) return 'desc-pos'
      if (desc.includes('salary')) return 'desc-salary'
      if (desc.includes('interest')) return 'desc-interest'
      if (desc.includes('gst')) return 'desc-gst'
      if (desc.includes('transfer')) return 'desc-transfer'
      return 'desc-default'
    }
  },
  methods: {
    formatCurrency(amount) {
      return formatCurrency(amount)
    },
    formatDateTime(dateTime) {
      return formatDateTime(dateTime)
    }
  }
}
</script>

<style scoped>
.transaction-row {
  transition: all 0.3s ease;
}

.transaction-row:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.debit-row {
  border-left: 3px solid #fc8181;
}

.credit-row {
  border-left: 3px solid #68d391;
}

.ref-number {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 0.8rem;
}

.ref-badge {
  background: #edf2f7;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 500;
  color: #4a5568;
}

.date-time {
  color: #718096;
  font-size: 0.875rem;
  white-space: nowrap;
}

.description {
  max-width: 200px;
}

.desc-text {
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.875rem;
}

.desc-pos {
  background: #fff5f5;
  color: #c53030;
}

.desc-salary {
  background: #f0fff4;
  color: #276749;
}

.desc-interest {
  background: #ebf8ff;
  color: #2c5aa0;
}

.desc-gst {
  background: #faf5ff;
  color: #6b46c1;
}

.desc-transfer {
  background: #fffaf0;
  color: #dd6b20;
}

.desc-default {
  background: #f7fafc;
  color: #4a5568;
}

.amount-cell {
  text-align: right;
  font-family: 'Monaco', 'Consolas', monospace;
}

.amount {
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.875rem;
}

.amount.debit {
  background: #fff5f5;
  color: #e53e3e;
}

.amount.credit {
  background: #f0fff4;
  color: #38a169;
}

.amount.balance {
  background: #ebf8ff;
  color: #3182ce;
  font-weight: 700;
}

.amount.zero {
  color: #a0aec0;
  background: transparent;
}

@media (max-width: 768px) {
  .description {
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .ref-badge, .desc-text, .amount {
    font-size: 0.75rem;
    padding: 2px 6px;
  }
}
</style>