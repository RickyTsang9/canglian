<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单据号" prop="purchaseOrderNo">
        <el-input v-model="queryParams.purchaseOrderNo" placeholder="请输入单据号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="queryParams.supplierId" placeholder="请选择供应商" clearable filterable style="width: 220px">
          <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:purOrder:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:purOrder:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:purOrder:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="purOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="单据号" align="center" prop="purchaseOrderNo" min-width="145" />
      <el-table-column label="供应商" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">{{ getSupplierName(scope.row.supplierId) }}</template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="业务日期" align="center" prop="businessDate" width="110">
        <template #default="scope">{{ parseTime(scope.row.businessDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="履约进度" align="center" width="145">
        <template #default="scope">
          <el-progress :percentage="calcProgress(scope.row)" :status="calcProgress(scope.row) >= 100 ? 'success' : ''" :stroke-width="14" />
        </template>
      </el-table-column>
      <el-table-column label="税额" align="center" prop="taxTotalAmount" width="90" />
      <el-table-column label="整单优惠" align="center" prop="discountAmount" width="90" />
      <el-table-column label="订单总额" align="center" prop="totalAmount" width="110" />
      <el-table-column label="业务状态" align="center" prop="bizStatus" width="95">
        <template #default="scope">
          <dict-tag :options="bizStatusOptions" :value="scope.row.bizStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="380" align="center">
        <template #default="scope">
          <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:purOrder:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" :disabled="!canEditRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:purOrder:remove']">删除</el-button>
          <el-button link type="success" icon="CircleCheck" v-if="canApproveRow(scope.row)" @click="handleApprove(scope.row)" v-hasPermi="['business:purOrder:approve']">审批</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="canUnapproveRow(scope.row)" @click="handleUnapprove(scope.row)" v-hasPermi="['business:purOrder:close']">反审批</el-button>
          <el-button link type="primary" icon="Right" v-if="canPushRow(scope.row)" @click="openPartialPush(scope.row)" v-hasPermi="['business:purOrder:pushInbound']">分批下推</el-button>
          <el-button link type="danger" icon="CircleClose" v-if="canCloseRow(scope.row)" @click="handleClose(scope.row)" v-hasPermi="['business:purOrder:close']">关闭</el-button>
          <el-button link type="primary" icon="Connection" @click="handleTrace(scope.row)" v-hasPermi="['business:trace:query']">链路</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)" v-hasPermi="['business:purOrder:print']">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改采购订单对话框 -->
    <el-dialog :title="title" v-model="open" width="1140px" append-to-body>
      <el-form ref="purOrderRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="单据号" prop="purchaseOrderNo">
              <el-input v-model="form.purchaseOrderNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable style="width: 100%">
                <el-option v-for="s in activeSupplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入库仓库" prop="warehouseId">
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

        <el-divider content-position="left">采购明细（支持多单位、行折扣与税率）</el-divider>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px">
          <el-button type="primary" plain icon="Plus" @click="handleAddItem">新增明细</el-button>
          <div style="font-size: 13px; color: #333">
            税额合计：<strong>￥{{ summaryTotals.taxTotal.toFixed(2) }}</strong> &nbsp;|&nbsp;
            整单优惠：<strong>￥{{ Number(form.discountAmount || 0).toFixed(2) }}</strong> &nbsp;|&nbsp;
            应付合计：<strong style="color: #409eff; font-size: 15px">￥{{ summaryTotals.netTotal.toFixed(2) }}</strong>
          </div>
        </div>
        <el-table :data="form.orderItemList" border>
          <el-table-column label="商品" min-width="200">
            <template #default="scope">
              <el-select v-model="scope.row.productId" placeholder="请选择商品" filterable style="width: 100%" @change="handleProductSelect(scope.row)">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单位" width="120">
            <template #default="scope">
              <el-select v-model="scope.row.unitName" placeholder="选择单位" filterable style="width: 100%" @change="handleUnitChange(scope.row)">
                <el-option v-for="u in getUnitOptions(scope.row.productId)" :key="u.unitName" :label="`${u.unitName}(×${u.convertRatio})`" :value="u.unitName" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="115">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" controls-position="right" :min="0.01" :step="1" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="115">
            <template #default="scope">
              <el-input-number v-model="scope.row.price" controls-position="right" :min="0" :precision="2" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="折扣%" width="95">
            <template #default="scope">
              <el-input-number v-model="scope.row.discountRate" controls-position="right" :min="0" :max="100" :precision="2" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="税率%" width="95">
            <template #default="scope">
              <el-input-number v-model="scope.row.taxRate" controls-position="right" :min="0" :max="100" :precision="2" style="width: 100%" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="税额" width="90" align="right">
            <template #default="scope">{{ Number(scope.row.taxAmount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="含税金额" width="105" align="right">
            <template #default="scope"><strong>{{ Number(scope.row.amount || 0).toFixed(2) }}</strong></template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
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

    <!-- 分批下推入库弹窗 -->
    <el-dialog title="采购订单分批下推入库" v-model="pushOpen" width="760px" append-to-body>
      <div style="margin-bottom: 10px; color: #555">
        单据号：<strong>{{ pushOrder.purchaseOrderNo }}</strong> &nbsp;|&nbsp;
        供应商：<strong>{{ getSupplierName(pushOrder.supplierId) }}</strong>
      </div>
      <el-table :data="pushRows" border>
        <el-table-column label="商品" min-width="180">
          <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
        </el-table-column>
        <el-table-column label="单位" prop="unitName" width="80" align="center" />
        <el-table-column label="订购数量" prop="quantity" width="95" align="right" />
        <el-table-column label="已下推" prop="receivedQty" width="95" align="right" />
        <el-table-column label="剩余可推" prop="remainingQty" width="95" align="right" />
        <el-table-column label="本次下推数量" width="150" align="center">
          <template #default="scope">
            <el-input-number v-model="scope.row.pushQty" :min="0" :max="scope.row.remainingQty" :step="1" controls-position="right" style="width: 100%" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="submitPartialPush">确认生成草稿入库单</el-button>
        <el-button @click="pushOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 只读单据详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="`购货订单详情 - ${detailDoc.purchaseOrderNo || ''}`" size="760px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="单据号">{{ detailDoc.purchaseOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="业务状态"><dict-tag :options="bizStatusOptions" :value="detailDoc.bizStatus" /></el-descriptions-item>
        <el-descriptions-item label="供应商">{{ getSupplierName(detailDoc.supplierId) }}</el-descriptions-item>
        <el-descriptions-item label="入库仓库">{{ getWarehouseName(detailDoc.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="订单总额">￥{{ Number(detailDoc.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.orderItemList || []" border size="small">
        <el-table-column label="商品" min-width="170"><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="单位" prop="unitName" width="75" align="center" />
        <el-table-column label="订购量" prop="quantity" width="80" align="right" />
        <el-table-column label="已入库" prop="receivedQty" width="80" align="right" />
        <el-table-column label="单价" prop="price" width="85" align="right" />
        <el-table-column label="含税金额" prop="amount" width="95" align="right" />
      </el-table>
    </el-drawer>

    <business-trace-dialog ref="businessTraceRef" />
  </div>
</template>

<script setup name="PurOrder">
import {
  listPurOrder, getPurOrder, addPurOrder, updatePurOrder,
  approvePurOrder, unapprovePurOrder, closePurOrder,
  pushPurOrderToInbound, printPurOrder, delPurOrder
} from "@/api/business/purOrder"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  supplierOptions, warehouseOptions, productOptions,
  activeSupplierOptions, activeWarehouseOptions, activeProductOptions,
  loadSuppliers, loadWarehouses, loadProducts, fetchProductUnits,
  getSupplierName, getWarehouseName, getProductName
} = useBusinessOptions()

const detailOpen = ref(false)
const detailDoc = ref({})
function handleViewDetail(row) {
  getPurOrder(row.purchaseOrderId).then(res => {
    detailDoc.value = res.data || row
    detailOpen.value = true
  })
}

loadSuppliers()
loadWarehouses()
loadProducts()

const unitCache = reactive({})
const purOrderList = ref([])
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
  queryParams: { pageNum: 1, pageSize: 10, purchaseOrderNo: undefined, supplierId: undefined, bizStatus: undefined },
  rules: {
    supplierId: [{ required: true, message: "供应商不能为空", trigger: "change" }],
    warehouseId: [{ required: true, message: "入库仓库不能为空", trigger: "change" }]
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
  const receivedQty = Number(row.receivedTotalQty || 0)
  if (totalQty <= 0) return 0
  return Math.min(100, Math.round((receivedQty / totalQty) * 100))
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
  const merged = list.length ? list : [{ unitName: baseUnitName, convertRatio: 1, purchasePrice: prod ? prod.costPrice : 0 }]
  unitCache[productId] = merged
  return merged
}

async function handleProductSelect(row) {
  const prod = productOptions.value.find(p => Number(p.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) {
    row.unitName = units[0].unitName
    row.convertRatio = Number(units[0].convertRatio || 1)
    if (units[0].purchasePrice != null && Number(units[0].purchasePrice) > 0) {
      row.price = Number(units[0].purchasePrice)
    } else if (prod) {
      row.price = Number(prod.costPrice || 0)
    }
  } else if (prod) {
    row.unitName = prod.unitName || "件"
    row.convertRatio = 1
    row.price = Number(prod.costPrice || 0)
  }
  calculateItemAmount(row)
}

function handleUnitChange(row) {
  const prevRatio = Number(row.convertRatio || 1)
  const units = getUnitOptions(row.productId)
  const matched = units.find(u => u.unitName === row.unitName)
  const newRatio = matched ? Number(matched.convertRatio || 1) : 1
  row.convertRatio = newRatio
  if (matched && matched.purchasePrice != null && Number(matched.purchasePrice) > 0) {
    row.price = Number(matched.purchasePrice)
  } else if (prevRatio > 0 && newRatio > 0 && Number(row.price || 0) > 0) {
    row.price = Number((Number(row.price) * newRatio / prevRatio).toFixed(2))
  }
  calculateItemAmount(row)
}

function calculateItemAmount(row) {
  const qty = Number(row.quantity || 0)
  const price = Number(row.price || 0)
  const ratio = Number(row.convertRatio || 1)
  const discountRate = row.discountRate !== undefined && row.discountRate !== null ? Number(row.discountRate) : 100
  const taxRate = Number(row.taxRate || 0)
  const discounted = qty * price * (discountRate / 100)
  const tax = discounted * (taxRate / 100)
  row.baseQuantity = Number((qty * ratio).toFixed(4))
  row.taxAmount = Number(tax.toFixed(2))
  row.amount = Number((discounted + tax).toFixed(2))
}

function getList() {
  loading.value = true
  listPurOrder(queryParams.value).then(res => {
    purOrderList.value = res.rows || []
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
    purchaseOrderId: undefined,
    purchaseOrderNo: undefined,
    supplierId: undefined,
    warehouseId: undefined,
    businessDate: undefined,
    expectedDate: undefined,
    discountAmount: 0,
    bizStatus: "draft",
    remark: undefined,
    orderItemList: []
  }
  proxy.resetForm("purOrderRef")
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
  selectedIds.value = selection.map(item => item.purchaseOrderId)
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
  title.value = "新增购货订单"
}

function handleUpdate(row) {
  reset()
  const id = row.purchaseOrderId || selectedIds.value
  getPurOrder(id).then(async res => {
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
    title.value = "修改购货订单"
  })
}

function handleApprove(row) {
  proxy.$modal.confirm("确认审批当前购货订单？").then(() => approvePurOrder(row.purchaseOrderId)).then(() => {
    proxy.$modal.msgSuccess("审批成功")
    getList()
  }).catch(() => {})
}

function handleUnapprove(row) {
  proxy.$modal.confirm("确认反审批当前购货订单并退回草稿？").then(() => unapprovePurOrder(row.purchaseOrderId)).then(() => {
    proxy.$modal.msgSuccess("反审批成功")
    getList()
  }).catch(() => {})
}

function handleClose(row) {
  proxy.$modal.prompt("请输入强制关闭原因（关闭后剩余未入库数量将不再下推）", "强制关闭采购订单", {
    confirmButtonText: "确认关闭",
    cancelButtonText: "取消",
    inputValue: "供应商取消余单"
  }).then(({ value }) => closePurOrder(row.purchaseOrderId, value)).then(() => {
    proxy.$modal.msgSuccess("采购订单已关闭")
    getList()
  }).catch(() => {})
}

function openPartialPush(row) {
  getPurOrder(row.purchaseOrderId).then(res => {
    const order = res.data || {}
    pushOrder.value = order
    pushRows.value = (order.orderItemList || []).map(it => {
      const ordered = Number(it.quantity || 0)
      const received = Number(it.receivedQty || 0)
      const remaining = Math.max(0, Number((ordered - received).toFixed(4)))
      return {
        purchaseOrderItemId: it.purchaseOrderItemId,
        productId: it.productId,
        unitName: it.unitName || "件",
        quantity: ordered,
        receivedQty: received,
        remainingQty: remaining,
        pushQty: remaining
      }
    }).filter(r => r.remainingQty > 0)
    if (!pushRows.value.length) {
      proxy.$modal.msgWarning("该采购订单所有明细已全部下推")
      return
    }
    pushOpen.value = true
  })
}

function submitPartialPush() {
  const payload = pushRows.value
    .filter(r => Number(r.pushQty || 0) > 0)
    .map(r => ({ purchaseOrderItemId: r.purchaseOrderItemId, productId: r.productId, pushQty: r.pushQty }))
  if (!payload.length) {
    proxy.$modal.msgError("请至少填写一行大于0的本次下推数量")
    return
  }
  pushPurOrderToInbound(pushOrder.value.purchaseOrderId, payload).then(() => {
    proxy.$modal.msgSuccess("已生成草稿入库单，请前往入库单审核生效")
    pushOpen.value = false
    getList()
  })
}

function handleTrace(row) {
  businessTraceRef.value.open("pur_order", row.purchaseOrderId)
}

function handlePrint(row) {
  printPurOrder(row.purchaseOrderId).then(async res => {
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
      title: "购货订单",
      docNo: h.purchaseOrderNo,
      bizDate: proxy.parseTime(h.businessDate, "{y}-{m}-{d}"),
      counterpartyLabel: "供应商",
      counterpartyName: dn.counterpartyName || getSupplierName(h.supplierId),
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
    remark: undefined
  })
}

function handleRemoveItem(index) {
  form.value.orderItemList.splice(index, 1)
}

function submitForm() {
  proxy.$refs["purOrderRef"].validate(valid => {
    if (!valid) return
    if (!form.value.orderItemList || !form.value.orderItemList.length) {
      proxy.$modal.msgError("请至少录入一条明细")
      return
    }
    form.value.orderItemList.forEach(calculateItemAmount)
    const req = form.value.purchaseOrderId ? updatePurOrder(form.value) : addPurOrder(form.value)
    req.then(() => {
      proxy.$modal.msgSuccess(form.value.purchaseOrderId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const ids = row.purchaseOrderId || selectedIds.value
  proxy.$modal.confirm(`是否确认删除购货订单？`).then(() => delPurOrder(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()
  }).catch(() => {})
}

getList()
</script>
