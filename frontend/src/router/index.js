import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/admin'
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      redirect: '/admin/article',
      children: [
        {
          path: 'article',
          name: 'ArticleList',
          component: () => import('@/views/admin/article/ArticleList.vue'),
          meta: { title: '文章管理', requiresAuth: true }
        },
        {
          path: 'article/edit/:id?',
          name: 'ArticleEdit',
          component: () => import('@/views/admin/article/ArticleEdit.vue'),
          meta: { title: '编辑文章', requiresAuth: true }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/admin'
    }
  ]
})

export default router 