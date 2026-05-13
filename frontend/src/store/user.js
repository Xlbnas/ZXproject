import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 用户状态管理
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)
  // 是否是管理员
  const isAdmin = computed(() => userInfo.value && userInfo.value.userType === 2)

  function setUser(tokenVal, info) {
    token.value = tokenVal
    userInfo.value = info
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, isAdmin, setUser, logout }
})
