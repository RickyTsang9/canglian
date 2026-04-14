import request from '@/utils/request'

// 查询仓库档案列表
export function listWarehouse(query) {
  return request({
    url: '/business/warehouse/list',
    method: 'get',
    params: query
  })
}

// 查询仓库档案详细
export function getWarehouse(warehouseId) {
  return request({
    url: '/business/warehouse/' + warehouseId,
    method: 'get'
  })
}

// 新增仓库档案
export function addWarehouse(data) {
  return request({
    url: '/business/warehouse',
    method: 'post',
    data: data
  })
}

// 修改仓库档案
export function updateWarehouse(data) {
  return request({
    url: '/business/warehouse',
    method: 'put',
    data: data
  })
}

// 删除仓库档案
export function delWarehouse(warehouseId) {
  return request({
    url: '/business/warehouse/' + warehouseId,
    method: 'delete'
  })
}
