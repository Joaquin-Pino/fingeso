<script setup>
import { reactive } from 'vue'
import { formatBytes } from '@/utils/fileValidation'
import { entregaService } from '@/services/entregaService'
import { extractErrorMessage } from '@/composables/useAsync'

const props = defineProps({
  tesisId: { type: [String, Number], required: true },
  entregas: { type: Array, required: true },
})

// Estado de "ver archivo" por entrega, para no bloquear la tabla completa
// mientras se descarga un único PDF.
const estadoArchivo = reactive({})

function formatFecha(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('es-CL', {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}

async function verArchivo(entrega) {
  estadoArchivo[entrega.id] = { loading: true, error: null }
  try {
    const response = await entregaService.descargarArchivo(props.tesisId, entrega.id)
    const blobUrl = URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }))
    // Se abre en una pestaña nueva; el navegador libera el object URL al cerrarla.
    window.open(blobUrl, '_blank', 'noopener')
    estadoArchivo[entrega.id] = { loading: false, error: null }
  } catch (err) {
    estadoArchivo[entrega.id] = { loading: false, error: extractErrorMessage(err) }
  }
}
</script>

<template>
  <p v-if="entregas.length === 0" class="empty">Todavía no hay entregas de avance para esta tesis.</p>
  <table v-else class="entrega-list">
    <thead>
    <tr>
      <th>Archivo</th>
      <th>Comentario / Observaciones</th>
      <th>Fecha de entrega</th>
      <th>Tamaño</th>
      <th></th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="entrega in entregas" :key="entrega.id">
      <td>
        <span class="file-name">{{ entrega.nombreArchivo }}</span>
      </td>
      <td>
          <span v-if="entrega.comentario" class="comentario-texto">
            {{ entrega.comentario }}
          </span>
        <span v-else class="comentario-vacio">—</span>
      </td>
      <td>{{ formatFecha(entrega.fechaEntrega) }}</td>
      <td>{{ formatBytes(entrega.tamanioBytes) }}</td>
      <td class="acciones">
        <button
            type="button"
            class="secondary"
            :disabled="estadoArchivo[entrega.id]?.loading"
            @click="verArchivo(entrega)"
        >
          {{ estadoArchivo[entrega.id]?.loading ? 'Abriendo…' : 'Ver archivo' }}
        </button>
        <span v-if="estadoArchivo[entrega.id]?.error" class="file-error">
            {{ estadoArchivo[entrega.id].error }}
          </span>
      </td>
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
  vertical-align: middle;
}

.entrega-list th {
  font-size: 0.8rem;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.file-name {
  font-weight: 500;
}

.comentario-texto {
  display: block;
  max-width: 320px;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 0.9rem;
  color: var(--color-text, #333);
}

.comentario-vacio {
  color: var(--color-text-muted, #888);
}

.acciones {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  white-space: nowrap;
}

.file-error {
  color: var(--color-danger, #c0392b);
  font-size: 0.8rem;
}
</style>