// src/store/ui.js
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUiStore = defineStore('ui', () => {
  // State
  const isModalVisible = ref(false);
  const modalTitle = ref('');
  const modalMessage = ref('');
  const modalType = ref('success'); // 'success' or 'error'

  // Actions
  function showModal(title, message, type = 'success') {
    modalTitle.value = title;
    modalMessage.value = message;
    modalType.value = type;
    isModalVisible.value = true;
  }

  function hideModal() {
    isModalVisible.value = false;
  }

  return {
    isModalVisible,
    modalTitle,
    modalMessage,
    modalType,
    showModal,
    hideModal,
  };
});