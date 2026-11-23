<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="发票单号" prop="invoiceNo">
        <el-input v-model="queryParams.invoiceNo" placeholder="请输入发票单号" clearable style="width: 155px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item v-if="isSaleInvoice" label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 160px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerName" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item v-else label="供应商" prop="supplierId">
        <el-select v-model="queryParams.supplierId" placeholder="请选择供应商" clearable filterable style="width: 160px">
          <el-option v-for="s in supplierOptions" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
        </el-select>
      </el-form-item>
      <el-form-item label="红蓝属性" prop="invoiceKind">
        <el-select v-model="queryParams.invoiceKind" placeholder="全部" clearable style="width: 110px">
          <el-option label="蓝字发票" value="blue" />
          <el-option label="红字冲销" value="red" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务状态" prop="bizStatus">
        <el-select v-model="queryParams.bizStatus" placeholder="业务状态" clearable style="width: 112px">
          <el-option v-for="s in bizStatusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:invoice:add']">新增蓝票</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:invoice:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:invoice:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="invoiceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" align="center" />
      <el-table-column label="发票单号" align="center" min-width="155" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.invoiceNo }}</span>
          <el-tag :type="scope.row.invoiceKind === 'red' ? 'danger' : 'primary'" size="small" style="margin-left: 4px">
            {{ scope.row.invoiceKind === 'red' ? '红票' : '蓝票' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="isSaleInvoice" label="客户" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column v-else label="供应商" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ getSupplierName(scope.row.supplierId) }}</template>
      </el-table-column>
      <el-table-column label="来源类型" align="center" width="96">
        <template #default="scope">{{ getSourceBillTypeLabel(scope.row.sourceBillType) }}</template>
      </el-table-column>
      <el-table-column label="来源单号" align="center" prop="sourceBillNo" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.sourceBillNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="含税金额" align="right" header-align="right" width="96">
        <template #default="scope">
          <span :style="scope.row.invoiceKind === 'red' ? 'color:#f56c6c;font-weight:600' : ''">{{ Number(scope.row.invoiceAmount || 0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="税率%" align="right" header-align="right" prop="taxRate" width="68" />
      <el-table-column label="税额" align="right" header-align="right" prop="taxAmount" width="84">
        <template #default="scope">{{ Number(scope.row.taxAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="不含税额" align="right" header-align="right" prop="untaxedAmount" width="94">
        <template #default="scope">{{ Number(scope.row.untaxedAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="业务状态" align="center" width="78">
        <template #default="scope"><dict-tag :options="bizStatusOptions" :value="scope.row.bizStatus" /></template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:invoice:edit']">修改</el-button>
            <el-button v-if="canConfirmRow(scope.row)" link type="success" icon="CircleCheck" @click="handleConfirm(scope.row)" v-hasPermi="['business:invoice:confirm']">确认</el-button>
            <el-button v-if="canRedRow(scope.row)" link type="danger" icon="DocumentDelete" @click="openRedDialog(scope.row)" v-hasPermi="['business:invoice:red']">红冲</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:invoice:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改蓝字发票 -->
    <el-dialog :title="title" v-model="open" width="780px" append-to-body>
      <el-form ref="invoiceRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="发票单号" prop="invoiceNo"><el-input v-model="form.invoiceNo" placeholder="留空自动生成" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item v-if="isSaleInvoice" label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%" @change="handleCounterpartyChange">
                <el-option v-for="c in activeCustomerOptions" :key="c.customerId" :label="c.customerName" :value="c.customerId" />
              </el-select>
            </el-form-item>
            <el-form-item v-else label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable style="width: 100%" @change="handleCounterpartyChange">
                <el-option v-for="s in activeSupplierOptions" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开票日期" prop="invoiceDate">
              <el-date-picker v-model="form.invoiceDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开票日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="来源单据类型" prop="sourceBillType">
              <el-select v-model="form.sourceBillType" placeholder="可选：绑定源单控额" clearable style="width: 100%" @change="handleSourceTypeChange">
                <el-option v-for="opt in sourceBillTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联来源单据" prop="sourceBillId">
              <el-select
                v-if="form.sourceBillType"
                v-model="form.sourceBillId"
                placeholder="请选择已审核/已签收源单"
                clearable
                filterable
                style="width: 100%"
                @change="handleSourceBillChange"
              >
                <el-option v-for="b in sourceBillOptions" :key="b.id" :label="b.label" :value="b.id" />
              </el-select>
              <el-input v-else v-model="form.sourceBillNo" placeholder="未选类型时可手工填写来源单号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="含税金额" prop="invoiceAmount">
              <el-input-number v-model="form.invoiceAmount" controls-position="right" :min="0.01" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="税率(%)" prop="taxRate">
              <el-input-number v-model="form.taxRate" controls-position="right" :min="0" :max="100" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="价税拆算预览">
          <el-alert
            type="info"
            :closable="false"
            show-icon
            :title="`不含税金额：￥${vatPreview.untaxed.toFixed(2)}  |  增值税额（${Number(form.taxRate || 0)}%，含税反算）：￥${vatPreview.tax.toFixed(2)}  |  价税合计：￥${Number(form.invoiceAmount || 0).toFixed(2)}`"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 红字发票冲销弹窗 -->
    <el-dialog title="开具红字冲销发票" v-model="redOpen" width="480px" append-to-body>
      <el-form :model="redForm" label-width="105px">
        <el-form-item label="原蓝字发票"><strong>{{ redForm.originInvoiceNo }}</strong></el-form-item>
        <el-form-item label="原蓝票含税额">￥{{ Number(redForm.originAmount || 0).toFixed(2) }}</el-form-item>
        <el-form-item label="本次红冲金额" required>
          <el-input-number v-model="redForm.amount" :min="0.01" :max="Number(redForm.originAmount || 0)" :precision="2" controls-position="right" style="width: 100%" />
          <div style="font-size: 11px; color: #909399; line-height: 1.4">支持全额或部分红冲，系统自动生成负数红票并冲减往来/税额</div>
        </el-form-item>
        <el-form-item label="红冲原因">
          <el-input v-model="redForm.remark" placeholder="如：销货退回/开票折让红冲" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="danger" @click="submitRedInvoice">确认开具红票</el-button>
        <el-button @click="redOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Invoice">
import { useRoute } from "vue-router"
import { listInvoice, getInvoice, addInvoice, updateInvoice, confirmInvoice, issueRedInvoice, delInvoice } from "@/api/business/invoice"
import { listSaleOrder } from "@/api/business/saleOrder"
import { listPurOrder } from "@/api/business/purOrder"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printFinancialDocument } from "@/utils/documentPrint"

const route = useRoute()
const invoiceType = computed(() => route.query.invoiceType || (route.path.includes("purchaseInvoice") ? "purchase" : "sale"))
const isSaleInvoice = computed(() => invoiceType.value === "sale")
const invoiceTitle = computed(() => isSaleInvoice.value ? "销售发票" : "采购发票")

const { proxy } = getCurrentInstance()
const {
  customerOptions,
  supplierOptions,
  activeCustomerOptions,
  activeSupplierOptions,
  loadCustomers,
  loadSuppliers,
  fetchSignedOutboundOptions,
  fetchAuditedInboundOptions,
  fetchOpenReceivables,
  fetchOpenPayables,
  getCustomerName,
  getSupplierName
} = useBusinessOptions()

loadCustomers(); loadSuppliers()

const invoiceList = ref([])
const sourceBillOptions = ref([])
const open = ref(false)
const redOpen = ref(false)
const redForm = reactive({ originInvoiceId: undefined, originInvoiceNo: "", originAmount: 0, amount: 0, remark: "" })
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const bizStatusOptions = [
  { label: "草稿", value: "draft" },
  { label: "已确认", value: "confirmed" }
]

const sourceBillTypeLabelMap = {
  outbound: "销售出库单",
  wms_outbound: "销售出库单",
  sale_order: "销售订单",
  sal_order: "销售订单",
  receivable: "应收账款",
  inbound: "采购入库单",
  wms_inbound: "采购入库单",
  pur_order: "采购订单",
  payable: "应付账款"
}

const sourceBillTypeOptions = computed(() => isSaleInvoice.value
  ? [
    { label: "销售出库单（已签收）", value: "outbound" },
    { label: "销售订单", value: "sale_order" },
    { label: "应收账款单", value: "receivable" }
  ]
  : [
    { label: "采购入库单（已审核）", value: "inbound" },
    { label: "采购订单", value: "pur_order" },
    { label: "应付账款单", value: "payable" }
  ]
)

function getSourceBillTypeLabel(t) {
  return sourceBillTypeLabelMap[t] || (t || "-")
}

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, invoiceNo: undefined, invoiceType: undefined, invoiceKind: undefined, customerId: undefined, supplierId: undefined, bizStatus: undefined },
  rules: {
    customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
    supplierId: [{ required: true, message: "请选择供应商", trigger: "change" }],
    invoiceAmount: [{ required: true, message: "请输入含税金额", trigger: "blur" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

// 与后端 FinInvoiceServiceImpl.fillInvoiceAmount 完全一致的 13% 含税反算预览
const vatPreview = computed(() => {
  const amt = Number(form.value.invoiceAmount || 0)
  const rawRate = Number(form.value.taxRate || 0)
  const effectiveRate = rawRate > 1 ? rawRate / 100 : rawRate
  if (amt <= 0) return { untaxed: 0, tax: 0 }
  const untaxed = Number((amt / (1 + effectiveRate)).toFixed(2))
  const tax = Number((amt - untaxed).toFixed(2))
  return { untaxed, tax }
})

async function loadSourceBills(type) {
  sourceBillOptions.value = []
  if (!type) return
  try {
    if (type === "outbound") {
      const rows = await fetchSignedOutboundOptions(form.value.customerId)
      sourceBillOptions.value = rows.map(r => ({
        id: r.outboundId,
        billNo: r.outboundNo,
        counterpartyId: r.customerId,
        amount: Number(r.totalAmount || 0),
        label: `${r.outboundNo} (${getCustomerName(r.customerId)} | ￥${Number(r.totalAmount || 0).toFixed(2)})`
      }))
    } else if (type === "inbound") {
      const rows = await fetchAuditedInboundOptions(form.value.supplierId)
      sourceBillOptions.value = rows.map(r => ({
        id: r.inboundId,
        billNo: r.inboundNo,
        counterpartyId: r.supplierId,
        amount: Number(r.totalAmount || 0),
        label: `${r.inboundNo} (${getSupplierName(r.supplierId)} | ￥${Number(r.totalAmount || 0).toFixed(2)})`
      }))
    } else if (type === "receivable") {
      const rows = await fetchOpenReceivables(form.value.customerId)
      sourceBillOptions.value = rows.map(r => ({
        id: r.receivableId,
        billNo: r.billNo || `AR-${r.receivableId}`,
        counterpartyId: r.customerId,
        amount: Number(r.amount || 0),
        label: `${r.billNo || `AR-${r.receivableId}`} (${getCustomerName(r.customerId)} | ￥${Number(r.amount || 0).toFixed(2)})`
      }))
    } else if (type === "payable") {
      const rows = await fetchOpenPayables(form.value.supplierId)
      sourceBillOptions.value = rows.map(r => ({
        id: r.payableId,
        billNo: r.billNo || `AP-${r.payableId}`,
        counterpartyId: r.supplierId,
        amount: Number(r.amount || 0),
        label: `${r.billNo || `AP-${r.payableId}`} (${getSupplierName(r.supplierId)} | ￥${Number(r.amount || 0).toFixed(2)})`
      }))
    } else if (type === "sale_order") {
      const res = await listSaleOrder({ pageNum: 1, pageSize: 200, customerId: form.value.customerId })
      sourceBillOptions.value = (res.rows || []).filter(r => r.status === "1" || r.bizStatus === "audited").map(r => {
        const amt = Number(r.taxTotalAmount || r.totalAmount || 0)
        return {
          id: r.orderId,
          billNo: r.orderNo,
          counterpartyId: r.customerId,
          amount: amt,
          label: `${r.orderNo} (${getCustomerName(r.customerId)} | ￥${amt.toFixed(2)})`
        }
      })
    } else if (type === "pur_order") {
      const res = await listPurOrder({ pageNum: 1, pageSize: 200, supplierId: form.value.supplierId })
      sourceBillOptions.value = (res.rows || []).filter(r => r.status === "1" || r.bizStatus === "audited").map(r => {
        const amt = Number(r.taxTotalAmount || r.totalAmount || 0)
        return {
          id: r.purchaseOrderId,
          billNo: r.orderNo,
          counterpartyId: r.supplierId,
          amount: amt,
          label: `${r.orderNo} (${getSupplierName(r.supplierId)} | ￥${amt.toFixed(2)})`
        }
      })
    }
  } catch (e) {
    sourceBillOptions.value = []
  }
}

function handleCounterpartyChange() {
  if (form.value.sourceBillType) {
    loadSourceBills(form.value.sourceBillType)
  }
}

function handleSourceTypeChange(type) {
  form.value.sourceBillId = undefined
  form.value.sourceBillNo = undefined
  loadSourceBills(type)
}

function handleSourceBillChange(id) {
  const match = sourceBillOptions.value.find(b => b.id === id)
  if (!match) return
  form.value.sourceBillNo = match.billNo
  if (isSaleInvoice.value && !form.value.customerId && match.counterpartyId) {
    form.value.customerId = match.counterpartyId
  }
  if (!isSaleInvoice.value && !form.value.supplierId && match.counterpartyId) {
    form.value.supplierId = match.counterpartyId
  }
  if (!form.value.invoiceAmount || Number(form.value.invoiceAmount) <= 0) {
    form.value.invoiceAmount = match.amount
  }
}

function getList() {
  loading.value = true
  queryParams.value.invoiceType = invoiceType.value
  listInvoice(queryParams.value).then(res => {
    invoiceList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }
function reset() {
  const today = new Date().toISOString().slice(0, 10)
  sourceBillOptions.value = []
  form.value = {
    invoiceId: undefined,
    invoiceNo: undefined,
    invoiceType: invoiceType.value,
    invoiceKind: "blue",
    customerId: undefined,
    supplierId: undefined,
    sourceBillType: undefined,
    sourceBillId: undefined,
    sourceBillNo: undefined,
    businessDate: today,
    invoiceDate: today,
    invoiceAmount: undefined,
    taxRate: 13,
    bizStatus: "draft",
    remark: undefined
  }
  proxy.resetForm("invoiceRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.invoiceId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canEditRow(i))
}
function canEditRow(r) { return !!r && r.bizStatus === "draft" }
function canConfirmRow(r) { return canEditRow(r) }
function canRedRow(r) { return !!r && r.bizStatus === "confirmed" && r.invoiceKind !== "red" && Number(r.invoiceAmount || 0) > 0 }

function handleAdd() { reset(); open.value = true; title.value = "新增" + invoiceTitle.value }
function handleUpdate(row) {
  reset()
  getInvoice(row.invoiceId || selectedIds.value).then(res => {
    form.value = res.data || {}
    form.value.invoiceAmount = Number(form.value.invoiceAmount || 0)
    form.value.taxRate = Number(form.value.taxRate ?? 13)
    if (form.value.sourceBillType) loadSourceBills(form.value.sourceBillType)
    open.value = true; title.value = "修改" + invoiceTitle.value
  })
}

function handleConfirm(row) {
  proxy.$modal.confirm(`确认当前"${invoiceTitle.value}"？确认后将按税额生成销项/进项税凭证事件。`).then(() => confirmInvoice(row.invoiceId)).then(() => {
    proxy.$modal.msgSuccess("确认成功"); getList()
  }).catch(() => {})
}

function openRedDialog(row) {
  Object.assign(redForm, {
    originInvoiceId: row.invoiceId,
    originInvoiceNo: row.invoiceNo,
    originAmount: Number(row.invoiceAmount || 0),
    amount: Number(row.invoiceAmount || 0),
    remark: "蓝票红字冲销"
  })
  redOpen.value = true
}

function submitRedInvoice() {
  if (!redForm.amount || redForm.amount <= 0) {
    proxy.$modal.msgError("红冲金额必须大于0"); return
  }
  issueRedInvoice(redForm.originInvoiceId, { redAmount: redForm.amount, remark: redForm.remark }).then(() => {
    proxy.$modal.msgSuccess("红字冲销发票开具成功")
    redOpen.value = false
    getList()
  })
}

function handlePrint(row) {
  printFinancialDocument({
    title: `${invoiceTitle.value}登记单（${row.invoiceKind === "red" ? "红字冲销" : "蓝字发票"}）`,
    docNo: row.invoiceNo,
    amount: row.invoiceAmount,
    remark: row.remark,
    createBy: row.createBy,
    auditBy: row.confirmBy,
    metaList: [
      { label: "发票单号", value: row.invoiceNo },
      { label: "开票日期", value: proxy.parseTime(row.invoiceDate, "{y}-{m}-{d}") },
      { label: "状态", value: row.bizStatus === "confirmed" ? "已确认" : "草稿" },
      { label: isSaleInvoice.value ? "客户" : "供应商", value: isSaleInvoice.value ? getCustomerName(row.customerId) : getSupplierName(row.supplierId) },
      { label: "来源单据", value: `${getSourceBillTypeLabel(row.sourceBillType)} / ${row.sourceBillNo || "-"}` },
      { label: "税率 / 税额 / 不含税额", value: `${Number(row.taxRate || 0)}% / ￥${Number(row.taxAmount || 0).toFixed(2)} / ￥${Number(row.untaxedAmount || 0).toFixed(2)}` }
    ]
  })
}

function submitForm() {
  proxy.$refs["invoiceRef"].validate(valid => {
    if (!valid) return
    form.value.invoiceType = invoiceType.value
    if (!isSaleInvoice.value) form.value.customerId = undefined
    else form.value.supplierId = undefined
    const fn = form.value.invoiceId ? updateInvoice(form.value) : addInvoice(form.value)
    fn.then(() => { proxy.$modal.msgSuccess(form.value.invoiceId ? "修改成功" : "新增成功"); open.value = false; getList() })
  })
}

function handleDelete(row) {
  const ids = row.invoiceId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中发票？").then(() => delInvoice(ids)).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {})
}

watch(invoiceType, () => { reset(); getList() })
getList()
</script>
