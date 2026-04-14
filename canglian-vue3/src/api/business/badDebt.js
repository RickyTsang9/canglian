import request from '@/utils/request'

// 查询坏账单列表
export function listBadDebt(query) {
  return request({
    url: '/business/badDebt/list',
    method: 'get',
    params: query
  })
}

// 查询坏账单详情
export function getBadDebt(badDebtId) {
  return request({
    url: '/business/badDebt/' + badDebtId,
    method: 'get'
  })
}

// 新增坏账单
export function addBadDebt(data) {
  return request({
    url: '/business/badDebt',
    method: 'post',
    data: data
  })
}

// 修改坏账单
export function updateBadDebt(data) {
  return request({
    url: '/business/badDebt',
    method: 'put',
    data: data
  })
}

// 删除坏账单
export function delBadDebt(badDebtId) {
  return request({
    url: '/business/badDebt/' + badDebtId,
    method: 'delete'
  })
}
