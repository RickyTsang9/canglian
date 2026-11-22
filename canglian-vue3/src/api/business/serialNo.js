import request from '@/utils/request'

// 查询序列号列表
export function listSerialNo(query) {
  return request({
    url: '/business/serialNo/list',
    method: 'get',
    params: query
  })
}

// 序列号全生命周期溯源
export function traceSerialNo(snCode) {
  return request({
    url: '/business/serialNo/trace/' + encodeURIComponent(snCode),
    method: 'get'
  })
}

// 批量登记入库序列号
export function batchRegisterSerialNo(data) {
  return request({
    url: '/business/serialNo/batchRegister',
    method: 'post',
    data: data
  })
}

// 序列号出库核销
export function consumeSerialNo(data) {
  return request({
    url: '/business/serialNo/consume',
    method: 'post',
    data: data
  })
}

// 序列号退货回库
export function returnBackSerialNo(data) {
  return request({
    url: '/business/serialNo/returnBack',
    method: 'post',
    data: data
  })
}

// 删除序列号
export function delSerialNo(serialIds) {
  return request({
    url: '/business/serialNo/' + serialIds,
    method: 'delete'
  })
}
