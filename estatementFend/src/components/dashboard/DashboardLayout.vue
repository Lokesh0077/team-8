<template>
  <div class="flex h-screen bg-gray-100 overflow-hidden">
    
    <Sidebar :isOpen="isSidebarOpen" @toggle-sidebar="toggleSidebar" />

    <div class="flex-1 flex flex-col">
      
      <DashboardHeader />
      
      <main class="flex-1 overflow-x-hidden overflow-y-auto p-4 sm:p-8">
        <slot /> 
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import DashboardHeader from '../common/DashboardHeader.vue';
import Sidebar from '../common/Sidebar.vue';

// This logic is unchanged and correct
const isSidebarOpen = ref(true);

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};
</script>