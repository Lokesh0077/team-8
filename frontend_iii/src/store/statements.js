// src/store/statements.js

import searchService from '@/services/searchService'

const state = {
  transactions: [],
  filteredTransactions: [],
  currentAccount: null,
  searchFilters: {
    accountNumber: '',
    dateFrom: '',
    dateTo: '',
    description: '',
    minAmount: null,
    maxAmount: null,
    type: 'all'
  },
  pagination: {
    currentPage: 1,
    pageSize: 10,
    totalItems: 0,
    totalPages: 0
  },
  sort: {
    field: 'dateTime',
    order: 'desc'
  },
  isLoading: false,
  error: null,
  stats: {
    totalDebits: 0,
    totalCredits: 0,
    netFlow: 0,
    transactionCount: 0
  }
}

const getters = {
  getTransactions: (state) => state.transactions,
  getFilteredTransactions: (state) => state.filteredTransactions,
  getSearchFilters: (state) => state.searchFilters,
  getPagination: (state) => state.pagination,
  getSort: (state) => state.sort,
  isLoading: (state) => state.isLoading,
  getError: (state) => state.error,
  getStats: (state) => state.stats,
  getCurrentAccount: (state) => state.currentAccount,
  
  // Computed paginated data
  getPaginatedTransactions: (state) => {
    const start = (state.pagination.currentPage - 1) * state.pagination.pageSize
    const end = start + state.pagination.pageSize
    return state.filteredTransactions.slice(start, end)
  },
  
  // Search summary
  getSearchSummary: (state) => {
    return {
      totalResults: state.filteredTransactions.length,
      showing: Math.min(state.pagination.pageSize, state.filteredTransactions.length),
      page: state.pagination.currentPage,
      totalPages: state.pagination.totalPages
    }
  }
}

const mutations = {
  SET_TRANSACTIONS(state, transactions) {
    state.transactions = transactions
    state.filteredTransactions = transactions
    state.pagination.totalItems = transactions.length
    state.pagination.totalPages = Math.ceil(transactions.length / state.pagination.pageSize)
  },
  
  SET_FILTERED_TRANSACTIONS(state, transactions) {
    state.filteredTransactions = transactions
    state.pagination.totalItems = transactions.length
    state.pagination.totalPages = Math.ceil(transactions.length / state.pagination.pageSize)
    state.pagination.currentPage = 1
  },
  
  SET_SEARCH_FILTERS(state, filters) {
    state.searchFilters = { ...state.searchFilters, ...filters }
  },
  
  SET_PAGINATION(state, pagination) {
    state.pagination = { ...state.pagination, ...pagination }
  },
  
  SET_SORT(state, sort) {
    state.sort = { ...state.sort, ...sort }
  },
  
  SET_LOADING(state, isLoading) {
    state.isLoading = isLoading
  },
  
  SET_ERROR(state, error) {
    state.error = error
  },
  
  SET_STATS(state, stats) {
    state.stats = stats
  },
  
  SET_CURRENT_ACCOUNT(state, account) {
    state.currentAccount = account
  },
  
  CLEAR_ERROR(state) {
    state.error = null
  },
  
  RESET_SEARCH(state) {
    state.searchFilters = {
      accountNumber: '',
      dateFrom: '',
      dateTo: '',
      description: '',
      minAmount: null,
      maxAmount: null,
      type: 'all'
    }
    state.filteredTransactions = state.transactions
    state.pagination.currentPage = 1
  }
}

const actions = {
  async fetchTransactions({ commit }, accountNumber) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    
    try {
      const filters = accountNumber ? { accountNumber } : {}
      const response = await searchService.searchTransactions(filters)
      commit('SET_TRANSACTIONS', response.transactions)
      commit('SET_CURRENT_ACCOUNT', accountNumber || null)
      
      // Calculate stats
      const stats = calculateTransactionStats(response.transactions)
      commit('SET_STATS', stats)
      
    } catch (error) {
      commit('SET_ERROR', error.message)
      console.error('Failed to fetch transactions:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  },
  
  async searchTransactions({ commit, state }, newFilters) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    
    try {
      // Update filters
      commit('SET_SEARCH_FILTERS', newFilters)
      
      // Validate filters
      const errors = searchService.validateSearchParams(state.searchFilters)
      if (errors.length > 0) {
        throw new Error(errors.join(', '))
      }
      
      // Perform search
      const response = await searchService.searchTransactions(state.searchFilters)
      commit('SET_FILTERED_TRANSACTIONS', response.transactions)
      
      // Update stats for filtered results
      const stats = calculateTransactionStats(response.transactions)
      commit('SET_STATS', stats)
      
    } catch (error) {
      commit('SET_ERROR', error.message)
      console.error('Search failed:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  },
  
  updatePagination({ commit }, pagination) {
    commit('SET_PAGINATION', pagination)
  },
  
  updateSort({ commit, state }, { field, order }) {
    commit('SET_SORT', { field, order })
    
    // Apply sorting to filtered transactions
    const sorted = [...state.filteredTransactions].sort((a, b) => {
      let aValue = a[field]
      let bValue = b[field]
      
      if (field === 'dateTime') {
        aValue = new Date(aValue)
        bValue = new Date(bValue)
      }
      
      if (aValue < bValue) return order === 'asc' ? -1 : 1
      if (aValue > bValue) return order === 'asc' ? 1 : -1
      return 0
    })
    
    commit('SET_FILTERED_TRANSACTIONS', sorted)
  },
  
  resetSearch({ commit }) {
    commit('RESET_SEARCH')
  },
  
  clearError({ commit }) {
    commit('CLEAR_ERROR')
  }
}

// Helper function to calculate transaction statistics
function calculateTransactionStats(transactions) {
  const stats = {
    totalDebits: 0,
    totalCredits: 0,
    netFlow: 0,
    transactionCount: transactions.length
  }
  
  transactions.forEach(transaction => {
    if (transaction.withdrawals > 0) {
      stats.totalDebits += transaction.withdrawals
    }
    if (transaction.credit > 0) {
      stats.totalCredits += transaction.credit
    }
  })
  
  stats.netFlow = stats.totalCredits - stats.totalDebits
  
  return stats
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}