import { ref } from 'vue'

/**
 * Helper mínimo para no repetir loading/error/data en cada vista.
 * Uso: const { data, loading, error, run } = useAsync()
 *      run(() => tesisService.list())
 */
export function useAsync(initialData = null) {
  const data = ref(initialData)
  const loading = ref(false)
  const error = ref(null)

  async function run(fn) {
    loading.value = true
    error.value = null
    try {
      const response = await fn()
      data.value = response.data
      return response.data
    } catch (err) {
      error.value = extractErrorMessage(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  return { data, loading, error, run }
}

export function extractErrorMessage(err) {
  const backendMessage = err?.response?.data?.message || err?.response?.data?.error
  if (backendMessage) return backendMessage
  if (err?.response?.status) return `Error del servidor (${err.response.status}).`
  if (err?.request) return 'No se pudo contactar al servidor.'
  return err?.message || 'Ocurrió un error inesperado.'
}
