import request from '@/utils/request'

export function listSaleReturn(query) {
  return request({ url: '/business/saleReturn/list', method: 'get', params: query })
}

export function getSaleReturn(saleReturnId) {
  return request({ url: '/business/saleReturn/' + saleReturnId, method: 'get' })
}

export function addSaleReturn(data) {
  return request({ url: '/business/saleReturn', method: 'post', data })
}

export function updateSaleReturn(data) {
  return request({ url: '/business/saleReturn', method: 'put', data })
}

export function auditSaleReturn(saleReturnId) {
  return request({ url: '/business/saleReturn/audit/' + saleReturnId, method: 'put' })
}

export function unauditSaleReturn(saleReturnId) {
  return request({ url: '/business/saleReturn/unaudit/' + saleReturnId, method: 'put' })
}

export function reverseSaleReturn(saleReturnId) {
  return request({ url: '/business/saleReturn/reverse/' + saleReturnId, method: 'post' })
}

export function delSaleReturn(saleReturnId) {
  return request({ url: '/business/saleReturn/' + saleReturnId, method: 'delete' })
}
