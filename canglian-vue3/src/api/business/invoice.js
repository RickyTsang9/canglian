import request from '@/utils/request'

export function listInvoice(query) {
  return request({ url: '/business/invoice/list', method: 'get', params: query })
}

export function getInvoice(invoiceId) {
  return request({ url: '/business/invoice/' + invoiceId, method: 'get' })
}

export function addInvoice(data) {
  return request({ url: '/business/invoice', method: 'post', data })
}

export function updateInvoice(data) {
  return request({ url: '/business/invoice', method: 'put', data })
}

export function confirmInvoice(invoiceId) {
  return request({ url: '/business/invoice/confirm/' + invoiceId, method: 'put' })
}

export function issueRedInvoice(originInvoiceId, data) {
  return request({ url: '/business/invoice/red/' + originInvoiceId, method: 'post', data })
}

export function delInvoice(invoiceId) {
  return request({ url: '/business/invoice/' + invoiceId, method: 'delete' })
}
