import request from '../utils/request.js'

export const textbookApi = {
  // 教材列表/检索
  list(params) {
    return request.get('/textbook/list', { params })
  },
  // 教材详情
  detail(id) {
    return request.get(`/textbook/detail/${id}`)
  },
  // 发布教材
  publish(data) {
    return request.post('/textbook/publish', data)
  },
  // 上传图片
  uploadImage(formData) {
    return request.post('/textbook/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  // 我发布的教材
  myPublished() {
    return request.get('/textbook/my')
  },
  // 用户下架
  offShelf(id) {
    return request.put(`/textbook/offshelf/${id}`)
  }
}
