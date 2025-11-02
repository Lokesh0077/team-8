// A formatter for currency, specifically for Indian Rupees (INR)
const currencyFormatter = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
});

export const formatCurrency = (amount) => {
  if (typeof amount !== 'number') return '';
  return currencyFormatter.format(amount);
};


export const formatDateTime = (dateTime) => {
  if (!dateTime) return 'N/A'
  const date = new Date(dateTime)
  return new Intl.DateTimeFormat('en-IN', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  }).format(date)
}

export const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Intl.DateTimeFormat('en-IN', {
    year: 'numeric',
    month: 'short',
    day: '2-digit'
  }).format(new Date(date))
}

export const formatAccountNumber = (accountNumber) => {
  if (!accountNumber) return ''
  return accountNumber.replace(/(\d{4})(?=\d)/g, '$1 ')
}

export const getTransactionType = (transaction) => {
  if (transaction.withdrawals > 0) return 'debit'
  if (transaction.credit > 0) return 'credit'
  return 'neutral'
}