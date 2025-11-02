<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
    <!-- Header -->
    <div class="px-6 py-4 border-b border-gray-200">
      <h2 class="text-xl font-semibold text-gray-800">User Management</h2>
    </div>

    <!-- Loading State -->
    <div v-if="adminStore.isLoading" class="p-12 text-center text-gray-500">
      <div class="animate-spin inline-block mb-3">
        <span class="text-4xl text-indigo-600">⟳</span>
      </div>
      <p>Loading users...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="adminStore.error" class="p-12 text-center text-red-600">
      <p class="font-medium">Error loading users:</p>
      <p>{{ adminStore.error }}</p>
    </div>

    <!-- Table Content -->
    <div v-else-if="adminStore.users.length > 0" class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="th-cell">User ID</th>
            <th class="th-cell">Username</th>
            <th class="th-cell">Email</th>
            <th class="th-cell">Status</th>
            <th class="th-cell">Last Updated</th>
            <th class="th-cell">Roles</th>
            <th class="th-cell text-right">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in adminStore.users" :key="user.id" class="hover:bg-gray-50">
            <td class="td-cell font-medium text-gray-900">{{ user.id }}</td>
            <td class="td-cell">{{ user.username }}</td>
            <td class="td-cell">{{ user.email }}</td>
            <td class="td-cell">
              <span 
                class="px-2 py-0.5 rounded-full text-xs font-medium"
                :class="user.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ user.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="td-cell">{{ formatDate(user.updatedAt) }}</td>
            <td class="td-cell">{{ formatRoles(user.roles) }}</td>
            <td class="td-cell text-right">
              <button 
                @click="toggleStatus(user)"
                class="px-3 py-1 rounded-md text-sm font-medium transition-colors"
                :class="user.isActive ? 'bg-red-100 text-red-700 hover:bg-red-200' : 'bg-green-100 text-green-700 hover:bg-green-200'"
              >
                {{ user.isActive ? 'Deactivate' : 'Activate' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Empty State -->
    <div v-else class="p-12 text-center text-gray-500">
      <p>No users found in the system.</p>
    </div>

    <!-- Pagination -->
    <div v-if="adminStore.userPage.totalPages > 1" class="border-t border-gray-200 bg-white px-4 py-3">
      <Pagination
        :current-page="adminStore.userPage.number + 1"
        :total-pages="adminStore.userPage.totalPages"
        :total-items="adminStore.userPage.totalElements"
        :page-size="adminStore.userPage.size"
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
const toggleStatus = (user) => {
  if (confirm(`Are you sure you want to ${user.isActive ? 'deactivate' : 'activate'} this user?`)) {
    adminStore.updateUserStatus(user.id, !user.isActive);
  }
};

const handlePageChange = (newPage) => {
  adminStore.fetchUsers(newPage - 1, adminStore.userPage.size);
};

// --- Helpers ---
const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
};

const formatRoles = (roles) => {
  if (!roles || roles.length === 0) return 'N/A';
  return roles.map(role => role.name.replace('ROLE_', '')).join(', ');
};

// --- Lifecycle ---
onMounted(() => {
  adminStore.fetchUsers(); // Fetch users when the component is loaded
});
</script>

<style scoped>
.th-cell {
  padding-left: 1.5rem;
  padding-right: 1.5rem;
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: #6B7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.td-cell {
  padding-left: 1.5rem;
  padding-right: 1.5rem;
  padding-top: 1rem;
  padding-bottom: 1rem;
  white-space: nowrap;
  font-size: 0.875rem;
  color: #374151;
}
</style>