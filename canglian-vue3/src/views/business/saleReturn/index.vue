<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="退货单号" prop="returnNo">
        <el-input v-model="queryParams.returnNo" placeholder="请输入退货单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="退货状态" clearable style="width: 140px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:saleReturn:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:saleReturn:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:saleReturn:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:saleReturn:export', 'business:saleReturn:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="saleReturnList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="退货单号" align="center" min-width="175">
        <template #default="scope">
          <span>{{ scope.row.returnNo }}</span>
          <el-tag v-if="scope.row.isReversal === '1'" type="danger" size="small" style="margin-left: 4px">红冲单</el-tag>
          <el-tag v-if="scope.row.reversedFlag === '1'" type="warning" size="small" style="margin-left: 4px">已红冲</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="原出库单" align="center" prop="sourceBillNo" width="145" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.sourceBillNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户" align="center" min-width="125" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="110" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="总数量" align="right" prop="totalQty" width="80" />
      <el-table-column label="总金额" align="right" prop="totalAmount" width="100">
        <template #default="scope">{{ Number(scope.row.totalAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="关联红字应收" align="center" width="108">
        <template #default="scope">
          <el-tag v-if="scope.row.receivableId" type="danger" effect="plain">已生成</el-tag>
          <span v-else style="color:#909399">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="78">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="235" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:saleReturn:edit']">修改</el-button>
            <el-button v-if="canAuditRow(scope.row)" link type="success" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:saleReturn:audit']">审核</el-button>
            <el-button v-if="canUnauditRow(scope.row)" link type="warning" icon="RefreshLeft" @click="handleUnaudit(scope.row)" v-hasPermi="['business:saleReturn:unaudit']">反审核</el-button>
            <el-dropdown trigger="click" @command="cmd => handleRowCommand(cmd, scope.row)">
              <el-button link type="primary">更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="canReverseRow(scope.row)" command="reverse">一键红冲</el-dropdown-item>
                  <el-dropdown-item command="trace">单据链路</el-dropdown-item>
                  <el-dropdown-item command="print">打印单据</el-dropdown-item>
                  <el-dropdown-item v-if="canDeleteRow(scope.row)" command="delete" divided>删除单据</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="min(1260px, 94vw)" append-to-body>
      <el-form ref="saleReturnRef" :model="form" :rules="rules" label-width="112px">
        <el-row>
          <el-col :span="6">
            <el-form-item label="退货单号" prop="returnNo">
              <el-input v-model="form.returnNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联签收出库" prop="sourceBillId">
              <el-select v-model="form.sourceBillId" placeholder="可选：关联已签收出库单自动带出明细并回溯成本与可退上限" clearable filterable style="width: 100%" @change="handleSourceOutboundChange">
                <el-option v-for="ob in sourceOutboundOptions" :key="ob.outboundId" :label="`${ob.outboundNo} | ${getCustomerName(ob.customerId)} | ￥${Number(ob.totalAmount || 0).toFixed(2)}`" :value="ob.outboundId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" clearable filterable style="width: 100%">
                <el-option v-for="c in activeCustomerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="退货仓库" prop="warehouseId">
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
        <el-divider content-position="left">退货明细（启用序列号商品必须录入足额已出库 SN 码回库）</el-divider>
        <div class="mb8">
          <el-button type="primary" plain size="small" icon="Plus" @click="addItemRow">新增明细</el-button>
        </div>
        <el-table :data="form.itemList" border size="small">
          <el-table-column label="商品" min-width="175">
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
          <el-table-column label="单位" min-width="125">
            <template #default="scope">
              <el-select v-model="scope.row.unitName" placeholder="选择单位" filterable style="width: 100%" @change="handleUnitChange(scope.row)">
                <el-option v-for="u in getUnitOptions(scope.row.productId)" :key="u.unitName" :label="`${u.unitName}(×${u.convertRatio})`" :value="u.unitName" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="批次号" width="105">
            <template #default="scope"><el-input v-model="scope.row.batchNo" placeholder="选填" /></template>
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
          <el-table-column label="金额" width="85" align="right">
            <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="序列号(SN)" min-width="180">
            <template #default="scope">
              <template v-if="isProductSerialEnabled(scope.row.productId)">
                <el-input v-model="scope.row.snCodes" type="textarea" :rows="2" :placeholder="`必填：需 ${requiredSnCount(scope.row)} 个已出库SN`" />
                <div style="font-size: 11px; color: #e6a23c">已录 {{ countEnteredSns(scope.row.snCodes) }} / 需 {{ requiredSnCount(scope.row) }}</div>
              </template>
              <span v-else style="color: #909399; font-size: 12px">未启用SN</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="55" align="center">
            <template #default="scope"><el-button link type="danger" icon="Delete" @click="removeItemRow(scope.$index)" /></template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailOpen" :title="`销售退货详情 - ${detailDoc.returnNo || ''}`" size="820px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="退货单号">{{ detailDoc.returnNo }}</el-descriptions-item>
        <el-descriptions-item label="关联原出库单">{{ detailDoc.sourceBillNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ getCustomerName(detailDoc.customerId) }}</el-descriptions-item>
        <el-descriptions-item label="退货仓库">{{ getWarehouseName(detailDoc.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate || detailDoc.createTime, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ Number(detailDoc.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.itemList || []" border size="small">
        <el-table-column label="商品" min-width="150"><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="库位" width="105"><template #default="s">{{ getLocationName(s.row.locationId) }}</template></el-table-column>
        <el-table-column label="单位" prop="unitName" width="60" align="center" />
        <el-table-column label="批次" prop="batchNo" width="85" align="center" />
        <el-table-column label="数量" prop="quantity" width="70" align="right" />
        <el-table-column label="金额" prop="amount" width="85" align="right" />
        <el-table-column label="SN码" prop="snCodes" min-width="115" show-overflow-tooltip />
      </el-table>
    </el-drawer>

    <business-trace-dialog ref="businessTraceRef" />
  </div>
</template>

<script setup name="SaleReturn">
import { listSaleReturn, addSaleReturn, delSaleReturn, getSaleReturn, updateSaleReturn, auditSaleReturn, unauditSaleReturn, reverseSaleReturn } from "@/api/business/saleReturn"
import { getOutbound } from "@/api/business/outbound"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  warehouseOptions, customerOptions, productOptions,
  activeWarehouseOptions, activeCustomerOptions, activeProductOptions,
  loadWarehouses, loadCustomers, loadProducts, loadLocations,
  fetchLocationsByWarehouse, fetchProductUnits, fetchSignedOutboundOptions, isProductSerialEnabled,
  getWarehouseName, getCustomerName, getProductName, getLocationName
} = useBusinessOptions()

const docStatusOptions = [
  { label: "草稿", value: "0", tagType: "info" },
  { label: "已审核", value: "1", tagType: "success" },
  { label: "已完成", value: "2", tagType: "primary" }
]
function getStatusLabel(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.label : (val || "-") }
function getStatusTagType(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.tagType : "info" }
function requiredSnCount(row) { return Math.round(Number(row.quantity || 0) * Number(row.convertRatio || 1)) }
function countEnteredSns(raw) { return raw ? new Set(String(raw).split(/[\s,，;；]+/).map(s => s.trim()).filter(Boolean)).size : 0 }

const saleReturnList = ref([])
const warehouseLocations = ref([])
const sourceOutboundOptions = ref([])
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
  form: { itemList: [] },
  queryParams: { pageNum: 1, pageSize: 10, returnNo: undefined, status: undefined },
  rules: { warehouseId: [{ required: true, message: "退货仓库不能为空", trigger: "change" }] }
})
const { queryParams, form, rules } = toRefs(data)

function getUnitOptions(productId) { return unitCache[productId] || [] }
async function ensureUnitsLoaded(productId) {
  if (!productId) return []
  if (unitCache[productId]) return unitCache[productId]
  const prod = productOptions.value.find(p => Number(p.productId) === Number(productId))
  const list = await fetchProductUnits(productId)
  const merged = list.length ? list : [{ unitName: (prod && (prod.baseUnit || prod.unitName)) || "件", convertRatio: 1 }]
  unitCache[productId] = merged
  return merged
}

function handleWarehouseChange(whId) { fetchLocationsByWarehouse(whId).then(r => { warehouseLocations.value = r }) }

async function handleSourceOutboundChange(outboundId) {
  if (!outboundId) {
    form.value.sourceBillType = undefined
    form.value.sourceBillNo = undefined
    return
  }
  const res = await getOutbound(outboundId)
  const ob = res.data
  if (!ob) return
  form.value.sourceBillType = "outbound"
  form.value.sourceBillNo = ob.outboundNo
  if (ob.customerId) form.value.customerId = ob.customerId
  if (ob.warehouseId) { form.value.warehouseId = ob.warehouseId; handleWarehouseChange(ob.warehouseId) }
  if (Array.isArray(ob.itemList) && ob.itemList.length) {
    form.value.itemList = ob.itemList.map(it => ({
      productId: it.productId, locationId: it.locationId ?? 0, unitName: it.unitName || "",
      convertRatio: Number(it.convertRatio || 1), batchNo: it.batchNo || "",
      quantity: Number(it.quantity || 1), price: Number(it.price || 0),
      amount: Number(it.amount || 0), snCodes: it.snCodes || ""
    }))
    for (const row of form.value.itemList) await ensureUnitsLoaded(row.productId)
    recalcTotals()
  }
}

function getList() {
  loading.value = true
  listSaleReturn(queryParams.value).then(res => {
    saleReturnList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function addItemRow() {
  if (!Array.isArray(form.value.itemList)) form.value.itemList = []
  form.value.itemList.push({ productId: undefined, unitName: "", convertRatio: 1, locationId: 0, batchNo: "", quantity: 1, price: 0, amount: 0, snCodes: "" })
}
function removeItemRow(idx) { form.value.itemList.splice(idx, 1); recalcTotals() }

async function handleProductChange(row) {
  const p = productOptions.value.find(item => Number(item.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) { row.unitName = units[0].unitName; row.convertRatio = Number(units[0].convertRatio || 1) }
  if (p && (!row.price || Number(row.price) <= 0)) row.price = Number(p.salePrice || p.costPrice || 0)
  recalcRow(row)
}
function handleUnitChange(row) {
  const prevRatio = Number(row.convertRatio || 1)
  const matched = getUnitOptions(row.productId).find(u => u.unitName === row.unitName)
  const newRatio = matched ? Number(matched.convertRatio || 1) : 1
  row.convertRatio = newRatio
  if (prevRatio > 0 && newRatio > 0 && Number(row.price || 0) > 0) row.price = Number((Number(row.price) * newRatio / prevRatio).toFixed(2))
  recalcRow(row)
}
function recalcRow(row) {
  row.baseQuantity = Number((Number(row.quantity || 0) * Number(row.convertRatio || 1)).toFixed(4))
  row.amount = Number((Number(row.quantity || 0) * Number(row.price || 0)).toFixed(2))
  recalcTotals()
}
function recalcTotals() {
  const items = form.value.itemList || []
  form.value.totalQty = Number(items.reduce((s, i) => s + Number(i.quantity || 0), 0).toFixed(2))
  form.value.totalAmount = Number(items.reduce((s, i) => s + Number(i.amount || 0), 0).toFixed(2))
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { saleReturnId: undefined, returnNo: undefined, returnType: "sale_return", sourceBillType: undefined, sourceBillId: undefined, sourceBillNo: undefined, businessDate: undefined, customerId: undefined, warehouseId: undefined, totalQty: 0, totalAmount: 0, status: "0", remark: undefined, itemList: [] }
  warehouseLocations.value = []
  proxy.resetForm("saleReturnRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.saleReturnId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}
function canEditRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }
function canAuditRow(r) { return !!r && r.status === "0" }
function canUnauditRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canReverseRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }

async function handleAdd() {
  reset()
  addItemRow()
  sourceOutboundOptions.value = await fetchSignedOutboundOptions()
  open.value = true
  title.value = "新增销售退货"
}
function handleViewDetail(row) {
  getSaleReturn(row.saleReturnId).then(res => { detailDoc.value = res.data || row; detailOpen.value = true })
}
function handleTrace(row) { businessTraceRef.value.open("sale_return", row.saleReturnId) }

function handleUpdate(row) {
  reset()
  fetchSignedOutboundOptions().then(list => { sourceOutboundOptions.value = list })
  getSaleReturn(row.saleReturnId || selectedIds.value).then(async res => {
    form.value = res.data || {}
    if (!Array.isArray(form.value.itemList)) form.value.itemList = []
    if (form.value.warehouseId) handleWarehouseChange(form.value.warehouseId)
    for (const it of form.value.itemList) await ensureUnitsLoaded(it.productId)
    open.value = true; title.value = "修改销售退货"
  })
}

function submitForm() {
  proxy.$refs["saleReturnRef"].validate(valid => {
    if (!valid) return
    const validItems = (form.value.itemList || []).filter(i => i.productId && Number(i.quantity) > 0)
    if (!validItems.length) { proxy.$modal.msgError("请至少添加一条有效退货明细"); return }
    const payload = { ...form.value, itemList: validItems }
    const fn = form.value.saleReturnId ? updateSaleReturn(payload) : addSaleReturn(payload)
    fn.then(() => { proxy.$modal.msgSuccess(form.value.saleReturnId ? "修改成功" : "新增成功"); open.value = false; getList() })
  })
}

function handleAudit(row) {
  proxy.$modal.confirm(`确认审核销售退货单"${row.returnNo}"？（将回补库存与序列号、冲减成本并生成红字应收单）`).then(() => auditSaleReturn(row.saleReturnId)).then(() => { proxy.$modal.msgSuccess("审核成功"); getList() }).catch(() => {})
}
function handleUnaudit(row) {
  proxy.$modal.confirm(`确认反审核销售退货单"${row.returnNo}"？将扣减回库库存与序列号并退回草稿。`).then(() => unauditSaleReturn(row.saleReturnId)).then(() => { proxy.$modal.msgSuccess("反审核成功"); getList() }).catch(() => {})
}
function handleReverse(row) {
  proxy.$modal.confirm(`确认对销售退货单"${row.returnNo}"执行一键红冲？将生成正向补偿应收并扣回库存。`).then(() => reverseSaleReturn(row.saleReturnId)).then(() => { proxy.$modal.msgSuccess("一键红冲成功"); getList() }).catch(() => {})
}

function handlePrint(row) {
  getSaleReturn(row.saleReturnId).then(async res => {
    const h = res.data || row
    const items = (h.itemList || []).map(it => ({ ...it, productLabel: getProductName(it.productId), locationLabel: getLocationName(it.locationId) }))
    await printBusinessDocument({
      title: h.isReversal === "1" ? "销售退货红冲单" : "销售退货单",
      docNo: h.returnNo, bizDate: proxy.parseTime(h.businessDate || h.createTime, "{y}-{m}-{d}"),
      counterpartyLabel: "客户", counterpartyName: getCustomerName(h.customerId), warehouseName: getWarehouseName(h.warehouseId),
      columns: [
        { label: "商品", prop: "productLabel" }, { label: "库位", prop: "locationLabel", align: "center" },
        { label: "批次号", prop: "batchNo", align: "center" }, { label: "数量", prop: "quantity", align: "right", summary: "qty" },
        { label: "单价", prop: "price", align: "right" }, { label: "金额", prop: "amount", align: "right", summary: "amount" }
      ],
      items, totalQty: h.totalQty, totalAmount: h.totalAmount, remark: h.remark, createBy: h.createBy, auditBy: h.auditBy
    })
  })
}

function handleRowCommand(cmd, row) {
  if (cmd === "reverse") handleReverse(row)
  else if (cmd === "trace") handleTrace(row)
  else if (cmd === "print") handlePrint(row)
  else if (cmd === "delete") handleDelete(row)
}

function handleDelete(row) {
  const ids = row.saleReturnId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中销售退货单？").then(() => delSaleReturn(ids)).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {})
}

loadWarehouses(); loadCustomers(); loadProducts(); loadLocations(); getList()

function handleExport() {
  proxy.download("business/saleReturn/export", { ...queryParams.value }, `销售退货单_${new Date().getTime()}.xlsx`)
}
</script>
