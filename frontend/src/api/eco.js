import request from '../utils/request.js'

export const ecoApi = {
  globalStats() {
    return request.get('/eco/global')
  },
  personalStats() {
    return request.get('/eco/personal')
  }
}
