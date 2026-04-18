import request from '@/utils/request'

// 查询业务工作台汇总
export function getWorkbenchSummary() {
  return request({
    url: '/business/workbench/summary',
    method: 'get'
  })
}
