<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="应付单号" prop="payableNo">
        <el-input
          v-model="queryParams.payableNo"
          placeholder="请输入应付单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商编号" prop="supplierId">
        <el-input
          v-model="queryParams.supplierId"
          placeholder="请输入供应商编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="应付状态" clearable style="width: 200px">
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
          v-hasPermi="['business:payable:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:payable:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:payable:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="payableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="应付编号" align="center" prop="payableId" />
      <el-table-column label="应付单号" align="center" prop="payableNo" />
      <el-table-column label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="单据类型" align="center" prop="billType" />
      <el-table-column label="单据编号" align="center" prop="billId" />
      <el-table-column label="应付金额" align="center" prop="amount" />
      <el-table-column label="已付金额" align="center" prop="paidAmount" />
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
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:payable:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:payable:remove']">删除</el-button>
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
      <el-form ref="payableRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="应付单号" prop="payableNo">
              <el-input v-model="form.payableNo" placeholder="请输入应付单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商编号" prop="supplierId">
              <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
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
            <el-form-item label="应付金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入应付金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已付金额" prop="paidAmount">
              <el-input v-model="form.paidAmount" placeholder="请输入已付金额" />
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

<script setup name="Payable">
import { listPayable, addPayable, delPayable, getPayable, updatePayable } from "@/api/business/payable"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const payableList = ref([])
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
    payableNo: undefined,
    supplierId: undefined,
    status: undefined
  },
  rules: {
    payableNo: [{ required: true, message: "应付单号不能为空", trigger: "blur" }],
    supplierId: [{ required: true, message: "供应商编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listPayable(queryParams.value).then(response => {
    payableList.value = response.rows
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
    payableId: undefined,
    payableNo: undefined,
    supplierId: undefined,
    billType: undefined,
    billId: undefined,
    amount: undefined,
    paidAmount: undefined,
    status: "0",
    dueDate: undefined,
    remark: undefined
  }
  proxy.resetForm("payableRef")
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
  selectedIds.value = selection.map(item => item.payableId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加应付单"
}

function handleUpdate(currentRow) {
  reset()
  const payableId = currentRow.payableId || selectedIds.value
  getPayable(payableId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改应付单"
  })
}

function submitForm() {
  proxy.$refs["payableRef"].validate(valid => {
    if (valid) {
      if (form.value.payableId != undefined) {
        updatePayable(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPayable(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const payableIds = currentRow.payableId || selectedIds.value
  proxy.$modal.confirm('是否确认删除应付编号为"' + payableIds + '"的数据项？').then(function() {
    return delPayable(payableIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
