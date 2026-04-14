<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="入库单号" prop="inboundNo">
        <el-input
          v-model="queryParams.inboundNo"
          placeholder="请输入入库单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入库类型" prop="inboundType">
        <el-input
          v-model="queryParams.inboundType"
          placeholder="请输入入库类型"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="入库状态" clearable style="width: 200px">
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
          v-hasPermi="['business:inbound:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:inbound:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:inbound:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="inboundList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入库编号" align="center" prop="inboundId" />
      <el-table-column label="入库单号" align="center" prop="inboundNo" />
      <el-table-column label="入库类型" align="center" prop="inboundType" />
      <el-table-column label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="总数量" align="center" prop="totalQty" />
      <el-table-column label="总金额" align="center" prop="totalAmount" />
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
      <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:inbound:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:inbound:remove']">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:inbound:audit']">审核</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)" v-hasPermi="['business:inbound:print']">打印</el-button>
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
      <el-form ref="inboundRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="入库单号" prop="inboundNo">
              <el-input v-model="form.inboundNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库类型" prop="inboundType">
              <el-input v-model="form.inboundType" placeholder="请输入入库类型" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="供应商编号" prop="supplierId">
              <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
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
  </div>
</template>

<script setup name="Inbound">
import { listInbound, addInbound, delInbound, getInbound, updateInbound, auditInbound, printInbound } from "@/api/business/inbound"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const inboundList = ref([])
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
    inboundNo: undefined,
    inboundType: undefined,
    status: undefined
  },
  rules: {
    inboundType: [{ required: true, message: "入库类型不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listInbound(queryParams.value).then(response => {
    inboundList.value = response.rows
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
    inboundId: undefined,
    inboundNo: undefined,
    inboundType: undefined,
    supplierId: undefined,
    warehouseId: undefined,
    totalQty: undefined,
    totalAmount: undefined,
    status: "0",
    auditBy: undefined,
    auditTime: undefined,
    remark: undefined
  }
  proxy.resetForm("inboundRef")
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
  selectedIds.value = selection.map(item => item.inboundId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加入库单"
}

function handleUpdate(currentRow) {
  reset()
  const inboundId = currentRow.inboundId || selectedIds.value
  getInbound(inboundId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改入库单"
  })
}

function handleAudit(row) {
  const inboundId = row.inboundId
  proxy.$modal.confirm("确认审核当前入库单吗？").then(function() {
    return auditInbound(inboundId)
  }).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {})
}

function handlePrint(currentRow) {
  printInbound(currentRow.inboundId).then(response => {
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
          <title>入库单打印</title>
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
          <h2>入库单</h2>
          <div class="header-row">入库单号：${header.inboundNo ?? ""}</div>
          <div class="header-row">入库类型：${header.inboundType ?? ""}</div>
          <div class="header-row">供应商编号：${header.supplierId ?? ""}</div>
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
  proxy.$refs["inboundRef"].validate(valid => {
    if (valid) {
      if (form.value.inboundId != undefined) {
        updateInbound(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addInbound(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const inboundIds = currentRow.inboundId || selectedIds.value
  proxy.$modal.confirm('是否确认删除入库编号为"' + inboundIds + '"的数据项？').then(function() {
    return delInbound(inboundIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
