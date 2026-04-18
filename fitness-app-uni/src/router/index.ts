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
    path: '/pages/mine/settings',
    name: 'Settings',
    component: () => import('../pages/mine/settings.vue')
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
  {
    path: '/pages/plans/index',
    name: 'PlansIndex',
    component: () => import('../pages/plans/index.vue')
  },
  // 用户登录路由
  {
    path: '/auth/login',
    name: 'UserLogin',
    component: () => import('../pages/auth/login.vue')
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

// 路由导航守卫，检查登录状态
router.beforeEach((to, from, next) => {
  // 需要登录的页面路径
  const requiresAuth = [
    '/pages/workout/track',
    '/pages/home/index',
    '/pages/ai/index',
    '/pages/mine/index',
    '/pages/mine/settings',
    '/pages/records/index',
    '/pages/plans/index'
  ];
  
  // 检查当前路径是否需要登录
  const isAuthRequired = requiresAuth.some(path => to.path.startsWith(path));
  
  // 检查是否已登录（有token）
  const isLoggedIn = localStorage.getItem('token') !== null;
  
  if (isAuthRequired && !isLoggedIn) {
    // 未登录，重定向到登录页面
    next('/auth/login');
  } else {
    // 已登录或不需要登录，继续导航
    next();
  }
});

export default router;