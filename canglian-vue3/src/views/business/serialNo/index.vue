<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="序列号(SN)" prop="snCode">
        <el-input v-model="queryParams.snCode" placeholder="请输入或扫描SN码" clearable style="width: 210px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 220px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable filterable style="width: 180px">
          <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
          <el-option label="在库" value="in_stock" />
          <el-option label="已出库" value="out_stock" />
          <el-option label="退货回库" value="returned" />
          <el-option label="已报废" value="scrapped" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="openRegisterDialog" v-hasPermi="['business:serialNo:edit']">批量登记入库SN</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="TopRight" @click="openConsumeDialog" v-hasPermi="['business:serialNo:edit']">SN出库核销</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="RefreshLeft" @click="openReturnDialog" v-hasPermi="['business:serialNo:edit']">SN退货回库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="selectedIds.length === 0" @click="handleDelete()" v-hasPermi="['business:serialNo:edit']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="serialList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序列号(SN)" align="center" prop="snCode" min-width="170" />
      <el-table-column label="商品" align="center" min-width="180" show-overflow-tooltip>
        <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
      </el-table-column>
      <el-table-column label="当前仓库" align="center" min-width="120">
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="售出客户" align="center" min-width="130">
        <template #default="scope">{{ scope.row.customerId ? getCustomerName(scope.row.customerId) : '-' }}</template>
      </el-table-column>
      <el-table-column label="质保截止" align="center" prop="warrantyEndDate" width="120">
        <template #default="scope">{{ parseTime(scope.row.warrantyEndDate, '{y}-{m}-{d}') || '-' }}</template>
      </el-table-column>
      <el-table-column label="登记时间" align="center" prop="createTime" width="165">
        <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template #default="scope">
          <el-button link type="primary" icon="Connection" @click="handleTrace(scope.row)">溯源履历</el-button>
          <el-button link type="danger" icon="Delete" :disabled="scope.row.status === 'out_stock'" @click="handleDelete(scope.row)" v-hasPermi="['business:serialNo:edit']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 批量登记 SN 弹窗 -->
    <el-dialog title="批量登记入库序列号(SN)" v-model="registerOpen" width="560px" append-to-body>
      <el-form :model="registerForm" label-width="100px">
        <el-form-item label="商品" required>
          <el-select v-model="registerForm.productId" placeholder="请选择商品" filterable style="width: 100%">
            <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库仓库" required>
          <el-select v-model="registerForm.warehouseId" placeholder="请选择仓库" filterable style="width: 100%" @change="loadRegLocations">
            <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="库位">
          <el-select v-model="registerForm.locationId" placeholder="默认库位（可选）" clearable filterable style="width: 100%">
            <el-option v-for="l in regLocations" :key="l.locationId" :label="`${l.locationCode} - ${l.locationName}`" :value="l.locationId" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="registerForm.batchNo" placeholder="可选，如 B20261122" />
        </el-form-item>
        <el-form-item label="关联单号">
          <el-input v-model="registerForm.billNo" placeholder="可选，如入库单号" />
        </el-form-item>
        <el-form-item label="SN码清单" required>
          <el-input v-model="registerForm.snCodesText" type="textarea" :rows="5" placeholder="每行输入或扫码枪连续扫入一个唯一序列号(SN)，支持逗号或换行分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitRegister">确 定 登 记</el-button>
        <el-button @click="registerOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- SN 出库核销弹窗 -->
    <el-dialog title="序列号(SN)出库核销" v-model="consumeOpen" width="520px" append-to-body>
      <el-form :model="consumeForm" label-width="100px">
        <el-form-item label="售出客户">
          <el-select v-model="consumeForm.customerId" placeholder="请选择客户（可选）" clearable filterable style="width: 100%">
            <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="出库单号">
          <el-input v-model="consumeForm.billNo" placeholder="可选，关联出库单号" />
        </el-form-item>
        <el-form-item label="SN码清单" required>
          <el-input v-model="consumeForm.snCodesText" type="textarea" :rows="4" placeholder="每行或逗号分隔输入待核销出库的SN码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitConsume">确认出库核销</el-button>
        <el-button @click="consumeOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- SN 退货回库弹窗 -->
    <el-dialog title="序列号(SN)退货回库" v-model="returnOpen" width="520px" append-to-body>
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="退回仓库" required>
          <el-select v-model="returnForm.warehouseId" placeholder="请选择退回仓库" filterable style="width: 100%">
            <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="退货单号">
          <el-input v-model="returnForm.billNo" placeholder="可选，关联退货单号" />
        </el-form-item>
        <el-form-item label="SN码清单" required>
          <el-input v-model="returnForm.snCodesText" type="textarea" :rows="4" placeholder="每行或逗号分隔输入已出库需退回的SN码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitReturn">确认退货回库</el-button>
        <el-button @click="returnOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- SN 溯源履历抽屉 -->
    <el-drawer v-model="traceOpen" title="序列号(SN)全生命周期溯源" size="540px">
      <div v-if="traceData.serial" style="margin-bottom: 16px">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="序列号(SN)">{{ traceData.serial.snCode }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="statusTagType(traceData.serial.status)">{{ statusLabel(traceData.serial.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="商品">{{ getProductName(traceData.serial.productId) }}</el-descriptions-item>
          <el-descriptions-item label="当前仓库">{{ getWarehouseName(traceData.serial.warehouseId) }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ traceData.serial.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最终客户">{{ traceData.serial.customerId ? getCustomerName(traceData.serial.customerId) : '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-divider content-position="left">流转履历时间轴</el-divider>
      <el-timeline v-if="traceData.logs && traceData.logs.length">
        <el-timeline-item
          v-for="log in traceData.logs"
          :key="log.snLogId"
          :timestamp="parseTime(log.createTime)"
          placement="top"
        >
          <el-card shadow="never">
            <div><strong>{{ actionLabel(log.actionType) }}</strong> &nbsp; 单号：{{ log.billNo || '-' }}</div>
            <div style="color: #666; font-size: 12px; margin-top: 4px">
              仓库：{{ getWarehouseName(log.warehouseId) }} &nbsp;|&nbsp; 操作人：{{ log.createBy || '-' }}
            </div>
            <div v-if="log.remark" style="color: #888; font-size: 12px; margin-top: 4px">{{ log.remark }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无流转日志" />
    </el-drawer>
  </div>
</template>

<script setup name="SerialNo">
import { listSerialNo, traceSerialNo, batchRegisterSerialNo, consumeSerialNo, returnBackSerialNo, delSerialNo } from "@/api/business/serialNo"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const {
  productOptions, warehouseOptions, customerOptions,
  loadProducts, loadWarehouses, loadCustomers, fetchLocationsByWarehouse,
  getProductName, getWarehouseName, getCustomerName
} = useBusinessOptions()

loadProducts()
loadWarehouses()
loadCustomers()

const loading = ref(true)
const showSearch = ref(true)
const serialList = ref([])
const total = ref(0)
const selectedIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  snCode: undefined,
  productId: undefined,
  warehouseId: undefined,
  status: undefined
})

const registerOpen = ref(false)
const regLocations = ref([])
const registerForm = reactive({ productId: undefined, warehouseId: undefined, locationId: undefined, batchNo: "", billNo: "", snCodesText: "" })

const consumeOpen = ref(false)
const consumeForm = reactive({ customerId: undefined, billNo: "", snCodesText: "" })

const returnOpen = ref(false)
const returnForm = reactive({ warehouseId: undefined, billNo: "", snCodesText: "" })

const traceOpen = ref(false)
const traceData = reactive({ serial: null, logs: [] })

function parseSnLines(text) {
  return String(text || "").split(/[\n\r,;，；]+/).map(s => s.trim()).filter(Boolean)
}

function statusLabel(s) {
  const map = { in_stock: "在库", out_stock: "已出库", returned: "退货回库", scrapped: "已报废" }
  return map[s] || s || "-"
}

function statusTagType(s) {
  if (s === "in_stock") return "success"
  if (s === "out_stock") return "info"
  if (s === "returned") return "warning"
  return "danger"
}

function actionLabel(a) {
  const map = { inbound: "入库登记", outbound: "出库核销", sale_return: "销售退货回库", purchase_return: "采购退货出库", transfer: "仓库调拨", adjust: "库存调整" }
  return map[a] || a
}

function getList() {
  loading.value = true
  listSerialNo(queryParams).then(res => {
    serialList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.serialId)
}

function openRegisterDialog() {
  Object.assign(registerForm, { productId: undefined, warehouseId: undefined, locationId: undefined, batchNo: "", billNo: "", snCodesText: "" })
  regLocations.value = []
  registerOpen.value = true
}

function loadRegLocations(whId) {
  registerForm.locationId = undefined
  fetchLocationsByWarehouse(whId).then(rows => { regLocations.value = rows })
}

function submitRegister() {
  const snCodes = parseSnLines(registerForm.snCodesText)
  if (!registerForm.productId || !registerForm.warehouseId || snCodes.length === 0) {
    proxy.$modal.msgError("请选择商品、仓库并录入至少一个SN码")
    return
  }
  batchRegisterSerialNo({
    productId: registerForm.productId,
    warehouseId: registerForm.warehouseId,
    locationId: registerForm.locationId || 0,
    batchNo: registerForm.batchNo,
    billNo: registerForm.billNo,
    snCodes
  }).then(() => {
    proxy.$modal.msgSuccess(`成功登记 ${snCodes.length} 个序列号`)
    registerOpen.value = false
    getList()
  })
}

function openConsumeDialog() {
  Object.assign(consumeForm, { customerId: undefined, billNo: "", snCodesText: "" })
  consumeOpen.value = true
}

function submitConsume() {
  const snCodes = parseSnLines(consumeForm.snCodesText)
  if (snCodes.length === 0) {
    proxy.$modal.msgError("请录入至少一个待出库的SN码")
    return
  }
  consumeSerialNo({ customerId: consumeForm.customerId, billNo: consumeForm.billNo, snCodes }).then(() => {
    proxy.$modal.msgSuccess(`成功核销出库 ${snCodes.length} 个序列号`)
    consumeOpen.value = false
    getList()
  })
}

function openReturnDialog() {
  Object.assign(returnForm, { warehouseId: undefined, billNo: "", snCodesText: "" })
  returnOpen.value = true
}

function submitReturn() {
  const snCodes = parseSnLines(returnForm.snCodesText)
  if (!returnForm.warehouseId || snCodes.length === 0) {
    proxy.$modal.msgError("请选择退回仓库并录入至少一个SN码")
    return
  }
  returnBackSerialNo({ warehouseId: returnForm.warehouseId, billNo: returnForm.billNo, snCodes }).then(() => {
    proxy.$modal.msgSuccess(`成功退货回库 ${snCodes.length} 个序列号`)
    returnOpen.value = false
    getList()
  })
}

function handleTrace(row) {
  traceSerialNo(row.snCode).then(res => {
    const data = res.data || {}
    traceData.serial = data.serial || row
    traceData.logs = data.logs || []
    traceOpen.value = true
  })
}

function handleDelete(row) {
  const ids = row ? row.serialId : selectedIds.value
  proxy.$modal.confirm(`是否确认删除选中的序列号记录？`).then(() => delSerialNo(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()
  }).catch(() => {})
}

getList()
</script>
