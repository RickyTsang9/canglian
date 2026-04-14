import request from '@/utils/request'

export function calculateCost(data) {
  return request({
    url: '/business/costCalculate/calculate',
    method: 'post',
    data: data
  })
}
