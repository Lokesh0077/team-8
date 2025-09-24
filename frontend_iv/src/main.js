import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// Import the CSS file - this line is crucial!
import './style.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')