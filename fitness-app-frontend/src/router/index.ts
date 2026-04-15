import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: '/pages/home/index'
  },
  {
    path: '/pages/home/index',
    name: 'Home',
    component: () => import('../pages/home/index.vue')
  },
  {
    path: '/pages/template/list',
    name: 'TemplateList',
    component: () => import('../pages/template/list.vue')
  },
  {
    path: '/pages/template/detail',
    name: 'TemplateDetail',
    component: () => import('../pages/template/detail.vue')
  },
  {
    path: '/pages/ai/index',
    name: 'AI',
    component: () => import('../pages/ai/index.vue')
  },
  {
    path: '/pages/mine/index',
    name: 'Mine',
    component: () => import('../pages/mine/index.vue')
  },
  {
    path: '/pages/music/player',
    name: 'MusicPlayer',
    component: () => import('../pages/music/player.vue')
  },
  {
    path: '/pages/workout/track',
    name: 'WorkoutTrack',
    component: () => import('../pages/workout/track.vue')
  },
  {
    path: '/pages/custom/create',
    name: 'CustomPlanCreate',
    component: () => import('../pages/custom/create.vue')
  },
  {
    path: '/pages/records/index',
    name: 'RecordsIndex',
    component: () => import('../pages/records/index.vue')
  },
  // 管理端路由
  {
    path: '/pages/admin/auth',
    name: 'AdminAuth',
    component: () => import('../pages/admin/auth.vue')
  },
  {
    path: '/pages/admin/categories',
    name: 'AdminCategories',
    component: () => import('../pages/admin/categories.vue')
  },
  {
    path: '/pages/admin/exercises',
    name: 'AdminExercises',
    component: () => import('../pages/admin/exercises.vue')
  },
  {
    path: '/pages/admin/users',
    name: 'AdminUsers',
    component: () => import('../pages/admin/users.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
