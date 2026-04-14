<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="商品编号" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input
          v-model="queryParams.warehouseId"
          placeholder="请输入仓库编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成本算法" prop="costMethod">
        <el-select v-model="queryParams.costMethod" placeholder="请选择算法" clearable style="width: 200px">
          <el-option label="移动加权" value="moving" />
          <el-option label="先进先出" value="fifo" />
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
          v-hasPermi="['business:costAccount:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:costAccount:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:costAccount:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="costAccountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成本账户" align="center" prop="costAccountId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="成本算法" align="center" prop="costMethod" />
      <el-table-column label="总数量" align="center" prop="totalQty" />
      <el-table-column label="总金额" align="center" prop="totalAmount" />
      <el-table-column label="平均成本" align="center" prop="avgCost" />
      <el-table-column label="最近入库单价" align="center" prop="lastCostPrice" />
      <el-table-column label="最近入库数量" align="center" prop="lastCostQty" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:costAccount:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:costAccount:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="costAccountRef" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品编号" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入商品编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成本算法" prop="costMethod">
              <el-select v-model="form.costMethod" placeholder="请选择算法">
                <el-option label="移动加权" value="moving" />
                <el-option label="先进先出" value="fifo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总数量" prop="totalQty">
              <el-input v-model="form.totalQty" placeholder="请输入总数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input v-model="form.totalAmount" placeholder="请输入总金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="平均成本" prop="avgCost">
              <el-input v-model="form.avgCost" placeholder="请输入平均成本" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最近入库单价" prop="lastCostPrice">
              <el-input v-model="form.lastCostPrice" placeholder="请输入最近入库单价" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最近入库数量" prop="lastCostQty">
              <el-input v-model="form.lastCostQty" placeholder="请输入最近入库数量" />
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

<script setup name="CostAccount">
import { listCostAccount, addCostAccount, delCostAccount, getCostAccount, updateCostAccount } from "@/api/business/costAccount"

const { proxy } = getCurrentInstance()

const costAccountList = ref([])
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
    productId: undefined,
    warehouseId: undefined,
    costMethod: undefined
  },
  rules: {
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }],
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
    costMethod: [{ required: true, message: "成本算法不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listCostAccount(queryParams.value).then(response => {
    costAccountList.value = response.rows
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
    costAccountId: undefined,
    productId: undefined,
    warehouseId: undefined,
    costMethod: undefined,
    totalQty: undefined,
    totalAmount: undefined,
    avgCost: undefined,
    lastCostPrice: undefined,
    lastCostQty: undefined,
    remark: undefined
  }
  proxy.resetForm("costAccountRef")
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
  selectedIds.value = selection.map(item => item.costAccountId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加成本账户"
}

function handleUpdate(currentRow) {
  reset()
  const costAccountId = currentRow.costAccountId || selectedIds.value
  getCostAccount(costAccountId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改成本账户"
  })
}

function submitForm() {
  proxy.$refs["costAccountRef"].validate(valid => {
    if (valid) {
      if (form.value.costAccountId != undefined) {
        updateCostAccount(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCostAccount(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const costAccountIds = currentRow.costAccountId || selectedIds.value
  proxy.$modal.confirm('是否确认删除成本账户编号为"' + costAccountIds + '"的数据项？').then(function() {
    return delCostAccount(costAccountIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
