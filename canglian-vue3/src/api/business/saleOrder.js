import request from '@/utils/request'

export function listSaleOrder(query) {
  return request({ url: '/business/saleOrder/list', method: 'get', params: query })
}

export function getSaleOrder(orderId) {
  return request({ url: '/business/saleOrder/' + orderId, method: 'get' })
}

export function addSaleOrder(data) {
  return request({ url: '/business/saleOrder', method: 'post', data })
}

export function updateSaleOrder(data) {
  return request({ url: '/business/saleOrder', method: 'put', data })
}

export function approveSaleOrder(orderId) {
  return request({ url: '/business/saleOrder/approve/' + orderId, method: 'put' })
}

export function unapproveSaleOrder(orderId) {
  return request({ url: '/business/saleOrder/unapprove/' + orderId, method: 'put' })
}

export function closeSaleOrder(orderId, reason) {
  return request({
    url: '/business/saleOrder/close/' + orderId,
    method: 'put',
    data: { closeReason: reason || '' }
  })
}

export function pushQuoteToSale(orderId) {
  return request({ url: '/business/saleOrder/pushSale/' + orderId, method: 'put' })
}

export function pushSaleOrderToOutbound(orderId, pushItems) {
  return request({
    url: '/business/saleOrder/pushOutbound/' + orderId,
    method: 'put',
    data: pushItems && pushItems.length ? pushItems : undefined
  })
}

export function printSaleOrder(orderId) {
  return request({ url: '/business/saleOrder/print/' + orderId, method: 'get' })
}

export function delSaleOrder(orderId) {
  return request({ url: '/business/saleOrder/' + orderId, method: 'delete' })
}
