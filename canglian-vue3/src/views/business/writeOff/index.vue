<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="核销单号" prop="writeOffNo">
        <el-input v-model="queryParams.writeOffNo" placeholder="请输入核销单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="核销类型" prop="writeOffType">
        <el-select v-model="queryParams.writeOffType" placeholder="请选择核销类型" clearable style="width: 150px">
          <el-option v-for="o in writeOffTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="核销状态" clearable style="width: 130px">
          <el-option v-for="o in writeOffStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:writeOff:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:writeOff:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="CircleCheck" :disabled="isConfirmDisabled" @click="handleConfirm" v-hasPermi="['business:writeOff:confirm']">确认</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:writeOff:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:writeOff:export', 'business:writeOff:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="writeOffList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" align="center" />
      <el-table-column label="核销单号" align="center" prop="writeOffNo" min-width="142" show-overflow-tooltip />
      <el-table-column label="核销类型" align="center" width="96">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.writeOffType === 'receivable' ? 'primary' : 'warning'">
            {{ scope.row.writeOffType === 'receivable' ? '应收核销' : '应付核销' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联往来单" align="center" min-width="138" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.receivableId">{{ getReceivableNo(scope.row.receivableId) }}</span>
          <span v-else-if="scope.row.payableId">{{ getPayableNo(scope.row.payableId) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="关联收/付款单" align="center" min-width="138" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.receiptId">{{ getReceiptNo(scope.row.receiptId) }}</span>
          <span v-else-if="scope.row.paymentId">{{ getPaymentNo(scope.row.paymentId) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="核销金额" align="right" header-align="right" prop="amount" width="102">
        <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="核销日期" align="center" width="106">
        <template #default="scope">{{ parseTime(scope.row.writeOffDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="84">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.status === '1' ? 'success' : 'info'">{{ scope.row.status === '1' ? '已核销' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="215" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:writeOff:edit']">修改</el-button>
            <el-button v-if="canConfirmRow(scope.row)" link type="success" icon="CircleCheck" @click="handleConfirm(scope.row)" v-hasPermi="['business:writeOff:confirm']">确认</el-button>
            <el-button v-if="scope.row.status === '1'" link type="warning" icon="RefreshLeft" @click="handleUnconfirm(scope.row)" v-hasPermi="['business:writeOff:unconfirm']">反确认</el-button>
            <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
            <el-button v-if="canDeleteRow(scope.row)" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:writeOff:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改核销单弹窗（零裸ID，全下拉联动） -->
    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="writeOffRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="核销单号" prop="writeOffNo">
              <el-input v-model="form.writeOffNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核销类型" prop="writeOffType">
              <el-select v-model="form.writeOffType" placeholder="请选择核销类型" style="width: 100%" @change="handleTypeChange">
                <el-option v-for="o in writeOffTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="form.writeOffType === 'receivable'">
          <el-col :span="12">
            <el-form-item label="未结清应收" prop="receivableId">
              <el-select v-model="form.receivableId" placeholder="请选择应收单" filterable clearable style="width: 100%" @change="syncArAmount">
                <el-option
                  v-for="r in openReceivables"
                  :key="r.receivableId"
                  :label="`${r.receivableNo} | ${getCustomerName(r.customerId)} | 未收￥${Number(r.unreceivedAmount || 0).toFixed(2)}`"
                  :value="r.receivableId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已确认收款" prop="receiptId">
              <el-select v-model="form.receiptId" placeholder="请选择收款单" filterable clearable style="width: 100%">
                <el-option
                  v-for="rc in confirmedReceipts"
                  :key="rc.receiptId"
                  :label="`${rc.receiptNo} | ${getCustomerName(rc.customerId)} | ￥${Number(rc.amount || 0).toFixed(2)}`"
                  :value="rc.receiptId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="form.writeOffType === 'payable'">
          <el-col :span="12">
            <el-form-item label="未结清应付" prop="payableId">
              <el-select v-model="form.payableId" placeholder="请选择应付单" filterable clearable style="width: 100%" @change="syncApAmount">
                <el-option
                  v-for="p in openPayables"
                  :key="p.payableId"
                  :label="`${p.payableNo} | ${getSupplierName(p.supplierId)} | 未付￥${Number(p.unpaidAmount || 0).toFixed(2)}`"
                  :value="p.payableId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已确认付款" prop="paymentId">
              <el-select v-model="form.paymentId" placeholder="请选择付款单" filterable clearable style="width: 100%">
                <el-option
                  v-for="pm in confirmedPayments"
                  :key="pm.paymentId"
                  :label="`${pm.paymentNo} | ${getSupplierName(pm.supplierId)} | ￥${Number(pm.amount || 0).toFixed(2)}`"
                  :value="pm.paymentId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="核销金额" prop="amount">
              <el-input-number v-model="form.amount" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核销日期" prop="writeOffDate">
              <el-date-picker v-model="form.writeOffDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择核销日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="WriteOff">
import { listWriteOff, addWriteOff, delWriteOff, getWriteOff, updateWriteOff, confirmWriteOff, unconfirmWriteOff } from "@/api/business/writeOff"
import { listReceivable } from "@/api/business/receivable"
import { listPayable } from "@/api/business/payable"
import { listReceipt } from "@/api/business/receipt"
import { listPayment } from "@/api/business/payment"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  loadCustomers, loadSuppliers,
  fetchOpenReceivables, fetchOpenPayables, fetchConfirmedReceipts, fetchConfirmedPayments,
  getCustomerName, getSupplierName
} = useBusinessOptions()

const docNoMaps = reactive({ receivable: {}, payable: {}, receipt: {}, payment: {} })
function loadDocNoMaps() {
  listReceivable({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { docNoMaps.receivable[i.receivableId] = i.receivableNo })).catch(() => {})
  listPayable({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { docNoMaps.payable[i.payableId] = i.payableNo })).catch(() => {})
  listReceipt({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { docNoMaps.receipt[i.receiptId] = i.receiptNo })).catch(() => {})
  listPayment({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { docNoMaps.payment[i.paymentId] = i.paymentNo })).catch(() => {})
}
function getReceivableNo(id) { return docNoMaps.receivable[id] || `AR #${id}` }
function getPayableNo(id) { return docNoMaps.payable[id] || `AP #${id}` }
function getReceiptNo(id) { return docNoMaps.receipt[id] || `收款 #${id}` }
function getPaymentNo(id) { return docNoMaps.payment[id] || `付款 #${id}` }

loadCustomers(); loadSuppliers(); loadDocNoMaps()

const writeOffList = ref([])
const openReceivables = ref([])
const openPayables = ref([])
const confirmedReceipts = ref([])
const confirmedPayments = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isConfirmDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const writeOffTypeOptions = [
  { label: "应收核销", value: "receivable" },
  { label: "应付核销", value: "payable" }
]
const writeOffStatusOptions = [
  { label: "草稿", value: "0" },
  { label: "已核销", value: "1" }
]

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, writeOffNo: undefined, writeOffType: undefined, status: undefined },
  rules: {
    writeOffType: [{ required: true, message: "请选择核销类型", trigger: "change" }],
    amount: [{ required: true, message: "请输入核销金额", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

async function loadDropdownsByType(type) {
  if (type === "receivable") {
    const [ars, rcs] = await Promise.all([fetchOpenReceivables(), fetchConfirmedReceipts()])
    openReceivables.value = ars
    confirmedReceipts.value = rcs
  } else if (type === "payable") {
    const [aps, pms] = await Promise.all([fetchOpenPayables(), fetchConfirmedPayments()])
    openPayables.value = aps
    confirmedPayments.value = pms
  }
}

function handleTypeChange(type) {
  form.value.receivableId = undefined
  form.value.payableId = undefined
  form.value.receiptId = undefined
  form.value.paymentId = undefined
  loadDropdownsByType(type)
}

function syncArAmount(arId) {
  const ar = openReceivables.value.find(r => Number(r.receivableId) === Number(arId))
  if (ar && (!form.value.amount || Number(form.value.amount) <= 0)) form.value.amount = Number(ar.unreceivedAmount || 0)
}
function syncApAmount(apId) {
  const ap = openPayables.value.find(p => Number(p.payableId) === Number(apId))
  if (ap && (!form.value.amount || Number(form.value.amount) <= 0)) form.value.amount = Number(ap.unpaidAmount || 0)
}

function getList() {
  loading.value = true
  listWriteOff(queryParams.value).then(res => {
    writeOffList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { writeOffId: undefined, writeOffNo: undefined, writeOffType: "receivable", receivableId: undefined, payableId: undefined, receiptId: undefined, paymentId: undefined, amount: undefined, writeOffDate: undefined, status: "0", remark: undefined }
  proxy.resetForm("writeOffRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.writeOffId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isConfirmDisabled.value = selection.length !== 1 || !canConfirmRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}
function canEditRow(r) { return !!r && r.status === "0" }
function canConfirmRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }

async function handleAdd() {
  reset()
  await loadDropdownsByType("receivable")
  open.value = true; title.value = "新增核销单"
}

function handleUpdate(row) {
  reset()
  getWriteOff(row.writeOffId || selectedIds.value).then(async res => {
    form.value = res.data
    form.value.amount = Number(form.value.amount || 0)
    await loadDropdownsByType(form.value.writeOffType || "receivable")
    open.value = true; title.value = "修改核销单"
  })
}

function handleConfirm(row) {
  const id = row?.writeOffId || selectedIds.value[0]
  proxy.$modal.confirm("确认执行该核销单？").then(() => confirmWriteOff(id)).then(() => {
    proxy.$modal.msgSuccess("核销确认成功"); getList()
  }).catch(() => {})
}

function handleUnconfirm(row) {
  proxy.$modal.confirm(`确认反确认核销单"${row.writeOffNo}"？系统将回退往来核销金额并退回草稿。`).then(() => unconfirmWriteOff(row.writeOffId)).then(() => {
    proxy.$modal.msgSuccess("核销反确认成功"); getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printFinancialDocument({
    title: row.writeOffType === "receivable" ? "应收核销单" : "应付核销单",
    docNo: row.writeOffNo,
    amount: row.amount,
    metaList: [
      { label: "核销单号", value: row.writeOffNo },
      { label: "核销类型", value: row.writeOffType === "receivable" ? "应收核销" : "应付核销" },
      { label: "核销日期", value: proxy.parseTime(row.writeOffDate, "{y}-{m}-{d}") },
      { label: "状态", value: row.status === "1" ? "已核销" : "草稿" }
    ],
    remark: row.remark,
    createBy: row.createBy
  })
}

function submitForm() {
  proxy.$refs["writeOffRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.writeOffId ? updateWriteOff(form.value) : addWriteOff(form.value)
    fn.then(() => { proxy.$modal.msgSuccess(form.value.writeOffId ? "修改成功" : "新增成功"); open.value = false; getList() })
  })
}

function handleDelete(row) {
  const ids = row.writeOffId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中核销单？").then(() => delWriteOff(ids)).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {})
}

getList()

function handleExport() {
  proxy.download("business/writeOff/export", { ...queryParams.value }, `核销单_${new Date().getTime()}.xlsx`)
}
</script>
