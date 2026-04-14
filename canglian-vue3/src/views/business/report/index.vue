<template>
  <div class="app-container">
    <el-tabs v-model="active">
      <el-tab-pane label="应收账龄" name="receivableAging">
        <el-form :model="receivableQuery" ref="receivableAgingRef" :inline="true">
          <el-form-item label="客户编号" prop="customerId">
            <el-input v-model="receivableQuery.customerId" placeholder="请输入客户编号" style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getReceivableAging" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetReceivableAging">重置</el-button>
          </el-form-item>
        </el-form>
        <div v-if="receivableAgingList.length > 0" ref="receivableAgingChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
        <el-table v-loading="loading.receivableAging" :data="receivableAgingList" style="width: 100%; margin-top: 10px">
          <el-table-column label="客户编号" align="center" prop="dimensionId" />
          <el-table-column label="未逾期金额" align="center" prop="notDueAmount" />
          <el-table-column label="0-30天金额" align="center" prop="days0To30Amount" />
          <el-table-column label="31-60天金额" align="center" prop="days31To60Amount" />
          <el-table-column label="61-90天金额" align="center" prop="days61To90Amount" />
          <el-table-column label="90天以上金额" align="center" prop="days90AboveAmount" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="应付账龄" name="payableAging">
        <el-form :model="payableQuery" ref="payableAgingRef" :inline="true">
          <el-form-item label="供应商编号" prop="supplierId">
            <el-input v-model="payableQuery.supplierId" placeholder="请输入供应商编号" style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getPayableAging" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetPayableAging">重置</el-button>
          </el-form-item>
        </el-form>
        <div v-if="payableAgingList.length > 0" ref="payableAgingChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
        <el-table v-loading="loading.payableAging" :data="payableAgingList" style="width: 100%; margin-top: 10px">
          <el-table-column label="供应商编号" align="center" prop="dimensionId" />
          <el-table-column label="未逾期金额" align="center" prop="notDueAmount" />
          <el-table-column label="0-30天金额" align="center" prop="days0To30Amount" />
          <el-table-column label="31-60天金额" align="center" prop="days31To60Amount" />
          <el-table-column label="61-90天金额" align="center" prop="days61To90Amount" />
          <el-table-column label="90天以上金额" align="center" prop="days90AboveAmount" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="应收对账" name="receivableReconciliation">
        <el-form :model="receivableReconciliationQuery" ref="receivableReconciliationRef" :inline="true">
          <el-form-item label="客户编号" prop="customerId">
            <el-input v-model="receivableReconciliationQuery.customerId" placeholder="请输入客户编号" style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getReceivableReconciliation" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetReceivableReconciliation">重置</el-button>
          </el-form-item>
        </el-form>
        <div v-if="receivableReconciliationList.length > 0" ref="receivableReconciliationChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
        <el-table v-loading="loading.receivableReconciliation" :data="receivableReconciliationList" style="width: 100%; margin-top: 10px">
          <el-table-column label="客户编号" align="center" prop="dimensionId" />
          <el-table-column label="应收金额" align="center" prop="billAmount" />
          <el-table-column label="已收金额" align="center" prop="paidAmount" />
          <el-table-column label="差额" align="center" prop="diffAmount" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="应付对账" name="payableReconciliation">
        <el-form :model="payableReconciliationQuery" ref="payableReconciliationRef" :inline="true">
          <el-form-item label="供应商编号" prop="supplierId">
            <el-input v-model="payableReconciliationQuery.supplierId" placeholder="请输入供应商编号" style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getPayableReconciliation" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetPayableReconciliation">重置</el-button>
          </el-form-item>
        </el-form>
        <div v-if="payableReconciliationList.length > 0" ref="payableReconciliationChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
        <el-table v-loading="loading.payableReconciliation" :data="payableReconciliationList" style="width: 100%; margin-top: 10px">
          <el-table-column label="供应商编号" align="center" prop="dimensionId" />
          <el-table-column label="应付金额" align="center" prop="billAmount" />
          <el-table-column label="已付金额" align="center" prop="paidAmount" />
          <el-table-column label="差额" align="center" prop="diffAmount" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="利润表" name="profitLoss">
        <el-form :model="profitLossQuery" ref="profitLossRef" :inline="true">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="profitLossQuery.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开始日期"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="profitLossQuery.endDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择结束日期"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getProfitLoss" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetProfitLoss">重置</el-button>
          </el-form-item>
        </el-form>
        <el-descriptions v-if="profitLossData" :column="3" style="margin-top: 10px">
          <el-descriptions-item label="收入金额">{{ profitLossData.incomeAmount }}</el-descriptions-item>
          <el-descriptions-item label="成本费用">{{ profitLossData.costAmount }}</el-descriptions-item>
          <el-descriptions-item label="利润金额">{{ profitLossData.profitAmount }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="profitLossData" ref="profitLossChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
      </el-tab-pane>

      <el-tab-pane label="收支汇总" name="revenueExpense">
        <el-form :model="revenueExpenseQuery" ref="revenueExpenseRef" :inline="true">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="revenueExpenseQuery.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开始日期"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="revenueExpenseQuery.endDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择结束日期"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getRevenueExpense" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetRevenueExpense">重置</el-button>
          </el-form-item>
        </el-form>
        <el-descriptions v-if="revenueExpenseData" :column="3" style="margin-top: 10px">
          <el-descriptions-item label="收入金额">{{ revenueExpenseData.incomeAmount }}</el-descriptions-item>
          <el-descriptions-item label="支出金额">{{ revenueExpenseData.expenseAmount }}</el-descriptions-item>
          <el-descriptions-item label="净额">{{ revenueExpenseData.netAmount }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="revenueExpenseData" ref="revenueExpenseChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
      </el-tab-pane>

      <el-tab-pane label="成本结构" name="costStructure">
        <el-form :model="costStructureQuery" ref="costStructureRef" :inline="true">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="costStructureQuery.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开始日期"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="costStructureQuery.endDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择结束日期"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getCostStructure" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetCostStructure">重置</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="loading.costStructure" :data="costStructureList" style="width: 100%; margin-top: 10px">
          <el-table-column label="类型" align="center" prop="costType" />
          <el-table-column label="金额" align="center" prop="costAmount" />
        </el-table>
        <div v-if="costStructureList.length > 0" ref="costStructureChartRef" style="width: 100%; height: 320px; margin-top: 10px"></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="Report">
import * as echarts from "echarts"
import { receivableAging, payableAging, receivableReconciliation, payableReconciliation, profitLoss, revenueExpense, costStructure } from "@/api/business/report"

const { proxy } = getCurrentInstance()

const active = ref("receivableAging")

const loading = reactive({
  receivableAging: false,
  payableAging: false,
  receivableReconciliation: false,
  payableReconciliation: false,
  costStructure: false
})

function formatDateValue(dateValue) {
  const year = dateValue.getFullYear()
  const month = String(dateValue.getMonth() + 1).padStart(2, "0")
  const day = String(dateValue.getDate()).padStart(2, "0")
  return `${year}-${month}-${day}`
}

function getPreviousYearDateRange() {
  const currentDate = new Date()
  const previousYear = currentDate.getFullYear() - 1
  const previousYearStartDate = new Date(previousYear, 0, 1)
  const previousYearEndDate = new Date(previousYear, 11, 31)
  return {
    startDate: formatDateValue(previousYearStartDate),
    endDate: formatDateValue(previousYearEndDate)
  }
}

const previousYearDateRange = getPreviousYearDateRange()

const receivableQuery = reactive({ customerId: undefined })
const payableQuery = reactive({ supplierId: undefined })
const receivableReconciliationQuery = reactive({ customerId: undefined })
const payableReconciliationQuery = reactive({ supplierId: undefined })
const profitLossQuery = reactive({ startDate: previousYearDateRange.startDate, endDate: previousYearDateRange.endDate })
const revenueExpenseQuery = reactive({ startDate: previousYearDateRange.startDate, endDate: previousYearDateRange.endDate })
const costStructureQuery = reactive({ startDate: previousYearDateRange.startDate, endDate: previousYearDateRange.endDate })

const receivableAgingList = ref([])
const payableAgingList = ref([])
const receivableReconciliationList = ref([])
const payableReconciliationList = ref([])
const profitLossData = ref(null)
const revenueExpenseData = ref(null)
const costStructureList = ref([])
const profitLossLoaded = ref(false)
const revenueExpenseLoaded = ref(false)
const costStructureLoaded = ref(false)
const receivableAgingChartRef = ref(null)
const payableAgingChartRef = ref(null)
const receivableReconciliationChartRef = ref(null)
const payableReconciliationChartRef = ref(null)
const profitLossChartRef = ref(null)
const revenueExpenseChartRef = ref(null)
const costStructureChartRef = ref(null)

let receivableAgingChartInstance = null
let payableAgingChartInstance = null
let receivableReconciliationChartInstance = null
let payableReconciliationChartInstance = null
let profitLossChartInstance = null
let revenueExpenseChartInstance = null
let costStructureChartInstance = null

// 查询应收账龄报表数据
function getReceivableAging() {
  loading.receivableAging = true
  receivableAging(receivableQuery).then(response => {
    receivableAgingList.value = response.data
    loading.receivableAging = false
    nextTick(() => {
      renderReceivableAgingChart()
    })
  })
}

// 重置应收账龄查询条件
function resetReceivableAging() {
  proxy.resetForm("receivableAgingRef")
  getReceivableAging()
}

// 查询应付账龄报表数据
function getPayableAging() {
  loading.payableAging = true
  payableAging(payableQuery).then(response => {
    payableAgingList.value = response.data
    loading.payableAging = false
    nextTick(() => {
      renderPayableAgingChart()
    })
  })
}

// 重置应付账龄查询条件
function resetPayableAging() {
  proxy.resetForm("payableAgingRef")
  getPayableAging()
}

// 查询应收对账报表数据
function getReceivableReconciliation() {
  loading.receivableReconciliation = true
  receivableReconciliation(receivableReconciliationQuery).then(response => {
    receivableReconciliationList.value = response.data
    loading.receivableReconciliation = false
    nextTick(() => {
      renderReceivableReconciliationChart()
    })
  })
}

// 重置应收对账查询条件
function resetReceivableReconciliation() {
  proxy.resetForm("receivableReconciliationRef")
  getReceivableReconciliation()
}

// 查询应付对账报表数据
function getPayableReconciliation() {
  loading.payableReconciliation = true
  payableReconciliation(payableReconciliationQuery).then(response => {
    payableReconciliationList.value = response.data
    loading.payableReconciliation = false
    nextTick(() => {
      renderPayableReconciliationChart()
    })
  })
}

// 重置应付对账查询条件
function resetPayableReconciliation() {
  proxy.resetForm("payableReconciliationRef")
  getPayableReconciliation()
}

// 查询利润表数据
function getProfitLoss() {
  profitLoss(profitLossQuery).then(response => {
    profitLossData.value = response.data
    profitLossLoaded.value = true
    nextTick(() => {
      renderProfitLossChart()
    })
  })
}

// 重置利润表查询条件
function resetProfitLoss() {
  proxy.resetForm("profitLossRef")
  profitLossQuery.startDate = previousYearDateRange.startDate
  profitLossQuery.endDate = previousYearDateRange.endDate
  getProfitLoss()
}

// 查询收支汇总数据
function getRevenueExpense() {
  revenueExpense(revenueExpenseQuery).then(response => {
    revenueExpenseData.value = response.data
    revenueExpenseLoaded.value = true
    nextTick(() => {
      renderRevenueExpenseChart()
    })
  })
}

// 重置收支汇总查询条件
function resetRevenueExpense() {
  proxy.resetForm("revenueExpenseRef")
  revenueExpenseQuery.startDate = previousYearDateRange.startDate
  revenueExpenseQuery.endDate = previousYearDateRange.endDate
  getRevenueExpense()
}

// 查询成本结构数据
function getCostStructure() {
  loading.costStructure = true
  costStructure(costStructureQuery).then(response => {
    costStructureList.value = response.data
    loading.costStructure = false
    costStructureLoaded.value = true
    nextTick(() => {
      renderCostStructureChart()
    })
  })
}

// 重置成本结构查询条件
function resetCostStructure() {
  proxy.resetForm("costStructureRef")
  costStructureQuery.startDate = previousYearDateRange.startDate
  costStructureQuery.endDate = previousYearDateRange.endDate
  getCostStructure()
}

watch(active, currentActiveTab => {
  if (currentActiveTab === "profitLoss" && !profitLossLoaded.value) {
    getProfitLoss()
  }
  if (currentActiveTab === "revenueExpense" && !revenueExpenseLoaded.value) {
    getRevenueExpense()
  }
  if (currentActiveTab === "costStructure" && !costStructureLoaded.value) {
    getCostStructure()
  }
})

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
    xAxis: { type: "category", data: ["收入金额", "成本费用", "利润金额"] },
    yAxis: { type: "value" },
    series: [{ type: "bar", data: profitLossChartData }]
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
  const receivableAgingLabels = receivableAgingList.value.map(item => item.dimensionId)
  const receivableNotDueAmounts = receivableAgingList.value.map(item => Number(item.notDueAmount || 0))
  const receivableDays0To30Amounts = receivableAgingList.value.map(item => Number(item.days0To30Amount || 0))
  const receivableDays31To60Amounts = receivableAgingList.value.map(item => Number(item.days31To60Amount || 0))
  const receivableDays61To90Amounts = receivableAgingList.value.map(item => Number(item.days61To90Amount || 0))
  const receivableDays90AboveAmounts = receivableAgingList.value.map(item => Number(item.days90AboveAmount || 0))
  receivableAgingChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["未逾期", "0-30天", "31-60天", "61-90天", "90天以上"] },
    xAxis: { type: "category", data: receivableAgingLabels },
    yAxis: { type: "value" },
    series: [
      { name: "未逾期", type: "bar", stack: "total", data: receivableNotDueAmounts },
      { name: "0-30天", type: "bar", stack: "total", data: receivableDays0To30Amounts },
      { name: "31-60天", type: "bar", stack: "total", data: receivableDays31To60Amounts },
      { name: "61-90天", type: "bar", stack: "total", data: receivableDays61To90Amounts },
      { name: "90天以上", type: "bar", stack: "total", data: receivableDays90AboveAmounts }
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
  const payableAgingLabels = payableAgingList.value.map(item => item.dimensionId)
  const payableNotDueAmounts = payableAgingList.value.map(item => Number(item.notDueAmount || 0))
  const payableDays0To30Amounts = payableAgingList.value.map(item => Number(item.days0To30Amount || 0))
  const payableDays31To60Amounts = payableAgingList.value.map(item => Number(item.days31To60Amount || 0))
  const payableDays61To90Amounts = payableAgingList.value.map(item => Number(item.days61To90Amount || 0))
  const payableDays90AboveAmounts = payableAgingList.value.map(item => Number(item.days90AboveAmount || 0))
  payableAgingChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["未逾期", "0-30天", "31-60天", "61-90天", "90天以上"] },
    xAxis: { type: "category", data: payableAgingLabels },
    yAxis: { type: "value" },
    series: [
      { name: "未逾期", type: "bar", stack: "total", data: payableNotDueAmounts },
      { name: "0-30天", type: "bar", stack: "total", data: payableDays0To30Amounts },
      { name: "31-60天", type: "bar", stack: "total", data: payableDays31To60Amounts },
      { name: "61-90天", type: "bar", stack: "total", data: payableDays61To90Amounts },
      { name: "90天以上", type: "bar", stack: "total", data: payableDays90AboveAmounts }
    ]
  })
}

// 渲染应收对账图表
function renderReceivableReconciliationChart() {
  if (!receivableReconciliationChartRef.value) {
    return
  }
  if (receivableReconciliationList.value.length === 0) {
    if (receivableReconciliationChartInstance) {
      receivableReconciliationChartInstance.clear()
    }
    return
  }
  if (!receivableReconciliationChartInstance) {
    receivableReconciliationChartInstance = echarts.init(receivableReconciliationChartRef.value)
  }
  const receivableReconciliationLabels = receivableReconciliationList.value.map(item => item.dimensionId)
  const receivableBillAmounts = receivableReconciliationList.value.map(item => Number(item.billAmount || 0))
  const receivablePaidAmounts = receivableReconciliationList.value.map(item => Number(item.paidAmount || 0))
  const receivableDiffAmounts = receivableReconciliationList.value.map(item => Number(item.diffAmount || 0))
  receivableReconciliationChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["应收金额", "已收金额", "差额"] },
    xAxis: { type: "category", data: receivableReconciliationLabels },
    yAxis: { type: "value" },
    series: [
      { name: "应收金额", type: "bar", data: receivableBillAmounts },
      { name: "已收金额", type: "bar", data: receivablePaidAmounts },
      { name: "差额", type: "bar", data: receivableDiffAmounts }
    ]
  })
}

// 渲染应付对账图表
function renderPayableReconciliationChart() {
  if (!payableReconciliationChartRef.value) {
    return
  }
  if (payableReconciliationList.value.length === 0) {
    if (payableReconciliationChartInstance) {
      payableReconciliationChartInstance.clear()
    }
    return
  }
  if (!payableReconciliationChartInstance) {
    payableReconciliationChartInstance = echarts.init(payableReconciliationChartRef.value)
  }
  const payableReconciliationLabels = payableReconciliationList.value.map(item => item.dimensionId)
  const payableBillAmounts = payableReconciliationList.value.map(item => Number(item.billAmount || 0))
  const payablePaidAmounts = payableReconciliationList.value.map(item => Number(item.paidAmount || 0))
  const payableDiffAmounts = payableReconciliationList.value.map(item => Number(item.diffAmount || 0))
  payableReconciliationChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["应付金额", "已付金额", "差额"] },
    xAxis: { type: "category", data: payableReconciliationLabels },
    yAxis: { type: "value" },
    series: [
      { name: "应付金额", type: "bar", data: payableBillAmounts },
      { name: "已付金额", type: "bar", data: payablePaidAmounts },
      { name: "差额", type: "bar", data: payableDiffAmounts }
    ]
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
    xAxis: { type: "category", data: ["收入金额", "支出金额", "净额"] },
    yAxis: { type: "value" },
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
  const costStructureChartData = costStructureList.value.map(item => {
    return { name: item.costType, value: Number(item.costAmount || 0) }
  })
  costStructureChartInstance.setOption({
    tooltip: { trigger: "item" },
    legend: { top: "bottom" },
    series: [
      {
        type: "pie",
        radius: "50%",
        data: costStructureChartData
      }
    ]
  })
}
</script>
