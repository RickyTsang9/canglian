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
      </el-form-item>
    </el-form>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="销售毛利" name="grossProfit">
        <el-table v-loading="loading.grossProfit" :data="grossProfitList">
          <el-table-column label="商品编码" align="center" prop="productCode" />
          <el-table-column label="商品名称" align="center" prop="productName" />
          <el-table-column label="销售数量" align="center" prop="saleQty" />
          <el-table-column label="销售金额" align="center" prop="saleAmount" />
          <el-table-column label="成本金额" align="center" prop="costAmount" />
          <el-table-column label="毛利" align="center" prop="grossProfit" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="客户贡献" name="customerContribution">
        <el-table v-loading="loading.customerContribution" :data="customerContributionList">
          <el-table-column label="客户编码" align="center" prop="customerCode" />
          <el-table-column label="客户名称" align="center" prop="customerName" />
          <el-table-column label="订单数" align="center" prop="orderCount" />
          <el-table-column label="销售金额" align="center" prop="saleAmount" />
          <el-table-column label="已收贡献" align="center" prop="receivedContribution" />
          <el-table-column label="未收金额" align="center" prop="unpaidAmount" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="商品周转" name="turnover">
        <el-table v-loading="loading.turnover" :data="turnoverList">
          <el-table-column label="商品编码" align="center" prop="productCode" />
          <el-table-column label="商品名称" align="center" prop="productName" />
          <el-table-column label="入库数量" align="center" prop="inboundQty" />
          <el-table-column label="出库数量" align="center" prop="outboundQty" />
          <el-table-column label="库存数量" align="center" prop="stockQty" />
          <el-table-column label="周转率" align="center" prop="turnoverRate" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="库存余额" name="stockBalance">
        <el-table v-loading="loading.stockBalance" :data="stockBalanceList">
          <el-table-column label="仓库编号" align="center" prop="warehouseId" />
          <el-table-column label="库位编号" align="center" prop="locationId" />
          <el-table-column label="商品编码" align="center" prop="productCode" />
          <el-table-column label="商品名称" align="center" prop="productName" />
          <el-table-column label="批次号" align="center" prop="batchNo" />
          <el-table-column label="库存数量" align="center" prop="stockQty" />
          <el-table-column label="可用数量" align="center" prop="availableQty" />
          <el-table-column label="库存金额" align="center" prop="stockAmount" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="资金分析" name="fundAnalysis">
        <el-table v-loading="loading.fundAnalysis" :data="fundAnalysisList">
          <el-table-column label="资金类型" align="center" prop="fundType" />
          <el-table-column label="金额" align="center" prop="amount" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="OperationReport">
import { salesGrossProfit, customerContribution, productTurnover, stockBalance, fundAnalysis } from "@/api/business/report"

const { proxy } = getCurrentInstance()

const activeTab = ref("grossProfit")
const loading = reactive({
  grossProfit: false,
  customerContribution: false,
  turnover: false,
  stockBalance: false,
  fundAnalysis: false
})

const currentDate = new Date()
const currentYear = currentDate.getFullYear()
const queryParams = reactive({
  startDate: `${currentYear}-01-01`,
  endDate: `${currentYear}-12-31`
})

const grossProfitList = ref([])
const customerContributionList = ref([])
const turnoverList = ref([])
const stockBalanceList = ref([])
const fundAnalysisList = ref([])

// 查询全部经营报表
function getAllReports() {
  getGrossProfitList()
  getCustomerContributionList()
  getTurnoverList()
  getStockBalanceList()
  getFundAnalysisList()
}

// 查询销售毛利
function getGrossProfitList() {
  loading.grossProfit = true
  salesGrossProfit(queryParams).then(response => {
    grossProfitList.value = response.data
  }).finally(() => {
    loading.grossProfit = false
  })
}

// 查询客户贡献
function getCustomerContributionList() {
  loading.customerContribution = true
  customerContribution(queryParams).then(response => {
    customerContributionList.value = response.data
  }).finally(() => {
    loading.customerContribution = false
  })
}

// 查询商品周转
function getTurnoverList() {
  loading.turnover = true
  productTurnover(queryParams).then(response => {
    turnoverList.value = response.data
  }).finally(() => {
    loading.turnover = false
  })
}

// 查询库存余额
function getStockBalanceList() {
  loading.stockBalance = true
  stockBalance().then(response => {
    stockBalanceList.value = response.data
  }).finally(() => {
    loading.stockBalance = false
  })
}

// 查询资金分析
function getFundAnalysisList() {
  loading.fundAnalysis = true
  fundAnalysis(queryParams).then(response => {
    fundAnalysisList.value = response.data
  }).finally(() => {
    loading.fundAnalysis = false
  })
}

// 重置查询条件
function resetQuery() {
  proxy.resetForm("queryRef")
  getAllReports()
}

getAllReports()
</script>
