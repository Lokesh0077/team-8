<template>
  <aside 
    :class="[isOpen ? 'w-64 sidebar-open' : 'w-20 sidebar-closed']" 
    class="flex-shrink-0 bg-slate-800 text-white h-screen flex flex-col shadow-lg transition-all duration-300 ease-in-out"
  >
    <div class="p-5 border-b border-slate-700 h-16 flex items-center gap-4 overflow-hidden">
      <button 
        @click="emitToggle" 
        class="p-1 rounded-md text-slate-300 hover:bg-slate-700 hover:text-white focus:outline-none"
        title="Toggle menu"
      >
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </div>
    
    <nav class="flex-grow p-4 space-y-2">

      <router-link to="/dashboard" class="nav-link" :class="!isOpen && 'justify-center'">
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
        <span v-if="isOpen" class="nav-text">Dashboard</span>
      </router-link>
      <router-link to="/upload" class="nav-link" :class="!isOpen && 'justify-center'">
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path></svg>
        <span v-if="isOpen" class="nav-text">Upload File</span>
      </router-link>

      <router-link to="/statements" class="nav-link" :class="!isOpen && 'justify-center'">
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
        <span v-if="isOpen" class="nav-text">Statements</span>
      </router-link>

      <router-link to="/user" class="nav-link" :class="!isOpen && 'justify-center'">
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
        <span v-if="isOpen" class="nav-text">User Profile</span>
      </router-link>

      <router-link to="/password" class="nav-link" :class="!isOpen && 'justify-center'">
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
        </svg>
        <span v-if="isOpen" class="nav-text">Change Password</span>
      </router-link>

      <!-- NEW ADMIN LINK -->
      <router-link 
        v-if="authStore.isAdmin"
        to="/admin" 
        class="nav-link" 
        :class="!isOpen && 'justify-center'"
      >
        <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
        </svg>
        <span v-if="isOpen" class="nav-text">Admin Panel</span>
      </router-link>
      
    </nav>

    <div class="p-4 border-t border-slate-700">
      <div :class="['flex items-center', isOpen ? 'justify-start' : 'justify-center']">
        <LogoutButton class="w-full" />
      </div>
    </div>
  </aside>
</template>

<script setup>
// Corrected the path from 'common' to 'auth'
import LogoutButton from '@/components/common/LogoutButton.vue'; 
import { useAuthStore } from '@/store/auth.js'; // Import auth store

const authStore = useAuthStore(); // Initialize store

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['toggle-sidebar']);

const emitToggle = () => {
  emit('toggle-sidebar');
};
</script>

<style scoped>
.nav-link {
  display: flex;
  align-items: center;
  gap: 0.75rem; /* 12px */
  padding: 0.75rem 1rem; /* 12px 16px */
  border-radius: 8px;
  font-weight: 500;
  transition: background-color 0.2s, color 0.2s;
  color: #cbd5e1; /* slate-300 */
  overflow: hidden; /* Prevent text from wrapping */
}
.nav-text {
  white-space: nowrap; /* Keep text on one line */
}
.nav-link:hover {
  background-color: #334155; /* slate-700 */
  color: white;
}
.router-link-exact-active {
  background-color: #4f46e5; /* indigo-600 */
  color: white;
}

.sidebar-open :deep(button) {
  width: 100%;
  color: #cbd5e1; /* slate-300 */
  padding-left: 0.75rem;
  padding-right: 0.75rem;
}
.sidebar-closed :deep(button) {
  width: 40px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  color: #cbd5e1;
  padding-left: 0;
  padding-right: 0;
}
:deep(button:hover) {
  background-color: #334155; /* slate-700 */
  color: #f87171; /* red-400 */
}
.sidebar-closed :deep(button span) {
  display: none;
}
</style>