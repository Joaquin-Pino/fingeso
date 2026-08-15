import http from '@/services/http'

// NOTA: /api/auth/login y /api/auth/register aún no existen en la rama de
// backend "front" — el contrato de abajo corresponde a la rama sin mergear
// origin/feature/autenticacion. Este servicio está listo para funcionar en
// cuanto esa rama se integre; hasta entonces estas llamadas fallarán contra
// el backend local (ver README de front/ y el plan de implementación).
export const authService = {
  login(email, password) {
    return http.post('/auth/login', { email, password })
  },

  register({ email, password, nombre, rol }) {
    return http.post('/auth/register', { email, password, nombre, rol })
  },
}
