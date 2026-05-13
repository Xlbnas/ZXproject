import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动带上token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器：统一处理错误
request.interceptors.response.use(response => {
  const data = response.data
  // 后端返回401，跳转登录
  if (data.code === 401) {
    ElMessage.error('请先登录')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    window.location.href = '/login'
    return Promise.reject(data)
  }
  return data
}, error => {
  if (error.response) {
    const status = error.response.status
    if (status === 401) {
      ElMessage.error('请先登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
    } else if (status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error('服务器异常，请稍后重试')
    }
  } else {
    ElMessage.error('网络连接失败，请检查网络')
  }
  return Promise.reject(error)
})

export default request
