import request from '@/utils/request'

export function listPayable(query) {
  return request({ url: '/business/payable/list', method: 'get', params: query })
}

export function getPayable(payableId) {
  return request({ url: '/business/payable/' + payableId, method: 'get' })
}

export function addPayable(data) {
  return request({ url: '/business/payable', method: 'post', data })
}

export function updatePayable(data) {
  return request({ url: '/business/payable', method: 'put', data })
}

export function auditPayable(payableId) {
  return request({ url: '/business/payable/audit/' + payableId, method: 'put' })
}

export function unconfirmPayable(payableId) {
  return request({ url: '/business/payable/unconfirm/' + payableId, method: 'post' })
}

export function delPayable(payableId) {
  return request({ url: '/business/payable/' + payableId, method: 'delete' })
}

export function offsetPayable(data) {
  return request({ url: '/business/payable/offset', method: 'post', data })
}

export function listPayableOffsetLogs(payableId) {
  return request({ url: '/business/payable/offsetLogs', method: 'get', params: { payableId } })
}

export function unoffsetPayable(offsetId) {
  return request({ url: '/business/payable/unoffset/' + offsetId, method: 'post' })
}
