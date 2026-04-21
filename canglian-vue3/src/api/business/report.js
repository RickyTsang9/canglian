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

// 查询销售毛利报表
export function salesGrossProfit(query) {
  return request({
    url: '/business/report/operation/salesGrossProfit',
    method: 'get',
    params: query
  })
}

// 查询客户贡献报表
export function customerContribution(query) {
  return request({
    url: '/business/report/operation/customerContribution',
    method: 'get',
    params: query
  })
}

// 查询商品周转报表
export function productTurnover(query) {
  return request({
    url: '/business/report/operation/productTurnover',
    method: 'get',
    params: query
  })
}

// 查询库存余额报表
export function stockBalance() {
  return request({
    url: '/business/report/operation/stockBalance',
    method: 'get'
  })
}

// 查询资金分析报表
export function fundAnalysis(query) {
  return request({
    url: '/business/report/operation/fundAnalysis',
    method: 'get',
    params: query
  })
}
