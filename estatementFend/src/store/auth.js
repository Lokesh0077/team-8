import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null,
    roles: JSON.parse(localStorage.getItem('roles')) || [],
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    username: (state) => state.user?.username || 'Guest',
    isAdmin: (state) => state.roles.includes('ROLE_ADMIN'),
  },

  actions: {
    login(token) {
      try {
        const decodedToken = jwtDecode(token);
        this.token = token;
        this.user = { username: decodedToken.sub };
        this.roles = decodedToken.roles || [];

        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(this.user));
        localStorage.setItem('roles', JSON.stringify(this.roles));
      } catch (error) {
        console.error("Failed to decode token:", error);
        this.logout();
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      this.roles = [];
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('roles');
    },
  },
});

