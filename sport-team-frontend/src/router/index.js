import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/Register.vue'),
  },
  {
    path: '/',
    name: 'MainLayout',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'ActivitySquare',
        component: () => import('@/pages/ActivitySquare.vue'),
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/pages/ActivityDetail.vue'),
      },
      {
        path: 'activity/audit/:id',
        name: 'ActivityAudit',
        component: () => import('@/pages/ActivityAudit.vue'),
      },
      {
        path: 'my-history',
        name: 'MyHistory',
        component: () => import('@/pages/MyHistory.vue'),
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/pages/Profile.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  userStore.loadUser()

  const publicRoutes = ['/login', '/register']
  const isPublicRoute = publicRoutes.includes(to.path)

  if (!userStore.isLoggedIn() && !isPublicRoute) {
    next('/login')
  } else if (userStore.isLoggedIn() && isPublicRoute) {
    next('/')
  } else {
    next()
  }
})

export default router
