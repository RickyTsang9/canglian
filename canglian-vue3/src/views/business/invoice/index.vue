<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="发票单号" prop="invoiceNo">
        <el-input v-model="queryParams.invoiceNo" placeholder="请输入发票单号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item v-if="isSaleInvoice" label="客户编号" prop="customerId">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item v-else label="供应商编号" prop="supplierId">
        <el-input v-model="queryParams.supplierId" placeholder="请输入供应商编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务状态" prop="bizStatus">
        <el-select v-model="queryParams.bizStatus" placeholder="请选择业务状态" clearable style="width: 200px">
          <el-option v-for="statusOption in bizStatusOptions" :key="statusOption.value" :label="statusOption.label" :value="statusOption.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:invoice:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:invoice:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:invoice:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="invoiceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="发票编号" align="center" prop="invoiceId" />
      <el-table-column label="发票单号" align="center" prop="invoiceNo" />
      <el-table-column label="发票类型" align="center" prop="invoiceType" />
      <el-table-column v-if="isSaleInvoice" label="客户编号" align="center" prop="customerId" />
      <el-table-column v-else label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="来源单号" align="center" prop="sourceBillNo" />
      <el-table-column label="含税金额" align="center" prop="invoiceAmount" />
      <el-table-column label="税率" align="center" prop="taxRate" />
      <el-table-column label="税额" align="center" prop="taxAmount" />
      <el-table-column label="不含税金额" align="center" prop="untaxedAmount" />
      <el-table-column label="业务状态" align="center" prop="bizStatus">
        <template #default="scope">
          <dict-tag :options="bizStatusOptions" :value="scope.row.bizStatus" />
        </template>
      </el-table-column>
      <el-table-column label="确认时间" align="center" prop="confirmTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.confirmTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:invoice:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" :disabled="!canEditRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:invoice:remove']">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" :disabled="!canConfirmRow(scope.row)" @click="handleConfirm(scope.row)" v-hasPermi="['business:invoice:confirm']">确认</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="720px" append-to-body>
      <el-form ref="invoiceRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="发票单号" prop="invoiceNo">
              <el-input v-model="form.invoiceNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="isSaleInvoice" label="客户编号" prop="customerId">
              <el-input v-model="form.customerId" placeholder="请输入客户编号" />
            </el-form-item>
            <el-form-item v-else label="供应商编号" prop="supplierId">
              <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源单号" prop="sourceBillNo">
              <el-input v-model="form.sourceBillNo" placeholder="请输入来源单号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开票日期" prop="invoiceDate">
              <el-date-picker v-model="form.invoiceDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开票日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="含税金额" prop="invoiceAmount">
              <el-input-number v-model="form.invoiceAmount" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="税率" prop="taxRate">
              <el-input-number v-model="form.taxRate" controls-position="right" :min="0" :step="0.01" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务状态" prop="bizStatus">
              <el-radio-group v-model="form.bizStatus" disabled>
                <el-radio v-for="statusOption in bizStatusOptions" :key="statusOption.value" :value="statusOption.value">{{ statusOption.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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

<script setup name="Invoice">
import { useRoute } from "vue-router"
import { listInvoice, getInvoice, addInvoice, updateInvoice, confirmInvoice, delInvoice } from "@/api/business/invoice"

const route = useRoute()
const invoiceType = computed(() => route.query.invoiceType || "sale")
const isSaleInvoice = computed(() => invoiceType.value === "sale")
const invoiceTitle = computed(() => isSaleInvoice.value ? "销售发票" : "采购发票")

const { proxy } = getCurrentInstance()

const invoiceList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const bizStatusOptions = [
  { label: "草稿", value: "draft" },
  { label: "已确认", value: "confirmed" }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    invoiceNo: undefined,
    invoiceType: undefined,
    customerId: undefined,
    supplierId: undefined,
    bizStatus: undefined
  },
  rules: {
    customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }],
    supplierId: [{ required: true, message: "供应商编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 查询发票登记列表
function getList() {
  loading.value = true
  queryParams.value.invoiceType = invoiceType.value
  listInvoice(queryParams.value).then(response => {
    invoiceList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    invoiceId: undefined,
    invoiceNo: undefined,
    invoiceType: invoiceType.value,
    customerId: undefined,
    supplierId: undefined,
    sourceBillType: undefined,
    sourceBillId: undefined,
    sourceBillNo: undefined,
    businessDate: undefined,
    invoiceDate: undefined,
    invoiceAmount: 0,
    taxRate: 0,
    bizStatus: "draft",
    remark: undefined
  }
  proxy.resetForm("invoiceRef")
}

// 搜索按钮操作
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置按钮操作
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.invoiceId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(item => !canEditRow(item))
}

// 判断是否允许修改
function canEditRow(currentRow) {
  return !!currentRow && currentRow.bizStatus === "draft"
}

// 判断是否允许确认
function canConfirmRow(currentRow) {
  return canEditRow(currentRow)
}

// 新增按钮操作
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加" + invoiceTitle.value
}

// 修改按钮操作
function handleUpdate(row) {
  reset()
  const invoiceId = row.invoiceId || selectedIds.value
  getInvoice(invoiceId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改" + invoiceTitle.value
  })
}

// 确认按钮操作
function handleConfirm(row) {
  proxy.$modal.confirm('是否确认当前"' + invoiceTitle.value + '"？').then(function() {
    return confirmInvoice(row.invoiceId)
  }).then(() => {
    proxy.$modal.msgSuccess("确认成功")
    getList()
  }).catch(() => {})
}

// 提交按钮
function submitForm() {
  proxy.$refs["invoiceRef"].validate(valid => {
    if (valid) {
      form.value.invoiceType = invoiceType.value
      if (!isSaleInvoice.value) {
        form.value.customerId = undefined
      } else {
        form.value.supplierId = undefined
      }
      if (form.value.invoiceId !== undefined) {
        updateInvoice(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addInvoice(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除按钮操作
function handleDelete(row) {
  const invoiceIds = row.invoiceId || selectedIds.value
  proxy.$modal.confirm('是否确认删除发票编号为"' + invoiceIds + '"的数据项？').then(function() {
    return delInvoice(invoiceIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

watch(invoiceType, () => {
  reset()
  getList()
})

getList()
</script>
