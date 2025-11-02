<template>
  <tr class="transaction-row hover:bg-gray-50 transition-colors" :class="rowClass">
    <!-- Transaction Reference Number -->
    <td class="ref-number px-4 py-3 text-sm font-mono">
      <span class="ref-badge inline-block px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-semibold">
        {{ transaction.txnRefNumber || 'N/A' }}
      </span>
    </td>

    <!-- Date & Time -->
    <td class="date-time px-4 py-3 text-sm text-gray-700">
      {{ formatDateTime(transaction) }}
    </td>

    <!-- Withdrawals (Debits) -->
    <td class="withdrawals amount-cell px-4 py-3 text-right text-sm font-semibold">
      <span 
        v-if="hasWithdrawals" 
        class="amount debit text-red-600 font-bold"
        :title="`Withdrawal: ${transaction.formattedWithdrawals || formatCurrency(transaction.withdrawals)}`"
      >
        -{{ transaction.formattedWithdrawals || formatCurrency(transaction.withdrawals) }}
      </span>
      <span v-else class="amount zero text-gray-400">
        —
      </span>
    </td>

    <!-- Credit -->
    <td class="credit amount-cell px-4 py-3 text-right text-sm font-semibold">
      <span 
        v-if="hasCredit" 
        class="amount credit text-green-600 font-bold"
        :title="`Credit: ${transaction.formattedCredit || formatCurrency(transaction.credit)}`"
      >
        +{{ transaction.formattedCredit || formatCurrency(transaction.credit) }}
      </span>
      <span v-else class="amount zero text-gray-400">
        —
      </span>
    </td>

    <!-- Running Balance -->
    <td class="balance amount-cell px-4 py-3 text-right text-sm font-semibold">
      <span 
        class="amount balance"
        :class="getBalanceClass"
        :title="`Balance: ${transaction.formattedBalance || formatCurrency(transaction.runningBalance)}`"
      >
        {{ transaction.formattedBalance || formatCurrency(transaction.runningBalance) || '0.00' }}
      </span>
    </td>

    <!-- Description -->
    <td class="description px-4 py-3 text-sm text-gray-700">
      <span
        class="desc-text inline-block max-w-xs truncate"
        :class="descriptionClass"
        :title="transaction.description || 'No description'"
      >
        {{ transaction.description || 'N/A' }}
      </span>
    </td>
  </tr>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'TransactionRow',
  props: {
    transaction: {
      type: Object,
      required: true,
      validator: (obj) => {
        return obj.id || obj.txnRefNumber;
      }
    }
  },
  setup(props) {
    // Check if transaction has withdrawals
    const hasWithdrawals = () => {
      const amount = parseFloat(props.transaction.withdrawals) || 0;
      return amount > 0;
    };

    // Check if transaction has credit
    const hasCredit = () => {
      const amount = parseFloat(props.transaction.credit) || 0;
      return amount > 0;
    };

    // Determine row class based on transaction type
    const rowClass = () => {
      return {
        'debit-row border-l-4 border-red-400': hasWithdrawals(),
        'credit-row border-l-4 border-green-400': hasCredit(),
        'neutral-row': !hasWithdrawals() && !hasCredit()
      };
    };

    // Get balance class (positive/negative)
    const getBalanceClass = () => {
      const balance = parseFloat(props.transaction.runningBalance) || 0;
      return {
        'text-green-600': balance >= 0,
        'text-red-600': balance < 0
      };
    };

    // Format currency - use backend formatted value if available
    const formatCurrency = (amount) => {
      if (amount === null || amount === undefined) return '0.00';
      
      const num = parseFloat(amount);
      if (isNaN(num)) return '0.00';
      
      return new Intl.NumberFormat('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(num);
    };

    // Format date and time
    const formatDateTime = (transaction) => {
      // Use backend formatted date if available
      if (transaction.formattedDate) {
        return transaction.formattedDate;
      }

      // Fallback: parse dateTime
      if (!transaction.dateTime) return 'N/A';

      try {
        const date = new Date(transaction.dateTime);
        if (isNaN(date.getTime())) return 'N/A';

        return new Intl.DateTimeFormat('en-IN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: true
        }).format(date);
      } catch (error) {
        console.error('Date formatting error:', error);
        return 'N/A';
      }
    };

    // Determine description style based on keywords
    const descriptionClass = () => {
      const desc = (props.transaction.description || '').toLowerCase();
      
      if (desc.includes('pos')) return 'desc-pos bg-orange-50 text-orange-700';
      if (desc.includes('salary')) return 'desc-salary bg-green-50 text-green-700';
      if (desc.includes('interest')) return 'desc-interest bg-blue-50 text-blue-700';
      if (desc.includes('gst')) return 'desc-gst bg-purple-50 text-purple-700';
      if (desc.includes('transfer')) return 'desc-transfer bg-indigo-50 text-indigo-700';
      if (desc.includes('bank')) return 'desc-bank bg-gray-50 text-gray-700';
      if (desc.includes('charge')) return 'desc-charge bg-red-50 text-red-700';
      if (desc.includes('payment')) return 'desc-payment bg-green-50 text-green-700';
      
      return 'desc-default bg-gray-50 text-gray-700';
    };

    return {
      hasWithdrawals: hasWithdrawals(),
      hasCredit: hasCredit(),
      rowClass: rowClass(),
      getBalanceClass,
      formatCurrency,
      formatDateTime,
      descriptionClass: descriptionClass()
    };
  }
});
</script>