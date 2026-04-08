import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/home/Index.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/forum/:forumId/topics',
    name: 'forum-topics',
    component: () => import('@/views/forum/TopicList.vue')
  },
  {
    path: '/topic/create',
    name: 'topic-create',
    component: () => import('@/views/topic/Create.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/topic/:topicId',
    name: 'topic-detail',
    component: () => import('@/views/topic/Detail.vue')
  },
  {
    path: '/topic/:topicId/edit',
    name: 'topic-edit',
    component: () => import('@/views/topic/Edit.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'messages',
    component: () => import('@/views/user/Messages.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('@/views/admin/Index.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: () => import('@/views/admin/Users.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/forums',
    name: 'admin-forums',
    component: () => import('@/views/admin/Forums.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 需要登录的路由
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限的路由
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'home' })
    return
  }
  
  next()
})

export default router