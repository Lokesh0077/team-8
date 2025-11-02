<template>
  <div 
    class="min-h-screen flex flex-col md:flex-row 
          bg-indigo-700 text-white"
    >
    <!-- Left Side: Branding Panel -->
    <div class="flex-1 flex flex-col justify-center px-10 py-12">
      <div class="max-w-lg">
        <div class="flex items-center space-x-3 mb-6">
          <svg class="h-10 w-10 text-indigo-300" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.602-3.751m-.228-1.114a11.959 11.959 0 00-2.602-.746c-.662-.144-1.33-.215-2.008-.215A11.959 11.959 0 0012 2.944 11.959 11.959 0 009 3.612Z" />
          </svg>
          <h1 class="text-2xl font-bold tracking-wide text-white">eStatement Portal</h1>
        </div>
        <h2 class="text-3xl md:text-4xl font-extrabold leading-snug mb-4">
          Join Our Secure Platform.
        </h2>
        <p class="text-indigo-100 max-w-md text-lg">
          Create an account to get started. It's fast, easy, and secure.
        </p>
      </div>
    </div>

    <!-- Right Side: White Panel -->
    <div class="flex-1 flex items-center justify-center bg-white text-gray-800 rounded-t-3xl md:rounded-none md:rounded-l-3xl shadow-2xl p-10">
      
      <!-- The new "Register Box" inside the white panel -->
      <div class="w-full max-w-md space-y-8 bg-gray-50 text-gray-800 rounded-2xl shadow-xl border border-gray-200 p-10">
        <div class="text-center">
          <h2 class="text-3xl font-extrabold text-gray-900">
            Create Your Account
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            Get started by filling out the form below
          </p>
        </div>

        <form @submit.prevent="handleRegister" class="mt-8 space-y-6">
          
          <!-- Username -->
          <div class="relative">
            <input
              id="username"
              type="text"
              v-model="username"
              class="peer form-input"
              placeholder="Username"
              autocomplete="off"
            />
            <label
              for="username"
              class="form-label"
            >
              Username
            </label>
          </div>

          <!-- Email -->
          <div class="relative">
            <input
              id="email"
              type="email"
              v-model="email"
              class="peer form-input"
              placeholder="Email"
              autocomplete="off"
            />
            <label
              for="email"
              class="form-label"
            >
              Email
            </label>
          </div>

          <!-- Password -->
          <div class="relative">
            <input
              id="password"
              :type="passwordVisible ? 'text' : 'password'"
              v-model="password"
              class="peer form-input pr-12"
              placeholder="Password"
              autocomplete="off"
            />
            <label
              for="password"
              class="form-label"
            >
              Password
            </label>
            <button
              type="button"
              @click="passwordVisible = !passwordVisible"
              class="absolute inset-y-0 right-0 flex items-center pr-4 text-gray-500 hover:text-indigo-600 focus:outline-none"
            >
              <svg v-if="!passwordVisible" class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.522 10.522 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L6.228 6.228" /></svg>
              <svg v-else class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
            </button>
          </div>

          <!-- Confirm Password -->
          <div class="relative">
            <input
              id="confirmPassword"
              :type="confirmPasswordVisible ? 'text' : 'password'"
              v-model="confirmPassword"
              class="peer form-input pr-12"
              placeholder="Confirm Password"
              autocomplete="off"
            />
            <label
              for="confirmPassword"
              class="form-label"
            >
              Confirm Password
            </label>
            <button
              type="button"
              @click="confirmPasswordVisible = !confirmPasswordVisible"
              class="absolute inset-y-0 right-0 flex items-center pr-4 text-gray-500 hover:text-indigo-600 focus:outline-none"
            >
              <svg v-if="!confirmPasswordVisible" class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.522 10.522 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L6.228 6.228" /></svg>
              <svg v-else class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
            </button>
          </div>
          
          <!-- Register Button -->
          <button
            type="submit"
            class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-semibold rounded-lg text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-indigo-500 transition-transform transform hover:scale-105"
            :class="{'opacity-75 cursor-not-allowed': loading}" 
            :disabled="loading"
          >
            <span v-if="loading" class="spinner"></span>
            <span v-else>Register</span>
          </button>
        </form>

        <!-- Bottom Link -->
        <p class="mt-2 text-sm text-gray-600 text-center">
          Already have an account?
          <router-link to="/login" class="font-medium text-indigo-600 hover:text-indigo-500 hover:underline">LOGIN</router-link>
        </p>

        <!-- Response Message -->
        <div v-if="responseMessage" class="mt-6">
          <div
            :class="[
              'w-full flex items-center p-4 rounded-lg text-sm font-medium',
              success ? 'bg-green-100 text-green-800 border border-green-200' : 'bg-red-100 text-red-800 border border-red-200'
            ]"
          >
            <svg v-if="success" class="w-5 h-5 mr-3 text-green-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
            <svg v-else class="w-5 h-5 mr-3 text-red-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
            <span>{{ responseMessage }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      responseMessage: '',
      success: false,
      loading: false,
      passwordVisible: false,
      confirmPasswordVisible: false
    };
  },
  methods: {
    async handleRegister() {
      this.loading = true;
      this.responseMessage = '';
      this.success = false;

      if (!this.username || !this.email || !this.password || !this.confirmPassword) {
        this.responseMessage = 'All fields are required.';
        this.success = false;
        this.loading = false;
        return;
      }
      if (!this.email.includes('@')) {
        this.responseMessage = 'Invalid email format.';
        this.success = false;
        this.loading = false;
        return;
      }
      if (this.password !== this.confirmPassword) {
        this.responseMessage = 'Passwords do not match.';
        this.success = false;
        this.loading = false;
        return;
      }

      try {
        const response = await axios.post('/api/auth/register', { 
          username: this.username,
          email: this.email,
          password: this.password
        });

        this.responseMessage = 'Registration successful! You can now log in.';
        this.success = true;
        this.username = '';
        this.email = '';
        this.password = '';
        this.confirmPassword = '';

      } catch (error) {
        this.responseMessage = error.response?.data?.message || error.response?.data || 'Registration failed.';
        this.success = false;
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>

.form-input {
  width: 100%;
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
  padding-left: 1rem;
  padding-right: 1rem;
  border-width: 2px;
  border-color: #D1D5DB;
  border-radius: 0.5rem;
  color: #111827;
  outline: 2px solid transparent;
  outline-offset: 2px;
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

.form-input::placeholder {
  color: transparent;
}

.form-input:focus {
  border-color: transparent;
  --tw-ring-color: #4F46E5;
  --tw-ring-offset-shadow: var(--tw-ring-inset) 0 0 0 var(--tw-ring-offset-width, 0px) var(--tw-ring-offset-color, #fff);
  --tw-ring-shadow: var(--tw-ring-inset) 0 0 0 calc(2px + var(--tw-ring-offset-width, 0px)) var(--tw-ring-color, #4F46E5);
  box-shadow: var(--tw-ring-offset-shadow, 0 0 #0000), var(--tw-ring-shadow, 0 0 #0000), var(--tw-shadow, 0 0 #0000);
}

/* Floating label styling (UPDATED) */
.form-label {
  position: absolute;
  left: 1rem;
  top: -0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: #6B7280;
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
  background-color: #f9fafb; /* Changed from bg-white to bg-gray-50 */
  padding-left: 0.25rem;
  padding-right: 0.25rem;
}

.peer:placeholder-shown + .form-label {
  font-size: 1rem;
  line-height: 1.5rem;
  color: #9CA3AF;
  top: 0.875rem;
}

.peer:focus + .form-label {
  top: -0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: #4F46E5;
  background-color: #f9fafb; /* Changed from bg-white to bg-gray-50 */
}

/* Spinner animation */
.spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>

