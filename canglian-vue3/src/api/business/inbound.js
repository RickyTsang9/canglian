import request from '@/utils/request'

export function listInbound(query) {
  return request({ url: '/business/inbound/list', method: 'get', params: query })
}

export function getInbound(inboundId) {
  return request({ url: '/business/inbound/' + inboundId, method: 'get' })
}

export function addInbound(data) {
  return request({ url: '/business/inbound', method: 'post', data })
}

export function updateInbound(data) {
  return request({ url: '/business/inbound', method: 'put', data })
}

export function auditInbound(inboundId) {
  return request({ url: '/business/inbound/audit/' + inboundId, method: 'put' })
}

export function unauditInbound(inboundId) {
  return request({ url: '/business/inbound/unaudit/' + inboundId, method: 'put' })
}

export function reverseInbound(inboundId) {
  return request({ url: '/business/inbound/reverse/' + inboundId, method: 'post' })
}

export function printInbound(inboundId) {
  return request({ url: '/business/inbound/print/' + inboundId, method: 'get' })
}

export function delInbound(inboundId) {
  return request({ url: '/business/inbound/' + inboundId, method: 'delete' })
}
