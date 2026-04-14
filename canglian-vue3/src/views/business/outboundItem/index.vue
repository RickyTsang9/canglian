<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="出库单编号" prop="outboundId">
        <el-input
          v-model="queryParams.outboundId"
          placeholder="请输入出库单编号"
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
          v-hasPermi="['business:outboundItem:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:outboundItem:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:outboundItem:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="outboundItemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="出库明细编号" align="center" prop="outboundItemId" />
      <el-table-column label="出库单编号" align="center" prop="outboundId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="数量" align="center" prop="quantity" />
      <el-table-column label="单价" align="center" prop="price" />
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:outboundItem:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:outboundItem:remove']">删除</el-button>
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
      <el-form ref="outboundItemRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="出库单编号" prop="outboundId">
              <el-input v-model="form.outboundId" placeholder="请输入出库单编号" />
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

<script setup name="OutboundItem">
import { listOutboundItem, addOutboundItem, delOutboundItem, getOutboundItem, updateOutboundItem } from "@/api/business/outboundItem"

const { proxy } = getCurrentInstance()

const outboundItemList = ref([])
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
    outboundId: undefined,
    productId: undefined
  },
  rules: {
    outboundId: [{ required: true, message: "出库单编号不能为空", trigger: "blur" }],
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 获取出库明细列表
function getList() {
  loading.value = true
  listOutboundItem(queryParams.value).then(response => {
    outboundItemList.value = response.rows
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
    outboundItemId: undefined,
    outboundId: undefined,
    productId: undefined,
    quantity: undefined,
    price: undefined,
    amount: undefined
  }
  proxy.resetForm("outboundItemRef")
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
  selectedIds.value = selection.map(item => item.outboundItemId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增出库明细
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加出库明细"
}

// 修改出库明细
function handleUpdate(currentRow) {
  reset()
  const outboundItemId = currentRow.outboundItemId || selectedIds.value
  getOutboundItem(outboundItemId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改出库明细"
  })
}

// 提交表单
function submitForm() {
  proxy.$refs["outboundItemRef"].validate(valid => {
    if (valid) {
      if (form.value.outboundItemId != undefined) {
        updateOutboundItem(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addOutboundItem(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除出库明细
function handleDelete(currentRow) {
  const outboundItemIds = currentRow.outboundItemId || selectedIds.value
  proxy.$modal.confirm('是否确认删除出库明细编号为"' + outboundItemIds + '"的数据项？').then(function() {
    return delOutboundItem(outboundItemIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
