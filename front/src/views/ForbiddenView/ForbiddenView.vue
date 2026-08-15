<script setup></script>

<template>
  <div class="container status-page">
    <h1>403 · No autorizado</h1>
    <p>No tienes permisos para acceder a esta página.</p>
    <RouterLink to="/">Volver al inicio</RouterLink>
  </div>
</template>

<style scoped>
.status-page {
  text-align: center;
  padding-top: var(--spacing-5);
}
</style>
