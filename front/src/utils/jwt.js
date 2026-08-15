/**
 * Decodifica el payload de un JWT sin verificar la firma (solo lectura en cliente).
 * No requiere ninguna librería externa: un JWT es
 * `base64url(header).base64url(payload).base64url(signature)`.
 */
export function decodeJwtPayload(token) {
  const parts = token.split('.')
  if (parts.length !== 3) {
    throw new Error('Token JWT con formato inválido')
  }

  const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
  const json = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
      .join('')
  )

  return JSON.parse(json)
}
