import http from '@/services/http'

export const entregaService = {
  list(tesisId) {
    return http.get(`/tesis/${tesisId}/entregas`)
  },

  upload(tesisId, file, onUploadProgress) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post(`/tesis/${tesisId}/entregas`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress,
    })
  },
}
