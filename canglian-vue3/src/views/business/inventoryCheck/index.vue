<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="盘点单号" prop="checkNo">
        <el-input v-model="queryParams.checkNo" placeholder="请输入盘点单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="盘点状态" clearable style="width: 150px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:inventoryCheck:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:inventoryCheck:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:inventoryCheck:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="inventoryCheckList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="盘点单号" align="center" min-width="175">
        <template #default="scope">
          <span>{{ scope.row.checkNo }}</span>
          <el-tag v-if="scope.row.isReversal === '1'" type="danger" size="small" style="margin-left: 4px">红冲单</el-tag>
          <el-tag v-if="scope.row.reversedFlag === '1'" type="warning" size="small" style="margin-left: 4px">已红冲</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="盘点仓库" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="业务日期" align="center" width="110">
        <template #default="scope">{{ parseTime(scope.row.businessDate || scope.row.createTime, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="差异数量合计" align="right" prop="totalDiffQty" width="115" />
      <el-table-column label="差异金额合计" align="right" prop="totalDiffAmount" width="120">
        <template #default="scope">{{ Number(scope.row.totalDiffAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="82">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="235" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:inventoryCheck:edit']">修改</el-button>
            <el-button v-if="canAuditRow(scope.row)" link type="success" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:inventoryCheck:audit']">审核</el-button>
            <el-button v-if="canUnauditRow(scope.row)" link type="warning" icon="RefreshLeft" @click="handleUnaudit(scope.row)" v-hasPermi="['business:inventoryCheck:unaudit']">反审核</el-button>
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

    <el-dialog :title="title" v-model="open" width="min(1080px, 94vw)" append-to-body>
      <el-form ref="inventoryCheckRef" :model="form" :rules="rules" label-width="95px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="盘点单号" prop="checkNo">
              <el-input v-model="form.checkNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="盘点仓库" prop="warehouseId">
              <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%" @change="handleWarehouseChange">
                <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="left">盘点明细（支持多单位与库位联动，审核同步生成成本与盘盈亏凭证）</el-divider>
        <div class="mb8">
          <el-button type="primary" plain size="small" icon="Plus" @click="addItemRow">新增明细</el-button>
        </div>
        <el-table :data="form.itemList" border size="small">
          <el-table-column label="商品" min-width="190">
            <template #default="scope">
              <el-select v-model="scope.row.productId" placeholder="选择商品" filterable style="width: 100%" @change="handleProductChange(scope.row)">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="库位" width="140">
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
          <el-table-column label="批次号" width="110">
            <template #default="scope"><el-input v-model="scope.row.batchNo" placeholder="选填" /></template>
          </el-table-column>
          <el-table-column label="实盘数量" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.actualQty" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="成本单价" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.price" :min="0" :precision="2" :controls="false" style="width: 100%" />
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

    <el-drawer v-model="detailOpen" :title="`盘点单详情 - ${detailDoc.checkNo || ''}`" size="800px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="盘点单号">{{ detailDoc.checkNo }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate || detailDoc.createTime, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="盘点仓库">{{ getWarehouseName(detailDoc.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="差异金额">￥{{ Number(detailDoc.totalDiffAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.itemList || []" border size="small">
        <el-table-column label="商品" min-width="150"><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="库位" width="105"><template #default="s">{{ getLocationName(s.row.locationId) }}</template></el-table-column>
        <el-table-column label="批次" prop="batchNo" width="85" align="center" />
        <el-table-column label="账面数" prop="stockQty" width="75" align="right" />
        <el-table-column label="实盘数" prop="actualQty" width="75" align="right" />
        <el-table-column label="盈亏数" prop="diffQty" width="75" align="right" />
        <el-table-column label="盈亏额" prop="diffAmount" width="85" align="right" />
      </el-table>
    </el-drawer>

    <business-trace-dialog ref="businessTraceRef" />
  </div>
</template>

<script setup name="InventoryCheck">
import { listInventoryCheck, addInventoryCheck, delInventoryCheck, getInventoryCheck, updateInventoryCheck, auditInventoryCheck, unauditInventoryCheck, reverseInventoryCheck } from "@/api/business/inventoryCheck"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  warehouseOptions, productOptions, activeWarehouseOptions, activeProductOptions,
  loadWarehouses, loadProducts, loadLocations, fetchLocationsByWarehouse, fetchProductUnits,
  getWarehouseName, getProductName, getLocationName
} = useBusinessOptions()

const docStatusOptions = [
  { label: "草稿", value: "0", tagType: "info" },
  { label: "已审核", value: "1", tagType: "success" },
  { label: "已完成", value: "2", tagType: "primary" }
]
function getStatusLabel(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.label : (val || "-") }
function getStatusTagType(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.tagType : "info" }

const inventoryCheckList = ref([])
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
  form: { itemList: [] },
  queryParams: { pageNum: 1, pageSize: 10, checkNo: undefined, status: undefined },
  rules: { warehouseId: [{ required: true, message: "盘点仓库不能为空", trigger: "change" }] }
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

function getList() {
  loading.value = true
  listInventoryCheck(queryParams.value).then(res => {
    inventoryCheckList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function addItemRow() {
  if (!Array.isArray(form.value.itemList)) form.value.itemList = []
  form.value.itemList.push({ productId: undefined, unitName: "", convertRatio: 1, locationId: 0, batchNo: "", actualQty: 0, price: 0 })
}
function removeItemRow(idx) { form.value.itemList.splice(idx, 1) }

async function handleProductChange(row) {
  const p = productOptions.value.find(item => Number(item.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) { row.unitName = units[0].unitName; row.convertRatio = Number(units[0].convertRatio || 1) }
  if (p && (!row.price || Number(row.price) <= 0)) row.price = Number(p.costPrice || 0)
}
function handleUnitChange(row) {
  const matched = getUnitOptions(row.productId).find(u => u.unitName === row.unitName)
  row.convertRatio = matched ? Number(matched.convertRatio || 1) : 1
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { checkId: undefined, checkNo: undefined, businessDate: undefined, warehouseId: undefined, status: "0", remark: undefined, itemList: [] }
  warehouseLocations.value = []
  proxy.resetForm("inventoryCheckRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.checkId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}
function canEditRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }
function canAuditRow(r) { return !!r && r.status === "0" }
function canUnauditRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canReverseRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }

function handleAdd() { reset(); addItemRow(); open.value = true; title.value = "新增盘点单" }

function handleViewDetail(row) {
  getInventoryCheck(row.checkId).then(res => { detailDoc.value = res.data || row; detailOpen.value = true })
}
function handleTrace(row) { businessTraceRef.value.open("inventory_check", row.checkId) }

function handleUpdate(row) {
  reset()
  getInventoryCheck(row.checkId || selectedIds.value).then(async res => {
    form.value = res.data || {}
    if (!Array.isArray(form.value.itemList)) form.value.itemList = []
    if (form.value.warehouseId) handleWarehouseChange(form.value.warehouseId)
    for (const it of form.value.itemList) await ensureUnitsLoaded(it.productId)
    open.value = true
    title.value = "修改盘点单"
  })
}

function handleAudit(row) {
  proxy.$modal.confirm("确认审核当前盘点单？（将按实盘盈亏调整库存、核算成本并生成盘盈亏凭证事件）").then(() => auditInventoryCheck(row.checkId)).then(() => {
    proxy.$modal.msgSuccess("审核成功"); getList()
  }).catch(() => {})
}
function handleUnaudit(row) {
  proxy.$modal.confirm(`确认反审核盘点单"${row.checkNo}"？将回滚盘盈盘亏数量、成本与待生成凭证并退回草稿。`).then(() => unauditInventoryCheck(row.checkId)).then(() => {
    proxy.$modal.msgSuccess("反审核成功"); getList()
  }).catch(() => {})
}
function handleReverse(row) {
  proxy.$modal.confirm(`确认对盘点单"${row.checkNo}"执行一键红冲？将生成反向差异调整红冲单。`).then(() => reverseInventoryCheck(row.checkId)).then(() => {
    proxy.$modal.msgSuccess("一键红冲成功"); getList()
  }).catch(() => {})
}

function handlePrint(row) {
  getInventoryCheck(row.checkId).then(async res => {
    const h = res.data || row
    const items = (h.itemList || []).map(it => ({
      ...it,
      productLabel: getProductName(it.productId),
      locationLabel: getLocationName(it.locationId)
    }))
    await printBusinessDocument({
      title: h.isReversal === "1" ? "库存盘点红冲单" : "库存盘点单",
      docNo: h.checkNo,
      bizDate: proxy.parseTime(h.businessDate || h.createTime, "{y}-{m}-{d}"),
      counterpartyLabel: "盘点仓库",
      counterpartyName: getWarehouseName(h.warehouseId),
      warehouseName: getWarehouseName(h.warehouseId),
      columns: [
        { label: "商品", prop: "productLabel" },
        { label: "库位", prop: "locationLabel", align: "center" },
        { label: "批次号", prop: "batchNo", align: "center" },
        { label: "账面数", prop: "stockQty", align: "right" },
        { label: "实盘数", prop: "actualQty", align: "right" },
        { label: "盈亏数", prop: "diffQty", align: "right", summary: "qty" },
        { label: "盈亏额", prop: "diffAmount", align: "right", summary: "amount" }
      ],
      items,
      totalQty: h.totalDiffQty,
      totalAmount: h.totalDiffAmount,
      remark: h.remark,
      createBy: h.createBy
    })
  })
}

function submitForm() {
  proxy.$refs["inventoryCheckRef"].validate(valid => {
    if (!valid) return
    const validItems = (form.value.itemList || []).filter(i => i.productId != null && i.actualQty != null && Number(i.actualQty) >= 0)
    if (!validItems.length) { proxy.$modal.msgError("请至少添加一条有效盘点明细"); return }
    const payload = { ...form.value, itemList: validItems }
    const fn = form.value.checkId ? updateInventoryCheck(payload) : addInventoryCheck(payload)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.checkId ? "修改成功" : "新增成功")
      open.value = false
      getList()
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
  const ids = row.checkId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中盘点单？").then(() => delInventoryCheck(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getList()
  }).catch(() => {})
}

loadWarehouses(); loadProducts(); loadLocations(); getList()
</script>
