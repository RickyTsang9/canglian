import request from '@/utils/request'

// 查询补货建议列表
export function listReplenishment(query) {
  return request({
    url: '/business/replenishment/list',
    method: 'get',
    params: query
  })
}
