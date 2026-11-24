<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="应付单号" prop="payableNo">
        <el-input
          v-model="queryParams.payableNo"
          placeholder="请输入应付单号"
          clearable
          style="width: 170px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="queryParams.supplierId" placeholder="请选择供应商" clearable filterable style="width: 185px">
          <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="付款状态" clearable style="width: 135px">
          <el-option
            v-for="dict in apStatusOptions"
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
          v-hasPermi="['business:payable:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:payable:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:payable:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:payable:export', 'business:payable:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="payableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" align="center" />
      <el-table-column label="应付单号" align="center" prop="payableNo" min-width="156" show-overflow-tooltip />
      <el-table-column label="供应商" align="center" min-width="145" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getSupplierName(scope.row.supplierId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="来源单据" align="center" min-width="152" show-overflow-tooltip>
        <template #default="scope">
          <el-tag size="small" effect="plain" style="margin-right: 4px">{{ getBillTypeLabel(scope.row.billType) }}</el-tag>
          <span>{{ scope.row.sourceBillNo || (scope.row.billId ? `#${scope.row.billId}` : '-') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="方向" align="center" width="84">
        <template #default="scope">
          <el-tag :type="isRedRow(scope.row) ? 'danger' : 'primary'" size="small">
            {{ isRedRow(scope.row) ? '红字' : '正向' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="应付金额" align="right" header-align="right" prop="amount" width="98">
        <template #default="scope">
          <span :style="isRedRow(scope.row) ? 'color:#f56c6c;font-weight:600' : ''">{{ Number(scope.row.amount || 0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已付/冲" align="right" header-align="right" prop="paidAmount" width="92">
        <template #default="scope">{{ Number(scope.row.paidAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="剩余未付" align="right" header-align="right" width="98">
        <template #default="scope">
          <strong :style="calcUnsettled(scope.row) > 0 ? 'color:#d97706' : 'color:#10b981'">{{ calcUnsettled(scope.row).toFixed(2) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="付款状态" align="center" prop="status" width="88">
        <template #default="scope">
          <el-tag size="small" :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="bizStatus" width="80">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.bizStatus === 'confirmed' ? 'success' : 'info'">
            {{ scope.row.bizStatus === 'confirmed' ? '已确认' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="到期日期" align="center" prop="dueDate" width="104">
        <template #default="scope">
          <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button v-if="canAuditRow(scope.row)" link type="success" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:payable:audit']">审核</el-button>
            <el-button v-if="canOffsetRow(scope.row)" link type="danger" icon="Switch" @click="openOffsetDialog(scope.row)" v-hasPermi="['business:payable:edit']">对冲</el-button>
            <el-button link type="info" icon="List" @click="openOffsetLogDrawer(scope.row)">流水</el-button>
            <el-button link type="primary" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
            <el-dropdown v-if="canEditRow(scope.row) || canUnconfirmRow(scope.row)" trigger="click" @command="cmd => handleRowCommand(cmd, scope.row)">
              <el-button link type="primary">更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="canEditRow(scope.row)" command="edit">修改单据</el-dropdown-item>
                  <el-dropdown-item v-if="canUnconfirmRow(scope.row)" command="unconfirm">反确认</el-dropdown-item>
                  <el-dropdown-item v-if="canDeleteRow(scope.row)" command="delete" divided>删除单据</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
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
      <el-form ref="payableRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="应付单号" prop="payableNo">
              <el-input v-model="form.payableNo" placeholder="可不填，保存后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable style="width: 100%">
                <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="来源类型" prop="billType">
              <el-select v-model="form.billType" placeholder="默认手工应付" style="width: 100%">
                <el-option label="手工应付" value="manual" />
                <el-option label="采购入库" value="inbound" />
                <el-option label="采购发票" value="purchase_invoice" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应付金额" prop="amount">
              <el-input-number v-model="form.amount" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" disabled>
            <el-radio
              v-for="dict in apStatusOptions"
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

    <el-dialog title="红蓝应付单互冲核销" v-model="offsetDialog.visible" width="540px" append-to-body>
      <el-form label-width="110px">
        <el-form-item label="红字冲减单">
          <el-tag type="danger">{{ offsetDialog.redNo }}（待冲减 ¥{{ offsetDialog.redRemaining }}）</el-tag>
        </el-form-item>
        <el-form-item label="冲抵目标应付单">
          <el-select v-model="offsetDialog.targetPayableId" placeholder="请选择同供应商未结清正向应付单" filterable style="width: 100%" @change="onTargetApChange">
            <el-option
              v-for="item in offsetDialog.candidates"
              :key="item.payableId"
              :label="`${item.payableNo}（应付 ¥${item.amount} / 未付 ¥${(Number(item.amount) - Number(item.paidAmount || 0)).toFixed(2)}）`"
              :value="item.payableId"
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

    <el-drawer v-model="offsetLogDrawer.visible" :title="`红蓝应付对冲流水 - ${offsetLogDrawer.billNo}`" size="680px">
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
            <el-button v-if="s.row.status === '0'" link type="danger" @click="handleUnoffset(s.row)" v-hasPermi="['business:payable:unoffset']">撤销对冲</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="Payable">
import { listPayable, addPayable, delPayable, getPayable, updatePayable, auditPayable, unconfirmPayable, offsetPayable, listPayableOffsetLogs, unoffsetPayable } from "@/api/business/payable"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const { supplierOptions, loadSuppliers, getSupplierName } = useBusinessOptions()

const apStatusOptions = [
  { label: "未付", value: "0", tagType: "warning" },
  { label: "部分付款", value: "1", tagType: "primary" },
  { label: "已结清", value: "2", tagType: "success" }
]

function getStatusLabel(val) {
  const item = apStatusOptions.find(d => d.value === String(val ?? ""))
  return item ? item.label : (val || "-")
}

function getStatusTagType(val) {
  const item = apStatusOptions.find(d => d.value === String(val ?? ""))
  return item ? item.tagType : "info"
}

function getBillTypeLabel(val) {
  const map = {
    inbound: "采购入库",
    pur_order: "购货订单",
    purchase_invoice: "采购发票",
    purchase_return: "采购退货",
    manual: "手工应付"
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
  redPayableId: undefined,
  redNo: "",
  redRemaining: 0,
  targetPayableId: undefined,
  offsetAmount: 0,
  maxAmount: 0,
  candidates: []
})

function calcUnsettled(row) {
  return Number(row.amount || 0) - Number(row.paidAmount || 0)
}

const offsetLogDrawer = reactive({ visible: false, payableId: undefined, billNo: "", list: [] })

function openOffsetLogDrawer(row) {
  offsetLogDrawer.payableId = row.payableId
  offsetLogDrawer.billNo = row.payableNo
  listPayableOffsetLogs(row.payableId).then(res => {
    offsetLogDrawer.list = res.data || []
    offsetLogDrawer.visible = true
  })
}

function handleUnoffset(logRow) {
  proxy.$modal.confirm(`确认撤销该笔金额为 ￥${logRow.offsetAmount} 的红蓝应付对冲？`).then(() => unoffsetPayable(logRow.offsetId)).then(() => {
    proxy.$modal.msgSuccess("已撤销对冲并恢复往来余额")
    listPayableOffsetLogs(offsetLogDrawer.payableId).then(res => { offsetLogDrawer.list = res.data || [] })
    getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printFinancialDocument({
    title: isRedRow(row) ? "红字应付单" : "应付账款确认单",
    docNo: row.payableNo,
    amount: row.amount,
    metaList: [
      { label: "单据编号", value: row.payableNo },
      { label: "供应商", value: getSupplierName(row.supplierId) },
      { label: "单据类型", value: getBillTypeLabel(row.billType) },
      { label: "到期日期", value: proxy.parseTime(row.dueDate, "{y}-{m}-{d}") },
      { label: "审核状态", value: row.bizStatus === "confirmed" ? "已确认" : "草稿" },
      { label: "付款状态", value: getStatusLabel(row.status) }
    ],
    columns: [
      { label: "应付总额", prop: "amount", align: "right" },
      { label: "已付/已冲", prop: "paidAmount", align: "right" },
      { label: "剩余未付", prop: "unsettled", align: "right" }
    ],
    rows: [{
      amount: Number(row.amount || 0).toFixed(2),
      paidAmount: Number(row.paidAmount || 0).toFixed(2),
      unsettled: calcUnsettled(row).toFixed(2)
    }],
    remark: row.remark,
    createBy: row.createBy
  })
}

function openOffsetDialog(row) {
  const redRemaining = Number((Math.abs(Number(row.amount || 0)) - Math.abs(Number(row.paidAmount || 0))).toFixed(2))
  offsetDialog.redPayableId = row.payableId
  offsetDialog.redNo = row.payableNo
  offsetDialog.redRemaining = redRemaining
  offsetDialog.targetPayableId = undefined
  offsetDialog.offsetAmount = redRemaining
  offsetDialog.maxAmount = redRemaining
  offsetDialog.candidates = []
  listPayable({ pageNum: 1, pageSize: 100, supplierId: row.supplierId, bizStatus: "confirmed", billDirection: "1" }).then(res => {
    offsetDialog.candidates = (res.rows || []).filter(r => Number(r.amount || 0) - Number(r.paidAmount || 0) > 0)
    offsetDialog.visible = true
  })
}

function onTargetApChange(targetId) {
  const target = offsetDialog.candidates.find(c => c.payableId === targetId)
  if (!target) return
  const unpaid = Number((Number(target.amount || 0) - Number(target.paidAmount || 0)).toFixed(2))
  const maxAmt = Math.min(offsetDialog.redRemaining, unpaid)
  offsetDialog.maxAmount = maxAmt
  offsetDialog.offsetAmount = maxAmt
}

function submitOffset() {
  if (!offsetDialog.targetPayableId) {
    proxy.$modal.msgWarning("请选择要冲抵的正向应付单")
    return
  }
  offsetDialog.submitting = true
  offsetPayable({
    redPayableId: offsetDialog.redPayableId,
    targetPayableId: offsetDialog.targetPayableId,
    offsetAmount: offsetDialog.offsetAmount
  }).then(() => {
    proxy.$modal.msgSuccess("红蓝应付对冲成功")
    offsetDialog.visible = false
    getList()
  }).finally(() => {
    offsetDialog.submitting = false
  })
}

const payableList = ref([])
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
    payableNo: undefined,
    supplierId: undefined,
    status: undefined
  },
  rules: {
    supplierId: [{ required: true, message: "请选择供应商", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listPayable(queryParams.value).then(response => {
    payableList.value = response.rows
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
    payableId: undefined,
    payableNo: undefined,
    supplierId: undefined,
    billType: undefined,
    billId: undefined,
    amount: undefined,
    paidAmount: undefined,
    status: "0",
    dueDate: undefined,
    remark: undefined
  }
  proxy.resetForm("payableRef")
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
  selectedIds.value = selection.map(item => item.payableId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(item => !canDeleteRow(item))
}

// 判断应付单是否允许修改
function canEditRow(currentRow) {
  return !!currentRow && currentRow.bizStatus !== "confirmed"
}

// 判断应付单是否允许删除
function canDeleteRow(currentRow) {
  return canEditRow(currentRow)
}

// 判断应付单是否允许审核
function canAuditRow(currentRow) {
  return !!currentRow && currentRow.bizStatus !== "confirmed"
}

function canUnconfirmRow(currentRow) {
  return !!currentRow && currentRow.bizStatus === "confirmed" && Number(currentRow.paidAmount || 0) === 0 && !isRedRow(currentRow) && (!currentRow.billType || currentRow.billType === "manual")
}

function handleUnconfirm(currentRow) {
  proxy.$modal.confirm(`确认反确认手工应付单"${currentRow.payableNo}"？`).then(() => unconfirmPayable(currentRow.payableId)).then(() => {
    proxy.$modal.msgSuccess("反确认成功")
    getList()
  }).catch(() => {})
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加应付单"
}

function handleUpdate(currentRow) {
  reset()
  const payableId = currentRow.payableId || selectedIds.value
  getPayable(payableId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改应付单"
  })
}

function handleAudit(currentRow) {
  proxy.$modal.confirm('是否确认审核应付编号为"' + currentRow.payableId + '"的数据项？').then(function() {
    return auditPayable(currentRow.payableId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("审核成功")
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs["payableRef"].validate(valid => {
    if (valid) {
      if (form.value.payableId != undefined) {
        updatePayable(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPayable(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleRowCommand(cmd, row) {
  if (cmd === "edit") handleUpdate(row)
  else if (cmd === "unconfirm") handleUnconfirm(row)
  else if (cmd === "delete") handleDelete(row)
}

function handleDelete(currentRow) {
  const payableIds = currentRow.payableId || selectedIds.value
  proxy.$modal.confirm('是否确认删除应付编号为"' + payableIds + '"的数据项？').then(function() {
    return delPayable(payableIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

loadSuppliers()
getList()

function handleExport() {
  proxy.download("business/payable/export", { ...queryParams.value }, `应付账款_${new Date().getTime()}.xlsx`)
}
</script>
