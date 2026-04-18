import request from '@/utils/request'

// 查询发票登记列表
export function listInvoice(query) {
  return request({
    url: '/business/invoice/list',
    method: 'get',
    params: query
  })
}

// 查询发票登记详细
export function getInvoice(invoiceId) {
  return request({
    url: '/business/invoice/' + invoiceId,
    method: 'get'
  })
}

// 新增发票登记
export function addInvoice(data) {
  return request({
    url: '/business/invoice',
    method: 'post',
    data: data
  })
}

// 修改发票登记
export function updateInvoice(data) {
  return request({
    url: '/business/invoice',
    method: 'put',
    data: data
  })
}

// 确认发票登记
export function confirmInvoice(invoiceId) {
  return request({
    url: '/business/invoice/confirm/' + invoiceId,
    method: 'put'
  })
}

// 删除发票登记
export function delInvoice(invoiceId) {
  return request({
    url: '/business/invoice/' + invoiceId,
    method: 'delete'
  })
}
