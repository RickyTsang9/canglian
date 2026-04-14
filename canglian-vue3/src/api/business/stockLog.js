import request from '@/utils/request'

export function listStockLog(query) {
  return request({
    url: '/business/stockLog/list',
    method: 'get',
    params: query
  })
}

export function getStockLog(stockLogId) {
  return request({
    url: '/business/stockLog/' + stockLogId,
    method: 'get'
  })
}

export function addStockLog(data) {
  return request({
    url: '/business/stockLog',
    method: 'post',
    data: data
  })
}

export function updateStockLog(data) {
  return request({
    url: '/business/stockLog',
    method: 'put',
    data: data
  })
}

export function delStockLog(stockLogId) {
  return request({
    url: '/business/stockLog/' + stockLogId,
    method: 'delete'
  })
}
