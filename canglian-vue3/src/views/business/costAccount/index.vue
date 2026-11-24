<template>
  <div class="app-container">
    <el-alert
      title="成本账户为系统自动维护的只读财务台账，随出入库、退货审核自动更新；如需调价或重算，请前往「成本核算」页面执行受控成本调价或期末重算。"
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
      <el-form-item label="成本算法" prop="costMethod">
        <el-select v-model="queryParams.costMethod" placeholder="请选择算法" clearable style="width: 180px">
          <el-option label="移动加权" value="moving" />
          <el-option label="先进先出" value="fifo" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:costAccount:export', 'business:costAccount:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="costAccountList" border>
      <el-table-column label="成本账户" align="center" prop="costAccountId" width="84" />
      <el-table-column label="商品" align="center" min-width="135" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getProductName(scope.row.productId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="96" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getWarehouseName(scope.row.warehouseId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本算法" align="center" width="96">
        <template #default="scope">
          <el-tag size="small" effect="plain">{{ scope.row.costMethod === 'fifo' ? '先进先出' : '移动加权' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结存数量" align="right" header-align="right" prop="totalQty" width="88" />
      <el-table-column label="结存总金额" align="right" header-align="right" prop="totalAmount" width="104">
        <template #default="scope">{{ Number(scope.row.totalAmount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="单位均价" align="right" header-align="right" prop="avgCost" width="92">
        <template #default="scope">{{ Number(scope.row.avgCost || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="最近入库价" align="right" header-align="right" prop="lastCostPrice" width="98">
        <template #default="scope">{{ Number(scope.row.lastCostPrice || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="最近入库数" align="right" header-align="right" prop="lastCostQty" width="98" />
      <el-table-column label="备注" align="center" prop="remark" min-width="115" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.remark || '-' }}</template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</span>
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

<script setup name="CostAccount">
import { listCostAccount } from "@/api/business/costAccount"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { productOptions, warehouseOptions, loadProducts, loadWarehouses, getProductName, getWarehouseName } = useBusinessOptions()

const costAccountList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productId: undefined,
  warehouseId: undefined,
  costMethod: undefined
})

function getList() {
  loading.value = true
  listCostAccount(queryParams).then(response => {
    costAccountList.value = response.rows || []
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

function handleExport() {
  proxy.download("business/costAccount/export", { ...queryParams }, `成本总账_${new Date().getTime()}.xlsx`)
}
</script>
