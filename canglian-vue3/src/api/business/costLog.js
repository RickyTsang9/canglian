import request from '@/utils/request'

export function listCostLog(query) {
  return request({
    url: '/business/costLog/list',
    method: 'get',
    params: query
  })
}

export function getCostLog(costLogId) {
  return request({
    url: '/business/costLog/' + costLogId,
    method: 'get'
  })
}

export function addCostLog(data) {
  return request({
    url: '/business/costLog',
    method: 'post',
    data: data
  })
}

export function updateCostLog(data) {
  return request({
    url: '/business/costLog',
    method: 'put',
    data: data
  })
}

export function delCostLog(costLogId) {
  return request({
    url: '/business/costLog/' + costLogId,
    method: 'delete'
  })
}
