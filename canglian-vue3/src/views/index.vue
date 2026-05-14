<template>
  <div class="app-container home" v-loading="loading">
    <section class="dashboard-header">
      <div>
        <h2>经营驾驶舱</h2>
        <p>汇总当前周期内的收支、利润、成本结构和往来账龄。</p>
      </div>
      <div class="dashboard-filter">
        <span v-if="dateRangeText">统计周期：{{ dateRangeText }}</span>
        <div class="date-picker-wrap">
          <el-date-picker
            v-model="dateRange"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </div>
      </div>
    </section>

    <el-alert
      v-if="dashboardError"
      class="dashboard-alert"
      title="首页报表暂时无法加载"
      :description="dashboardError"
      type="warning"
      show-icon
      :closable="false"
    />

    <el-row :gutter="16" class="summary-row">
      <el-col v-for="summaryItem in summaryList" :key="summaryItem.title" :xs="24" :sm="12" :md="6">
        <div class="summary-card">
          <div class="summary-card-top">
            <div class="summary-icon" :class="summaryItem.type">{{ summaryItem.icon }}</div>
            <div>
              <div class="summary-title">{{ summaryItem.title }}</div>
              <div class="summary-subtitle">{{ summaryItem.subtitle }}</div>
            </div>
          </div>
          <div class="summary-value">{{ formatAmount(summaryItem.value) }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span>利润表</span></template>
          <div v-if="profitLossData" ref="profitLossChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无利润表数据" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span>收支汇总</span></template>
          <div v-if="revenueExpenseData" ref="revenueExpenseChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无收支汇总数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span>成本结构</span></template>
          <div v-if="costStructureList.length > 0" ref="costStructureChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无成本结构数据" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span>应收账龄</span></template>
          <div v-if="receivableAgingList.length > 0" ref="receivableAgingChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无应收账龄数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24">
        <el-card class="chart-card">
          <template #header><span>应付账龄</span></template>
          <div v-if="payableAgingList.length > 0" ref="payableAgingChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无应付账龄数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import * as echarts from "echarts"
import { receivableAging, payableAging, profitLoss, revenueExpense, costStructure } from "@/api/business/report"
import useSettingsStore from "@/store/modules/settings"

const loading = ref(false)
const settingsStore = useSettingsStore()
const dateRange = ref([])
const dashboardError = ref("")
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

const summaryList = computed(() => [
  {
    title: "收入金额",
    subtitle: "收款合计",
    value: revenueExpenseData.value?.incomeAmount,
    icon: "收",
    type: "income"
  },
  {
    title: "支出金额",
    subtitle: "付款与费用",
    value: revenueExpenseData.value?.expenseAmount,
    icon: "支",
    type: "expense"
  },
  {
    title: "净额",
    subtitle: "收支结余",
    value: revenueExpenseData.value?.netAmount,
    icon: "净",
    type: "net"
  },
  {
    title: "利润金额",
    subtitle: "收入减成本",
    value: profitLossData.value?.profitAmount,
    icon: "利",
    type: "profit"
  }
])

const chartTextColor = computed(() => settingsStore.isDark ? "#d6deeb" : "#526171")
const chartAxisLineColor = computed(() => settingsStore.isDark ? "#314057" : "#d8e1ec")
const chartSplitLineColor = computed(() => settingsStore.isDark ? "#263449" : "#edf1f7")

// 获取首页图表坐标轴通用样式
function getChartAxisStyle() {
  return {
    axisLabel: { color: chartTextColor.value },
    axisLine: { lineStyle: { color: chartAxisLineColor.value } },
    splitLine: { lineStyle: { color: chartSplitLineColor.value } }
  }
}

// 获取首页图表图例通用样式
function getChartLegendStyle() {
  return {
    textStyle: { color: chartTextColor.value }
  }
}

function formatDateValue(dateValue) {
  const year = dateValue.getFullYear()
  const month = String(dateValue.getMonth() + 1).padStart(2, "0")
  const day = String(dateValue.getDate()).padStart(2, "0")
  return `${year}-${month}-${day}`
}

// 初始化首页默认统计周期
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

// 格式化首页金额展示
function formatAmount(value) {
  const numberValue = Number(value || 0)
  return numberValue.toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 加载首页驾驶舱报表
function loadDashboardData() {
  if (!dashboardQuery.startDate || !dashboardQuery.endDate) {
    return
  }
  loading.value = true
  dashboardError.value = ""
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
  }).catch(() => {
    clearDashboardData()
    dashboardError.value = "请检查报表相关数据表字段是否已升级到最新结构，重点确认 fin_receipt、fin_payment、fin_expense 是否包含 amount 字段。"
  }).finally(() => {
    loading.value = false
  })
}

// 清空首页报表数据
function clearDashboardData() {
  profitLossData.value = null
  revenueExpenseData.value = null
  costStructureList.value = []
  receivableAgingList.value = []
  payableAgingList.value = []
  clearChartInstances()
}

// 清空图表实例
function clearChartInstances() {
  if (profitLossChartInstance) {
    profitLossChartInstance.clear()
  }
  if (revenueExpenseChartInstance) {
    revenueExpenseChartInstance.clear()
  }
  if (costStructureChartInstance) {
    costStructureChartInstance.clear()
  }
  if (receivableAgingChartInstance) {
    receivableAgingChartInstance.clear()
  }
  if (payableAgingChartInstance) {
    payableAgingChartInstance.clear()
  }
}

// 渲染利润表图表
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
    xAxis: { type: "category", data: ["收入金额", "成本费用", "利润金额"], ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [{ type: "bar", data: profitLossChartData }]
  })
}

// 渲染收支汇总图表
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
    xAxis: { type: "category", data: ["收入金额", "支出金额", "净额"], ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [{ type: "bar", data: revenueExpenseChartData }]
  })
}

// 渲染成本结构图表
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
    legend: { top: "bottom", ...getChartLegendStyle() },
    series: [
      {
        type: "pie",
        radius: "50%",
        data: chartData
      }
    ]
  })
}

// 渲染应收账龄图表
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
    legend: { data: ["未逾期", "0-30天", "31-60天", "61-90天", "90天以上"], ...getChartLegendStyle() },
    xAxis: { type: "category", data: labels, ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [
      { name: "未逾期", type: "bar", stack: "total", data: notDueAmounts },
      { name: "0-30天", type: "bar", stack: "total", data: days0To30Amounts },
      { name: "31-60天", type: "bar", stack: "total", data: days31To60Amounts },
      { name: "61-90天", type: "bar", stack: "total", data: days61To90Amounts },
      { name: "90天以上", type: "bar", stack: "total", data: days90AboveAmounts }
    ]
  })
}

// 渲染应付账龄图表
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
    legend: { data: ["未逾期", "0-30天", "31-60天", "61-90天", "90天以上"], ...getChartLegendStyle() },
    xAxis: { type: "category", data: labels, ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [
      { name: "未逾期", type: "bar", stack: "total", data: notDueAmounts },
      { name: "0-30天", type: "bar", stack: "total", data: days0To30Amounts },
      { name: "31-60天", type: "bar", stack: "total", data: days31To60Amounts },
      { name: "61-90天", type: "bar", stack: "total", data: days61To90Amounts },
      { name: "90天以上", type: "bar", stack: "total", data: days90AboveAmounts }
    ]
  })
}

// 重新计算首页图表尺寸
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

watch(() => settingsStore.isDark, () => {
  nextTick(() => {
    renderProfitLossChart()
    renderRevenueExpenseChart()
    renderCostStructureChart()
    renderReceivableAgingChart()
    renderPayableAgingChart()
  })
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
  background: var(--page-bg, #f5f7fb);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  padding: 20px 22px;
  border: 1px solid var(--panel-border, #e8edf5);
  border-radius: 8px;
  background: var(--home-header-bg, linear-gradient(135deg, #ffffff 0%, #f5fbff 100%));
}

.dashboard-header h2 {
  margin: 0;
  color: var(--panel-heading, #1f2d3d);
  font-size: 22px;
  font-weight: 650;
}

.dashboard-header p {
  margin: 8px 0 0;
  color: var(--panel-muted, #7a8599);
  font-size: 14px;
}

.dashboard-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--panel-muted, #7a8599);
  font-size: 13px;
  white-space: nowrap;
}

.date-picker-wrap {
  max-width: 100%;
}

.dashboard-alert {
  border-radius: 8px;
}

.summary-row {
  row-gap: 16px;
}

.summary-card {
  min-height: 128px;
  padding: 18px;
  border: 1px solid var(--panel-border, #e8edf5);
  border-radius: 8px;
  background: var(--panel-bg, #ffffff);
  box-shadow: var(--panel-shadow, 0 10px 24px rgba(31, 45, 61, 0.04));
}

.summary-card-top {
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  color: #ffffff;
  font-size: 15px;
  font-weight: 650;
}

.summary-icon.income {
  background: #1677ff;
}

.summary-icon.expense {
  background: #f59e0b;
}

.summary-icon.net {
  background: #0f8f8f;
}

.summary-icon.profit {
  background: #22a06b;
}

.summary-title {
  font-size: 14px;
  color: var(--panel-text, #303846);
  font-weight: 600;
}

.summary-subtitle {
  margin-top: 4px;
  color: var(--panel-muted, #8a94a6);
  font-size: 12px;
}

.summary-value {
  margin-top: 20px;
  color: var(--panel-heading, #152033);
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
  word-break: break-all;
}

.chart-row {
  margin-top: 8px;
}

.chart-panel {
  width: 100%;
  height: 320px;
}

.chart-card {
  min-height: 402px;
}

.chart-empty {
  height: 320px;
}

@media (max-width: 900px) {
  .dashboard-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .dashboard-filter {
    align-items: flex-start;
    flex-direction: column;
    width: 100%;
  }

  .date-picker-wrap {
    width: 100%;
  }

  .date-picker-wrap :deep(.el-date-editor) {
    width: 100%;
  }
}

@media (max-width: 560px) {
  .home {
    gap: 12px;
  }

  .dashboard-header {
    padding: 16px;
  }

  .dashboard-header h2 {
    font-size: 20px;
  }

  .summary-card {
    min-height: 112px;
    padding: 16px;
  }

  .chart-card {
    min-height: 360px;
  }

  .chart-panel,
  .chart-empty {
    height: 280px;
  }
}
</style>
