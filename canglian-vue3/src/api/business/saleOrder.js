import request from '@/utils/request'

// 查询销售单据列表
export function listSaleOrder(query) {
  return request({
    url: '/business/saleOrder/list',
    method: 'get',
    params: query
  })
}

// 查询销售单据详细
export function getSaleOrder(orderId) {
  return request({
    url: '/business/saleOrder/' + orderId,
    method: 'get'
  })
}

// 新增销售单据
export function addSaleOrder(data) {
  return request({
    url: '/business/saleOrder',
    method: 'post',
    data: data
  })
}

// 修改销售单据
export function updateSaleOrder(data) {
  return request({
    url: '/business/saleOrder',
    method: 'put',
    data: data
  })
}

// 审批销售单据
export function approveSaleOrder(orderId) {
  return request({
    url: '/business/saleOrder/approve/' + orderId,
    method: 'put'
  })
}

// 报价下推销货订单
export function pushQuoteToSale(orderId) {
  return request({
    url: '/business/saleOrder/pushSale/' + orderId,
    method: 'put'
  })
}

// 销货订单下推出库单
export function pushSaleOrderToOutbound(orderId) {
  return request({
    url: '/business/saleOrder/pushOutbound/' + orderId,
    method: 'put'
  })
}

// 查询销售单据打印数据
export function printSaleOrder(orderId) {
  return request({
    url: '/business/saleOrder/print/' + orderId,
    method: 'get'
  })
}

// 删除销售单据
export function delSaleOrder(orderId) {
  return request({
    url: '/business/saleOrder/' + orderId,
    method: 'delete'
  })
}
