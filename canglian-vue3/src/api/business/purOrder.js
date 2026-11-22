import request from '@/utils/request'

export function listPurOrder(query) {
  return request({ url: '/business/purOrder/list', method: 'get', params: query })
}

export function getPurOrder(purchaseOrderId) {
  return request({ url: '/business/purOrder/' + purchaseOrderId, method: 'get' })
}

export function addPurOrder(data) {
  return request({ url: '/business/purOrder', method: 'post', data })
}

export function updatePurOrder(data) {
  return request({ url: '/business/purOrder', method: 'put', data })
}

export function approvePurOrder(purchaseOrderId) {
  return request({ url: '/business/purOrder/approve/' + purchaseOrderId, method: 'put' })
}

export function unapprovePurOrder(purchaseOrderId) {
  return request({ url: '/business/purOrder/unapprove/' + purchaseOrderId, method: 'put' })
}

export function closePurOrder(purchaseOrderId, reason) {
  return request({
    url: '/business/purOrder/close/' + purchaseOrderId,
    method: 'put',
    data: { closeReason: reason || '' }
  })
}

export function pushPurOrderToInbound(purchaseOrderId, pushItems) {
  return request({
    url: '/business/purOrder/pushInbound/' + purchaseOrderId,
    method: 'put',
    data: pushItems && pushItems.length ? pushItems : undefined
  })
}

export function printPurOrder(purchaseOrderId) {
  return request({ url: '/business/purOrder/print/' + purchaseOrderId, method: 'get' })
}

export function delPurOrder(purchaseOrderId) {
  return request({ url: '/business/purOrder/' + purchaseOrderId, method: 'delete' })
}
