import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView/LoginView.vue'),
    meta: { public: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/RegisterView/RegisterView.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    name: 'tesis-list',
    component: () => import('@/views/TesisListView/TesisListView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/tesis/:id',
    name: 'tesis-detail',
    component: () => import('@/views/TesisDetailView/TesisDetailView.vue'),
    meta: { requiresAuth: true },
    props: true,
  },
  {
    path: '/403',
    name: 'forbidden',
    component: () => import('@/views/ForbiddenView/ForbiddenView.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView/NotFoundView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const { isAuthenticated } = useAuth()

  if (to.meta.requiresAuth && !isAuthenticated.value) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.public && isAuthenticated.value) {
    return { name: 'tesis-list' }
  }

  return true
})

export default router
