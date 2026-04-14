import request from '@/utils/request'

export function receivableAging(query) {
  return request({
    url: '/business/report/receivable/aging',
    method: 'get',
    params: query
  })
}

export function payableAging(query) {
  return request({
    url: '/business/report/payable/aging',
    method: 'get',
    params: query
  })
}

export function receivableReconciliation(query) {
  return request({
    url: '/business/report/receivable/reconciliation',
    method: 'get',
    params: query
  })
}

export function payableReconciliation(query) {
  return request({
    url: '/business/report/payable/reconciliation',
    method: 'get',
    params: query
  })
}

export function profitLoss(query) {
  return request({
    url: '/business/report/profitLoss',
    method: 'get',
    params: query
  })
}

export function revenueExpense(query) {
  return request({
    url: '/business/report/revenueExpense',
    method: 'get',
    params: query
  })
}

export function costStructure(query) {
  return request({
    url: '/business/report/costStructure',
    method: 'get',
    params: query
  })
}
