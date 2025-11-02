import { nextTick } from 'vue';
import router from '@/router';

const IDLE_TIMEOUT_MS = 10 * 60 * 1000;
let timeoutTimer = null;

function resetTimer() {
  clearTimeout(timeoutTimer);
  timeoutTimer = setTimeout(logoutUser, IDLE_TIMEOUT_MS);
}

function logoutUser() {
  localStorage.removeItem('token');
  stopIdleTimer();

  nextTick(() => {
    router.push({ name: 'login', query: { timeout: 'true' } }).catch(() => {});
  });

  console.log("Session timed out due to inactivity.");
}

export function startIdleTimer() {
  if (!localStorage.getItem('token')) return;

  ['mousemove', 'keypress', 'click', 'scroll'].forEach(evt =>
    window.addEventListener(evt, resetTimer)
  );

  resetTimer();
}

export function stopIdleTimer() {
  clearTimeout(timeoutTimer);
  ['mousemove', 'keypress', 'click', 'scroll'].forEach(evt =>
    window.removeEventListener(evt, resetTimer)
  );
}
