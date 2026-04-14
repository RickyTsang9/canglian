import request from '@/utils/request'

export function listCostAccount(query) {
  return request({
    url: '/business/costAccount/list',
    method: 'get',
    params: query
  })
}

export function getCostAccount(costAccountId) {
  return request({
    url: '/business/costAccount/' + costAccountId,
    method: 'get'
  })
}

export function addCostAccount(data) {
  return request({
    url: '/business/costAccount',
    method: 'post',
    data: data
  })
}

export function updateCostAccount(data) {
  return request({
    url: '/business/costAccount',
    method: 'put',
    data: data
  })
}

export function delCostAccount(costAccountId) {
  return request({
    url: '/business/costAccount/' + costAccountId,
    method: 'delete'
  })
}
