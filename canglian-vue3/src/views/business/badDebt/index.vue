<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="坏账单号" prop="badDebtNo">
        <el-input
          v-model="queryParams.badDebtNo"
          placeholder="请输入坏账单号"
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
        <el-select v-model="queryParams.status" placeholder="坏账状态" clearable style="width: 200px">
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
          v-hasPermi="['business:badDebt:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:badDebt:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:badDebt:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="badDebtList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="坏账编号" align="center" prop="badDebtId" />
      <el-table-column label="坏账单号" align="center" prop="badDebtNo" />
      <el-table-column label="应收编号" align="center" prop="receivableId" />
      <el-table-column label="客户编号" align="center" prop="customerId" />
      <el-table-column label="坏账金额" align="center" prop="amount" />
      <el-table-column label="坏账日期" align="center" prop="badDebtDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.badDebtDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="原因" align="center" prop="reason" />
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
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:badDebt:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:badDebt:remove']">删除</el-button>
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
      <el-form ref="badDebtRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="坏账单号" prop="badDebtNo">
              <el-input v-model="form.badDebtNo" placeholder="请输入坏账单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应收编号" prop="receivableId">
              <el-input v-model="form.receivableId" placeholder="请输入应收编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编号" prop="customerId">
              <el-input v-model="form.customerId" placeholder="请输入客户编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="坏账金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入坏账金额" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="坏账日期" prop="badDebtDate">
              <el-date-picker
                v-model="form.badDebtDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择坏账日期"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原因" prop="reason">
              <el-input v-model="form.reason" placeholder="请输入原因" />
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

<script setup name="BadDebt">
import { listBadDebt, addBadDebt, delBadDebt, getBadDebt, updateBadDebt } from "@/api/business/badDebt"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const badDebtList = ref([])
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
    badDebtNo: undefined,
    customerId: undefined,
    status: undefined
  },
  rules: {
    badDebtNo: [{ required: true, message: "坏账单号不能为空", trigger: "blur" }],
    customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 获取坏账单列表
function getList() {
  loading.value = true
  listBadDebt(queryParams.value).then(response => {
    badDebtList.value = response.rows
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
    badDebtId: undefined,
    badDebtNo: undefined,
    receivableId: undefined,
    customerId: undefined,
    amount: undefined,
    badDebtDate: undefined,
    reason: undefined,
    status: undefined,
    remark: undefined
  }
  proxy.resetForm("badDebtRef")
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
  selectedIds.value = selection.map(item => item.badDebtId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增坏账单
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加坏账单"
}

// 修改坏账单
function handleUpdate(currentRow) {
  reset()
  const badDebtId = currentRow.badDebtId || selectedIds.value
  getBadDebt(badDebtId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改坏账单"
  })
}

// 提交表单
function submitForm() {
  proxy.$refs["badDebtRef"].validate(valid => {
    if (valid) {
      if (form.value.badDebtId != undefined) {
        updateBadDebt(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBadDebt(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除坏账单
function handleDelete(currentRow) {
  const badDebtIds = currentRow.badDebtId || selectedIds.value
  proxy.$modal.confirm('是否确认删除坏账编号为"' + badDebtIds + '"的数据项？').then(function() {
    return delBadDebt(badDebtIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
