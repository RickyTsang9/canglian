import request from '@/utils/request'

// 查询核销单列表
export function listWriteOff(query) {
  return request({
    url: '/business/writeOff/list',
    method: 'get',
    params: query
  })
}

// 查询核销单详情
export function getWriteOff(writeOffId) {
  return request({
    url: '/business/writeOff/' + writeOffId,
    method: 'get'
  })
}

// 新增核销单
export function addWriteOff(data) {
  return request({
    url: '/business/writeOff',
    method: 'post',
    data: data
  })
}

// 修改核销单
export function updateWriteOff(data) {
  return request({
    url: '/business/writeOff',
    method: 'put',
    data: data
  })
}

// 确认核销单
export function confirmWriteOff(writeOffId) {
  return request({
    url: '/business/writeOff/confirm/' + writeOffId,
    method: 'put'
  })
}

// 删除核销单
export function delWriteOff(writeOffId) {
  return request({
    url: '/business/writeOff/' + writeOffId,
    method: 'delete'
  })
}
