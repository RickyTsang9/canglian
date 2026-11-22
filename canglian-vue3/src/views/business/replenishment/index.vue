<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable filterable style="width: 220px">
          <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="replenishmentList" border>
      <el-table-column label="仓库" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ getWarehouseName(scope.row.warehouseId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品编码" align="center" prop="productCode" width="120" />
      <el-table-column label="商品名称" align="center" prop="productName" min-width="150" show-overflow-tooltip />
      <el-table-column label="当前库存" align="right" prop="currentQty" width="90" />
      <el-table-column label="最小库存" align="right" prop="warningMinQty" width="90" />
      <el-table-column label="近30天销量" align="right" prop="recentOutboundQty" width="100" />
      <el-table-column label="在途采购量" align="right" prop="pendingPurchaseQty" width="100" />
      <el-table-column label="建议补货量" align="right" prop="suggestedQty" width="100">
        <template #default="scope"><strong style="color: #e6a23c">{{ scope.row.suggestedQty }}</strong></template>
      </el-table-column>
      <el-table-column label="建议说明" align="center" prop="suggestionReason" min-width="170" show-overflow-tooltip />
      <el-table-column label="操作" width="145" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="ShoppingCart" :disabled="Number(scope.row.suggestedQty || 0) <= 0" @click="openGenPoDialog(scope.row)" v-hasPermi="['business:purOrder:add']">一键生成采单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="一键生成采购订单" v-model="poOpen" width="480px" append-to-body>
      <el-form :model="poForm" label-width="95px">
        <el-form-item label="补货仓库"><el-input :model-value="getWarehouseName(poForm.warehouseId)" disabled /></el-form-item>
        <el-form-item label="补货商品"><el-input :model-value="`${poForm.productCode || ''} - ${poForm.productName || ''}`" disabled /></el-form-item>
        <el-form-item label="供应商" required>
          <el-select v-model="poForm.supplierId" placeholder="请选择供应商" filterable style="width: 100%">
            <el-option v-for="s in activeSupplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购数量" required>
          <el-input-number v-model="poForm.quantity" :min="1" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="参考单价">
          <el-input-number v-model="poForm.price" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitGenPo">生成草稿采购订单</el-button>
        <el-button @click="poOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Replenishment">
import { listReplenishment } from "@/api/business/replenishment"
import { addPurOrder } from "@/api/business/purOrder"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const {
  warehouseOptions, productOptions, activeSupplierOptions,
  loadWarehouses, loadSuppliers, loadProducts, getWarehouseName
} = useBusinessOptions()

const replenishmentList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const poOpen = ref(false)
const poForm = reactive({ warehouseId: undefined, productId: undefined, productCode: "", productName: "", supplierId: undefined, quantity: 1, price: 0 })

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, warehouseId: undefined }
})
const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listReplenishment(queryParams.value).then(response => {
    replenishmentList.value = response.rows || []
    total.value = response.total || 0
  }).finally(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function openGenPoDialog(row) {
  const prod = productOptions.value.find(p => Number(p.productId) === Number(row.productId))
  Object.assign(poForm, {
    warehouseId: row.warehouseId,
    productId: row.productId,
    productCode: row.productCode,
    productName: row.productName,
    supplierId: activeSupplierOptions.value[0]?.supplierId,
    quantity: Math.max(1, Number(row.suggestedQty || 1)),
    price: Number(prod?.costPrice || 0)
  })
  poOpen.value = true
}

function submitGenPo() {
  if (!poForm.supplierId) { proxy.$modal.msgError("请选择供应商"); return }
  const prod = productOptions.value.find(p => Number(p.productId) === Number(poForm.productId))
  const today = new Date().toISOString().slice(0, 10)
  const amount = Number((Number(poForm.quantity || 0) * Number(poForm.price || 0)).toFixed(2))
  addPurOrder({
    supplierId: poForm.supplierId,
    warehouseId: poForm.warehouseId,
    businessDate: today,
    remark: "由智能补货建议一键生成",
    orderItemList: [{
      productId: poForm.productId,
      unitName: prod?.baseUnit || prod?.unitName || "件",
      convertRatio: 1,
      quantity: poForm.quantity,
      price: poForm.price,
      discountRate: 100,
      taxRate: 0,
      amount
    }]
  }).then(() => {
    proxy.$modal.msgSuccess("已生成草稿采购订单，请前往「购货订单」审批下推")
    poOpen.value = false
    getList()
  })
}

loadWarehouses(); loadSuppliers(); loadProducts(); getList()
</script>
