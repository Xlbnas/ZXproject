import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../api/user.js'

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

  /**
   * 从后端拉取最新用户信息，合并进 store（解决：登录时缓存的 realName 乱码 / 过期）
   * @returns {Promise<object|null>} 接口返回的用户对象，失败时 null
   */
  async function refreshFromServer() {
    if (!token.value) return null
    try {
      const res = await userApi.getInfo()
      if (res.code !== 200 || !res.data) return null
      const u = res.data
      const prev = userInfo.value || {}
      const merged = {
        ...prev,
        userId: u.id ?? prev.userId,
        username: u.username ?? prev.username,
        realName: u.realName ?? prev.realName,
        userType: u.userType ?? prev.userType
      }
      userInfo.value = merged
      localStorage.setItem('userInfo', JSON.stringify(merged))
      return u
    } catch (e) {
      return null
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, isAdmin, setUser, logout, refreshFromServer }
})
