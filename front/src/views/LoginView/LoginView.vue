<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authService } from '@/services/authService'
import { useAuth } from '@/composables/useAuth'
import { extractErrorMessage } from '@/composables/useAsync'
import ErrorMessage from '@/components/ErrorMessage/ErrorMessage.vue'

// NOTA: /api/auth/login todavía no existe en la rama de backend actual
// (existe en la rama sin mergear origin/feature/autenticacion). Esta vista
// queda funcionalmente completa y lista para usarse en cuanto esa rama se
// integre; hasta entonces el submit fallará con un error de red o 404.

const route = useRoute()
const router = useRouter()
const { login } = useAuth()

const email = ref('')
const password = ref('')
const error = ref(null)
const submitting = ref(false)

const expiredSession = route.query.reason === 'expired'

async function handleSubmit() {
  submitting.value = true
  error.value = null
  try {
    const { data } = await authService.login(email.value, password.value)
    login(data.token)
    router.push(route.query.redirect?.toString() || '/')
  } catch (err) {
    error.value = extractErrorMessage(err)
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <form class="card auth-form" @submit.prevent="handleSubmit">
      <h1>Iniciar sesión</h1>
      <p class="subtitle">Plataforma de Gestión de Tesistas</p>

      <ErrorMessage v-if="expiredSession && !error" message="Tu sesión expiró. Inicia sesión nuevamente." />
      <ErrorMessage v-if="error" :message="error" />

      <div class="form-field">
        <label for="email">Correo institucional</label>
        <input id="email" v-model="email" type="email" required autocomplete="username" />
      </div>

      <div class="form-field">
        <label for="password">Contraseña</label>
        <input id="password" v-model="password" type="password" required autocomplete="current-password" />
      </div>

      <button type="submit" :disabled="submitting">{{ submitting ? 'Ingresando…' : 'Ingresar' }}</button>

      <p class="switch-link">
        ¿No tienes cuenta? <RouterLink to="/register">Regístrate</RouterLink>
      </p>
    </form>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 64px);
  padding: var(--spacing-3);
}

.auth-form {
  width: 100%;
  max-width: 380px;
}

.auth-form h1 {
  margin: 0 0 var(--spacing-1) 0;
}

.subtitle {
  color: var(--color-text-muted);
  margin: 0 0 var(--spacing-4) 0;
}

.switch-link {
  text-align: center;
  margin: var(--spacing-3) 0 0 0;
  font-size: 0.9rem;
}
</style>
