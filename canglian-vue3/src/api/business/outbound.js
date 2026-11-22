import request from '@/utils/request'

export function listOutbound(query) {
  return request({ url: '/business/outbound/list', method: 'get', params: query })
}

export function getOutbound(outboundId) {
  return request({ url: '/business/outbound/' + outboundId, method: 'get' })
}

export function addOutbound(data) {
  return request({ url: '/business/outbound', method: 'post', data })
}

export function updateOutbound(data) {
  return request({ url: '/business/outbound', method: 'put', data })
}

export function auditOutbound(outboundId) {
  return request({ url: '/business/outbound/audit/' + outboundId, method: 'put' })
}

export function unauditOutbound(outboundId) {
  return request({ url: '/business/outbound/unaudit/' + outboundId, method: 'put' })
}

export function reverseOutbound(outboundId) {
  return request({ url: '/business/outbound/reverse/' + outboundId, method: 'post' })
}

export function printOutbound(outboundId) {
  return request({ url: '/business/outbound/print/' + outboundId, method: 'get' })
}

export function delOutbound(outboundId) {
  return request({ url: '/business/outbound/' + outboundId, method: 'delete' })
}

export function outboundShip(outboundId, data) {
  return request({ url: '/business/outbound/ship/' + outboundId, method: 'put', data })
}

export function outboundSign(outboundId) {
  return request({ url: '/business/outbound/sign/' + outboundId, method: 'put' })
}

export function outboundReturn(outboundId, params) {
  return request({ url: '/business/outbound/return/' + outboundId, method: 'put', params })
}
