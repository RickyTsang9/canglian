import request from "@/utils/request"

// 查询备份文件列表
export function listDataBackup() {
  return request({
    url: "/business/dataBackup/list",
    method: "get"
  })
}

// 查询自动定时备份策略状态
export function getBackupSchedule() {
  return request({
    url: "/business/dataBackup/schedule",
    method: "get"
  })
}

// 立即创建备份快照
export function createDataBackup(compress = false) {
  return request({
    url: "/business/dataBackup/create",
    method: "post",
    params: { compress }
  })
}

// 校验备份完整性
export function verifyDataBackup(fileName) {
  return request({
    url: "/business/dataBackup/verify/" + encodeURIComponent(fileName),
    method: "get"
  })
}

// 受控恢复备份快照（仅超级管理员）
export function restoreDataBackup(data) {
  return request({
    url: "/business/dataBackup/restore",
    method: "post",
    data
  })
}

// 下载备份文件（返回 Blob）
export function downloadDataBackup(fileName) {
  return request({
    url: "/business/dataBackup/download/" + encodeURIComponent(fileName),
    method: "get",
    responseType: "blob"
  })
}

// 删除备份文件
export function delDataBackup(fileName) {
  return request({
    url: "/business/dataBackup/" + encodeURIComponent(fileName),
    method: "delete"
  })
}
