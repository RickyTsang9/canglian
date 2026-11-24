<template>
  <div class="app-container">
    <el-card class="backup-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">数据库逻辑备份管理</span>
            <el-tag type="success">在线快照可用</el-tag>
            <el-tag v-if="scheduleInfo.configured" :type="scheduleInfo.status === '0' ? 'primary' : 'warning'">
              定时策略：{{ scheduleInfo.status === '0' ? `已启用 (${scheduleInfo.cronExpression})` : '已暂停' }}
            </el-tag>
          </div>
          <div class="header-actions">
            <el-button
              type="primary"
              icon="Download"
              :loading="creating"
              @click="handleCreateBackup(false)"
              v-hasPermi="['business:dataBackup:add']"
            >创建 SQL 备份</el-button>
            <el-button
              type="success"
              plain
              icon="FolderChecked"
              :loading="creating"
              @click="handleCreateBackup(true)"
              v-hasPermi="['business:dataBackup:add']"
            >创建压缩备份 (.gz)</el-button>
            <el-button icon="Timer" @click="goScheduleConfig">调整定时策略</el-button>
            <el-button icon="Refresh" :loading="loading" @click="refreshAll">刷新列表</el-button>
          </div>
        </div>
      </template>

      <el-alert
        :title="scheduleAlertTitle"
        type="info"
        show-icon
        :closable="false"
        class="mb16"
      />

      <el-table v-loading="loading" :data="backupList" border>
        <el-table-column label="备份文件名" prop="fileName" min-width="260" show-overflow-tooltip />
        <el-table-column label="格式" width="95" align="center">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.compressed ? 'success' : 'info'">
              {{ scope.row.compressed ? 'GZIP' : 'SQL' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" prop="fileSizeText" width="120" align="center" />
        <el-table-column label="完整性签名" width="130" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.checksumStatus === 'valid' || scope.row.checksumStatus === 'recorded'" type="success" size="small">SHA-256 已签</el-tag>
            <el-tag v-else-if="scope.row.checksumStatus === 'tampered'" type="danger" size="small">校验异常</el-tag>
            <el-tag v-else type="warning" size="small">旧版无签名</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备份时间" prop="createTime" width="180" align="center">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="275" fixed="right" align="center">
          <template #default="scope">
            <div class="table-action-cell">
              <el-button
                link
                type="success"
                icon="CircleCheck"
                @click="handleVerify(scope.row)"
                v-hasPermi="['business:dataBackup:verify']"
              >校验</el-button>
              <el-button
                link
                type="primary"
                icon="Download"
                @click="handleDownload(scope.row)"
                v-hasPermi="['business:dataBackup:download']"
              >下载</el-button>
              <el-button
                link
                type="warning"
                icon="RefreshLeft"
                @click="openRestoreDialog(scope.row)"
                v-hasPermi="['business:dataBackup:restore']"
              >受控恢复</el-button>
              <el-button
                link
                type="danger"
                icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['business:dataBackup:remove']"
              >删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="backup-tips">
        <el-descriptions title="备份与恢复说明" :column="1" border size="small">
          <el-descriptions-item label="自动备份" label-class-name="tip-label">
            内置 Quartz 定时任务 <code>canglianBackupTask.autoBackup(30)</code>，默认每日凌晨 02:00 自动导出 GZIP 压缩快照并自动清理超过保留期的旧备份（至少保留最近 3 份）。
          </el-descriptions-item>
          <el-descriptions-item label="完整性保护" label-class-name="tip-label">
            每次创建备份自动生成 <code>.sha256</code> 伴生签名文件，点击「校验」可实时比对文件哈希与文件头签名。
          </el-descriptions-item>
          <el-descriptions-item label="受控恢复" label-class-name="tip-label">
            仅超级管理员可执行在线恢复；恢复前系统会自动先创建一份 <code>pre_restore_*.sql</code> 兜底快照，防止误操作导致数据不可逆损失。
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <el-dialog v-model="restoreDialog.visible" title="高危操作：受控恢复数据库备份" width="540px" append-to-body>
      <el-alert
        title="警告：恢复操作将用所选快照覆盖当前业务库全部表数据！系统会在覆盖前自动创建一份 pre_restore_*.sql 兜底备份。"
        type="error"
        show-icon
        :closable="false"
        class="mb16"
      />
      <el-form label-width="110px">
        <el-form-item label="目标快照">
          <el-tag type="warning">{{ restoreDialog.fileName }}</el-tag>
        </el-form-item>
        <el-form-item label="确认口令">
          <el-input
            v-model="restoreDialog.confirmPhrase"
            :placeholder="`请完整输入：RESTORE:${restoreDialog.fileName}`"
            clearable
          />
          <div class="confirm-hint">请输入 <code>RESTORE:{{ restoreDialog.fileName }}</code> 确认执行</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="restoreDialog.visible = false">取消</el-button>
        <el-button
          type="danger"
          :loading="restoreDialog.submitting"
          :disabled="restoreDialog.confirmPhrase !== `RESTORE:${restoreDialog.fileName}`"
          @click="submitRestore"
        >确认覆盖恢复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="DataBackup">
import {
  listDataBackup,
  getBackupSchedule,
  createDataBackup,
  verifyDataBackup,
  restoreDataBackup,
  downloadDataBackup,
  delDataBackup
} from "@/api/business/dataBackup"
import { useRouter } from "vue-router"
import { blobValidate } from "@/utils/canglian"

const { proxy } = getCurrentInstance()
const router = useRouter()

function hasRouteAccess(path) {
  if (!path) return false
  const resolved = router.resolve(path)
  return resolved && resolved.matched && resolved.matched.length > 0 && resolved.name !== "NotFound" && !resolved.matched.some(r => String(r.path || "").includes(":pathMatch"))
}

const loading = ref(false)
const creating = ref(false)
const backupList = ref([])
const scheduleInfo = ref({ configured: false })
const restoreDialog = reactive({
  visible: false,
  fileName: "",
  confirmPhrase: "",
  submitting: false
})

const scheduleAlertTitle = computed(() => {
  if (scheduleInfo.value.configured) {
    const nextTime = scheduleInfo.value.nextValidTime ? `，下次计划触发：${proxy.parseTime(scheduleInfo.value.nextValidTime)}` : ""
    return `自动备份任务「${scheduleInfo.value.jobName || '每日自动备份'}」已配置（${scheduleInfo.value.invokeTarget}，Cron：${scheduleInfo.value.cronExpression}${nextTime}）。点击下方按钮也可随时手工创建快照。`
  }
  return "支持导出当前独立业务库全部表结构与数据为 UTF-8 SQL 或 GZIP 压缩快照，并自动生成 SHA-256 完整性校验签名。"
})

function getList() {
  loading.value = true
  listDataBackup().then(response => {
    backupList.value = response.data || []
  }).finally(() => {
    loading.value = false
  })
}

function loadSchedule() {
  getBackupSchedule().then(response => {
    scheduleInfo.value = response.data || { configured: false }
  }).catch(() => {})
}

function refreshAll() {
  getList()
  loadSchedule()
}

function goScheduleConfig() {
  if (!hasRouteAccess("/monitor/job")) {
    proxy.$modal.msgWarning("当前账号暂无访问定时任务管理模块的菜单权限")
    return
  }
  router.push("/monitor/job")
}

function handleCreateBackup(compress) {
  creating.value = true
  createDataBackup(compress).then(response => {
    const info = response.data || {}
    proxy.$modal.msgSuccess(`备份成功：${info.fileName || ""}（${info.tableCount || 0} 张表，${info.rowCount || 0} 条记录）`)
    getList()
  }).finally(() => {
    creating.value = false
  })
}

function handleVerify(row) {
  verifyDataBackup(row.fileName).then(response => {
    const res = response.data || {}
    row.checksumStatus = res.checksumStatus
    if (res.verified) {
      proxy.$modal.msgSuccess(`${row.fileName}：${res.message || "校验通过"}`)
    } else {
      proxy.$modal.msgError(`${row.fileName}：${res.message || "校验失败"}`)
    }
  })
}

function openRestoreDialog(row) {
  restoreDialog.fileName = row.fileName
  restoreDialog.confirmPhrase = ""
  restoreDialog.visible = true
}

function submitRestore() {
  restoreDialog.submitting = true
  restoreDataBackup({
    fileName: restoreDialog.fileName,
    confirmPhrase: restoreDialog.confirmPhrase
  }).then(response => {
    const res = response.data || {}
    proxy.$modal.msgSuccess(`恢复成功（已执行 ${res.executedStatements || 0} 条 SQL，恢复前兜底快照：${res.preRestoreBackupFile || ""}）`)
    restoreDialog.visible = false
    getList()
  }).finally(() => {
    restoreDialog.submitting = false
  })
}

async function handleDownload(row) {
  try {
    const data = await downloadDataBackup(row.fileName)
    const isBlob = blobValidate(data)
    if (!isBlob) {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      proxy.$modal.msgError(rspObj.msg || "备份文件下载失败")
      return
    }
    const url = window.URL.createObjectURL(new Blob([data]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", row.fileName)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    proxy.$modal.msgError("备份文件下载失败")
  }
}

function handleDelete(row) {
  proxy.$modal.confirm(`是否确认删除备份文件「${row.fileName}」？删除后不可恢复。`).then(() => {
    return delDataBackup(row.fileName)
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()
  }).catch(() => {})
}

refreshAll()
</script>

<style scoped>
.backup-card {
  min-height: 420px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.mb16 {
  margin-bottom: 16px;
}

.backup-tips {
  margin-top: 20px;
}

.backup-tips :deep(.tip-label) {
  width: 112px;
  white-space: nowrap;
}

.backup-tips code {
  padding: 1px 6px;
  margin: 0 2px;
  border-radius: 4px;
  background: var(--panel-subtle-bg, #f3f4f6);
  border: 1px solid var(--panel-border, #e5e7eb);
  font-size: 12px;
  color: #1d4ed8;
}

.confirm-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
</style>
