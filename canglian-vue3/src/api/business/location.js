import request from '@/utils/request'

// 查询库位档案列表
export function listLocation(query) {
  return request({
    url: '/business/location/list',
    method: 'get',
    params: query
  })
}

// 查询库位档案详细
export function getLocation(locationId) {
  return request({
    url: '/business/location/' + locationId,
    method: 'get'
  })
}

// 新增库位档案
export function addLocation(data) {
  return request({
    url: '/business/location',
    method: 'post',
    data: data
  })
}

// 修改库位档案
export function updateLocation(data) {
  return request({
    url: '/business/location',
    method: 'put',
    data: data
  })
}

// 删除库位档案
export function delLocation(locationId) {
  return request({
    url: '/business/location/' + locationId,
    method: 'delete'
  })
}
