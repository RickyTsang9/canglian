import request from '@/utils/request'

// 按条码解析商品
export function scanProduct(barCode) {
  return request({
    url: '/business/scan/product/' + barCode,
    method: 'get'
  })
}

// 按库位码解析库位
export function scanLocation(locationCode) {
  return request({
    url: '/business/scan/location/' + locationCode,
    method: 'get'
  })
}

// 查询扫码库存
export function scanStock(params) {
  return request({
    url: '/business/scan/stock',
    method: 'get',
    params: params
  })
}

// 扫码快速开单
export function quickSaleByScan(data) {
  return request({
    url: '/business/scan/quickSale',
    method: 'post',
    data: data
  })
}

// 扫码快速盘点建单
export function inventoryCheckByScan(data) {
  return request({
    url: '/business/scan/inventoryCheck',
    method: 'post',
    data: data
  })
}

// 扫码快速调拨建单
export function transferByScan(data) {
  return request({
    url: '/business/scan/transfer',
    method: 'post',
    data: data
  })
}
