<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户编号" prop="customerId">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入商品编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:recentTransactionPrice:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:recentTransactionPrice:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:recentTransactionPrice:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recentTransactionPriceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="最近成交价编号" align="center" prop="recentPriceId" />
      <el-table-column label="客户编号" align="center" prop="customerId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="销售单价" align="center" prop="salePrice" />
      <el-table-column label="来源类型" align="center" prop="sourceBillType" />
      <el-table-column label="来源编号" align="center" prop="sourceBillId" />
      <el-table-column label="业务日期" align="center" prop="businessDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.businessDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:recentTransactionPrice:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:recentTransactionPrice:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="recentTransactionPriceRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编号" prop="customerId">
              <el-input v-model="form.customerId" placeholder="请输入客户编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品编号" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入商品编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="销售单价" prop="salePrice">
              <el-input-number v-model="form.salePrice" controls-position="right" :min="0" />
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
            <el-form-item label="来源类型" prop="sourceBillType">
              <el-input v-model="form.sourceBillType" placeholder="请输入来源类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源编号" prop="sourceBillId">
              <el-input v-model="form.sourceBillId" placeholder="请输入来源编号" />
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

<script setup name="RecentTransactionPrice">
import { listRecentTransactionPrice, getRecentTransactionPrice, addRecentTransactionPrice, updateRecentTransactionPrice, delRecentTransactionPrice } from "@/api/business/recentTransactionPrice"

const { proxy } = getCurrentInstance()

const recentTransactionPriceList = ref([])
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
    customerId: undefined,
    productId: undefined
  },
  rules: {
    customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }],
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 查询最近成交价列表
function getList() {
  loading.value = true
  listRecentTransactionPrice(queryParams.value).then(response => {
    recentTransactionPriceList.value = response.rows
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
    recentPriceId: undefined,
    customerId: undefined,
    productId: undefined,
    salePrice: 0,
    sourceBillType: undefined,
    sourceBillId: undefined,
    businessDate: undefined,
    remark: undefined
  }
  proxy.resetForm("recentTransactionPriceRef")
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
  selectedIds.value = selection.map(item => item.recentPriceId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增按钮操作
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加最近成交价"
}

// 修改按钮操作
function handleUpdate(row) {
  reset()
  const recentPriceId = row.recentPriceId || selectedIds.value
  getRecentTransactionPrice(recentPriceId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改最近成交价"
  })
}

// 提交按钮
function submitForm() {
  proxy.$refs["recentTransactionPriceRef"].validate(valid => {
    if (valid) {
      if (form.value.recentPriceId !== undefined) {
        updateRecentTransactionPrice(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRecentTransactionPrice(form.value).then(() => {
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
  const recentPriceIds = row.recentPriceId || selectedIds.value
  proxy.$modal.confirm('是否确认删除最近成交价编号为"' + recentPriceIds + '"的数据项？').then(function() {
    return delRecentTransactionPrice(recentPriceIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
