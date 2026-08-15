import http from '@/services/http'

export const tesisService = {
  list(params = {}) {
    return http.get('/tesis', { params })
  },

  getById(id) {
    return http.get(`/tesis/${id}`)
  },
}
