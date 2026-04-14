<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input
          v-model="queryParams.warehouseId"
          placeholder="请输入仓库编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单据类型" prop="billType">
        <el-input
          v-model="queryParams.billType"
          placeholder="请输入单据类型"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出入库标识" prop="inOut">
        <el-input
          v-model="queryParams.inOut"
          placeholder="请输入出入库标识"
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
          v-hasPermi="['business:stockLog:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:stockLog:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:stockLog:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流水编号" align="center" prop="stockLogId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="单据类型" align="center" prop="billType" />
      <el-table-column label="单据编号" align="center" prop="billId" />
      <el-table-column label="出入库标识" align="center" prop="inOut" />
      <el-table-column label="数量" align="center" prop="quantity" />
      <el-table-column label="单价" align="center" prop="price" />
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="变动前数量" align="center" prop="beforeQty" />
      <el-table-column label="变动后数量" align="center" prop="afterQty" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:stockLog:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:stockLog:remove']">删除</el-button>
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
      <el-form ref="stockLogRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
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
            <el-form-item label="出入库标识" prop="inOut">
              <el-input v-model="form.inOut" placeholder="请输入出入库标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单价" prop="price">
              <el-input v-model="form.price" placeholder="请输入单价" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入金额" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="变动前数量" prop="beforeQty">
              <el-input v-model="form.beforeQty" placeholder="请输入变动前数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="变动后数量" prop="afterQty">
              <el-input v-model="form.afterQty" placeholder="请输入变动后数量" />
            </el-form-item>
          </el-col>
        </el-row>
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

<script setup name="StockLog">
import { listStockLog, addStockLog, delStockLog, getStockLog, updateStockLog } from "@/api/business/stockLog"

const { proxy } = getCurrentInstance()

const stockLogList = ref([])
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
    warehouseId: undefined,
    productId: undefined,
    billType: undefined,
    inOut: undefined
  },
  rules: {
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }],
    billType: [{ required: true, message: "单据类型不能为空", trigger: "blur" }],
    inOut: [{ required: true, message: "出入库标识不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 获取库存流水列表
function getList() {
  loading.value = true
  listStockLog(queryParams.value).then(response => {
    stockLogList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 关闭弹窗
function cancel() {
  open.value = false
  reset()
}

// 重置表单数据
function reset() {
  form.value = {
    stockLogId: undefined,
    warehouseId: undefined,
    productId: undefined,
    billType: undefined,
    billId: undefined,
    inOut: undefined,
    quantity: undefined,
    price: undefined,
    amount: undefined,
    beforeQty: undefined,
    afterQty: undefined
  }
  proxy.resetForm("stockLogRef")
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
  selectedIds.value = selection.map(item => item.stockLogId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增库存流水
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加库存流水"
}

// 修改库存流水
function handleUpdate(currentRow) {
  reset()
  const stockLogId = currentRow.stockLogId || selectedIds.value
  getStockLog(stockLogId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改库存流水"
  })
}

// 提交表单
function submitForm() {
  proxy.$refs["stockLogRef"].validate(valid => {
    if (valid) {
      if (form.value.stockLogId != undefined) {
        updateStockLog(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addStockLog(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除库存流水
function handleDelete(currentRow) {
  const stockLogIds = currentRow.stockLogId || selectedIds.value
  proxy.$modal.confirm('是否确认删除库存流水编号为"' + stockLogIds + '"的数据项？').then(function() {
    return delStockLog(stockLogIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
