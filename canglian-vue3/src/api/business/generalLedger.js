import request from '@/utils/request'

// 查询会计科目列表
export function listAccountSubject(query) {
  return request({
    url: '/business/generalLedger/subject/list',
    method: 'get',
    params: query
  })
}

// 查询辅助核算列表
export function listAuxiliaryItem(query) {
  return request({
    url: '/business/generalLedger/auxiliary/list',
    method: 'get',
    params: query
  })
}

// 查询会计期间列表
export function listFiscalPeriod(query) {
  return request({
    url: '/business/generalLedger/period/list',
    method: 'get',
    params: query
  })
}

// 结账会计期间
export function closeFiscalPeriod(periodId) {
  return request({
    url: '/business/generalLedger/period/close/' + periodId,
    method: 'put'
  })
}

// 反结账会计期间
export function reopenFiscalPeriod(periodId) {
  return request({
    url: '/business/generalLedger/period/reopen/' + periodId,
    method: 'put'
  })
}
