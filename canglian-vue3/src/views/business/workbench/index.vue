<template>
  <div class="app-container">
    <el-row :gutter="16" class="mb16">
      <el-col :xl="4" :lg="6" :md="8" :sm="12" :xs="24" v-for="summaryCard in summaryCardList" :key="summaryCard.label">
        <el-card shadow="hover">
          <div class="summary-title">{{ summaryCard.label }}</div>
          <div class="summary-value">{{ summaryCard.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>低库存提醒</template>
          <el-table :data="summaryData.lowStockList || []" border>
            <el-table-column label="仓库编号" align="center" prop="warehouseId" />
            <el-table-column label="商品编号" align="center" prop="productId" />
            <el-table-column label="库位编号" align="center" prop="locationId" />
            <el-table-column label="当前库存" align="center" prop="quantity" />
            <el-table-column label="最小库存" align="center" prop="warningMinQty" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>审批待办</template>
          <el-table :data="summaryData.pendingApproveList || []" border>
            <el-table-column label="单据类型" align="center" prop="billTypeLabel" />
            <el-table-column label="单据编号" align="center" prop="billId" />
            <el-table-column label="单据单号" align="center" prop="billNo" />
            <el-table-column label="业务日期" align="center" prop="businessDate" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.businessDate) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>应收到期提醒</template>
          <el-table :data="summaryData.dueReceivableList || []" border>
            <el-table-column label="应收单号" align="center" prop="receivableNo" />
            <el-table-column label="客户编号" align="center" prop="customerId" />
            <el-table-column label="应收金额" align="center" prop="amount" />
            <el-table-column label="已收金额" align="center" prop="receivedAmount" />
            <el-table-column label="到期日期" align="center" prop="dueDate" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.dueDate) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>应付到期提醒</template>
          <el-table :data="summaryData.duePayableList || []" border>
            <el-table-column label="应付单号" align="center" prop="payableNo" />
            <el-table-column label="供应商编号" align="center" prop="supplierId" />
            <el-table-column label="应付金额" align="center" prop="amount" />
            <el-table-column label="已付金额" align="center" prop="paidAmount" />
            <el-table-column label="到期日期" align="center" prop="dueDate" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.dueDate) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Workbench">
import { getWorkbenchSummary } from "@/api/business/workbench"

const summaryData = ref({})
const summaryCardList = computed(() => {
  return [
    { label: "审批待办", value: summaryData.value.pendingApproveCount || 0 },
    { label: "草稿销售单", value: summaryData.value.draftSaleOrderCount || 0 },
    { label: "草稿购货单", value: summaryData.value.draftPurOrderCount || 0 },
    { label: "草稿入库单", value: summaryData.value.draftInboundCount || 0 },
    { label: "草稿出库单", value: summaryData.value.draftOutboundCount || 0 },
    { label: "今日入库", value: summaryData.value.todayInboundCount || 0 },
    { label: "今日出库", value: summaryData.value.todayOutboundCount || 0 },
    { label: "应收到期", value: summaryData.value.dueReceivableCount || 0 },
    { label: "应付到期", value: summaryData.value.duePayableCount || 0 },
    { label: "低库存", value: summaryData.value.lowStockCount || 0 }
  ]
})

// 查询业务工作台汇总
function getSummary() {
  getWorkbenchSummary().then(response => {
    summaryData.value = response.data || {}
  })
}

getSummary()
</script>

<style scoped>
.summary-title {
  font-size: 14px;
  color: #606266;
}

.summary-value {
  margin-top: 12px;
  font-size: 30px;
  font-weight: 600;
  color: #303133;
}

.mb16 {
  margin-bottom: 16px;
}
</style>
