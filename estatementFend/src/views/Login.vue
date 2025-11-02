<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <LoginForm />
  </div>
</template>

<script>
import LoginForm from '../components/auth/LoginForm.vue'; 

export default { 
  components: {
    LoginForm 
  }
}
</script>