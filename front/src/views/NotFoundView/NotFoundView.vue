<script setup></script>

<template>
  <div class="container status-page">
    <h1>404 · Página no encontrada</h1>
    <p>La página que buscas no existe.</p>
    <RouterLink to="/">Volver al inicio</RouterLink>
  </div>
</template>

<style scoped>
.status-page {
  text-align: center;
  padding-top: var(--spacing-5);
}
</style>
