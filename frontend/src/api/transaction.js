import request from '../utils/request.js'

export const transactionApi = {
  // 发起交易
  create(bookId) {
    return request.post('/transaction/create', { bookId })
  },
  // 确认交易
  confirm(id) {
    return request.put(`/transaction/confirm/${id}`)
  },
  // 我的订单
  myOrders(params) {
    return request.get('/transaction/my', { params })
  },
  // 取消订单
  cancel(id) {
    return request.put(`/transaction/cancel/${id}`)
  },
  // 确认完成
  complete(id) {
    return request.put(`/transaction/complete/${id}`)
  }
}
