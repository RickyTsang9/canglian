<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单据号" prop="purchaseOrderNo">
        <el-input v-model="queryParams.purchaseOrderNo" placeholder="请输入单据号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="供应商编号" prop="supplierId">
        <el-input v-model="queryParams.supplierId" placeholder="请输入供应商编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务状态" prop="bizStatus">
        <el-select v-model="queryParams.bizStatus" placeholder="请选择业务状态" clearable style="width: 200px">
          <el-option v-for="statusOption in bizStatusOptions" :key="statusOption.value" :label="statusOption.label" :value="statusOption.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:purOrder:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:purOrder:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:purOrder:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="purOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单据编号" align="center" prop="purchaseOrderId" />
      <el-table-column label="单据号" align="center" prop="purchaseOrderNo" />
      <el-table-column label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="来源单号" align="center" prop="sourceBillNo" />
      <el-table-column label="业务日期" align="center" prop="businessDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.businessDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总数量" align="center" prop="totalQty" />
      <el-table-column label="总金额" align="center" prop="totalAmount" />
      <el-table-column label="业务状态" align="center" prop="bizStatus">
        <template #default="scope">
          <dict-tag :options="bizStatusOptions" :value="scope.row.bizStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:purOrder:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" :disabled="!canEditRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:purOrder:remove']">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" :disabled="!canApproveRow(scope.row)" @click="handleApprove(scope.row)" v-hasPermi="['business:purOrder:approve']">审批</el-button>
          <el-button link type="primary" icon="Right" :disabled="!canPushRow(scope.row)" @click="handlePushInbound(scope.row)" v-hasPermi="['business:purOrder:pushInbound']">下推入库</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)" v-hasPermi="['business:purOrder:print']">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="1000px" append-to-body>
      <el-form ref="purOrderRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="单据号" prop="purchaseOrderNo">
              <el-input v-model="form.purchaseOrderNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商编号" prop="supplierId">
              <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计日期" prop="expectedDate">
              <el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择预计日期" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="来源单号" prop="sourceBillNo">
              <el-input v-model="form.sourceBillNo" placeholder="请输入来源单号" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>

        <el-divider content-position="left">明细信息</el-divider>
        <el-button type="primary" plain icon="Plus" @click="handleAddItem">新增明细</el-button>
        <el-table :data="form.orderItemList" border style="margin-top: 12px">
          <el-table-column label="商品编号" min-width="140">
            <template #default="scope">
              <el-input v-model="scope.row.productId" placeholder="请输入商品编号" />
            </template>
          </el-table-column>
          <el-table-column label="数量" min-width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" controls-position="right" :min="0.01" :step="1" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="单价" min-width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.price" controls-position="right" :min="0" :step="0.01" @change="calculateItemAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="金额" min-width="140">
            <template #default="scope">
              <el-input v-model="scope.row.amount" disabled />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.remark" placeholder="请输入备注" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button link type="primary" icon="Delete" @click="handleRemoveItem(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
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

<script setup name="PurOrder">
import { listPurOrder, getPurOrder, addPurOrder, updatePurOrder, approvePurOrder, pushPurOrderToInbound, printPurOrder, delPurOrder } from "@/api/business/purOrder"

const { proxy } = getCurrentInstance()

const purOrderList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const bizStatusOptions = [
  { label: "草稿", value: "draft" },
  { label: "已审批", value: "approved" },
  { label: "执行中", value: "executing" },
  { label: "已完成", value: "completed" }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    purchaseOrderNo: undefined,
    supplierId: undefined,
    bizStatus: undefined
  },
  rules: {
    supplierId: [{ required: true, message: "供应商编号不能为空", trigger: "blur" }],
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 查询购货订单列表
function getList() {
  loading.value = true
  listPurOrder(queryParams.value).then(response => {
    purOrderList.value = response.rows
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
    purchaseOrderId: undefined,
    purchaseOrderNo: undefined,
    supplierId: undefined,
    warehouseId: undefined,
    sourceBillType: undefined,
    sourceBillId: undefined,
    sourceBillNo: undefined,
    businessDate: undefined,
    expectedDate: undefined,
    bizStatus: "draft",
    remark: undefined,
    orderItemList: []
  }
  proxy.resetForm("purOrderRef")
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
  selectedIds.value = selection.map(item => item.purchaseOrderId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(item => !canEditRow(item))
}

// 判断是否允许修改
function canEditRow(currentRow) {
  return !!currentRow && currentRow.bizStatus === "draft"
}

// 判断是否允许审批
function canApproveRow(currentRow) {
  return !!currentRow && currentRow.bizStatus === "draft"
}

// 判断是否允许下推
function canPushRow(currentRow) {
  return !!currentRow && currentRow.bizStatus === "approved"
}

// 新增按钮操作
function handleAdd() {
  reset()
  handleAddItem()
  open.value = true
  title.value = "添加购货订单"
}

// 修改按钮操作
function handleUpdate(row) {
  reset()
  const purchaseOrderId = row.purchaseOrderId || selectedIds.value
  getPurOrder(purchaseOrderId).then(response => {
    form.value = response.data
    if (!form.value.orderItemList) {
      form.value.orderItemList = []
    }
    open.value = true
    title.value = "修改购货订单"
  })
}

// 审批按钮操作
function handleApprove(row) {
  proxy.$modal.confirm("是否确认审批当前购货订单？").then(function() {
    return approvePurOrder(row.purchaseOrderId)
  }).then(() => {
    proxy.$modal.msgSuccess("审批成功")
    getList()
  }).catch(() => {})
}

// 下推入库按钮操作
function handlePushInbound(row) {
  proxy.$modal.confirm("是否确认下推入库单？").then(function() {
    return pushPurOrderToInbound(row.purchaseOrderId)
  }).then(() => {
    proxy.$modal.msgSuccess("下推成功")
    getList()
  }).catch(() => {})
}

// 打印按钮操作
function handlePrint(row) {
  printPurOrder(row.purchaseOrderId).then(response => {
    const printData = response.data || {}
    const header = printData.header || {}
    const orderItemList = printData.items || []
    let itemHtml = ""
    orderItemList.forEach(orderItem => {
      itemHtml += `<tr><td>${orderItem.productId ?? ""}</td><td>${orderItem.quantity ?? ""}</td><td>${orderItem.price ?? ""}</td><td>${orderItem.amount ?? ""}</td><td>${orderItem.remark ?? ""}</td></tr>`
    })
    const printWindow = window.open("", "_blank")
    if (!printWindow) {
      proxy.$modal.msgError("打印窗口被拦截，请允许浏览器弹窗")
      return
    }
    printWindow.document.write(`
      <html>
        <head><title>购货订单打印</title></head>
        <body style="font-family: Arial; padding: 24px;">
          <h2>购货订单</h2>
          <div>单据号：${header.purchaseOrderNo ?? ""}</div>
          <div>供应商编号：${header.supplierId ?? ""}</div>
          <div>仓库编号：${header.warehouseId ?? ""}</div>
          <div>总金额：${header.totalAmount ?? ""}</div>
          <table border="1" cellspacing="0" cellpadding="8" style="width:100%; margin-top:16px; border-collapse: collapse;">
            <thead><tr><th>商品编号</th><th>数量</th><th>单价</th><th>金额</th><th>备注</th></tr></thead>
            <tbody>${itemHtml}</tbody>
          </table>
        </body>
      </html>
    `)
    printWindow.document.close()
    printWindow.focus()
    printWindow.print()
    printWindow.close()
  })
}

// 新增明细
function handleAddItem() {
  form.value.orderItemList.push({
    productId: undefined,
    quantity: 1,
    price: 0,
    amount: 0,
    remark: undefined
  })
}

// 删除明细
function handleRemoveItem(index) {
  form.value.orderItemList.splice(index, 1)
}

// 计算明细金额
function calculateItemAmount(currentItem) {
  const itemQuantity = Number(currentItem.quantity || 0)
  const itemPrice = Number(currentItem.price || 0)
  currentItem.amount = (itemQuantity * itemPrice).toFixed(2)
}

// 提交按钮
function submitForm() {
  proxy.$refs["purOrderRef"].validate(valid => {
    if (valid) {
      if (!form.value.orderItemList || form.value.orderItemList.length === 0) {
        proxy.$modal.msgError("请至少录入一条明细")
        return
      }
      form.value.orderItemList.forEach(orderItem => {
        calculateItemAmount(orderItem)
      })
      if (form.value.purchaseOrderId !== undefined) {
        updatePurOrder(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPurOrder(form.value).then(() => {
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
  const purchaseOrderIds = row.purchaseOrderId || selectedIds.value
  proxy.$modal.confirm('是否确认删除单据编号为"' + purchaseOrderIds + '"的数据项？').then(function() {
    return delPurOrder(purchaseOrderIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
