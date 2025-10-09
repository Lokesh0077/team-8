<template>
  <div class="pagination">
    <div class="pagination-info">
      Showing {{ startItem }}-{{ endItem }} of {{ totalItems }} transactions
    </div>
    
    <div class="pagination-controls">
      <button
        @click="goToPage(1)"
        :disabled="currentPage === 1"
        class="pagination-btn first"
        title="First Page"
      >
        ««
      </button>
      
      <button
        @click="goToPage(currentPage - 1)"
        :disabled="currentPage === 1"
        class="pagination-btn prev"
        title="Previous Page"
      >
        «
      </button>

      <div class="page-numbers">
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="goToPage(page)"
          class="page-number"
          :class="{
            active: page === currentPage,
            ellipsis: page === '...'
          }"
          :disabled="page === '...'"
        >
          {{ page }}
        </button>
      </div>

      <button
        @click="goToPage(currentPage + 1)"
        :disabled="currentPage === totalPages"
        class="pagination-btn next"
        title="Next Page"
      >
        »
      </button>
      
      <button
        @click="goToPage(totalPages)"
        :disabled="currentPage === totalPages"
        class="pagination-btn last"
        title="Last Page"
      >
        »»
      </button>
    </div>

    <div class="page-size-selector">
      <label for="pageSize">Show:</label>
      <select
        id="pageSize"
        v-model="localPageSize"
        @change="handlePageSizeChange"
        class="size-select"
      >
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="25">25</option>
      </select>
      <span>per page</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    },
    pageSize: {
      type: Number,
      default: 10
    }
  },
  data() {
    return {
      localPageSize: this.pageSize
    }
  },
  computed: {
    startItem() {
      return ((this.currentPage - 1) * this.localPageSize) + 1
    },
    endItem() {
      const end = this.currentPage * this.localPageSize
      return end > this.totalItems ? this.totalItems : end
    },
    visiblePages() {
      const pages = []
      const delta = 1
      const range = {
        start: Math.max(1, this.currentPage - delta),
        end: Math.min(this.totalPages, this.currentPage + delta)
      }

      if (range.start > 1) {
        pages.push(1)
        if (range.start > 2) {
          pages.push('...')
        }
      }

      for (let i = range.start; i <= range.end; i++) {
        pages.push(i)
      }

      if (range.end < this.totalPages) {
        if (range.end < this.totalPages - 1) {
          pages.push('...')
        }
        pages.push(this.totalPages)
      }

      return pages
    }
  },
  methods: {
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
        this.$emit('page-change', page)
      }
    },
    handlePageSizeChange() {
      this.$emit('page-size-change', parseInt(this.localPageSize))
      this.goToPage(1)
    }
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-top: 1px solid #e2e8f0;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-info {
  color: #718096;
  font-size: 0.875rem;
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-btn,
.page-number {
  padding: 8px 12px;
  border: 1px solid #cbd5e0;
  background: white;
  color: #4a5568;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 4px;
  min-width: 40px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-btn:hover:not(:disabled),
.page-number:hover:not(.ellipsis):not(.active) {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.pagination-btn:disabled {
  background: #f7fafc;
  border-color: #e2e8f0;
  color: #a0aec0;
  cursor: not-allowed;
}

.page-number.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.page-number.ellipsis {
  background: none;
  border: none;
  cursor: default;
  min-width: auto;
}

.page-number.ellipsis:hover {
  background: none;
  color: #4a5568;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 0.875rem;
}

.size-select {
  padding: 6px 8px;
  border: 1px solid #cbd5e0;
  border-radius: 4px;
  background: white;
  font-size: 0.875rem;
  color: #4a5568;
  cursor: pointer;
}

.size-select:focus {
  outline: none;
  border-color: #667eea;
}

@media (max-width: 768px) {
  .pagination {
    flex-direction: column;
    text-align: center;
  }
  
  .pagination-controls {
    order: -1;
  }
  
  .page-numbers {
    display: none;
  }
}
</style>