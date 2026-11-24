<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单据号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入单据号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 220px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务状态" prop="bizStatus">
        <el-select v-model="queryParams.bizStatus" placeholder="请选择业务状态" clearable style="width: 180px">
          <el-option v-for="statusOption in bizStatusOptions" :key="statusOption.value" :label="statusOption.label" :value="statusOption.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:saleOrder:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:saleOrder:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:saleOrder:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:saleOrder:export', 'business:saleOrder:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="saleOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="48" align="center" />
      <el-table-column label="单据号" align="center" prop="orderNo" min-width="145" show-overflow-tooltip />
      <el-table-column label="单据类型" align="center" prop="orderType" width="88">
        <template #default="scope">
          <el-tag :type="scope.row.orderType === 'quote' ? 'info' : 'primary'">
            {{ scope.row.orderType === "quote" ? "销售报价" : "销售订单" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="客户" align="center" min-width="125" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="105" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="业务日期" align="center" prop="businessDate" width="108">
        <template #default="scope">{{ parseTime(scope.row.businessDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column v-if="!isQuotePage" label="发货进度" align="center" width="135">
        <template #default="scope">
          <el-progress :percentage="calcProgress(scope.row)" :status="calcProgress(scope.row) >= 100 ? 'success' : ''" :stroke-width="12" />
        </template>
      </el-table-column>
      <el-table-column label="税额" align="right" prop="taxTotalAmount" width="88">
        <template #default="scope">{{ Number(scope.row.taxTotalAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="整单优惠" align="right" prop="discountAmount" width="90">
        <template #default="scope">{{ Number(scope.row.discountAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="订单总额" align="right" prop="totalAmount" width="105">
        <template #default="scope">{{ Number(scope.row.totalAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="业务状态" align="center" prop="bizStatus" width="88">
        <template #default="scope">
          <dict-tag :options="bizStatusOptions" :value="scope.row.bizStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="245" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:saleOrder:edit']">修改</el-button>
            <el-button v-if="canApproveRow(scope.row)" link type="success" icon="CircleCheck" @click="handleApprove(scope.row)" v-hasPermi="['business:saleOrder:approve']">审批</el-button>
            <el-button v-if="isQuotePage && scope.row.bizStatus === 'approved'" link type="primary" icon="Right" @click="handlePushSale(scope.row)" v-hasPermi="['business:saleOrder:pushSale']">下推</el-button>
            <el-button v-if="!isQuotePage && canPushRow(scope.row)" link type="primary" icon="Right" @click="openPartialPush(scope.row)" v-hasPermi="['business:saleOrder:pushOutbound']">下推</el-button>
            <el-dropdown trigger="click" @command="cmd => handleRowCommand(cmd, scope.row)">
              <el-button link type="primary">更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="canUnapproveRow(scope.row)" command="unapprove">反审批</el-dropdown-item>
                  <el-dropdown-item v-if="canCloseRow(scope.row)" command="close">强制关闭</el-dropdown-item>
                  <el-dropdown-item command="trace">单据链路</el-dropdown-item>
                  <el-dropdown-item command="print">打印单据</el-dropdown-item>
                  <el-dropdown-item v-if="canEditRow(scope.row)" command="delete" divided>删除单据</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改销售单据弹窗 -->
    <el-dialog :title="title" v-model="open" width="min(1180px, 94vw)" append-to-body>
      <el-form ref="saleOrderRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="单据号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%" @change="handleCustomerChanged">
                <el-option v-for="c in activeCustomerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发货仓库" prop="warehouseId">
              <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
                <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计交期" prop="expectedDate">
              <el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择预计交期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="整单优惠" prop="discountAmount">
              <el-input-number v-model="form.discountAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>

        <el-divider content-position="left">销售明细（支持客户价自动带出、多单位、折扣与税率）</el-divider>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px">
          <el-button type="primary" plain icon="Plus" @click="handleAddItem">新增明细</el-button>
          <div style="font-size: 13px; color: #333">
            税额合计：<strong>￥{{ summaryTotals.taxTotal.toFixed(2) }}</strong> &nbsp;|&nbsp;
            整单优惠：<strong>￥{{ Number(form.discountAmount || 0).toFixed(2) }}</strong> &nbsp;|&nbsp;
            应收合计：<strong style="color: #409eff; font-size: 15px">￥{{ summaryTotals.netTotal.toFixed(2) }}</strong>
          </div>
        </div>
        <el-table :data="form.orderItemList" border>
          <el-table-column label="商品" min-width="195">
            <template #default="scope">
              <el-select v-model="scope.row.productId" placeholder="请选择商品" filterable style="width: 100%" @change="handleProductSelect(scope.row)">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单位" min-width="130">
            <template #default="scope">
              <el-select v-model="scope.row.unitName" placeholder="选择单位" filterable style="width: 100%" @change="handleUnitChange(scope.row)">
                <el-option v-for="u in getUnitOptions(scope.row.productId)" :key="u.unitName" :label="`${u.unitName}(×${u.convertRatio})`" :value="u.unitName" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="110">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" controls-position="right" :min="0.01" :step="1" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="单价 & 价格策略" width="180">
            <template #default="scope">
              <el-input-number v-model="scope.row.price" controls-position="right" :min="0" :precision="2" style="width: 100%" @change="calculateItemAmount(scope.row)" />
              <el-tag v-if="scope.row.priceSourceLabel" size="small" type="success" style="margin-top: 4px">{{ scope.row.priceSourceLabel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="折扣%" width="124">
            <template #default="scope">
              <el-input-number v-model="scope.row.discountRate" controls-position="right" :min="0" :max="100" :precision="2" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="税率%" width="124">
            <template #default="scope">
              <el-input-number v-model="scope.row.taxRate" controls-position="right" :min="0" :max="100" :precision="2" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="税额" width="85" align="right">
            <template #default="scope">{{ Number(scope.row.taxAmount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="含税金额" width="100" align="right">
            <template #default="scope"><strong>{{ Number(scope.row.amount || 0).toFixed(2) }}</strong></template>
          </el-table-column>
          <el-table-column label="操作" width="65" align="center">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="handleRemoveItem(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分批下推出库弹窗 -->
    <el-dialog title="销货订单分批下推出库" v-model="pushOpen" width="760px" append-to-body>
      <div style="margin-bottom: 10px; color: #555">
        单据号：<strong>{{ pushOrder.orderNo }}</strong> &nbsp;|&nbsp;
        客户：<strong>{{ getCustomerName(pushOrder.customerId) }}</strong>
      </div>
      <el-table :data="pushRows" border>
        <el-table-column label="商品" min-width="180">
          <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
        </el-table-column>
        <el-table-column label="单位" prop="unitName" width="80" align="center" />
        <el-table-column label="订购数量" prop="quantity" width="95" align="right" />
        <el-table-column label="已下推" prop="shippedQty" width="95" align="right" />
        <el-table-column label="剩余可发" prop="remainingQty" width="95" align="right" />
        <el-table-column label="本次发货数量" width="150" align="center">
          <template #default="scope">
            <el-input-number v-model="scope.row.pushQty" :min="0" :max="scope.row.remainingQty" :step="1" controls-position="right" style="width: 100%" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="submitPartialPush">确认生成草稿出库单</el-button>
        <el-button @click="pushOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 只读单据详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="`${pageTitle}详情 - ${detailDoc.orderNo || ''}`" size="760px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="单据号">{{ detailDoc.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="业务状态"><dict-tag :options="bizStatusOptions" :value="detailDoc.bizStatus" /></el-descriptions-item>
        <el-descriptions-item label="客户">{{ getCustomerName(detailDoc.customerId) }}</el-descriptions-item>
        <el-descriptions-item label="发货仓库">{{ getWarehouseName(detailDoc.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="订单总额">￥{{ Number(detailDoc.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.orderItemList || []" border size="small">
        <el-table-column label="商品" min-width="170"><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="单位" prop="unitName" width="75" align="center" />
        <el-table-column label="订购量" prop="quantity" width="80" align="right" />
        <el-table-column label="已发货" prop="shippedQty" width="80" align="right" />
        <el-table-column label="单价" prop="price" width="85" align="right" />
        <el-table-column label="含税金额" prop="amount" width="95" align="right" />
      </el-table>
    </el-drawer>

    <business-trace-dialog ref="businessTraceRef" />
  </div>
</template>

<script setup name="SaleOrder">
import { useRoute } from "vue-router"
import {
  listSaleOrder, getSaleOrder, addSaleOrder, updateSaleOrder,
  approveSaleOrder, unapproveSaleOrder, closeSaleOrder,
  pushQuoteToSale, pushSaleOrderToOutbound, printSaleOrder, delSaleOrder
} from "@/api/business/saleOrder"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const route = useRoute()
const orderType = computed(() => route.query.orderType || (route.path.includes("saleQuote") ? "quote" : "sale"))
const isQuotePage = computed(() => orderType.value === "quote")
const pageTitle = computed(() => isQuotePage.value ? "销售报价单" : "销售订单")

const { proxy } = getCurrentInstance()
const {
  customerOptions, warehouseOptions, productOptions,
  activeCustomerOptions, activeWarehouseOptions, activeProductOptions,
  loadCustomers, loadWarehouses, loadProducts,
  fetchProductUnits, resolveLiveSalePrice,
  getCustomerName, getWarehouseName, getProductName
} = useBusinessOptions()

const detailOpen = ref(false)
const detailDoc = ref({})
function handleViewDetail(row) {
  getSaleOrder(row.orderId).then(res => {
    detailDoc.value = res.data || row
    detailOpen.value = true
  })
}

loadCustomers()
loadWarehouses()
loadProducts()

const unitCache = reactive({})
const saleOrderList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")
const businessTraceRef = ref(null)

const pushOpen = ref(false)
const pushOrder = ref({})
const pushRows = ref([])

const bizStatusOptions = [
  { label: "草稿", value: "draft" },
  { label: "已审批", value: "approved" },
  { label: "执行中", value: "executing" },
  { label: "已完成", value: "completed" },
  { label: "已关闭", value: "closed" }
]

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, orderNo: undefined, orderType: undefined, customerId: undefined, bizStatus: undefined },
  rules: {
    customerId: [{ required: true, message: "客户不能为空", trigger: "change" }],
    warehouseId: [{ required: true, message: "仓库不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const summaryTotals = computed(() => {
  const items = form.value.orderItemList || []
  let gross = 0
  let taxTotal = 0
  items.forEach(it => {
    gross += Number(it.amount || 0)
    taxTotal += Number(it.taxAmount || 0)
  })
  const disc = Number(form.value.discountAmount || 0)
  return { taxTotal, netTotal: Math.max(0, gross - disc) }
})

function calcProgress(row) {
  if (row.bizStatus === "completed") return 100
  const totalQty = Number(row.totalQty || 0)
  const shippedQty = Number(row.shippedTotalQty || 0)
  if (totalQty <= 0) return 0
  return Math.min(100, Math.round((shippedQty / totalQty) * 100))
}

function getUnitOptions(productId) {
  return unitCache[productId] || []
}

async function ensureUnitsLoaded(productId) {
  if (!productId) return []
  if (unitCache[productId]) return unitCache[productId]
  const prod = productOptions.value.find(p => Number(p.productId) === Number(productId))
  const list = await fetchProductUnits(productId)
  const baseUnitName = (prod && (prod.baseUnit || prod.unitName)) || "件"
  const merged = list.length ? list : [{ unitName: baseUnitName, convertRatio: 1, salePrice: prod ? prod.salePrice : 0 }]
  unitCache[productId] = merged
  return merged
}

async function applyLivePrice(row) {
  if (!row.productId) return
  const resolved = await resolveLiveSalePrice(form.value.customerId, row.productId, row.unitName)
  if (resolved && resolved.price !== undefined && resolved.price !== null) {
    row.price = Number(resolved.price)
    row.priceSourceLabel = resolved.priceSourceLabel || resolved.priceSource || "自动取价"
    if (resolved.convertRatio) {
      row.convertRatio = Number(resolved.convertRatio)
    }
  } else {
    const prod = productOptions.value.find(p => Number(p.productId) === Number(row.productId))
    if (prod) row.price = Number(prod.salePrice || 0)
    row.priceSourceLabel = "商品标准售价"
  }
  calculateItemAmount(row)
}

async function handleCustomerChanged() {
  for (const row of form.value.orderItemList || []) {
    if (row.productId) {
      await applyLivePrice(row)
    }
  }
}

async function handleProductSelect(row) {
  const prod = productOptions.value.find(p => Number(p.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) {
    const preferred = (prod && prod.saleUnit && units.find(u => u.unitName === prod.saleUnit)) || units[0]
    row.unitName = preferred.unitName
    row.convertRatio = Number(preferred.convertRatio || 1)
  } else {
    row.convertRatio = 1
  }
  await applyLivePrice(row)
}

async function handleUnitChange(row) {
  const units = getUnitOptions(row.productId)
  const matched = units.find(u => u.unitName === row.unitName)
  row.convertRatio = matched ? Number(matched.convertRatio || 1) : 1
  await applyLivePrice(row)
}

function calculateItemAmount(row) {
  const qty = Number(row.quantity || 0)
  const price = Number(row.price || 0)
  const ratio = Number(row.convertRatio || 1)
  const discountRate = row.discountRate !== undefined && row.discountRate !== null ? Number(row.discountRate) : 100
  const taxRate = Number(row.taxRate || 0)
  const net = Number((qty * price * (discountRate / 100)).toFixed(2))
  const tax = Number((net * (taxRate / 100)).toFixed(2))
  row.baseQuantity = Number((qty * ratio).toFixed(4))
  row.taxAmount = tax
  row.amount = Number((net + tax).toFixed(2))
}

function getList() {
  loading.value = true
  queryParams.value.orderType = orderType.value
  listSaleOrder(queryParams.value).then(res => {
    saleOrderList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    orderId: undefined,
    orderNo: undefined,
    orderType: orderType.value,
    customerId: undefined,
    warehouseId: undefined,
    businessDate: undefined,
    expectedDate: undefined,
    discountAmount: 0,
    bizStatus: "draft",
    remark: undefined,
    orderItemList: []
  }
  proxy.resetForm("saleOrderRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.orderId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(item => !canEditRow(item))
}

function canEditRow(r) { return !!r && r.bizStatus === "draft" }
function canApproveRow(r) { return !!r && r.bizStatus === "draft" }
function canUnapproveRow(r) { return !!r && r.bizStatus === "approved" }
function canPushRow(r) { return !!r && (r.bizStatus === "approved" || r.bizStatus === "executing") }
function canCloseRow(r) { return !!r && (r.bizStatus === "approved" || r.bizStatus === "executing") }

function handleAdd() {
  reset()
  handleAddItem()
  open.value = true
  title.value = "新增" + pageTitle.value
}

function handleUpdate(row) {
  reset()
  const orderId = row.orderId || selectedIds.value
  getSaleOrder(orderId).then(async res => {
    form.value = res.data
    form.value.discountAmount = Number(form.value.discountAmount || 0)
    form.value.orderItemList = (form.value.orderItemList || []).map(it => ({
      ...it,
      discountRate: it.discountRate != null ? Number(it.discountRate) : 100,
      taxRate: Number(it.taxRate || 0),
      taxAmount: Number(it.taxAmount || 0),
      convertRatio: Number(it.convertRatio || 1)
    }))
    for (const it of form.value.orderItemList) {
      await ensureUnitsLoaded(it.productId)
    }
    open.value = true
    title.value = "修改" + pageTitle.value
  })
}

function handleApprove(row) {
  const msg = isQuotePage.value
    ? `确认审批当前报价单"${row.orderNo}"？`
    : `确认审批当前销货订单"${row.orderNo}"？（将按库存策略自动锁库）`
  proxy.$modal.confirm(msg).then(() => approveSaleOrder(row.orderId)).then(() => {
    proxy.$modal.msgSuccess("审批成功")
    getList()
  }).catch(() => {})
}

function handleUnapprove(row) {
  const msg = isQuotePage.value
    ? `确认反审批当前报价单"${row.orderNo}"？`
    : `确认反审批当前销货订单"${row.orderNo}"？（将自动释放该订单锁定库存）`
  proxy.$modal.confirm(msg).then(() => unapproveSaleOrder(row.orderId)).then(() => {
    proxy.$modal.msgSuccess("反审批成功")
    getList()
  }).catch(() => {})
}

function handleClose(row) {
  proxy.$modal.prompt("请输入强制关闭原因（关闭后自动释放剩余未发货锁库）", "强制关闭销售订单", {
    confirmButtonText: "确认关闭",
    cancelButtonText: "取消",
    inputValue: "客户取消余单"
  }).then(({ value }) => closeSaleOrder(row.orderId, value)).then(() => {
    proxy.$modal.msgSuccess("销售单据已关闭")
    getList()
  }).catch(() => {})
}

function handlePushSale(row) {
  proxy.$modal.confirm("确认将该报价单下推为销货订单？").then(() => pushQuoteToSale(row.orderId)).then(() => {
    proxy.$modal.msgSuccess("下推销货订单成功")
    getList()
  }).catch(() => {})
}

function openPartialPush(row) {
  getSaleOrder(row.orderId).then(res => {
    const order = res.data || {}
    pushOrder.value = order
    pushRows.value = (order.orderItemList || []).map(it => {
      const ordered = Number(it.quantity || 0)
      const shipped = Number(it.shippedQty || 0)
      const remaining = Math.max(0, Number((ordered - shipped).toFixed(4)))
      return {
        orderItemId: it.orderItemId,
        productId: it.productId,
        unitName: it.unitName || "件",
        quantity: ordered,
        shippedQty: shipped,
        remainingQty: remaining,
        pushQty: remaining
      }
    }).filter(r => r.remainingQty > 0)
    if (!pushRows.value.length) {
      proxy.$modal.msgWarning("该销货订单所有明细已全部下推")
      return
    }
    pushOpen.value = true
  })
}

function submitPartialPush() {
  const payload = pushRows.value
    .filter(r => Number(r.pushQty || 0) > 0)
    .map(r => ({ orderItemId: r.orderItemId, productId: r.productId, pushQty: r.pushQty }))
  if (!payload.length) {
    proxy.$modal.msgError("请至少填写一行大于0的本次发货数量")
    return
  }
  pushSaleOrderToOutbound(pushOrder.value.orderId, payload).then(() => {
    proxy.$modal.msgSuccess("已生成草稿出库单，请前往出库单审核生效")
    pushOpen.value = false
    getList()
  })
}

function handleTrace(row) {
  businessTraceRef.value.open("sale_order", row.orderId)
}

function handlePrint(row) {
  printSaleOrder(row.orderId).then(async res => {
    const data = res.data || {}
    const h = data.header || row
    const dn = data.displayNames || {}
    const prodNames = dn.productNames || {}
    const items = (data.items || []).map(it => ({
      ...it,
      productLabel: prodNames[String(it.productId)] || getProductName(it.productId),
      unitName: it.unitName || "件",
      discountText: `${it.discountRate ?? 100}%`,
      taxText: `${it.taxRate ?? 0}%`
    }))
    await printBusinessDocument({
      title: pageTitle.value,
      docNo: h.orderNo,
      bizDate: proxy.parseTime(h.businessDate, "{y}-{m}-{d}"),
      counterpartyLabel: "客户",
      counterpartyName: dn.counterpartyName || getCustomerName(h.customerId),
      warehouseName: dn.warehouseName || getWarehouseName(h.warehouseId),
      displayNames: dn,
      extraMeta: [
        { label: "整单优惠", value: `￥${Number(h.discountAmount || 0).toFixed(2)}` },
        { label: "税额合计", value: `￥${Number(h.taxTotalAmount || 0).toFixed(2)}` }
      ],
      columns: [
        { label: "商品", prop: "productLabel" },
        { label: "单位", prop: "unitName", align: "center" },
        { label: "数量", prop: "quantity", align: "right", summary: "qty" },
        { label: "单价", prop: "price", align: "right" },
        { label: "折扣", prop: "discountText", align: "center" },
        { label: "税率", prop: "taxText", align: "center" },
        { label: "含税金额", prop: "amount", align: "right", summary: "amount" }
      ],
      items,
      totalQty: h.totalQty,
      totalAmount: h.totalAmount,
      remark: h.remark,
      createBy: h.createBy,
      auditBy: h.approveBy
    })
  })
}

function handleAddItem() {
  form.value.orderItemList.push({
    productId: undefined,
    unitName: "",
    convertRatio: 1,
    quantity: 1,
    price: 0,
    discountRate: 100,
    taxRate: 0,
    taxAmount: 0,
    amount: 0,
    priceSourceLabel: "",
    remark: undefined
  })
}

function handleRemoveItem(index) {
  form.value.orderItemList.splice(index, 1)
}

function submitForm() {
  proxy.$refs["saleOrderRef"].validate(valid => {
    if (!valid) return
    form.value.orderType = orderType.value
    if (!form.value.orderItemList || !form.value.orderItemList.length) {
      proxy.$modal.msgError("请至少录入一条明细")
      return
    }
    form.value.orderItemList.forEach(calculateItemAmount)
    const req = form.value.orderId ? updateSaleOrder(form.value) : addSaleOrder(form.value)
    req.then(() => {
      proxy.$modal.msgSuccess(form.value.orderId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleRowCommand(cmd, row) {
  if (cmd === "unapprove") handleUnapprove(row)
  else if (cmd === "close") handleClose(row)
  else if (cmd === "trace") handleTrace(row)
  else if (cmd === "print") handlePrint(row)
  else if (cmd === "delete") handleDelete(row)
}

function handleDelete(row) {
  const ids = row.orderId || selectedIds.value
  proxy.$modal.confirm(`是否确认删除当前${pageTitle.value}？`).then(() => delSaleOrder(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()
  }).catch(() => {})
}

watch(orderType, () => {
  reset()
  getList()
})

getList()

function handleExport() {
  proxy.download("business/saleOrder/export", { ...queryParams.value }, `销售订单_${new Date().getTime()}.xlsx`)
}
</script>
