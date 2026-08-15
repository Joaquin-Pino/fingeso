<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { tesisService } from '@/services/tesisService'
import { entregaService } from '@/services/entregaService'
import { useAsync } from '@/composables/useAsync'
import { useAuth } from '@/composables/useAuth'
import EstadoBadge from '@/components/EstadoBadge/EstadoBadge.vue'
import EntregaList from '@/components/EntregaList/EntregaList.vue'
import LoadingSpinner from '@/components/LoadingSpinner/LoadingSpinner.vue'
import ErrorMessage from '@/components/ErrorMessage/ErrorMessage.vue'
import EntregaUploadModal from '@/components/modals/EntregaUploadModal/EntregaUploadModal.vue'

const route = useRoute()
const { isTesista, email } = useAuth()

const { data: tesis, loading: loadingTesis, error: errorTesis, run: runTesis } = useAsync(null)
const { data: entregas, loading: loadingEntregas, error: errorEntregas, run: runEntregas } = useAsync([])

const showUploadModal = ref(false)

function fetchAll() {
  runTesis(() => tesisService.getById(route.params.id))
  runEntregas(() => entregaService.list(route.params.id))
}

const canUpload = computed(() => {
  if (!tesis.value) return false
  return (
    isTesista.value &&
    tesis.value.estado === 'EN_DESARROLLO' &&
    tesis.value.tesista?.email === email.value
  )
})

function handleUploaded() {
  runEntregas(() => entregaService.list(route.params.id))
}

onMounted(fetchAll)
</script>

<template>
  <div class="container">
    <LoadingSpinner v-if="loadingTesis" />
    <ErrorMessage v-else-if="errorTesis" :message="errorTesis" />

    <template v-else-if="tesis">
      <div class="page-header">
        <div>
          <h1>{{ tesis.titulo }}</h1>
          <EstadoBadge :estado="tesis.estado" />
        </div>
        <button v-if="canUpload" type="button" @click="showUploadModal = true">Subir avance</button>
      </div>

      <dl class="tesis-meta card">
        <div>
          <dt>Tesista</dt>
          <dd>{{ tesis.tesista?.nombre }} ({{ tesis.tesista?.email }})</dd>
        </div>
        <div>
          <dt>Profesor guía</dt>
          <dd>{{ tesis.profesorGuia?.nombre }} ({{ tesis.profesorGuia?.email }})</dd>
        </div>
        <div v-if="tesis.profesorCoguia">
          <dt>Profesor co-guía</dt>
          <dd>{{ tesis.profesorCoguia.nombre }} ({{ tesis.profesorCoguia.email }})</dd>
        </div>
        <div v-if="tesis.fechaInicio">
          <dt>Fecha de inicio</dt>
          <dd>{{ tesis.fechaInicio }}</dd>
        </div>
      </dl>

      <h2>Avances entregados</h2>
      <LoadingSpinner v-if="loadingEntregas" />
      <ErrorMessage v-else-if="errorEntregas" :message="errorEntregas" />
      <EntregaList v-else :entregas="entregas ?? []" />
    </template>

    <EntregaUploadModal
      v-if="showUploadModal"
      :tesis-id="route.params.id"
      @close="showUploadModal = false"
      @uploaded="handleUploaded"
    />
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-4);
}

.page-header h1 {
  margin: 0 0 var(--spacing-1) 0;
}

.tesis-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-4);
  margin: 0 0 var(--spacing-4) 0;
}

.tesis-meta dt {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.tesis-meta dd {
  margin: 0;
}
</style>
