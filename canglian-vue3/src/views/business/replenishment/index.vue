<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="replenishmentList">
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="商品编码" align="center" prop="productCode" />
      <el-table-column label="商品名称" align="center" prop="productName" />
      <el-table-column label="当前库存" align="center" prop="currentQty" />
      <el-table-column label="最小库存" align="center" prop="warningMinQty" />
      <el-table-column label="近30天销量" align="center" prop="recentOutboundQty" />
      <el-table-column label="在途采购量" align="center" prop="pendingPurchaseQty" />
      <el-table-column label="建议补货量" align="center" prop="suggestedQty" />
      <el-table-column label="建议说明" align="center" prop="suggestionReason" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="Replenishment">
import { listReplenishment } from "@/api/business/replenishment"

const { proxy } = getCurrentInstance()

const replenishmentList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    warehouseId: undefined
  }
})

const { queryParams } = toRefs(data)

// 查询补货建议列表
function getList() {
  loading.value = true
  listReplenishment(queryParams.value).then(response => {
    replenishmentList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 搜索按钮操作
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置按钮操作
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

getList()
</script>
