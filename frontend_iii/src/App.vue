<template>
  <div id="app">
    <Statements />
  </div>
</template>

<script>
import Statements from './views/Statements.vue'

export default {
  name: 'App',
  components: {
    Statements
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #f5f7fa;
}
</style>