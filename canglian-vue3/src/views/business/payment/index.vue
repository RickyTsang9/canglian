<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="付款单号" prop="paymentNo">
        <el-input v-model="queryParams.paymentNo" placeholder="请输入付款单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="queryParams.supplierId" placeholder="请选择供应商" clearable filterable style="width: 200px">
          <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="单据状态" clearable style="width: 130px">
          <el-option label="草稿" value="0" />
          <el-option label="已确认" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:payment:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:payment:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:payment:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:payment:export', 'business:payment:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="paymentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" align="center" />
      <el-table-column label="付款单号" align="center" prop="paymentNo" min-width="142" show-overflow-tooltip />
      <el-table-column label="供应商" align="center" min-width="125" show-overflow-tooltip>
        <template #default="scope">{{ getSupplierName(scope.row.supplierId) }}</template>
      </el-table-column>
      <el-table-column label="关联应付" align="center" min-width="136" show-overflow-tooltip>
        <template #default="scope">{{ getPayableNo(scope.row.payableId) }}</template>
      </el-table-column>
      <el-table-column label="资金账户" align="center" min-width="105" show-overflow-tooltip>
        <template #default="scope">{{ getFundAccountName(scope.row.fundAccountId) }}</template>
      </el-table-column>
      <el-table-column label="付款金额" align="right" header-align="right" prop="amount" width="102">
        <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="付款日期" align="center" width="106">
        <template #default="scope">{{ parseTime(scope.row.paymentDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.status === '1' ? 'success' : 'info'">{{ scope.row.status === '1' ? '已确认' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="auditBy" width="80">
        <template #default="scope">{{ scope.row.auditBy || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="225" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button v-if="scope.row.status !== '1'" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:payment:edit']">修改</el-button>
            <el-button v-if="scope.row.status !== '1'" link type="success" icon="CircleCheck" @click="handleConfirm(scope.row)" v-hasPermi="['business:payment:confirm']">确认</el-button>
            <el-button v-if="scope.row.status === '1'" link type="warning" icon="RefreshLeft" @click="handleUnconfirm(scope.row)" v-hasPermi="['business:payment:confirm']">反确认</el-button>
            <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
            <el-button v-if="scope.row.status !== '1'" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:payment:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="paymentRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="付款单号" prop="paymentNo">
              <el-input v-model="form.paymentNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable style="width: 100%" @change="handleSupplierChange">
                <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="关联应付单" prop="payableId">
              <el-select v-model="form.payableId" placeholder="按供应商过滤（可选）" clearable filterable style="width: 100%" @change="handleApSelect">
                <el-option
                  v-for="p in openPayables"
                  :key="p.payableId"
                  :label="`${p.payableNo} | 未付￥${Number(p.unpaidAmount || 0).toFixed(2)}`"
                  :value="p.payableId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款金额" prop="amount">
              <el-input-number v-model="form.amount" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="付款日期" prop="paymentDate">
              <el-date-picker v-model="form.paymentDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择付款日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付渠道" prop="payChannel">
              <el-select v-model="form.payChannel" placeholder="请选择" allow-create filterable style="width: 100%">
                <el-option label="银行转账" value="bank" />
                <el-option label="微信支付" value="wechat" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="现金" value="cash" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="出账资金账户" prop="fundAccountId">
              <el-select v-model="form.fundAccountId" placeholder="请选择出账资金账户" filterable style="width: 100%">
                <el-option v-for="acc in activeFundAccountOptions" :key="acc.accountId" :label="acc.accountName" :value="acc.accountId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">保存草稿</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Payment">
import { listPayment, addPayment, delPayment, getPayment, updatePayment, confirmPayment, unconfirmPayment } from "@/api/business/payment"
import { listPayable } from "@/api/business/payable"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  supplierOptions, fundAccountOptions, activeFundAccountOptions,
  loadSuppliers, loadFundAccounts, fetchOpenPayables,
  getSupplierName, getFundAccountName
} = useBusinessOptions()

const payableNoMap = reactive({})
function loadPayableNos() {
  listPayable({ pageNum: 1, pageSize: 500 }).then(r => {
    ;(r.rows || []).forEach(it => { payableNoMap[it.payableId] = it.payableNo })
  }).catch(() => {})
}
function getPayableNo(id) {
  if (!id) return "-"
  return payableNoMap[id] || `AP #${id}`
}

function handlePrint(row) {
  printFinancialDocument({
    title: "供应商付款单",
    docNo: row.paymentNo,
    amount: row.amount,
    metaList: [
      { label: "付款单号", value: row.paymentNo },
      { label: "供应商", value: getSupplierName(row.supplierId) },
      { label: "出账账户", value: getFundAccountName(row.fundAccountId) },
      { label: "付款日期", value: proxy.parseTime(row.paymentDate, "{y}-{m}-{d}") },
      { label: "支付渠道", value: row.payChannel || "-" },
      { label: "单据状态", value: row.status === "1" ? "已确认出账" : "草稿" }
    ],
    remark: row.remark,
    createBy: row.createBy,
    auditBy: row.auditBy
  })
}

const paymentList = ref([])
const openPayables = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, paymentNo: undefined, supplierId: undefined, status: undefined },
  rules: {
    supplierId: [{ required: true, message: "请选择供应商", trigger: "change" }],
    fundAccountId: [{ required: true, message: "请选择出账资金账户", trigger: "change" }],
    amount: [{ required: true, message: "请输入付款金额", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

async function handleSupplierChange(sid) {
  form.value.payableId = undefined
  openPayables.value = sid ? await fetchOpenPayables(sid) : []
}

function handleApSelect(apId) {
  const ap = openPayables.value.find(p => Number(p.payableId) === Number(apId))
  if (ap && (!form.value.amount || Number(form.value.amount) <= 0)) {
    form.value.amount = Number(ap.unpaidAmount || 0)
  }
}

function getList() {
  loading.value = true
  listPayment(queryParams.value).then(res => {
    paymentList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { paymentId: undefined, paymentNo: undefined, supplierId: undefined, payableId: undefined, fundAccountId: undefined, amount: undefined, paymentDate: undefined, payChannel: "bank", remark: undefined }
  openPayables.value = []
  proxy.resetForm("paymentRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.paymentId)
  isSingleDisabled.value = selection.length !== 1 || selection[0].status === "1"
  isMultipleDisabled.value = !selection.length || selection.some(i => i.status === "1")
}

function handleAdd() { reset(); open.value = true; title.value = "新增付款单（草稿）" }

function handleUpdate(row) {
  reset()
  getPayment(row.paymentId || selectedIds.value).then(async res => {
    form.value = res.data
    form.value.amount = Number(form.value.amount || 0)
    if (form.value.supplierId) openPayables.value = await fetchOpenPayables(form.value.supplierId)
    open.value = true; title.value = "修改付款单"
  })
}

function handleConfirm(row) {
  proxy.$modal.confirm(`确认出账付款单"${row.paymentNo}"（金额￥${row.amount}）？将扣减资金余额、核减应付并记录资金流水。`).then(() => confirmPayment(row.paymentId)).then(() => {
    proxy.$modal.msgSuccess("付款确认出账成功"); getList()
  }).catch(() => {})
}

function handleUnconfirm(row) {
  proxy.$modal.confirm(`确认反确认付款单"${row.paymentNo}"？系统将回补资金余额并回退应付已付金额。`).then(() => unconfirmPayment(row.paymentId)).then(() => {
    proxy.$modal.msgSuccess("反确认成功，单据已退回草稿"); getList()
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs["paymentRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.paymentId ? updatePayment(form.value) : addPayment(form.value)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.paymentId ? "修改成功" : "草稿已保存，请点击“确认出账”生效")
      open.value = false; getList()
    })
  })
}

function handleDelete(row) {
  const ids = row.paymentId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中付款单？").then(() => delPayment(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getList()
  }).catch(() => {})
}

loadFundAccounts(); loadSuppliers(); loadPayableNos(); getList()

function handleExport() {
  proxy.download("business/payment/export", { ...queryParams.value }, `付款单_${new Date().getTime()}.xlsx`)
}
</script>
