<template>
  <div class="pagination-container bg-white rounded-lg shadow-md p-6 mt-6">
    <div class="pagination-info flex flex-col md:flex-row items-center justify-between mb-4 pb-4 border-b border-gray-200 gap-4">
      <div class="text-sm text-gray-600">
        <span v-if="totalItems > 0">
          Showing <strong>{{ startItem }}</strong> to <strong>{{ endItem }}</strong> of <strong>{{ totalItems }}</strong> transactions
        </span>
        <span v-else class="text-gray-500">
          No transactions to display
        </span>
      </div>

      <div class="page-size-selector flex items-center gap-3">
        <label for="pageSize" class="text-sm font-medium text-gray-700">Show:</label>
        <select
          id="pageSize"
          v-model="pageSize" 
          @change="handlePageSizeChange"
          class="size-select px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          :disabled="disabled || totalItems === 0"
        >
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
          <option :value="100">100</option>
        </select>
        <span class="text-sm text-gray-700">per page</span>
      </div>
    </div>

    <div v-if="totalPages > 1" class="pagination-controls flex items-center justify-center gap-2">
      <button
        @click="goToPage(1)"
        :disabled="currentPage === 1 || disabled"
        class="pagination-btn"
        title="First Page"
      >
        ⟨⟨
      </button>

      <button
        @click="goToPage(currentPage - 1)"
        :disabled="currentPage === 1 || disabled"
        class="pagination-btn"
        title="Previous Page"
      >
        ⟨
      </button>

      <div class="page-numbers flex items-center gap-1">
        <button
          v-if="visiblePages[0] > 1"
          @click="goToPage(1)"
          class="page-number"
          :disabled="disabled"
        >
          1
        </button>

        <span v-if="visiblePages[0] > 2" class="ellipsis">...</span>

        <button
          v-for="page in visiblePages"
          :key="page"
          @click="goToPage(page)"
          class="page-number"
          :class="{ 'active': page === currentPage }"
          :disabled="disabled"
        >
          {{ page }}
        </button>

        <span v-if="visiblePages[visiblePages.length - 1] < totalPages - 1" class="ellipsis">...</span>

        <button
          v-if="visiblePages[visiblePages.length - 1] < totalPages"
          @click="goToPage(totalPages)"
          class="page-number"
          :disabled="disabled"
        >
          {{ totalPages }}
        </button>
      </div>

      <button
        @click="goToPage(currentPage + 1)"
        :disabled="currentPage === totalPages || disabled"
        class="pagination-btn"
        title="Next Page"
      >
        ⟩
      </button>

      <button
        @click="goToPage(totalPages)"
        :disabled="currentPage === totalPages || disabled"
        class="pagination-btn"
        title="Last Page"
      >
        ⟩⟩
      </button>
    </div>

    <div v-else-if="totalItems > 0" class="text-center text-sm text-gray-500 py-4">
      All {{ totalItems }} results fit on a single page
    </div>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue';


export default defineComponent({
  name: 'Pagination',
  props: {
    currentPage: {
      type: Number,
      required: true,
      validator: (value) => value >= 1
    },
    totalPages: {
      type: Number,
      required: true,
      validator: (value) => value >= 0
    },
    totalItems: {
      type: Number,
      required: true,
      validator: (value) => value >= 0
    },
    pageSize: {
      type: Number,
      default: 20,
      validator: (value) => [10, 20, 50, 100].includes(value)
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['page-change', 'page-size-change'],
  setup(props, { emit }) {

    // Calculate start and end items
    const startItem = computed(() => {
      if (props.totalItems === 0) return 0;
      return (props.currentPage - 1) * props.pageSize + 1;
    });

    const endItem = computed(() => {
      const end = props.currentPage * props.pageSize;
      return Math.min(end, props.totalItems);
    });

    // Calculate visible page numbers with smart pagination
    const visiblePages = computed(() => {
      const pages = [];
      const maxVisible = 5; // Max buttons to show (e.g., 3, 4, *5*, 6, 7)
      
      if (props.totalPages <= maxVisible) {
        // If total pages is small, show all
        for (let i = 1; i <= props.totalPages; i++) {
          pages.push(i);
        }
      } else {
        // If total pages is large, calculate smart range
        let start = Math.max(1, props.currentPage - 2);
        let end = Math.min(props.totalPages, props.currentPage + 2);

        // Adjust range if near the edges
        if (props.currentPage <= 3) {
          end = maxVisible;
        } else if (props.currentPage >= props.totalPages - 2) {
          start = props.totalPages - maxVisible + 1;
        }
        
        for (let i = start; i <= end; i++) {
          pages.push(i);
        }
      }
      return pages;
    });

    const goToPage = (page) => {
      if (page < 1 || page > props.totalPages || page === props.currentPage || props.disabled) {
        return;
      }
      emit('page-change', page);
    };

    const handlePageSizeChange = (event) => {
      const newPageSize = parseInt(event.target.value);
      if (newPageSize !== props.pageSize) {
        emit('page-size-change', newPageSize);
      }
    };

    return {
      startItem,
      endItem,
      visiblePages,
      goToPage,
      handlePageSizeChange
    };
  }
});
</script>
