<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>商品扫码</template>
          <el-form :model="productScanForm" label-width="80px">
            <el-form-item label="商品条码">
              <el-input v-model="productScanForm.barCode" placeholder="请输入商品条码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleProductScan">扫码解析</el-button>
            </el-form-item>
          </el-form>
          <el-descriptions v-if="productResult.product" :column="1" border>
            <el-descriptions-item label="商品编号">{{ productResult.product.productId }}</el-descriptions-item>
            <el-descriptions-item label="商品编码">{{ productResult.product.productCode }}</el-descriptions-item>
            <el-descriptions-item label="商品名称">{{ productResult.product.productName }}</el-descriptions-item>
            <el-descriptions-item label="销售价">{{ productResult.product.salePrice }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>库位扫码</template>
          <el-form :model="locationScanForm" label-width="80px">
            <el-form-item label="库位编码">
              <el-input v-model="locationScanForm.locationCode" placeholder="请输入库位编码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLocationScan">扫码解析</el-button>
            </el-form-item>
          </el-form>
          <el-descriptions v-if="locationResult.location" :column="1" border>
            <el-descriptions-item label="库位编号">{{ locationResult.location.locationId }}</el-descriptions-item>
            <el-descriptions-item label="仓库编号">{{ locationResult.location.warehouseId }}</el-descriptions-item>
            <el-descriptions-item label="库位编码">{{ locationResult.location.locationCode }}</el-descriptions-item>
            <el-descriptions-item label="库位名称">{{ locationResult.location.locationName }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>库存扫码</template>
          <el-form :model="stockScanForm" label-width="80px">
            <el-form-item label="商品条码">
              <el-input v-model="stockScanForm.barCode" placeholder="请输入商品条码" />
            </el-form-item>
            <el-form-item label="仓库编号">
              <el-input v-model="stockScanForm.warehouseId" placeholder="请输入仓库编号" />
            </el-form-item>
            <el-form-item label="库位编码">
              <el-input v-model="stockScanForm.locationCode" placeholder="请输入库位编码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleStockScan">查询库存</el-button>
            </el-form-item>
          </el-form>
          <el-alert
            v-if="stockResult.totalQty !== undefined"
            :title="'当前库存合计：' + stockResult.totalQty + '，可用库存：' + (stockResult.availableQty || 0)"
            type="success"
            :closable="false"
          />
          <el-table v-if="stockResult.stockList" :data="stockResult.stockList" size="small" style="margin-top: 12px">
            <el-table-column label="库位编号" align="center" prop="locationId" />
            <el-table-column label="批次号" align="center" prop="batchNo" />
            <el-table-column label="数量" align="center" prop="quantity" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <template #header>扫码快速开单</template>
      <el-form ref="quickSaleRef" :model="quickSaleForm" :rules="rules" label-width="90px" inline>
        <el-form-item label="客户编号" prop="customerId">
          <el-input v-model="quickSaleForm.customerId" placeholder="请输入客户编号" />
        </el-form-item>
        <el-form-item label="仓库编号" prop="warehouseId">
          <el-input v-model="quickSaleForm.warehouseId" placeholder="请输入仓库编号" />
        </el-form-item>
        <el-form-item label="商品条码" prop="barCode">
          <el-input v-model="quickSaleForm.barCode" placeholder="请输入商品条码" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="quickSaleForm.quantity" controls-position="right" :min="1" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="quickSaleForm.remark" placeholder="请输入备注" style="width: 240px" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="handleAppendQuickSaleItem">加入清单</el-button>
          <el-button @click="handleClearQuickSaleItemList">清空清单</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuickSale">立即开单</el-button>
        </el-form-item>
      </el-form>

      <el-table v-if="quickSaleItemList.length > 0" :data="quickSaleItemList" size="small" style="margin-bottom: 16px">
        <el-table-column label="商品条码" align="center" prop="barCode" />
        <el-table-column label="商品编码" align="center" prop="productCode" />
        <el-table-column label="商品名称" align="center" prop="productName" />
        <el-table-column label="数量" align="center" prop="quantity" />
        <el-table-column label="销售价" align="center" prop="salePrice" />
        <el-table-column label="金额" align="center">
          <template #default="scope">
            {{ calculateQuickSaleItemAmount(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="100">
          <template #default="scope">
            <el-button link type="danger" @click="handleRemoveQuickSaleItem(scope.$index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-descriptions v-if="quickSaleResult.orderId" :column="4" border>
        <el-descriptions-item label="订单编号">{{ quickSaleResult.orderId }}</el-descriptions-item>
        <el-descriptions-item label="订单单号">{{ quickSaleResult.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户编号">{{ quickSaleResult.customerId }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ quickSaleResult.totalAmount }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>扫码盘点建单</template>
          <el-form ref="inventoryCheckRef" :model="inventoryCheckForm" :rules="inventoryCheckRules" label-width="90px" inline>
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="inventoryCheckForm.warehouseId" placeholder="请输入盘点仓库编号" />
            </el-form-item>
            <el-form-item label="库位编码" prop="locationCode">
              <el-input v-model="inventoryCheckForm.locationCode" placeholder="请输入盘点库位编码" />
            </el-form-item>
            <el-form-item label="商品条码" prop="barCode">
              <el-input v-model="inventoryCheckForm.barCode" placeholder="请输入商品条码" />
            </el-form-item>
            <el-form-item label="实盘数量" prop="actualQty">
              <el-input-number v-model="inventoryCheckForm.actualQty" controls-position="right" :min="0" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="inventoryCheckForm.remark" placeholder="请输入备注" style="width: 220px" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleAppendInventoryCheckItem">加入清单</el-button>
              <el-button @click="handleClearInventoryCheckItemList">清空清单</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleInventoryCheck">生成盘点单</el-button>
            </el-form-item>
          </el-form>

          <el-table v-if="inventoryCheckItemList.length > 0" :data="inventoryCheckItemList" size="small" style="margin-bottom: 16px">
            <el-table-column label="商品条码" align="center" prop="barCode" />
            <el-table-column label="商品编码" align="center" prop="productCode" />
            <el-table-column label="商品名称" align="center" prop="productName" />
            <el-table-column label="账面数量" align="center" prop="stockQty" />
            <el-table-column label="实盘数量" align="center" prop="actualQty" />
            <el-table-column label="差异数量" align="center" prop="diffQty" />
            <el-table-column label="操作" align="center" width="100">
              <template #default="scope">
                <el-button link type="danger" @click="handleRemoveInventoryCheckItem(scope.$index)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-descriptions v-if="inventoryCheckResult.checkId" :column="3" border>
            <el-descriptions-item label="盘点单编号">{{ inventoryCheckResult.checkId }}</el-descriptions-item>
            <el-descriptions-item label="盘点单号">{{ inventoryCheckResult.checkNo }}</el-descriptions-item>
            <el-descriptions-item label="仓库编号">{{ inventoryCheckResult.warehouseId }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>扫码调拨建单</template>
          <el-form ref="transferRef" :model="transferForm" :rules="transferRules" label-width="90px" inline>
            <el-form-item label="调出仓库" prop="outWarehouseId">
              <el-input v-model="transferForm.outWarehouseId" placeholder="请输入调出仓库编号" />
            </el-form-item>
            <el-form-item label="调入仓库" prop="inWarehouseId">
              <el-input v-model="transferForm.inWarehouseId" placeholder="请输入调入仓库编号" />
            </el-form-item>
            <el-form-item label="调出库位" prop="outLocationCode">
              <el-input v-model="transferForm.outLocationCode" placeholder="请输入调出库位编码" />
            </el-form-item>
            <el-form-item label="调入库位" prop="inLocationCode">
              <el-input v-model="transferForm.inLocationCode" placeholder="请输入调入库位编码" />
            </el-form-item>
            <el-form-item label="商品条码" prop="barCode">
              <el-input v-model="transferForm.barCode" placeholder="请输入商品条码" />
            </el-form-item>
            <el-form-item label="调拨数量" prop="quantity">
              <el-input-number v-model="transferForm.quantity" controls-position="right" :min="1" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="transferForm.remark" placeholder="请输入备注" style="width: 220px" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleAppendTransferItem">加入清单</el-button>
              <el-button @click="handleClearTransferItemList">清空清单</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTransfer">生成调拨单</el-button>
            </el-form-item>
          </el-form>

          <el-table v-if="transferItemList.length > 0" :data="transferItemList" size="small" style="margin-bottom: 16px">
            <el-table-column label="商品条码" align="center" prop="barCode" />
            <el-table-column label="商品编码" align="center" prop="productCode" />
            <el-table-column label="商品名称" align="center" prop="productName" />
            <el-table-column label="可用库存" align="center" prop="availableQty" />
            <el-table-column label="调拨数量" align="center" prop="quantity" />
            <el-table-column label="成本单价" align="center" prop="costPrice" />
            <el-table-column label="金额" align="center">
              <template #default="scope">
                {{ calculateTransferItemAmount(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="100">
              <template #default="scope">
                <el-button link type="danger" @click="handleRemoveTransferItem(scope.$index)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-descriptions v-if="transferResult.transferId" :column="3" border>
            <el-descriptions-item label="调拨单编号">{{ transferResult.transferId }}</el-descriptions-item>
            <el-descriptions-item label="调拨单号">{{ transferResult.transferNo }}</el-descriptions-item>
            <el-descriptions-item label="调出/调入">{{ transferResult.outWarehouseId }} -> {{ transferResult.inWarehouseId }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Scan">
import { scanProduct, scanLocation, scanStock, quickSaleByScan, inventoryCheckByScan, transferByScan } from "@/api/business/scan"

const { proxy } = getCurrentInstance()

const productScanForm = reactive({
  barCode: undefined
})
const locationScanForm = reactive({
  locationCode: undefined
})
const stockScanForm = reactive({
  barCode: undefined,
  warehouseId: undefined,
  locationCode: undefined
})
const quickSaleForm = reactive({
  customerId: undefined,
  warehouseId: undefined,
  barCode: undefined,
  quantity: 1,
  remark: undefined
})
const inventoryCheckForm = reactive({
  warehouseId: undefined,
  locationCode: undefined,
  barCode: undefined,
  actualQty: 1,
  remark: undefined
})
const transferForm = reactive({
  outWarehouseId: undefined,
  inWarehouseId: undefined,
  outLocationCode: undefined,
  inLocationCode: undefined,
  barCode: undefined,
  quantity: 1,
  remark: undefined
})

const productResult = ref({})
const locationResult = ref({})
const stockResult = ref({})
const quickSaleResult = ref({})
const inventoryCheckResult = ref({})
const transferResult = ref({})
const quickSaleItemList = ref([])
const inventoryCheckItemList = ref([])
const transferItemList = ref([])

const rules = {
  customerId: [{ required: true, message: "客户编号不能为空", trigger: "blur" }],
  warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
  barCode: [{ required: true, message: "商品条码不能为空", trigger: "blur" }]
}
const inventoryCheckRules = {
  warehouseId: [{ required: true, message: "盘点仓库不能为空", trigger: "blur" }],
  locationCode: [{ required: true, message: "盘点库位编码不能为空", trigger: "blur" }],
  barCode: [{ required: true, message: "商品条码不能为空", trigger: "blur" }]
}
const transferRules = {
  outWarehouseId: [{ required: true, message: "调出仓库不能为空", trigger: "blur" }],
  inWarehouseId: [{ required: true, message: "调入仓库不能为空", trigger: "blur" }],
  outLocationCode: [{ required: true, message: "调出库位编码不能为空", trigger: "blur" }],
  inLocationCode: [{ required: true, message: "调入库位编码不能为空", trigger: "blur" }],
  barCode: [{ required: true, message: "商品条码不能为空", trigger: "blur" }]
}

// 扫码解析商品
function handleProductScan() {
  scanProduct(productScanForm.barCode).then(response => {
    productResult.value = response.data || {}
  })
}

// 扫码解析库位
function handleLocationScan() {
  scanLocation(locationScanForm.locationCode).then(response => {
    locationResult.value = response.data || {}
  })
}

// 扫码查询库存
function handleStockScan() {
  scanStock(stockScanForm).then(response => {
    stockResult.value = response.data || {}
  })
}

// 扫码快速开单
function handleQuickSale() {
  const requestData = {
    ...quickSaleForm,
    quickSaleItemList: quickSaleItemList.value
  }
  if (quickSaleItemList.value.length > 0) {
    proxy.$refs["quickSaleRef"].validateField(["customerId", "warehouseId"]).then(() => {
      quickSaleByScan(requestData).then(response => {
        quickSaleResult.value = response.data || {}
        quickSaleItemList.value = []
        quickSaleForm.barCode = undefined
        quickSaleForm.quantity = 1
        proxy.$modal.msgSuccess("扫码开单成功")
      })
    }).catch(() => {})
    return
  }
  proxy.$refs["quickSaleRef"].validate(valid => {
    if (valid) {
      quickSaleByScan(requestData).then(response => {
        quickSaleResult.value = response.data || {}
        quickSaleItemList.value = []
        proxy.$modal.msgSuccess("扫码开单成功")
      })
    }
  })
}

// 加入扫码开单清单
function handleAppendQuickSaleItem() {
  proxy.$refs["quickSaleRef"].validateField(["barCode"]).then(() => {
    if (!quickSaleForm.quantity || quickSaleForm.quantity <= 0) {
      proxy.$modal.msgError("扫码数量必须大于0")
      return
    }
    scanProduct(quickSaleForm.barCode).then(response => {
      const productInfo = response.data?.product
      if (!productInfo) {
        proxy.$modal.msgError("未找到扫码商品")
        return
      }
      const existingQuickSaleItem = quickSaleItemList.value.find(item => item.barCode === quickSaleForm.barCode)
      if (existingQuickSaleItem) {
        existingQuickSaleItem.quantity = Number(existingQuickSaleItem.quantity) + Number(quickSaleForm.quantity)
        proxy.$modal.msgSuccess("重复扫码已累计数量")
      } else {
        quickSaleItemList.value.push({
          barCode: quickSaleForm.barCode,
          productId: productInfo.productId,
          productCode: productInfo.productCode,
          productName: productInfo.productName,
          quantity: Number(quickSaleForm.quantity),
          salePrice: productInfo.salePrice || 0
        })
        proxy.$modal.msgSuccess("已加入扫码清单")
      }
      quickSaleForm.barCode = undefined
      quickSaleForm.quantity = 1
    })
  }).catch(() => {})
}

// 移除扫码开单明细
function handleRemoveQuickSaleItem(index) {
  quickSaleItemList.value.splice(index, 1)
}

// 清空扫码开单清单
function handleClearQuickSaleItemList() {
  quickSaleItemList.value = []
}

// 计算扫码明细金额
function calculateQuickSaleItemAmount(quickSaleItem) {
  return Number(quickSaleItem.quantity || 0) * Number(quickSaleItem.salePrice || 0)
}

// 生成扫码盘点单
function handleInventoryCheck() {
  const requestData = {
    warehouseId: inventoryCheckForm.warehouseId,
    locationCode: inventoryCheckForm.locationCode,
    remark: inventoryCheckForm.remark,
    inventoryCheckItemList: inventoryCheckItemList.value
  }
  if (inventoryCheckItemList.value.length === 0) {
    proxy.$modal.msgError("请先加入扫码盘点清单")
    return
  }
  proxy.$refs["inventoryCheckRef"].validateField(["warehouseId", "locationCode"]).then(() => {
    inventoryCheckByScan(requestData).then(response => {
      inventoryCheckResult.value = response.data || {}
      inventoryCheckItemList.value = []
      inventoryCheckForm.barCode = undefined
      inventoryCheckForm.actualQty = 1
      proxy.$modal.msgSuccess("扫码盘点单生成成功")
    })
  }).catch(() => {})
}

// 加入扫码盘点清单
function handleAppendInventoryCheckItem() {
  proxy.$refs["inventoryCheckRef"].validateField(["warehouseId", "locationCode", "barCode"]).then(() => {
    if (inventoryCheckForm.actualQty === undefined || inventoryCheckForm.actualQty < 0) {
      proxy.$modal.msgError("实盘数量不能小于0")
      return
    }
    Promise.all([
      scanProduct(inventoryCheckForm.barCode),
      scanStock({
        barCode: inventoryCheckForm.barCode,
        warehouseId: inventoryCheckForm.warehouseId,
        locationCode: inventoryCheckForm.locationCode
      })
    ]).then(([productResponse, stockResponse]) => {
      const productInfo = productResponse.data?.product
      const stockQty = Number(stockResponse.data?.totalQty || 0)
      if (!productInfo) {
        proxy.$modal.msgError("未找到扫码商品")
        return
      }
      const existingInventoryItem = inventoryCheckItemList.value.find(item => item.barCode === inventoryCheckForm.barCode)
      if (existingInventoryItem) {
        existingInventoryItem.actualQty = Number(existingInventoryItem.actualQty || 0) + Number(inventoryCheckForm.actualQty || 0)
        existingInventoryItem.diffQty = Number(existingInventoryItem.actualQty || 0) - Number(existingInventoryItem.stockQty || 0)
        proxy.$modal.msgSuccess("重复扫码已累计实盘数量")
      } else {
        inventoryCheckItemList.value.push({
          barCode: inventoryCheckForm.barCode,
          productId: productInfo.productId,
          productCode: productInfo.productCode,
          productName: productInfo.productName,
          stockQty: stockQty,
          actualQty: Number(inventoryCheckForm.actualQty || 0),
          diffQty: Number(inventoryCheckForm.actualQty || 0) - stockQty
        })
        proxy.$modal.msgSuccess("已加入盘点清单")
      }
      inventoryCheckForm.barCode = undefined
      inventoryCheckForm.actualQty = 1
    })
  }).catch(() => {})
}

// 移除扫码盘点明细
function handleRemoveInventoryCheckItem(index) {
  inventoryCheckItemList.value.splice(index, 1)
}

// 清空扫码盘点清单
function handleClearInventoryCheckItemList() {
  inventoryCheckItemList.value = []
}

// 生成扫码调拨单
function handleTransfer() {
  const requestData = {
    outWarehouseId: transferForm.outWarehouseId,
    inWarehouseId: transferForm.inWarehouseId,
    outLocationCode: transferForm.outLocationCode,
    inLocationCode: transferForm.inLocationCode,
    remark: transferForm.remark,
    transferItemList: transferItemList.value
  }
  if (transferItemList.value.length === 0) {
    proxy.$modal.msgError("请先加入扫码调拨清单")
    return
  }
  proxy.$refs["transferRef"].validateField(["outWarehouseId", "inWarehouseId", "outLocationCode", "inLocationCode"]).then(() => {
    transferByScan(requestData).then(response => {
      transferResult.value = response.data || {}
      transferItemList.value = []
      transferForm.barCode = undefined
      transferForm.quantity = 1
      proxy.$modal.msgSuccess("扫码调拨单生成成功")
    })
  }).catch(() => {})
}

// 加入扫码调拨清单
function handleAppendTransferItem() {
  proxy.$refs["transferRef"].validateField(["outWarehouseId", "inWarehouseId", "outLocationCode", "inLocationCode", "barCode"]).then(() => {
    if (!transferForm.quantity || transferForm.quantity <= 0) {
      proxy.$modal.msgError("调拨数量必须大于0")
      return
    }
    Promise.all([
      scanProduct(transferForm.barCode),
      scanStock({
        barCode: transferForm.barCode,
        warehouseId: transferForm.outWarehouseId,
        locationCode: transferForm.outLocationCode
      })
    ]).then(([productResponse, stockResponse]) => {
      const productInfo = productResponse.data?.product
      const availableQty = Number(stockResponse.data?.availableQty || 0)
      if (!productInfo) {
        proxy.$modal.msgError("未找到扫码商品")
        return
      }
      const existingTransferItem = transferItemList.value.find(item => item.barCode === transferForm.barCode)
      const mergedQty = Number(existingTransferItem?.quantity || 0) + Number(transferForm.quantity || 0)
      if (mergedQty > availableQty) {
        proxy.$modal.msgError("可用库存不足，无法加入清单")
        return
      }
      if (existingTransferItem) {
        existingTransferItem.quantity = mergedQty
        proxy.$modal.msgSuccess("重复扫码已累计调拨数量")
      } else {
        transferItemList.value.push({
          barCode: transferForm.barCode,
          productId: productInfo.productId,
          productCode: productInfo.productCode,
          productName: productInfo.productName,
          availableQty: availableQty,
          quantity: Number(transferForm.quantity || 0),
          costPrice: Number(productInfo.costPrice || 0)
        })
        proxy.$modal.msgSuccess("已加入调拨清单")
      }
      transferForm.barCode = undefined
      transferForm.quantity = 1
    })
  }).catch(() => {})
}

// 移除扫码调拨明细
function handleRemoveTransferItem(index) {
  transferItemList.value.splice(index, 1)
}

// 清空扫码调拨清单
function handleClearTransferItemList() {
  transferItemList.value = []
}

// 计算扫码调拨明细金额
function calculateTransferItemAmount(transferItem) {
  return Number(transferItem.quantity || 0) * Number(transferItem.costPrice || 0)
}
</script>
