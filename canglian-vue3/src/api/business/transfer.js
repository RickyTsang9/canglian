import request from '@/utils/request'

export function listTransfer(query) {
  return request({ url: '/business/transfer/list', method: 'get', params: query })
}

export function getTransfer(transferId) {
  return request({ url: '/business/transfer/' + transferId, method: 'get' })
}

export function addTransfer(data) {
  return request({ url: '/business/transfer', method: 'post', data })
}

export function updateTransfer(data) {
  return request({ url: '/business/transfer', method: 'put', data })
}

export function auditTransfer(transferId) {
  return request({ url: '/business/transfer/audit/' + transferId, method: 'put' })
}

export function unauditTransfer(transferId) {
  return request({ url: '/business/transfer/unaudit/' + transferId, method: 'put' })
}

export function reverseTransfer(transferId) {
  return request({ url: '/business/transfer/reverse/' + transferId, method: 'post' })
}

export function printTransfer(transferId) {
  return request({ url: '/business/transfer/print/' + transferId, method: 'get' })
}

export function delTransfer(transferId) {
  return request({ url: '/business/transfer/' + transferId, method: 'delete' })
}
