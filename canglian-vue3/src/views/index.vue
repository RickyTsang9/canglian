<template>
  <div class="app-container home" v-loading="loading">
    <!-- 纯浅色极简驾驶舱头部面板 -->
    <section class="hero-panel">
      <div class="hero-main">
        <div class="hero-copy">
          <div class="hero-eyebrow">
            <span class="status-dot" />
            <span>仓链智慧供应链云</span>
            <span class="sep">/</span>
            <span>{{ todayLabel }}</span>
          </div>
          <h2>{{ greetingText }}，{{ userNickName }}</h2>
          <p>实时掌握企业现金流、签收口径经营利润、成本结构与往来账龄。</p>
        </div>

        <div class="hero-controls">
          <div class="period-segment">
            <button
              v-for="preset in periodPresets"
              :key="preset.key"
              type="button"
              class="period-btn"
              :class="{ active: activePreset === preset.key }"
              @click="applyPreset(preset.key)"
            >
              {{ preset.label }}
            </button>
          </div>
          <div class="date-picker-wrap">
            <el-date-picker
              v-model="dateRange"
              value-format="YYYY-MM-DD"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="activePreset = 'custom'"
            />
          </div>
        </div>
      </div>

      <!-- 极简快捷入口 + 口径标识 -->
      <div class="hero-toolbar">
        <div class="quick-actions">
          <button
            v-for="act in quickActions"
            :key="act.path"
            type="button"
            class="quick-chip"
            @click="safePush(act.path)"
          >
            <el-icon><component :is="act.icon" /></el-icon>
            <span>{{ act.label }}</span>
          </button>
        </div>
        <div class="policy-tags">
          <span class="policy-tag">签收确认营收</span>
          <span class="policy-tag">真实成本结转</span>
          <span class="policy-tag">SN 全程追溯</span>
        </div>
      </div>
    </section>

    <el-alert
      v-if="dashboardError"
      class="dashboard-alert"
      title="首页经营数据暂时无法加载"
      :description="dashboardError"
      type="warning"
      show-icon
      :closable="false"
    />

    <!-- 4 大极简核心财务 KPI 卡片 -->
    <el-row :gutter="16" class="summary-row">
      <el-col v-for="item in summaryList" :key="item.title" :xs="24" :sm="12" :lg="6">
        <div class="kpi-card" @click="goToReport(item.tab)">
          <div class="kpi-top">
            <div>
              <div class="kpi-title">{{ item.title }}</div>
              <div class="kpi-subtitle">{{ item.subtitle }}</div>
            </div>
            <div class="kpi-icon" :class="item.type">
              <el-icon :size="18"><component :is="item.icon" /></el-icon>
            </div>
          </div>
          <div class="kpi-value" :class="{ negative: Number(item.value || 0) < 0 }">
            <span v-if="Number(item.value || 0) < 0" class="minus-sign">-</span><span class="currency">￥</span>{{ formatAmount(Math.abs(Number(item.value || 0))) }}
          </div>
          <div class="kpi-footer">
            <span class="kpi-meta">{{ item.meta }}</span>
            <span class="kpi-arrow">查看 &rarr;</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 极简业务运营状态条 -->
    <section class="ops-strip">
      <div
        v-for="op in opsPulseList"
        :key="op.label"
        class="ops-tile"
        @click="safePush(op.route)"
      >
        <div class="ops-left">
          <span class="ops-icon"><el-icon :size="15"><component :is="op.icon" /></el-icon></span>
          <span class="ops-label">{{ op.label }}</span>
        </div>
        <div class="ops-right">
          <span v-if="op.highlight && op.value > 0" class="alert-dot" />
          <strong class="ops-num">{{ op.value }}</strong>
        </div>
      </div>
    </section>

    <!-- 图表第一行：利润构成 + 现金收支 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="14">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-card-head">
              <div>
                <div class="chart-title">经营利润构成（权责发生制 · 签收口径）</div>
                <div class="chart-sub">
                  毛利率 <strong>{{ profitLossData?.grossMarginRate ?? '0.00' }}%</strong>
                  <span class="dot-sep">·</span>
                  净利率 <strong>{{ profitLossData?.netMarginRate ?? '0.00' }}%</strong>
                </div>
              </div>
              <el-button link type="primary" @click="goToReport('profitLoss')">查看明细 &gt;</el-button>
            </div>
          </template>
          <div v-if="profitLossData" ref="profitLossChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无利润表数据" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-card-head">
              <div>
                <div class="chart-title">现金收支对比（收付实现制）</div>
                <div class="chart-sub">已确认收付款、期间费用与坏账收回</div>
              </div>
              <el-button link type="primary" @click="goToReport('revenueExpense')">查看明细 &gt;</el-button>
            </div>
          </template>
          <div v-if="revenueExpenseData" ref="revenueExpenseChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无收支汇总数据" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表第二行：成本结构环形图 + 应收账龄 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="10">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-card-head">
              <div>
                <div class="chart-title">成本费用结构分布</div>
                <div class="chart-sub">与利润表成本费用总额 100% 勾稽一致</div>
              </div>
              <el-button link type="primary" @click="goToReport('costStructure')">查看明细 &gt;</el-button>
            </div>
          </template>
          <div v-if="costStructureList.length > 0" ref="costStructureChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无成本结构数据" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-card-head">
              <div>
                <div class="chart-title">客户应收账龄分布</div>
                <div class="chart-sub">按逾期天数组合展示未收欠款结构（已剔除坏账核销）</div>
              </div>
              <el-button link type="primary" @click="goToReport('receivableAging')">查看明细 &gt;</el-button>
            </div>
          </template>
          <div v-if="receivableAgingList.length > 0" ref="receivableAgingChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无应收账龄数据" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表第三行：应付账龄 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-card-head">
              <div>
                <div class="chart-title">供应商应付账龄分布</div>
                <div class="chart-sub">掌握采购应付款项到期节奏，提前统筹资金调拨</div>
              </div>
              <el-button link type="primary" @click="goToReport('payableAging')">查看明细 &gt;</el-button>
            </div>
          </template>
          <div v-if="payableAgingList.length > 0" ref="payableAgingChartRef" class="chart-panel"></div>
          <el-empty v-else class="chart-empty" description="暂无应付账龄数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import * as echarts from "echarts"
import { receivableAging, payableAging, profitLoss, revenueExpense, costStructure } from "@/api/business/report"
import { getWorkbenchSummary } from "@/api/business/workbench"
import useSettingsStore from "@/store/modules/settings"
import useUserStore from "@/store/modules/user"
import auth from "@/plugins/auth"
import { useBusinessOptions } from "@/utils/businessOptions"

const router = useRouter()
const loading = ref(false)
const settingsStore = useSettingsStore()
const userStore = useUserStore()
const { loadCustomers, loadSuppliers, getCustomerName, getSupplierName } = useBusinessOptions()

const dateRange = ref([])
const activePreset = ref("year")
const dashboardError = ref("")
const workbenchData = ref({})

const userNickName = computed(() => userStore.nickName || userStore.name || "管理员")
const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return "早上好"
  if (h < 18) return "下午好"
  return "晚上好"
})
const todayLabel = computed(() => {
  const d = new Date()
  const weeks = ["日", "一", "二", "三", "四", "五", "六"]
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weeks[d.getDay()]}`
})

const periodPresets = [
  { key: "month", label: "本月" },
  { key: "quarter", label: "本季" },
  { key: "year", label: "本年" }
]

function hasRouteAccess(path) {
  if (!path) return false
  const resolved = router.resolve(path)
  return resolved && resolved.matched && resolved.matched.length > 0 && resolved.name !== "NotFound" && !resolved.matched.some(r => String(r.path || "").includes(":pathMatch"))
}

function safePush(target) {
  const path = typeof target === "string" ? target : target?.path
  if (!hasRouteAccess(path)) {
    ElMessage.warning("当前账号暂无访问该模块的菜单权限")
    return
  }
  router.push(target)
}

const allQuickActions = [
  { label: "销售开单", path: "/business/saleOrder", icon: "Sell", perm: "business:saleOrder:list" },
  { label: "采购入库", path: "/wms/inbound", icon: "Box", perm: "business:inbound:list" },
  { label: "扫码作业", path: "/wms/scan", icon: "FullScreen", perm: "business:scan:list" },
  { label: "业务工作台", path: "/business/workbench", icon: "Odometer", perm: "business:workbench:view" },
  { label: "财务报表", path: "/finance/report", icon: "DataAnalysis", perm: "business:report:view" }
]
const quickActions = computed(() => allQuickActions.filter(act => hasRouteAccess(act.path) || (act.perm && auth.hasPermi(act.perm))))

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

function goToReport(tab) {
  safePush({ path: "/finance/report", query: tab ? { tab } : {} })
}

const summaryList = computed(() => [
  {
    title: "现金流入",
    subtitle: "已确认收款与坏账收回",
    value: revenueExpenseData.value?.incomeAmount,
    icon: "Wallet",
    type: "income",
    tab: "revenueExpense",
    meta: "收付实现制流入"
  },
  {
    title: "现金流出",
    subtitle: "采购付款、费用与手续费",
    value: revenueExpenseData.value?.expenseAmount,
    icon: "CreditCard",
    type: "expense",
    tab: "revenueExpense",
    meta: "收付实现制流出"
  },
  {
    title: "现金净流量",
    subtitle: "统计期内现金结余",
    value: revenueExpenseData.value?.netAmount,
    icon: "Coin",
    type: "net",
    tab: "revenueExpense",
    meta: "流入减流出"
  },
  {
    title: "营业净利润",
    subtitle: "签收净收入减成本费用坏账",
    value: profitLossData.value?.netProfitAmount ?? profitLossData.value?.profitAmount,
    icon: "TrendCharts",
    type: "profit",
    tab: "profitLoss",
    meta: `净利率 ${profitLossData.value?.netMarginRate ?? "0.00"}%`
  }
])

const opsPulseList = computed(() => [
  { label: "审批待办", value: workbenchData.value.pendingApproveCount || 0, route: "/business/todoCenter", icon: "Bell", highlight: true },
  { label: "今日有效入库", value: workbenchData.value.todayInboundCount || 0, route: "/wms/inbound", icon: "Download", highlight: false },
  { label: "今日有效出库", value: workbenchData.value.todayOutboundCount || 0, route: "/wms/outbound", icon: "Upload", highlight: false },
  { label: "应收到期预警", value: workbenchData.value.dueReceivableCount || 0, route: "/finance/receivable", icon: "Clock", highlight: true },
  { label: "低库存预警", value: workbenchData.value.lowStockCount || 0, route: "/wms/stock", icon: "Warning", highlight: true }
])

const isDark = computed(() => settingsStore.isDark)
const chartTextColor = computed(() => isDark.value ? "#94a3b8" : "#6b7280")
const chartAxisLineColor = computed(() => isDark.value ? "#1e293b" : "#e5e7eb")
const chartSplitLineColor = computed(() => isDark.value ? "#1e293b" : "#f3f4f6")
const tooltipStyle = computed(() => isDark.value
  ? {
    backgroundColor: "rgba(15, 23, 42, 0.94)",
    borderColor: "#334155",
    borderWidth: 1,
    textStyle: { color: "#f8fafc", fontSize: 13 },
    padding: [10, 14]
  }
  : {
    backgroundColor: "#ffffff",
    borderColor: "#e5e7eb",
    borderWidth: 1,
    textStyle: { color: "#111827", fontSize: 13 },
    extraCssText: "box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08); border-radius: 8px;",
    padding: [10, 14]
  }
)

function getChartAxisStyle() {
  return {
    axisLabel: { color: chartTextColor.value, fontSize: 12 },
    axisLine: { lineStyle: { color: chartAxisLineColor.value } },
    axisTick: { show: false },
    splitLine: { lineStyle: { color: chartSplitLineColor.value, type: "dashed" } }
  }
}

function getChartLegendStyle() {
  return {
    icon: "roundRect",
    itemWidth: 10,
    itemHeight: 10,
    textStyle: { color: chartTextColor.value, fontSize: 12 }
  }
}

function formatDateValue(dateValue) {
  const year = dateValue.getFullYear()
  const month = String(dateValue.getMonth() + 1).padStart(2, "0")
  const day = String(dateValue.getDate()).padStart(2, "0")
  return `${year}-${month}-${day}`
}

function applyPreset(presetKey) {
  activePreset.value = presetKey
  const now = new Date()
  const y = now.getFullYear()
  const m = now.getMonth()
  let start
  let end
  if (presetKey === "month") {
    start = new Date(y, m, 1)
    end = new Date(y, m + 1, 0)
  } else if (presetKey === "quarter") {
    const qStart = Math.floor(m / 3) * 3
    start = new Date(y, qStart, 1)
    end = new Date(y, qStart + 3, 0)
  } else {
    start = new Date(y, 0, 1)
    end = new Date(y, 11, 31)
  }
  const s = formatDateValue(start)
  const e = formatDateValue(end)
  dateRange.value = [s, e]
  dashboardQuery.startDate = s
  dashboardQuery.endDate = e
}

function formatAmount(value) {
  const numberValue = Number(value || 0)
  return numberValue.toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function loadWorkbenchPulse() {
  getWorkbenchSummary().then(res => {
    workbenchData.value = res.data || {}
  }).catch(() => {})
}

function loadDashboardData() {
  if (!dashboardQuery.startDate || !dashboardQuery.endDate) return
  if (!auth.hasPermi("business:report:query")) {
    clearDashboardData()
    dashboardError.value = "当前账号暂无经营报表查看权限，如需查看经营驾驶舱数据请联系管理员授权。"
    return
  }
  loading.value = true
  dashboardError.value = ""
  Promise.all([
    profitLoss({ startDate: dashboardQuery.startDate, endDate: dashboardQuery.endDate }),
    revenueExpense({ startDate: dashboardQuery.startDate, endDate: dashboardQuery.endDate }),
    costStructure({ startDate: dashboardQuery.startDate, endDate: dashboardQuery.endDate }),
    receivableAging({}),
    payableAging({}),
    loadCustomers(),
    loadSuppliers()
  ]).then(([plRes, reRes, csRes, raRes, paRes]) => {
    profitLossData.value = plRes.data
    revenueExpenseData.value = reRes.data
    costStructureList.value = csRes.data || []
    receivableAgingList.value = raRes.data || []
    payableAgingList.value = paRes.data || []
    nextTick(() => {
      renderProfitLossChart()
      renderRevenueExpenseChart()
      renderCostStructureChart()
      renderReceivableAgingChart()
      renderPayableAgingChart()
    })
  }).catch(err => {
    clearDashboardData()
    dashboardError.value = (err && err.message) ? `经营数据加载失败：${err.message}` : "经营数据加载失败，请稍后刷新重试。"
  }).finally(() => {
    loading.value = false
  })
}

function clearDashboardData() {
  profitLossData.value = null
  revenueExpenseData.value = null
  costStructureList.value = []
  receivableAgingList.value = []
  payableAgingList.value = []
  clearChartInstances()
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

function clearChartInstances() {
  ;[profitLossChartInstance, revenueExpenseChartInstance, costStructureChartInstance, receivableAgingChartInstance, payableAgingChartInstance].forEach(i => {
    if (i) i.dispose()
  })
  profitLossChartInstance = null
  revenueExpenseChartInstance = null
  costStructureChartInstance = null
  receivableAgingChartInstance = null
  payableAgingChartInstance = null
}

function formatShortMoney(v) {
  const n = Number(v || 0)
  return n < 0 ? `-￥${Math.abs(n).toFixed(0)}` : `￥${n.toFixed(0)}`
}

function renderProfitLossChart() {
  if (!profitLossChartRef.value || !profitLossData.value) return
  profitLossChartInstance = ensureChartInstance(profitLossChartInstance, profitLossChartRef.value)
  const d = profitLossData.value
  const colors = ["#2563eb", "#94a3b8", "#0d9488", "#f59e0b", "#ef4444", "#10b981"]
  const rawVals = [
    Number(d.netIncomeAmount ?? d.incomeAmount ?? 0),
    Number(d.salesCostAmount ?? 0),
    Number(d.grossProfitAmount ?? 0),
    Number(d.expenseAmount ?? 0),
    Number(d.badDebtAmount ?? 0),
    Number(d.netProfitAmount ?? d.profitAmount ?? 0)
  ]
  profitLossChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, ...tooltipStyle.value },
    grid: { top: 32, right: 20, bottom: 28, left: 60 },
    xAxis: { type: "category", data: ["营业净收入", "销售成本", "销售毛利", "期间费用", "坏账损失", "营业净利润"], ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [{
      type: "bar",
      barMaxWidth: 32,
      label: { show: true, color: chartTextColor.value, fontSize: 11.5, formatter: p => formatShortMoney(p.value) },
      data: rawVals.map((v, i) => ({
        value: v,
        label: { position: v >= 0 ? "top" : "bottom" },
        itemStyle: {
          color: colors[i],
          borderRadius: v >= 0 ? [6, 6, 0, 0] : [0, 0, 6, 6]
        }
      }))
    }]
  })
}

function renderRevenueExpenseChart() {
  if (!revenueExpenseChartRef.value || !revenueExpenseData.value) return
  revenueExpenseChartInstance = ensureChartInstance(revenueExpenseChartInstance, revenueExpenseChartRef.value)
  const d = revenueExpenseData.value
  const vals = [
    Number(d.incomeAmount || 0),
    Number(d.expenseAmount || 0),
    Number(d.netAmount || 0)
  ]
  const palette = ["#10b981", "#f59e0b", vals[2] >= 0 ? "#2563eb" : "#ef4444"]
  revenueExpenseChartInstance.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, ...tooltipStyle.value },
    grid: { top: 32, right: 20, bottom: 32, left: 60 },
    xAxis: { type: "category", data: ["现金流入", "现金流出", "现金净流量"], ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [{
      type: "bar",
      barMaxWidth: 38,
      label: { show: true, color: chartTextColor.value, fontSize: 12, formatter: p => formatShortMoney(p.value) },
      data: vals.map((v, i) => ({
        value: v,
        label: { position: v >= 0 ? "top" : "bottom" },
        itemStyle: {
          color: palette[i],
          borderRadius: v >= 0 ? [6, 6, 0, 0] : [0, 0, 6, 6]
        }
      }))
    }]
  })
}

function renderCostStructureChart() {
  if (!costStructureChartRef.value || costStructureList.value.length === 0) return
  costStructureChartInstance = ensureChartInstance(costStructureChartInstance, costStructureChartRef.value)
  const chartData = costStructureList.value
    .filter(item => Number(item.costAmount || 0) > 0)
    .map(item => ({ name: formatCostTypeLabel(item.costType), value: Number(item.costAmount || 0) }))
  const totalCost = chartData.reduce((s, i) => s + i.value, 0)
  const pctMap = {}
  chartData.forEach(item => {
    pctMap[item.name] = totalCost > 0 ? ((item.value / totalCost) * 100).toFixed(1) : "0.0"
  })
  costStructureChartInstance.setOption({
    color: ["#2563eb", "#0d9488", "#f59e0b", "#ef4444", "#8b5cf6", "#64748b"],
    tooltip: { trigger: "item", formatter: "{b}<br/>￥{c} ({d}%)", ...tooltipStyle.value },
    legend: {
      bottom: 0,
      itemGap: 14,
      formatter: name => `${name} ${pctMap[name] ?? "0.0"}%`,
      ...getChartLegendStyle()
    },
    title: {
      text: `￥${formatAmount(totalCost)}`,
      subtext: "成本费用总额",
      left: "center",
      top: "35%",
      textStyle: { fontSize: 16, fontWeight: 700, color: isDark.value ? "#f8fafc" : "#111827" },
      subtextStyle: { fontSize: 12, color: chartTextColor.value }
    },
    series: [{
      type: "pie",
      radius: ["45%", "63%"],
      center: ["50%", "41%"],
      avoidLabelOverlap: true,
      minShowLabelAngle: 0,
      itemStyle: { borderRadius: 5, borderColor: isDark.value ? "#111827" : "#ffffff", borderWidth: 3 },
      label: {
        show: true,
        formatter: "{b}\n{d}%",
        color: chartTextColor.value,
        fontSize: 11.5,
        lineHeight: 15
      },
      labelLine: { length: 10, length2: 10, smooth: true },
      data: chartData
    }]
  })
}

function renderAgingChart(instance, el, rows, nameResolver) {
  if (!el || !rows.length) {
    if (instance) instance.dispose()
    return null
  }
  const chart = ensureChartInstance(instance, el)
  const labels = rows.map(item => item.dimensionName || nameResolver(item.dimensionId))
  chart.setOption({
    color: ["#10b981", "#3b82f6", "#f59e0b", "#f97316", "#ef4444"],
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" }, ...tooltipStyle.value },
    legend: { top: 0, ...getChartLegendStyle() },
    grid: { top: 38, right: 20, bottom: 28, left: 56 },
    xAxis: { type: "category", data: labels, ...getChartAxisStyle() },
    yAxis: { type: "value", ...getChartAxisStyle() },
    series: [
      { name: "未逾期", type: "bar", stack: "total", barMaxWidth: 36, data: rows.map(i => Number(i.notDueAmount || 0)) },
      { name: "0-30天", type: "bar", stack: "total", barMaxWidth: 36, data: rows.map(i => Number(i.days0To30Amount || 0)) },
      { name: "31-60天", type: "bar", stack: "total", barMaxWidth: 36, data: rows.map(i => Number(i.days31To60Amount || 0)) },
      { name: "61-90天", type: "bar", stack: "total", barMaxWidth: 36, data: rows.map(i => Number(i.days61To90Amount || 0)) },
      { name: "90天以上", type: "bar", stack: "total", barMaxWidth: 36, itemStyle: { borderRadius: [5, 5, 0, 0] }, data: rows.map(i => Number(i.days90AboveAmount || 0)) }
    ]
  })
  return chart
}

function renderReceivableAgingChart() {
  receivableAgingChartInstance = renderAgingChart(receivableAgingChartInstance, receivableAgingChartRef.value, receivableAgingList.value, getCustomerName)
}

function renderPayableAgingChart() {
  payableAgingChartInstance = renderAgingChart(payableAgingChartInstance, payableAgingChartRef.value, payableAgingList.value, getSupplierName)
}

function resizeCharts() {
  ;[profitLossChartInstance, revenueExpenseChartInstance, costStructureChartInstance, receivableAgingChartInstance, payableAgingChartInstance].forEach(i => {
    if (i) i.resize()
  })
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
  applyPreset("year")
  loadWorkbenchPulse()
  window.addEventListener("resize", resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeCharts)
  clearChartInstances()
})
</script>

<style scoped lang="scss">
.home {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-panel {
  padding: 22px 24px 16px;
  border-radius: 14px;
  background: var(--panel-bg, #ffffff);
  border: 1px solid var(--panel-border, #e5e7eb);
  box-shadow: var(--panel-shadow);
}

.hero-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  flex-wrap: wrap;
}

.hero-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 12px;
  font-weight: 500;
  color: var(--panel-muted, #6b7280);

  .sep {
    color: var(--panel-border-strong, #d1d5db);
  }
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #10b981;
}

.hero-copy h2 {
  margin: 8px 0 4px;
  font-size: 22px;
  font-weight: 700;
  color: var(--panel-heading, #111827);
  letter-spacing: -0.01em;
}

.hero-copy p {
  margin: 0;
  font-size: 13.5px;
  color: var(--panel-muted, #6b7280);
}

.hero-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.period-segment {
  display: inline-flex;
  padding: 3px;
  border-radius: 9px;
  background: var(--panel-subtle-bg, #f3f4f6);
  border: 1px solid var(--panel-border, #e5e7eb);
}

.period-btn {
  padding: 5px 13px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--panel-muted, #6b7280);
  font-size: 12.5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;

  &:hover:not(.active) {
    color: var(--panel-heading, #111827);
  }

  &.active {
    background: var(--panel-bg, #ffffff);
    color: var(--panel-heading, #111827);
    font-weight: 600;
    box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
  }
}

.hero-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--panel-border, #f3f4f6);
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 13px;
  border-radius: 8px;
  border: 1px solid var(--panel-border, #e5e7eb);
  background: var(--panel-bg, #ffffff);
  color: var(--panel-text, #374151);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;

  &:hover {
    border-color: #bfdbfe;
    background: var(--primary-soft-bg, #eff6ff);
    color: var(--brand-primary, #2563eb);
  }
}

.policy-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.policy-tag {
  padding: 3px 9px;
  border-radius: 6px;
  font-size: 11.5px;
  font-weight: 500;
  color: var(--panel-muted, #6b7280);
  background: var(--panel-subtle-bg, #f9fafb);
  border: 1px solid var(--panel-border, #e5e7eb);
}

.summary-row {
  row-gap: 16px;
}

.kpi-card {
  padding: 18px 20px 14px;
  border-radius: 13px;
  background: var(--panel-bg, #ffffff);
  border: 1px solid var(--panel-border, #e5e7eb);
  box-shadow: var(--panel-shadow);
  cursor: pointer;
  transition: border-color 0.16s ease, box-shadow 0.16s ease, transform 0.16s ease;

  &:hover {
    border-color: var(--panel-border-strong, #cbd5e1);
    box-shadow: var(--panel-shadow-hover);
    transform: translateY(-1px);
  }
}

.kpi-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.kpi-title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--panel-heading, #111827);
}

.kpi-subtitle {
  margin-top: 3px;
  font-size: 12px;
  color: var(--panel-muted, #6b7280);
}

.kpi-icon {
  width: 36px;
  height: 36px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: var(--panel-subtle-bg, #f3f4f6);
  color: var(--panel-text, #4b5563);

  &.income { background: #ecfdf5; color: #059669; }
  &.expense { background: #fffbeb; color: #d97706; }
  &.net { background: #eff6ff; color: #2563eb; }
  &.profit { background: #f0fdfa; color: #0d9488; }
}

.kpi-value {
  margin-top: 16px;
  font-size: 26px;
  font-weight: 700;
  color: var(--panel-heading, #111827);
  font-variant-numeric: tabular-nums;
  line-height: 1.2;

  .currency {
    font-size: 15px;
    font-weight: 600;
    margin-right: 2px;
    color: var(--panel-muted, #9ca3af);
  }

  &.negative {
    color: #dc2626;
  }
}

.kpi-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--panel-border, #f3f4f6);
  font-size: 12px;
}

.kpi-meta {
  color: var(--panel-muted, #6b7280);
}

.kpi-arrow {
  color: var(--brand-primary, #2563eb);
  font-weight: 500;
}

.ops-strip {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.ops-tile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 13px 16px;
  border-radius: 11px;
  background: var(--panel-bg, #ffffff);
  border: 1px solid var(--panel-border, #e5e7eb);
  box-shadow: var(--panel-shadow);
  cursor: pointer;
  transition: border-color 0.15s ease, background 0.15s ease;

  &:hover {
    border-color: var(--panel-border-strong, #cbd5e1);
    background: var(--panel-subtle-bg, #f9fafb);
  }
}

.ops-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ops-icon {
  display: inline-flex;
  align-items: center;
  color: var(--panel-muted, #6b7280);
}

.ops-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--panel-text, #374151);
}

.ops-right {
  display: flex;
  align-items: center;
  gap: 7px;
}

.alert-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #f59e0b;
}

.ops-num {
  font-size: 18px;
  font-weight: 700;
  color: var(--panel-heading, #111827);
  font-variant-numeric: tabular-nums;
}

.chart-row {
  row-gap: 16px;
}

.chart-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.chart-title {
  font-size: 14.5px;
  font-weight: 650;
  color: var(--panel-heading, #111827);
}

.chart-sub {
  margin-top: 3px;
  font-size: 12px;
  font-weight: 400;
  color: var(--panel-muted, #6b7280);

  strong {
    color: var(--panel-heading, #111827);
    font-weight: 600;
  }

  .dot-sep {
    margin: 0 6px;
  }
}

.chart-panel {
  width: 100%;
  height: 310px;
}

.chart-empty {
  height: 310px;
}

@media (max-width: 1100px) {
  .ops-strip {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ops-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
