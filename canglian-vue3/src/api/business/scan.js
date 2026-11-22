import request from '@/utils/request'

export function scanProduct(barCode) {
  return request({ url: '/business/scan/product/' + encodeURIComponent(barCode), method: 'get' })
}

export function scanLocation(locationCode) {
  return request({ url: '/business/scan/location/' + encodeURIComponent(locationCode), method: 'get' })
}

export function scanStock(params) {
  return request({ url: '/business/scan/stock', method: 'get', params })
}

export function quickSaleByScan(data) {
  return request({ url: '/business/scan/quickSale', method: 'post', data })
}

export function quickInboundByScan(data) {
  return request({ url: '/business/scan/quickInbound', method: 'post', data })
}

export function outboundVerifyByScan(outboundId, items) {
  return request({ url: '/business/scan/outboundVerify/' + outboundId, method: 'post', data: items })
}

export function inventoryCheckByScan(data) {
  return request({ url: '/business/scan/inventoryCheck', method: 'post', data })
}

export function transferByScan(data) {
  return request({ url: '/business/scan/transfer', method: 'post', data })
}
