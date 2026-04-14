import request from '@/utils/request'

export function listFundAccount(query) {
  return request({
    url: '/business/fundAccount/list',
    method: 'get',
    params: query
  })
}

export function getFundAccount(accountId) {
  return request({
    url: '/business/fundAccount/' + accountId,
    method: 'get'
  })
}

export function addFundAccount(data) {
  return request({
    url: '/business/fundAccount',
    method: 'post',
    data: data
  })
}

export function updateFundAccount(data) {
  return request({
    url: '/business/fundAccount',
    method: 'put',
    data: data
  })
}

export function delFundAccount(accountId) {
  return request({
    url: '/business/fundAccount/' + accountId,
    method: 'delete'
  })
}
