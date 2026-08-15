import http from '@/services/http'

export const entregaService = {
  list(tesisId) {
    return http.get(`/tesis/${tesisId}/entregas`)
  },

  upload(tesisId, file, comentario, onUploadProgress) {
    const formData = new FormData()
    formData.append('file', file)
    if (comentario && comentario.trim()){
      formData.append('comentario', comentario.trim())
    }
    return http.post(`/tesis/${tesisId}/entregas`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress,
    })
  },

  // Descarga el PDF de una entrega puntual como blob, para poder previsualizarlo
  // o guardarlo (el endpoint exige el JWT en el header Authorization, así que no
  // se puede enlazar directo con un <a href>).
  descargarArchivo(tesisId, entregaId) {
    return http.get(`/tesis/${tesisId}/entregas/${entregaId}/archivo`, {
      responseType: 'blob',
    })
  },
}
