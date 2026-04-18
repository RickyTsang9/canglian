import request from '@/utils/request'

// 查询客户专属价列表
export function listCustomerSpecialPrice(query) {
  return request({
    url: '/business/customerSpecialPrice/list',
    method: 'get',
    params: query
  })
}

// 查询客户专属价详细
export function getCustomerSpecialPrice(specialPriceId) {
  return request({
    url: '/business/customerSpecialPrice/' + specialPriceId,
    method: 'get'
  })
}

// 新增客户专属价
export function addCustomerSpecialPrice(data) {
  return request({
    url: '/business/customerSpecialPrice',
    method: 'post',
    data: data
  })
}

// 修改客户专属价
export function updateCustomerSpecialPrice(data) {
  return request({
    url: '/business/customerSpecialPrice',
    method: 'put',
    data: data
  })
}

// 删除客户专属价
export function delCustomerSpecialPrice(specialPriceId) {
  return request({
    url: '/business/customerSpecialPrice/' + specialPriceId,
    method: 'delete'
  })
}
