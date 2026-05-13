<template>
  <div class="login-page">
    <div class="login-box">
      <h2>校园教材循环利用平台</h2>
      <p class="sub-title">登录账号</p>

      <el-form :model="form" ref="formRef" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名/学号" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"
                    prefix-icon="Lock" size="large" show-password
                    @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading"
                     @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="bottom-links">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
        <span style="margin-left:10px; color:#999; font-size:12px">
          默认管理员：admin / admin123
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '../api/user.js'
import { useUserStore } from '../store/user.js'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '学号和密码不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '学号和密码不能为空', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userApi.login({ username: form.username, password: form.password })
    if (res.code === 200) {
      const data = res.data
      userStore.setUser(data.token, {
        userId: data.userId,
        username: data.username,
        realName: data.realName,
        userType: data.userType
      })
      ElMessage.success('登录成功')
      // 管理员跳管理后台，普通用户跳首页
      if (data.userType === 2) {
        router.push('/admin')
      } else {
        router.push('/home')
      }
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff22, #f5f7fa);
}
.login-box {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 20px rgba(0,0,0,.1);
  width: 380px;
}
.login-box h2 {
  text-align: center;
  color: #409eff;
  margin-bottom: 8px;
  font-size: 20px;
}
.sub-title {
  text-align: center;
  color: #999;
  margin-bottom: 24px;
  font-size: 14px;
}
.bottom-links {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}
.bottom-links a {
  color: #409eff;
  text-decoration: none;
}
</style>
