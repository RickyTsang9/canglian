import request from '@/utils/request'

// 查询销售退货列表
export function listSaleReturn(query) {
  return request({
    url: '/business/saleReturn/list',
    method: 'get',
    params: query
  })
}

// 查询销售退货详细
export function getSaleReturn(saleReturnId) {
  return request({
    url: '/business/saleReturn/' + saleReturnId,
    method: 'get'
  })
}

// 新增销售退货
export function addSaleReturn(data) {
  return request({
    url: '/business/saleReturn',
    method: 'post',
    data: data
  })
}

// 修改销售退货
export function updateSaleReturn(data) {
  return request({
    url: '/business/saleReturn',
    method: 'put',
    data: data
  })
}

// 销售退货审核
export function auditSaleReturn(saleReturnId) {
  return request({
    url: '/business/saleReturn/audit/' + saleReturnId,
    method: 'put'
  })
}

// 删除销售退货
export function delSaleReturn(saleReturnId) {
  return request({
    url: '/business/saleReturn/' + saleReturnId,
    method: 'delete'
  })
}
