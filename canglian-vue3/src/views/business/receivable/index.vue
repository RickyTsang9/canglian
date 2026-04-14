<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="应收单号" prop="receivableNo">
        <el-input
          v-model="queryParams.receivableNo"
          placeholder="请输入应收单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户编号" prop="customerId">
        <el-input
          v-model="queryParams.customerId"
          placeholder="请输入客户编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="应收状态" clearable style="width: 200px">
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
          v-hasPermi="['business:receivable:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:receivable:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:receivable:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="receivableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="应收编号" align="center" prop="receivableId" />
      <el-table-column label="应收单号" align="center" prop="receivableNo" />
      <el-table-column label="客户编号" align="center" prop="customerId" />
      <el-table-column label="单据类型" align="center" prop="billType" />
      <el-table-column label="单据编号" align="center" prop="billId" />
      <el-table-column label="应收金额" align="center" prop="amount" />
      <el-table-column label="已收金额" align="center" prop="receivedAmount" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="到期日期" align="center" prop="dueDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.dueDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:receivable:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:receivable:remove']">删除</el-button>
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
      <el-form ref="receivableRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="应收单号" prop="receivableNo">
              <el-input v-model="form.receivableNo" placeholder="请输入应收单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户编号" prop="customerId">
              <el-input v-model="form.customerId" placeholder="请输入客户编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单据类型" prop="billType">
              <el-input v-model="form.billType" placeholder="请输入单据类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单据编号" prop="billId">
              <el-input v-model="form.billId" placeholder="请输入单据编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="应收金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入应收金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已收金额" prop="receivedAmount">
              <el-input v-model="form.receivedAmount" placeholder="请输入已收金额" />
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
        <el-form-item label="到期日期" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择到期日期"
          ></el-date-picker>
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

<script setup name="Receivable">
import { listReceivable, addReceivable, delReceivable, getReceivable, updateReceivable } from "@/api/business/receivable"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const receivableList = ref([])
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
    receivableNo: undefined,
    customerId: undefined,
    status: undefined
  },
  rules: {
    receivableNo: [{ required: true, message: "应收单号不能为空", trigger: "blur" }],
    customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listReceivable(queryParams.value).then(response => {
    receivableList.value = response.rows
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
    receivableId: undefined,
    receivableNo: undefined,
    customerId: undefined,
    billType: undefined,
    billId: undefined,
    amount: undefined,
    receivedAmount: undefined,
    status: "0",
    dueDate: undefined,
    remark: undefined
  }
  proxy.resetForm("receivableRef")
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
  selectedIds.value = selection.map(item => item.receivableId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加应收单"
}

function handleUpdate(currentRow) {
  reset()
  const receivableId = currentRow.receivableId || selectedIds.value
  getReceivable(receivableId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改应收单"
  })
}

function submitForm() {
  proxy.$refs["receivableRef"].validate(valid => {
    if (valid) {
      if (form.value.receivableId != undefined) {
        updateReceivable(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addReceivable(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const receivableIds = currentRow.receivableId || selectedIds.value
  proxy.$modal.confirm('是否确认删除应收编号为"' + receivableIds + '"的数据项？').then(function() {
    return delReceivable(receivableIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
