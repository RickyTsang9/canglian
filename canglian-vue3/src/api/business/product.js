import request from '@/utils/request'

// 查询商品档案列表
export function listProduct(query) {
  return request({
    url: '/business/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品档案详细
export function getProduct(productId) {
  return request({
    url: '/business/product/' + productId,
    method: 'get'
  })
}

// 新增商品档案
export function addProduct(data) {
  return request({
    url: '/business/product',
    method: 'post',
    data: data
  })
}

// 修改商品档案
export function updateProduct(data) {
  return request({
    url: '/business/product',
    method: 'put',
    data: data
  })
}

// 删除商品档案
export function delProduct(productId) {
  return request({
    url: '/business/product/' + productId,
    method: 'delete'
  })
}
