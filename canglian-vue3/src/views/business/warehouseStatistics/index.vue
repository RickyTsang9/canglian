<template>
  <div class="app-container warehouse-statistics-page" v-loading="loading">
    <el-card class="panel-card mode-panel" shadow="never">
      <div class="mode-list">
        <div
          v-for="modeItem in statisticsViewOptions"
          :key="modeItem.value"
          class="mode-item"
          :class="{ active: queryParams.statisticsView === modeItem.value }"
          @click="switchStatisticsView(modeItem.value)"
        >
          <div class="mode-icon" :class="`mode-icon-${modeItem.value}`">{{ modeItem.iconText }}</div>
          <div class="mode-name">{{ modeItem.label }}</div>
          <div class="mode-badge">{{ modeItem.count }}</div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="12" class="panel-row">
      <el-col :xs="24" :sm="24" :md="24" :lg="7" :xl="6">
        <el-card class="panel-card business-list-card" shadow="never">
          <template #header>
            <div class="panel-title-wrap">
              <span class="panel-title">入库业务清查</span>
              <span class="panel-count">{{ overviewData.inboundOperationList.length }}</span>
            </div>
          </template>
          <div class="business-list-scroll">
            <el-empty v-if="overviewData.inboundOperationList.length === 0" description="暂无入库作业记录" :image-size="100" />
            <ul v-else class="business-list">
              <li v-for="inboundItem in overviewData.inboundOperationList" :key="`inbound-${inboundItem.billNo}-${inboundItem.businessTime}`" class="business-item">
                <span class="business-dot inbound"></span>
                <div class="business-main">
                  <div class="business-line">
                    <span class="business-no">{{ inboundItem.billNo || '-' }}</span>
                    <span class="business-qty">x {{ formatIntegerValue(inboundItem.totalQuantity) }}</span>
                  </div>
                  <div class="business-sub">{{ inboundItem.summary || getBusinessTypeLabel(inboundItem.businessType, true) }}</div>
                </div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="24" :lg="10" :xl="12">
        <el-card class="panel-card center-panel" shadow="never">
          <template #header>
            <div class="filter-toolbar">
              <div class="filter-left">
                <span class="filter-label">仓库筛选</span>
                <el-select
                  v-model="queryParams.warehouseId"
                  placeholder="请选择仓库"
                  clearable
                  filterable
                  class="warehouse-select"
                  @change="handleWarehouseChange"
                >
                  <el-option
                    v-for="warehouseOption in warehouseOptions"
                    :key="warehouseOption.warehouseId"
                    :label="warehouseOption.warehouseName"
                    :value="warehouseOption.warehouseId"
                  />
                </el-select>
              </div>
              <div class="quick-range-list">
                <el-button
                  v-for="rangeItem in quickRangeOptions"
                  :key="rangeItem.value"
                  size="small"
                  :type="quickRangeKey === rangeItem.value ? 'primary' : 'default'"
                  plain
                  @click="applyQuickRange(rangeItem.value)"
                >
                  {{ rangeItem.label }}
                </el-button>
              </div>
            </div>
          </template>

          <div class="query-row">
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              value-format="YYYY-MM-DD HH:mm:ss"
              range-separator="-"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              class="date-picker"
              @change="handleDateRangeChange"
            />
            <el-button type="primary" icon="Search" v-hasPermi="['business:warehouseStatistics:query']" @click="handleQuery">查询</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </div>

          <div class="metric-grid">
            <div class="metric-item">
              <div class="metric-title">入库单数</div>
              <div class="metric-value">{{ formatIntegerValue(overviewData.inboundCard.billCount) }}<span class="metric-unit">单</span></div>
            </div>
            <div class="metric-item">
              <div class="metric-title">入库产品总数</div>
              <div class="metric-value">{{ formatIntegerValue(overviewData.inboundCard.totalQuantity) }}<span class="metric-unit">件</span></div>
            </div>
            <div class="metric-item">
              <div class="metric-title">入库产品价值</div>
              <div class="metric-value amount">{{ formatNumberValue(overviewData.inboundCard.totalAmount) }}<span class="metric-unit">元</span></div>
            </div>
            <div class="metric-item">
              <div class="metric-title">出库单数</div>
              <div class="metric-value">{{ formatIntegerValue(overviewData.outboundCard.billCount) }}<span class="metric-unit">单</span></div>
            </div>
            <div class="metric-item">
              <div class="metric-title">出库产品总数</div>
              <div class="metric-value">{{ formatIntegerValue(overviewData.outboundCard.totalQuantity) }}<span class="metric-unit">件</span></div>
            </div>
            <div class="metric-item">
              <div class="metric-title">出库产品价值</div>
              <div class="metric-value amount">{{ formatNumberValue(overviewData.outboundCard.totalAmount) }}<span class="metric-unit">元</span></div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="24" :lg="7" :xl="6">
        <el-card class="panel-card business-list-card" shadow="never">
          <template #header>
            <div class="panel-title-wrap">
              <span class="panel-title">出库业务清查</span>
              <span class="panel-count danger">{{ overviewData.outboundOperationList.length }}</span>
            </div>
          </template>
          <div class="business-list-scroll">
            <el-empty v-if="overviewData.outboundOperationList.length === 0" description="暂无出库作业记录" :image-size="100" />
            <ul v-else class="business-list">
              <li v-for="outboundItem in overviewData.outboundOperationList" :key="`outbound-${outboundItem.billNo}-${outboundItem.businessTime}`" class="business-item">
                <span class="business-dot outbound"></span>
                <div class="business-main">
                  <div class="business-line">
                    <span class="business-no">{{ outboundItem.billNo || '-' }}</span>
                    <span class="business-qty">x {{ formatIntegerValue(outboundItem.totalQuantity) }}</span>
                  </div>
                  <div class="business-sub">{{ outboundItem.summary || getBusinessTypeLabel(outboundItem.businessType, false) }}</div>
                </div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="panel-card product-panel" shadow="never">
      <div class="product-tabs">
        <el-button type="primary" size="small">产品实时库存查询</el-button>
        <el-button size="small" disabled>库存趋势图表</el-button>
      </div>

      <el-row :gutter="12" class="product-content">
        <el-col :xs="24" :sm="24" :md="8" :lg="6" :xl="5">
          <div class="product-filter-panel">
            <el-form label-width="72px">
              <el-form-item label="仓库">
                <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" filterable @change="handleWarehouseChange">
                  <el-option
                    v-for="warehouseOption in warehouseOptions"
                    :key="`panel-${warehouseOption.warehouseId}`"
                    :label="warehouseOption.warehouseName"
                    :value="warehouseOption.warehouseId"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="产品维度">
                <el-select v-model="productRankDimension" @change="renderProductRankChart">
                  <el-option label="按产品名称" value="productName" />
                  <el-option label="按产品编码" value="productCode" />
                </el-select>
              </el-form-item>
            </el-form>
            <div class="stock-summary-card">
              <div class="stock-summary-title">当前库存总量</div>
              <div class="stock-summary-value">{{ formatIntegerValue(overviewData.stockData.totalQuantity) }} 件</div>
            </div>
            <div class="stock-summary-card">
              <div class="stock-summary-title">当前库存总额</div>
              <div class="stock-summary-value amount">{{ formatNumberValue(overviewData.stockData.totalAmount) }} 元</div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="24" :md="16" :lg="18" :xl="19">
          <div class="rank-panel">
            <div class="rank-header">
              <span class="rank-title">产品可用库存排行</span>
              <span class="rank-count">产品种类：{{ overviewData.productRankList.length }}</span>
            </div>
            <el-empty v-if="overviewData.productRankList.length === 0" description="暂无产品库存排行数据" />
            <div v-else ref="productRankChartRef" class="rank-chart"></div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="WarehouseStatistics">
import * as echarts from "echarts"
import { listWarehouse } from "@/api/business/warehouse"
import { getWarehouseStatisticsOverview } from "@/api/business/warehouseStatistics"

const loading = ref(false)
const queryRef = ref()
const productRankChartRef = ref()
const warehouseOptions = ref([])
const productRankDimension = ref("productName")
const dateRange = ref([])
const quickRangeKey = ref("today")

const queryParams = reactive({
  warehouseId: undefined,
  statisticsView: "all",
  operationLimit: 4,
  rankLimit: 10
})

const overviewData = reactive({
  inboundCard: {
    billCount: 0,
    totalQuantity: 0,
    totalAmount: 0
  },
  outboundCard: {
    billCount: 0,
    totalQuantity: 0,
    totalAmount: 0
  },
  stockData: {
    totalQuantity: 0,
    totalAmount: 0
  },
  inboundOperationList: [],
  outboundOperationList: [],
  productRankList: []
})

let productRankChartInstance = null

const quickRangeOptions = [
  { label: "今天", value: "today" },
  { label: "昨天", value: "yesterday" },
  { label: "本周", value: "week" },
  { label: "本月", value: "month" },
  { label: "本年", value: "year" }
]

const statisticsViewOptions = computed(() => [
  {
    value: "all",
    label: "查看全部",
    iconText: "全",
    count: getViewOperationCount("all")
  },
  {
    value: "inbound",
    label: "入库单",
    iconText: "入",
    count: getViewOperationCount("inbound")
  },
  {
    value: "outbound",
    label: "出库单",
    iconText: "出",
    count: getViewOperationCount("outbound")
  },
  {
    value: "inventory",
    label: "库存调整",
    iconText: "调",
    count: getViewOperationCount("inventory")
  }
])

function formatDateToString(dateValue) {
  const year = dateValue.getFullYear()
  const month = String(dateValue.getMonth() + 1).padStart(2, "0")
  const day = String(dateValue.getDate()).padStart(2, "0")
  const hour = String(dateValue.getHours()).padStart(2, "0")
  const minute = String(dateValue.getMinutes()).padStart(2, "0")
  const second = String(dateValue.getSeconds()).padStart(2, "0")
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

function getTodayDateRange() {
  const currentDate = new Date()
  const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate(), 0, 0, 0)
  return [startDate, currentDate]
}

function getYesterdayDateRange() {
  const currentDate = new Date()
  const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() - 1, 0, 0, 0)
  const endDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() - 1, 23, 59, 59)
  return [startDate, endDate]
}

function getCurrentWeekDateRange() {
  const currentDate = new Date()
  const currentDay = currentDate.getDay()
  const distanceFromMonday = currentDay === 0 ? 6 : currentDay - 1
  const startDate = new Date(currentDate)
  startDate.setDate(currentDate.getDate() - distanceFromMonday)
  startDate.setHours(0, 0, 0, 0)
  return [startDate, currentDate]
}

function getCurrentMonthDateRange() {
  const currentDate = new Date()
  const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1, 0, 0, 0)
  return [startDate, currentDate]
}

function getCurrentYearDateRange() {
  const currentDate = new Date()
  const startDate = new Date(currentDate.getFullYear(), 0, 1, 0, 0, 0)
  return [startDate, currentDate]
}

function initializeDefaultDateRange() {
  const defaultDateRange = getDateRangeByKey("today")
  quickRangeKey.value = "today"
  dateRange.value = [
    formatDateToString(defaultDateRange[0]),
    formatDateToString(defaultDateRange[1])
  ]
}

function formatIntegerValue(value) {
  return Number(value || 0).toLocaleString("zh-CN", { maximumFractionDigits: 0 })
}

function formatNumberValue(value) {
  return Number(value || 0).toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function getBusinessTypeLabel(businessType, isInbound) {
  if (businessType === "inventory") {
    return isInbound ? "库存调入" : "库存调出"
  }
  if (businessType === "inbound") {
    return "入库单"
  }
  if (businessType === "outbound") {
    return "出库单"
  }
  return "未知类型"
}

function buildOverviewRequestParams() {
  const statisticsQuery = {
    warehouseId: queryParams.warehouseId,
    statisticsView: queryParams.statisticsView,
    operationLimit: queryParams.operationLimit,
    rankLimit: queryParams.rankLimit
  }
  if (Array.isArray(dateRange.value) && dateRange.value.length === 2) {
    statisticsQuery.startTime = dateRange.value[0]
    statisticsQuery.endTime = dateRange.value[1]
  }
  return statisticsQuery
}

function getDateRangeByKey(rangeKey) {
  if (rangeKey === "yesterday") {
    return getYesterdayDateRange()
  }
  if (rangeKey === "week") {
    return getCurrentWeekDateRange()
  }
  if (rangeKey === "month") {
    return getCurrentMonthDateRange()
  }
  if (rangeKey === "year") {
    return getCurrentYearDateRange()
  }
  return getTodayDateRange()
}

function assignOverviewData(sourceData) {
  overviewData.inboundCard = sourceData.inboundCard || { billCount: 0, totalQuantity: 0, totalAmount: 0 }
  overviewData.outboundCard = sourceData.outboundCard || { billCount: 0, totalQuantity: 0, totalAmount: 0 }
  overviewData.stockData = sourceData.stockData || { totalQuantity: 0, totalAmount: 0 }
  overviewData.inboundOperationList = sourceData.inboundOperationList || []
  overviewData.outboundOperationList = sourceData.outboundOperationList || []
  overviewData.productRankList = sourceData.productRankList || []
}

function loadWarehouseOptions() {
  return listWarehouse({ pageNum: 1, pageSize: 1000, status: "0" }).then(response => {
    warehouseOptions.value = response.rows || []
    if (warehouseOptions.value.length > 0) {
      queryParams.warehouseId = warehouseOptions.value[0].warehouseId
    }
  })
}

function loadOverviewData() {
  loading.value = true
  return getWarehouseStatisticsOverview(buildOverviewRequestParams()).then(response => {
    assignOverviewData(response.data || {})
    nextTick(() => {
      renderProductRankChart()
    })
  }).finally(() => {
    loading.value = false
  })
}

function getInventoryOperationCount() {
  const inboundInventoryCount = overviewData.inboundOperationList.filter(operationItem => operationItem.businessType === "inventory").length
  const outboundInventoryCount = overviewData.outboundOperationList.filter(operationItem => operationItem.businessType === "inventory").length
  return inboundInventoryCount + outboundInventoryCount
}

function getViewOperationCount(viewValue) {
  if (viewValue === "inbound") {
    return overviewData.inboundOperationList.length
  }
  if (viewValue === "outbound") {
    return overviewData.outboundOperationList.length
  }
  if (viewValue === "inventory") {
    return getInventoryOperationCount()
  }
  return overviewData.inboundOperationList.length + overviewData.outboundOperationList.length
}

function switchStatisticsView(viewValue) {
  queryParams.statisticsView = viewValue
  loadOverviewData()
}

function applyQuickRange(rangeValue) {
  quickRangeKey.value = rangeValue
  const selectedRange = getDateRangeByKey(rangeValue)
  dateRange.value = [
    formatDateToString(selectedRange[0]),
    formatDateToString(selectedRange[1])
  ]
  loadOverviewData()
}

function handleDateRangeChange() {
  quickRangeKey.value = "custom"
}

function handleWarehouseChange() {
  loadOverviewData()
}

function renderProductRankChart() {
  if (!productRankChartRef.value || overviewData.productRankList.length === 0) {
    if (productRankChartInstance) {
      productRankChartInstance.clear()
    }
    return
  }
  if (!productRankChartInstance) {
    productRankChartInstance = echarts.init(productRankChartRef.value)
  }
  const categoryFieldName = productRankDimension.value
  const categoryLabels = overviewData.productRankList.map(rankItem => rankItem[categoryFieldName] || "未命名")
  const quantityData = overviewData.productRankList.map(rankItem => Number(rankItem.availableQuantity || 0))
  productRankChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    xAxis: {
      type: "category",
      data: categoryLabels,
      axisLabel: {
        interval: 0,
        rotate: categoryLabels.length > 8 ? 25 : 0
      }
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        name: "可用库存",
        type: "bar",
        data: quantityData,
        barMaxWidth: 48,
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: chartParams => chartParams.dataIndex % 2 === 0 ? "#76c5ff" : "#2f7ef8"
        },
        label: {
          show: true,
          position: "top"
        }
      }
    ],
    grid: {
      left: "5%",
      right: "3%",
      top: "10%",
      bottom: "8%",
      containLabel: true
    }
  })
}

function handleQuery() {
  loadOverviewData()
}

function resetQuery() {
  queryParams.statisticsView = "all"
  queryParams.operationLimit = 4
  queryParams.rankLimit = 10
  queryParams.warehouseId = warehouseOptions.value.length > 0 ? warehouseOptions.value[0].warehouseId : undefined
  initializeDefaultDateRange()
  loadOverviewData()
}

onMounted(() => {
  initializeDefaultDateRange()
  loadWarehouseOptions().then(() => {
    loadOverviewData()
  })
})

onBeforeUnmount(() => {
  if (productRankChartInstance) {
    productRankChartInstance.dispose()
    productRankChartInstance = null
  }
})

watch(() => overviewData.productRankList, () => {
  nextTick(() => {
    renderProductRankChart()
  })
}, { deep: true })
</script>

<style lang="scss" scoped>
.warehouse-statistics-page {
  background: #f3f7ff;

  .panel-row,
  .panel-card {
    margin-bottom: 12px;
  }

  :deep(.el-card__header) {
    border-bottom: 1px solid #edf2fb;
    padding: 12px 14px;
  }

  :deep(.el-card__body) {
    padding: 12px 14px;
  }
}

.mode-panel {
  .mode-list {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;
  }

  .mode-item {
    position: relative;
    border: 1px solid #eaf0fb;
    border-radius: 8px;
    padding: 10px 12px;
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover,
    &.active {
      border-color: #3f91ff;
      box-shadow: 0 0 0 2px rgba(63, 145, 255, 0.12);
    }
  }

  .mode-icon {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 700;
    color: #fff;
  }

  .mode-icon-inbound {
    background: #4d95ff;
  }

  .mode-icon-outbound {
    background: #ff9f43;
  }

  .mode-icon-inventory {
    background: #ffa23f;
  }

  .mode-icon-all {
    background: #7487a8;
  }

  .mode-name {
    flex: 1;
    color: var(--el-text-color-primary);
    font-size: 14px;
    font-weight: 500;
  }

  .mode-badge {
    min-width: 22px;
    height: 22px;
    border-radius: 11px;
    background: #f56c6c;
    color: #fff;
    text-align: center;
    line-height: 22px;
    font-size: 12px;
    padding: 0 6px;
  }
}

.business-list-card {
  height: 330px;
  .panel-title-wrap {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .panel-title {
    color: var(--el-text-color-primary);
    font-size: 14px;
    font-weight: 500;
    line-height: 1.4;
  }

  .panel-count {
    min-width: 24px;
    height: 24px;
    border-radius: 12px;
    background: #4d95ff;
    color: #fff;
    text-align: center;
    line-height: 24px;
    font-size: 12px;

    &.danger {
      background: #f56c6c;
    }
  }

  .business-list-scroll {
    max-height: none;
    overflow-y: hidden;
  }

  .business-list {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .business-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    padding: 8px 0;
    border-bottom: 1px dashed #eef2f9;
  }

  .business-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-top: 7px;
    flex-shrink: 0;

    &.inbound {
      background: #4d95ff;
    }

    &.outbound {
      background: #ff9f43;
    }
  }

  .business-main {
    flex: 1;
    min-width: 0;
  }

  .business-line {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
  }

  .business-no {
    color: #1f2d3d;
    font-weight: 500;
    font-size: 13px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .business-qty {
    color: #5e6b85;
    font-size: 12px;
    flex-shrink: 0;
  }

  .business-sub {
    color: #8492a6;
    font-size: 12px;
    margin-top: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.center-panel {
  height: 330px;
  .filter-toolbar {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    align-items: center;
    flex-wrap: wrap;
  }

  .filter-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .filter-label {
    color: #5e6b85;
    font-size: 13px;
  }

  .warehouse-select {
    width: 180px;
  }

  .quick-range-list {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
  }

  .query-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 12px;
    flex-wrap: wrap;
  }

  .date-picker {
    flex: 1;
    min-width: 260px;
  }

  .metric-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 10px;
  }

  .metric-item {
    background: #f7fbff;
    border-radius: 8px;
    border: 1px solid #ebf3ff;
    padding: 14px 12px;
  }

  .metric-title {
    color: #5e6b85;
    font-size: 13px;
    margin-bottom: 8px;
  }

  .metric-value {
    color: #2f7ef8;
    font-weight: 700;
    font-size: 28px;
    line-height: 1;
  }

  .metric-value.amount {
    color: #8855ff;
  }

  .metric-unit {
    color: #8aa0c9;
    font-size: 14px;
    margin-left: 6px;
    font-weight: 500;
  }
}

.product-panel {
  margin-bottom: 0;

  .product-tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
  }

  .product-filter-panel {
    background: #f8fbff;
    border: 1px solid #ebf3ff;
    border-radius: 8px;
    padding: 12px;
  }

  .stock-summary-card {
    border-radius: 8px;
    background: #fff;
    border: 1px solid #edf2fb;
    padding: 10px 12px;
    margin-top: 10px;
  }

  .stock-summary-title {
    color: #5e6b85;
    font-size: 13px;
    margin-bottom: 4px;
  }

  .stock-summary-value {
    color: #2f7ef8;
    font-size: 26px;
    font-weight: 700;

    &.amount {
      color: #8855ff;
    }
  }

  .rank-panel {
    border: 1px solid #edf2fb;
    border-radius: 8px;
    padding: 12px;
    min-height: 480px;
    background: #fff;
  }

  .rank-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }

  .rank-title {
    color: var(--el-text-color-primary);
    font-size: 14px;
    font-weight: 500;
    line-height: 1.4;
  }

  .rank-count {
    color: #8c98ae;
    font-size: 13px;
  }
}

.rank-chart {
  width: 100%;
  height: 420px;
}

@media (max-width: 1200px) {
  .mode-panel .mode-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .center-panel .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
