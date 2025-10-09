// Mock data - using the exact data from the business case
const mockTransactions = [
  {
    jxnRefNumber: 'BNK0001',
    accountNumber: '00770989423',
    dateTime: '2020-12-01T01:01:45',
    description: 'POS Transaction',
    withdrawals: 2000,
    credit: null,
    runningBalance: 8000
  },
  {
    jxnRefNumber: 'BNK0002',
    accountNumber: '00770989423',
    dateTime: '2020-12-02T01:01:45',
    description: 'Salary Payment',
    withdrawals: null,
    credit: 10000,
    runningBalance: 18000
  },
  {
    jxnRefNumber: 'BNK0003',
    accountNumber: '00770989423',
    dateTime: '2020-12-02T02:01:45',
    description: 'Bank Interest Credit',
    withdrawals: null,
    credit: 25,
    runningBalance: 18025
  },
  {
    jxnRefNumber: 'BNK0004',
    accountNumber: '00770989423',
    dateTime: '2020-12-03T02:01:45',
    description: 'GST Deduction',
    withdrawals: 230,
    credit: null,
    runningBalance: 17995
  },
  {
    jxnRefNumber: 'BNK0005',
    accountNumber: '00770989423',
    dateTime: '2020-12-04T02:01:45',
    description: 'POS Transaction',
    withdrawals: 1500,
    credit: null,
    runningBalance: 16295
  }
]

export const searchService = {
  async searchTransactions(filters = {}) {
    // Simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        let filtered = [...mockTransactions]
        
        // Filter by account number
        if (filters.accountNumber) {
          filtered = filtered.filter(t => 
            t.accountNumber === filters.accountNumber
          )
        }
        
        // Filter by date range
        if (filters.dateFrom && filters.dateTo) {
          filtered = filtered.filter(t => {
            const transactionDate = new Date(t.dateTime)
            return transactionDate >= new Date(filters.dateFrom) &&
                   transactionDate <= new Date(filters.dateTo)
          })
        }
        
        // Filter by description
        if (filters.description) {
          filtered = filtered.filter(t =>
            t.description.toLowerCase().includes(filters.description.toLowerCase())
          )
        }
        
        // Filter by amount range
        if (filters.minAmount !== null && filters.minAmount !== undefined) {
          filtered = filtered.filter(t => {
            const amount = t.withdrawals || t.credit || 0
            return amount >= filters.minAmount
          })
        }
        
        if (filters.maxAmount !== null && filters.maxAmount !== undefined) {
          filtered = filtered.filter(t => {
            const amount = t.withdrawals || t.credit || 0
            return amount <= filters.maxAmount
          })
        }
        
        // Filter by transaction type
        if (filters.type && filters.type !== 'all') {
          filtered = filtered.filter(t => {
            if (filters.type === 'debit') return t.withdrawals > 0
            if (filters.type === 'credit') return t.credit > 0
            return true
          })
        }
        
        resolve({
          transactions: filtered,
          totalCount: filtered.length
        })
      }, 500)
    })
  },

  async getAccounts() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve([
          {
            number: '00770989423',
            name: 'Primary Savings Account',
            balance: 16295,
            currency: 'INR'
          },
          {
            number: '001003457803',
            name: 'Current Account',
            balance: 25000,
            currency: 'INR'
          }
        ])
      }, 200)
    })
  }
}

export default searchService