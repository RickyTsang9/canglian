<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="核销单号" prop="writeOffNo">
        <el-input
          v-model="queryParams.writeOffNo"
          placeholder="请输入核销单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销类型" prop="writeOffType">
        <el-select
          v-model="queryParams.writeOffType"
          placeholder="请选择核销类型"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="writeOffTypeOption in writeOffTypeOptions"
            :key="writeOffTypeOption.value"
            :label="writeOffTypeOption.label"
            :value="writeOffTypeOption.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="核销状态" clearable style="width: 200px">
          <el-option
            v-for="writeOffStatusOption in writeOffStatusOptions"
            :key="writeOffStatusOption.value"
            :label="writeOffStatusOption.label"
            :value="writeOffStatusOption.value"
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
          v-hasPermi="['business:writeOff:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:writeOff:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="CircleCheck"
          :disabled="isConfirmDisabled"
          @click="handleConfirm"
          v-hasPermi="['business:writeOff:confirm']"
        >确认</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:writeOff:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="writeOffList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="核销编号" align="center" prop="writeOffId" />
      <el-table-column label="核销单号" align="center" prop="writeOffNo" />
      <el-table-column label="核销类型" align="center" prop="writeOffType" />
      <el-table-column label="应收编号" align="center" prop="receivableId" />
      <el-table-column label="应付编号" align="center" prop="payableId" />
      <el-table-column label="收款单编号" align="center" prop="receiptId" />
      <el-table-column label="付款单编号" align="center" prop="paymentId" />
      <el-table-column label="核销金额" align="center" prop="amount" />
      <el-table-column label="核销日期" align="center" prop="writeOffDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.writeOffDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="writeOffStatusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:writeOff:edit']">修改</el-button>
          <el-button link type="primary" icon="CircleCheck" :disabled="!canConfirmRow(scope.row)" @click="handleConfirm(scope.row)" v-hasPermi="['business:writeOff:confirm']">确认</el-button>
          <el-button link type="primary" icon="Delete" :disabled="!canDeleteRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:writeOff:remove']">删除</el-button>
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
      <el-form ref="writeOffRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="核销单号" prop="writeOffNo">
              <el-input v-model="form.writeOffNo" placeholder="请输入核销单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核销类型" prop="writeOffType">
              <el-select v-model="form.writeOffType" placeholder="请选择核销类型" style="width: 100%">
                <el-option
                  v-for="writeOffTypeOption in writeOffTypeOptions"
                  :key="writeOffTypeOption.value"
                  :label="writeOffTypeOption.label"
                  :value="writeOffTypeOption.value"
                />
              </el-select>
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
            <el-form-item label="应付编号" prop="payableId">
              <el-input v-model="form.payableId" placeholder="请输入应付编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="收款单编号" prop="receiptId">
              <el-input v-model="form.receiptId" placeholder="请输入收款单编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款单编号" prop="paymentId">
              <el-input v-model="form.paymentId" placeholder="请输入付款单编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="核销金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入核销金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核销日期" prop="writeOffDate">
              <el-date-picker
                v-model="form.writeOffDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择核销日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" disabled>
            <el-radio
              v-for="writeOffStatusOption in writeOffStatusOptions"
              :key="writeOffStatusOption.value"
              :value="writeOffStatusOption.value"
            >{{ writeOffStatusOption.label }}</el-radio>
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

<script setup name="WriteOff">
import { listWriteOff, addWriteOff, delWriteOff, getWriteOff, updateWriteOff, confirmWriteOff } from "@/api/business/writeOff"

const { proxy } = getCurrentInstance()

const writeOffList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isConfirmDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")
const writeOffTypeOptions = [
  { label: "应收核销", value: "receivable" },
  { label: "应付核销", value: "payable" }
]
const writeOffStatusOptions = [
  { label: "草稿", value: "0" },
  { label: "已核销", value: "1" }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    writeOffNo: undefined,
    writeOffType: undefined,
    status: undefined
  },
  rules: {
    writeOffNo: [{ required: true, message: "核销单号不能为空", trigger: "blur" }],
    writeOffType: [{ required: true, message: "核销类型不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 获取核销单列表
function getList() {
  loading.value = true
  listWriteOff(queryParams.value).then(response => {
    writeOffList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消弹窗
function cancel() {
  open.value = false
  reset()
}

// 重置表单
function reset() {
  form.value = {
    writeOffId: undefined,
    writeOffNo: undefined,
    writeOffType: undefined,
    receivableId: undefined,
    payableId: undefined,
    receiptId: undefined,
    paymentId: undefined,
    amount: undefined,
    writeOffDate: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("writeOffRef")
}

// 执行查询
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置查询
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 选择行变化
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.writeOffId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isConfirmDisabled.value = selection.length !== 1 || !canConfirmRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(item => !canDeleteRow(item))
}

// 判断核销单是否允许修改
function canEditRow(currentRow) {
  return !!currentRow && currentRow.status === "0"
}

// 判断核销单是否允许确认
function canConfirmRow(currentRow) {
  return !!currentRow && currentRow.status === "0"
}

// 判断核销单是否允许删除
function canDeleteRow(currentRow) {
  return canEditRow(currentRow)
}

// 新增核销单
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加核销单"
}

// 修改核销单
function handleUpdate(currentRow) {
  reset()
  const writeOffId = currentRow.writeOffId || selectedIds.value
  getWriteOff(writeOffId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改核销单"
  })
}

// 确认核销单
function handleConfirm(currentRow) {
  const writeOffId = currentRow?.writeOffId || selectedIds.value[0]
  proxy.$modal.confirm('是否确认核销编号为"' + writeOffId + '"的数据项？').then(function() {
    return confirmWriteOff(writeOffId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("确认成功")
  }).catch(() => {})
}

// 提交表单
function submitForm() {
  proxy.$refs["writeOffRef"].validate(valid => {
    if (valid) {
      if (form.value.writeOffId != undefined) {
        updateWriteOff(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addWriteOff(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除核销单
function handleDelete(currentRow) {
  const writeOffIds = currentRow.writeOffId || selectedIds.value
  proxy.$modal.confirm('是否确认删除核销编号为"' + writeOffIds + '"的数据项？').then(function() {
    return delWriteOff(writeOffIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
