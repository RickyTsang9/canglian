import request from '@/utils/request'

// 查询最近成交价列表
export function listRecentTransactionPrice(query) {
  return request({
    url: '/business/recentTransactionPrice/list',
    method: 'get',
    params: query
  })
}

// 查询最近成交价详细
export function getRecentTransactionPrice(recentPriceId) {
  return request({
    url: '/business/recentTransactionPrice/' + recentPriceId,
    method: 'get'
  })
}

// 新增最近成交价
export function addRecentTransactionPrice(data) {
  return request({
    url: '/business/recentTransactionPrice',
    method: 'post',
    data: data
  })
}

// 修改最近成交价
export function updateRecentTransactionPrice(data) {
  return request({
    url: '/business/recentTransactionPrice',
    method: 'put',
    data: data
  })
}

// 删除最近成交价
export function delRecentTransactionPrice(recentPriceId) {
  return request({
    url: '/business/recentTransactionPrice/' + recentPriceId,
    method: 'delete'
  })
}
