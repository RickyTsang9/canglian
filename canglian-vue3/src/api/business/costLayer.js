import request from '@/utils/request'

export function listCostLayer(query) {
  return request({
    url: '/business/costLayer/list',
    method: 'get',
    params: query
  })
}

export function getCostLayer(costLayerId) {
  return request({
    url: '/business/costLayer/' + costLayerId,
    method: 'get'
  })
}

export function addCostLayer(data) {
  return request({
    url: '/business/costLayer',
    method: 'post',
    data: data
  })
}

export function updateCostLayer(data) {
  return request({
    url: '/business/costLayer',
    method: 'put',
    data: data
  })
}

export function delCostLayer(costLayerId) {
  return request({
    url: '/business/costLayer/' + costLayerId,
    method: 'delete'
  })
}
