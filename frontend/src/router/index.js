import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 路由懒加载
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const Home = () => import('../views/Home.vue')
const TextbookList = () => import('../views/TextbookList.vue')
const TextbookDetail = () => import('../views/TextbookDetail.vue')
const PublishTextbook = () => import('../views/PublishTextbook.vue')
const MyOrders = () => import('../views/MyOrders.vue')
const MyReviews = () => import('../views/MyReviews.vue')
const MyFavorites = () => import('../views/MyFavorites.vue')
const MyProfile = () => import('../views/MyProfile.vue')

// 管理员页面
const AdminLayout = () => import('../views/admin/AdminLayout.vue')
const UserManage = () => import('../views/admin/UserManage.vue')
const TextbookManage = () => import('../views/admin/TextbookManage.vue')
const TransactionMonitor = () => import('../views/admin/TransactionMonitor.vue')
const AnnouncementManage = () => import('../views/admin/AnnouncementManage.vue')

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/home', component: Home },
  { path: '/textbook/list', component: TextbookList },
  { path: '/textbook/detail/:id', component: TextbookDetail },
  // 需要登录的页面
  {
    path: '/textbook/publish',
    component: PublishTextbook,
    meta: { requiresAuth: true }
  },
  {
    path: '/my/orders',
    component: MyOrders,
    meta: { requiresAuth: true }
  },
  {
    path: '/my/reviews',
    component: MyReviews,
    meta: { requiresAuth: true }
  },
  {
    path: '/my/favorites',
    component: MyFavorites,
    meta: { requiresAuth: true }
  },
  {
    path: '/my/profile',
    component: MyProfile,
    meta: { requiresAuth: true }
  },
  // 管理员页面（需要管理员权限，TC-ADMIN-010）
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/users' },
      { path: 'users', component: UserManage },
      { path: 'textbooks', component: TextbookManage },
      { path: 'transactions', component: TransactionMonitor },
      { path: 'announcements', component: AnnouncementManage }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫（TC-ADMIN-010：普通用户访问管理页面拒绝）
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }
  if (to.meta.requiresAdmin) {
    if (!userInfo || userInfo.userType !== 2) {
      ElMessage.error('无权限访问')
      next('/home')
      return
    }
  }
  next()
})

export default router
