export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return 'N/A'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount)
}

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