import request from '@/utils/request'

export function listExpense(query) {
  return request({
    url: '/business/expense/list',
    method: 'get',
    params: query
  })
}

export function getExpense(expenseId) {
  return request({
    url: '/business/expense/' + expenseId,
    method: 'get'
  })
}

export function addExpense(data) {
  return request({
    url: '/business/expense',
    method: 'post',
    data: data
  })
}

export function updateExpense(data) {
  return request({
    url: '/business/expense',
    method: 'put',
    data: data
  })
}

export function confirmExpense(expenseId) {
  return request({
    url: '/business/expense/confirm/' + expenseId,
    method: 'put'
  })
}

export function delExpense(expenseId) {
  return request({
    url: '/business/expense/' + expenseId,
    method: 'delete'
  })
}
