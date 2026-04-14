<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="出库单号" prop="outboundNo">
        <el-input
          v-model="queryParams.outboundNo"
          placeholder="请输入出库单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出库类型" prop="outboundType">
        <el-input
          v-model="queryParams.outboundType"
          placeholder="请输入出库类型"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台类型" prop="platformType">
        <el-input
          v-model="queryParams.platformType"
          placeholder="请输入平台类型"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺编号" prop="storeId">
        <el-input
          v-model="queryParams.storeId"
          placeholder="请输入店铺编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="源订单号" prop="sourceOrderNo">
        <el-input
          v-model="queryParams.sourceOrderNo"
          placeholder="请输入源订单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="出库状态" clearable style="width: 200px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="配送状态" prop="deliveryStatus">
        <el-select v-model="queryParams.deliveryStatus" placeholder="配送状态" clearable style="width: 200px">
          <el-option
            v-for="item in deliveryStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
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
          v-hasPermi="['business:outbound:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:outbound:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:outbound:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="outboundList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="出库编号" align="center" prop="outboundId" />
      <el-table-column label="出库单号" align="center" prop="outboundNo" />
      <el-table-column label="出库类型" align="center" prop="outboundType" />
      <el-table-column label="客户编号" align="center" prop="customerId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="平台类型" align="center" prop="platformType" />
      <el-table-column label="店铺编号" align="center" prop="storeId" />
      <el-table-column label="源订单号" align="center" prop="sourceOrderNo" />
      <el-table-column label="总数量" align="center" prop="totalQty" />
      <el-table-column label="总金额" align="center" prop="totalAmount" />
      <el-table-column label="承运商" align="center" prop="carrier" />
      <el-table-column label="运单号" align="center" prop="waybillNo" />
      <el-table-column label="运费成本" align="center" prop="freightCost" />
      <el-table-column label="配送状态" align="center" prop="deliveryStatus">
        <template #default="scope">
          <dict-tag :options="deliveryStatusOptions" :value="scope.row.deliveryStatus" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="auditBy" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.auditTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:outbound:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:outbound:remove']">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:outbound:audit']">审核</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)" v-hasPermi="['business:outbound:print']">打印</el-button>
          <el-button link type="primary" icon="Position" @click="handleShip(scope.row)" v-hasPermi="['business:outbound:ship']">发货</el-button>
          <el-button link type="primary" icon="CircleCheck" @click="handleSign(scope.row)" v-hasPermi="['business:outbound:sign']">签收</el-button>
          <el-button link type="primary" icon="RefreshLeft" @click="handleReturn(scope.row)" v-hasPermi="['business:outbound:return']">退货</el-button>
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
      <el-form ref="outboundRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="出库单号" prop="outboundNo">
              <el-input v-model="form.outboundNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库类型" prop="outboundType">
              <el-input v-model="form.outboundType" placeholder="请输入出库类型" />
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
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台类型" prop="platformType">
              <el-input v-model="form.platformType" placeholder="请输入平台类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="店铺编号" prop="storeId">
              <el-input v-model="form.storeId" placeholder="请输入店铺编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="源订单号" prop="sourceOrderNo">
              <el-input v-model="form.sourceOrderNo" placeholder="请输入源订单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="承运商" prop="carrier">
              <el-input v-model="form.carrier" placeholder="请输入承运商" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="运单号" prop="waybillNo">
              <el-input v-model="form.waybillNo" placeholder="请输入运单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运费成本" prop="freightCost">
              <el-input v-model="form.freightCost" placeholder="请输入运费成本" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总数量" prop="totalQty">
              <el-input v-model="form.totalQty" placeholder="请输入总数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input v-model="form.totalAmount" placeholder="请输入总金额" />
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
        <el-form-item label="配送状态" prop="deliveryStatus">
          <el-radio-group v-model="form.deliveryStatus">
            <el-radio
              v-for="item in deliveryStatusOptions"
              :key="item.value"
              :value="item.value"
            >{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="审核人" prop="auditBy">
              <el-input v-model="form.auditBy" placeholder="请输入审核人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核时间" prop="auditTime">
              <el-date-picker
                v-model="form.auditTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择审核时间"
              ></el-date-picker>
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
    <el-dialog title="出库发货" v-model="shipOpen" width="520px" append-to-body>
      <el-form ref="shipRef" :model="shipForm" label-width="90px">
        <el-form-item label="承运商" prop="carrier">
          <el-input v-model="shipForm.carrier" placeholder="请输入承运商" />
        </el-form-item>
        <el-form-item label="运单号" prop="waybillNo">
          <el-input v-model="shipForm.waybillNo" placeholder="请输入运单号" />
        </el-form-item>
        <el-form-item label="运费成本" prop="freightCost">
          <el-input v-model="shipForm.freightCost" placeholder="请输入运费成本" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitShip">确 定</el-button>
          <el-button @click="cancelShip">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog title="退货处理" v-model="returnOpen" width="520px" append-to-body>
      <el-form ref="returnRef" :model="returnForm" label-width="90px">
        <el-form-item label="退款金额" prop="refundAmount">
          <el-input v-model="returnForm.refundAmount" placeholder="请输入退款金额" />
        </el-form-item>
        <el-form-item label="应收编号" prop="receivableId">
          <el-input v-model="returnForm.receivableId" placeholder="请输入应收编号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitReturn">确 定</el-button>
          <el-button @click="cancelReturn">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Outbound">
import { listOutbound, addOutbound, delOutbound, getOutbound, updateOutbound, outboundShip, outboundSign, outboundReturn, auditOutbound, printOutbound } from "@/api/business/outbound"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const outboundList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")
const shipOpen = ref(false)
const returnOpen = ref(false)
const currentOutboundId = ref(undefined)
const shipForm = ref({})
const returnForm = ref({})
const deliveryStatusOptions = [
  { label: "待发货", value: "0" },
  { label: "已发货", value: "1" },
  { label: "已签收", value: "2" },
  { label: "退货", value: "3" }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    outboundNo: undefined,
    outboundType: undefined,
    platformType: undefined,
    storeId: undefined,
    sourceOrderNo: undefined,
    status: undefined,
    deliveryStatus: undefined
  },
  rules: {
    outboundType: [{ required: true, message: "出库类型不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listOutbound(queryParams.value).then(response => {
    outboundList.value = response.rows
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
    outboundId: undefined,
    outboundNo: undefined,
    outboundType: undefined,
    customerId: undefined,
    warehouseId: undefined,
    platformType: undefined,
    storeId: undefined,
    sourceOrderNo: undefined,
    carrier: undefined,
    waybillNo: undefined,
    freightCost: undefined,
    deliveryStatus: "0",
    totalQty: undefined,
    totalAmount: undefined,
    status: "0",
    auditBy: undefined,
    auditTime: undefined,
    remark: undefined
  }
  proxy.resetForm("outboundRef")
}

function resetShip() {
  shipForm.value = {
    carrier: undefined,
    waybillNo: undefined,
    freightCost: undefined
  }
  proxy.resetForm("shipRef")
}

function resetReturn() {
  returnForm.value = {
    refundAmount: undefined,
    receivableId: undefined
  }
  proxy.resetForm("returnRef")
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
  selectedIds.value = selection.map(item => item.outboundId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加出库单"
}

function handleUpdate(currentRow) {
  reset()
  const outboundId = currentRow.outboundId || selectedIds.value
  getOutbound(outboundId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改出库单"
  })
}

function handleAudit(row) {
  const outboundId = row.outboundId
  proxy.$modal.confirm("确认审核当前出库单吗？").then(function() {
    return auditOutbound(outboundId)
  }).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {})
}

function handlePrint(currentRow) {
  printOutbound(currentRow.outboundId).then(response => {
    const printData = response.data
    const header = printData.header || {}
    const itemList = printData.items || []
    let printTableRows = ""
    itemList.forEach(item => {
      printTableRows += `<tr>
        <td>${item.productId ?? ""}</td>
        <td>${item.locationId ?? ""}</td>
        <td>${item.batchNo ?? ""}</td>
        <td>${item.quantity ?? ""}</td>
        <td>${item.price ?? ""}</td>
        <td>${item.amount ?? ""}</td>
      </tr>`
    })
    const printWindow = window.open("", "_blank")
    if (!printWindow) {
      proxy.$modal.msgError("打印窗口被拦截，请允许浏览器弹窗")
      return
    }
    printWindow.document.write(`
      <html>
        <head>
          <title>出库单打印</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 24px; color: #333; }
            h2 { margin-bottom: 16px; }
            .header-row { margin-bottom: 8px; }
            table { width: 100%; border-collapse: collapse; margin-top: 16px; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
            th { background: #f5f7fa; }
          </style>
        </head>
        <body>
          <h2>出库单</h2>
          <div class="header-row">出库单号：${header.outboundNo ?? ""}</div>
          <div class="header-row">出库类型：${header.outboundType ?? ""}</div>
          <div class="header-row">客户编号：${header.customerId ?? ""}</div>
          <div class="header-row">仓库编号：${header.warehouseId ?? ""}</div>
          <div class="header-row">总数量：${header.totalQty ?? ""}</div>
          <div class="header-row">总金额：${header.totalAmount ?? ""}</div>
          <table>
            <thead>
              <tr>
                <th>商品编号</th>
                <th>库位编号</th>
                <th>批次号</th>
                <th>数量</th>
                <th>单价</th>
                <th>金额</th>
              </tr>
            </thead>
            <tbody>${printTableRows}</tbody>
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

function submitForm() {
  proxy.$refs["outboundRef"].validate(valid => {
    if (valid) {
      if (form.value.outboundId != undefined) {
        updateOutbound(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addOutbound(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const outboundIds = currentRow.outboundId || selectedIds.value
  proxy.$modal.confirm('是否确认删除出库编号为"' + outboundIds + '"的数据项？').then(function() {
    return delOutbound(outboundIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleShip(currentRow) {
  resetShip()
  currentOutboundId.value = currentRow.outboundId
  shipOpen.value = true
}

function cancelShip() {
  shipOpen.value = false
  resetShip()
}

function submitShip() {
  outboundShip(currentOutboundId.value, shipForm.value).then(() => {
    shipOpen.value = false
    getList()
    proxy.$modal.msgSuccess("发货成功")
  })
}

function handleSign(currentRow) {
  proxy.$modal.confirm('是否确认签收出库编号为"' + currentRow.outboundId + '"的数据项？').then(function() {
    return outboundSign(currentRow.outboundId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("签收成功")
  }).catch(() => {})
}

function handleReturn(currentRow) {
  resetReturn()
  currentOutboundId.value = currentRow.outboundId
  returnOpen.value = true
}

function cancelReturn() {
  returnOpen.value = false
  resetReturn()
}

function submitReturn() {
  outboundReturn(currentOutboundId.value, returnForm.value).then(() => {
    returnOpen.value = false
    getList()
    proxy.$modal.msgSuccess("退货成功")
  })
}

getList()
</script>
