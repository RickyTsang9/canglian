<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单据类型" prop="billType">
        <el-select v-model="queryParams.billType" placeholder="全部单据类型" clearable filterable style="width: 170px">
          <el-option v-for="(label, code) in billTypeMap" :key="code" :label="label" :value="code" />
        </el-select>
      </el-form-item>
      <el-form-item label="事件类型" prop="eventType">
        <el-select v-model="queryParams.eventType" placeholder="全部事件类型" clearable filterable style="width: 190px">
          <el-option v-for="(label, code) in eventTypeMap" :key="code" :label="label" :value="code" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
          <el-option v-for="dict in voucherStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          icon="RefreshRight"
          :loading="regenerating"
          @click="handleRegenerate"
          v-hasPermi="['business:voucherEvent:generate']"
        >一键补生成凭证</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-dropdown @command="handleExportFormat" v-hasPermi="['business:voucherEvent:export']">
          <el-button type="warning" plain icon="Download">
            导出复式凭证分录<el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="standard">标准复式记账 Excel</el-dropdown-item>
              <el-dropdown-item command="kingdee">金蝶 KIS 标准导入格式</el-dropdown-item>
              <el-dropdown-item command="yonyou">用友 T+ 标准导入格式</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="voucherEventList" row-key="voucherEventId" @expand-change="handleExpandChange">
      <el-table-column type="expand">
        <template #default="scope">
          <div style="padding: 8px 16px; max-width: 100%; box-sizing: border-box">
            <div style="margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap">
              <strong>复式借贷分录明细（凭证号：{{ scope.row.voucherNo || '未生成' }}）</strong>
              <el-tag v-if="(entryMap[scope.row.voucherEventId] || []).length" type="success" size="small">
                借方合计：￥{{ sumEntries(scope.row.voucherEventId, 'debitAmount') }} &nbsp;|&nbsp;
                贷方合计：￥{{ sumEntries(scope.row.voucherEventId, 'creditAmount') }}（借贷平衡）
              </el-tag>
            </div>
            <el-table :data="entryMap[scope.row.voucherEventId] || []" border size="small">
              <el-table-column label="凭证号" prop="voucherNo" width="132" align="center" />
              <el-table-column label="行号" prop="entrySeq" width="52" align="center" />
              <el-table-column label="借贷" width="58" align="center">
                <template #default="s">
                  <el-tag :type="s.row.direction === 'debit' ? 'primary' : 'warning'" size="small">
                    {{ s.row.direction === 'debit' ? '借' : '贷' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="科目编码" prop="subjectCode" width="84" align="center" />
              <el-table-column label="科目名称" prop="subjectName" min-width="105" show-overflow-tooltip />
              <el-table-column label="辅助核算" prop="auxiliaryName" min-width="100" show-overflow-tooltip>
                <template #default="s">{{ s.row.auxiliaryName || '-' }}</template>
              </el-table-column>
              <el-table-column label="借方金额" prop="debitAmount" width="96" align="right" header-align="right">
                <template #default="s">{{ Number(s.row.debitAmount || 0).toFixed(2) }}</template>
              </el-table-column>
              <el-table-column label="贷方金额" prop="creditAmount" width="96" align="right" header-align="right">
                <template #default="s">{{ Number(s.row.creditAmount || 0).toFixed(2) }}</template>
              </el-table-column>
              <el-table-column label="摘要" prop="summary" min-width="140" show-overflow-tooltip>
                <template #default="s">{{ s.row.summary || '-' }}</template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单据类型" align="center" width="96">
        <template #default="scope">
          <el-tag effect="plain" size="small">{{ billTypeMap[scope.row.billType] || scope.row.billType || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="单据号" align="center" prop="billNo" min-width="148" show-overflow-tooltip />
      <el-table-column label="事件类型" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">{{ eventTypeMap[scope.row.eventType] || scope.row.eventType || '-' }}</template>
      </el-table-column>
      <el-table-column label="事件金额" align="right" header-align="right" prop="eventAmount" width="102">
        <template #default="scope">￥{{ Number(scope.row.eventAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="凭证号" align="center" prop="voucherNo" width="132" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.voucherNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="冲销凭证号" align="center" prop="reverseVoucherNo" width="112" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.reverseVoucherNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="事件日期" align="center" width="102">
        <template #default="scope">{{ parseTime(scope.row.eventDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="78">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="215" fixed="right">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="Printer" @click="handlePrint(scope.row)">打印</el-button>
            <el-button link type="primary" icon="DocumentAdd" :disabled="scope.row.status !== '0'" @click="handleGenerate(scope.row)" v-hasPermi="['business:voucherEvent:generate']">生成凭证</el-button>
            <el-dropdown trigger="click" @command="cmd => handleRowCommand(cmd, scope.row)">
              <el-button link type="primary">更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="writeback" :disabled="scope.row.status !== '1'" v-hasPermi="['business:voucherEvent:writeback']">回写凭证</el-dropdown-item>
                  <el-dropdown-item command="reverse" :disabled="!['1', '2'].includes(scope.row.status)" v-hasPermi="['business:voucherEvent:reverse']">红字冲销</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="VoucherEvent">
import {
  listVoucherEvent,
  listVoucherEntries,
  generateVoucher,
  writebackVoucher,
  reverseVoucher,
  regenerateVoucherEvents
} from "@/api/business/voucherEvent"
import { printFinancialDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()

const voucherStatusOptions = [
  { label: "待生成", value: "0", tagType: "info" },
  { label: "已生成", value: "1", tagType: "primary" },
  { label: "已回写", value: "2", tagType: "success" },
  { label: "已冲销", value: "3", tagType: "danger" }
]

const billTypeMap = {
  inbound: "采购入库",
  outbound: "销售出库",
  transfer: "库存调拨",
  inventory_check: "库存盘点",
  purchase_return: "采购退货",
  sale_return: "销售退货",
  receipt: "销售收款",
  payment: "采购付款",
  expense: "期间费用",
  invoice: "发票登记",
  write_off: "往来核销",
  bad_debt: "坏账处理",
  fund_transfer: "资金互转",
  cost_adjust: "成本调价"
}

const eventTypeMap = {
  inbound_complete: "采购入库审核",
  inbound_audit: "采购入库审核",
  outbound_complete: "销售签收确认收入",
  outbound_cogs: "销售签收结转成本",
  outbound_cogs_return: "出库退货成本冲回",
  outbound_return: "出库整单退货冲回",
  sale_return_complete: "销售退货入库",
  sale_return_audit: "销售退货入库",
  sale_return_cogs: "销售退货成本冲回",
  purchase_return_complete: "采购退货出库",
  purchase_return_audit: "采购退货出库",
  inventory_check_gain: "库存盘盈入账",
  inventory_check_loss: "库存盘亏入账",
  receipt_confirm: "收款单确认",
  refund_receipt: "销售退款冲回",
  payment_confirm: "付款单确认",
  expense_confirm: "费用单确认",
  write_off_confirm: "往来核销确认",
  bad_debt_confirm: "坏账计提确认",
  bad_debt_recover: "坏账收回入账",
  fund_transfer: "资金内部互转",
  fund_transfer_fee: "内部互转手续费",
  invoice_confirm: "发票确认入账",
  sale_invoice_confirm: "销项增值税确认",
  purchase_invoice_confirm: "进项增值税确认",
  manual_receivable: "手工应收确认",
  manual_payable: "手工应付确认",
  cost_adjust: "受控成本调价",
  cost_adjust_confirm: "受控成本调价"
}

function getStatusLabel(val) { const i = voucherStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.label : (val || "-") }
function getStatusTagType(val) { const i = voucherStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.tagType : "info" }

const voucherEventList = ref([])
const entryMap = reactive({})
const loading = ref(true)
const regenerating = ref(false)
const showSearch = ref(true)
const total = ref(0)

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, billType: undefined, eventType: undefined, status: undefined }
})
const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listVoucherEvent(queryParams.value).then(res => {
    voucherEventList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function handleExpandChange(row, expandedRows) {
  const expanded = expandedRows.some(r => r.voucherEventId === row.voucherEventId)
  if (expanded) {
    listVoucherEntries(row.voucherEventId).then(res => {
      entryMap[row.voucherEventId] = res.data || []
    })
  }
}

function sumEntries(eventId, field) {
  const rows = entryMap[eventId] || []
  return rows.reduce((s, r) => s + Number(r[field] || 0), 0).toFixed(2)
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleRegenerate() {
  proxy.$modal.confirm("将扫描所有已审核/已签收/已确认但缺失会计凭证事件的业务单据并自动补全，是否继续？").then(() => {
    regenerating.value = true
    return regenerateVoucherEvents()
  }).then(res => {
    const d = res.data || {}
    const count = d.createdCount ?? d.regeneratedCount ?? d.count ?? 0
    proxy.$modal.msgSuccess(`补生成完成，本次新增凭证事件 ${count} 条`)
    getList()
  }).catch(() => {}).finally(() => {
    regenerating.value = false
  })
}

function handleGenerate(row) {
  generateVoucher(row.voucherEventId).then(() => {
    proxy.$modal.msgSuccess("复式凭证生成成功")
    listVoucherEntries(row.voucherEventId).then(res => { entryMap[row.voucherEventId] = res.data || [] })
    getList()
  })
}

function handleWriteback(row) {
  writebackVoucher(row.voucherEventId).then(() => {
    proxy.$modal.msgSuccess("凭证回写成功"); getList()
  })
}

function handleRowCommand(cmd, row) {
  if (cmd === "writeback") handleWriteback(row)
  else if (cmd === "reverse") handleReverse(row)
}

function handleReverse(row) {
  proxy.$modal.confirm(`确认红字冲销凭证"${row.voucherNo}"？将自动生成红字负向借贷分录。`).then(() => reverseVoucher(row.voucherEventId)).then(() => {
    proxy.$modal.msgSuccess("凭证冲销成功")
    listVoucherEntries(row.voucherEventId).then(res => { entryMap[row.voucherEventId] = res.data || [] })
    getList()
  }).catch(() => {})
}

async function handlePrint(row) {
  let entries = entryMap[row.voucherEventId]
  if (!entries) {
    try {
      const res = await listVoucherEntries(row.voucherEventId)
      entries = res.data || []
      entryMap[row.voucherEventId] = entries
    } catch (e) {
      entries = []
    }
  }
  printFinancialDocument({
    title: "记账凭证",
    docNo: row.voucherNo || row.billNo,
    amount: row.eventAmount,
    remark: row.remark,
    createBy: row.createBy,
    auditBy: row.updateBy,
    metaList: [
      { label: "凭证号", value: row.voucherNo || "待生成" },
      { label: "业务日期", value: proxy.parseTime(row.eventDate, "{y}-{m}-{d}") },
      { label: "状态", value: getStatusLabel(row.status) },
      { label: "单据类型", value: billTypeMap[row.billType] || row.billType },
      { label: "来源单号", value: row.billNo },
      { label: "事件类型", value: eventTypeMap[row.eventType] || row.eventType }
    ],
    columns: [
      { label: "借贷", prop: "direction", align: "center", formatter: r => r.direction === "debit" ? "借" : "贷" },
      { label: "科目编码", prop: "subjectCode", align: "center" },
      { label: "科目名称", prop: "subjectName" },
      { label: "辅助核算", prop: "auxiliaryName" },
      { label: "借方金额", prop: "debitAmount", align: "right", formatter: r => Number(r.debitAmount || 0).toFixed(2) },
      { label: "贷方金额", prop: "creditAmount", align: "right", formatter: r => Number(r.creditAmount || 0).toFixed(2) },
      { label: "摘要", prop: "summary" }
    ],
    rows: entries
  })
}

function handleExportFormat(format) {
  proxy.download("business/voucherEvent/export", { ...queryParams.value, format }, `voucher_entries_${format}_${Date.now()}.xlsx`)
}

getList()
</script>
