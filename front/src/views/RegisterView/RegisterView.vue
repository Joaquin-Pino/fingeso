<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '@/services/authService'
import { useAuth } from '@/composables/useAuth'
import { extractErrorMessage } from '@/composables/useAsync'
import ErrorMessage from '@/components/ErrorMessage/ErrorMessage.vue'

// NOTA: /api/auth/register todavía no existe en la rama de backend actual
// (ver misma nota en LoginView.vue). El dominio (contexto_proyecto.md) prevé
// que el registro lo haga normalmente el Coordinador de Tesis (CU-02); este
// formulario público es provisional, alineado al contrato actualmente
// implementado en origin/feature/autenticacion, y puede restringirse o
// eliminarse cuando ese caso de uso se implemente formalmente.

const router = useRouter()
const { login } = useAuth()

const nombre = ref('')
const email = ref('')
const password = ref('')
const rol = ref('TESISTA')
const error = ref(null)
const submitting = ref(false)

async function handleSubmit() {
  submitting.value = true
  error.value = null
  try {
    const { data } = await authService.register({
      nombre: nombre.value,
      email: email.value,
      password: password.value,
      rol: rol.value,
    })
    login(data.token)
    router.push('/')
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
      <h1>Crear cuenta</h1>

      <ErrorMessage v-if="error" :message="error" />

      <div class="form-field">
        <label for="nombre">Nombre completo</label>
        <input id="nombre" v-model="nombre" type="text" required autocomplete="name" />
      </div>

      <div class="form-field">
        <label for="email">Correo institucional</label>
        <input id="email" v-model="email" type="email" required autocomplete="username" />
      </div>

      <div class="form-field">
        <label for="password">Contraseña</label>
        <input id="password" v-model="password" type="password" required autocomplete="new-password" />
      </div>

      <div class="form-field">
        <label for="rol">Rol</label>
        <select id="rol" v-model="rol">
          <option value="TESISTA">Tesista</option>
          <option value="PROFESOR">Profesor</option>
        </select>
      </div>

      <button type="submit" :disabled="submitting">{{ submitting ? 'Creando cuenta…' : 'Crear cuenta' }}</button>

      <p class="switch-link">
        ¿Ya tienes cuenta? <RouterLink to="/login">Inicia sesión</RouterLink>
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
  margin: 0 0 var(--spacing-4) 0;
}

.switch-link {
  text-align: center;
  margin: var(--spacing-3) 0 0 0;
  font-size: 0.9rem;
}
</style>
