import request from '@/utils/request'

// 查询客户等级价列表
export function listCustomerLevelPrice(query) {
  return request({
    url: '/business/customerLevelPrice/list',
    method: 'get',
    params: query
  })
}

// 查询客户等级价详细
export function getCustomerLevelPrice(levelPriceId) {
  return request({
    url: '/business/customerLevelPrice/' + levelPriceId,
    method: 'get'
  })
}

// 新增客户等级价
export function addCustomerLevelPrice(data) {
  return request({
    url: '/business/customerLevelPrice',
    method: 'post',
    data: data
  })
}

// 修改客户等级价
export function updateCustomerLevelPrice(data) {
  return request({
    url: '/business/customerLevelPrice',
    method: 'put',
    data: data
  })
}

// 删除客户等级价
export function delCustomerLevelPrice(levelPriceId) {
  return request({
    url: '/business/customerLevelPrice/' + levelPriceId,
    method: 'delete'
  })
}
