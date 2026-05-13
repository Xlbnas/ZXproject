<template>
  <div class="header">
    <div class="header-inner">
      <div class="logo" @click="$router.push('/home')">
        📚 校园教材循环利用平台
      </div>
      <nav class="nav-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/textbook/list">教材列表</router-link>
        <template v-if="userStore.isLoggedIn">
          <router-link to="/textbook/publish">发布教材</router-link>
          <router-link to="/my/orders">我的订单</router-link>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userStore.userInfo?.realName || userStore.userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="reviews">我的评价</el-dropdown-item>
                <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.isAdmin" divided>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../store/user.js'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

function handleCommand(cmd) {
  if (cmd === 'profile') {
    router.push('/my/profile')
  } else if (cmd === 'reviews') {
    router.push('/my/reviews')
  } else if (cmd === 'favorites') {
    router.push('/my/favorites')
  } else if (cmd === 'admin') {
    router.push('/admin')
  } else if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/home')
  }
}
</script>

<style scoped>
.header {
  background-color: #409eff;
  color: #fff;
  padding: 0 20px;
  height: 56px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,.12);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  color: #fff;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}
.nav-links a {
  color: #fff;
  text-decoration: none;
  font-size: 14px;
}
.nav-links a:hover, .nav-links a.router-link-active {
  text-decoration: underline;
}
.el-dropdown-link {
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
