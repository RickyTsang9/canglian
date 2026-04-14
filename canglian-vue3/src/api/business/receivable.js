import request from '@/utils/request'

// 查询应收单列表
export function listReceivable(query) {
  return request({
    url: '/business/receivable/list',
    method: 'get',
    params: query
  })
}

// 查询应收单详细
export function getReceivable(receivableId) {
  return request({
    url: '/business/receivable/' + receivableId,
    method: 'get'
  })
}

// 新增应收单
export function addReceivable(data) {
  return request({
    url: '/business/receivable',
    method: 'post',
    data: data
  })
}

// 修改应收单
export function updateReceivable(data) {
  return request({
    url: '/business/receivable',
    method: 'put',
    data: data
  })
}

// 删除应收单
export function delReceivable(receivableId) {
  return request({
    url: '/business/receivable/' + receivableId,
    method: 'delete'
  })
}
