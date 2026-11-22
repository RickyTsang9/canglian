import request from '@/utils/request'

export function listStock(query) {
  return request({ url: '/business/stock/list', method: 'get', params: query })
}

export function listStockWarning(query) {
  return request({ url: '/business/stock/warning', method: 'get', params: query })
}

export function listExpiryWarning(query) {
  return request({ url: '/business/stock/expiryWarning', method: 'get', params: query })
}

export function listFefoBatches(params) {
  return request({ url: '/business/stock/fefo', method: 'get', params })
}

export function freezeStock(data) {
  return request({ url: '/business/stock/freeze', method: 'post', data })
}

export function unfreezeStock(data) {
  return request({ url: '/business/stock/unfreeze', method: 'post', data })
}

export function getStock(stockId) {
  return request({ url: '/business/stock/' + stockId, method: 'get' })
}

export function addStock(data) {
  return request({ url: '/business/stock', method: 'post', data })
}

export function updateStock(data) {
  return request({ url: '/business/stock', method: 'put', data })
}

export function delStock(stockId) {
  return request({ url: '/business/stock/' + stockId, method: 'delete' })
}
