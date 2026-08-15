# PGT — Frontend (Vue 3)

SPA para la Plataforma de Gestión de Tesistas. Vue 3 (Composition API) + Vite + vue-router + Axios. **Sin Pinia**: el estado de sesión vive en un composable (`src/composables/useAuth.js`) con un `reactive` a nivel de módulo, actuando como store singleton sin depender de una librería externa.

## Requisitos

- Node.js 20+
- Backend Spring Boot corriendo en `http://localhost:8080` (ver `../README` / `../CLAUDE.md` del proyecto para levantarlo con `./mvnw spring-boot:run`, requiere Postgres local).

## Uso

```bash
npm install
npm run dev      # http://localhost:5173, con proxy /api -> http://localhost:8080
npm run build     # build de producción a dist/
npm run preview   # sirve el build de producción localmente
```

En desarrollo, Vite reenvía `/api/*` al backend (ver `vite.config.js`) para evitar problemas de CORS, ya que el backend no tiene CORS configurado. `VITE_API_BASE_URL` (en `.env.development` / `.env.production`) controla la URL base usada por Axios.

## Estructura

```
src/
  main.js, App.vue
  assets/            # estilos base
  router/            # rutas + guards de autenticación/rol
  services/          # instancia axios + servicios por recurso (auth, tesis, entregas)
  composables/        # useAuth (sesión, sin Pinia), useIdleTimer (RNF_010), useAsync
  utils/               # decodeJwtPayload, validación de archivos (RNF_009)
  components/          # un componente por carpeta; modales en components/modals/
  views/                # una vista por carpeta
```

## Estado conocido / limitaciones actuales

- `POST /api/auth/login` y `POST /api/auth/register` **no existen todavía** en la rama de backend `front` — solo están implementados en la rama remota sin mergear `origin/feature/autenticacion`. Las vistas `LoginView`/`RegisterView` y toda la lógica de sesión (`useAuth`, guards del router, interceptores 401/403, idle timeout) están construidas contra ese contrato y quedarán operativas en cuanto esa rama se integre al backend. Hasta entonces, el login fallará contra el backend local.
- Sí son usables hoy, contra el backend de esta rama: listado/detalle de tesis (`/`, `/tesis/:id`) y subida/listado de avances de una tesis `EN_DESARROLLO`.
- El filtrado de "un tesista solo ve su propia tesis" (RNF_021) se hace en el cliente comparando email, porque no hay endpoint que devuelva el id numérico del usuario autenticado a partir del JWT. Es una medida adicional, no el control de seguridad principal — ese debe reforzarse en el backend.
