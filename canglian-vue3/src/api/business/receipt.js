import request from '@/utils/request'

// 查询收款单列表
export function listReceipt(query) {
  return request({
    url: '/business/receipt/list',
    method: 'get',
    params: query
  })
}

// 查询收款单详细
export function getReceipt(receiptId) {
  return request({
    url: '/business/receipt/' + receiptId,
    method: 'get'
  })
}

// 新增收款单
export function addReceipt(data) {
  return request({
    url: '/business/receipt',
    method: 'post',
    data: data
  })
}

// 修改收款单
export function updateReceipt(data) {
  return request({
    url: '/business/receipt',
    method: 'put',
    data: data
  })
}

// 删除收款单
export function delReceipt(receiptId) {
  return request({
    url: '/business/receipt/' + receiptId,
    method: 'delete'
  })
}
