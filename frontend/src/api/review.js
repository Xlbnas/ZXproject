import request from '../utils/request.js'

export const reviewApi = {
  // 发布评价
  add(data) {
    return request.post('/review/add', data)
  },
  // 我发出的评价
  mySent(params) {
    return request.get('/review/my-sent', { params })
  },
  // 我收到的评价
  myReceived(params) {
    return request.get('/review/my-received', { params })
  },
  // 修改评价
  update(id, data) {
    return request.put(`/review/update/${id}`, data)
  },
  // 删除评价
  delete(id) {
    return request.delete(`/review/delete/${id}`)
  }
}
