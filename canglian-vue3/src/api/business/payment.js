import request from '@/utils/request'

export function listPayment(query) {
  return request({ url: '/business/payment/list', method: 'get', params: query })
}

export function getPayment(paymentId) {
  return request({ url: '/business/payment/' + paymentId, method: 'get' })
}

export function addPayment(data) {
  return request({ url: '/business/payment', method: 'post', data })
}

export function updatePayment(data) {
  return request({ url: '/business/payment', method: 'put', data })
}

export function confirmPayment(paymentId) {
  return request({ url: '/business/payment/confirm/' + paymentId, method: 'post' })
}

export function unconfirmPayment(paymentId) {
  return request({ url: '/business/payment/unconfirm/' + paymentId, method: 'post' })
}

export function delPayment(paymentId) {
  return request({ url: '/business/payment/' + paymentId, method: 'delete' })
}
