<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker v-model="queryParams.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开始日期" />
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker v-model="queryParams.endDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getAllReports">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="warning" plain icon="Download" @click="handleExportActive" v-hasPermi="['business:report:export']">导出当前报表 Excel</el-button>
      </el-form-item>
    </el-form>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="销售毛利（真实成本）" name="grossProfit">
        <el-alert
          title="统计口径：严格按客户签收确认的销售出库单与有效销售退货单汇总（数量统一换算为基础计量单位），结转成本取自历史真实成本流水。"
          type="info"
          show-icon
          :closable="false"
          style="margin-bottom: 10px"
        />
        <el-table v-loading="loading.grossProfit" :data="grossProfitList" border>
          <el-table-column label="商品编码" align="center" prop="productCode" min-width="115" />
          <el-table-column label="商品名称" align="center" prop="productName" min-width="150" show-overflow-tooltip />
          <el-table-column label="签收销量(基础)" align="right" header-align="right" prop="saleQty" min-width="115" />
          <el-table-column label="退货数量(基础)" align="right" header-align="right" prop="returnQty" min-width="115" />
          <el-table-column label="净销量(基础)" align="right" header-align="right" prop="netSaleQty" min-width="110" />
          <el-table-column label="净销售额" align="right" header-align="right" prop="netSaleAmount" min-width="115">
            <template #default="scope">{{ formatMoney(scope.row.netSaleAmount) }}</template>
          </el-table-column>
          <el-table-column label="真实成本" align="right" header-align="right" prop="costAmount" min-width="115">
            <template #default="scope">{{ formatMoney(scope.row.costAmount) }}</template>
          </el-table-column>
          <el-table-column label="销售毛利" align="right" header-align="right" prop="grossProfit" min-width="115">
            <template #default="scope">
              <span :style="Number(scope.row.grossProfit || 0) >= 0 ? 'color:#67c23a;font-weight:600' : 'color:#f56c6c;font-weight:600'">{{ formatMoney(scope.row.grossProfit) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="毛利率" align="center" width="95">
            <template #default="scope">{{ Number(scope.row.grossMarginRate || 0).toFixed(2) }}%</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="客户贡献" name="customerContribution">
        <el-table v-loading="loading.customerContribution" :data="customerContributionList" border>
          <el-table-column label="客户编码" align="center" prop="customerCode" min-width="115" />
          <el-table-column label="客户名称" align="center" prop="customerName" min-width="150" show-overflow-tooltip />
          <el-table-column label="签收单数" align="center" prop="orderCount" width="100" />
          <el-table-column label="净销售金额" align="right" header-align="right" min-width="125">
            <template #default="scope">{{ formatMoney(scope.row.saleAmount) }}</template>
          </el-table-column>
          <el-table-column label="已收贡献" align="right" header-align="right" min-width="125">
            <template #default="scope">{{ formatMoney(scope.row.receivedContribution) }}</template>
          </el-table-column>
          <el-table-column label="剩余未收" align="right" header-align="right" min-width="125">
            <template #default="scope">{{ formatMoney(scope.row.unpaidAmount) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="商品周转" name="turnover">
        <el-table v-loading="loading.turnover" :data="turnoverList" border>
          <el-table-column label="商品编码" align="center" prop="productCode" min-width="115" />
          <el-table-column label="商品名称" align="center" prop="productName" min-width="150" show-overflow-tooltip />
          <el-table-column label="商业入库数" align="right" header-align="right" prop="inboundQty" min-width="115" />
          <el-table-column label="商业出库数" align="right" header-align="right" prop="outboundQty" min-width="115" />
          <el-table-column label="当前库存数" align="right" header-align="right" prop="stockQty" min-width="115" />
          <el-table-column label="周转率" align="center" prop="turnoverRate" width="110">
            <template #default="scope">{{ Number(scope.row.turnoverRate || 0).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="库存余额" name="stockBalance">
        <el-table v-loading="loading.stockBalance" :data="stockBalanceList" border>
          <el-table-column label="仓库" align="center" prop="warehouseName" min-width="120" />
          <el-table-column label="库位" align="center" min-width="115">
            <template #default="scope">{{ scope.row.locationName || '默认库位' }}</template>
          </el-table-column>
          <el-table-column label="商品编码" align="center" prop="productCode" min-width="110" />
          <el-table-column label="商品名称" align="center" prop="productName" min-width="145" show-overflow-tooltip />
          <el-table-column label="批次号" align="center" prop="batchNo" min-width="120">
            <template #default="scope">{{ scope.row.batchNo || '-' }}</template>
          </el-table-column>
          <el-table-column label="库存数量" align="right" header-align="right" prop="stockQty" width="100" />
          <el-table-column label="可用数量" align="right" header-align="right" prop="availableQty" width="100" />
          <el-table-column label="库存金额" align="right" header-align="right" width="120">
            <template #default="scope">{{ formatMoney(scope.row.stockAmount) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="资金分析" name="fundAnalysis">
        <el-table v-loading="loading.fundAnalysis" :data="fundAnalysisList" border>
          <el-table-column label="资金类型" align="center" min-width="180">
            <template #default="scope">
              <span>{{ scope.row.fundTypeName || fundTypeLabelMap[scope.row.fundType] || scope.row.fundType }}</span>
            </template>
          </el-table-column>
          <el-table-column label="金额" align="right" header-align="right" min-width="160">
            <template #default="scope">{{ formatMoney(scope.row.amount) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="OperationReport">
import { salesGrossProfit, customerContribution, productTurnover, stockBalance, fundAnalysis, exportReport } from "@/api/business/report"

const { proxy } = getCurrentInstance()

function formatMoney(v) {
  const n = Number(v || 0)
  return n < 0 ? `-￥${Math.abs(n).toFixed(2)}` : `￥${n.toFixed(2)}`
}

const activeTab = ref("grossProfit")
const loading = reactive({
  grossProfit: false,
  customerContribution: false,
  turnover: false,
  stockBalance: false,
  fundAnalysis: false
})

const fundTypeLabelMap = {
  receipt: "销售收款流入",
  payment: "采购付款流出",
  expense: "期间费用支出",
  bad_debt_recover: "坏账收回流入",
  transfer_fee: "内部互转手续费"
}

const currentYear = new Date().getFullYear()
const queryParams = reactive({
  startDate: `${currentYear}-01-01`,
  endDate: `${currentYear}-12-31`
})

const grossProfitList = ref([])
const customerContributionList = ref([])
const turnoverList = ref([])
const stockBalanceList = ref([])
const fundAnalysisList = ref([])

const tabExportMap = {
  grossProfit: { type: "salesGrossProfit", file: "销售毛利报表.xlsx" },
  customerContribution: { type: "customerContribution", file: "客户贡献报表.xlsx" },
  turnover: { type: "productTurnover", file: "商品周转报表.xlsx" },
  stockBalance: { type: "stockBalance", file: "库存余额报表.xlsx" },
  fundAnalysis: { type: "fundAnalysis", file: "资金分析报表.xlsx" }
}

function handleExportActive() {
  const cfg = tabExportMap[activeTab.value]
  if (!cfg) return
  exportReport({ reportType: cfg.type, ...queryParams }).then(blob => {
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", cfg.file)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }).catch(() => {
    proxy.$modal.msgError("报表导出失败")
  })
}

function getAllReports() {
  getGrossProfitList()
  getCustomerContributionList()
  getTurnoverList()
  getStockBalanceList()
  getFundAnalysisList()
}

function getGrossProfitList() {
  loading.grossProfit = true
  salesGrossProfit(queryParams).then(response => {
    grossProfitList.value = response.data || []
  }).finally(() => {
    loading.grossProfit = false
  })
}

function getCustomerContributionList() {
  loading.customerContribution = true
  customerContribution(queryParams).then(response => {
    customerContributionList.value = response.data || []
  }).finally(() => {
    loading.customerContribution = false
  })
}

function getTurnoverList() {
  loading.turnover = true
  productTurnover(queryParams).then(response => {
    turnoverList.value = response.data || []
  }).finally(() => {
    loading.turnover = false
  })
}

function getStockBalanceList() {
  loading.stockBalance = true
  stockBalance().then(response => {
    stockBalanceList.value = response.data || []
  }).finally(() => {
    loading.stockBalance = false
  })
}

function getFundAnalysisList() {
  loading.fundAnalysis = true
  fundAnalysis(queryParams).then(response => {
    fundAnalysisList.value = response.data || []
  }).finally(() => {
    loading.fundAnalysis = false
  })
}

function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.startDate = `${currentYear}-01-01`
  queryParams.endDate = `${currentYear}-12-31`
  getAllReports()
}

getAllReports()
</script>
