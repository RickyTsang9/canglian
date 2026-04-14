<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="费用单号" prop="expenseNo">
        <el-input
          v-model="queryParams.expenseNo"
          placeholder="请输入费用单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="费用类型" prop="expenseType">
        <el-input
          v-model="queryParams.expenseType"
          placeholder="请输入费用类型"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资金账户" prop="fundAccountId">
        <el-input
          v-model="queryParams.fundAccountId"
          placeholder="请输入资金账户编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="费用状态" clearable style="width: 200px">
          <el-option
            v-for="dict in sys_normal_disable"
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
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="费用编号" align="center" prop="expenseId" />
      <el-table-column label="费用单号" align="center" prop="expenseNo" />
      <el-table-column label="费用类型" align="center" prop="expenseType" />
      <el-table-column label="费用金额" align="center" prop="amount" />
      <el-table-column label="费用日期" align="center" prop="expenseDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.expenseDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="资金账户" align="center" prop="fundAccountId" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:expense:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:expense:remove']">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" @click="handleConfirm(scope.row)" v-hasPermi="['business:expense:confirm']">确认</el-button>
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
      <el-form ref="expenseRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="费用单号" prop="expenseNo">
              <el-input v-model="form.expenseNo" placeholder="请输入费用单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用类型" prop="expenseType">
              <el-input v-model="form.expenseType" placeholder="请输入费用类型" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="费用金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入费用金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用日期" prop="expenseDate">
              <el-date-picker v-model="form.expenseDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择费用日期"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="payChannel">
              <el-input v-model="form.payChannel" placeholder="请输入支付方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资金账户" prop="fundAccountId">
              <el-input v-model="form.fundAccountId" placeholder="请输入资金账户编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
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
import { listExpense, addExpense, delExpense, getExpense, updateExpense, confirmExpense } from "@/api/business/expense"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

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
    expenseNo: [{ required: true, message: "费用单号不能为空", trigger: "blur" }],
    expenseType: [{ required: true, message: "费用类型不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listExpense(queryParams.value).then(response => {
    expenseList.value = response.rows
    total.value = response.total
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
    amount: undefined,
    expenseDate: undefined,
    payChannel: undefined,
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
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加费用"
}

function handleUpdate(currentRow) {
  reset()
  const expenseId = currentRow.expenseId || selectedIds.value
  getExpense(expenseId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改费用"
  })
}

function handleConfirm(row) {
  const expenseId = row.expenseId
  proxy.$modal.confirm("确认当前费用单吗？").then(function() {
    return confirmExpense(expenseId)
  }).then(() => {
    proxy.$modal.msgSuccess("确认成功")
    getList()
  }).catch(() => {})
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

getList()
</script>
