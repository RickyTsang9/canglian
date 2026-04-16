import request from '@/utils/request'

// 查询应付单列表
export function listPayable(query) {
  return request({
    url: '/business/payable/list',
    method: 'get',
    params: query
  })
}

// 查询应付单详细
export function getPayable(payableId) {
  return request({
    url: '/business/payable/' + payableId,
    method: 'get'
  })
}

// 新增应付单
export function addPayable(data) {
  return request({
    url: '/business/payable',
    method: 'post',
    data: data
  })
}

// 修改应付单
export function updatePayable(data) {
  return request({
    url: '/business/payable',
    method: 'put',
    data: data
  })
}

// 审核应付单
export function auditPayable(payableId) {
  return request({
    url: '/business/payable/audit/' + payableId,
    method: 'put'
  })
}

// 删除应付单
export function delPayable(payableId) {
  return request({
    url: '/business/payable/' + payableId,
    method: 'delete'
  })
}
