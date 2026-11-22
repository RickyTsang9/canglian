import request from '@/utils/request'

export function listBadDebt(query) {
  return request({ url: '/business/badDebt/list', method: 'get', params: query })
}

export function getBadDebt(badDebtId) {
  return request({ url: '/business/badDebt/' + badDebtId, method: 'get' })
}

export function addBadDebt(data) {
  return request({ url: '/business/badDebt', method: 'post', data })
}

export function updateBadDebt(data) {
  return request({ url: '/business/badDebt', method: 'put', data })
}

export function delBadDebt(badDebtId) {
  return request({ url: '/business/badDebt/' + badDebtId, method: 'delete' })
}

export function confirmBadDebt(badDebtId) {
  return request({ url: '/business/badDebt/confirm/' + badDebtId, method: 'put' })
}

export function unconfirmBadDebt(badDebtId) {
  return request({ url: '/business/badDebt/unconfirm/' + badDebtId, method: 'post' })
}

export function recoverBadDebt(badDebtId, data) {
  return request({ url: '/business/badDebt/recover/' + badDebtId, method: 'post', data })
}
