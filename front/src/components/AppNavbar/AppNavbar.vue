<script setup>
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const { isAuthenticated, email, role, logout } = useAuth()

function handleLogout() {
  logout()
  router.push({ name: 'login' })
}

const roleLabel = {
  ROLE_TESISTA: 'Tesista',
  ROLE_PROFESOR: 'Profesor',
}
</script>

<template>
  <header v-if="isAuthenticated" class="app-navbar">
    <RouterLink to="/" class="brand">PGT · Gestión de Tesistas</RouterLink>
    <div class="user-info">
      <span class="user-email">{{ email }}</span>
      <span v-if="role" class="user-role">{{ roleLabel[role] ?? role }}</span>
      <button type="button" class="secondary" @click="handleLogout">Cerrar sesión</button>
    </div>
  </header>
</template>

<style scoped>
.app-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-3) var(--spacing-4);
  background: var(--color-navbar-bg);
  color: var(--color-navbar-text);
  border-bottom: 3px solid var(--color-primary);
}

.brand {
  font-weight: 700;
  text-decoration: none;
  color: var(--color-navbar-text);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.user-email {
  color: #e0e0e0;
  font-size: 0.9rem;
}

.user-role {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  border-radius: var(--radius);
  padding: 2px 8px;
  font-size: 0.8rem;
}

button.secondary {
  background: transparent;
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

button.secondary:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
}
</style>
