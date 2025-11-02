import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import Dashboard from '@/views/Dashboard.vue';
import LoginPage from '@/views/Login.vue';
import Statements from '@/views/Statements.vue';
import UserInfo from '../components/user/Userinfo.vue';
import FileUpload from '@/views/Upload.vue';
import { startIdleTimer, stopIdleTimer } from '@/utils/idleTimer';
import PasswordChange from '../components/user/PasswordChange.vue';
import RegisterForm from '../views/Register.vue';
import AdminPage from '@/views/Admin.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    
    { path: '/', redirect: '/login'},

    { path: '/login', name: 'login', component: LoginPage },

    { path: '/register', name: 'register', component: RegisterForm},
    
    { 
      path: '/dashboard', 
      name: 'dashboard', 
      component: Dashboard, 
      meta: { requiresAuth: true } 
    },
    { 
      path: '/statements', 
      name: 'statements', 
      component: Statements,
      meta: { requiresAuth: true }
    },
    { 
      path: '/user', 
      name: 'userinfo', 
      component: UserInfo,
      meta: { requiresAuth: true }
    },
    {
      path: '/upload', 
      name:'fileupload', 
      component: FileUpload,
      meta: { requiresAuth: true }
    },
    {
      path: '/password',
      name: 'passwordchange',
      component: PasswordChange,
      meta: { requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);

  const isAuthenticated = authStore.isAuthenticated;
  const isAdmin = authStore.isAdmin;
  
  const enteringProtected = requiresAuth && isAuthenticated;
  const leavingProtected = from.matched.some(record => record.meta.requiresAuth) && !requiresAuth;

  if (enteringProtected) {
    startIdleTimer();
  } else if (leavingProtected || to.name === 'login') {
    stopIdleTimer();
  }

  if (requiresAuth && !isAuthenticated) {
    next({ 
      name: 'login', 
      query: { redirect: to.fullPath } 
    });
  } else if (requiresAdmin && !isAdmin) {
    next({ name: 'dashboard' }); 
  } else if (isAuthenticated && to.name === 'login') {
    next({ name: 'dashboard' });
  } else {
    next();
  }
});

export default router
