<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable filterable style="width: 200px">
          <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 220px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item label="单据类型" prop="billType">
        <el-select v-model="queryParams.billType" placeholder="请选择单据类型" clearable style="width: 180px">
          <el-option v-for="item in billTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="出入库方向" prop="inOut">
        <el-select v-model="queryParams.inOut" placeholder="请选择方向" clearable style="width: 150px">
          <el-option label="入库" value="1" />
          <el-option label="出库" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="12">
        <el-tag type="info">库存流水为系统自动记录的审计台账（只读，不可手工改删）</el-tag>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockLogList" border>
      <el-table-column label="流水号" align="center" prop="stockLogId" width="72" />
      <el-table-column label="仓库" align="center" min-width="95" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getWarehouseName(scope.row.warehouseId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品" align="center" min-width="130" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getProductName(scope.row.productId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单据类型" align="center" prop="billType" width="96">
        <template #default="scope">
          <el-tag size="small" effect="plain">{{ getBillTypeLabel(scope.row.billType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="单据单号" align="center" min-width="140" show-overflow-tooltip>
        <template #default="scope">{{ getBillNoLabel(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="方向" align="center" prop="inOut" width="68">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.inOut === '1' ? 'success' : 'warning'">
            {{ scope.row.inOut === "1" ? "入库" : (scope.row.inOut === "2" ? "出库" : scope.row.inOut) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变动数" align="right" header-align="right" prop="quantity" width="76" />
      <el-table-column label="单价" align="right" header-align="right" prop="price" width="82">
        <template #default="scope">{{ Number(scope.row.price || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="金额" align="right" header-align="right" prop="amount" width="92">
        <template #default="scope">{{ Number(scope.row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="变动前" align="right" header-align="right" prop="beforeQty" width="76" />
      <el-table-column label="变动后" align="right" header-align="right" prop="afterQty" width="76" />
      <el-table-column label="记录时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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
  </div>
</template>

<script setup name="StockLog">
import { listStockLog } from "@/api/business/stockLog"
import { listInbound } from "@/api/business/inbound"
import { listOutbound } from "@/api/business/outbound"
import { listTransfer } from "@/api/business/transfer"
import { listInventoryCheck } from "@/api/business/inventoryCheck"
import { listPurchaseReturn } from "@/api/business/purchaseReturn"
import { listSaleReturn } from "@/api/business/saleReturn"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { warehouseOptions, productOptions, loadWarehouses, loadProducts, getWarehouseName, getProductName } = useBusinessOptions()

const billNoMap = reactive({})
function loadBillNoMaps() {
  listInbound({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { billNoMap[`inbound:${i.inboundId}`] = i.inboundNo })).catch(() => {})
  listOutbound({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => {
    billNoMap[`outbound:${i.outboundId}`] = i.outboundNo
    billNoMap[`outbound_return:${i.outboundId}`] = i.outboundNo
  })).catch(() => {})
  listTransfer({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => {
    billNoMap[`transfer:${i.transferId}`] = i.transferNo
    billNoMap[`scan_transfer:${i.transferId}`] = i.transferNo
  })).catch(() => {})
  listInventoryCheck({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => {
    billNoMap[`inventory_check:${i.checkId}`] = i.checkNo
    billNoMap[`scan_check:${i.checkId}`] = i.checkNo
  })).catch(() => {})
  listPurchaseReturn({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { billNoMap[`purchase_return:${i.purchaseReturnId}`] = i.returnNo })).catch(() => {})
  listSaleReturn({ pageNum: 1, pageSize: 500 }).then(r => (r.rows || []).forEach(i => { billNoMap[`sale_return:${i.saleReturnId}`] = i.returnNo })).catch(() => {})
}
function getBillNoLabel(row) {
  if (row.billNo) return row.billNo
  const key = `${row.billType || ""}:${row.billId}`
  return billNoMap[key] || (row.billId ? `#${row.billId}` : "-")
}

const stockLogList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const billTypeOptions = [
  { label: "采购入库", value: "inbound" },
  { label: "销售出库", value: "outbound" },
  { label: "出库退货", value: "outbound_return" },
  { label: "采购退货", value: "purchase_return" },
  { label: "销售退货", value: "sale_return" },
  { label: "库存调拨", value: "transfer" },
  { label: "库存盘点", value: "inventory_check" },
  { label: "扫码调拨", value: "scan_transfer" },
  { label: "扫码盘点", value: "scan_check" }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    warehouseId: undefined,
    productId: undefined,
    billType: undefined,
    inOut: undefined
  }
})

const { queryParams } = toRefs(data)

function getBillTypeLabel(type) {
  const matched = billTypeOptions.find(item => item.value === type)
  return matched ? matched.label : (type || "-")
}

function getList() {
  loading.value = true
  listStockLog(queryParams.value).then(response => {
    stockLogList.value = response.rows || []
    total.value = response.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

loadWarehouses()
loadProducts()
loadBillNoMaps()
getList()
</script>
