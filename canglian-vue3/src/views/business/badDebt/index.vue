<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="坏账单号" prop="badDebtNo">
        <el-input v-model="queryParams.badDebtNo" placeholder="请输入坏账单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 200px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="坏账状态" clearable style="width: 140px">
          <el-option v-for="dict in badDebtStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:badDebt:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:badDebt:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:badDebt:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="badDebtList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="坏账单号" align="center" prop="badDebtNo" min-width="145" />
      <el-table-column label="客户" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="应收单" align="center" width="105">
        <template #default="scope">AR #{{ scope.row.receivableId }}</template>
      </el-table-column>
      <el-table-column label="核销坏账金额" align="right" prop="amount" width="115" />
      <el-table-column label="已收回金额" align="right" width="105">
        <template #default="scope">{{ Number(scope.row.recoveredAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="坏账日期" align="center" width="115">
        <template #default="scope">{{ parseTime(scope.row.badDebtDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="原因" align="center" prop="reason" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" align="center" width="95">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="330" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:badDebt:edit']">修改</el-button>
          <el-button link type="success" icon="CircleCheck" v-if="canConfirmRow(scope.row)" @click="handleConfirm(scope.row)" v-hasPermi="['business:badDebt:confirm']">确认核销</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="canUnconfirmRow(scope.row)" @click="handleUnconfirm(scope.row)" v-hasPermi="['business:badDebt:confirm']">反确认</el-button>
          <el-button link type="warning" icon="Money" v-if="canRecoverRow(scope.row)" @click="openRecoverDialog(scope.row)" v-hasPermi="['business:badDebt:recover']">坏账收回</el-button>
          <el-button link type="danger" icon="Delete" :disabled="!canDeleteRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:badDebt:remove']">删除</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改坏账单弹窗 -->
    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="badDebtRef" :model="form" :rules="rules" label-width="95px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="坏账单号" prop="badDebtNo">
              <el-input v-model="form.badDebtNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="选客户自动过滤未收应收" clearable filterable style="width: 100%" @change="handleCustomerChange">
                <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="关联应收单" prop="receivableId">
              <el-select v-model="form.receivableId" placeholder="请选择未结清应收单" filterable style="width: 100%" @change="handleArSelect">
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
            <el-form-item label="坏账金额" prop="amount">
              <el-input-number v-model="form.amount" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="坏账日期" prop="badDebtDate">
              <el-date-picker v-model="form.badDebtDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="坏账原因" prop="reason">
              <el-input v-model="form.reason" placeholder="如：客户破产清算无法追回" />
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

    <!-- 坏账收回弹窗 -->
    <el-dialog title="已核销坏账收回入账" v-model="recoverOpen" width="480px" append-to-body>
      <el-form :model="recoverForm" label-width="100px">
        <el-form-item label="坏账单号"><strong>{{ recoverForm.badDebtNo }}</strong></el-form-item>
        <el-form-item label="可收回余额">
          <span style="color: #e6a23c; font-weight: 600">￥{{ recoverForm.maxRecoverable.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="本次收回金额" required>
          <el-input-number v-model="recoverForm.amount" :min="0.01" :max="recoverForm.maxRecoverable" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="入账资金账户" required>
          <el-select v-model="recoverForm.accountId" placeholder="请选择入账资金账户" filterable style="width: 100%">
            <el-option v-for="acc in fundAccountOptions" :key="acc.accountId" :label="`${acc.accountNo || ''} - ${acc.accountName}`" :value="acc.accountId" />
          </el-select>
        </el-form-item>
        <el-form-item label="收回说明">
          <el-input v-model="recoverForm.remark" placeholder="如：诉讼执行回款" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitRecover">确认收回入账</el-button>
        <el-button @click="recoverOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="BadDebt">
import { listBadDebt, addBadDebt, delBadDebt, getBadDebt, updateBadDebt, confirmBadDebt, unconfirmBadDebt, recoverBadDebt } from "@/api/business/badDebt"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  customerOptions, fundAccountOptions,
  loadCustomers, loadFundAccounts, fetchOpenReceivables, getCustomerName
} = useBusinessOptions()

const badDebtStatusOptions = [
  { label: "草稿", value: "0", tagType: "info" },
  { label: "已核销", value: "1", tagType: "warning" },
  { label: "已全部收回", value: "2", tagType: "success" }
]
function getStatusLabel(val) { const i = badDebtStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.label : (val || "-") }
function getStatusTagType(val) { const i = badDebtStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.tagType : "info" }

const badDebtList = ref([])
const openReceivables = ref([])
const open = ref(false)
const recoverOpen = ref(false)
const recoverForm = reactive({ badDebtId: undefined, badDebtNo: "", maxRecoverable: 0, amount: 0, accountId: undefined, remark: "" })
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, badDebtNo: undefined, customerId: undefined, status: undefined },
  rules: {
    receivableId: [{ required: true, message: "请选择关联应收单", trigger: "change" }],
    amount: [{ required: true, message: "坏账金额不能为空", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

async function handleCustomerChange(cid) {
  openReceivables.value = await fetchOpenReceivables(cid)
}

function handleArSelect(arId) {
  const ar = openReceivables.value.find(r => Number(r.receivableId) === Number(arId))
  if (ar) {
    form.value.customerId = ar.customerId
    if (!form.value.amount || Number(form.value.amount) <= 0) {
      form.value.amount = Number(ar.unreceivedAmount || 0)
    }
  }
}

function getList() {
  loading.value = true
  listBadDebt(queryParams.value).then(res => {
    badDebtList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { badDebtId: undefined, badDebtNo: undefined, receivableId: undefined, customerId: undefined, amount: undefined, badDebtDate: undefined, reason: undefined, status: "0", remark: undefined }
  proxy.resetForm("badDebtRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function canEditRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }
function canConfirmRow(r) { return !!r && r.status === "0" }
function canUnconfirmRow(r) { return !!r && r.status === "1" && Number(r.recoveredAmount || 0) <= 0 }
function canRecoverRow(r) {
  return !!r && r.status === "1" && (Number(r.amount || 0) - Number(r.recoveredAmount || 0)) > 0.001
}

function handleUnconfirm(row) {
  proxy.$modal.confirm(`确认反确认坏账单"${row.badDebtNo}"？系统将恢复应收账款坏账核销金额并退回草稿。`).then(() => unconfirmBadDebt(row.badDebtId)).then(() => {
    proxy.$modal.msgSuccess("坏账反确认成功"); getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printFinancialDocument({
    title: "坏账核销审批单",
    docNo: row.badDebtNo,
    amount: row.amount,
    metaList: [
      { label: "坏账单号", value: row.badDebtNo },
      { label: "客户名称", value: getCustomerName(row.customerId) },
      { label: "坏账日期", value: proxy.parseTime(row.badDebtDate, "{y}-{m}-{d}") },
      { label: "已收回金额", value: `￥${Number(row.recoveredAmount || 0).toFixed(2)}` },
      { label: "核销原因", value: row.reason || "-" },
      { label: "状态", value: getStatusLabel(row.status) }
    ],
    remark: row.remark,
    createBy: row.createBy
  })
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.badDebtId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}

function handleConfirm(row) {
  proxy.$modal.confirm(`确认核销坏账单"${row.badDebtNo}"并同步冲减应收单吗？`).then(() => confirmBadDebt(row.badDebtId)).then(() => {
    proxy.$modal.msgSuccess("坏账核销确认成功"); getList()
  }).catch(() => {})
}

function openRecoverDialog(row) {
  const rem = Number((Number(row.amount || 0) - Number(row.recoveredAmount || 0)).toFixed(2))
  Object.assign(recoverForm, { badDebtId: row.badDebtId, badDebtNo: row.badDebtNo, maxRecoverable: rem, amount: rem, accountId: undefined, remark: "" })
  recoverOpen.value = true
}

function submitRecover() {
  if (!recoverForm.accountId || !recoverForm.amount || recoverForm.amount <= 0) {
    proxy.$modal.msgError("请填写收回金额并选择入账资金账户")
    return
  }
  recoverBadDebt(recoverForm.badDebtId, {
    recoverAmount: recoverForm.amount,
    fundAccountId: recoverForm.accountId,
    remark: recoverForm.remark
  }).then(() => {
    proxy.$modal.msgSuccess("坏账收回成功，已入账资金账户并生成凭证事件")
    recoverOpen.value = false
    getList()
  })
}

async function handleAdd() {
  reset()
  openReceivables.value = await fetchOpenReceivables()
  open.value = true; title.value = "新增坏账单"
}

function handleUpdate(row) {
  reset()
  getBadDebt(row.badDebtId || selectedIds.value).then(async res => {
    form.value = res.data
    form.value.amount = Number(form.value.amount || 0)
    openReceivables.value = await fetchOpenReceivables(form.value.customerId)
    open.value = true; title.value = "修改坏账单"
  })
}

function submitForm() {
  proxy.$refs["badDebtRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.badDebtId ? updateBadDebt(form.value) : addBadDebt(form.value)
    fn.then(() => { proxy.$modal.msgSuccess(form.value.badDebtId ? "修改成功" : "新增成功"); open.value = false; getList() })
  })
}

function handleDelete(row) {
  const ids = row.badDebtId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中坏账单？").then(() => delBadDebt(ids)).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {})
}

loadCustomers(); loadFundAccounts(); getList()
</script>
