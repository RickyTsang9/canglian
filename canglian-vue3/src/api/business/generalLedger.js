import request from '@/utils/request'

export function listAccountSubject(query) {
  return request({ url: '/business/generalLedger/subject/list', method: 'get', params: query })
}

export function addAccountSubject(data) {
  return request({ url: '/business/generalLedger/subject', method: 'post', data })
}

export function updateAccountSubject(data) {
  return request({ url: '/business/generalLedger/subject', method: 'put', data })
}

export function delAccountSubject(subjectId) {
  return request({ url: '/business/generalLedger/subject/' + subjectId, method: 'delete' })
}

export function listAuxiliaryItem(query) {
  return request({ url: '/business/generalLedger/auxiliary/list', method: 'get', params: query })
}

export function syncAuxiliaryItems() {
  return request({ url: '/business/generalLedger/auxiliary/sync', method: 'post' })
}

export function listFiscalPeriod(query) {
  return request({ url: '/business/generalLedger/period/list', method: 'get', params: query })
}

export function initFiscalPeriodYear(year) {
  return request({ url: '/business/generalLedger/period/initYear/' + year, method: 'post' })
}

export function checkPeriodReadiness(periodId) {
  return request({ url: '/business/generalLedger/period/checkReadiness/' + periodId, method: 'get' })
}

export function closeFiscalPeriod(periodId) {
  return request({ url: '/business/generalLedger/period/close/' + periodId, method: 'put' })
}

export function reopenFiscalPeriod(periodId) {
  return request({ url: '/business/generalLedger/period/reopen/' + periodId, method: 'put' })
}

export function listVoucherTemplate(query) {
  return request({ url: '/business/generalLedger/voucherTemplate/list', method: 'get', params: query })
}

export function addVoucherTemplate(data) {
  return request({ url: '/business/generalLedger/voucherTemplate', method: 'post', data })
}

export function updateVoucherTemplate(data) {
  return request({ url: '/business/generalLedger/voucherTemplate', method: 'put', data })
}

export function delVoucherTemplate(templateId) {
  return request({ url: '/business/generalLedger/voucherTemplate/' + templateId, method: 'delete' })
}
