import request from '../utils/request.js'

export const favoriteApi = {
  toggle(bookId) {
    return request.post('/favorite/toggle', { bookId })
  },
  myFavorites(params) {
    return request.get('/favorite/my', { params })
  },
  check(bookId) {
    return request.get('/favorite/check', { params: { bookId } })
  }
}
