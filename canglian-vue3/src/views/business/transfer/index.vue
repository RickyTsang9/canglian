<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="调拨单号" prop="transferNo">
        <el-input v-model="queryParams.transferNo" placeholder="请输入调拨单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="调拨状态" clearable style="width: 150px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:transfer:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:transfer:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:transfer:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="transferList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="调拨单号" align="center" min-width="170">
        <template #default="scope">
          <span>{{ scope.row.transferNo }}</span>
          <el-tag v-if="scope.row.isReversal === '1'" type="danger" size="small" style="margin-left: 4px">红冲单</el-tag>
          <el-tag v-if="scope.row.reversedFlag === '1'" type="warning" size="small" style="margin-left: 4px">已红冲</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="调出仓库" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.outWarehouseId) }}</template>
      </el-table-column>
      <el-table-column label="调入仓库" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.inWarehouseId) }}</template>
      </el-table-column>
      <el-table-column label="业务日期" align="center" width="110">
        <template #default="scope">{{ parseTime(scope.row.businessDate || scope.row.createTime, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="总数量" align="right" prop="totalQty" width="85" />
      <el-table-column label="总金额" align="right" prop="totalAmount" width="105" />
      <el-table-column label="状态" align="center" prop="status" width="85">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="370" align="center">
        <template #default="scope">
          <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:transfer:edit']">修改</el-button>
          <el-button link type="success" icon="CircleCheck" v-if="canAuditRow(scope.row)" @click="handleAudit(scope.row)" v-hasPermi="['business:transfer:audit']">审核</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="canUnauditRow(scope.row)" @click="handleUnaudit(scope.row)" v-hasPermi="['business:transfer:unaudit']">反审核</el-button>
          <el-button link type="danger" icon="DocumentDelete" v-if="canReverseRow(scope.row)" @click="handleReverse(scope.row)" v-hasPermi="['business:transfer:unaudit']">红冲</el-button>
          <el-button link type="danger" icon="Delete" :disabled="!canDeleteRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:transfer:remove']">删除</el-button>
          <el-button link type="primary" icon="Connection" @click="handleTrace(scope.row)" v-hasPermi="['business:trace:query']">链路</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)" v-hasPermi="['business:transfer:print']">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="1220px" append-to-body>
      <el-form ref="transferRef" :model="form" :rules="rules" label-width="95px">
        <el-row>
          <el-col :span="6">
            <el-form-item label="调拨单号" prop="transferNo">
              <el-input v-model="form.transferNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="调出仓库" prop="outWarehouseId">
              <el-select v-model="form.outWarehouseId" placeholder="请选择调出仓库" filterable style="width: 100%" @change="handleOutWarehouseChange">
                <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="调入仓库" prop="inWarehouseId">
              <el-select v-model="form.inWarehouseId" placeholder="请选择调入仓库" filterable style="width: 100%" @change="handleInWarehouseChange">
                <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="left">调拨明细（启用序列号商品必须录入足额在库 SN 码）</el-divider>
        <div class="mb8">
          <el-button type="primary" plain size="small" icon="Plus" @click="addItemRow">新增明细</el-button>
        </div>
        <el-table :data="form.itemList" border size="small">
          <el-table-column label="商品" min-width="170">
            <template #default="scope">
              <el-select v-model="scope.row.productId" placeholder="选择商品" filterable style="width: 100%" @change="handleProductChange(scope.row)">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}${p.enableSerial === '1' ? ' [SN]' : ''}`" :value="p.productId" />
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
          <el-table-column label="调出库位" width="125">
            <template #default="scope">
              <el-select v-model="scope.row.outLocationId" placeholder="默认库位" filterable style="width: 100%">
                <el-option label="默认库位" :value="0" />
                <el-option v-for="l in outLocations" :key="l.locationId" :label="`${l.locationCode} - ${l.locationName}`" :value="l.locationId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="调入库位" width="125">
            <template #default="scope">
              <el-select v-model="scope.row.inLocationId" placeholder="默认库位" filterable style="width: 100%">
                <el-option label="默认库位" :value="0" />
                <el-option v-for="l in inLocations" :key="l.locationId" :label="`${l.locationCode} - ${l.locationName}`" :value="l.locationId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="批次号" width="100">
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
          <el-table-column label="金额" width="80" align="right">
            <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="序列号(SN)" min-width="175">
            <template #default="scope">
              <template v-if="isProductSerialEnabled(scope.row.productId)">
                <el-input v-model="scope.row.snCodes" type="textarea" :rows="2" :placeholder="`必填：需 ${requiredSnCount(scope.row)} 个调出仓SN`" />
                <div style="font-size: 11px; color: #e6a23c">已录 {{ countEnteredSns(scope.row.snCodes) }} / 需 {{ requiredSnCount(scope.row) }}</div>
              </template>
              <span v-else style="color: #909399; font-size: 12px">未启用SN</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="55" align="center">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="removeItemRow(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailOpen" :title="`调拨单详情 - ${detailDoc.transferNo || ''}`" size="820px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="调拨单号">{{ detailDoc.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate || detailDoc.createTime, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="调出仓库">{{ getWarehouseName(detailDoc.outWarehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="调入仓库">{{ getWarehouseName(detailDoc.inWarehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ detailDoc.totalQty }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ Number(detailDoc.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.itemList || []" border size="small">
        <el-table-column label="商品" min-width="150"><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="调出库位" width="105"><template #default="s">{{ getLocationName(s.row.outLocationId) }}</template></el-table-column>
        <el-table-column label="调入库位" width="105"><template #default="s">{{ getLocationName(s.row.inLocationId) }}</template></el-table-column>
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

<script setup name="Transfer">
import { listTransfer, addTransfer, delTransfer, getTransfer, updateTransfer, auditTransfer, unauditTransfer, reverseTransfer, printTransfer } from "@/api/business/transfer"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  warehouseOptions, productOptions, activeWarehouseOptions, activeProductOptions,
  loadWarehouses, loadProducts, loadLocations, fetchLocationsByWarehouse, fetchProductUnits,
  isProductSerialEnabled, getWarehouseName, getProductName, getLocationName
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

const transferList = ref([])
const outLocations = ref([])
const inLocations = ref([])
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
  queryParams: { pageNum: 1, pageSize: 10, transferNo: undefined, status: undefined },
  rules: {
    outWarehouseId: [{ required: true, message: "调出仓库不能为空", trigger: "change" }],
    inWarehouseId: [{ required: true, message: "调入仓库不能为空", trigger: "change" }]
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

function handleOutWarehouseChange(whId) { fetchLocationsByWarehouse(whId).then(r => { outLocations.value = r }) }
function handleInWarehouseChange(whId) { fetchLocationsByWarehouse(whId).then(r => { inLocations.value = r }) }

function getList() {
  loading.value = true
  listTransfer(queryParams.value).then(res => {
    transferList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function addItemRow() {
  if (!Array.isArray(form.value.itemList)) form.value.itemList = []
  form.value.itemList.push({ productId: undefined, unitName: "", convertRatio: 1, outLocationId: 0, inLocationId: 0, batchNo: "", quantity: 1, price: 0, amount: 0, snCodes: "" })
}
function removeItemRow(idx) { form.value.itemList.splice(idx, 1); recalcTotals() }

async function handleProductChange(row) {
  const p = productOptions.value.find(item => Number(item.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) {
    row.unitName = units[0].unitName
    row.convertRatio = Number(units[0].convertRatio || 1)
  }
  if (p && (!row.price || Number(row.price) <= 0)) row.price = Number(p.costPrice || 0)
  recalcRow(row)
}

function handleUnitChange(row) {
  const prevRatio = Number(row.convertRatio || 1)
  const matched = getUnitOptions(row.productId).find(u => u.unitName === row.unitName)
  const newRatio = matched ? Number(matched.convertRatio || 1) : 1
  row.convertRatio = newRatio
  if (prevRatio > 0 && newRatio > 0 && Number(row.price || 0) > 0) {
    row.price = Number((Number(row.price) * newRatio / prevRatio).toFixed(2))
  }
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
  form.value = { transferId: undefined, transferNo: undefined, businessDate: undefined, outWarehouseId: undefined, inWarehouseId: undefined, totalQty: 0, totalAmount: 0, status: "0", remark: undefined, itemList: [] }
  outLocations.value = []; inLocations.value = []
  proxy.resetForm("transferRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.transferId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}
function canEditRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }
function canAuditRow(r) { return !!r && r.status === "0" }
function canUnauditRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canReverseRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }

function handleAdd() { reset(); addItemRow(); open.value = true; title.value = "新增调拨单" }

function handleViewDetail(row) {
  getTransfer(row.transferId).then(res => { detailDoc.value = res.data || row; detailOpen.value = true })
}
function handleTrace(row) { businessTraceRef.value.open("transfer", row.transferId) }

function handleUpdate(row) {
  reset()
  getTransfer(row.transferId || selectedIds.value).then(async res => {
    form.value = res.data || {}
    if (!Array.isArray(form.value.itemList)) form.value.itemList = []
    if (form.value.outWarehouseId) handleOutWarehouseChange(form.value.outWarehouseId)
    if (form.value.inWarehouseId) handleInWarehouseChange(form.value.inWarehouseId)
    for (const it of form.value.itemList) await ensureUnitsLoaded(it.productId)
    open.value = true
    title.value = "修改调拨单"
  })
}

function handleAudit(row) {
  proxy.$modal.confirm("确认审核当前调拨单？（将同步结转跨仓库存、序列号与真实成本）").then(() => auditTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("审核成功"); getList()
  }).catch(() => {})
}
function handleUnaudit(row) {
  proxy.$modal.confirm(`确认反审核调拨单"${row.transferNo}"？将反向恢复调入/调出仓存货、成本与序列号并退回草稿。`).then(() => unauditTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("反审核成功"); getList()
  }).catch(() => {})
}
function handleReverse(row) {
  proxy.$modal.confirm(`确认对调拨单"${row.transferNo}"执行一键红冲？将生成反向调拨红冲单。`).then(() => reverseTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("一键红冲成功"); getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printTransfer(row.transferId).then(async res => {
    const data = res.data || {}
    const h = data.header || row
    const dn = data.displayNames || {}
    const prodNames = dn.productNames || {}
    const locNames = dn.locationNames || {}
    const items = (data.items || []).map(it => ({
      ...it,
      productLabel: prodNames[String(it.productId)] || getProductName(it.productId),
      outLocLabel: locNames[String(it.outLocationId)] || getLocationName(it.outLocationId),
      inLocLabel: locNames[String(it.inLocationId)] || getLocationName(it.inLocationId)
    }))
    await printBusinessDocument({
      title: h.isReversal === "1" ? "仓库调拨红冲单" : "仓库调拨单",
      docNo: h.transferNo,
      bizDate: proxy.parseTime(h.businessDate || h.createTime, "{y}-{m}-{d}"),
      counterpartyLabel: "调出仓库",
      counterpartyName: dn.outWarehouseName || getWarehouseName(h.outWarehouseId),
      warehouseName: dn.inWarehouseName || getWarehouseName(h.inWarehouseId),
      columns: [
        { label: "商品", prop: "productLabel" },
        { label: "调出库位", prop: "outLocLabel", align: "center" },
        { label: "调入库位", prop: "inLocLabel", align: "center" },
        { label: "批次号", prop: "batchNo", align: "center" },
        { label: "数量", prop: "quantity", align: "right", summary: "qty" },
        { label: "单价", prop: "price", align: "right" },
        { label: "金额", prop: "amount", align: "right", summary: "amount" }
      ],
      items,
      totalQty: h.totalQty,
      totalAmount: h.totalAmount,
      remark: h.remark,
      createBy: h.createBy
    })
  })
}

function submitForm() {
  proxy.$refs["transferRef"].validate(valid => {
    if (!valid) return
    const validItems = (form.value.itemList || []).filter(i => i.productId && Number(i.quantity) > 0)
    if (!validItems.length) { proxy.$modal.msgError("请至少添加一条有效调拨明细"); return }
    const payload = { ...form.value, itemList: validItems }
    const fn = form.value.transferId ? updateTransfer(payload) : addTransfer(payload)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.transferId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const ids = row.transferId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中调拨单？").then(() => delTransfer(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getList()
  }).catch(() => {})
}

loadWarehouses(); loadProducts(); loadLocations(); getList()
</script>
