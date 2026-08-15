// RNF_009: las entregas de avance deben ser PDF de máximo 20MB.
export const MAX_ENTREGA_BYTES = 20 * 1024 * 1024

export function validateEntregaFile(file) {
  if (!file) {
    return 'Debes seleccionar un archivo.'
  }
  if (file.type !== 'application/pdf') {
    return 'El archivo debe ser un PDF.'
  }
  if (file.size > MAX_ENTREGA_BYTES) {
    return 'El archivo no debe superar los 20MB.'
  }
  return null
}

export function formatBytes(bytes) {
  if (bytes == null) return '—'
  const units = ['B', 'KB', 'MB', 'GB']
  let value = bytes
  let unitIndex = 0
  while (value >= 1024 && unitIndex < units.length - 1) {
    value /= 1024
    unitIndex += 1
  }
  return `${value.toFixed(unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`
}
