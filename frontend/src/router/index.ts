import { createRouter, createWebHistory } from 'vue-router';
import { useMatchaStore } from '@/store';
import axios from 'axios';

const routes = [
  {
    path: '/',
    component: () => import('../views/MainView.vue'),
  },
  {
    name: 'Login',
    path: '/login',
    component: () => import('../views/LoginPage.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async(to, from, next) => {
  let store = useMatchaStore();

  console.log(`navigating from ${from.path} to ${to.path}`);
  if (to.name === 'Login') {
    next();
  } else if (!store.isLoggedIn) {
    next({ name: 'Login' });
  } else {
    try {
      const response = await axios.get('/api/auth/self');
      store.user = response.data;
      next();
    } catch {
      store.logout();
      next({ name: 'Login' });
    }
  }
});

export default router;
