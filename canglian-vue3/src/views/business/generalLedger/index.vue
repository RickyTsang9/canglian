<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 1. 会计科目 -->
      <el-tab-pane label="会计科目" name="subject">
        <el-form :model="subjectQuery" ref="subjectQueryRef" :inline="true">
          <el-form-item label="状态" prop="status">
            <el-select v-model="subjectQuery.status" placeholder="全部状态" clearable style="width: 150px">
              <el-option label="正常" value="0" />
              <el-option label="停用" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getSubjectList">查询</el-button>
            <el-button icon="Refresh" @click="resetSubjectQuery">重置</el-button>
            <el-button type="success" plain icon="Plus" @click="openSubjectDialog()" v-hasPermi="['business:generalLedger:edit']">新增科目</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="subjectLoading" :data="subjectList" border>
          <el-table-column label="科目编码" align="center" prop="subjectCode" width="120" />
          <el-table-column label="科目名称" align="center" prop="subjectName" min-width="150" />
          <el-table-column label="科目类别" align="center" width="110">
            <template #default="scope">
              <el-tag effect="plain" size="small">{{ subjectTypeMap[scope.row.subjectType] || scope.row.subjectType || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="余额方向" align="center" width="95">
            <template #default="scope">
              <el-tag :type="scope.row.balanceDirection === 'debit' ? 'primary' : 'warning'" size="small">
                {{ scope.row.balanceDirection === 'debit' ? '借方' : '贷方' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="85">
            <template #default="scope">
              <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="small">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
          <el-table-column label="操作" align="center" width="150">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="openSubjectDialog(scope.row)" v-hasPermi="['business:generalLedger:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click="handleDeleteSubject(scope.row)" v-hasPermi="['business:generalLedger:edit']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 2. 辅助核算 -->
      <el-tab-pane label="辅助核算" name="auxiliary">
        <el-form :model="auxiliaryQuery" ref="auxiliaryQueryRef" :inline="true">
          <el-form-item label="辅助类型" prop="auxiliaryType">
            <el-select v-model="auxiliaryQuery.auxiliaryType" placeholder="全部类型" clearable style="width: 170px">
              <el-option label="客户" value="customer" />
              <el-option label="供应商" value="supplier" />
              <el-option label="仓库" value="warehouse" />
              <el-option label="商品" value="product" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getAuxiliaryList">查询</el-button>
            <el-button icon="Refresh" @click="resetAuxiliaryQuery">重置</el-button>
            <el-button type="warning" plain icon="Refresh" @click="handleSyncAuxiliary" v-hasPermi="['business:generalLedger:edit']">一键同步客户/供应商/仓库</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="auxiliaryLoading" :data="auxiliaryList" border>
          <el-table-column label="辅助类型" align="center" width="110">
            <template #default="scope">
              <el-tag effect="plain" size="small">{{ auxiliaryTypeMap[scope.row.auxiliaryType] || scope.row.auxiliaryType || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="项目编码" align="center" prop="itemCode" width="140" />
          <el-table-column label="项目名称" align="center" prop="itemName" min-width="170" />
          <el-table-column label="关联主数据ID" align="center" prop="relationId" width="120" />
          <el-table-column label="状态" align="center" width="90">
            <template #default="scope">
              <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="small">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 3. 会计期间 -->
      <el-tab-pane label="会计期间与结账" name="period">
        <el-form :model="periodQuery" ref="periodQueryRef" :inline="true">
          <el-form-item label="结账状态" prop="closeStatus">
            <el-select v-model="periodQuery.closeStatus" placeholder="全部状态" clearable style="width: 150px">
              <el-option label="未结账" value="open" />
              <el-option label="已结账" value="closed" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getPeriodList">查询</el-button>
            <el-button icon="Refresh" @click="resetPeriodQuery">重置</el-button>
            <el-button type="success" plain icon="Calendar" @click="handleInitYear" v-hasPermi="['business:generalLedger:edit']">批量初始化年度期间</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="periodLoading" :data="periodList" border>
          <el-table-column label="期间编码" align="center" prop="periodCode" width="120" />
          <el-table-column label="开始日期" align="center" width="125">
            <template #default="scope">{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</template>
          </el-table-column>
          <el-table-column label="结束日期" align="center" width="125">
            <template #default="scope">{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</template>
          </el-table-column>
          <el-table-column label="结账状态" align="center" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.closeStatus === 'closed' ? 'danger' : 'success'">
                {{ scope.row.closeStatus === 'closed' ? '已结账' : '未结账' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="结账人" align="center" prop="closeBy" width="100" />
          <el-table-column label="操作" align="center" width="260">
            <template #default="scope">
              <el-button link type="info" icon="Checked" @click="openReadinessDrawer(scope.row, false)">结账前检查</el-button>
              <el-button v-if="scope.row.closeStatus !== 'closed'" link type="primary" @click="openReadinessDrawer(scope.row, true)" v-hasPermi="['business:generalLedger:close']">结账</el-button>
              <el-button v-else link type="warning" @click="handleReopen(scope.row)" v-hasPermi="['business:generalLedger:reopen']">反结账</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 4. 凭证科目映射模板 -->
      <el-tab-pane label="凭证借贷模板" name="template">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="openTemplateDialog()" v-hasPermi="['business:generalLedger:edit']">新增映射分录</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button icon="Refresh" @click="getTemplateList">刷新</el-button>
          </el-col>
        </el-row>
        <el-table v-loading="templateLoading" :data="templateList" border size="small">
          <el-table-column label="事件类型" min-width="170">
            <template #default="scope">{{ eventTypeMap[scope.row.eventType] || scope.row.eventType || '-' }}</template>
          </el-table-column>
          <el-table-column label="行号" prop="entrySeq" width="65" align="center" />
          <el-table-column label="借贷" width="75" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.direction === 'debit' ? 'primary' : 'warning'" size="small">
                {{ scope.row.direction === 'debit' ? '借' : '贷' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="科目编码" prop="subjectCode" width="95" align="center" />
          <el-table-column label="科目名称" prop="subjectName" min-width="125" />
          <el-table-column label="辅助核算" width="95" align="center">
            <template #default="scope">{{ auxiliaryTypeMap[scope.row.auxiliaryType] || scope.row.auxiliaryType || '无' }}</template>
          </el-table-column>
          <el-table-column label="摘要模板" prop="summaryTemplate" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="135" align="center">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="openTemplateDialog(scope.row)" v-hasPermi="['business:generalLedger:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click="handleDeleteTemplate(scope.row)" v-hasPermi="['business:generalLedger:edit']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 科目新增/修改弹窗 -->
    <el-dialog :title="subjectForm.subjectId ? '修改会计科目' : '新增会计科目'" v-model="subjectDialogOpen" width="520px" append-to-body>
      <el-form :model="subjectForm" label-width="90px">
        <el-form-item label="科目编码" required><el-input v-model="subjectForm.subjectCode" placeholder="如：1001" /></el-form-item>
        <el-form-item label="科目名称" required><el-input v-model="subjectForm.subjectName" placeholder="如：库存现金" /></el-form-item>
        <el-form-item label="科目类别">
          <el-select v-model="subjectForm.subjectType" style="width: 100%">
            <el-option label="资产" value="asset" />
            <el-option label="负债" value="liability" />
            <el-option label="权益" value="equity" />
            <el-option label="收入" value="income" />
            <el-option label="费用" value="expense" />
            <el-option label="成本" value="cost" />
            <el-option label="损益" value="profit_loss" />
          </el-select>
        </el-form-item>
        <el-form-item label="余额方向">
          <el-radio-group v-model="subjectForm.balanceDirection">
            <el-radio value="debit">借方</el-radio>
            <el-radio value="credit">贷方</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="subjectForm.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="subjectForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitSubject">确 定</el-button>
        <el-button @click="subjectDialogOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 凭证模板弹窗 -->
    <el-dialog :title="templateForm.templateId ? '修改凭证映射模板' : '新增凭证映射模板'" v-model="templateDialogOpen" width="540px" append-to-body>
      <el-form :model="templateForm" label-width="95px">
        <el-form-item label="事件类型" required>
          <el-select v-model="templateForm.eventType" placeholder="请选择事件类型" filterable allow-create style="width: 100%">
            <el-option v-for="(label, code) in eventTypeMap" :key="code" :label="`${label} (${code})`" :value="code" />
          </el-select>
        </el-form-item>
        <el-form-item label="分录行号" required><el-input-number v-model="templateForm.entrySeq" :min="1" controls-position="right" /></el-form-item>
        <el-form-item label="借贷方向" required>
          <el-radio-group v-model="templateForm.direction">
            <el-radio value="debit">借方</el-radio>
            <el-radio value="credit">贷方</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="会计科目" required>
          <el-select v-model="templateForm.subjectCode" placeholder="选择科目" filterable allow-create style="width: 100%" @change="syncTplSubjectName">
            <el-option v-for="s in subjectList" :key="s.subjectCode" :label="`${s.subjectCode} - ${s.subjectName}`" :value="s.subjectCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目名称" required><el-input v-model="templateForm.subjectName" /></el-form-item>
        <el-form-item label="辅助核算">
          <el-select v-model="templateForm.auxiliaryType" style="width: 100%">
            <el-option label="无" value="none" />
            <el-option label="客户" value="customer" />
            <el-option label="供应商" value="supplier" />
            <el-option label="仓库" value="warehouse" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要模板"><el-input v-model="templateForm.summaryTemplate" placeholder="支持 {billNo} 占位符" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitTemplate">确 定</el-button>
        <el-button @click="templateDialogOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 结账前置检查与执行抽屉（15项硬性拦截门禁） -->
    <el-drawer v-model="readinessOpen" :title="`结账前置检查（共 ${(readinessData.items || []).length} 项） - ${readinessData.periodCode || ''}`" size="600px">
      <el-alert
        :title="readinessData.ready ? '所有结账前置检查项已全部通过，可以执行期末结账！' : `存在 ${readinessData.totalBlockingCount} 条未处理/未回写单据，系统已硬性阻断结账，请先完成处理后再试`"
        :type="readinessData.ready ? 'success' : 'error'"
        :closable="false"
        show-icon
        style="margin-bottom: 14px"
      />
      <el-table :data="readinessData.items || []" border size="small">
        <el-table-column type="index" label="#" width="46" align="center" />
        <el-table-column label="检查项" prop="label" min-width="240" />
        <el-table-column label="未结数量" prop="count" width="90" align="center">
          <template #default="scope">
            <span :style="scope.row.count > 0 ? 'color:#f56c6c;font-weight:700' : 'color:#67c23a'">{{ scope.row.count }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="85" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.passed ? 'success' : 'danger'" size="small">{{ scope.row.passed ? '通过' : '阻断' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="readinessCloseMode" style="margin-top: 18px; display: flex; justify-content: flex-end; gap: 10px">
        <el-button @click="readinessOpen = false">取 消</el-button>
        <el-button
          type="primary"
          :disabled="!readinessData.ready"
          @click="confirmCloseFromDrawer"
          v-hasPermi="['business:generalLedger:close']"
        >确认执行结账</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="GeneralLedger">
import {
  listAccountSubject, addAccountSubject, updateAccountSubject, delAccountSubject,
  listAuxiliaryItem, syncAuxiliaryItems,
  listFiscalPeriod, initFiscalPeriodYear, checkPeriodReadiness, closeFiscalPeriod, reopenFiscalPeriod,
  listVoucherTemplate, addVoucherTemplate, updateVoucherTemplate, delVoucherTemplate
} from "@/api/business/generalLedger"

const { proxy } = getCurrentInstance()

const subjectTypeMap = {
  asset: "资产",
  liability: "负债",
  equity: "权益",
  income: "收入",
  expense: "费用",
  cost: "成本",
  profit: "损益",
  profit_loss: "损益"
}

const auxiliaryTypeMap = {
  none: "无",
  customer: "客户",
  supplier: "供应商",
  warehouse: "仓库",
  product: "商品"
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

const activeTab = ref("subject")
const subjectLoading = ref(false)
const auxiliaryLoading = ref(false)
const periodLoading = ref(false)
const templateLoading = ref(false)
const subjectList = ref([])
const auxiliaryList = ref([])
const periodList = ref([])
const templateList = ref([])

const subjectQuery = reactive({ status: undefined })
const auxiliaryQuery = reactive({ auxiliaryType: undefined })
const periodQuery = reactive({ closeStatus: undefined })

const subjectDialogOpen = ref(false)
const subjectForm = reactive({ subjectId: undefined, subjectCode: "", subjectName: "", subjectType: "asset", balanceDirection: "debit", status: "0", remark: "" })

const templateDialogOpen = ref(false)
const templateForm = reactive({ templateId: undefined, eventType: "", entrySeq: 1, direction: "debit", subjectCode: "", subjectName: "", auxiliaryType: "none", summaryTemplate: "", status: "0" })

const readinessOpen = ref(false)
const readinessCloseMode = ref(false)
const currentPeriodRow = ref(null)
const readinessData = reactive({ periodCode: "", ready: false, totalBlockingCount: 0, items: [] })

function getSubjectList() {
  subjectLoading.value = true
  listAccountSubject(subjectQuery).then(res => { subjectList.value = res.data || [] }).finally(() => { subjectLoading.value = false })
}
function resetSubjectQuery() { proxy.resetForm("subjectQueryRef"); getSubjectList() }

function openSubjectDialog(row) {
  if (row) Object.assign(subjectForm, { ...row })
  else Object.assign(subjectForm, { subjectId: undefined, subjectCode: "", subjectName: "", subjectType: "asset", balanceDirection: "debit", status: "0", remark: "" })
  subjectDialogOpen.value = true
}

function submitSubject() {
  const fn = subjectForm.subjectId ? updateAccountSubject(subjectForm) : addAccountSubject(subjectForm)
  fn.then(() => { proxy.$modal.msgSuccess("保存成功"); subjectDialogOpen.value = false; getSubjectList() })
}

function handleDeleteSubject(row) {
  proxy.$modal.confirm(`确认删除科目"${row.subjectCode} - ${row.subjectName}"？`).then(() => delAccountSubject(row.subjectId)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getSubjectList()
  }).catch(() => {})
}

function getAuxiliaryList() {
  auxiliaryLoading.value = true
  listAuxiliaryItem(auxiliaryQuery).then(res => { auxiliaryList.value = res.data || [] }).finally(() => { auxiliaryLoading.value = false })
}
function resetAuxiliaryQuery() { proxy.resetForm("auxiliaryQueryRef"); getAuxiliaryList() }

function handleSyncAuxiliary() {
  syncAuxiliaryItems().then(res => {
    const d = res.data || {}
    proxy.$modal.msgSuccess(`同步完成！客户 ${d.syncedCustomers || 0}、供应商 ${d.syncedSuppliers || 0}、仓库 ${d.syncedWarehouses || 0}`)
    getAuxiliaryList()
  })
}

function getPeriodList() {
  periodLoading.value = true
  listFiscalPeriod(periodQuery).then(res => { periodList.value = res.data || [] }).finally(() => { periodLoading.value = false })
}
function resetPeriodQuery() { proxy.resetForm("periodQueryRef"); getPeriodList() }

function handleInitYear() {
  proxy.$modal.prompt("请输入要初始化12个会计期间的年份（如 2027）", "批量初始化年度期间", {
    inputValue: String(new Date().getFullYear() + 1)
  }).then(({ value }) => initFiscalPeriodYear(Number(value))).then(res => {
    const d = res.data || {}
    proxy.$modal.msgSuccess(`${d.year}年度期间初始化完成：新建 ${d.createdCount || 0} 期，跳过已存在 ${d.skippedCount || 0} 期`)
    getPeriodList()
  }).catch(() => {})
}

function openReadinessDrawer(row, closeMode = false) {
  currentPeriodRow.value = row
  readinessCloseMode.value = closeMode
  checkPeriodReadiness(row.periodId).then(res => {
    Object.assign(readinessData, { periodCode: row.periodCode, ready: false, totalBlockingCount: 0, items: [] }, res.data || {})
    readinessOpen.value = true
  })
}

function confirmCloseFromDrawer() {
  if (!currentPeriodRow.value) return
  const row = currentPeriodRow.value
  proxy.$modal.confirm(`确认对会计期间"${row.periodCode}"执行结账？结账后该期间所有业务单据将被锁定。`).then(() => closeFiscalPeriod(row.periodId)).then(() => {
    proxy.$modal.msgSuccess("结账成功")
    readinessOpen.value = false
    getPeriodList()
  }).catch(() => {})
}

function handleReopen(row) {
  proxy.$modal.confirm(`确认反结账会计期间"${row.periodCode}"？`).then(() => reopenFiscalPeriod(row.periodId)).then(() => {
    proxy.$modal.msgSuccess("反结账成功"); getPeriodList()
  }).catch(() => {})
}

function getTemplateList() {
  templateLoading.value = true
  listVoucherTemplate().then(res => { templateList.value = res.data || [] }).finally(() => { templateLoading.value = false })
}

function openTemplateDialog(row) {
  if (row) Object.assign(templateForm, { ...row })
  else Object.assign(templateForm, { templateId: undefined, eventType: "", entrySeq: 1, direction: "debit", subjectCode: "", subjectName: "", auxiliaryType: "none", summaryTemplate: "", status: "0" })
  templateDialogOpen.value = true
}

function syncTplSubjectName(code) {
  const s = subjectList.value.find(x => x.subjectCode === code)
  if (s) templateForm.subjectName = s.subjectName
}

function submitTemplate() {
  const fn = templateForm.templateId ? updateVoucherTemplate(templateForm) : addVoucherTemplate(templateForm)
  fn.then(() => { proxy.$modal.msgSuccess("保存成功"); templateDialogOpen.value = false; getTemplateList() })
}

function handleDeleteTemplate(row) {
  proxy.$modal.confirm(`确认删除模板分录(${eventTypeMap[row.eventType] || row.eventType} #${row.entrySeq})？`).then(() => delVoucherTemplate(row.templateId)).then(() => {
    proxy.$modal.msgSuccess("删除成功"); getTemplateList()
  }).catch(() => {})
}

getSubjectList(); getAuxiliaryList(); getPeriodList(); getTemplateList()
</script>
