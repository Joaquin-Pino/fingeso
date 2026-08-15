import axios from 'axios'
import { getStoredToken, useAuth } from '@/composables/useAuth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
})

http.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Import diferido del router para evitar un ciclo de módulos en el arranque
// (router -> guards -> useAuth, y aquí services/http -> router).
let routerPromise = null
function getRouter() {
  if (!routerPromise) routerPromise = import('@/router')
  return routerPromise
}

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error?.response?.status

    if (status === 401) {
      useAuth().logout()
      const { default: router } = await getRouter()
      if (router.currentRoute.value.name !== 'login') {
        router.push({ name: 'login', query: { reason: 'expired' } })
      }
    } else if (status === 403) {
      const { default: router } = await getRouter()
      if (router.currentRoute.value.name !== 'forbidden') {
        router.push({ name: 'forbidden' })
      }
    }

    return Promise.reject(error)
  }
)

export default http
