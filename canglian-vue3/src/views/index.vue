<template>
  <div class="app-container home" v-loading="loading">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="summary-card">
          <div class="summary-title">收入金额</div>
          <div class="summary-value">{{ formatAmount(revenueExpenseData?.incomeAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="summary-card">
          <div class="summary-title">支出金额</div>
          <div class="summary-value">{{ formatAmount(revenueExpenseData?.expenseAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="summary-card">
          <div class="summary-title">净额</div>
          <div class="summary-value">{{ formatAmount(revenueExpenseData?.netAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="summary-card">
          <div class="summary-title">利润金额</div>
          <div class="summary-value">{{ formatAmount(profitLossData?.profitAmount) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="filter-card">
      <el-row :gutter="16" align="middle">
        <el-col :xs="24" :sm="12" :md="10">
          <el-date-picker
            v-model="dateRange"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="14" class="range-text">
          <span v-if="dateRangeText">统计周期：{{ dateRangeText }}</span>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header><span>利润表</span></template>
          <div ref="profitLossChartRef" class="chart-panel"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header><span>收支汇总</span></template>
          <div ref="revenueExpenseChartRef" class="chart-panel"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header><span>成本结构</span></template>
          <div ref="costStructureChartRef" class="chart-panel"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header><span>应收账龄</span></template>
          <div ref="receivableAgingChartRef" class="chart-panel"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24">
        <el-card>
          <template #header><span>应付账龄</span></template>
          <div ref="payableAgingChartRef" class="chart-panel"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import * as echarts from "echarts"
import { receivableAging, payableAging, profitLoss, revenueExpense, costStructure } from "@/api/business/report"

const loading = ref(false)
const dateRange = ref([])
const dateRangeText = computed(() => {
  if (Array.isArray(dateRange.value) && dateRange.value.length === 2) {
    return `${dateRange.value[0]} - ${dateRange.value[1]}`
  }
  return ""
})

const dashboardQuery = reactive({
  startDate: "",
  endDate: ""
})

const profitLossData = ref(null)
const revenueExpenseData = ref(null)
const costStructureList = ref([])
const receivableAgingList = ref([])
const payableAgingList = ref([])

const profitLossChartRef = ref(null)
const revenueExpenseChartRef = ref(null)
const costStructureChartRef = ref(null)
const receivableAgingChartRef = ref(null)
const payableAgingChartRef = ref(null)

let profitLossChartInstance = null
let revenueExpenseChartInstance = null
let costStructureChartInstance = null
let receivableAgingChartInstance = null
let payableAgingChartInstance = null

function formatDateValue(dateValue) {
  const year = dateValue.getFullYear()
  const month = String(dateValue.getMonth() + 1).padStart(2, "0")
  const day = String(dateValue.getDate()).padStart(2, "0")
  return `${year}-${month}-${day}`
}

function initializeDateRange() {
  const currentDate = new Date()
  const previousYear = currentDate.getFullYear() - 1
  const previousYearStartDate = new Date(previousYear, 0, 1)
  const previousYearEndDate = new Date(previousYear, 11, 31)
  const formattedStartDate = formatDateValue(previousYearStartDate)
  const formattedEndDate = formatDateValue(previousYearEndDate)
  dateRange.value = [formattedStartDate, formattedEndDate]
  dashboardQuery.startDate = formattedStartDate
  dashboardQuery.endDate = formattedEndDate
}

function formatAmount(value) {
  const numberValue = Number(value || 0)
  return numberValue.toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function loadDashboardData() {
  if (!dashboardQuery.startDate || !dashboardQuery.endDate) {
    return
  }
  loading.value = true
  Promise.all([
    profitLoss({ startDate: dashboardQuery.startDate, endDate: dashboardQuery.endDate }),
    revenueExpense({ startDate: dashboardQuery.startDate, endDate: dashboardQuery.endDate }),
    costStructure({ startDate: dashboardQuery.startDate, endDate: dashboardQuery.endDate }),
    receivableAging({}),
    payableAging({})
  ]).then(([profitLossResponse, revenueExpenseResponse, costStructureResponse, receivableAgingResponse, payableAgingResponse]) => {
    profitLossData.value = profitLossResponse.data
    revenueExpenseData.value = revenueExpenseResponse.data
    costStructureList.value = costStructureResponse.data || []
    receivableAgingList.value = receivableAgingResponse.data || []
    payableAgingList.value = payableAgingResponse.data || []
    nextTick(() => {
      renderProfitLossChart()
      renderRevenueExpenseChart()
      renderCostStructureChart()
      renderReceivableAgingChart()
      renderPayableAgingChart()
    })
  }).finally(() => {
    loading.value = false
  })
}

function renderProfitLossChart() {
  if (!profitLossChartRef.value || !profitLossData.value) {
    return
  }
  if (!profitLossChartInstance) {
    profitLossChartInstance = echarts.init(profitLossChartRef.value)
  }
  const profitLossChartData = [
    Number(profitLossData.value.incomeAmount || 0),
    Number(profitLossData.value.costAmount || 0),
    Number(profitLossData.value.profitAmount || 0)
  ]
  profitLossChartInstance.setOption({
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: ["收入金额", "成本费用", "利润金额"] },
    yAxis: { type: "value" },
    series: [{ type: "bar", data: profitLossChartData }]
  })
}

function renderRevenueExpenseChart() {
  if (!revenueExpenseChartRef.value || !revenueExpenseData.value) {
    return
  }
  if (!revenueExpenseChartInstance) {
    revenueExpenseChartInstance = echarts.init(revenueExpenseChartRef.value)
  }
  const revenueExpenseChartData = [
    Number(revenueExpenseData.value.incomeAmount || 0),
    Number(revenueExpenseData.value.expenseAmount || 0),
    Number(revenueExpenseData.value.netAmount || 0)
  ]
  revenueExpenseChartInstance.setOption({
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: ["收入金额", "支出金额", "净额"] },
    yAxis: { type: "value" },
    series: [{ type: "bar", data: revenueExpenseChartData }]
  })
}

function renderCostStructureChart() {
  if (!costStructureChartRef.value) {
    return
  }
  if (!costStructureChartInstance) {
    costStructureChartInstance = echarts.init(costStructureChartRef.value)
  }
  const chartData = costStructureList.value.map(item => {
    return { name: item.costType, value: Number(item.costAmount || 0) }
  })
  costStructureChartInstance.setOption({
    tooltip: { trigger: "item" },
    legend: { top: "bottom" },
    series: [
      {
        type: "pie",
        radius: "50%",
        data: chartData
      }
    ]
  })
}

function renderReceivableAgingChart() {
  if (!receivableAgingChartRef.value) {
    return
  }
  if (receivableAgingList.value.length === 0) {
    if (receivableAgingChartInstance) {
      receivableAgingChartInstance.clear()
    }
    return
  }
  if (!receivableAgingChartInstance) {
    receivableAgingChartInstance = echarts.init(receivableAgingChartRef.value)
  }
  const labels = receivableAgingList.value.map(item => `客户${item.dimensionId}`)
  const notDueAmounts = receivableAgingList.value.map(item => Number(item.notDueAmount || 0))
  const days0To30Amounts = receivableAgingList.value.map(item => Number(item.days0To30Amount || 0))
  const days31To60Amounts = receivableAgingList.value.map(item => Number(item.days31To60Amount || 0))
  const days61To90Amounts = receivableAgingList.value.map(item => Number(item.days61To90Amount || 0))
  const days90AboveAmounts = receivableAgingList.value.map(item => Number(item.days90AboveAmount || 0))
  receivableAgingChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["未逾期", "0-30天", "31-60天", "61-90天", "90天以上"] },
    xAxis: { type: "category", data: labels },
    yAxis: { type: "value" },
    series: [
      { name: "未逾期", type: "bar", stack: "total", data: notDueAmounts },
      { name: "0-30天", type: "bar", stack: "total", data: days0To30Amounts },
      { name: "31-60天", type: "bar", stack: "total", data: days31To60Amounts },
      { name: "61-90天", type: "bar", stack: "total", data: days61To90Amounts },
      { name: "90天以上", type: "bar", stack: "total", data: days90AboveAmounts }
    ]
  })
}

function renderPayableAgingChart() {
  if (!payableAgingChartRef.value) {
    return
  }
  if (payableAgingList.value.length === 0) {
    if (payableAgingChartInstance) {
      payableAgingChartInstance.clear()
    }
    return
  }
  if (!payableAgingChartInstance) {
    payableAgingChartInstance = echarts.init(payableAgingChartRef.value)
  }
  const labels = payableAgingList.value.map(item => `供应商${item.dimensionId}`)
  const notDueAmounts = payableAgingList.value.map(item => Number(item.notDueAmount || 0))
  const days0To30Amounts = payableAgingList.value.map(item => Number(item.days0To30Amount || 0))
  const days31To60Amounts = payableAgingList.value.map(item => Number(item.days31To60Amount || 0))
  const days61To90Amounts = payableAgingList.value.map(item => Number(item.days61To90Amount || 0))
  const days90AboveAmounts = payableAgingList.value.map(item => Number(item.days90AboveAmount || 0))
  payableAgingChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["未逾期", "0-30天", "31-60天", "61-90天", "90天以上"] },
    xAxis: { type: "category", data: labels },
    yAxis: { type: "value" },
    series: [
      { name: "未逾期", type: "bar", stack: "total", data: notDueAmounts },
      { name: "0-30天", type: "bar", stack: "total", data: days0To30Amounts },
      { name: "31-60天", type: "bar", stack: "total", data: days31To60Amounts },
      { name: "61-90天", type: "bar", stack: "total", data: days61To90Amounts },
      { name: "90天以上", type: "bar", stack: "total", data: days90AboveAmounts }
    ]
  })
}

function resizeCharts() {
  if (profitLossChartInstance) {
    profitLossChartInstance.resize()
  }
  if (revenueExpenseChartInstance) {
    revenueExpenseChartInstance.resize()
  }
  if (costStructureChartInstance) {
    costStructureChartInstance.resize()
  }
  if (receivableAgingChartInstance) {
    receivableAgingChartInstance.resize()
  }
  if (payableAgingChartInstance) {
    payableAgingChartInstance.resize()
  }
}

watch(dateRange, () => {
  if (Array.isArray(dateRange.value) && dateRange.value.length === 2) {
    dashboardQuery.startDate = dateRange.value[0]
    dashboardQuery.endDate = dateRange.value[1]
    loadDashboardData()
  }
})

onMounted(() => {
  initializeDateRange()
  loadDashboardData()
  window.addEventListener("resize", resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeCharts)
  if (profitLossChartInstance) {
    profitLossChartInstance.dispose()
  }
  if (revenueExpenseChartInstance) {
    revenueExpenseChartInstance.dispose()
  }
  if (costStructureChartInstance) {
    costStructureChartInstance.dispose()
  }
  if (receivableAgingChartInstance) {
    receivableAgingChartInstance.dispose()
  }
  if (payableAgingChartInstance) {
    payableAgingChartInstance.dispose()
  }
})
</script>

<style scoped lang="scss">
.home {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-row {
  margin-bottom: 8px;
}

.summary-card {
  text-align: center;
}

.summary-title {
  font-size: 14px;
  color: #909399;
}

.summary-value {
  font-size: 22px;
  font-weight: 600;
  margin-top: 6px;
}

.chart-row {
  margin-top: 8px;
}

.chart-panel {
  width: 100%;
  height: 320px;
}

.filter-card {
  margin-top: 4px;
}

.range-text {
  text-align: right;
  color: #909399;
}
</style>
