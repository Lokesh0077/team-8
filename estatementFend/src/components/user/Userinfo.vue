<template>
  <DashboardLayout>
    <div class="max-w-5xl mx-auto space-y-6">
      
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-slate-900">My Profile</h1>
        <p class="text-slate-600 mt-2">View your account details.</p>
      </div>

      <div v-if="isLoading" class="text-center p-12">
        <div class="animate-spin inline-block mb-3">
          <span class="text-4xl text-indigo-600">⟳</span>
        </div>
        <p class="text-gray-600 font-medium">Loading profile...</p>
      </div>
      
      <div v-else-if="error" class="p-4 bg-red-50 text-red-800 border border-red-200 rounded-lg">
        <p class="font-medium">Error loading profile</p>
        <p class="text-sm">{{ error }}</p>
      </div>

      <div v-else-if="profile" class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        
        <div class="p-6 space-y-4">
          <div class="flex items-center space-x-4">
            <span class="flex-shrink-0 h-16 w-16 rounded-full bg-slate-700 text-white text-3xl font-medium flex items-center justify-center">
              {{ initials }}
            </span>
            <div>
              <h2 class="text-2xl font-bold text-slate-900">{{ profile.name }}</h2>
              <p class="text-slate-600">{{ profile.email }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-gray-50 px-6 py-4 border-t border-gray-200">
          <h3 class="font-semibold text-slate-800 mb-3">Account Details</h3>
          <dl class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4 text-sm">
            <div class="col-span-1">
              <dt class="text-gray-500">User ID</dt>
              <dd class="text-gray-900 font-medium">{{ profile.id }}</dd>
            </div>
            <div class="col-span-1">
              <dt class="text-gray-500">Username</dt>
              <dd class="text-gray-900 font-medium">{{ profile.name }}</dd>
            </div>
            <div class="col-span-1">
              <dt class="text-gray-500">Email Address</dt>
              <dd class="text-gray-900 font-medium">{{ profile.email }}</dd>
            </div>
             <div class="col-span-1">
              <dt class="text-gray-500">Last Updated</dt>
              <dd class="text-gray-900 font-medium">{{ formatDate(profile.updatedAt) }}</dd>
            </div>
          </dl>
        </div>
      </div>

    </div>
  </DashboardLayout>
</template>

<script>
import { mapState, mapActions } from 'pinia';
import { useUserStore } from '@/store/user.js';
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue';

export default {
  components: {
    DashboardLayout
  },

  computed: {
    // 4. Map ALL the state properties you need
    ...mapState(useUserStore, ['profile', 'isLoading', 'error']),

    initials() {
      // 'this.profile' is available because of mapState
      if (!this.profile || !this.profile.name) return '??';
      return this.profile.name[0].toUpperCase();
    }
  },

  methods: {
    ...mapActions(useUserStore, ['fetchProfile']),

    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  },

  mounted() {
    // 'this.fetchProfile()' is available because of mapActions
    this.fetchProfile();
  }
}
</script>