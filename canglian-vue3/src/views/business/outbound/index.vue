<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="出库单号" prop="outboundNo">
        <el-input v-model="queryParams.outboundNo" placeholder="请输入出库单号" clearable style="width: 185px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 190px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="出库状态" clearable style="width: 130px">
          <el-option v-for="dict in docStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="配送状态" prop="deliveryStatus">
        <el-select v-model="queryParams.deliveryStatus" placeholder="配送状态" clearable style="width: 130px">
          <el-option v-for="item in deliveryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:outbound:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:outbound:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:outbound:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:outbound:export', 'business:outbound:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="outboundList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="48" align="center" />
      <el-table-column label="出库单号" align="center" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.outboundNo }}</span>
          <el-tag v-if="scope.row.isReversal === '1'" type="danger" size="small" style="margin-left: 4px">红冲单</el-tag>
          <el-tag v-if="scope.row.reversedFlag === '1'" type="warning" size="small" style="margin-left: 4px">已红冲</el-tag>
          <el-tag v-if="scope.row.verifiedFlag === '1'" type="success" size="small" style="margin-left: 4px">已复核</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="出库类型" align="center" width="106">
        <template #default="scope">
          <el-tag size="small" type="info">{{ outboundTypeLabel(scope.row.outboundType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="客户" align="center" min-width="136" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="100" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="总数量" align="right" prop="totalQty" width="78" />
      <el-table-column label="总金额" align="right" prop="totalAmount" width="100">
        <template #default="scope">{{ Number(scope.row.totalAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="配送状态" align="center" prop="deliveryStatus" width="88">
        <template #default="scope">
          <dict-tag :options="deliveryStatusOptions" :value="scope.row.deliveryStatus" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="86">
        <template #default="scope">
          <el-tag size="small" :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="245" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="info" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button v-if="canEditRow(scope.row)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:outbound:edit']">修改</el-button>
            <el-button v-if="canAuditRow(scope.row)" link type="success" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:outbound:audit']">审核</el-button>
            <el-button v-if="canShipRow(scope.row)" link type="primary" icon="Position" @click="handleShip(scope.row)" v-hasPermi="['business:outbound:ship']">发货</el-button>
            <el-button v-if="canSignRow(scope.row)" link type="success" icon="Check" @click="handleSign(scope.row)" v-hasPermi="['business:outbound:sign']">签收</el-button>
            <el-dropdown trigger="click" @command="cmd => handleRowCommand(cmd, scope.row)">
              <el-button link type="primary">更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="canUnauditRow(scope.row)" command="unaudit">反审核</el-dropdown-item>
                  <el-dropdown-item v-if="canReverseRow(scope.row)" command="reverse">一键红冲</el-dropdown-item>
                  <el-dropdown-item v-if="canReturnRow(scope.row)" command="return">退货处理</el-dropdown-item>
                  <el-dropdown-item command="trace">单据链路</el-dropdown-item>
                  <el-dropdown-item command="print">打印单据</el-dropdown-item>
                  <el-dropdown-item v-if="canDeleteRow(scope.row)" command="delete" divided>删除单据</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改出库单 -->
    <el-dialog :title="title" v-model="open" width="min(1260px, 94vw)" append-to-body>
      <el-form ref="outboundRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="出库单号" prop="outboundNo">
              <el-input v-model="form.outboundNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出库类型" prop="outboundType">
              <el-select v-model="form.outboundType" placeholder="请选择出库类型" style="width: 100%">
                <el-option label="销售出库" value="sale" />
                <el-option label="销货订单出库" value="sale_order" />
                <el-option label="其他出库" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择业务日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" clearable filterable style="width: 100%" @change="handleCustomerChanged">
                <el-option v-for="c in activeCustomerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出库仓库" prop="warehouseId">
              <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%" @change="handleWarehouseChange">
                <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合计">
              <span>数量：<strong>{{ form.totalQty }}</strong> &nbsp;|&nbsp; 金额：<strong>￥{{ Number(form.totalAmount || 0).toFixed(2) }}</strong></span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="承运商" prop="carrier">
              <el-input v-model="form.carrier" placeholder="选填" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="运单号" prop="waybillNo">
              <el-input v-model="form.waybillNo" placeholder="选填" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="运费成本" prop="freightCost">
              <el-input-number v-model="form.freightCost" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入内容" />
        </el-form-item>

        <el-divider content-position="left">出库明细（FEFO 自动同步库位；启用序列号商品必须录入足额在库 SN 码）</el-divider>
        <div class="mb8">
          <el-button type="primary" plain size="small" icon="Plus" @click="handleAddItem">新增明细</el-button>
        </div>
        <el-table :data="form.itemList" border size="small">
          <el-table-column label="商品" min-width="170">
            <template #default="scope">
              <el-select v-model="scope.row.productId" placeholder="选择商品" filterable style="width: 100%" @change="handleProductChange(scope.row)">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}${p.enableSerial === '1' ? ' [SN]' : ''}`" :value="p.productId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="库位" width="125">
            <template #default="scope">
              <el-select v-model="scope.row.locationId" placeholder="默认库位" filterable style="width: 100%">
                <el-option label="默认库位" :value="0" />
                <el-option v-for="l in warehouseLocations" :key="l.locationId" :label="`${l.locationCode} - ${l.locationName}`" :value="l.locationId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单位" min-width="125">
            <template #default="scope">
              <el-select v-model="scope.row.unitName" placeholder="选择单位" filterable style="width: 100%" @change="handleUnitChange(scope.row)">
                <el-option v-for="u in getUnitOptions(scope.row.productId)" :key="u.unitName" :label="`${u.unitName}(×${u.convertRatio})`" :value="u.unitName" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="批次号(FEFO建议)" width="195">
            <template #default="scope">
              <el-select
                v-model="scope.row.batchNo"
                placeholder="选择批次(FEFO)"
                filterable
                allow-create
                clearable
                style="width: 100%"
                @focus="loadFefoForRow(scope.row)"
                @change="handleBatchSelect(scope.row)"
              >
                <el-option
                  v-for="b in (scope.row._fefoBatches || [])"
                  :key="b.stockId"
                  :label="`${b.batchNo || '无批次'} | ${getLocationName(b.locationId)} | 可用:${b.availableQuantity}${b.expiryDate ? ' 效期:' + parseTime(b.expiryDate, '{y}-{m}-{d}') : ''}`"
                  :value="b.batchNo || ''"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="80">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="0.01" :precision="2" :controls="false" style="width: 100%" @change="recalcRow(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="85">
            <template #default="scope">
              <el-input-number v-model="scope.row.price" :min="0" :precision="2" :controls="false" style="width: 100%" @change="recalcRow(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="金额" width="80" align="right">
            <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="序列号(SN)" min-width="180">
            <template #default="scope">
              <template v-if="isProductSerialEnabled(scope.row.productId)">
                <el-input
                  v-model="scope.row.snCodes"
                  type="textarea"
                  :rows="2"
                  :placeholder="`必填：需录 ${requiredSnCount(scope.row)} 个在库SN（逗号/换行分隔）`"
                />
                <div style="font-size: 11px; color: #e6a23c; margin-top: 2px">
                  已录 {{ countEnteredSns(scope.row.snCodes) }} / 需 {{ requiredSnCount(scope.row) }} 个
                </div>
              </template>
              <span v-else style="color: #909399; font-size: 12px">未启用SN</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="55" align="center">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="handleRemoveItem(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog title="出库发货" v-model="shipOpen" width="480px" append-to-body>
      <el-form ref="shipRef" :model="shipForm" label-width="90px">
        <el-form-item label="承运商"><el-input v-model="shipForm.carrier" placeholder="请输入承运商" /></el-form-item>
        <el-form-item label="运单号"><el-input v-model="shipForm.waybillNo" placeholder="请输入运单号" /></el-form-item>
        <el-form-item label="运费成本"><el-input-number v-model="shipForm.freightCost" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitShip">确 定</el-button>
        <el-button @click="shipOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 退货处理弹窗 -->
    <el-dialog title="整单退货处理" v-model="returnOpen" width="520px" append-to-body>
      <el-alert
        v-if="!currentOutboundSigned"
        title="该出库单尚未签收确认（未生成应收与收款），退货仅回补库存与释放订单进度，不产生现金退款。"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 12px"
      />
      <el-form ref="returnRef" :model="returnForm" label-width="100px">
        <el-form-item label="退款金额" v-if="currentOutboundSigned">
          <el-input-number v-model="returnForm.refundAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" placeholder="可选，仅可退实际已收款金额，自动核销红字应收" />
        </el-form-item>
        <el-form-item label="退款账户" v-if="currentOutboundSigned && Number(returnForm.refundAmount || 0) > 0">
          <el-select v-model="returnForm.fundAccountId" placeholder="请选择退款资金账户（空则取默认账户）" clearable filterable style="width: 100%">
            <el-option v-for="a in activeFundAccountOptions" :key="a.accountId" :label="`${a.accountName} (余额￥${Number(a.balance || 0).toFixed(2)})`" :value="a.accountId" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联应收单" v-if="currentOutboundSigned">
          <el-select v-model="returnForm.receivableId" placeholder="可选，校验该笔收款的可退上限" clearable filterable style="width: 100%">
            <el-option
              v-for="r in customerReceivables"
              :key="r.receivableId"
              :label="`${r.receivableNo} | 已收￥${Number(r.receivedAmount || 0).toFixed(2)} | 未收￥${Number(r.unreceivedAmount || 0).toFixed(2)}`"
              :value="r.receivableId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitReturn">确认退货</el-button>
        <el-button @click="returnOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 只读单据详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="`出库单详情 - ${detailDoc.outboundNo || ''}`" size="900px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 14px">
        <el-descriptions-item label="出库单号">{{ detailDoc.outboundNo }}</el-descriptions-item>
        <el-descriptions-item label="出库类型">{{ outboundTypeLabel(detailDoc.outboundType) }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ getCustomerName(detailDoc.customerId) }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ getWarehouseName(detailDoc.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="业务日期">{{ parseTime(detailDoc.businessDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ Number(detailDoc.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="承运商/运单">{{ detailDoc.carrier || '-' }} / {{ detailDoc.waybillNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="复核状态">{{ detailDoc.verifiedFlag === '1' ? `已复核 (${detailDoc.verifiedBy || ''})` : '未复核' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailDoc.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailDoc.itemList || []" border size="small">
        <el-table-column label="商品" min-width="145" show-overflow-tooltip><template #default="s">{{ getProductName(s.row.productId) }}</template></el-table-column>
        <el-table-column label="库位" min-width="120" show-overflow-tooltip><template #default="s">{{ getLocationName(s.row.locationId) }}</template></el-table-column>
        <el-table-column label="单位" prop="unitName" width="66" align="center"><template #default="s">{{ s.row.unitName || '-' }}</template></el-table-column>
        <el-table-column label="批次" prop="batchNo" width="128" align="center" show-overflow-tooltip><template #default="s">{{ s.row.batchNo || '-' }}</template></el-table-column>
        <el-table-column label="数量" prop="quantity" width="70" align="right" />
        <el-table-column label="单价" prop="price" width="80" align="right"><template #default="s">{{ Number(s.row.price || 0).toFixed(2) }}</template></el-table-column>
        <el-table-column label="金额" prop="amount" width="88" align="right"><template #default="s">{{ Number(s.row.amount || 0).toFixed(2) }}</template></el-table-column>
        <el-table-column label="SN码" prop="snCodes" min-width="125" show-overflow-tooltip><template #default="s">{{ s.row.snCodes || '-' }}</template></el-table-column>
      </el-table>
    </el-drawer>

    <business-trace-dialog ref="businessTraceRef" />
  </div>
</template>

<script setup name="Outbound">
import {
  listOutbound, addOutbound, delOutbound, getOutbound, updateOutbound,
  outboundShip, outboundSign, outboundReturn, auditOutbound, unauditOutbound, reverseOutbound, printOutbound
} from "@/api/business/outbound"
import BusinessTraceDialog from "@/components/Canglian/BusinessTraceDialog.vue"
import { useBusinessOptions } from "@/utils/businessOptions"
import { printBusinessDocument } from "@/utils/documentPrint"

const { proxy } = getCurrentInstance()
const {
  warehouseOptions, customerOptions, productOptions,
  activeWarehouseOptions, activeCustomerOptions, activeProductOptions, activeFundAccountOptions,
  loadWarehouses, loadCustomers, loadProducts, loadLocations, loadFundAccounts,
  fetchLocationsByWarehouse, fetchProductUnits, fetchFefoBatches, fetchOpenReceivables,
  resolveLiveSalePrice, isProductSerialEnabled,
  getWarehouseName, getCustomerName, getProductName, getLocationName
} = useBusinessOptions()

const OUTBOUND_TYPE_MAP = { sale: "销售出库", sale_order: "销货订单出库", other: "其他出库", reversal: "出库红冲" }
function outboundTypeLabel(v) { return OUTBOUND_TYPE_MAP[v] || v || "-" }

const docStatusOptions = [
  { label: "草稿", value: "0", tagType: "info" },
  { label: "已审核", value: "1", tagType: "success" },
  { label: "已完成", value: "2", tagType: "primary" }
]
const deliveryStatusOptions = [
  { label: "待发货", value: "0" },
  { label: "已发货", value: "1" },
  { label: "已签收", value: "2" },
  { label: "退货", value: "3" }
]

function getStatusLabel(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.label : (val || "-") }
function getStatusTagType(val) { const i = docStatusOptions.find(d => d.value === String(val ?? "")); return i ? i.tagType : "info" }

function requiredSnCount(row) {
  return Math.round(Number(row.quantity || 0) * Number(row.convertRatio || 1))
}
function countEnteredSns(raw) {
  if (!raw) return 0
  return new Set(String(raw).split(/[\s,，;；]+/).map(s => s.trim()).filter(Boolean)).size
}

const outboundList = ref([])
const warehouseLocations = ref([])
const customerReceivables = ref([])
const unitCache = reactive({})
const open = ref(false)
const detailOpen = ref(false)
const detailDoc = ref({})
const businessTraceRef = ref(null)
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
const returnForm = ref({ refundAmount: undefined, receivableId: undefined, fundAccountId: undefined })

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, outboundNo: undefined, customerId: undefined, status: undefined, deliveryStatus: undefined },
  rules: {
    outboundType: [{ required: true, message: "出库类型不能为空", trigger: "change" }],
    warehouseId: [{ required: true, message: "请选择仓库", trigger: "change" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getUnitOptions(productId) { return unitCache[productId] || [] }

async function ensureUnitsLoaded(productId) {
  if (!productId) return []
  if (unitCache[productId]) return unitCache[productId]
  const prod = productOptions.value.find(p => Number(p.productId) === Number(productId))
  const list = await fetchProductUnits(productId)
  const merged = list.length ? list : [{ unitName: (prod && (prod.baseUnit || prod.unitName)) || "件", convertRatio: 1, salePrice: prod ? prod.salePrice : 0 }]
  unitCache[productId] = merged
  return merged
}

function handleWarehouseChange(whId) {
  fetchLocationsByWarehouse(whId).then(rows => { warehouseLocations.value = rows })
}

async function loadFefoForRow(row) {
  if (!form.value.warehouseId || !row.productId) return
  row._fefoBatches = await fetchFefoBatches(form.value.warehouseId, row.productId)
}

function handleBatchSelect(row) {
  const matched = (row._fefoBatches || []).find(b => (b.batchNo || "") === (row.batchNo || ""))
  if (matched && matched.locationId !== undefined && matched.locationId !== null) {
    row.locationId = Number(matched.locationId)
  }
}

async function applyLivePrice(row) {
  if (!row.productId) return
  const resolved = await resolveLiveSalePrice(form.value.customerId, row.productId, row.unitName)
  if (resolved && resolved.price !== undefined && resolved.price !== null) {
    row.price = Number(resolved.price)
    if (resolved.convertRatio) row.convertRatio = Number(resolved.convertRatio)
  }
  recalcRow(row)
}

async function handleCustomerChanged() {
  for (const row of form.value.itemList || []) {
    if (row.productId) await applyLivePrice(row)
  }
}

function getList() {
  loading.value = true
  listOutbound(queryParams.value).then(res => {
    outboundList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}

function cancel() { open.value = false; reset() }

function reset() {
  form.value = {
    outboundId: undefined, outboundNo: undefined, outboundType: "sale",
    customerId: undefined, warehouseId: undefined, businessDate: undefined,
    carrier: undefined, waybillNo: undefined, freightCost: 0, deliveryStatus: "0",
    totalQty: 0, totalAmount: 0, status: "0", remark: undefined, itemList: []
  }
  warehouseLocations.value = []
  proxy.resetForm("outboundRef")
}

function handleAddItem() {
  if (!Array.isArray(form.value.itemList)) form.value.itemList = []
  form.value.itemList.push({
    productId: undefined, locationId: 0, unitName: "", convertRatio: 1,
    batchNo: "", quantity: 1, price: 0, amount: 0, snCodes: "", _fefoBatches: []
  })
}

function handleRemoveItem(idx) { form.value.itemList.splice(idx, 1); recalcTotals() }

async function handleProductChange(row) {
  const prod = productOptions.value.find(p => Number(p.productId) === Number(row.productId))
  const units = await ensureUnitsLoaded(row.productId)
  if (units.length) {
    const preferred = (prod && prod.saleUnit)
      ? (units.find(u => u.unitName === prod.saleUnit) || units[0])
      : units[0]
    row.unitName = preferred.unitName
    row.convertRatio = Number(preferred.convertRatio || 1)
  } else if (prod) {
    row.unitName = prod.saleUnit || prod.unitName || "件"
    row.convertRatio = 1
  }
  await applyLivePrice(row)
  await loadFefoForRow(row)
  if (row._fefoBatches && row._fefoBatches.length && !row.batchNo) {
    row.batchNo = row._fefoBatches[0].batchNo || ""
    row.locationId = Number(row._fefoBatches[0].locationId ?? 0)
  }
  recalcRow(row)
}

async function handleUnitChange(row) {
  const matched = getUnitOptions(row.productId).find(u => u.unitName === row.unitName)
  row.convertRatio = matched ? Number(matched.convertRatio || 1) : 1
  await applyLivePrice(row)
}

function recalcRow(row) {
  const qty = Number(row.quantity || 0)
  const price = Number(row.price || 0)
  const ratio = Number(row.convertRatio || 1)
  row.baseQuantity = Number((qty * ratio).toFixed(4))
  row.amount = Number((qty * price).toFixed(2))
  recalcTotals()
}

function recalcTotals() {
  const items = form.value.itemList || []
  form.value.totalQty = Number(items.reduce((s, i) => s + Number(i.quantity || 0), 0).toFixed(2))
  form.value.totalAmount = Number(items.reduce((s, i) => s + Number(i.amount || 0), 0).toFixed(2))
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.outboundId)
  isSingleDisabled.value = selection.length !== 1 || !canEditRow(selection[0])
  isMultipleDisabled.value = !selection.length || selection.some(i => !canDeleteRow(i))
}

function canEditRow(r) { return !!r && r.status === "0" }
function canDeleteRow(r) { return canEditRow(r) }
function canAuditRow(r) { return !!r && r.status === "0" }
function canUnauditRow(r) { return !!r && r.status === "1" && r.deliveryStatus === "0" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canReverseRow(r) { return !!r && r.status === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canShipRow(r) { return !!r && r.status === "1" && r.deliveryStatus === "0" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canSignRow(r) { return !!r && r.status === "1" && r.deliveryStatus === "1" && r.isReversal !== "1" && r.reversedFlag !== "1" }
function canReturnRow(r) { return !!r && r.status === "1" && ["1", "2"].includes(r.deliveryStatus) && r.isReversal !== "1" && r.reversedFlag !== "1" }

function handleAdd() { reset(); handleAddItem(); open.value = true; title.value = "新增出库单" }

function handleViewDetail(row) {
  getOutbound(row.outboundId).then(res => {
    detailDoc.value = res.data || row
    detailOpen.value = true
  })
}

function handleTrace(row) {
  businessTraceRef.value.open("outbound", row.outboundId)
}

function handleUpdate(row) {
  reset()
  getOutbound(row.outboundId || selectedIds.value).then(async res => {
    form.value = res.data || {}
    if (!Array.isArray(form.value.itemList)) form.value.itemList = []
    if (form.value.warehouseId) handleWarehouseChange(form.value.warehouseId)
    for (const it of form.value.itemList) await ensureUnitsLoaded(it.productId)
    recalcTotals()
    open.value = true
    title.value = "修改出库单"
  })
}

function handleAudit(row) {
  proxy.$modal.confirm("确认审核当前出库单？（将校验效期、信用额度与序列号并扣减库存；收入、成本与应收将在客户【签收】节点正式确认）").then(() => auditOutbound(row.outboundId)).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {})
}

function handleUnaudit(row) {
  proxy.$modal.confirm(`确认反审核出库单"${row.outboundNo}"？系统将回补库存与序列号并回退草稿。`).then(() => unauditOutbound(row.outboundId)).then(() => {
    proxy.$modal.msgSuccess("反审核成功，单据已退回草稿")
    getList()
  }).catch(() => {})
}

function handleReverse(row) {
  proxy.$modal.confirm(`确认对出库单"${row.outboundNo}"执行一键红冲？将自动生成红字出库单回冲库存，已签收单据同步红冲应收与收入成本凭证。`).then(() => reverseOutbound(row.outboundId)).then(() => {
    proxy.$modal.msgSuccess("一键红冲成功")
    getList()
  }).catch(() => {})
}

function handlePrint(row) {
  printOutbound(row.outboundId).then(async res => {
    const data = res.data || {}
    const h = data.header || row
    const dn = data.displayNames || {}
    const prodNames = dn.productNames || {}
    const locNames = dn.locationNames || {}
    const items = (data.items || []).map(it => ({
      ...it,
      productLabel: prodNames[String(it.productId)] || getProductName(it.productId),
      locationLabel: locNames[String(it.locationId)] || getLocationName(it.locationId),
      unitName: it.unitName || "件"
    }))
    await printBusinessDocument({
      title: h.isReversal === "1" ? "出库红冲单" : "销售/发货出库单",
      docNo: h.outboundNo,
      bizDate: proxy.parseTime(h.businessDate || h.createTime, "{y}-{m}-{d}"),
      counterpartyLabel: "客户",
      counterpartyName: dn.counterpartyName || getCustomerName(h.customerId),
      warehouseName: dn.warehouseName || getWarehouseName(h.warehouseId),
      displayNames: dn,
      extraMeta: [
        { label: "承运商", value: h.carrier || "-" },
        { label: "运单号", value: h.waybillNo || "-" }
      ],
      columns: [
        { label: "商品", prop: "productLabel" },
        { label: "库位", prop: "locationLabel", align: "center" },
        { label: "单位", prop: "unitName", align: "center" },
        { label: "批次号", prop: "batchNo", align: "center" },
        { label: "数量", prop: "quantity", align: "right", summary: "qty" },
        { label: "单价", prop: "price", align: "right" },
        { label: "金额", prop: "amount", align: "right", summary: "amount" }
      ],
      items,
      totalQty: h.totalQty,
      totalAmount: h.totalAmount,
      remark: h.remark,
      createBy: h.createBy,
      auditBy: h.auditBy
    })
  })
}

function submitForm() {
  proxy.$refs["outboundRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.outboundId ? updateOutbound(form.value) : addOutbound(form.value)
    fn.then(() => {
      proxy.$modal.msgSuccess(form.value.outboundId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleRowCommand(cmd, row) {
  if (cmd === "unaudit") handleUnaudit(row)
  else if (cmd === "reverse") handleReverse(row)
  else if (cmd === "return") handleReturn(row)
  else if (cmd === "trace") handleTrace(row)
  else if (cmd === "print") handlePrint(row)
  else if (cmd === "delete") handleDelete(row)
}

function handleDelete(row) {
  const ids = row.outboundId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中出库单？").then(() => delOutbound(ids)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    getList()
  }).catch(() => {})
}

function handleShip(row) {
  shipForm.value = { carrier: row.carrier || "", waybillNo: row.waybillNo || "", freightCost: Number(row.freightCost || 0) }
  currentOutboundId.value = row.outboundId
  shipOpen.value = true
}

function submitShip() {
  outboundShip(currentOutboundId.value, shipForm.value).then(() => {
    shipOpen.value = false
    proxy.$modal.msgSuccess("发货成功")
    getList()
  })
}

function handleSign(row) {
  proxy.$modal.confirm(`确认客户已签收出库单"${row.outboundNo}"？（将正式确认销售收入与结转成本，自动生成应收单与财务凭证事件）`).then(() => outboundSign(row.outboundId)).then(() => {
    proxy.$modal.msgSuccess("签收成功，收入/应收/成本凭证已联动生成")
    getList()
  }).catch(() => {})
}

const currentOutboundSigned = ref(false)
async function handleReturn(row) {
  currentOutboundId.value = row.outboundId
  currentOutboundSigned.value = row.deliveryStatus === "2"
  returnForm.value = { refundAmount: undefined, receivableId: undefined, fundAccountId: undefined }
  customerReceivables.value = currentOutboundSigned.value ? await fetchOpenReceivables(row.customerId, true) : []
  returnOpen.value = true
}

function submitReturn() {
  const params = {}
  if (returnForm.value.refundAmount !== undefined && returnForm.value.refundAmount !== null) {
    params.refundAmount = returnForm.value.refundAmount
  }
  if (returnForm.value.receivableId) {
    params.receivableId = returnForm.value.receivableId
  }
  if (returnForm.value.fundAccountId) {
    params.fundAccountId = returnForm.value.fundAccountId
  }
  outboundReturn(currentOutboundId.value, params).then(() => {
    returnOpen.value = false
    proxy.$modal.msgSuccess("退货处理成功")
    getList()
  })
}

loadWarehouses()
loadCustomers()
loadProducts()
loadLocations()
loadFundAccounts()
getList()

function handleExport() {
  proxy.download("business/outbound/export", { ...queryParams.value }, `销售出库单_${new Date().getTime()}.xlsx`)
}
</script>
