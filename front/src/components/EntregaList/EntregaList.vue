<script setup>
import { formatBytes } from '@/utils/fileValidation'

defineProps({
  entregas: { type: Array, required: true },
})

function formatFecha(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('es-CL', {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}
</script>

<template>
  <p v-if="entregas.length === 0" class="empty">Todavía no hay entregas de avance para esta tesis.</p>
  <table v-else class="entrega-list">
    <thead>
      <tr>
        <th>Archivo</th>
        <th>Fecha de entrega</th>
        <th>Tamaño</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="entrega in entregas" :key="entrega.id">
        <td>{{ entrega.nombreArchivo }}</td>
        <td>{{ formatFecha(entrega.fechaEntrega) }}</td>
        <td>{{ formatBytes(entrega.tamanioBytes) }}</td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
.empty {
  color: var(--color-text-muted);
}

.entrega-list {
  width: 100%;
  border-collapse: collapse;
}

.entrega-list th,
.entrega-list td {
  text-align: left;
  padding: var(--spacing-2) var(--spacing-3);
  border-bottom: 1px solid var(--color-border);
}

.entrega-list th {
  font-size: 0.8rem;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}
</style>
