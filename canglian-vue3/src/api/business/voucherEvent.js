import request from '@/utils/request'

// 查询凭证事件列表
export function listVoucherEvent(query) {
  return request({
    url: '/business/voucherEvent/list',
    method: 'get',
    params: query
  })
}

// 查询凭证事件详细
export function getVoucherEvent(voucherEventId) {
  return request({
    url: '/business/voucherEvent/' + voucherEventId,
    method: 'get'
  })
}
