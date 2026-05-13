import request from '../utils/request.js'

export const adminApi = {
  // 用户管理
  userList(params) {
    return request.get('/admin/user/list', { params })
  },
  updateUserType(data) {
    return request.put('/admin/user/type', data)
  },
  updateUserStatus(data) {
    return request.put('/admin/user/status', data)
  },

  // 教材管理
  textbookList(params) {
    return request.get('/admin/textbook/list', { params })
  },
  auditTextbook(data) {
    return request.put('/admin/textbook/audit', data)
  },
  offShelf(id) {
    return request.put(`/admin/textbook/offshelf/${id}`)
  },

  // 交易监控
  transactionList(params) {
    return request.get('/admin/transaction/list', { params })
  },
  cancelTransaction(id, reason) {
    return request.put(`/admin/transaction/cancel/${id}`, { reason })
  },
  markDispute(id) {
    return request.put(`/admin/transaction/dispute/${id}`)
  },

  // 公告管理
  announcementList(params) {
    return request.get('/admin/announcement/list', { params })
  },
  publishAnnouncement(data) {
    return request.post('/admin/announcement/publish', data)
  },
  updateAnnouncement(id, data) {
    return request.put(`/admin/announcement/update/${id}`, data)
  },
  deleteAnnouncement(id) {
    return request.delete(`/admin/announcement/delete/${id}`)
  }
}
