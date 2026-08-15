import { useAuth } from '@/composables/useAuth'

// RNF_010: la sesión expira tras 1h de inactividad. Complementa (no reemplaza)
// la expiración propia del JWT: lo que ocurra primero cierra la sesión.
const IDLE_LIMIT_MS = 60 * 60 * 1000
const ACTIVITY_EVENTS = ['mousemove', 'keydown', 'click', 'scroll', 'touchstart']

let timeoutId = null
let checkIntervalId = null
let started = false

export function useIdleTimer(onExpire) {
  const { logout, state } = useAuth()

  function handleExpire() {
    logout()
    if (onExpire) onExpire()
  }

  function resetTimer() {
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(handleExpire, IDLE_LIMIT_MS)
  }

  function start() {
    if (started) return
    started = true
    ACTIVITY_EVENTS.forEach((evt) => window.addEventListener(evt, resetTimer, { passive: true }))
    resetTimer()

    // Además de la inactividad, vigila que el JWT no haya expirado ya
    // (por ejemplo si el usuario dejó la pestaña abierta sin interactuar).
    checkIntervalId = setInterval(() => {
      if (state.exp != null && Date.now() >= state.exp * 1000) {
        handleExpire()
      }
    }, 30 * 1000)
  }

  function stop() {
    started = false
    if (timeoutId) clearTimeout(timeoutId)
    if (checkIntervalId) clearInterval(checkIntervalId)
    ACTIVITY_EVENTS.forEach((evt) => window.removeEventListener(evt, resetTimer))
  }

  return { start, stop }
}
