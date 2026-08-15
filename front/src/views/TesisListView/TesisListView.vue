<script setup>
import { computed, onMounted, ref } from 'vue'
import { tesisService } from '@/services/tesisService'
import { useAsync } from '@/composables/useAsync'
import { useAuth } from '@/composables/useAuth'
import TesisCard from '@/components/TesisCard/TesisCard.vue'
import LoadingSpinner from '@/components/LoadingSpinner/LoadingSpinner.vue'
import ErrorMessage from '@/components/ErrorMessage/ErrorMessage.vue'

const { data: tesisList, loading, error, run } = useAsync([])
const { isTesista, email } = useAuth()
const estadoFiltro = ref('')

const ESTADOS = [
  { value: '', label: 'Todos los estados' },
  { value: 'EN_DESARROLLO', label: 'En desarrollo' },
  { value: 'HABILITADA_PARA_DEFENSA', label: 'Habilitada para defensa' },
  { value: 'FINALIZADA', label: 'Finalizada' },
]

function fetchTesis() {
  const params = estadoFiltro.value ? { estado: estadoFiltro.value } : {}
  return run(() => tesisService.list(params))
}

// RNF_021: un tesista no debe ver tesis de otros tesistas. No existe aún un
// endpoint que entregue el id numérico del usuario autenticado a partir del
// JWT (solo email/rol), así que este filtrado es una medida adicional en el
// cliente, no el único control — la restricción real debe reforzarse también
// en el backend en cuanto exponga esa relación.
const visibleTesis = computed(() => {
  if (!isTesista.value) return tesisList.value ?? []
  return (tesisList.value ?? []).filter((t) => t.tesista?.email === email.value)
})

onMounted(fetchTesis)
</script>

<template>
  <div class="container">
    <div class="page-header">
      <h1>Tesis</h1>
      <select v-model="estadoFiltro" @change="fetchTesis">
        <option v-for="opt in ESTADOS" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
      </select>
    </div>

    <LoadingSpinner v-if="loading" />
    <ErrorMessage v-else-if="error" :message="error" />
    <p v-else-if="visibleTesis.length === 0" class="empty">No hay tesis para mostrar.</p>
    <div v-else class="tesis-grid">
      <TesisCard v-for="tesis in visibleTesis" :key="tesis.id" :tesis="tesis" />
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-4);
}

.page-header select {
  width: auto;
}

.tesis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-3);
}

.empty {
  color: var(--color-text-muted);
}
</style>
