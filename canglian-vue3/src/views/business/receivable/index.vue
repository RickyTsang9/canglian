<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="应收单号" prop="receivableNo">
        <el-input
          v-model="queryParams.receivableNo"
          placeholder="请输入应收单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 220px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="收款状态" clearable style="width: 200px">
          <el-option
            v-for="dict in arStatusOptions"
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
          v-hasPermi="['business:receivable:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:receivable:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:receivable:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="receivableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="应收编号" align="center" prop="receivableId" />
      <el-table-column label="应收单号" align="center" prop="receivableNo" min-width="140" />
      <el-table-column label="客户" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getCustomerName(scope.row.customerId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单据类型" align="center" prop="billType" width="115">
        <template #default="scope">
          <el-tag effect="plain">{{ getBillTypeLabel(scope.row.billType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="单据编号" align="center" prop="billId" />
      <el-table-column label="方向" align="center" width="95">
        <template #default="scope">
          <el-tag :type="isRedRow(scope.row) ? 'danger' : 'primary'" size="small">
            {{ isRedRow(scope.row) ? '红字冲减' : '正向应收' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="应收金额" align="right" prop="amount" width="100">
        <template #default="scope">
          <span :style="isRedRow(scope.row) ? 'color:#f56c6c;font-weight:600' : ''">{{ scope.row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已收/已冲" align="right" prop="receivedAmount" width="95" />
      <el-table-column label="坏账核销" align="right" width="90">
        <template #default="scope">{{ Number(scope.row.badDebtAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="剩余未收" align="right" width="100">
        <template #default="scope">
          <strong :style="calcUnsettled(scope.row) > 0 ? 'color:#e6a23c' : 'color:#67c23a'">{{ calcUnsettled(scope.row).toFixed(2) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="收款状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="bizStatus" width="85">
        <template #default="scope">
          <el-tag :type="scope.row.bizStatus === 'confirmed' ? 'success' : 'info'">
            {{ scope.row.bizStatus === 'confirmed' ? '已确认' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="到期日期" align="center" prop="dueDate" width="110">
        <template #default="scope">
          <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="360" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" :disabled="!canEditRow(scope.row)" @click="handleUpdate(scope.row)" v-hasPermi="['business:receivable:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" :disabled="!canDeleteRow(scope.row)" @click="handleDelete(scope.row)" v-hasPermi="['business:receivable:remove']">删除</el-button>
          <el-button link type="success" icon="CircleCheck" v-if="canAuditRow(scope.row)" @click="handleAudit(scope.row)" v-hasPermi="['business:receivable:audit']">审核</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="canUnconfirmRow(scope.row)" @click="handleUnconfirm(scope.row)" v-hasPermi="['business:receivable:audit']">反确认</el-button>
          <el-button v-if="canOffsetRow(scope.row)" link type="danger" icon="Switch" @click="openOffsetDialog(scope.row)" v-hasPermi="['business:receivable:edit']">红蓝对冲</el-button>
          <el-button link type="info" icon="List" @click="openOffsetLogDrawer(scope.row)">对冲流水</el-button>
          <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
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
      <el-form ref="receivableRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="应收单号" prop="receivableNo">
              <el-input v-model="form.receivableNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
                <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="来源类型" prop="billType">
              <el-select v-model="form.billType" placeholder="默认手工应收" style="width: 100%">
                <el-option label="手工应收" value="manual" />
                <el-option label="销售出库" value="outbound" />
                <el-option label="销售发票" value="sale_invoice" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应收金额" prop="amount">
              <el-input-number v-model="form.amount" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" disabled>
            <el-radio
              v-for="dict in arStatusOptions"
              :key="dict.value"
              :value="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="到期日期" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择到期日期"
          ></el-date-picker>
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

    <el-dialog title="红蓝应收单互冲核销" v-model="offsetDialog.visible" width="540px" append-to-body>
      <el-form label-width="110px">
        <el-form-item label="红字冲减单">
          <el-tag type="danger">{{ offsetDialog.redNo }}（待冲减 ¥{{ offsetDialog.redRemaining }}）</el-tag>
        </el-form-item>
        <el-form-item label="冲抵目标应收单">
          <el-select v-model="offsetDialog.targetReceivableId" placeholder="请选择同客户未结清正向应收单" filterable style="width: 100%" @change="onTargetArChange">
            <el-option
              v-for="item in offsetDialog.candidates"
              :key="item.receivableId"
              :label="`${item.receivableNo}（应收 ¥${item.amount} / 未收 ¥${(Number(item.amount) - Number(item.receivedAmount || 0)).toFixed(2)}）`"
              :value="item.receivableId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="本次对冲金额">
          <el-input-number v-model="offsetDialog.offsetAmount" :min="0.01" :max="offsetDialog.maxAmount" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="offsetDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="offsetDialog.submitting" @click="submitOffset">确认对冲</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="offsetLogDrawer.visible" :title="`红蓝应收对冲流水 - ${offsetLogDrawer.billNo}`" size="680px">
      <el-table :data="offsetLogDrawer.list" border size="small">
        <el-table-column label="对冲金额" align="right" prop="offsetAmount" width="105" />
        <el-table-column label="对冲时间" align="center" width="160">
          <template #default="s">{{ parseTime(s.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="90">
          <template #default="s">
            <el-tag :type="s.row.status === '0' ? 'success' : 'info'">{{ s.row.status === '0' ? '生效' : '已撤销' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作人" prop="createBy" width="90" align="center" />
        <el-table-column label="操作" width="105" align="center">
          <template #default="s">
            <el-button v-if="s.row.status === '0'" link type="danger" @click="handleUnoffset(s.row)" v-hasPermi="['business:receivable:edit']">撤销对冲</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="Receivable">
import { listReceivable, addReceivable, delReceivable, getReceivable, updateReceivable, auditReceivable, unconfirmReceivable, offsetReceivable, listReceivableOffsetLogs, unoffsetReceivable } from "@/api/business/receivable"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const { customerOptions, loadCustomers, getCustomerName } = useBusinessOptions()

const arStatusOptions = [
  { label: "未收", value: "0", tagType: "warning" },
  { label: "部分收款", value: "1", tagType: "primary" },
  { label: "已结清", value: "2", tagType: "success" }
]

function getStatusLabel(val) {
  const item = arStatusOptions.find(d => d.value === String(val ?? ""))
  return item ? item.label : (val || "-")
}

function getStatusTagType(val) {
  const item = arStatusOptions.find(d => d.value === String(val ?? ""))
  return item ? item.tagType : "info"
}

function getBillTypeLabel(val) {
  const map = {
    outbound: "销售出库",
    outbound_return: "出库退货",
    sale_order: "销货订单",
    sale_invoice: "销售发票",
    sale_return: "销售退货",
    manual: "手工应收"
  }
  return map[val] || val || "-"
}

function isRedRow(row) {
  return !!row && (row.billDirection === "2" || Number(row.amount || 0) < 0)
}

function canOffsetRow(row) {
  return isRedRow(row) && row.bizStatus === "confirmed" && row.status !== "2"
}

const offsetDialog = reactive({
  visible: false,
  submitting: false,
  redReceivableId: undefined,
  redNo: "",
  redRemaining: 0,
  targetReceivableId: undefined,
  offsetAmount: 0,
  maxAmount: 0,
  candidates: []
})

function calcUnsettled(row) {
  return Number(row.amount || 0) - Number(row.receivedAmount || 0) - Number(row.badDebtAmount || 0)
}

const offsetLogDrawer = reactive({ visible: false, receivableId: undefined, billNo: "", list: [] })

function openOffsetLogDrawer(row) {
  offsetLogDrawer.receivableId = row.receivableId
  offsetLogDrawer.billNo = row.receivableNo
  listReceivableOffsetLogs(row.receivableId).then(res => {
    offsetLogDrawer.list = res.data || []
    offsetLogDrawer.visible = true
  })
}

function handleUnoffset(logRow) {
  proxy.$modal.confirm(`确认撤销该笔金额为 ￥${logRow.offsetAmount} 的红蓝应收对冲？`).then(() => unoffsetReceivable(logRow.offsetId)).then(() => {
    proxy.$modal.msgSuccess("已撤销对冲并恢复往来余额")
    listReceivableOffsetLogs(offsetLogDrawer.receivableId).then(res => { offsetLogDrawer.list = res.data || [] })
    getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printFinancialDocument({
    title: isRedRow(row) ? "红字应收单" : "应收账款确认单",
    docNo: row.receivableNo,
    amount: row.amount,
    metaList: [
      { label: "单据编号", value: row.receivableNo },
      { label: "客户名称", value: getCustomerName(row.customerId) },
      { label: "单据类型", value: getBillTypeLabel(row.billType) },
      { label: "到期日期", value: proxy.parseTime(row.dueDate, "{y}-{m}-{d}") },
      { label: "审核状态", value: row.bizStatus === "confirmed" ? "已确认" : "草稿" },
      { label: "收款状态", value: getStatusLabel(row.status) }
    ],
    columns: [
      { label: "应收总额", prop: "amount", align: "right" },
      { label: "已收/已冲", prop: "receivedAmount", align: "right" },
      { label: "坏账核销", prop: "badDebtAmount", align: "right" },
      { label: "剩余未收", prop: "unsettled", align: "right" }
    ],
    rows: [{
      amount: Number(row.amount || 0).toFixed(2),
      receivedAmount: Number(row.receivedAmount || 0).toFixed(2),
      badDebtAmount: Number(row.badDebtAmount || 0).toFixed(2),
      unsettled: calcUnsettled(row).toFixed(2)
    }],
    remark: row.remark,
    createBy: row.createBy
  })
}

function openOffsetDialog(row) {
  const redRemaining = Number((Math.abs(Number(row.amount || 0)) - Math.abs(Number(row.receivedAmount || 0))).toFixed(2))
  offsetDialog.redReceivableId = row.receivableId
  offsetDialog.redNo = row.receivableNo
  offsetDialog.redRemaining = redRemaining
  offsetDialog.targetReceivableId = undefined
  offsetDialog.offsetAmount = redRemaining
  offsetDialog.maxAmount = redRemaining
  offsetDialog.candidates = []
  listReceivable({ pageNum: 1, pageSize: 100, customerId: row.customerId, bizStatus: "confirmed", billDirection: "1" }).then(res => {
    offsetDialog.candidates = (res.rows || []).filter(r => calcUnsettled(r) > 0.005)
    offsetDialog.visible = true
  })
}

function onTargetArChange(targetId) {
  const target = offsetDialog.candidates.find(c => c.receivableId === targetId)
  if (!target) return
  const unpaid = Number(calcUnsettled(target).toFixed(2))
  const maxAmt = Math.min(offsetDialog.redRemaining, unpaid)
  offsetDialog.maxAmount = maxAmt
  offsetDialog.offsetAmount = maxAmt
}

function submitOffset() {
  if (!offsetDialog.targetReceivableId) {
    proxy.$modal.msgWarning("请选择要冲抵的正向应收单")
    return
  }
  offsetDialog.submitting = true
  offsetReceivable({
    redReceivableId: offsetDialog.redReceivableId,
    targetReceivableId: offsetDialog.targetReceivableId,
    offsetAmount: offsetDialog.offsetAmount
  }).then(() => {
    proxy.$modal.msgSuccess("红蓝应收对冲成功")
    offsetDialog.visible = false
    getList()
  }).finally(() => {
    offsetDialog.submitting = false
  })
}

const receivableList = ref([])
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
    receivableNo: undefined,
    customerId: undefined,
    status: undefined
  },
  rules: {
    customerId: [{ required: true, message: "请选择客户", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listReceivable(queryParams.value).then(response => {
    receivableList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    receivableId: undefined,
    receivableNo: undefined,
    customerId: undefined,
    billType: undefined,
    billId: undefined,
    amount: undefined,
    receivedAmount: undefined,
    status: "0",
    dueDate: undefined,
    remark: undefined
  }
  proxy.resetForm("receivableRef")
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
  selectedIds.value = selection.map(item => item.receivableId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(item => !canDeleteRow(item))
}

// 判断应收单是否允许修改
function canEditRow(currentRow) {
  return !!currentRow && currentRow.bizStatus !== "confirmed"
}

// 判断应收单是否允许删除
function canDeleteRow(currentRow) {
  return canEditRow(currentRow)
}

// 判断应收单是否允许审核
function canAuditRow(currentRow) {
  return !!currentRow && currentRow.bizStatus !== "confirmed"
}

function canUnconfirmRow(currentRow) {
  return !!currentRow && currentRow.bizStatus === "confirmed" && Number(currentRow.receivedAmount || 0) === 0 && !isRedRow(currentRow) && (!currentRow.billType || currentRow.billType === "manual")
}

function handleUnconfirm(currentRow) {
  proxy.$modal.confirm(`确认反确认手工应收单"${currentRow.receivableNo}"？`).then(() => unconfirmReceivable(currentRow.receivableId)).then(() => {
    proxy.$modal.msgSuccess("反确认成功")
    getList()
  }).catch(() => {})
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加应收单"
}

function handleUpdate(currentRow) {
  reset()
  const receivableId = currentRow.receivableId || selectedIds.value
  getReceivable(receivableId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改应收单"
  })
}

function handleAudit(currentRow) {
  proxy.$modal.confirm('是否确认审核应收编号为"' + currentRow.receivableId + '"的数据项？').then(function() {
    return auditReceivable(currentRow.receivableId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("审核成功")
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs["receivableRef"].validate(valid => {
    if (valid) {
      if (form.value.receivableId != undefined) {
        updateReceivable(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addReceivable(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const receivableIds = currentRow.receivableId || selectedIds.value
  proxy.$modal.confirm('是否确认删除应收编号为"' + receivableIds + '"的数据项？').then(function() {
    return delReceivable(receivableIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

loadCustomers()
getList()
</script>
