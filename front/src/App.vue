<script setup>
import { onMounted, onUnmounted } from 'vue'
import AppNavbar from '@/components/AppNavbar/AppNavbar.vue'
import { useIdleTimer } from '@/composables/useIdleTimer'
import { useRouter } from 'vue-router'

const router = useRouter()
const idleTimer = useIdleTimer(() => {
  router.push({ name: 'login', query: { reason: 'expired' } })
})

onMounted(() => idleTimer.start())
onUnmounted(() => idleTimer.stop())
</script>

<template>
  <AppNavbar />
  <RouterView />
</template>
