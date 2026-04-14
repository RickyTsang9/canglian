import request from '@/utils/request'

// 查询客户档案列表
export function listCustomer(query) {
  return request({
    url: '/business/customer/list',
    method: 'get',
    params: query
  })
}

// 查询客户档案详细
export function getCustomer(customerId) {
  return request({
    url: '/business/customer/' + customerId,
    method: 'get'
  })
}

// 新增客户档案
export function addCustomer(data) {
  return request({
    url: '/business/customer',
    method: 'post',
    data: data
  })
}

// 修改客户档案
export function updateCustomer(data) {
  return request({
    url: '/business/customer',
    method: 'put',
    data: data
  })
}

// 删除客户档案
export function delCustomer(customerId) {
  return request({
    url: '/business/customer/' + customerId,
    method: 'delete'
  })
}
