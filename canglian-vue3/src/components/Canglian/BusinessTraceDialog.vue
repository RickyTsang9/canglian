<template>
  <el-dialog title="业务链路" v-model="visible" width="760px" append-to-body>
    <el-timeline v-loading="loading">
      <el-timeline-item
        v-for="traceNode in traceNodeList"
        :key="traceNode.billType + '-' + traceNode.billId"
        :timestamp="parseTime(traceNode.businessDate)"
        placement="top"
      >
        <el-card shadow="never">
          <template #header>
            <div class="trace-header">
              <span>{{ traceNode.nodeName }}</span>
              <el-tag size="small">{{ traceNode.relationLabel }}</el-tag>
            </div>
          </template>
          <el-descriptions :column="2" size="small" border>
            <el-descriptions-item label="单据号">{{ traceNode.billNo }}</el-descriptions-item>
            <el-descriptions-item label="金额">{{ traceNode.amount }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ traceNode.bizStatus || traceNode.status || "-" }}</el-descriptions-item>
            <el-descriptions-item label="来源单号">{{ traceNode.sourceBillNo || "-" }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-if="!loading && traceNodeList.length === 0" description="暂无链路数据" />
  </el-dialog>
</template>

<script setup name="BusinessTraceDialog">
import { listBusinessTrace } from "@/api/business/trace"

const { proxy } = getCurrentInstance()
const visible = ref(false)
const loading = ref(false)
const traceNodeList = ref([])

// 打开业务链路弹窗
function open(billType, billId) {
  visible.value = true
  loading.value = true
  traceNodeList.value = []
  listBusinessTrace(billType, billId).then(response => {
    traceNodeList.value = response.data || []
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

// 格式化时间
function parseTime(time) {
  return proxy.parseTime(time)
}

defineExpose({
  open
})
</script>

<style scoped>
.trace-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
