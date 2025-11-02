<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <RegisterForm />
  </div>
</template>

<script>
import RegisterForm from '../components/auth/RegisterForm.vue'; 

export default { 
  components: {
    RegisterForm
  }
}
</script>