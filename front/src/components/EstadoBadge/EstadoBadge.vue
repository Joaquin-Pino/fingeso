<script setup>
import { computed } from 'vue'

const props = defineProps({
  estado: { type: String, required: true },
})

const labels = {
  EN_DESARROLLO: 'En desarrollo',
  HABILITADA_PARA_DEFENSA: 'Habilitada para defensa',
  FINALIZADA: 'Finalizada',
}

const classByEstado = {
  EN_DESARROLLO: 'badge-warning',
  HABILITADA_PARA_DEFENSA: 'badge-primary',
  FINALIZADA: 'badge-success',
}

const label = computed(() => labels[props.estado] ?? props.estado)
const badgeClass = computed(() => classByEstado[props.estado] ?? 'badge-default')
</script>

<template>
  <span class="badge" :class="badgeClass">{{ label }}</span>
</template>

<style scoped>
.badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
  white-space: nowrap;
}

.badge-warning {
  background: var(--color-warning-bg);
  color: #7a4a00;
}

.badge-primary {
  background: #e3ecfb;
  color: var(--color-primary);
}

.badge-success {
  background: var(--color-success-bg);
  color: var(--color-success);
}

.badge-default {
  background: var(--color-bg);
  color: var(--color-text-muted);
}
</style>
