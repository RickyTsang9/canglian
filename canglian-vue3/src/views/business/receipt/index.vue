<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="收款单号" prop="receiptNo">
        <el-input v-model="queryParams.receiptNo" placeholder="请输入收款单号" clearable style="width: 190px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 200px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:receipt:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:receipt:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:receipt:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="receiptList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="收款单号" align="center" prop="receiptNo" min-width="150" />
      <el-table-column label="客户" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="关联应收" align="center" width="110">
        <template #default="scope">{{ scope.row.receivableId ? `AR #${scope.row.receivableId}` : '-' }}</template>
      </el-table-column>
      <el-table-column label="资金账户" align="center" min-width="125" show-overflow-tooltip>
        <template #default="scope">{{ getFundAccountName(scope.row.fundAccountId) }}</template>
      </el-table-column>
      <el-table-column label="收款金额" align="right" prop="amount" width="110" />
      <el-table-column label="收款日期" align="center" width="115">
        <template #default="scope">{{ parseTime(scope.row.receiptDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">{{ scope.row.status === '1' ? '已确认' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="auditBy" width="90" />
      <el-table-column label="操作" width="290" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" :disabled="scope.row.status === '1'" @click="handleUpdate(scope.row)" v-hasPermi="['business:receipt:edit']">修改</el-button>
          <el-button link type="success" icon="CircleCheck" v-if="scope.row.status !== '1'" @click="handleConfirm(scope.row)" v-hasPermi="['business:receipt:confirm']">确认入账</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="scope.row.status === '1'" @click="handleUnconfirm(scope.row)" v-hasPermi="['business:receipt:confirm']">反确认</el-button>
          <el-button link type="danger" icon="Delete" :disabled="scope.row.status === '1'" @click="handleDelete(scope.row)" v-hasPermi="['business:receipt:remove']">删除</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="receiptRef" :model="form" :rules="rules" label-width="95px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="收款单号" prop="receiptNo">
              <el-input v-model="form.receiptNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%" @change="handleCustomerChange">
                <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="关联应收单" prop="receivableId">
              <el-select v-model="form.receivableId" placeholder="按客户自动过滤未收应收（可选）" clearable filterable style="width: 100%" @change="handleArSelect">
                <el-option
                  v-for="r in openReceivables"
                  :key="r.receivableId"
                  :label="`${r.receivableNo} | 未收￥${Number(r.unreceivedAmount || 0).toFixed(2)}`"
                  :value="r.receivableId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收款金额" prop="amount">
              <el-input-number v-model="form.amount" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="收款日期" prop="receiptDate">
              <el-date-picker v-model="form.receiptDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
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
        <el-row>
          <el-col :span="12">
            <el-form-item label="入账资金账户" prop="fundAccountId">
              <el-select v-model="form.fundAccountId" placeholder="请选择入账资金账户（必填）" filterable style="width: 100%">
                <el-option v-for="acc in activeFundAccountOptions" :key="acc.accountId" :label="`${acc.accountNo || ''} - ${acc.accountName}`" :value="acc.accountId" />
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

<script setup name="Receipt">
import { listReceipt, addReceipt, delReceipt, getReceipt, updateReceipt, confirmReceipt, unconfirmReceipt } from "@/api/business/receipt"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  customerOptions, fundAccountOptions, activeFundAccountOptions,
  loadCustomers, loadFundAccounts, fetchOpenReceivables,
  getCustomerName, getFundAccountName
} = useBusinessOptions()

function handlePrint(row) {
  printFinancialDocument({
    title: Number(row.amount || 0) < 0 ? "红字退款单" : "客户收款单",
    docNo: row.receiptNo,
    amount: row.amount,
    metaList: [
      { label: "收款单号", value: row.receiptNo },
      { label: "客户名称", value: getCustomerName(row.customerId) },
      { label: "入账账户", value: getFundAccountName(row.fundAccountId) },
      { label: "收款日期", value: proxy.parseTime(row.receiptDate, "{y}-{m}-{d}") },
      { label: "支付渠道", value: row.payChannel || "-" },
      { label: "单据状态", value: row.status === "1" ? "已确认入账" : "草稿" }
    ],
    remark: row.remark,
    createBy: row.createBy,
    auditBy: row.auditBy
  })
}

const receiptList = ref([])
const openReceivables = ref([])
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
  queryParams: { pageNum: 1, pageSize: 10, receiptNo: undefined, customerId: undefined, status: undefined },
  rules: {
    customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
    fundAccountId: [{ required: true, message: "请选择入账资金账户", trigger: "change" }],
    amount: [{ required: true, message: "请输入收款金额", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

async function handleCustomerChange(cid) {
  form.value.receivableId = undefined
  openReceivables.value = cid ? await fetchOpenReceivables(cid) : []
}

function handleArSelect(arId) {
  const ar = openReceivables.value.find(r => Number(r.receivableId) === Number(arId))
  if (ar && (!form.value.amount || Number(form.value.amount) <= 0)) {
    form.value.amount = Number(ar.unreceivedAmount || 0)
  }
}

function getList() {
  loading.value = true
  listReceipt(queryParams.value).then(res => {
    receiptList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { receiptId: undefined, receiptNo: undefined, customerId: undefined, receivableId: undefined, fundAccountId: undefined, amount: undefined, receiptDate: undefined, payChannel: "bank", remark: undefined }
  openReceivables.value = []
  proxy.resetForm("receiptRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.receiptId)
  isSingleDisabled.value = selection.length !== 1 || selection[0].status === "1"
  isMultipleDisabled.value = !selection.length || selection.some(i => i.status === "1")
}

function handleAdd() { reset(); open.value = true; title.value = "新增收款单（草稿）" }

function handleUpdate(row) {
  reset()
  getReceipt(row.receiptId || selectedIds.value).then(async res => {
    form.value = res.data
    form.value.amount = Number(form.value.amount || 0)
    if (form.value.customerId) openReceivables.value = await fetchOpenReceivables(form.value.customerId)
    open.value = true; title.value = "修改收款单"
  })
}

function handleConfirm(row) {
  proxy.$modal.confirm(`确认入账收款单"${row.receiptNo}"（金额￥${row.amount}）？将实时更新资金余额、核减应收并记录资金流水。`).then(() => confirmReceipt(row.receiptId)).then(() => {
    proxy.$modal.msgSuccess("收款确认入账成功"); getList()
  }).catch(() => {})
}

function handleUnconfirm(row) {
  proxy.$modal.confirm(`确认反确认收款单"${row.receiptNo}"？系统将回退资金余额与应收已收金额。`).then(() => unconfirmReceipt(row.receiptId)).then(() => {
    proxy.$modal.msgSuccess("反确认成功，单据已退回草稿"); getList()
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs["receiptRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.receiptId ? updateReceipt(form.value) : addReceipt(form.value)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.receiptId ? "修改成功" : "草稿已保存，请点击“确认入账”生效")
      open.value = false; getList()
    })
  })
}

function handleDelete(row) {
  const ids = row.receiptId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中收款单？").then(() => delReceipt(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getList()
  }).catch(() => {})
}

loadFundAccounts(); loadCustomers(); getList()
</script>
