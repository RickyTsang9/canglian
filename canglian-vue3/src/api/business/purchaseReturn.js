import request from '@/utils/request'

// 查询采购退货列表
export function listPurchaseReturn(query) {
  return request({
    url: '/business/purchaseReturn/list',
    method: 'get',
    params: query
  })
}

// 查询采购退货详细
export function getPurchaseReturn(purchaseReturnId) {
  return request({
    url: '/business/purchaseReturn/' + purchaseReturnId,
    method: 'get'
  })
}

// 新增采购退货
export function addPurchaseReturn(data) {
  return request({
    url: '/business/purchaseReturn',
    method: 'post',
    data: data
  })
}

// 修改采购退货
export function updatePurchaseReturn(data) {
  return request({
    url: '/business/purchaseReturn',
    method: 'put',
    data: data
  })
}

// 采购退货审核
export function auditPurchaseReturn(purchaseReturnId) {
  return request({
    url: '/business/purchaseReturn/audit/' + purchaseReturnId,
    method: 'put'
  })
}

// 删除采购退货
export function delPurchaseReturn(purchaseReturnId) {
  return request({
    url: '/business/purchaseReturn/' + purchaseReturnId,
    method: 'delete'
  })
}
