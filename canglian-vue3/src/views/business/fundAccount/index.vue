<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="资金账户台账" name="accounts">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
          <el-form-item label="账户编码" prop="accountNo">
            <el-input v-model="queryParams.accountNo" placeholder="请输入账户编码" clearable style="width: 190px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="账户名称" prop="accountName">
            <el-input v-model="queryParams.accountName" placeholder="请输入账户名称" clearable style="width: 190px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="账户状态" clearable style="width: 140px">
              <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:fundAccount:add']">新增账户</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Switch" @click="openTransferDialog" v-hasPermi="['business:fundAccount:transfer']">账户内部互转</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:fundAccount:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:fundAccount:remove']">删除</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </el-row>

        <el-table v-loading="loading" :data="fundAccountList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="46" align="center" />
          <el-table-column label="账户编码" align="center" prop="accountNo" width="102" show-overflow-tooltip />
          <el-table-column label="账户名称" align="center" prop="accountName" min-width="110" show-overflow-tooltip />
          <el-table-column label="账户类型" align="center" width="102">
            <template #default="scope">
              <el-tag size="small" effect="plain">{{ getAccountTypeLabel(scope.row.accountType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="开户行" align="center" prop="bankName" min-width="105" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.bankName || '-' }}</template>
          </el-table-column>
          <el-table-column label="银行账号" align="center" prop="bankAccount" min-width="145" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.bankAccount || '-' }}</template>
          </el-table-column>
          <el-table-column label="当前余额" align="right" header-align="right" prop="balance" width="122">
            <template #default="scope"><strong>￥{{ Number(scope.row.balance || 0).toFixed(2) }}</strong></template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status" width="74">
            <template #default="scope"><dict-tag :options="sys_normal_disable" :value="scope.row.status" /></template>
          </el-table-column>
          <el-table-column label="操作" width="232" align="center" fixed="right">
            <template #default="scope">
              <div class="table-action-cell">
                <el-button link type="success" icon="Tickets" @click="openLogDrawer(scope.row)">流水</el-button>
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:fundAccount:edit']">修改</el-button>
                <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:fundAccount:remove']">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-tab-pane>

      <el-tab-pane label="资金内部互转单" name="transfers">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="openTransferDialog" v-hasPermi="['business:fundAccount:transfer']">新建内部互转</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button icon="Refresh" @click="getTransferList">刷新</el-button>
          </el-col>
        </el-row>
        <el-table v-loading="transferLoading" :data="transferList" border>
          <el-table-column label="互转单号" align="center" prop="transferNo" min-width="140" show-overflow-tooltip />
          <el-table-column label="转出账户" align="center" min-width="105" show-overflow-tooltip>
            <template #default="scope">{{ getAccountName(scope.row.outAccountId) }}</template>
          </el-table-column>
          <el-table-column label="转入账户" align="center" min-width="105" show-overflow-tooltip>
            <template #default="scope">{{ getAccountName(scope.row.inAccountId) }}</template>
          </el-table-column>
          <el-table-column label="转账金额" align="right" header-align="right" prop="amount" width="108">
            <template #default="scope">￥{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="手续费" align="right" header-align="right" prop="feeAmount" width="90">
            <template #default="scope">￥{{ Number(scope.row.feeAmount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="转账日期" align="center" width="104">
            <template #default="scope">{{ parseTime(scope.row.transferDate, '{y}-{m}-{d}') }}</template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="78">
            <template #default="scope">
              <el-tag size="small" :type="scope.row.status === '1' ? 'success' : 'info'">{{ scope.row.status === '1' ? '已确认' : '草稿' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="115" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.remark || '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="235" fixed="right">
            <template #default="scope">
              <div class="table-action-cell">
                <el-button link type="info" icon="Printer" @click="handlePrintTransfer(scope.row)">打印</el-button>
                <el-button v-if="scope.row.status === '0'" link type="success" icon="CircleCheck" @click="handleConfirmTransfer(scope.row)" v-hasPermi="['business:fundAccount:transfer']">确认</el-button>
                <el-button v-if="scope.row.status === '0'" link type="danger" icon="Delete" @click="handleDeleteTransfer(scope.row)" v-hasPermi="['business:fundAccount:transfer']">删除</el-button>
                <el-button v-if="scope.row.status === '1' && Number(scope.row.amount || 0) > 0" link type="warning" icon="RefreshLeft" @click="handleUnconfirmTransfer(scope.row)" v-hasPermi="['business:fundAccount:transferUnconfirm']">反确认</el-button>
                <el-button v-if="scope.row.status === '1' && Number(scope.row.amount || 0) > 0 && !(scope.row.remark || '').includes('[已红冲')" link type="danger" icon="RefreshLeft" @click="handleReverseTransfer(scope.row)" v-hasPermi="['business:fundAccount:transferUnconfirm']">红冲</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/修改资金账户弹窗 -->
    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="fundAccountRef" :model="form" :rules="rules" label-width="105px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="账户编码" prop="accountNo"><el-input v-model="form.accountNo" placeholder="请输入账户编码" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户名称" prop="accountName"><el-input v-model="form.accountName" placeholder="请输入账户名称" /></el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="账户类型" prop="accountType">
              <el-select v-model="form.accountType" placeholder="请选择账户类型" filterable style="width: 100%">
                <el-option label="银行账户" value="bank" />
                <el-option label="现金账户" value="cash" />
                <el-option label="微信账户" value="wechat" />
                <el-option label="支付宝账户" value="alipay" />
                <el-option label="线上第三方账户" value="online" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行" prop="bankName"><el-input v-model="form.bankName" placeholder="请输入开户行" /></el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccount"><el-input v-model="form.bankAccount" placeholder="请输入银行账号" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="币种" prop="currency"><el-input v-model="form.currency" placeholder="默认 CNY" /></el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="期初/当前余额" prop="balance">
              <el-input-number v-model="form.balance" :precision="2" controls-position="right" style="width: 100%" />
              <div style="font-size: 11px; color: #e6a23c; line-height: 1.4">提示：一旦产生收付款或互转流水，系统将自动锁定余额，禁止手工篡改。</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
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

    <!-- 资金内部互转弹窗 -->
    <el-dialog title="新建资金账户内部互转单" v-model="transferOpen" width="520px" append-to-body>
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="转出账户" required>
          <el-select v-model="transferForm.outAccountId" placeholder="请选择转出账户" filterable style="width: 100%">
            <el-option v-for="acc in activeAccounts" :key="acc.accountId" :label="`${acc.accountName} (余额:￥${Number(acc.balance || 0).toFixed(2)})`" :value="acc.accountId" />
          </el-select>
        </el-form-item>
        <el-form-item label="转入账户" required>
          <el-select v-model="transferForm.inAccountId" placeholder="请选择转入账户" filterable style="width: 100%">
            <el-option v-for="acc in activeAccounts" :key="acc.accountId" :label="`${acc.accountName} (余额:￥${Number(acc.balance || 0).toFixed(2)})`" :value="acc.accountId" />
          </el-select>
        </el-form-item>
        <el-form-item label="转账金额" required>
          <el-input-number v-model="transferForm.amount" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="手续费">
          <el-input-number v-model="transferForm.feeAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="转账日期">
          <el-date-picker v-model="transferForm.transferDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="transferForm.remark" placeholder="如：银行提现至备用金" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitTransfer">保存草稿</el-button>
        <el-button @click="transferOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 资金流水台账抽屉 -->
    <el-drawer v-model="logDrawerOpen" :title="`资金逐笔流水台账 - ${currentLogAccount.accountName || ''}`" size="920px">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
        <span>当前余额：<strong style="color: #2563eb; font-size: 16px">￥{{ Number(currentLogAccount.balance || 0).toFixed(2) }}</strong></span>
        <el-button type="warning" plain size="small" icon="Download" @click="exportLogs">导出流水Excel</el-button>
      </div>
      <el-table v-loading="logLoading" :data="logList" border size="small">
        <el-table-column label="时间" width="148" align="center">
          <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="业务类型" width="96" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain">{{ getFundBizTypeLabel(scope.row.bizType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源单号" prop="billNo" width="136" align="center" show-overflow-tooltip />
        <el-table-column label="方向" width="64" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.direction === '1' ? 'success' : 'danger'" size="small">{{ scope.row.direction === '1' ? '收入' : '支出' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变动金额" align="right" header-align="right" width="98">
          <template #default="scope">
            <span :style="scope.row.direction === '1' ? 'color:#10b981' : 'color:#ef4444'">
              {{ scope.row.direction === '1' ? '+' : '-' }}{{ Number(scope.row.changeAmount || 0).toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="变动前" align="right" header-align="right" width="96">
          <template #default="scope">{{ Number(scope.row.beforeBalance || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="变动后" align="right" header-align="right" width="96">
          <template #default="scope">{{ Number(scope.row.afterBalance || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="125" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="FundAccount">
import {
  listFundAccount, addFundAccount, delFundAccount, getFundAccount, updateFundAccount,
  listFundLogs, listFundTransfers, addFundTransfer, confirmFundTransfer, unconfirmFundTransfer, reverseFundTransfer, delFundTransfer
} from "@/api/business/fundAccount"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const accountTypeMap = {
  bank: "银行账户",
  cash: "现金账户",
  wechat: "微信账户",
  alipay: "支付宝账户",
  online: "线上第三方账户"
}

const fundBizTypeMap = {
  receipt: "销售收款",
  payment: "采购付款",
  expense: "期间费用",
  transfer_out: "内部转出",
  transfer_in: "内部转入",
  transfer_fee: "互转手续费",
  transfer_unconfirm: "互转反确认",
  transfer_reverse: "互转红冲",
  unconfirm: "反确认冲回",
  outbound_return: "退货退款",
  bad_debt_recover: "坏账收回",
  opening: "期初余额"
}

function getAccountTypeLabel(type) {
  return accountTypeMap[String(type || "").toLowerCase()] || type || "-"
}

function getFundBizTypeLabel(type) {
  return fundBizTypeMap[String(type || "").toLowerCase()] || type || "-"
}

const activeTab = ref("accounts")
const fundAccountList = ref([])
const allAccounts = ref([])
const activeAccounts = computed(() => allAccounts.value.filter(a => String(a.status ?? "0") === "0"))
const transferList = ref([])
const transferLoading = ref(false)
const open = ref(false)
const transferOpen = ref(false)
const logDrawerOpen = ref(false)
const logLoading = ref(false)
const currentLogAccount = ref({})
const logList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const transferForm = reactive({ outAccountId: undefined, inAccountId: undefined, amount: 0, feeAmount: 0, transferDate: undefined, remark: "" })

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, accountNo: undefined, accountName: undefined, status: undefined },
  rules: {
    accountNo: [{ required: true, message: "账户编码不能为空", trigger: "blur" }],
    accountName: [{ required: true, message: "账户名称不能为空", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getAccountName(id) {
  const a = allAccounts.value.find(x => Number(x.accountId) === Number(id))
  return a ? a.accountName : `#${id}`
}

function loadAllAccounts() {
  listFundAccount({ pageNum: 1, pageSize: 500 }).then(res => { allAccounts.value = res.rows || [] }).catch(() => {})
}

function getList() {
  loading.value = true
  listFundAccount(queryParams.value).then(res => {
    fundAccountList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function getTransferList() {
  transferLoading.value = true
  listFundTransfers({ pageNum: 1, pageSize: 100 }).then(res => {
    transferList.value = res.rows || []
  }).finally(() => { transferLoading.value = false })
}

function openLogDrawer(row) {
  currentLogAccount.value = row
  logDrawerOpen.value = true
  logLoading.value = true
  listFundLogs({ accountId: row.accountId, pageNum: 1, pageSize: 200 }).then(res => {
    logList.value = res.rows || []
  }).finally(() => { logLoading.value = false })
}

function exportLogs() {
  proxy.download("business/fundAccount/logs/export", { accountId: currentLogAccount.value.accountId }, `fund_logs_${currentLogAccount.value.accountNo || currentLogAccount.value.accountId}.xlsx`)
}

function openTransferDialog() {
  loadAllAccounts()
  Object.assign(transferForm, {
    outAccountId: undefined,
    inAccountId: undefined,
    amount: 0,
    feeAmount: 0,
    transferDate: new Date().toISOString().slice(0, 10),
    remark: ""
  })
  transferOpen.value = true
}

function submitTransfer() {
  if (!transferForm.outAccountId || !transferForm.inAccountId || transferForm.outAccountId === transferForm.inAccountId) {
    proxy.$modal.msgError("请选择两个不同的转出与转入资金账户")
    return
  }
  if (!transferForm.amount || Number(transferForm.amount) <= 0) {
    proxy.$modal.msgError("转账金额必须大于0")
    return
  }
  addFundTransfer(transferForm).then(() => {
    proxy.$modal.msgSuccess("内部互转草稿已保存，请点击“确认转账”过账")
    transferOpen.value = false
    activeTab.value = "transfers"
    getTransferList()
  })
}

function handleConfirmTransfer(row) {
  proxy.$modal.confirm(`确认执行内部互转单"${row.transferNo}"（金额￥${row.amount}）？将原子扣减转出账户并增加转入账户。`).then(() => confirmFundTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("资金互转过账成功")
    getTransferList()
    getList()
    loadAllAccounts()
  }).catch(() => {})
}

function handleUnconfirmTransfer(row) {
  proxy.$modal.confirm(`确认反确认内部互转单"${row.transferNo}"？将反向恢复转出/转入账户余额（含手续费）并取消待生成手续费凭证。`).then(() => unconfirmFundTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("资金互转反确认成功")
    getTransferList()
    getList()
    loadAllAccounts()
  }).catch(() => {})
}

function handleReverseTransfer(row) {
  proxy.$modal.confirm(`确认红冲已确认的内部互转单"${row.transferNo}"（金额￥${row.amount}）？将生成负向红字互转单并反向冲回账户余额及凭证事件。`).then(() => reverseFundTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("资金互转红冲成功")
    getTransferList()
    getList()
    loadAllAccounts()
  }).catch(() => {})
}

function handleDeleteTransfer(row) {
  proxy.$modal.confirm(`确认删除互转草稿单"${row.transferNo}"？`).then(() => delFundTransfer(row.transferId)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getTransferList()
  }).catch(() => {})
}

function handlePrintTransfer(row) {
  printFinancialDocument({
    title: "资金内部互转单",
    docNo: row.transferNo,
    amount: row.amount,
    remark: row.remark,
    createBy: row.createBy,
    auditBy: row.auditBy,
    metaList: [
      { label: "互转单号", value: row.transferNo },
      { label: "转账日期", value: proxy.parseTime(row.transferDate, "{y}-{m}-{d}") },
      { label: "状态", value: row.status === "1" ? "已确认" : "草稿" },
      { label: "转出账户", value: getAccountName(row.outAccountId) },
      { label: "转入账户", value: getAccountName(row.inAccountId) },
      { label: "手续费", value: `￥${Number(row.feeAmount || 0).toFixed(2)}` }
    ]
  })
}

function cancel() { open.value = false; reset() }
function reset() {
  form.value = { accountId: undefined, accountNo: undefined, accountName: undefined, accountType: "bank", bankName: undefined, bankAccount: undefined, currency: "CNY", balance: 0, status: "0", remark: undefined }
  proxy.resetForm("fundAccountRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.accountId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}
function handleAdd() { reset(); open.value = true; title.value = "新增资金账户" }
function handleUpdate(row) {
  reset()
  getFundAccount(row.accountId || selectedIds.value).then(res => {
    form.value = res.data
    form.value.balance = Number(form.value.balance || 0)
    open.value = true; title.value = "修改资金账户"
  })
}
function submitForm() {
  proxy.$refs["fundAccountRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.accountId ? updateFundAccount(form.value) : addFundAccount(form.value)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.accountId ? "修改成功" : "新增成功")
      open.value = false; getList(); loadAllAccounts()
    })
  })
}
function handleDelete(row) {
  const ids = row.accountId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中资金账户？").then(() => delFundAccount(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getList(); loadAllAccounts()
  }).catch(() => {})
}

watch(activeTab, (tab) => { if (tab === "transfers") getTransferList() })

loadAllAccounts()
getList()
</script>
