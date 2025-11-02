<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow duration-200">
    <div class="flex items-center gap-4">
      <!-- Icon -->
      <div :class="getIconBackgroundClass(color)" class="p-3 rounded-lg flex-shrink-0">
        <component :is="getIcon(icon)" :class="getIconColorClass(color)" class="w-6 h-6" />
      </div>
      
      <!-- Content -->
      <div class="flex-grow">
        <p class="text-sm font-medium text-gray-600">{{ label }}</p>
        <p class="text-3xl font-bold text-gray-900 mt-1">{{ value }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    required: true,
    validator: (v) => ['document', 'check', 'clock'].includes(v)
  },
  label: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  color: {
    type: String,
    default: 'blue',
    validator: (v) => ['blue', 'green', 'amber'].includes(v)
  }
})

const getIcon = (iconName) => {
  const icons = {
    document: 'IconDocument',
    check: 'IconCheck',
    clock: 'IconClock'
  }
  return icons[iconName] || 'IconDocument'
}

const getIconBackgroundClass = (color) => {
  const classes = {
    blue: 'bg-blue-100',
    green: 'bg-green-100',
    amber: 'bg-amber-100'
  }
  return classes[color] || 'bg-blue-100'
}

const getIconColorClass = (color) => {
  const classes = {
    blue: 'text-blue-600',
    green: 'text-green-600',
    amber: 'text-amber-600'
  }
  return classes[color] || 'text-blue-600'
}
</script>

<!-- Inline SVG Icons -->
<script>
export const IconDocument = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>`
}

export const IconCheck = {
  template: `<svg fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" /></svg>`
}

export const IconClock = {
  template: `<svg fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00-.293.707l-1.414 1.414a1 1 0 101.414 1.414L9 11.414V6z" clip-rule="evenodd" /></svg>`
}
</script>