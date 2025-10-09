import { createRouter, createWebHistory } from 'vue-router';
import Statements from '@/views/Statements.vue';

const routes = [
  { path: '/', redirect: '/statements' },
  { path: '/statements', component: Statements }
];

export const router = createRouter({
  history: createWebHistory(),
  routes
});