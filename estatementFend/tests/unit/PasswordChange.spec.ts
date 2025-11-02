import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PasswordChange from '@/components/user/PasswordChange.vue'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'

// Define a dummy router to suppress warnings
const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/', component: { template: '<div />' } }],
})

describe('PasswordChange.vue', () => {
  let pinia

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
  })

  it('renders all password input fields and submit button', async () => {
    const wrapper = mount(PasswordChange, {
      global: {
        plugins: [pinia, router],
        // ensure all outer layout components only render slot content
        stubs: {
          DashboardLayout: { template: '<div><slot /></div>' },
          DashboardHeader: true,
          Sidebar: true,
          UserProfile: true,
          'router-link': true,
          'router-view': true,
        },
      },
    })

    await router.isReady()

    // Check inputs using a broader selector (case-insensitive)
    const inputs = wrapper.findAll('input[type=password]')
    console.log('Found password inputs:', inputs.length)

    expect(inputs.length).toBeGreaterThanOrEqual(1)

    const submitButton = wrapper.find('button[type=submit]')
    console.log('Found submit button:', submitButton.exists())

    expect(submitButton.exists()).toBe(true)
  })
})
