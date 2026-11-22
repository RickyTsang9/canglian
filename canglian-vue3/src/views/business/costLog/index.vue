<template>
  <div class="app-container">
    <el-alert
      title="成本流水为不可篡改的财务审计明细，记录每笔出入库、调拨、盘点、退货及受控调价的历史真实结转成本，供销售毛利表与利润表溯源。"
      type="info"
      show-icon
      :closable="false"
      style="margin-bottom: 12px"
    />
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 220px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable filterable style="width: 200px">
          <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
        </el-select>
      </el-form-item>
      <el-form-item label="单据类型" prop="billType">
        <el-select v-model="queryParams.billType" placeholder="请选择单据类型" clearable filterable style="width: 170px">
          <el-option v-for="(label, code) in billTypeMap" :key="code" :label="label" :value="code" />
        </el-select>
      </el-form-item>
      <el-form-item label="方向" prop="inOut">
        <el-select v-model="queryParams.inOut" placeholder="请选择" clearable style="width: 130px">
          <el-option label="入库/调增" value="1" />
          <el-option label="出库/调减" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="成本算法" prop="costMethod">
        <el-select v-model="queryParams.costMethod" placeholder="请选择" clearable style="width: 150px">
          <el-option label="移动加权平均" value="moving" />
          <el-option label="先进先出" value="fifo" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="costLogList" border>
      <el-table-column label="流水编号" align="center" prop="costLogId" width="90" />
      <el-table-column label="商品" align="center" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getProductName(scope.row.productId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="115" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getWarehouseName(scope.row.warehouseId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单据类型" align="center" width="125">
        <template #default="scope">
          <el-tag effect="plain">{{ billTypeMap[scope.row.billType] || scope.row.billType || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源ID" align="center" prop="billId" width="85" />
      <el-table-column label="方向" align="center" width="95">
        <template #default="scope">
          <el-tag :type="scope.row.inOut === '1' ? 'success' : 'warning'" size="small">
            {{ scope.row.inOut === '1' ? '入库/调增' : '出库/调减' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="数量" align="right" prop="quantity" width="90" />
      <el-table-column label="结转单价" align="right" prop="price" width="100" />
      <el-table-column label="结转金额" align="right" prop="amount" width="110" />
      <el-table-column label="成本算法" align="center" width="115">
        <template #default="scope">{{ costMethodMap[scope.row.costMethod] || scope.row.costMethod || '移动加权平均' }}</template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="140" show-overflow-tooltip />
      <el-table-column label="记录时间" align="center" prop="createTime" width="175">
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

<script setup name="CostLog">
import { listCostLog } from "@/api/business/costLog"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { productOptions, warehouseOptions, loadProducts, loadWarehouses, getProductName, getWarehouseName } = useBusinessOptions()

const costLogList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const billTypeMap = {
  inbound: "采购入库",
  inbound_unaudit: "入库反审核",
  inbound_reverse: "入库红冲",
  outbound: "销售出库",
  outbound_unaudit: "出库反审核",
  outbound_reverse: "出库红冲",
  outbound_return: "出库整单退货",
  sale_return: "销售退货",
  purchase_return: "采购退货",
  transfer: "库存调拨",
  transfer_out: "调拨出库",
  transfer_in: "调拨入库",
  inventory_check: "库存盘点",
  cost_adjust: "受控调价"
}

const costMethodMap = {
  moving: "移动加权平均",
  fifo: "先进先出"
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productId: undefined,
  warehouseId: undefined,
  billType: undefined,
  inOut: undefined,
  costMethod: undefined
})

function getList() {
  loading.value = true
  listCostLog(queryParams).then(response => {
    costLogList.value = response.rows || []
    total.value = response.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

Promise.all([loadProducts(), loadWarehouses()]).finally(() => {
  getList()
})
</script>
