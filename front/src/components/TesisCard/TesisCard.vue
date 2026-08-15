<script setup>
import EstadoBadge from '@/components/EstadoBadge/EstadoBadge.vue'

defineProps({
  tesis: { type: Object, required: true },
})
</script>

<template>
  <RouterLink :to="`/tesis/${tesis.id}`" class="tesis-card card">
    <div class="tesis-card-header">
      <h3>{{ tesis.titulo }}</h3>
      <EstadoBadge :estado="tesis.estado" />
    </div>
    <dl class="tesis-card-meta">
      <div>
        <dt>Tesista</dt>
        <dd>{{ tesis.tesista?.nombre ?? '—' }}</dd>
      </div>
      <div>
        <dt>Profesor guía</dt>
        <dd>{{ tesis.profesorGuia?.nombre ?? '—' }}</dd>
      </div>
      <div v-if="tesis.profesorCoguia">
        <dt>Profesor co-guía</dt>
        <dd>{{ tesis.profesorCoguia.nombre }}</dd>
      </div>
    </dl>
  </RouterLink>
</template>

<style scoped>
.tesis-card {
  display: block;
  text-decoration: none;
  color: inherit;
  transition: box-shadow 0.15s ease;
}

.tesis-card:hover {
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
}

.tesis-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-2);
}

.tesis-card-header h3 {
  margin: 0;
  font-size: 1.05rem;
}

.tesis-card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-4);
  margin: 0;
}

.tesis-card-meta dt {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.tesis-card-meta dd {
  margin: 0;
  font-size: 0.9rem;
}
</style>
