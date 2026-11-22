<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="入库单号" prop="inboundNo">
        <el-input v-model="queryParams.inboundNo" placeholder="请输入入库单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="入库类型" prop="inboundType">
        <el-select v-model="queryParams.inboundType" placeholder="全部类型" clearable style="width: 150px">
          <el-option label="采购入库" value="purchase" />
          <el-option label="采购订单入库" value="purchase_order" />
          <el-option label="其他入库" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="入库状态" clearable style="width: 140px">
          <el-option v-for="dict in docStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:inbound:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:inbound:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:inbound:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="inboundList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="入库单号" align="center" min-width="175">
        <template #default="scope">
          <span>{{ scope.row.inboundNo }}</span>
          <el-tag v-if="scope.row.isReversal === '1'" type="danger" size="small" style="margin-left: 4px">红冲单</el-tag>
          <el-tag v-if="scope.row.reversedFlag === '1'" type="warning" size="small" style="margin-left: 4px">已红冲</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入库类型" align="center" width="115">
        <template #default="scope">
          <el-tag size="small" type="info">{{ inboundTypeLabel(scope.row.inboundType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="供应商" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">{{ getSupplierName(scope.row.supplierId) }}</template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="业务日期" align="center" width="110">
        <template #default="scope">{{ parseTime(scope.row.businessDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="总数量" align="right" prop="totalQty" width="85" />
      <el-table-column label="总金额" align="right" prop="totalAmount" width="105" />
      <el-table-column label="状态" align="center" prop="status" width="85">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="380" align="center">
        <template #default="scope">
          <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:inbound:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" :disabled="!canDeleteRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:inbound:remove']">删除</el-button>
          <el-button link type="success" icon="CircleCheck" v-if="canAuditRow(scope.row)" @click="handleAudit(scope.row)" v-hasPermi="['business:inbound:audit']">审核</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="canUnauditRow(scope.row)" @click="handleUnaudit(scope.row)" v-hasPermi="['business:inbound:unaudit']">反审核</el-button>
          <el-button link type="danger" icon="DocumentDelete" v-if="canReverseRow(scope.row)" @click="handleReverse(scope.row)" v-hasPermi="['business:inbound:reverse']">红冲</el-button>
          <el-button link type="primary" icon="Connection" @click="handleTrace(scope.row)" v-hasPermi="['business:trace:query']">链路</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)" v-hasPermi="['business:inbound:print']">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="1240px" append-to-body>
      <el-form ref="inboundRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="入库单号" prop="inboundNo">
              <el-input v-model="form.inboundNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入库类型" prop="inboundType">
              <el-select v-model="form.inboundType" placeholder="请选择入库类型" style="width: 100%">
                <el-option label="采购入库" value="purchase" />
                <el-option label="采购订单入库" value="purchase_order" />
                <el-option label="其他入库" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" clearable filterable style="width: 100%">
                <el-option v-for="s in activeSupplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入库仓库" prop="warehouseId">
              <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%" @change="handleWarehouseChange">
                <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合计">
              <span>数量：<strong>{{ form.totalQty }}</strong> &nbsp;|&nbsp; 金额：<strong>￥{{ Number(form.totalAmount || 0).toFixed(2) }}</strong></span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入内容" />
        </el-form-item>

        <el-divider content-position="left">入库明细（启用序列号商品必须录入足额唯一 SN 码，逗号/空格/换行分隔）</el-divider>
        <div class="mb8">
          <el-button type="primary" plain size="small" icon="Plus" @click="handleAddItem">新增明细</el-button>
        </div>
        <el-table :data="form.itemList" border size="small">
          <el-table-column label="商品" min-width="170">
            <template #default="scope">
              <el-select v-model="scope.row.productId" placeholder="选择商品" filterable style="width: 100%" @change="handleProductChange(scope.row)">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}${p.enableSerial === '1' ? ' [SN]' : ''}`" :value="p.productId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="库位" width="125">
            <template #default="scope">
              <el-select v-model="scope.row.locationId" placeholder="默认库位" filterable style="width: 100%">
                <el-option label="默认库位" :value="0" />
                <el-option v-for="l in warehouseLocations" :key="l.locationId" :label="`${l.locationCode} - ${l.locationName}`" :value="l.locationId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单位" width="100">
            <template #default="scope">
              <el-select v-model="scope.row.unitName" placeholder="选择单位" filterable style="width: 100%" @change="handleUnitChange(scope.row)">
                <el-option v-for="u in getUnitOptions(scope.row.productId)" :key="u.unitName" :label="`${u.unitName}(×${u.convertRatio})`" :value="u.unitName" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="批次号" width="100">
            <template #default="scope">
              <el-input v-model="scope.row.batchNo" placeholder="批次号" />
            </template>
          </el-table-column>
          <el-table-column label="生产/有效期" width="145">
            <template #default="scope">
              <el-date-picker v-model="scope.row.productionDate" type="date" value-format="YYYY-MM-DD" placeholder="生产日期" size="small" style="width: 100%; margin-bottom: 4px" />
              <el-date-picker v-model="scope.row.expiryDate" type="date" value-format="YYYY-MM-DD" placeholder="有效期(空自动推算)" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="80">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="0.01" :precision="2" :controls="false" style="width: 100%" @change="recalcRow(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="85">
            <template #default="scope">
              <el-input-number v-model="scope.row.price" :min="0" :precision="2" :controls="false" style="width: 100%" @change="recalcRow(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="金额" width="80" align="right">
            <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="序列号(SN)" min-width="180">
            <template #default="scope">
              <template v-if="isProductSerialEnabled(scope.row.productId)">
                <el-input
                  v-model="scope.row.snCodes"
                  type="textarea"
                  :rows="2"
                  :placeholder="`必填：需录 ${requiredSnCount(scope.row)} 个唯一SN（逗号/换行分隔）`"
                />
                <div style="font-size: 11px; color: #e6a23c; margin-top: 2px">
                  已录 {{ countEnteredSns(scope.row.snCodes) }} / 需 {{ requiredSnCount(scope.row) }} 个
                </div>
              </template>
              <span v-else style="color: #909399; font-size: 12px">未启用SN</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="55" align="center">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="handleRemoveItem(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 只读单据详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="`入库单详情 - ${detailDoc.inboundNo || ''}`" size="820px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="入库单号">{{ detailDoc.inboundNo }}</el-descriptions-item>
        <el-descriptions-item label="入库类型">{{ inboundTypeLabel(detailDoc.inboundType) }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ getSupplierName(detailDoc.supplierId) }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ getWarehouseName(detailDoc.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ Number(detailDoc.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusLabel(detailDoc.status) }}</el-descriptions-item>
        <el-descriptions-item label="审核人/时间">{{ detailDoc.auditBy || '-' }} {{ parseTime(detailDoc.auditTime) || '' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.itemList || []" border size="small">
        <el-table-column label="商品" min-width="150"><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="库位" width="110"><template #default="s">{{ getLocationName(s.row.locationId) }}</template></el-table-column>
        <el-table-column label="单位" prop="unitName" width="65" align="center" />
        <el-table-column label="批次" prop="batchNo" width="90" align="center" />
        <el-table-column label="有效期" width="100" align="center"><template #default="s">{{ parseTime(s.row.expiryDate, '{y}-{m}-{d}') || '-' }}</template></el-table-column>
        <el-table-column label="数量" prop="quantity" width="70" align="right" />
        <el-table-column label="单价" prop="price" width="75" align="right" />
        <el-table-column label="金额" prop="amount" width="85" align="right" />
        <el-table-column label="SN码" prop="snCodes" min-width="120" show-overflow-tooltip />
      </el-table>
    </el-drawer>

    <business-trace-dialog ref="businessTraceRef" />
  </div>
</template>

<script setup name="Inbound">
import { listInbound, addInbound, delInbound, getInbound, updateInbound, auditInbound, unauditInbound, reverseInbound, printInbound } from "@/api/business/inbound"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  warehouseOptions, supplierOptions, productOptions,
  activeWarehouseOptions, activeSupplierOptions, activeProductOptions,
  loadWarehouses, loadSuppliers, loadProducts, loadLocations,
  fetchLocationsByWarehouse, fetchProductUnits, isProductSerialEnabled,
  getWarehouseName, getSupplierName, getProductName, getLocationName
} = useBusinessOptions()

const INBOUND_TYPE_MAP = { purchase: "采购入库", purchase_order: "采购订单入库", other: "其他入库", reversal: "入库红冲" }
function inboundTypeLabel(v) { return INBOUND_TYPE_MAP[v] || v || "-" }

const docStatusOptions = [
  { label: "草稿", value: "0", tagType: "info" },
  { label: "已审核", value: "1", tagType: "success" },
  { label: "已完成", value: "2", tagType: "primary" }
]
function getStatusLabel(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.label : (val || "-") }
function getStatusTagType(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.tagType : "info" }

function requiredSnCount(row) {
  return Math.round(Number(row.quantity || 0) * Number(row.convertRatio || 1))
}
function countEnteredSns(raw) {
  if (!raw) return 0
  return new Set(String(raw).split(/[\s,，;；]+/).map(s => s.trim()).filter(Boolean)).size
}

const inboundList = ref([])
const warehouseLocations = ref([])
const unitCache = reactive({})
const open = ref(false)
const detailOpen = ref(false)
const detailDoc = ref({})
const businessTraceRef = ref(null)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, inboundNo: undefined, inboundType: undefined, status: undefined },
  rules: {
    inboundType: [{ required: true, message: "入库类型不能为空", trigger: "change" }],
    warehouseId: [{ required: true, message: "请选择仓库", trigger: "change" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getUnitOptions(productId) { return unitCache[productId] || [] }

async function ensureUnitsLoaded(productId) {
  if (!productId) return []
  if (unitCache[productId]) return unitCache[productId]
  const prod = productOptions.value.find(p => Number(p.productId) === Number(productId))
  const list = await fetchProductUnits(productId)
  const merged = list.length ? list : [{ unitName: (prod && (prod.baseUnit || prod.unitName)) || "件", convertRatio: 1, purchasePrice: prod ? prod.costPrice : 0 }]
  unitCache[productId] = merged
  return merged
}

function handleWarehouseChange(whId) {
  fetchLocationsByWarehouse(whId).then(rows => { warehouseLocations.value = rows })
}

function getList() {
  loading.value = true
  listInbound(queryParams.value).then(res => {
    inboundList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }

function reset() {
  form.value = {
    inboundId: undefined, inboundNo: undefined, inboundType: "purchase",
    supplierId: undefined, warehouseId: undefined, businessDate: undefined,
    totalQty: 0, totalAmount: 0, status: "0", remark: undefined, itemList: []
  }
  warehouseLocations.value = []
  proxy.resetForm("inboundRef")
}

function handleAddItem() {
  if (!Array.isArray(form.value.itemList)) form.value.itemList = []
  form.value.itemList.push({
    productId: undefined, locationId: 0, unitName: "", convertRatio: 1,
    batchNo: "", productionDate: undefined, expiryDate: undefined,
    quantity: 1, price: 0, amount: 0, snCodes: ""
  })
}

function handleRemoveItem(idx) { form.value.itemList.splice(idx, 1); recalcTotals() }

async function handleProductChange(row) {
  const prod = productOptions.value.find(p => Number(p.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) {
    row.unitName = units[0].unitName
    row.convertRatio = Number(units[0].convertRatio || 1)
    if (units[0].purchasePrice > 0) row.price = Number(units[0].purchasePrice)
    else if (prod && (!row.price || Number(row.price) === 0)) row.price = Number(prod.costPrice || 0)
  } else if (prod && (!row.price || Number(row.price) === 0)) {
    row.price = Number(prod.costPrice || 0)
  }
  recalcRow(row)
}

function handleUnitChange(row) {
  const prevRatio = Number(row.convertRatio || 1)
  const matched = getUnitOptions(row.productId).find(u => u.unitName === row.unitName)
  const newRatio = matched ? Number(matched.convertRatio || 1) : 1
  row.convertRatio = newRatio
  if (matched && matched.purchasePrice > 0) {
    row.price = Number(matched.purchasePrice)
  } else if (prevRatio > 0 && newRatio > 0 && Number(row.price || 0) > 0) {
    row.price = Number((Number(row.price) * newRatio / prevRatio).toFixed(2))
  }
  recalcRow(row)
}

function recalcRow(row) {
  const qty = Number(row.quantity || 0)
  const price = Number(row.price || 0)
  const ratio = Number(row.convertRatio || 1)
  row.baseQuantity = Number((qty * ratio).toFixed(4))
  row.amount = Number((qty * price).toFixed(2))
  recalcTotals()
}

function recalcTotals() {
  const items = form.value.itemList || []
  form.value.totalQty = Number(items.reduce((s, i) => s + Number(i.quantity || 0), 0).toFixed(2))
  form.value.totalAmount = Number(items.reduce((s, i) => s + Number(i.amount || 0), 0).toFixed(2))
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.inboundId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}

function canEditRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }
function canAuditRow(r) { return !!r && r.status === "0" }
function canUnauditRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canReverseRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }

function handleAdd() { reset(); handleAddItem(); open.value = true; title.value = "新增入库单" }

function handleViewDetail(row) {
  getInbound(row.inboundId).then(res => {
    detailDoc.value = res.data || row
    detailOpen.value = true
  })
}

function handleTrace(row) {
  businessTraceRef.value.open("inbound", row.inboundId)
}

function handleUpdate(row) {
  reset()
  getInbound(row.inboundId || selectedIds.value).then(async res => {
    form.value = res.data || {}
    if (!Array.isArray(form.value.itemList)) form.value.itemList = []
    if (form.value.warehouseId) handleWarehouseChange(form.value.warehouseId)
    for (const it of form.value.itemList) await ensureUnitsLoaded(it.productId)
    recalcTotals()
    open.value = true
    title.value = "修改入库单"
  })
}

function handleAudit(row) {
  proxy.$modal.confirm("确认审核当前入库单？（将校验序列号/效期并增加库存，同步生成应付及凭证事件）").then(() => auditInbound(row.inboundId)).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {})
}

function handleUnaudit(row) {
  proxy.$modal.confirm(`确认反审核入库单"${row.inboundNo}"？系统将校验库存余额、序列号、应付及凭证状态并回退草稿。`).then(() => unauditInbound(row.inboundId)).then(() => {
    proxy.$modal.msgSuccess("反审核成功，单据已退回草稿")
    getList()
  }).catch(() => {})
}

function handleReverse(row) {
  proxy.$modal.confirm(`确认对入库单"${row.inboundNo}"执行一键红冲？将自动生成负向红字入库单并回冲库存、成本与应付。`).then(() => reverseInbound(row.inboundId)).then(() => {
    proxy.$modal.msgSuccess("一键红冲成功")
    getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printInbound(row.inboundId).then(async res => {
    const data = res.data || {}
    const h = data.header || row
    const dn = data.displayNames || {}
    const prodNames = dn.productNames || {}
    const locNames = dn.locationNames || {}
    const items = (data.items || []).map(it => ({
      ...it,
      productLabel: prodNames[String(it.productId)] || getProductName(it.productId),
      locationLabel: locNames[String(it.locationId)] || getLocationName(it.locationId),
      unitName: it.unitName || "件"
    }))
    await printBusinessDocument({
      title: h.isReversal === "1" ? "入库红冲单" : "采购/其他入库单",
      docNo: h.inboundNo,
      bizDate: proxy.parseTime(h.businessDate || h.createTime, "{y}-{m}-{d}"),
      counterpartyLabel: "供应商",
      counterpartyName: dn.counterpartyName || getSupplierName(h.supplierId),
      warehouseName: dn.warehouseName || getWarehouseName(h.warehouseId),
      displayNames: dn,
      columns: [
        { label: "商品", prop: "productLabel" },
        { label: "库位", prop: "locationLabel", align: "center" },
        { label: "单位", prop: "unitName", align: "center" },
        { label: "批次号", prop: "batchNo", align: "center" },
        { label: "数量", prop: "quantity", align: "right", summary: "qty" },
        { label: "单价", prop: "price", align: "right" },
        { label: "金额", prop: "amount", align: "right", summary: "amount" }
      ],
      items,
      totalQty: h.totalQty,
      totalAmount: h.totalAmount,
      remark: h.remark,
      createBy: h.createBy,
      auditBy: h.auditBy
    })
  })
}

function submitForm() {
  proxy.$refs["inboundRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.inboundId ? updateInbound(form.value) : addInbound(form.value)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.inboundId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const ids = row.inboundId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中入库单？").then(() => delInbound(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()
  }).catch(() => {})
}

loadWarehouses()
loadSuppliers()
loadProducts()
loadLocations()
getList()
</script>
