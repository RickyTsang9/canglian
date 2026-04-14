import request from '@/utils/request'

// 查询入库单列表
export function listInbound(query) {
  return request({
    url: '/business/inbound/list',
    method: 'get',
    params: query
  })
}

// 查询入库单详细
export function getInbound(inboundId) {
  return request({
    url: '/business/inbound/' + inboundId,
    method: 'get'
  })
}

// 新增入库单
export function addInbound(data) {
  return request({
    url: '/business/inbound',
    method: 'post',
    data: data
  })
}

// 修改入库单
export function updateInbound(data) {
  return request({
    url: '/business/inbound',
    method: 'put',
    data: data
  })
}

export function auditInbound(inboundId) {
  return request({
    url: '/business/inbound/audit/' + inboundId,
    method: 'put'
  })
}

export function printInbound(inboundId) {
  return request({
    url: '/business/inbound/print/' + inboundId,
    method: 'get'
  })
}

// 删除入库单
export function delInbound(inboundId) {
  return request({
    url: '/business/inbound/' + inboundId,
    method: 'delete'
  })
}
