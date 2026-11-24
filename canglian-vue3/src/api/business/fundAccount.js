import request from '@/utils/request'

export function listFundAccount(query) {
  return request({ url: '/business/fundAccount/list', method: 'get', params: query })
}

export function getFundAccount(accountId) {
  return request({ url: '/business/fundAccount/' + accountId, method: 'get' })
}

export function addFundAccount(data) {
  return request({ url: '/business/fundAccount', method: 'post', data })
}

export function updateFundAccount(data) {
  return request({ url: '/business/fundAccount', method: 'put', data })
}

export function delFundAccount(accountId) {
  return request({ url: '/business/fundAccount/' + accountId, method: 'delete' })
}

export function listFundLogs(query) {
  return request({ url: '/business/fundAccount/logs', method: 'get', params: query })
}

export function listFundTransfers(query) {
  return request({ url: '/business/fundAccount/transfer/list', method: 'get', params: query })
}

export function addFundTransfer(data) {
  return request({ url: '/business/fundAccount/transfer', method: 'post', data })
}

export function confirmFundTransfer(transferId) {
  return request({ url: '/business/fundAccount/transfer/confirm/' + transferId, method: 'put' })
}

export function unconfirmFundTransfer(transferId) {
  return request({ url: '/business/fundAccount/transfer/unconfirm/' + transferId, method: 'put' })
}

export function reverseFundTransfer(transferId) {
  return request({ url: '/business/fundAccount/transfer/reverse/' + transferId, method: 'post' })
}

export function delFundTransfer(transferId) {
  return request({ url: '/business/fundAccount/transfer/' + transferId, method: 'delete' })
}
