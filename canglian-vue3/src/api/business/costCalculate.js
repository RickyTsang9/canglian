import request from '@/utils/request'

export function getCostConfig() {
  return request({
    url: '/business/costCalculate/config',
    method: 'get'
  })
}

export function updateCostConfig(data) {
  return request({
    url: '/business/costCalculate/config',
    method: 'put',
    data: data
  })
}

export function calculateCost(data) {
  return request({
    url: '/business/costCalculate/calculate',
    method: 'post',
    data: data
  })
}

export function recalculateCost(data) {
  return request({
    url: '/business/costCalculate/recalculate',
    method: 'post',
    data: data
  })
}

export function adjustCost(data) {
  return request({
    url: '/business/costCalculate/adjust',
    method: 'post',
    data: data
  })
}
