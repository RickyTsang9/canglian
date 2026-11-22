import request from '@/utils/request'

export function listReceivable(query) {
  return request({ url: '/business/receivable/list', method: 'get', params: query })
}

export function getReceivable(receivableId) {
  return request({ url: '/business/receivable/' + receivableId, method: 'get' })
}

export function addReceivable(data) {
  return request({ url: '/business/receivable', method: 'post', data })
}

export function updateReceivable(data) {
  return request({ url: '/business/receivable', method: 'put', data })
}

export function auditReceivable(receivableId) {
  return request({ url: '/business/receivable/audit/' + receivableId, method: 'put' })
}

export function unconfirmReceivable(receivableId) {
  return request({ url: '/business/receivable/unconfirm/' + receivableId, method: 'post' })
}

export function delReceivable(receivableId) {
  return request({ url: '/business/receivable/' + receivableId, method: 'delete' })
}

export function offsetReceivable(data) {
  return request({ url: '/business/receivable/offset', method: 'post', data })
}

export function listReceivableOffsetLogs(receivableId) {
  return request({ url: '/business/receivable/offsetLogs', method: 'get', params: { receivableId } })
}

export function unoffsetReceivable(offsetId) {
  return request({ url: '/business/receivable/unoffset/' + offsetId, method: 'post' })
}
