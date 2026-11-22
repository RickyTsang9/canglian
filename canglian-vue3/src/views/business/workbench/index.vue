<template>
  <div class="app-container workbench-page" v-loading="loading">
    <!-- 顶部工作台标题栏 -->
    <div class="wb-header">
      <div>
        <h2 class="wb-title">业务运营工作台</h2>
        <p class="wb-desc">集中监控待审批草稿单据、当日有效出入库作业、低库存补货与往来账期到期提醒。</p>
      </div>
      <div class="wb-actions">
        <el-button icon="Refresh" @click="getSummary">刷新数据</el-button>
        <el-button type="primary" icon="FullScreen" @click="goTo('/wms/scan')">扫码开单</el-button>
        <el-button type="primary" plain icon="List" @click="goTo('/business/todoCenter')">统一待办中心</el-button>
      </div>
    </div>

    <!-- 10 项分类色标指标卡 -->
    <div class="metric-grid">
      <div
        v-for="card in summaryCardList"
        :key="card.label"
        class="metric-card"
        :class="card.tone"
        @click="goTo(card.route)"
      >
        <div class="metric-head">
          <span class="metric-label">{{ card.label }}</span>
          <span class="metric-icon" :class="card.tone">
            <el-icon :size="15"><component :is="card.icon" /></el-icon>
          </span>
        </div>
        <div class="metric-value">{{ card.value }}</div>
        <div class="metric-foot">
          <span>{{ card.hint }}</span>
          <span class="metric-go">前往 &rarr;</span>
        </div>
      </div>
    </div>

    <!-- 中部：低库存提醒 + 审批待办 -->
    <el-row :gutter="16" class="panel-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="wb-panel">
          <template #header>
            <div class="card-head">
              <div class="head-left">
                <span class="head-dot rose" />
                <span>低库存提醒（按仓库×商品可用库存汇总）</span>
                <el-tag size="small" type="danger" effect="light">{{ (summaryData.lowStockList || []).length }}</el-tag>
              </div>
              <el-button link type="primary" @click="goTo('/wms/replenishment')">补货建议 &gt;</el-button>
            </div>
          </template>
          <el-table :data="summaryData.lowStockList || []" border size="small" empty-text="当前库存充足，暂无低库存预警">
            <el-table-column label="仓库" align="center" min-width="95" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.warehouseName || `仓库#${scope.row.warehouseId}` }}</template>
            </el-table-column>
            <el-table-column label="商品" align="center" min-width="110" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.productName || `商品#${scope.row.productId}` }}</template>
            </el-table-column>
            <el-table-column label="账面" align="right" prop="quantity" width="74" />
            <el-table-column label="可用" align="right" width="74">
              <template #default="scope">
                <span style="color: #ef4444; font-weight: 700">{{ scope.row.availableQuantity ?? scope.row.quantity }}</span>
              </template>
            </el-table-column>
            <el-table-column label="预警下限" align="right" prop="warningMinQty" width="80" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="wb-panel">
          <template #header>
            <div class="card-head">
              <div class="head-left">
                <span class="head-dot amber" />
                <span>审批待办（全业务草稿单据）</span>
                <el-tag size="small" type="warning" effect="light">{{ summaryData.pendingApproveCount || 0 }}</el-tag>
              </div>
              <el-button link type="primary" @click="goTo('/business/todoCenter')">全部待办 &gt;</el-button>
            </div>
          </template>
          <el-table
            :data="summaryData.pendingApproveList || []"
            border
            size="small"
            row-class-name="clickable-row"
            empty-text="太棒了，当前无待审草稿单据"
            @row-click="handlePendingRowClick"
          >
            <el-table-column label="单据类型" align="center" width="105">
              <template #default="scope">
                <el-tag size="small" effect="plain">{{ scope.row.billTypeLabel }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="单据单号" align="center" prop="billNo" min-width="145" />
            <el-table-column label="业务日期" align="center" width="115">
              <template #default="scope">{{ parseTime(scope.row.businessDate, '{y}-{m}-{d}') }}</template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="85">
              <template #default="scope">
                <el-button link type="primary" size="small" @click.stop="handlePendingRowClick(scope.row)">去处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：应收到期 + 应付到期 -->
    <el-row :gutter="16" class="panel-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="wb-panel">
          <template #header>
            <div class="card-head">
              <div class="head-left">
                <span class="head-dot blue" />
                <span>应收到期提醒（7日内到期 / 已逾期）</span>
                <el-tag size="small" effect="light">{{ summaryData.dueReceivableCount || 0 }}</el-tag>
              </div>
              <el-button link type="primary" @click="goTo('/finance/receivable')">前往应收单 &gt;</el-button>
            </div>
          </template>
          <el-table :data="summaryData.dueReceivableList || []" border size="small" empty-text="暂无7日内到期或逾期应收">
            <el-table-column label="应收单号" align="center" prop="receivableNo" min-width="135" />
            <el-table-column label="客户" align="center" min-width="120" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.customerName || `客户#${scope.row.customerId}` }}</template>
            </el-table-column>
            <el-table-column label="应收金额" align="right" prop="amount" width="95" />
            <el-table-column label="剩余未收" align="right" width="95">
              <template #default="scope">
                <span style="color: #d97706; font-weight: 700">
                  {{ scope.row.unsettledAmount ?? (Number(scope.row.amount || 0) - Number(scope.row.receivedAmount || 0)).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="到期日期" align="center" width="110">
              <template #default="scope">{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="wb-panel">
          <template #header>
            <div class="card-head">
              <div class="head-left">
                <span class="head-dot teal" />
                <span>应付到期提醒（7日内到期 / 已逾期）</span>
                <el-tag size="small" type="success" effect="light">{{ summaryData.duePayableCount || 0 }}</el-tag>
              </div>
              <el-button link type="primary" @click="goTo('/finance/payable')">前往应付单 &gt;</el-button>
            </div>
          </template>
          <el-table :data="summaryData.duePayableList || []" border size="small" empty-text="暂无7日内到期或逾期应付">
            <el-table-column label="应付单号" align="center" prop="payableNo" min-width="135" />
            <el-table-column label="供应商" align="center" min-width="120" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.supplierName || `供应商#${scope.row.supplierId}` }}</template>
            </el-table-column>
            <el-table-column label="应付金额" align="right" prop="amount" width="95" />
            <el-table-column label="剩余未付" align="right" width="95">
              <template #default="scope">
                <span style="color: #ef4444; font-weight: 700">
                  {{ scope.row.unsettledAmount ?? (Number(scope.row.amount || 0) - Number(scope.row.paidAmount || 0)).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="到期日期" align="center" width="110">
              <template #default="scope">{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Workbench">
import { useRouter } from "vue-router"
import { getWorkbenchSummary } from "@/api/business/workbench"

const router = useRouter()
const loading = ref(false)
const summaryData = ref({})

const summaryCardList = computed(() => [
  { label: "审批待办", value: summaryData.value.pendingApproveCount || 0, route: "/business/todoCenter", tone: "amber", icon: "Bell", hint: "全业务草稿单" },
  { label: "草稿销售单", value: summaryData.value.draftSaleOrderCount || 0, route: "/business/saleOrder", tone: "blue", icon: "Sell", hint: "待审核销单" },
  { label: "草稿购货单", value: summaryData.value.draftPurOrderCount || 0, route: "/business/purOrder", tone: "indigo", icon: "ShoppingCart", hint: "待审核采单" },
  { label: "草稿入库单", value: summaryData.value.draftInboundCount || 0, route: "/wms/inbound", tone: "blue", icon: "Box", hint: "待验收入库" },
  { label: "草稿出库单", value: summaryData.value.draftOutboundCount || 0, route: "/wms/outbound", tone: "indigo", icon: "Van", hint: "待审核出库" },
  { label: "今日有效入库", value: summaryData.value.todayInboundCount || 0, route: "/wms/inbound", tone: "teal", icon: "Download", hint: "已审核非红冲" },
  { label: "今日有效出库", value: summaryData.value.todayOutboundCount || 0, route: "/wms/outbound", tone: "teal", icon: "Upload", hint: "已审核非红冲" },
  { label: "应收到期", value: summaryData.value.dueReceivableCount || 0, route: "/finance/receivable", tone: "amber", icon: "Clock", hint: "7日内待回款" },
  { label: "应付到期", value: summaryData.value.duePayableCount || 0, route: "/finance/payable", tone: "rose", icon: "Wallet", hint: "7日内待付款" },
  { label: "低库存预警", value: summaryData.value.lowStockCount || 0, route: "/wms/stock", tone: "rose", icon: "Warning", hint: "低于安全库存" }
])

function goTo(path) {
  if (path) router.push(path)
}

function handlePendingRowClick(row) {
  if (row && row.route) {
    router.push(row.route)
  }
}

function getSummary() {
  loading.value = true
  getWorkbenchSummary().then(response => {
    summaryData.value = response.data || {}
  }).finally(() => {
    loading.value = false
  })
}

getSummary()
</script>

<style scoped lang="scss">
.workbench-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.wb-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 18px 22px;
  border-radius: 14px;
  background: var(--panel-bg, #ffffff);
  border: 1px solid var(--panel-border, #e2e8f0);
  box-shadow: var(--panel-shadow);
}

.wb-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--panel-heading, #0f172a);
}

.wb-desc {
  margin: 5px 0 0;
  font-size: 13px;
  color: var(--panel-muted, #64748b);
}

.wb-actions {
  display: flex;
  gap: 8px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
}

.metric-card {
  padding: 16px 18px 13px;
  border-radius: 12px;
  background: var(--panel-bg, #ffffff);
  border: 1px solid var(--panel-border, #e5e7eb);
  box-shadow: var(--panel-shadow);
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: var(--panel-shadow-hover);
    border-color: var(--panel-border-strong, #cbd5e1);
  }
}

.metric-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metric-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--panel-text, #334155);
}

.metric-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;

  &.blue { background: rgba(37, 99, 235, 0.1); color: #2563eb; }
  &.indigo { background: rgba(99, 102, 241, 0.1); color: #6366f1; }
  &.teal { background: rgba(13, 148, 136, 0.12); color: #0d9488; }
  &.amber { background: rgba(245, 158, 11, 0.14); color: #d97706; }
  &.rose { background: rgba(239, 68, 68, 0.12); color: #dc2626; }
}

.metric-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: var(--panel-heading, #0f172a);
  font-variant-numeric: tabular-nums;
  line-height: 1.15;
}

.metric-foot {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--panel-muted, #64748b);
}

.metric-go {
  color: var(--brand-primary, #2563eb);
  font-weight: 500;
}

.panel-row {
  row-gap: 16px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.head-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14.5px;
  font-weight: 650;
  color: var(--panel-heading, #0f172a);
}

.head-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;

  &.rose { background: #ef4444; }
  &.amber { background: #f59e0b; }
  &.blue { background: #2563eb; }
  &.teal { background: #0d9488; }
}

:deep(.clickable-row) {
  cursor: pointer;
}

@media (max-width: 1200px) {
  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
