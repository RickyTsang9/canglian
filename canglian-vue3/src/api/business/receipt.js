import request from '@/utils/request'

export function listReceipt(query) {
  return request({ url: '/business/receipt/list', method: 'get', params: query })
}

export function getReceipt(receiptId) {
  return request({ url: '/business/receipt/' + receiptId, method: 'get' })
}

export function addReceipt(data) {
  return request({ url: '/business/receipt', method: 'post', data })
}

export function updateReceipt(data) {
  return request({ url: '/business/receipt', method: 'put', data })
}

export function confirmReceipt(receiptId) {
  return request({ url: '/business/receipt/confirm/' + receiptId, method: 'post' })
}

export function unconfirmReceipt(receiptId) {
  return request({ url: '/business/receipt/unconfirm/' + receiptId, method: 'post' })
}

export function delReceipt(receiptId) {
  return request({ url: '/business/receipt/' + receiptId, method: 'delete' })
}
