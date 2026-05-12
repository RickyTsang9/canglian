import request from '@/utils/request'

// 查询业务链路
export function listBusinessTrace(billType, billId) {
  return request({
    url: '/business/trace/' + billType + '/' + billId,
    method: 'get'
  })
}
