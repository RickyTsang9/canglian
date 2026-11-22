import request from '@/utils/request'

export function listPurchaseReturn(query) {
  return request({ url: '/business/purchaseReturn/list', method: 'get', params: query })
}

export function getPurchaseReturn(purchaseReturnId) {
  return request({ url: '/business/purchaseReturn/' + purchaseReturnId, method: 'get' })
}

export function addPurchaseReturn(data) {
  return request({ url: '/business/purchaseReturn', method: 'post', data })
}

export function updatePurchaseReturn(data) {
  return request({ url: '/business/purchaseReturn', method: 'put', data })
}

export function auditPurchaseReturn(purchaseReturnId) {
  return request({ url: '/business/purchaseReturn/audit/' + purchaseReturnId, method: 'put' })
}

export function unauditPurchaseReturn(purchaseReturnId) {
  return request({ url: '/business/purchaseReturn/unaudit/' + purchaseReturnId, method: 'put' })
}

export function reversePurchaseReturn(purchaseReturnId) {
  return request({ url: '/business/purchaseReturn/reverse/' + purchaseReturnId, method: 'post' })
}

export function delPurchaseReturn(purchaseReturnId) {
  return request({ url: '/business/purchaseReturn/' + purchaseReturnId, method: 'delete' })
}
