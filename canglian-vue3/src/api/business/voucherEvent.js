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

// 生成凭证
export function generateVoucher(voucherEventId) {
  return request({
    url: '/business/voucherEvent/generate/' + voucherEventId,
    method: 'put'
  })
}

// 回写凭证
export function writebackVoucher(voucherEventId) {
  return request({
    url: '/business/voucherEvent/writeback/' + voucherEventId,
    method: 'put'
  })
}

// 冲销凭证
export function reverseVoucher(voucherEventId) {
  return request({
    url: '/business/voucherEvent/reverse/' + voucherEventId,
    method: 'put'
  })
}
