import request from '@/utils/request'

// 查询多单位列表
export function listProductUnit(query) {
  return request({
    url: '/business/productUnit/list',
    method: 'get',
    params: query
  })
}

// 按商品ID查询启用单位列表
export function listUnitsByProduct(productId) {
  return request({
    url: '/business/productUnit/byProduct/' + productId,
    method: 'get'
  })
}

// 新增商品多单位
export function addProductUnit(data) {
  return request({
    url: '/business/productUnit',
    method: 'post',
    data: data
  })
}

// 修改商品多单位
export function updateProductUnit(data) {
  return request({
    url: '/business/productUnit',
    method: 'put',
    data: data
  })
}

// 删除商品多单位
export function delProductUnit(unitIds) {
  return request({
    url: '/business/productUnit/' + unitIds,
    method: 'delete'
  })
}

// 销售价格实时自动解析（特价 > 等级价 > 最近成交价 > 包装单位售价 > 默认价）
export function resolveLivePrice(params) {
  return request({
    url: '/business/price/resolve',
    method: 'get',
    params: params
  })
}
