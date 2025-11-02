<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
    <!-- Header -->
    <div class="px-6 py-4 border-b border-gray-200">
      <h2 class="text-xl font-semibold text-gray-800">System Audit Logs</h2>
    </div>

    <!-- Loading State -->
    <div v-if="adminStore.isLoading" class="p-12 text-center text-gray-500">
      <div class="animate-spin inline-block mb-3">
        <span class="text-4xl text-indigo-600">⟳</span>
      </div>
      <p>Loading logs...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="adminStore.error" class="p-12 text-center text-red-600">
      <p class="font-medium">Error loading logs:</p>
      <p>{{ adminStore.error }}</p>
    </div>

    <!-- Table Content -->
    <div v-else-if="adminStore.auditLogs.length > 0" class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="th-cell">Timestamp</th>
            <th class="th-cell">User</th>
            <th class="th-cell">Action</th>
            <th class="th-cell">Details</th>
            <th class="th-cell">IP Address</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="log in adminStore.auditLogs" :key="log.id" class="hover:bg-gray-50">
            <td class="td-cell">
              <span class="font-medium text-gray-900">{{ formatDate(log.timestamp) }}</span>
              <span class="block text-xs text-gray-500">{{ formatTime(log.timestamp) }}</span>
            </td>
            <td class="td-cell">{{ log.user ? log.user.username : 'SYSTEM' }}</td>
            <td class="td-cell">
              <span class="font-medium px-2 py-0.5 rounded-full text-xs" :class="getActionClass(log.action)">
                {{ log.action }}
              </span>
            </td>
            <td class="td-cell max-w-sm truncate" :title="log.details">{{ log.details }}</td>
            <td class="td-cell font-mono text-xs">{{ log.ipAddress }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Empty State -->
    <div v-else class="p-12 text-center text-gray-500">
      <p>No audit logs found.</p>
    </div>

    <!-- Pagination -->
    <div v-if="adminStore.logPage.totalPages > 1" class="border-t border-gray-200 bg-white px-4 py-3">
      <Pagination
        :current-page="adminStore.logPage.number + 1"
        :total-pages="adminStore.logPage.totalPages"
        :total-items="adminStore.logPage.totalElements"
        :page-size="adminStore.logPage.size"
        @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useAdminStore } from '@/store/admin';
import Pagination from '@/components/statements/Pagination.vue'; // Reusing your pagination component

const adminStore = useAdminStore();

// --- Methods ---
const handlePageChange = (newPage) => {
  adminStore.fetchAuditLogs(newPage - 1, adminStore.logPage.size);
};

// --- Helpers ---
const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
};

const formatTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
};

const getActionClass = (action) => {
  if (!action) return 'bg-gray-100 text-gray-800';
  switch (action.toUpperCase()) {
    case 'LOGIN_SUCCESS':
      return 'bg-green-100 text-green-800';
    case 'LOGIN_FAILURE':
    case 'PASSWORD_CHANGE_FAILURE':
    case 'FILE_UPLOAD_FAILED':
      return 'bg-red-100 text-red-800';
    case 'FILE_UPLOAD_SUCCESS':
    case 'PASSWORD_CHANGE_SUCCESS':
      return 'bg-blue-100 text-blue-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

// --- Lifecycle ---
onMounted(() => {
  adminStore.fetchAuditLogs(); // Fetch logs when the component is loaded
});
</script>

<style scoped>
.th-cell {
  padding: 0.75rem 1.5rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280; /* approximates Tailwind's gray-500 */
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.td-cell {
  padding: 1rem 1.5rem;
  white-space: nowrap;
  font-size: 0.875rem;
  color: #374151; /* approximates Tailwind's gray-700 */
}
.max-w-sm {
  max-width: 24rem; /* For truncating long details */
}
</style>