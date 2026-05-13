import request from '../utils/request.js'

export const announcementApi = {
  list(params) {
    return request.get('/announcement/list', { params })
  }
}
