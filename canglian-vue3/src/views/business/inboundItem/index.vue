<template>
  <div class="app-container">
    <el-alert title="入库明细台账为只读查询视图，请前往「入库单」执行新增、编辑与审核，确保表头汇总与库存、应付严密一致。" type="info" :closable="false" show-icon class="mb8" />
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="入库单" prop="inboundId">
        <el-select v-model="queryParams.inboundId" placeholder="请选择入库单" clearable filterable style="width: 220px">
          <el-option v-for="ib in inboundOptions" :key="ib.inboundId" :label="ib.inboundNo" :value="ib.inboundId" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 220px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="inboundItemList" border>
      <el-table-column label="入库单号" align="center" min-width="150">
        <template #default="scope">{{ getInboundLabel(scope.row.inboundId) }}</template>
      </el-table-column>
      <el-table-column label="商品" align="center" min-width="175" show-overflow-tooltip>
        <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
      </el-table-column>
      <el-table-column label="库位" align="center" width="115" show-overflow-tooltip>
        <template #default="scope">{{ getLocationName(scope.row.locationId) }}</template>
      </el-table-column>
      <el-table-column label="单位" align="center" prop="unitName" width="75" />
      <el-table-column label="批次号" align="center" prop="batchNo" width="105" />
      <el-table-column label="有效期至" align="center" width="105">
        <template #default="scope">{{ parseTime(scope.row.expiryDate, '{y}-{m}-{d}') || '-' }}</template>
      </el-table-column>
      <el-table-column label="数量" align="right" prop="quantity" width="85" />
      <el-table-column label="基准数量" align="right" prop="baseQuantity" width="90" />
      <el-table-column label="单价" align="right" prop="price" width="90" />
      <el-table-column label="金额" align="right" prop="amount" width="100" />
      <el-table-column label="序列号(SN)" prop="snCodes" min-width="130" show-overflow-tooltip />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="InboundItem">
import { listInboundItem } from "@/api/business/inboundItem"
import { listInbound } from "@/api/business/inbound"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { productOptions, loadProducts, loadLocations, getProductName, getLocationName } = useBusinessOptions()

loadProducts(); loadLocations()
const inboundOptions = ref([])
listInbound({ pageNum: 1, pageSize: 500 }).then(res => { inboundOptions.value = res.rows || [] }).catch(() => {})

function getInboundLabel(id) {
  const f = inboundOptions.value.find(i => Number(i.inboundId) === Number(id))
  return f ? f.inboundNo : `#${id}`
}

const inboundItemList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, inboundId: undefined, productId: undefined })

function getList() {
  loading.value = true
  listInboundItem(queryParams).then(res => {
    inboundItemList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}
function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }
getList()
</script>
