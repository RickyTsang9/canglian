<template>
  <div class="app-container">
    <el-alert
      title="成本分层为先进先出（FIFO）模式下的只读入库批次成本层，由系统按入库与出库顺序自动维护，禁止手工增删改。"
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
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="costLayerList" border>
      <el-table-column label="分层编号" align="center" prop="costLayerId" width="90" />
      <el-table-column label="成本账户" align="center" prop="costAccountId" width="90" />
      <el-table-column label="商品" align="center" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getProductName(scope.row.productId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="仓库" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getWarehouseName(scope.row.warehouseId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入库数量" align="center" prop="quantity" />
      <el-table-column label="剩余数量" align="center" prop="remainingQty" />
      <el-table-column label="层单价" align="center" prop="price" />
      <el-table-column label="层金额" align="center" prop="amount" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
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

<script setup name="CostLayer">
import { listCostLayer } from "@/api/business/costLayer"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { productOptions, warehouseOptions, loadProducts, loadWarehouses, getProductName, getWarehouseName } = useBusinessOptions()

const costLayerList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productId: undefined,
  warehouseId: undefined
})

function getList() {
  loading.value = true
  listCostLayer(queryParams).then(response => {
    costLayerList.value = response.rows || []
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
