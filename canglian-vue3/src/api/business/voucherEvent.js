import request from '@/utils/request'

export function listVoucherEvent(query) {
  return request({ url: '/business/voucherEvent/list', method: 'get', params: query })
}

export function getVoucherEvent(voucherEventId) {
  return request({ url: '/business/voucherEvent/' + voucherEventId, method: 'get' })
}

export function listVoucherEntries(voucherEventId) {
  return request({ url: '/business/voucherEvent/' + voucherEventId + '/entries', method: 'get' })
}

export function generateVoucher(voucherEventId) {
  return request({ url: '/business/voucherEvent/generate/' + voucherEventId, method: 'put' })
}

export function writebackVoucher(voucherEventId) {
  return request({ url: '/business/voucherEvent/writeback/' + voucherEventId, method: 'put' })
}

export function reverseVoucher(voucherEventId) {
  return request({ url: '/business/voucherEvent/reverse/' + voucherEventId, method: 'put' })
}

export function regenerateVoucherEvents() {
  return request({ url: '/business/voucherEvent/regenerate', method: 'post' })
}
