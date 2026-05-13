<template>
  <div class="admin-layout">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <div class="admin-logo">⚙️ 校园教材管理后台</div>
      <div class="admin-user">
        <span>{{ userStore.userInfo?.realName }}</span>
        <el-button text @click="goHome">返回前台</el-button>
        <el-button text @click="logout">退出</el-button>
      </div>
    </div>

    <div class="admin-body">
      <!-- 左侧菜单 -->
      <div class="admin-menu">
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>用户管理
          </el-menu-item>
          <el-menu-item index="/admin/textbooks">
            <el-icon><Reading /></el-icon>教材管理
          </el-menu-item>
          <el-menu-item index="/admin/transactions">
            <el-icon><List /></el-icon>交易监控
          </el-menu-item>
          <el-menu-item index="/admin/announcements">
            <el-icon><Bell /></el-icon>公告管理
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../../store/user.js'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

function goHome() {
  router.push('/home')
}

function logout() {
  userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; display: flex; flex-direction: column; }
.admin-header {
  background: #303133;
  color: #fff;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.admin-logo { font-size: 16px; font-weight: bold; }
.admin-user { display: flex; align-items: center; gap: 10px; font-size: 14px; }
.admin-body { display: flex; flex: 1; }
.admin-menu { width: 180px; background: #fff; box-shadow: 2px 0 4px rgba(0,0,0,.06); flex-shrink: 0; }
.admin-content { flex: 1; padding: 20px; background: #f5f7fa; overflow: auto; }
</style>
