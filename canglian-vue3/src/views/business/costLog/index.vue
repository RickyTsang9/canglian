<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="成本账户" prop="costAccountId">
        <el-input
          v-model="queryParams.costAccountId"
          placeholder="请输入成本账户编号"
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
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input
          v-model="queryParams.warehouseId"
          placeholder="请输入仓库编号"
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
      <el-form-item label="出入库" prop="inOut">
        <el-select v-model="queryParams.inOut" placeholder="请选择" clearable style="width: 200px">
          <el-option label="入库" value="1" />
          <el-option label="出库" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="成本算法" prop="costMethod">
        <el-select v-model="queryParams.costMethod" placeholder="请选择" clearable style="width: 200px">
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
          v-hasPermi="['business:costLog:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:costLog:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:costLog:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="costLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流水编号" align="center" prop="costLogId" />
      <el-table-column label="成本账户" align="center" prop="costAccountId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="单据类型" align="center" prop="billType" />
      <el-table-column label="单据编号" align="center" prop="billId" />
      <el-table-column label="出入库" align="center" prop="inOut" />
      <el-table-column label="数量" align="center" prop="quantity" />
      <el-table-column label="单价" align="center" prop="price" />
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="成本算法" align="center" prop="costMethod" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:costLog:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:costLog:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="costLogRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="成本账户" prop="costAccountId">
              <el-input v-model="form.costAccountId" placeholder="请输入成本账户编号" />
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
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单据类型" prop="billType">
              <el-input v-model="form.billType" placeholder="请输入单据类型" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单据编号" prop="billId">
              <el-input v-model="form.billId" placeholder="请输入单据编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出入库" prop="inOut">
              <el-select v-model="form.inOut" placeholder="请选择">
                <el-option label="入库" value="1" />
                <el-option label="出库" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价" prop="price">
              <el-input v-model="form.price" placeholder="请输入单价" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成本算法" prop="costMethod">
              <el-select v-model="form.costMethod" placeholder="请选择">
                <el-option label="移动加权" value="moving" />
                <el-option label="先进先出" value="fifo" />
              </el-select>
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

<script setup name="CostLog">
import { listCostLog, addCostLog, delCostLog, getCostLog, updateCostLog } from "@/api/business/costLog"

const { proxy } = getCurrentInstance()

const costLogList = ref([])
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
    costAccountId: undefined,
    productId: undefined,
    warehouseId: undefined,
    billType: undefined,
    inOut: undefined,
    costMethod: undefined
  },
  rules: {
    costAccountId: [{ required: true, message: "成本账户不能为空", trigger: "blur" }],
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }],
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listCostLog(queryParams.value).then(response => {
    costLogList.value = response.rows
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
    costLogId: undefined,
    costAccountId: undefined,
    productId: undefined,
    warehouseId: undefined,
    billType: undefined,
    billId: undefined,
    inOut: undefined,
    quantity: undefined,
    price: undefined,
    amount: undefined,
    costMethod: undefined,
    remark: undefined
  }
  proxy.resetForm("costLogRef")
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
  selectedIds.value = selection.map(item => item.costLogId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加成本流水"
}

function handleUpdate(currentRow) {
  reset()
  const costLogId = currentRow.costLogId || selectedIds.value
  getCostLog(costLogId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改成本流水"
  })
}

function submitForm() {
  proxy.$refs["costLogRef"].validate(valid => {
    if (valid) {
      if (form.value.costLogId != undefined) {
        updateCostLog(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCostLog(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const costLogIds = currentRow.costLogId || selectedIds.value
  proxy.$modal.confirm('是否确认删除成本流水编号为"' + costLogIds + '"的数据项？').then(function() {
    return delCostLog(costLogIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
