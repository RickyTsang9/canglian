import request from '@/utils/request'

// 查询出库单列表
export function listOutbound(query) {
  return request({
    url: '/business/outbound/list',
    method: 'get',
    params: query
  })
}

// 查询出库单详细
export function getOutbound(outboundId) {
  return request({
    url: '/business/outbound/' + outboundId,
    method: 'get'
  })
}

// 新增出库单
export function addOutbound(data) {
  return request({
    url: '/business/outbound',
    method: 'post',
    data: data
  })
}

// 修改出库单
export function updateOutbound(data) {
  return request({
    url: '/business/outbound',
    method: 'put',
    data: data
  })
}

export function auditOutbound(outboundId) {
  return request({
    url: '/business/outbound/audit/' + outboundId,
    method: 'put'
  })
}

export function printOutbound(outboundId) {
  return request({
    url: '/business/outbound/print/' + outboundId,
    method: 'get'
  })
}

// 删除出库单
export function delOutbound(outboundId) {
  return request({
    url: '/business/outbound/' + outboundId,
    method: 'delete'
  })
}

// 发货
export function outboundShip(outboundId, data) {
  return request({
    url: '/business/outbound/ship/' + outboundId,
    method: 'put',
    data: data
  })
}

// 签收
export function outboundSign(outboundId) {
  return request({
    url: '/business/outbound/sign/' + outboundId,
    method: 'put'
  })
}

// 退货（可传退款金额与应收id）
export function outboundReturn(outboundId, params) {
  return request({
    url: '/business/outbound/return/' + outboundId,
    method: 'put',
    params: params
  })
}
