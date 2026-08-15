import { reactive, computed, readonly } from 'vue'
import { decodeJwtPayload } from '@/utils/jwt'

const TOKEN_STORAGE_KEY = 'fingeso_token'

// Estado reactivo a nivel de módulo: como no usamos Pinia, este objeto actúa
// como un "store" singleton. Todo componente que llame a useAuth() recibe la
// MISMA instancia reactiva, así que los cambios se propagan igual que con
// una store real, sin necesitar una librería de estado global.
const state = reactive({
  token: null,
  email: null, // JWT "sub"
  role: null, // "ROLE_TESISTA" | "ROLE_PROFESOR"
  exp: null, // segundos desde epoch
})

function isExpired() {
  return state.exp != null && Date.now() >= state.exp * 1000
}

function clearSession() {
  state.token = null
  state.email = null
  state.role = null
  state.exp = null
  localStorage.removeItem(TOKEN_STORAGE_KEY)
}

function applyToken(token) {
  const payload = decodeJwtPayload(token)
  state.token = token
  state.email = payload.sub ?? payload.email ?? null
  state.role = payload.role ?? null
  state.exp = payload.exp ?? null
  localStorage.setItem(TOKEN_STORAGE_KEY, token)
}

// Hidratar la sesión desde localStorage al cargar el módulo (recarga de página).
const persisted = localStorage.getItem(TOKEN_STORAGE_KEY)
if (persisted) {
  try {
    applyToken(persisted)
    if (isExpired()) clearSession()
  } catch {
    clearSession()
  }
}

export function useAuth() {
  function login(token) {
    applyToken(token)
  }

  function logout() {
    clearSession()
  }

  return {
    state: readonly(state),
    email: computed(() => state.email),
    role: computed(() => state.role),
    isAuthenticated: computed(() => !!state.token && !isExpired()),
    isTesista: computed(() => state.role === 'ROLE_TESISTA'),
    isProfesor: computed(() => state.role === 'ROLE_PROFESOR'),
    login,
    logout,
  }
}

// Usado por el interceptor de axios (fuera de un componente, sin necesidad
// de instanciar el composable completo).
export function getStoredToken() {
  return state.token
}
