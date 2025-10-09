import { createApp } from 'vue'
import App from './App.vue'

const app = createApp(App)

// Register global filters
app.config.globalProperties.$filters = {
  formatCurrency: (amount) => {
    if (amount === null || amount === undefined) return 'N/A'
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  },
  formatDateTime: (dateTime) => {
    if (!dateTime) return 'N/A'
    const date = new Date(dateTime)
    return new Intl.DateTimeFormat('en-IN', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    }).format(date)
  }
}

app.mount('#app')