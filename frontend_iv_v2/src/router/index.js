import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'  // Changed from DashboardView to Dashboard

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard  // Make sure this matches the import name
    }
    // Other routes can be added by teammates later
  ]
})

export default router