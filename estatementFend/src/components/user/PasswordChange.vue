<template>
  <DashboardLayout>
    <div class="p-4 sm:p-6 lg:p-8 min-h-screen bg-gray-50">
      
      <div class="max-w-xl mx-auto bg-white p-6 rounded-2xl shadow-xl border border-gray-100">
        
        <header class="text-center mb-8">
          <h2 class="text-2xl font-extrabold text-gray-800">Security & Password</h2>
          <p class="text-sm text-gray-500 mt-1">Update your account password below. You will be logged out upon success.</p>
        </header>

        <form @submit.prevent="handleChangePassword" class="space-y-6">
          
          <div v-if="message" :class="messageClass" class="p-3 rounded-xl text-sm font-medium transition-all duration-300">
            {{ message }}
          </div>

          <div class="field-group">
            <label for="currentPassword" class="form-label">Current Password</label>
            <div class="relative">
              <input
                id="currentPassword"
                v-model="passwords.current"
                :type="showCurrent ? 'text' : 'password'"
                autocomplete="current-password"
                class="form-input"
                :class="{ 'input-error': v$.current.$error }"
                @input="v$.current.$touch"
                required
              >
              <button type="button" @click="showCurrent = !showCurrent" class="password-toggle-btn">
                <svg v-if="showCurrent" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                <svg v-else class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7 1.274 4.057 5.064 7 9.542 7 1.41 0 2.763.348 3.997.981M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.982 17.982L6.018 6.018M21 21L3 3" /></svg>
              </button>
            </div>
            <span v-if="v$.current.$error" class="error-message">Current password is required.</span>
          </div>

          <div class="py-2">
            <hr class="border-gray-200">
          </div>
          
          <div class="field-group">
            <label for="newPassword" class="form-label">New Password</label>
            <div class="relative">
              <input
                id="newPassword"
                v-model="passwords.new"
                :type="showNew ? 'text' : 'password'"
                autocomplete="new-password"
                class="form-input"
                :class="{ 'input-error': v$.new.$error }"
                @input="v$.new.$touch"
                required
              >
              <button type="button" @click="showNew = !showNew" class="password-toggle-btn">
                <svg v-if="showNew" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                <svg v-else class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7 1.274 4.057 5.064 7 9.542 7 1.41 0 2.763.348 3.997.981M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.982 17.982L6.018 6.018M21 21L3 3" /></svg>
              </button>
            </div>
            <div class="mt-1">
              <span v-if="v$.new.required.$invalid && v$.new.$dirty" class="error-message block">New password is required.</span>
              <span v-else-if="v$.new.minLength.$invalid && v$.new.$dirty" class="error-message block">Must be at least 8 characters long.</span>
            </div>
            <div v-if="passwords.new.length > 0" class="strength-meter-container">
              <div class="strength-meter-bar" :style="strengthBarStyle"></div>
            </div>
          </div>

          <div class="field-group">
            <label for="confirmPassword" class="form-label">Confirm New Password</label>
            <div class="relative">
              <input
                id="confirmPassword"
                v-model="passwords.confirm"
                :type="showConfirm ? 'text' : 'password'"
                autocomplete="new-password"
                class="form-input"
                :class="{ 'input-error': v$.confirm.$error }"
                @input="v$.confirm.$touch"
                required
              >
              <button type="button" @click="showConfirm = !showConfirm" class="password-toggle-btn">
                <svg v-if="showConfirm" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                <svg v-else class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7 1.274 4.057 5.064 7 9.542 7 1.41 0 2.763.348 3.997.981M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.982 17.982L6.018 6.018M21 21L3 3" /></svg>
              </button>
            </div>
            <span v-if="v$.confirm.$error" class="error-message">Passwords do not match.</span>
          </div>

          <div class="pt-4">
            <button 
              type="submit" 
              :disabled="isSubmitting || v$.$invalid || isSuccess"
              class="submit-btn"
              :class="{ 'success': isSuccess }"
            >
              <span v-if="isSuccess" class="flex items-center justify-center">
                <svg class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                Password Updated!
              </span>
              <span v-else-if="isSubmitting" class="flex items-center justify-center">
                <svg class="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Updating Password...
              </span>
              <span v-else>Update Password</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </DashboardLayout>
</template>

<script>
import { defineComponent, ref, computed } from 'vue';
import { useVuelidate } from '@vuelidate/core';
import { required, minLength, sameAs } from '@vuelidate/validators';
import axios from 'axios';
import router from '@/router'; 
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue';

export default defineComponent({
  components: {
    DashboardLayout
  },
  name: 'PasswordChangeForm',
  setup() {
    const passwords = ref({
      current: '',
      new: '',
      confirm: ''
    });
    const message = ref(null);
    const messageType = ref('error'); // 'error' or 'success'
    const isSubmitting = ref(false);
    const isSuccess = ref(false); // New success state

    // Show/Hide Password Toggles
    const showCurrent = ref(false);
    const showNew = ref(false);
    const showConfirm = ref(false);

    const rules = computed(() => ({
      current: { required },
      new: { required, minLength: minLength(8) },
      confirm: { required, sameAs: sameAs(passwords.value.new) },
    }));

    const v$ = useVuelidate(rules, passwords);

    const messageClass = computed(() => ({
      'bg-red-50 border border-red-200 text-red-700': messageType.value === 'error',
      'bg-green-50 border border-green-200 text-green-700': messageType.value === 'success',
    }));

    // --- Password Strength ---
    const passwordStrength = computed(() => {
      let score = 0;
      const pass = passwords.value.new;
      if (pass.length >= 8) score++;
      if (pass.length >= 12) score++;
      if (/[A-Z]/.test(pass)) score++; // Uppercase
      if (/[0-9]/.test(pass)) score++; // Number
      if (/[^A-Za-z0-9]/.test(pass)) score++; // Special char
      return score; // Max score is 5
    });

    const strengthBarStyle = computed(() => {
      const score = passwordStrength.value;
      const width = (score / 5) * 100;
      let color = '#dc2626'; // red-600
      if (score >= 4) color = '#16a34a'; // green-600
      else if (score >= 2) color = '#f59e0b'; // amber-500
      return {
        width: `${width}%`,
        backgroundColor: color
      };
    });
    // --- End Password Strength ---

    const getAuthHeaders = () => {
      const token = localStorage.getItem('token');
      if (!token) {
        router.push({ name: 'login' }); 
        return null;
      }
      return { Authorization: `Bearer ${token}` };
    };

    const handleChangePassword = async () => {
      message.value = null;
      isSuccess.value = false;
      const isValid = await v$.value.$validate();
      if (!isValid) return;

      const headers = getAuthHeaders();
      if (!headers) return;

      isSubmitting.value = true;

      try {
        const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
        
        await axios.post(`${apiUrl}/api/user/change-password`, {
          currentPassword: passwords.value.current,
          newPassword: passwords.value.new
        }, { headers });

        message.value = 'Password updated successfully. You will be logged out now.';
        messageType.value = 'success';
        isSuccess.value = true; // Trigger success state on button
        
        setTimeout(() => {
          localStorage.removeItem('token');
          router.push({ name: 'login', query: { message: 'password_changed' } });
        }, 2000);

      } catch (error) {
        messageType.value = 'error';
        if (error.response && error.response.status === 401) {
          message.value = 'Invalid current password. Please try again.';
        } else {
          message.value = 'An error occurred while changing password. Please try again.';
        }
        console.error('Password change error:', error);
      } finally {
        // Only clear inputs if the submission failed
        if (!isSuccess.value) {
          isSubmitting.value = false;
          passwords.value.current = '';
          passwords.value.new = '';
          passwords.value.confirm = '';
          v$.value.$reset();
        }
      }
    };

    return {
      passwords,
      v$,
      message,
      messageClass,
      isSubmitting,
      isSuccess,
      handleChangePassword,
      showCurrent,
      showNew,
      showConfirm,
      strengthBarStyle
    };
  }
});
</script>

<style scoped>
/* Use the same class as SearchForm.css for consistency */
.form-input {
  width: 100%;
  padding-left: 1rem;
  padding-right: 2.75rem; /* Make space for the icon */
  padding-top: 0.625rem;
  padding-bottom: 0.625rem;
  border-width: 1px;
  border-style: solid;
  border-color: #D1D5DB; /* gray-300 */
  border-radius: 0.5rem; /* rounded-lg */
  outline: none;
  transition: all 0.2s ease-in-out;
}

.form-input:focus {
  box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.08); /* subtle indigo ring */
  border-color: transparent;
}

.form-input.input-error {
  border-color: #ef4444; /* red-500 */
}

.form-input.input-error:focus {
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.08); /* red ring */
}

/* form label */
.form-label {
  display: block;
  font-size: 0.875rem; /* text-sm */
  font-weight: 600; /* font-semibold */
  color: #374151; /* gray-700 */
}

.error-message {
  font-size: 0.75rem; /* text-xs */
  color: #dc2626; /* red-600 */
  margin-top: 0.25rem; /* mt-1 */
}

/* Eye icon button */
.password-toggle-btn {
  position: absolute;
  top: 0;
  bottom: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding-right: 0.75rem; /* pr-3 */
  color: #9CA3AF; /* gray-400 */
  transition: color 0.15s ease-in-out;
}

.password-toggle-btn:hover {
  color: #4B5563; /* gray-600 */
}

/* Submit button */
.submit-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 1rem;
  padding-right: 1rem;
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
  color: #ffffff;
  font-weight: 500;
  border-radius: 0.5rem; /* rounded-lg */
  transition: all 300ms ease;
  background-color: #4F46E5; /* indigo-600 */
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #4338CA; /* indigo-700 */
}

.submit-btn:focus {
  outline: none;
  box-shadow: 0 0 0 6px rgba(79, 70, 229, 0.08); /* focus ring */
}

/* disabled state */
.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Success state for the button */
.submit-btn.success {
  background-color: #16A34A; /* green-600 */
}

/* Ensure disabled success button remains visible */
.submit-btn:disabled.success {
  opacity: 1;
}

/* Password Strength Meter */
.strength-meter-container {
  width: 100%;
  background-color: #E5E7EB; /* gray-200 */
  border-radius: 9999px; /* rounded-full */
  height: 0.375rem; /* h-1.5 */
  margin-top: 0.5rem; /* mt-2 */
}
.strength-meter-bar {
  height: 0.375rem; /* h-1.5 */
  border-radius: 9999px;
  transition: all 300ms ease-out;
}
</style>