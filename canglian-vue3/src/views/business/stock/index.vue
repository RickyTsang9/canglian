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
          v-hasPermi="['business:stock:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:stock:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:stock:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="库存编号" align="center" prop="stockId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="库存数量" align="center" prop="quantity" />
      <el-table-column label="可用数量" align="center" prop="availableQuantity" />
      <el-table-column label="锁定数量" align="center" prop="lockedQuantity" />
      <el-table-column label="冻结数量" align="center" prop="frozenQuantity" />
      <el-table-column label="最小预警" align="center" prop="warningMinQty" />
      <el-table-column label="最大预警" align="center" prop="warningMaxQty" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:stock:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:stock:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="720px" append-to-body>
      <el-form ref="stockRef" :model="form" :rules="rules" label-width="90px">
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
            <el-form-item label="库存数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入库存数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="锁定数量" prop="lockedQuantity">
              <el-input v-model="form.lockedQuantity" placeholder="请输入锁定数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最小预警" prop="warningMinQty">
              <el-input v-model="form.warningMinQty" placeholder="请输入最小预警" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大预警" prop="warningMaxQty">
              <el-input v-model="form.warningMaxQty" placeholder="请输入最大预警" />
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

<script setup name="Stock">
import { listStock, addStock, delStock, getStock, updateStock } from "@/api/business/stock"

const { proxy } = getCurrentInstance()

const stockList = ref([])
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
    productId: undefined
  },
  rules: {
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listStock(queryParams.value).then(response => {
    stockList.value = response.rows
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
    stockId: undefined,
    warehouseId: undefined,
    productId: undefined,
    quantity: undefined,
    lockedQuantity: undefined,
    warningMinQty: undefined,
    warningMaxQty: undefined
  }
  proxy.resetForm("stockRef")
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
  selectedIds.value = selection.map(item => item.stockId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加库存"
}

function handleUpdate(currentRow) {
  reset()
  const stockId = currentRow.stockId || selectedIds.value
  getStock(stockId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改库存"
  })
}

function submitForm() {
  proxy.$refs["stockRef"].validate(valid => {
    if (valid) {
      if (form.value.stockId != undefined) {
        updateStock(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addStock(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const stockIds = currentRow.stockId || selectedIds.value
  proxy.$modal.confirm('是否确认删除库存编号为"' + stockIds + '"的数据项？').then(function() {
    return delStock(stockIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
