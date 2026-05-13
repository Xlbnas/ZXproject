import request from '../utils/request.js'

export const userApi = {
  // 注册
  register(data) {
    return request.post('/user/register', data)
  },
  // 登录
  login(data) {
    return request.post('/user/login', data)
  },
  // 获取当前用户信息
  getInfo() {
    return request.get('/user/info')
  },
  // 修改个人信息
  updateProfile(data) {
    return request.put('/user/update', data)
  },
  // 修改密码
  changePassword(data) {
    return request.put('/user/password', data)
  }
}
