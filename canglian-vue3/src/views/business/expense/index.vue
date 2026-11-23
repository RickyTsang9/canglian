<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="费用单号" prop="expenseNo">
        <el-input
          v-model="queryParams.expenseNo"
          placeholder="请输入费用单号"
          clearable
          style="width: 155px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="费用类别" prop="expenseType">
        <el-select v-model="queryParams.expenseType" placeholder="请选择类别" clearable filterable style="width: 145px">
          <el-option
            v-for="cat in expenseCategoryOptions"
            :key="cat.value"
            :label="cat.label"
            :value="cat.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="资金账户" prop="fundAccountId">
        <el-select v-model="queryParams.fundAccountId" placeholder="请选择账户" clearable filterable style="width: 150px">
          <el-option v-for="acc in fundAccountOptions" :key="acc.accountId" :label="acc.accountName" :value="acc.accountId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="费用状态" clearable style="width: 115px">
          <el-option
            v-for="dict in expenseStatusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['business:expense:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:expense:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:expense:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="expenseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" align="center" />
      <el-table-column label="费用单号" align="center" prop="expenseNo" min-width="142" show-overflow-tooltip />
      <el-table-column label="费用类别" align="center" width="108">
        <template #default="scope">
          <el-tag size="small" effect="plain">{{ getExpenseTypeLabel(scope.row.expenseType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="费用金额" align="right" header-align="right" prop="amount" width="108">
        <template #default="scope">￥{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="费用日期" align="center" prop="expenseDate" width="108">
        <template #default="scope">
          <span>{{ parseTime(scope.row.expenseDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="资金账户" align="center" min-width="105" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getFundAccountName(scope.row.fundAccountId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="78">
        <template #default="scope">
          <el-tag size="small" :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="158">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="215" align="center" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:expense:edit']">修改</el-button>
            <el-button v-if="canConfirmRow(scope.row)" link type="success" icon="CircleCheck" @click="handleConfirm(scope.row)" v-hasPermi="['business:expense:confirm']">确认</el-button>
            <el-button v-if="scope.row.status === '1'" link type="warning" icon="RefreshLeft" @click="handleUnconfirm(scope.row)" v-hasPermi="['business:expense:confirm']">反确认</el-button>
            <el-button v-if="canDeleteRow(scope.row)" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:expense:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="expenseRef" :model="form" :rules="rules" label-width="95px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="费用单号" prop="expenseNo">
              <el-input v-model="form.expenseNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用类别" prop="expenseType">
              <el-select v-model="form.expenseType" placeholder="请选择费用类别" filterable style="width: 100%">
                <el-option
                  v-for="cat in expenseCategoryOptions"
                  :key="cat.value"
                  :label="cat.label"
                  :value="cat.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="费用金额" prop="amount">
              <el-input-number v-model="form.amount" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用日期" prop="expenseDate">
              <el-date-picker v-model="form.expenseDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择费用日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="payChannel">
              <el-select v-model="form.payChannel" placeholder="请选择支付方式" clearable style="width: 100%">
                <el-option label="银行转账" value="bank_transfer" />
                <el-option label="现金支付" value="cash" />
                <el-option label="微信支付" value="wechat" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资金账户" prop="fundAccountId">
              <el-select v-model="form.fundAccountId" placeholder="请选择资金账户" filterable style="width: 100%">
                <el-option v-for="acc in activeFundAccountOptions" :key="acc.accountId" :label="acc.accountName" :value="acc.accountId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" disabled>
            <el-radio
              v-for="dict in expenseStatusOptions"
              :key="dict.value"
              :value="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Expense">
import { listExpense, addExpense, delExpense, getExpense, updateExpense, confirmExpense, unconfirmExpense } from "@/api/business/expense"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  fundAccountOptions,
  activeFundAccountOptions,
  loadFundAccounts,
  fetchExpenseCategoryOptions,
  getFundAccountName
} = useBusinessOptions()

const expenseStatusOptions = [
  { label: "草稿", value: "0", tagType: "info" },
  { label: "已确认", value: "1", tagType: "success" }
]

const builtinExpenseTypeLabels = {
  logistics: "物流运费",
  freight: "物流运费",
  rent: "仓储房租",
  warehouse_rent: "仓储房租",
  office: "办公费用",
  salary: "人员薪资",
  utility: "水电物业",
  marketing: "市场推广",
  travel: "差旅费",
  other: "其他期间费用"
}

const rawExpenseCategories = ref([])

const expenseCategoryOptions = computed(() => {
  const seen = new Set()
  const list = []
  for (const cat of rawExpenseCategories.value) {
    const val = cat.categoryCode || cat.categoryName
    if (!val || seen.has(val)) continue
    seen.add(val)
    list.push({ value: val, label: cat.categoryName || val })
  }
  if (!list.length) {
    for (const [k, v] of Object.entries(builtinExpenseTypeLabels)) {
      if (!seen.has(k)) {
        seen.add(k)
        list.push({ value: k, label: v })
      }
    }
  }
  return list
})

function getExpenseTypeLabel(val) {
  if (!val) return "-"
  const match = rawExpenseCategories.value.find(c => c.categoryCode === val || c.categoryName === val)
  if (match) return match.categoryName
  return builtinExpenseTypeLabels[String(val).toLowerCase()] || val
}

function getStatusLabel(val) {
  const item = expenseStatusOptions.find(d => d.value === String(val ?? ""))
  return item ? item.label : (val || "-")
}

function getStatusTagType(val) {
  const item = expenseStatusOptions.find(d => d.value === String(val ?? ""))
  return item ? item.tagType : "info"
}

const expenseList = ref([])
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
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    expenseNo: undefined,
    expenseType: undefined,
    fundAccountId: undefined,
    status: undefined
  },
  rules: {
    expenseType: [{ required: true, message: "请选择费用类别", trigger: "change" }],
    amount: [{ required: true, message: "费用金额不能为空", trigger: "blur" }],
    fundAccountId: [{ required: true, message: "请选择资金账户", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listExpense(queryParams.value).then(response => {
    expenseList.value = response.rows || []
    total.value = response.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    expenseId: undefined,
    expenseNo: undefined,
    expenseType: undefined,
    amount: 0,
    expenseDate: new Date().toISOString().slice(0, 10),
    payChannel: "bank_transfer",
    fundAccountId: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("expenseRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.expenseId)
  isSingleDisabled.value = selection.length !== 1 || !selection.every(item => canEditRow(item))
  isMultipleDisabled.value = !selection.length || !selection.every(item => canDeleteRow(item))
}

function canEditRow(row) {
  return row?.status !== "1"
}

function canDeleteRow(row) {
  return row?.status !== "1"
}

function canConfirmRow(row) {
  return row?.status !== "1"
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增费用支出单"
}

function handleUpdate(currentRow) {
  reset()
  const expenseId = currentRow.expenseId || selectedIds.value
  getExpense(expenseId).then(response => {
    form.value = response.data || {}
    form.value.amount = Number(form.value.amount || 0)
    open.value = true
    title.value = "修改费用支出单"
  })
}

function handleConfirm(row) {
  const expenseId = row.expenseId
  proxy.$modal.confirm("确认当前费用单吗？确认后将扣减所选资金账户余额并生成会计凭证事件。").then(function() {
    return confirmExpense(expenseId)
  }).then(() => {
    proxy.$modal.msgSuccess("确认成功")
    getList()
  }).catch(() => {})
}

function handleUnconfirm(row) {
  proxy.$modal.confirm(`确认反确认费用单"${row.expenseNo}"？将自动回补资金账户余额并退回草稿。`).then(function() {
    return unconfirmExpense(row.expenseId)
  }).then(() => {
    proxy.$modal.msgSuccess("反确认成功")
    getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printFinancialDocument({
    title: "费用报销/支出单",
    docNo: row.expenseNo,
    amount: row.amount,
    remark: row.remark,
    createBy: row.createBy,
    auditBy: row.updateBy,
    metaList: [
      { label: "费用单号", value: row.expenseNo },
      { label: "费用日期", value: proxy.parseTime(row.expenseDate, "{y}-{m}-{d}") },
      { label: "单据状态", value: getStatusLabel(row.status) },
      { label: "费用类别", value: getExpenseTypeLabel(row.expenseType) },
      { label: "支付方式", value: row.payChannel || "-" },
      { label: "资金账户", value: getFundAccountName(row.fundAccountId) }
    ]
  })
}

function submitForm() {
  proxy.$refs["expenseRef"].validate(valid => {
    if (valid) {
      if (form.value.expenseId != undefined) {
        updateExpense(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addExpense(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const expenseIds = currentRow.expenseId || selectedIds.value
  proxy.$modal.confirm('是否确认删除费用编号为"' + expenseIds + '"的数据项？').then(function() {
    return delExpense(expenseIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

Promise.all([
  loadFundAccounts(),
  fetchExpenseCategoryOptions().then(list => { rawExpenseCategories.value = list })
]).finally(() => {
  getList()
})
</script>
