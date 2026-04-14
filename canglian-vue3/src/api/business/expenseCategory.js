import request from '@/utils/request'

export function listExpenseCategory(query) {
  return request({
    url: '/business/expenseCategory/list',
    method: 'get',
    params: query
  })
}

export function treeselectExpenseCategory(query) {
  return request({
    url: '/business/expenseCategory/treeselect',
    method: 'get',
    params: query
  })
}

export function getExpenseCategory(categoryId) {
  return request({
    url: '/business/expenseCategory/' + categoryId,
    method: 'get'
  })
}

export function addExpenseCategory(data) {
  return request({
    url: '/business/expenseCategory',
    method: 'post',
    data: data
  })
}

export function updateExpenseCategory(data) {
  return request({
    url: '/business/expenseCategory',
    method: 'put',
    data: data
  })
}

export function delExpenseCategory(categoryIds) {
  return request({
    url: '/business/expenseCategory/' + categoryIds,
    method: 'delete'
  })
}
