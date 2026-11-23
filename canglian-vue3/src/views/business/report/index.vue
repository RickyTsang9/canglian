<template>
  <div class="app-container">
    <el-tabs v-model="active">
      <el-tab-pane label="应收账龄" name="receivableAging">
        <el-form :model="receivableQuery" ref="receivableAgingRef" :inline="true">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="receivableQuery.customerId" placeholder="请选择客户" clearable filterable style="width: 220px">
              <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerCode ? `${c.customerCode} - ${c.customerName}` : c.customerName" :value="c.customerId" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getReceivableAging" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetReceivableAging">重置</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport('receivableAging', receivableQuery, '应收账龄报表.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <div v-if="receivableAgingList.length > 0" ref="receivableAgingChartRef" class="report-chart"></div>
        <el-table v-loading="loading.receivableAging" :data="receivableAgingList" border style="width: 100%; margin-top: 12px">
          <el-table-column label="客户" align="center" min-width="140">
            <template #default="scope">
              <span>{{ scope.row.dimensionName || getCustomerName(scope.row.dimensionId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="未逾期金额" align="right" header-align="right" prop="notDueAmount">
            <template #default="scope">{{ formatAmount(scope.row.notDueAmount) }}</template>
          </el-table-column>
          <el-table-column label="0-30天逾期" align="right" header-align="right" prop="days0To30Amount">
            <template #default="scope">{{ formatAmount(scope.row.days0To30Amount) }}</template>
          </el-table-column>
          <el-table-column label="31-60天逾期" align="right" header-align="right" prop="days31To60Amount">
            <template #default="scope">{{ formatAmount(scope.row.days31To60Amount) }}</template>
          </el-table-column>
          <el-table-column label="61-90天逾期" align="right" header-align="right" prop="days61To90Amount">
            <template #default="scope">{{ formatAmount(scope.row.days61To90Amount) }}</template>
          </el-table-column>
          <el-table-column label="90天以上逾期" align="right" header-align="right" prop="days90AboveAmount">
            <template #default="scope">{{ formatAmount(scope.row.days90AboveAmount) }}</template>
          </el-table-column>
          <el-table-column label="红字待抵扣" align="right" header-align="right" prop="creditBalance">
            <template #default="scope">
              <span :style="Number(scope.row.creditBalance || 0) < 0 ? 'color:#ef4444;font-weight:600' : ''">{{ formatAmount(scope.row.creditBalance) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="净欠款余额" align="right" header-align="right" prop="netBalance">
            <template #default="scope">{{ formatAmount(scope.row.netBalance) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="应付账龄" name="payableAging">
        <el-form :model="payableQuery" ref="payableAgingRef" :inline="true">
          <el-form-item label="供应商" prop="supplierId">
            <el-select v-model="payableQuery.supplierId" placeholder="请选择供应商" clearable filterable style="width: 220px">
              <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="s.supplierCode ? `${s.supplierCode} - ${s.supplierName}` : s.supplierName" :value="s.supplierId" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getPayableAging" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetPayableAging">重置</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport('payableAging', payableQuery, '应付账龄报表.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <div v-if="payableAgingList.length > 0" ref="payableAgingChartRef" class="report-chart"></div>
        <el-table v-loading="loading.payableAging" :data="payableAgingList" border style="width: 100%; margin-top: 12px">
          <el-table-column label="供应商" align="center" min-width="140">
            <template #default="scope">
              <span>{{ scope.row.dimensionName || getSupplierName(scope.row.dimensionId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="未逾期金额" align="right" header-align="right" prop="notDueAmount">
            <template #default="scope">{{ formatAmount(scope.row.notDueAmount) }}</template>
          </el-table-column>
          <el-table-column label="0-30天逾期" align="right" header-align="right" prop="days0To30Amount">
            <template #default="scope">{{ formatAmount(scope.row.days0To30Amount) }}</template>
          </el-table-column>
          <el-table-column label="31-60天逾期" align="right" header-align="right" prop="days31To60Amount">
            <template #default="scope">{{ formatAmount(scope.row.days31To60Amount) }}</template>
          </el-table-column>
          <el-table-column label="61-90天逾期" align="right" header-align="right" prop="days61To90Amount">
            <template #default="scope">{{ formatAmount(scope.row.days61To90Amount) }}</template>
          </el-table-column>
          <el-table-column label="90天以上逾期" align="right" header-align="right" prop="days90AboveAmount">
            <template #default="scope">{{ formatAmount(scope.row.days90AboveAmount) }}</template>
          </el-table-column>
          <el-table-column label="红字待抵扣" align="right" header-align="right" prop="creditBalance">
            <template #default="scope">
              <span :style="Number(scope.row.creditBalance || 0) < 0 ? 'color:#ef4444;font-weight:600' : ''">{{ formatAmount(scope.row.creditBalance) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="净应付余额" align="right" header-align="right" prop="netBalance">
            <template #default="scope">{{ formatAmount(scope.row.netBalance) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="应收对账" name="receivableReconciliation">
        <el-form :model="receivableReconciliationQuery" ref="receivableReconciliationRef" :inline="true">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="receivableReconciliationQuery.customerId" placeholder="请选择客户" clearable filterable style="width: 220px">
              <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerCode ? `${c.customerCode} - ${c.customerName}` : c.customerName" :value="c.customerId" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getReceivableReconciliation" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetReceivableReconciliation">重置</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport('receivableReconciliation', receivableReconciliationQuery, '应收对账报表.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <div v-if="receivableReconciliationList.length > 0" ref="receivableReconciliationChartRef" class="report-chart"></div>
        <el-table v-loading="loading.receivableReconciliation" :data="receivableReconciliationList" border style="width: 100%; margin-top: 12px">
          <el-table-column label="客户" align="center" min-width="140">
            <template #default="scope">
              <span>{{ scope.row.dimensionName || getCustomerName(scope.row.dimensionId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="应收净额（含退货冲减）" align="right" header-align="right" prop="billAmount">
            <template #default="scope">{{ formatAmount(scope.row.billAmount) }}</template>
          </el-table-column>
          <el-table-column label="已收/已抵扣金额" align="right" header-align="right" prop="paidAmount">
            <template #default="scope">{{ formatAmount(scope.row.paidAmount) }}</template>
          </el-table-column>
          <el-table-column label="剩余未收（已扣坏账）" align="right" header-align="right" prop="diffAmount">
            <template #default="scope">{{ formatAmount(scope.row.diffAmount) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="应付对账" name="payableReconciliation">
        <el-form :model="payableReconciliationQuery" ref="payableReconciliationRef" :inline="true">
          <el-form-item label="供应商" prop="supplierId">
            <el-select v-model="payableReconciliationQuery.supplierId" placeholder="请选择供应商" clearable filterable style="width: 220px">
              <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="s.supplierCode ? `${s.supplierCode} - ${s.supplierName}` : s.supplierName" :value="s.supplierId" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getPayableReconciliation" v-hasPermi="['business:report:query']">查询</el-button>
            <el-button icon="Refresh" @click="resetPayableReconciliation">重置</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport('payableReconciliation', payableReconciliationQuery, '应付对账报表.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <div v-if="payableReconciliationList.length > 0" ref="payableReconciliationChartRef" class="report-chart"></div>
        <el-table v-loading="loading.payableReconciliation" :data="payableReconciliationList" border style="width: 100%; margin-top: 12px">
          <el-table-column label="供应商" align="center" min-width="140">
            <template #default="scope">
              <span>{{ scope.row.dimensionName || getSupplierName(scope.row.dimensionId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="应付净额（含退货冲减）" align="right" header-align="right" prop="billAmount">
            <template #default="scope">{{ formatAmount(scope.row.billAmount) }}</template>
          </el-table-column>
          <el-table-column label="已付金额" align="right" header-align="right" prop="paidAmount">
            <template #default="scope">{{ formatAmount(scope.row.paidAmount) }}</template>
          </el-table-column>
          <el-table-column label="未付差额" align="right" header-align="right" prop="diffAmount">
            <template #default="scope">{{ formatAmount(scope.row.diffAmount) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="利润表（权责发生制）" name="profitLoss">
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
            <el-button type="warning" plain icon="Download" @click="handleExport('profitLoss', profitLossQuery, '经营利润表.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <el-alert
          title="权责发生制口径（以客户签收确认为准）：营业净收入 = 已签收销售出库（扣已确认销项税） - 有效销售退货冲减；销售成本取自已签收结转成本（扣退货成本冲回，含盘点损益与受控调价）；营业净利润 = 销售毛利 - 已确认期间费用（含互转手续费） - 坏账净损失（扣期间坏账收回）。"
          type="info"
          show-icon
          :closable="false"
          style="margin-bottom: 14px"
        />
        <div v-if="profitLossData" class="stat-strip">
          <div class="stat-box blue">
            <div class="stat-label">营业净收入</div>
            <div class="stat-val">{{ formatMoney(profitLossData.netIncomeAmount) }}</div>
            <div class="stat-sub">签收收入 {{ formatMoney(profitLossData.salesIncomeAmount) }} · 退货 {{ formatMoney(profitLossData.saleReturnAmount) }}</div>
          </div>
          <div class="stat-box teal">
            <div class="stat-label">销售毛利（毛利率 {{ Number(profitLossData.grossMarginRate || 0).toFixed(2) }}%）</div>
            <div class="stat-val">{{ formatMoney(profitLossData.grossProfitAmount) }}</div>
            <div class="stat-sub">减：销售成本 {{ formatMoney(profitLossData.salesCostAmount) }}</div>
          </div>
          <div class="stat-box amber">
            <div class="stat-label">期间费用与坏账净损失</div>
            <div class="stat-val">{{ formatMoney(Number(profitLossData.expenseAmount || 0) + Number(profitLossData.badDebtAmount || 0)) }}</div>
            <div class="stat-sub">费用 {{ formatMoney(profitLossData.expenseAmount) }} · 坏账 {{ formatMoney(profitLossData.badDebtAmount) }}</div>
          </div>
          <div class="stat-box" :class="Number(profitLossData.netProfitAmount || 0) >= 0 ? 'green' : 'rose'">
            <div class="stat-label">营业净利润 / 净利率</div>
            <div class="stat-val">{{ formatMoney(profitLossData.netProfitAmount) }}</div>
            <div class="stat-sub">净利率 {{ Number(profitLossData.netMarginRate || 0).toFixed(2) }}%</div>
          </div>
        </div>
        <div v-if="profitLossData" ref="profitLossChartRef" class="report-chart"></div>
      </el-tab-pane>

      <el-tab-pane label="收支汇总（现金口径）" name="revenueExpense">
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
            <el-button type="warning" plain icon="Download" @click="handleExport('revenueExpense', revenueExpenseQuery, '现金收支汇总表.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <el-alert
          title="收付实现制（现金流）口径：仅统计已确认收款单与坏账收回的现金流入，减去已确认采购付款、期间费用与银行互转手续费的现金流出。"
          type="info"
          show-icon
          :closable="false"
          style="margin-bottom: 14px"
        />
        <div v-if="revenueExpenseData" class="stat-strip cols-3">
          <div class="stat-box green">
            <div class="stat-label">现金流入总额</div>
            <div class="stat-val">{{ formatMoney(revenueExpenseData.incomeAmount) }}</div>
          </div>
          <div class="stat-box amber">
            <div class="stat-label">现金流出总额</div>
            <div class="stat-val">{{ formatMoney(revenueExpenseData.expenseAmount) }}</div>
          </div>
          <div class="stat-box" :class="Number(revenueExpenseData.netAmount || 0) >= 0 ? 'blue' : 'rose'">
            <div class="stat-label">现金净流量</div>
            <div class="stat-val">{{ formatMoney(revenueExpenseData.netAmount) }}</div>
          </div>
        </div>
        <div v-if="revenueExpenseData" ref="revenueExpenseChartRef" class="report-chart"></div>
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
            <el-button type="warning" plain icon="Download" @click="handleExport('costStructure', costStructureQuery, '成本结构分析.xlsx')" v-hasPermi="['business:report:export']">导出 Excel</el-button>
          </el-form-item>
        </el-form>
        <div class="stat-strip cols-3">
          <div class="stat-box slate">
            <div class="stat-label">成本费用总额（与利润表一致）</div>
            <div class="stat-val">{{ formatMoney(costSummary.total) }}</div>
          </div>
          <div class="stat-box blue">
            <div class="stat-label">主营成本及存货调整（结转/盘点/调价）</div>
            <div class="stat-val">{{ formatMoney(costSummary.cogs) }}</div>
          </div>
          <div class="stat-box amber">
            <div class="stat-label">期间费用、手续费与坏账损失</div>
            <div class="stat-val">{{ formatMoney(costSummary.opex) }}</div>
          </div>
        </div>
        <el-row :gutter="16" style="margin-top: 12px">
          <el-col :xs="24" :lg="13">
            <el-table v-loading="loading.costStructure" :data="displayCostStructureList" border style="width: 100%">
              <el-table-column label="成本费用构成项" align="center" prop="displayCostType" />
              <el-table-column label="金额" align="right" header-align="right" width="160">
                <template #default="scope">{{ formatMoney(scope.row.costAmount) }}</template>
              </el-table-column>
              <el-table-column label="占比" align="center" width="110">
                <template #default="scope">
                  <el-tag size="small" effect="plain">{{ scope.row.ratio }}%</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
          <el-col :xs="24" :lg="11">
            <div v-if="displayCostStructureList.length > 0" ref="costStructureChartRef" class="report-chart donut"></div>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="Report">
import { useRoute } from "vue-router"
import * as echarts from "echarts"
import { receivableAging, payableAging, receivableReconciliation, payableReconciliation, profitLoss, revenueExpense, costStructure, exportReport } from "@/api/business/report"
import { useBusinessOptions } from "@/utils/businessOptions"

const route = useRoute()
const { proxy } = getCurrentInstance()
const { customerOptions, supplierOptions, loadCustomers, loadSuppliers, getCustomerName, getSupplierName } = useBusinessOptions()

function formatMoney(v) {
  const n = Number(v || 0)
  return n < 0 ? `-￥${Math.abs(n).toFixed(2)}` : `￥${n.toFixed(2)}`
}

function formatAmount(v) {
  return Number(v || 0).toFixed(2)
}

const validTabs = ["receivableAging", "payableAging", "receivableReconciliation", "payableReconciliation", "profitLoss", "revenueExpense", "costStructure"]
const active = ref(validTabs.includes(route.query.tab) ? String(route.query.tab) : "receivableAging")

const expenseCodeMap = {
  logistics: "物流运费",
  freight: "物流运费",
  rent: "仓储房租",
  warehouse_rent: "仓储房租",
  office: "办公费用",
  salary: "人员薪资",
  utility: "水电物业",
  marketing: "市场推广",
  travel: "差旅费",
  other: "其他期间费用"
}

function formatCostTypeLabel(raw) {
  if (!raw) return "-"
  const text = String(raw)
  if (text.startsWith("费用-")) {
    const code = text.slice(3).trim()
    return `费用-${expenseCodeMap[code.toLowerCase()] || code}`
  }
  return expenseCodeMap[text.toLowerCase()] || text
}

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

function getCurrentYearDateRange() {
  const currentDate = new Date()
  const currentYear = currentDate.getFullYear()
  return {
    startDate: formatDateValue(new Date(currentYear, 0, 1)),
    endDate: formatDateValue(new Date(currentYear, 11, 31))
  }
}

const currentYearDateRange = getCurrentYearDateRange()

const receivableQuery = reactive({ customerId: undefined })
const payableQuery = reactive({ supplierId: undefined })
const receivableReconciliationQuery = reactive({ customerId: undefined })
const payableReconciliationQuery = reactive({ supplierId: undefined })
const profitLossQuery = reactive({ startDate: currentYearDateRange.startDate, endDate: currentYearDateRange.endDate })
const revenueExpenseQuery = reactive({ startDate: currentYearDateRange.startDate, endDate: currentYearDateRange.endDate })
const costStructureQuery = reactive({ startDate: currentYearDateRange.startDate, endDate: currentYearDateRange.endDate })

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

const costSummary = computed(() => {
  const cogsTypes = new Set(["销售成本结转", "盘点损益", "成本调价"])
  let total = 0
  let cogs = 0
  let opex = 0
  for (const item of costStructureList.value) {
    const val = Number(item.costAmount || 0)
    total += val
    if (cogsTypes.has(item.costType)) cogs += val
    else opex += val
  }
  return { total, cogs, opex }
})

const displayCostStructureList = computed(() => {
  const totalAbs = costStructureList.value.reduce((s, i) => s + Math.abs(Number(i.costAmount || 0)), 0)
  return costStructureList.value.map(item => ({
    ...item,
    displayCostType: formatCostTypeLabel(item.costType),
    ratio: totalAbs > 0 ? ((Math.abs(Number(item.costAmount || 0)) * 100) / totalAbs).toFixed(2) : "0.00"
  }))
})

function handleExport(reportType, query, fileName) {
  exportReport({ reportType, ...query }).then(blob => {
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", fileName)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }).catch(() => {
    proxy.$modal.msgError("报表导出失败")
  })
}

function getReceivableAging() {
  loading.receivableAging = true
  receivableAging(receivableQuery).then(response => {
    receivableAgingList.value = response.data || []
    nextTick(() => renderReceivableAgingChart())
  }).finally(() => { loading.receivableAging = false })
}
function resetReceivableAging() { proxy.resetForm("receivableAgingRef"); getReceivableAging() }

function getPayableAging() {
  loading.payableAging = true
  payableAging(payableQuery).then(response => {
    payableAgingList.value = response.data || []
    nextTick(() => renderPayableAgingChart())
  }).finally(() => { loading.payableAging = false })
}
function resetPayableAging() { proxy.resetForm("payableAgingRef"); getPayableAging() }

function getReceivableReconciliation() {
  loading.receivableReconciliation = true
  receivableReconciliation(receivableReconciliationQuery).then(response => {
    receivableReconciliationList.value = response.data || []
    nextTick(() => renderReceivableReconciliationChart())
  }).finally(() => { loading.receivableReconciliation = false })
}
function resetReceivableReconciliation() { proxy.resetForm("receivableReconciliationRef"); getReceivableReconciliation() }

function getPayableReconciliation() {
  loading.payableReconciliation = true
  payableReconciliation(payableReconciliationQuery).then(response => {
    payableReconciliationList.value = response.data || []
    nextTick(() => renderPayableReconciliationChart())
  }).finally(() => { loading.payableReconciliation = false })
}
function resetPayableReconciliation() { proxy.resetForm("payableReconciliationRef"); getPayableReconciliation() }

function getProfitLoss() {
  profitLoss(profitLossQuery).then(response => {
    profitLossData.value = response.data
    profitLossLoaded.value = true
    nextTick(() => renderProfitLossChart())
  })
}
function resetProfitLoss() {
  proxy.resetForm("profitLossRef")
  profitLossQuery.startDate = currentYearDateRange.startDate
  profitLossQuery.endDate = currentYearDateRange.endDate
  getProfitLoss()
}

function getRevenueExpense() {
  revenueExpense(revenueExpenseQuery).then(response => {
    revenueExpenseData.value = response.data
    revenueExpenseLoaded.value = true
    nextTick(() => renderRevenueExpenseChart())
  })
}
function resetRevenueExpense() {
  proxy.resetForm("revenueExpenseRef")
  revenueExpenseQuery.startDate = currentYearDateRange.startDate
  revenueExpenseQuery.endDate = currentYearDateRange.endDate
  getRevenueExpense()
}

function getCostStructure() {
  loading.costStructure = true
  costStructure(costStructureQuery).then(response => {
    costStructureList.value = response.data || []
    costStructureLoaded.value = true
    nextTick(() => renderCostStructureChart())
  }).finally(() => { loading.costStructure = false })
}
function resetCostStructure() {
  proxy.resetForm("costStructureRef")
  costStructureQuery.startDate = currentYearDateRange.startDate
  costStructureQuery.endDate = currentYearDateRange.endDate
  getCostStructure()
}

function loadTabIfNeeded(tab) {
  if (tab === "receivableAging" && receivableAgingList.value.length === 0) getReceivableAging()
  if (tab === "payableAging" && payableAgingList.value.length === 0) getPayableAging()
  if (tab === "receivableReconciliation" && receivableReconciliationList.value.length === 0) getReceivableReconciliation()
  if (tab === "payableReconciliation" && payableReconciliationList.value.length === 0) getPayableReconciliation()
  if (tab === "profitLoss" && !profitLossLoaded.value) getProfitLoss()
  if (tab === "revenueExpense" && !revenueExpenseLoaded.value) getRevenueExpense()
  if (tab === "costStructure" && !costStructureLoaded.value) getCostStructure()
  nextTick(() => {
    ;[profitLossChartInstance, receivableAgingChartInstance, payableAgingChartInstance, receivableReconciliationChartInstance, payableReconciliationChartInstance, revenueExpenseChartInstance, costStructureChartInstance].forEach(inst => {
      if (inst) inst.resize()
    })
  })
}

watch(active, currentActiveTab => loadTabIfNeeded(currentActiveTab))
watch(() => route.query.tab, tab => {
  if (tab && validTabs.includes(String(tab))) active.value = String(tab)
})

Promise.all([loadCustomers(), loadSuppliers()]).finally(() => loadTabIfNeeded(active.value))

const chartTooltip = {
  backgroundColor: "#ffffff",
  borderColor: "#e5e7eb",
  borderWidth: 1,
  textStyle: { color: "#111827", fontSize: 13 },
  extraCssText: "box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08); border-radius: 8px;"
}

const commonAxis = {
  axisLabel: { color: "#6b7280", fontSize: 12 },
  axisLine: { lineStyle: { color: "#e5e7eb" } },
  axisTick: { show: false },
  splitLine: { lineStyle: { color: "#f3f4f6", type: "dashed" } }
}

function makeBarGradient(topColor, bottomColor) {
  return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: topColor },
    { offset: 1, color: bottomColor }
  ])
}

function ensureChartInstance(instance, el) {
  if (!el) {
    if (instance) instance.dispose()
    return null
  }
  if (instance && instance.getDom() !== el) {
    instance.dispose()
    instance = null
  }
  return instance || echarts.init(el)
}

function renderProfitLossChart() {
  if (!profitLossChartRef.value || !profitLossData.value) return
  profitLossChartInstance = ensureChartInstance(profitLossChartInstance, profitLossChartRef.value)
  const d = profitLossData.value
  const colors = [
    makeBarGradient("#3b82f6", "#2563eb"),
    makeBarGradient("#94a3b8", "#64748b"),
    makeBarGradient("#14b8a6", "#0d9488"),
    makeBarGradient("#fbbf24", "#f59e0b"),
    makeBarGradient("#f87171", "#ef4444"),
    makeBarGradient("#34d399", "#10b981")
  ]
  const vals = [
    Number(d.netIncomeAmount || 0),
    Number(d.salesCostAmount || 0),
    Number(d.grossProfitAmount || 0),
    Number(d.expenseAmount || 0),
    Number(d.badDebtAmount || 0),
    Number(d.netProfitAmount || 0)
  ]
  profitLossChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, valueFormatter: v => formatMoney(v), ...chartTooltip },
    grid: { top: 36, right: 20, bottom: 28, left: 72 },
    xAxis: { type: "category", data: ["营业净收入", "销售成本", "销售毛利", "期间费用", "坏账损失", "营业净利润"], ...commonAxis },
    yAxis: { type: "value", ...commonAxis },
    series: [{
      type: "bar",
      barMaxWidth: 40,
      label: { show: true, color: "#475569", formatter: p => formatMoney(p.value) },
      data: vals.map((v, i) => ({
        value: v,
        label: { position: v >= 0 ? "top" : "bottom" },
        itemStyle: { color: colors[i], borderRadius: v >= 0 ? [8, 8, 0, 0] : [0, 0, 8, 8] }
      }))
    }]
  })
}

function renderAging(inst, el, list, resolver) {
  if (!el || !list.length) {
    if (inst) inst.dispose()
    return null
  }
  const c = ensureChartInstance(inst, el)
  const labels = list.map(i => i.dimensionName || resolver(i.dimensionId))
  c.setOption({
    color: ["#10b981", "#3b82f6", "#f59e0b", "#f97316", "#ef4444"],
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, valueFormatter: v => formatMoney(v), ...chartTooltip },
    legend: { top: 0, icon: "roundRect", itemWidth: 10, itemHeight: 10 },
    grid: { top: 38, right: 20, bottom: 28, left: 68 },
    xAxis: { type: "category", data: labels, ...commonAxis },
    yAxis: { type: "value", ...commonAxis },
    series: [
      { name: "未逾期", type: "bar", stack: "total", barMaxWidth: 40, data: list.map(i => Number(i.notDueAmount || 0)) },
      { name: "0-30天", type: "bar", stack: "total", barMaxWidth: 40, data: list.map(i => Number(i.days0To30Amount || 0)) },
      { name: "31-60天", type: "bar", stack: "total", barMaxWidth: 40, data: list.map(i => Number(i.days31To60Amount || 0)) },
      { name: "61-90天", type: "bar", stack: "total", barMaxWidth: 40, data: list.map(i => Number(i.days61To90Amount || 0)) },
      { name: "90天以上", type: "bar", stack: "total", barMaxWidth: 40, itemStyle: { borderRadius: [6, 6, 0, 0] }, data: list.map(i => Number(i.days90AboveAmount || 0)) }
    ]
  })
  return c
}

function renderReceivableAgingChart() {
  receivableAgingChartInstance = renderAging(receivableAgingChartInstance, receivableAgingChartRef.value, receivableAgingList.value, getCustomerName)
}
function renderPayableAgingChart() {
  payableAgingChartInstance = renderAging(payableAgingChartInstance, payableAgingChartRef.value, payableAgingList.value, getSupplierName)
}

function renderRecon(inst, el, list, resolver, names) {
  if (!el || !list.length) {
    if (inst) inst.dispose()
    return null
  }
  const c = ensureChartInstance(inst, el)
  const labels = list.map(i => i.dimensionName || resolver(i.dimensionId))
  c.setOption({
    color: ["#2563eb", "#10b981", "#f59e0b"],
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, valueFormatter: v => formatMoney(v), ...chartTooltip },
    legend: { top: 0, icon: "roundRect", itemWidth: 10, itemHeight: 10 },
    grid: { top: 38, right: 20, bottom: 28, left: 68 },
    xAxis: { type: "category", data: labels, ...commonAxis },
    yAxis: { type: "value", ...commonAxis },
    series: [
      { name: names[0], type: "bar", barMaxWidth: 28, itemStyle: { borderRadius: [6, 6, 0, 0] }, data: list.map(i => Number(i.billAmount || 0)) },
      { name: names[1], type: "bar", barMaxWidth: 28, itemStyle: { borderRadius: [6, 6, 0, 0] }, data: list.map(i => Number(i.paidAmount || 0)) },
      { name: names[2], type: "bar", barMaxWidth: 28, itemStyle: { borderRadius: [6, 6, 0, 0] }, data: list.map(i => Number(i.diffAmount || 0)) }
    ]
  })
  return c
}

function renderReceivableReconciliationChart() {
  receivableReconciliationChartInstance = renderRecon(receivableReconciliationChartInstance, receivableReconciliationChartRef.value, receivableReconciliationList.value, getCustomerName, ["应收净额", "已收金额", "剩余未收"])
}
function renderPayableReconciliationChart() {
  payableReconciliationChartInstance = renderRecon(payableReconciliationChartInstance, payableReconciliationChartRef.value, payableReconciliationList.value, getSupplierName, ["应付净额", "已付金额", "未付差额"])
}

function renderRevenueExpenseChart() {
  if (!revenueExpenseChartRef.value || !revenueExpenseData.value) return
  revenueExpenseChartInstance = ensureChartInstance(revenueExpenseChartInstance, revenueExpenseChartRef.value)
  const vals = [
    Number(revenueExpenseData.value.incomeAmount || 0),
    Number(revenueExpenseData.value.expenseAmount || 0),
    Number(revenueExpenseData.value.netAmount || 0)
  ]
  const palette = [
    makeBarGradient("#34d399", "#10b981"),
    makeBarGradient("#fbbf24", "#f59e0b"),
    vals[2] >= 0 ? makeBarGradient("#3b82f6", "#2563eb") : makeBarGradient("#f87171", "#ef4444")
  ]
  revenueExpenseChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, valueFormatter: v => formatMoney(v), ...chartTooltip },
    grid: { top: 36, right: 20, bottom: 32, left: 72 },
    xAxis: { type: "category", data: ["现金流入", "现金流出", "现金净额"], ...commonAxis },
    yAxis: { type: "value", ...commonAxis },
    series: [{
      type: "bar",
      barMaxWidth: 46,
      label: { show: true, color: "#475569", formatter: p => formatMoney(p.value) },
      data: vals.map((v, i) => ({
        value: v,
        label: { position: v >= 0 ? "top" : "bottom" },
        itemStyle: { color: palette[i], borderRadius: v >= 0 ? [8, 8, 0, 0] : [0, 0, 8, 8] }
      }))
    }]
  })
}

function renderCostStructureChart() {
  if (!costStructureChartRef.value || displayCostStructureList.value.length === 0) return
  costStructureChartInstance = ensureChartInstance(costStructureChartInstance, costStructureChartRef.value)
  const data = displayCostStructureList.value
    .filter(item => Number(item.costAmount || 0) > 0)
    .map(item => ({ name: item.displayCostType, value: Number(item.costAmount || 0), ratio: item.ratio }))
  const ratioMap = Object.fromEntries(data.map(d => [d.name, d.ratio]))
  costStructureChartInstance.setOption({
    color: ["#2563eb", "#0d9488", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4"],
    tooltip: { trigger: "item", formatter: p => `${p.name}<br/>${formatMoney(p.value)} (${p.percent}%)`, ...chartTooltip },
    legend: {
      bottom: 4,
      icon: "roundRect",
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { fontSize: 12, color: "#475569" },
      formatter: name => `${name} ${ratioMap[name] ?? "0.00"}%`
    },
    title: {
      text: formatMoney(costSummary.value.total),
      subtext: "成本费用总额",
      left: "center",
      top: "37%",
      textStyle: { fontSize: 16, fontWeight: 700, color: "#0f172a" },
      subtextStyle: { fontSize: 11.5, color: "#64748b" }
    },
    series: [{
      type: "pie",
      radius: ["45%", "64%"],
      center: ["50%", "43%"],
      avoidLabelOverlap: true,
      minShowLabelAngle: 0,
      itemStyle: { borderRadius: 5, borderColor: "#ffffff", borderWidth: 2 },
      label: { show: true, formatter: "{b}\n{d}%", fontSize: 11.5, lineHeight: 15, color: "#334155" },
      labelLine: { length: 10, length2: 12 },
      data
    }]
  })
}

onBeforeUnmount(() => {
  ;[profitLossChartInstance, receivableAgingChartInstance, payableAgingChartInstance, receivableReconciliationChartInstance, payableReconciliationChartInstance, revenueExpenseChartInstance, costStructureChartInstance].forEach(inst => {
    if (inst) inst.dispose()
  })
})
</script>

<style scoped lang="scss">
.report-chart {
  width: 100%;
  height: 310px;
  margin-top: 12px;
  padding: 12px 8px 4px;
  border-radius: 12px;
  background: var(--panel-subtle-bg, #f8fafc);
  border: 1px solid var(--panel-border, #e2e8f0);

  &.donut {
    margin-top: 0;
    height: 300px;
  }
}

.stat-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 12px;

  &.cols-3 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.stat-box {
  padding: 16px 18px;
  border-radius: 12px;
  background: var(--panel-bg, #ffffff);
  border: 1px solid var(--panel-border, #e5e7eb);
}

.stat-label {
  font-size: 12.5px;
  font-weight: 500;
  color: var(--panel-muted, #64748b);
}

.stat-val {
  margin-top: 8px;
  font-size: 22px;
  font-weight: 700;
  color: var(--panel-heading, #0f172a);
  font-variant-numeric: tabular-nums;
}

.stat-sub {
  margin-top: 6px;
  font-size: 12px;
  color: var(--panel-muted, #64748b);
}

@media (max-width: 960px) {
  .stat-strip,
  .stat-strip.cols-3 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
