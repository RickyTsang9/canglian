<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="收款单号" prop="receiptNo">
        <el-input
          v-model="queryParams.receiptNo"
          placeholder="请输入收款单号"
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
          v-hasPermi="['business:receipt:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:receipt:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:receipt:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="receiptList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="收款编号" align="center" prop="receiptId" />
      <el-table-column label="收款单号" align="center" prop="receiptNo" />
      <el-table-column label="客户编号" align="center" prop="customerId" />
      <el-table-column label="应收编号" align="center" prop="receivableId" />
      <el-table-column label="收款金额" align="center" prop="amount" />
      <el-table-column label="收款日期" align="center" prop="receiptDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.receiptDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="支付渠道" align="center" prop="payChannel" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:receipt:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:receipt:remove']">删除</el-button>
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
      <el-form ref="receiptRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="收款单号" prop="receiptNo">
              <el-input v-model="form.receiptNo" placeholder="请输入收款单号" />
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
            <el-form-item label="应收编号" prop="receivableId">
              <el-input v-model="form.receivableId" placeholder="请输入应收编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收款金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入收款金额" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="收款日期" prop="receiptDate">
              <el-date-picker
                v-model="form.receiptDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择收款日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付渠道" prop="payChannel">
              <el-input v-model="form.payChannel" placeholder="请输入支付渠道" />
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

<script setup name="Receipt">
import { listReceipt, addReceipt, delReceipt, getReceipt, updateReceipt } from "@/api/business/receipt"

const { proxy } = getCurrentInstance()

const receiptList = ref([])
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
    receiptNo: undefined,
    customerId: undefined
  },
  rules: {
    receiptNo: [{ required: true, message: "收款单号不能为空", trigger: "blur" }],
    customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listReceipt(queryParams.value).then(response => {
    receiptList.value = response.rows
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
    receiptId: undefined,
    receiptNo: undefined,
    customerId: undefined,
    receivableId: undefined,
    amount: undefined,
    receiptDate: undefined,
    payChannel: undefined,
    remark: undefined
  }
  proxy.resetForm("receiptRef")
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
  selectedIds.value = selection.map(item => item.receiptId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加收款单"
}

function handleUpdate(currentRow) {
  reset()
  const receiptId = currentRow.receiptId || selectedIds.value
  getReceipt(receiptId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改收款单"
  })
}

function submitForm() {
  proxy.$refs["receiptRef"].validate(valid => {
    if (valid) {
      if (form.value.receiptId != undefined) {
        updateReceipt(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addReceipt(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const receiptIds = currentRow.receiptId || selectedIds.value
  proxy.$modal.confirm('是否确认删除收款编号为"' + receiptIds + '"的数据项？').then(function() {
    return delReceipt(receiptIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
