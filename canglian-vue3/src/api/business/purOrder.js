import request from '@/utils/request'

// 查询购货订单列表
export function listPurOrder(query) {
  return request({
    url: '/business/purOrder/list',
    method: 'get',
    params: query
  })
}

// 查询购货订单详细
export function getPurOrder(purchaseOrderId) {
  return request({
    url: '/business/purOrder/' + purchaseOrderId,
    method: 'get'
  })
}

// 新增购货订单
export function addPurOrder(data) {
  return request({
    url: '/business/purOrder',
    method: 'post',
    data: data
  })
}

// 修改购货订单
export function updatePurOrder(data) {
  return request({
    url: '/business/purOrder',
    method: 'put',
    data: data
  })
}

// 审批购货订单
export function approvePurOrder(purchaseOrderId) {
  return request({
    url: '/business/purOrder/approve/' + purchaseOrderId,
    method: 'put'
  })
}

// 购货订单下推入库单
export function pushPurOrderToInbound(purchaseOrderId) {
  return request({
    url: '/business/purOrder/pushInbound/' + purchaseOrderId,
    method: 'put'
  })
}

// 查询购货订单打印数据
export function printPurOrder(purchaseOrderId) {
  return request({
    url: '/business/purOrder/print/' + purchaseOrderId,
    method: 'get'
  })
}

// 删除购货订单
export function delPurOrder(purchaseOrderId) {
  return request({
    url: '/business/purOrder/' + purchaseOrderId,
    method: 'delete'
  })
}
