<template>
  <div class="app-container todo-center">
    <div class="todo-header">
      <div>
        <div class="page-title">统一待办中心</div>
        <div class="page-subtitle">汇总审批、库存和往来账款的关键待处理事项</div>
      </div>
      <el-button type="primary" icon="Refresh" :loading="loading" @click="getSummary">刷新</el-button>
    </div>

    <el-row :gutter="16" class="summary-row">
      <el-col v-for="summaryItem in summaryCardList" :key="summaryItem.key" :xs="24" :sm="12" :md="6">
        <div class="summary-panel" :class="'summary-panel--' + summaryItem.type" @click="handleSummaryClick(summaryItem)">
          <div class="summary-label">{{ summaryItem.label }}</div>
          <div class="summary-value">{{ summaryItem.value }}</div>
          <div class="summary-desc">{{ summaryItem.desc }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="todo-split-row">
      <el-col :xs="24" :lg="12" class="todo-split-col">
        <div class="todo-panel">
          <div class="panel-header">
            <span>审批待办</span>
            <el-tag type="warning">{{ pendingApproveList.length }}</el-tag>
          </div>
          <el-table v-if="loading || pendingApproveList.length > 0" v-loading="loading" :data="pendingApproveList" border>
            <el-table-column label="单据类型" align="center" prop="billTypeLabel" width="100" />
            <el-table-column label="单据单号" align="center" prop="billNo" min-width="145" show-overflow-tooltip />
            <el-table-column label="业务日期" align="center" prop="businessDate" width="110">
              <template #default="scope">
                <span>{{ parseTime(scope.row.businessDate, "{y}-{m}-{d}") }}</span>
              </template>
            </el-table-column>
            <el-table-column label="处理" align="center" width="80" fixed="right">
              <template #default="scope">
                <el-button link type="primary" @click="handlePendingApprove(scope.row)">进入</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && pendingApproveList.length === 0" description="暂无审批待办" />
        </div>
      </el-col>

      <el-col :xs="24" :lg="12" class="todo-split-col">
        <div class="todo-panel">
          <div class="panel-header">
            <span>库存预警</span>
            <el-tag type="danger">{{ lowStockList.length }}</el-tag>
          </div>
          <el-table v-if="loading || lowStockList.length > 0" v-loading="loading" :data="lowStockList" border>
            <el-table-column label="仓库" align="center" min-width="95" show-overflow-tooltip>
              <template #default="scope">
                <span>{{ getWarehouseName(scope.row.warehouseId) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="商品" align="center" min-width="125" show-overflow-tooltip>
              <template #default="scope">
                <span>{{ getProductName(scope.row.productId) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="库位" align="center" width="88">
              <template #default="scope">
                <span>{{ scope.row.locationId ? `#${scope.row.locationId}` : "默认库位" }}</span>
              </template>
            </el-table-column>
            <el-table-column label="当前库存" align="right" prop="quantity" width="85" />
            <el-table-column label="最小库存" align="right" prop="warningMinQty" width="85" />
            <el-table-column label="处理" align="center" width="76" fixed="right">
              <template #default>
                <el-button link type="primary" @click="handleStockWarning">进入</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && lowStockList.length === 0" description="暂无库存预警" />
        </div>
      </el-col>
    </el-row>

    <div class="todo-panel finance-panel">
      <div class="panel-header">
        <span>往来到期</span>
        <el-tag type="info">{{ financeTodoList.length }}</el-tag>
      </div>
      <el-table v-if="loading || financeTodoList.length > 0" v-loading="loading" :data="financeTodoList" border>
        <el-table-column label="类型" align="center" prop="billTypeLabel" width="90" />
        <el-table-column label="单据单号" align="center" prop="billNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="往来单位" align="center" min-width="140" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.billType === "receivable" ? getCustomerName(scope.row.partnerId) : getSupplierName(scope.row.partnerId) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="应收应付金额" align="right" width="125">
          <template #default="scope">￥{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="已收已付金额" align="right" width="125">
          <template #default="scope">￥{{ Number(scope.row.settledAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="到期日期" align="center" prop="dueDate" width="115">
          <template #default="scope">
            <span>{{ parseTime(scope.row.dueDate, "{y}-{m}-{d}") }}</span>
          </template>
        </el-table-column>
        <el-table-column label="处理" align="center" width="80" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleFinanceTodo(scope.row)">进入</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && financeTodoList.length === 0" description="暂无往来到期事项" />
    </div>
  </div>
</template>

<script setup name="TodoCenter">
import { getWorkbenchSummary } from "@/api/business/workbench"
import { useRouter } from "vue-router"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const router = useRouter()
const { loadWarehouses, loadProducts, loadCustomers, loadSuppliers, getWarehouseName, getProductName, getCustomerName, getSupplierName } = useBusinessOptions()
const loading = ref(false)
const summaryData = ref({})

const summaryCardList = computed(() => {
  return [
    { key: "pendingApproveCount", label: "审批待办", value: getNumber(summaryData.value.pendingApproveCount), desc: "草稿单据待审批", type: "warning", path: "/business/saleOrder" },
    { key: "lowStockCount", label: "库存预警", value: getNumber(summaryData.value.lowStockCount), desc: "低于最小库存", type: "danger", path: "/wms/stock" },
    { key: "dueReceivableCount", label: "应收到期", value: getNumber(summaryData.value.dueReceivableCount), desc: "7天内待跟进", type: "primary", path: "/finance/receivable" },
    { key: "duePayableCount", label: "应付到期", value: getNumber(summaryData.value.duePayableCount), desc: "7天内待付款", type: "success", path: "/finance/payable" }
  ]
})

const pendingApproveList = computed(() => summaryData.value.pendingApproveList || [])
const lowStockList = computed(() => summaryData.value.lowStockList || [])
const financeTodoList = computed(() => {
  const receivableList = summaryData.value.dueReceivableList || []
  const payableList = summaryData.value.duePayableList || []
  const financeList = []
  receivableList.forEach(receivableItem => {
    financeList.push({
      billType: "receivable",
      billTypeLabel: "应收",
      billNo: receivableItem.receivableNo,
      partnerId: receivableItem.customerId,
      amount: receivableItem.amount,
      settledAmount: receivableItem.receivedAmount,
      dueDate: receivableItem.dueDate
    })
  })
  payableList.forEach(payableItem => {
    financeList.push({
      billType: "payable",
      billTypeLabel: "应付",
      billNo: payableItem.payableNo,
      partnerId: payableItem.supplierId,
      amount: payableItem.amount,
      settledAmount: payableItem.paidAmount,
      dueDate: payableItem.dueDate
    })
  })
  return financeList
})

// 查询统一待办汇总
function getSummary() {
  loading.value = true
  Promise.all([loadWarehouses(), loadProducts(), loadCustomers(), loadSuppliers()])
  getWorkbenchSummary().then(response => {
    summaryData.value = response.data || {}
  }).finally(() => {
    loading.value = false
  })
}

// 获取数字展示值
function getNumber(value) {
  return value || 0
}

// 解析时间展示
function parseTime(time, pattern) {
  return time ? proxy.parseTime(time, pattern) : "-"
}

// 点击汇总卡片进入对应业务页面
function handleSummaryClick(summaryItem) {
  if (summaryItem.key === "lowStockCount") {
    handleStockWarning()
    return
  }
  router.push({ path: summaryItem.path })
}

// 进入审批待办对应页面
function handlePendingApprove(todoItem) {
  if (todoItem.routePath) {
    router.push({ path: todoItem.routePath })
    return
  }
  const billTypePathMap = {
    saleOrder: "/business/saleOrder",
    purOrder: "/business/purOrder",
    inbound: "/wms/inbound",
    outbound: "/wms/outbound",
    purchaseReturn: "/wms/purchaseReturn",
    saleReturn: "/wms/saleReturn",
    salesInvoice: "/finance/salesInvoice",
    purchaseInvoice: "/finance/purchaseInvoice"
  }
  router.push({ path: billTypePathMap[todoItem.billType] || "/business/workbench" })
}

// 进入库存预警页面
function handleStockWarning() {
  router.push({ path: "/wms/stock", query: { warningMode: "1" } })
}

// 进入往来账款页面
function handleFinanceTodo(todoItem) {
  const financePath = todoItem.billType === "payable" ? "/finance/payable" : "/finance/receivable"
  router.push({ path: financePath })
}

getSummary()
</script>

<style scoped>
.todo-center {
  background: var(--el-bg-color-page, #f5f7fb);
  min-height: calc(100vh - 84px);
}

.todo-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  color: var(--el-text-color-primary, #1f2d3d);
  font-size: 20px;
  font-weight: 650;
}

.page-subtitle {
  margin-top: 6px;
  color: var(--el-text-color-secondary, #6b7280);
  font-size: 13px;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-panel {
  min-height: 118px;
  margin-bottom: 16px;
  padding: 18px;
  border: 1px solid var(--el-border-color-light, #e5eaf3);
  border-left: 4px solid #409eff;
  border-radius: 8px;
  background: var(--el-bg-color-overlay, #ffffff);
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, transform 0.2s;
}

.summary-panel:hover {
  border-color: #c6e2ff;
  box-shadow: var(--panel-shadow, 0 8px 18px rgba(31, 45, 61, 0.08));
  transform: translateY(-1px);
}

.summary-panel--warning {
  border-left-color: #e6a23c;
}

.summary-panel--danger {
  border-left-color: #f56c6c;
}

.summary-panel--primary {
  border-left-color: #409eff;
}

.summary-panel--success {
  border-left-color: #67c23a;
}

.summary-label {
  color: var(--el-text-color-regular, #606266);
  font-size: 14px;
}

.summary-value {
  margin-top: 12px;
  color: var(--el-text-color-primary, #1f2d3d);
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
}

.summary-desc {
  margin-top: 12px;
  color: var(--el-text-color-secondary, #909399);
  font-size: 13px;
}

.todo-split-row {
  display: flex;
  flex-wrap: wrap;
}

.todo-split-col {
  display: flex;
  flex-direction: column;
}

.todo-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid var(--el-border-color-light, #e5eaf3);
  border-radius: 12px;
  background: var(--el-bg-color-overlay, #ffffff);
}

.finance-panel {
  margin-bottom: 0;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  color: var(--el-text-color-primary, #1f2d3d);
  font-size: 15px;
  font-weight: 650;
}
</style>
