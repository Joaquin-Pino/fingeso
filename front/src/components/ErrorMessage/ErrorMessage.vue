<script setup>
defineProps({
  message: { type: String, required: true },
})
</script>

<template>
  <div class="error-message" role="alert">
    {{ message }}
  </div>
</template>

<style scoped>
.error-message {
  background: var(--color-danger-bg);
  color: var(--color-danger);
  border: 1px solid var(--color-danger);
  border-radius: var(--radius);
  padding: var(--spacing-2) var(--spacing-3);
  margin-bottom: var(--spacing-3);
}
</style>
