<template>
  <div class="app-container scan-pda-container">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 1. 扫码查询 -->
      <el-tab-pane label="条码/库位/库存解析" name="lookup">
        <el-row :gutter="16" class="equal-row">
          <el-col :xs="24" :md="8" class="equal-col">
            <el-card shadow="never" class="equal-card">
              <template #header>商品/包装条码解析</template>
              <el-form :model="productScanForm" label-width="80px">
                <el-form-item label="条码">
                  <el-input v-model="productScanForm.barCode" placeholder="扫码枪扫描或输入回车" clearable @keyup.enter="handleProductScan" />
                </el-form-item>
                <el-form-item><el-button type="primary" @click="handleProductScan">解析商品</el-button></el-form-item>
              </el-form>
              <el-descriptions v-if="productResult.product" :column="1" border size="small">
                <el-descriptions-item label="编码">{{ productResult.product.productCode }}</el-descriptions-item>
                <el-descriptions-item label="名称">{{ productResult.product.productName }}</el-descriptions-item>
                <el-descriptions-item label="默认售价">￥{{ productResult.product.salePrice }}</el-descriptions-item>
                <el-descriptions-item v-if="resolvedScanUnit" label="命中包装单位">
                  {{ resolvedScanUnit.unitName }} (×{{ resolvedScanUnit.convertRatio }})
                  <span v-if="resolvedScanUnit.salePrice > 0"> | 包装售价 ￥{{ resolvedScanUnit.salePrice }}</span>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="8" class="equal-col">
            <el-card shadow="never" class="equal-card">
              <template #header>库位扫码解析</template>
              <el-form :model="locationScanForm" label-width="80px">
                <el-form-item label="库位码">
                  <el-input v-model="locationScanForm.locationCode" placeholder="扫描库位码并回车" clearable @keyup.enter="handleLocationScan" />
                </el-form-item>
                <el-form-item><el-button type="primary" @click="handleLocationScan">解析库位</el-button></el-form-item>
              </el-form>
              <el-descriptions v-if="locationResult.location" :column="1" border size="small">
                <el-descriptions-item label="仓库">{{ getWarehouseName(locationResult.location.warehouseId) }}</el-descriptions-item>
                <el-descriptions-item label="库位编码">{{ locationResult.location.locationCode }}</el-descriptions-item>
                <el-descriptions-item label="库位名称">{{ locationResult.location.locationName }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="8" class="equal-col">
            <el-card shadow="never" class="equal-card">
              <template #header>实时库存查询（支持批次过滤）</template>
              <el-form :model="stockScanForm" label-width="70px">
                <el-form-item label="条码">
                  <el-input v-model="stockScanForm.barCode" placeholder="扫描商品条码" clearable @keyup.enter="handleStockScan" />
                </el-form-item>
                <el-form-item label="仓库">
                  <el-select v-model="stockScanForm.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
                    <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                  </el-select>
                </el-form-item>
                <el-form-item label="库位码">
                  <el-input v-model="stockScanForm.locationCode" placeholder="可选" clearable @keyup.enter="handleStockScan" />
                </el-form-item>
                <el-form-item label="批次号">
                  <el-input v-model="stockScanForm.batchNo" placeholder="可选" clearable @keyup.enter="handleStockScan" />
                </el-form-item>
                <el-form-item><el-button type="primary" @click="handleStockScan">查询库存</el-button></el-form-item>
              </el-form>
              <el-alert v-if="stockResult.totalQty !== undefined" :title="`总库存：${stockResult.totalQty} | 可用：${stockResult.availableQty || 0}`" type="success" :closable="false" />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 2. 扫码快速入库建单 -->
      <el-tab-pane label="扫码快速入库" name="quickInbound">
        <el-form :model="quickInboundForm" label-width="85px" inline>
          <el-form-item label="入库仓库" required>
            <el-select v-model="quickInboundForm.warehouseId" placeholder="请选择仓库" filterable style="width: 170px">
              <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="供应商">
            <el-select v-model="quickInboundForm.supplierId" placeholder="可选" clearable filterable style="width: 180px">
              <el-option v-for="s in activeSupplierOptions" :key="s.supplierId" :label="`${s.supplierCode || ''} - ${s.supplierName}`" :value="s.supplierId" />
            </el-select>
          </el-form-item>
          <el-form-item label="库位编码">
            <el-input v-model="quickInboundForm.locationCode" placeholder="可选" style="width: 115px" />
          </el-form-item>
          <el-form-item label="批次号">
            <el-input v-model="quickInboundForm.batchNo" placeholder="可选" style="width: 115px" />
          </el-form-item>
          <el-form-item label="生产日期">
            <el-date-picker v-model="quickInboundForm.productionDate" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 135px" />
          </el-form-item>
          <el-form-item label="有效期至">
            <el-date-picker v-model="quickInboundForm.expiryDate" type="date" value-format="YYYY-MM-DD" placeholder="空自动推算" style="width: 135px" />
          </el-form-item>
          <el-form-item label="条码(回车)">
            <el-input v-model="quickInboundForm.barCode" placeholder="连续扫描商品/箱码回车" style="width: 200px" @keyup.enter="appendQuickInboundItem" />
          </el-form-item>
          <el-form-item label="数量">
            <el-input-number v-model="quickInboundForm.quantity" :min="1" controls-position="right" style="width: 95px" />
          </el-form-item>
          <el-form-item label="SN码">
            <el-input v-model="quickInboundForm.snCodes" placeholder="启用SN商品必填(逗号分隔)" style="width: 195px" />
          </el-form-item>
          <el-form-item label="一键过账">
            <el-switch v-model="quickInboundForm.autoAudit" active-text="建单并审核" inactive-text="仅存草稿" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="appendQuickInboundItem">加入清单</el-button>
            <el-button type="primary" @click="submitQuickInbound">{{ quickInboundForm.autoAudit ? '建单并自动审核' : '生成草稿入库单' }}</el-button>
            <el-button @click="quickInboundList = []">清空</el-button>
          </el-form-item>
        </el-form>
        <el-table v-if="quickInboundList.length" :data="quickInboundList" border size="small" style="margin-top: 10px">
          <el-table-column label="条码" prop="barCode" align="center" width="130" />
          <el-table-column label="商品名称" prop="productName" align="center" min-width="140" />
          <el-table-column label="单位" prop="unitName" align="center" width="80" />
          <el-table-column label="批次号" prop="batchNo" align="center" width="100" />
          <el-table-column label="有效期" prop="expiryDate" align="center" width="105" />
          <el-table-column label="数量" prop="quantity" align="right" width="80" />
          <el-table-column label="单价(按单位)" prop="price" align="right" width="105" />
          <el-table-column label="SN码" prop="snCodes" min-width="130" show-overflow-tooltip />
          <el-table-column label="操作" width="75" align="center">
            <template #default="scope"><el-button link type="danger" @click="quickInboundList.splice(scope.$index, 1)">移除</el-button></template>
          </el-table-column>
        </el-table>
        <el-alert v-if="quickInboundResult.inbound" :title="`已生成入库单：${quickInboundResult.inbound.inboundNo}，总金额 ￥${quickInboundResult.inbound.totalAmount}${quickInboundResult.autoAudited ? '（已自动审核过账）' : '（草稿）'}`" type="success" style="margin-top: 12px" show-icon />
      </el-tab-pane>

      <!-- 3. 扫码出库复核 -->
      <el-tab-pane label="出库扫码复核" name="outboundVerify">
        <el-form :model="verifyForm" label-width="108px" inline>
          <el-form-item label="待核出库单" required>
            <el-select v-model="verifyForm.outboundId" placeholder="请选择待复核出库单" filterable style="width: 300px">
              <el-option v-for="ob in pendingVerifyOutbounds" :key="ob.outboundId" :label="`${ob.outboundNo} | ${getWarehouseName(ob.warehouseId)} | 数量:${ob.totalQty}`" :value="ob.outboundId" />
            </el-select>
          </el-form-item>
          <el-form-item label="条码(回车)">
            <el-input v-model="verifyForm.barCode" placeholder="扫描货品/箱条码回车累加" style="width: 210px" @keyup.enter="appendVerifyItem" />
          </el-form-item>
          <el-form-item label="本次数量">
            <el-input-number v-model="verifyForm.quantity" :min="1" controls-position="right" style="width: 100px" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="appendVerifyItem">记入复核</el-button>
            <el-button type="primary" @click="submitOutboundVerify">提交核验并落库</el-button>
            <el-button @click="verifyScanList = []; verifyResult = null">重置</el-button>
          </el-form-item>
        </el-form>
        <el-table v-if="verifyScanList.length" :data="verifyScanList" border size="small" style="margin-top: 10px">
          <el-table-column label="条码" prop="barCode" align="center" />
          <el-table-column label="商品名称" prop="productName" align="center" />
          <el-table-column label="扫码单位" prop="unitName" align="center" width="90" />
          <el-table-column label="已扫复核数量" prop="quantity" align="center" />
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope"><el-button link type="danger" @click="verifyScanList.splice(scope.$index, 1)">移除</el-button></template>
          </el-table-column>
        </el-table>
        <div v-if="verifyResult" style="margin-top: 14px">
          <el-alert :title="verifyResult.passed ? `复核通过并已持久化标记！出库单 ${verifyResult.outboundNo} 数量完全一致` : `复核存在差异！请检查以下红色差异项`" :type="verifyResult.passed ? 'success' : 'error'" :closable="false" />
          <el-table :data="verifyResult.details || []" border size="small" style="margin-top: 8px">
            <el-table-column label="商品" prop="productName" />
            <el-table-column label="条码" prop="barCode" align="center" />
            <el-table-column label="应出基准数" prop="expectedQty" align="right" />
            <el-table-column label="实扫基准数" prop="scannedQty" align="right" />
            <el-table-column label="差异" prop="diffQty" align="right" />
            <el-table-column label="结果" align="center" width="90">
              <template #default="scope">
                <el-tag :type="scope.row.matched ? 'success' : 'danger'">{{ scope.row.matched ? '一致' : '差异' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 4. 扫码快速开单 -->
      <el-tab-pane label="扫码快速开单" name="quickSale">
        <el-form ref="quickSaleRef" :model="quickSaleForm" :rules="rules" label-width="85px" inline>
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="quickSaleForm.customerId" placeholder="请选择客户" filterable style="width: 190px">
              <el-option v-for="c in activeCustomerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
            </el-select>
          </el-form-item>
          <el-form-item label="仓库" prop="warehouseId">
            <el-select v-model="quickSaleForm.warehouseId" placeholder="请选择仓库" filterable style="width: 170px">
              <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="条码(回车)" prop="barCode">
            <el-input v-model="quickSaleForm.barCode" placeholder="扫描商品/箱码回车累加" style="width: 200px" @keyup.enter="handleAppendQuickSaleItem" />
          </el-form-item>
          <el-form-item label="数量">
            <el-input-number v-model="quickSaleForm.quantity" controls-position="right" :min="1" style="width: 100px" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="handleAppendQuickSaleItem">加入清单</el-button>
            <el-button type="primary" @click="handleQuickSale">立即开单</el-button>
            <el-button @click="quickSaleItemList = []">清空</el-button>
          </el-form-item>
        </el-form>
        <el-table v-if="quickSaleItemList.length" :data="quickSaleItemList" border size="small" style="margin-top: 10px">
          <el-table-column label="条码" prop="barCode" align="center" />
          <el-table-column label="商品" prop="productName" align="center" />
          <el-table-column label="单位" prop="unitName" align="center" width="85" />
          <el-table-column label="数量" prop="quantity" align="right" width="85" />
          <el-table-column label="参考单价" prop="salePrice" align="right" width="95" />
          <el-table-column label="金额" align="right" width="100">
            <template #default="scope">{{ (Number(scope.row.quantity || 0) * Number(scope.row.salePrice || 0)).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope"><el-button link type="danger" @click="quickSaleItemList.splice(scope.$index, 1)">移除</el-button></template>
          </el-table-column>
        </el-table>
        <el-alert v-if="quickSaleResult.orderNo" :title="`开单成功！销货订单号：${quickSaleResult.orderNo}，总额 ￥${quickSaleResult.totalAmount}`" type="success" style="margin-top: 12px" show-icon />
      </el-tab-pane>

      <!-- 5. 扫码盘点与调拨 -->
      <el-tab-pane label="扫码盘点/调拨" name="checkTransfer">
        <el-row :gutter="16" class="equal-row">
          <el-col :xs="24" :lg="12" class="equal-col">
            <el-card shadow="never" class="equal-card">
              <template #header>扫码盘点建单（支持批次）</template>
              <el-form :model="inventoryCheckForm" label-width="86px" inline>
                <el-form-item label="仓库" required>
                  <el-select v-model="inventoryCheckForm.warehouseId" placeholder="请选择仓库" filterable style="width: 155px">
                    <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                  </el-select>
                </el-form-item>
                <el-form-item label="库位码" required>
                  <el-input v-model="inventoryCheckForm.locationCode" placeholder="请输入库位码" style="width: 120px" />
                </el-form-item>
                <el-form-item label="批次号">
                  <el-input v-model="inventoryCheckForm.batchNo" placeholder="选填" style="width: 115px" />
                </el-form-item>
                <el-form-item label="条码(回车)">
                  <el-input v-model="inventoryCheckForm.barCode" placeholder="扫码回车" style="width: 150px" @keyup.enter="handleAppendInventoryCheckItem" />
                </el-form-item>
                <el-form-item label="实盘数">
                  <el-input-number v-model="inventoryCheckForm.actualQty" :min="0" controls-position="right" style="width: 95px" />
                </el-form-item>
                <el-form-item>
                  <el-button type="success" @click="handleAppendInventoryCheckItem">加入</el-button>
                  <el-button type="primary" @click="handleInventoryCheck">生成盘点单</el-button>
                  <el-button @click="inventoryCheckItemList = []; inventoryCheckResult = {}">清空</el-button>
                </el-form-item>
              </el-form>
              <el-table v-if="inventoryCheckItemList.length" :data="inventoryCheckItemList" border size="small">
                <el-table-column label="商品" prop="productName" />
                <el-table-column label="批次" prop="batchNo" width="85" />
                <el-table-column label="账面" prop="stockQty" width="70" align="right" />
                <el-table-column label="实盘" prop="actualQty" width="70" align="right" />
                <el-table-column label="差异" prop="diffQty" width="70" align="right" />
                <el-table-column label="操作" width="60" align="center">
                  <template #default="scope"><el-button link type="danger" @click="inventoryCheckItemList.splice(scope.$index, 1)">删</el-button></template>
                </el-table-column>
              </el-table>
              <el-alert v-if="inventoryCheckResult.checkNo" :title="`已生成盘点单：${inventoryCheckResult.checkNo}`" type="success" style="margin-top: 10px" />
            </el-card>
          </el-col>

          <el-col :xs="24" :lg="12" class="equal-col">
            <el-card shadow="never" class="equal-card">
              <template #header>扫码调拨建单（支持批次）</template>
              <el-form :model="transferForm" label-width="86px" inline>
                <el-form-item label="调出仓" required>
                  <el-select v-model="transferForm.outWarehouseId" placeholder="请选择调出仓" filterable style="width: 145px">
                    <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                  </el-select>
                </el-form-item>
                <el-form-item label="调入仓" required>
                  <el-select v-model="transferForm.inWarehouseId" placeholder="请选择调入仓" filterable style="width: 145px">
                    <el-option v-for="w in activeWarehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                  </el-select>
                </el-form-item>
                <el-form-item label="出库位码" required>
                  <el-input v-model="transferForm.outLocationCode" placeholder="请输入调出库位" style="width: 120px" />
                </el-form-item>
                <el-form-item label="入库位码" required>
                  <el-input v-model="transferForm.inLocationCode" placeholder="请输入调入库位" style="width: 120px" />
                </el-form-item>
                <el-form-item label="批次号">
                  <el-input v-model="transferForm.batchNo" placeholder="选填" style="width: 110px" />
                </el-form-item>
                <el-form-item label="条码(回车)">
                  <el-input v-model="transferForm.barCode" placeholder="扫码回车" style="width: 150px" @keyup.enter="handleAppendTransferItem" />
                </el-form-item>
                <el-form-item label="数量">
                  <el-input-number v-model="transferForm.quantity" :min="1" controls-position="right" style="width: 95px" />
                </el-form-item>
                <el-form-item>
                  <el-button type="success" @click="handleAppendTransferItem">加入</el-button>
                  <el-button type="primary" @click="handleTransfer">生成调拨单</el-button>
                  <el-button @click="transferItemList = []; transferResult = {}">清空</el-button>
                </el-form-item>
              </el-form>
              <el-table v-if="transferItemList.length" :data="transferItemList" border size="small">
                <el-table-column label="商品" prop="productName" />
                <el-table-column label="批次" prop="batchNo" width="85" />
                <el-table-column label="可用" prop="availableQty" width="70" align="right" />
                <el-table-column label="调拨数" prop="quantity" width="75" align="right" />
                <el-table-column label="操作" width="60" align="center">
                  <template #default="scope"><el-button link type="danger" @click="transferItemList.splice(scope.$index, 1)">删</el-button></template>
                </el-table-column>
              </el-table>
              <el-alert v-if="transferResult.transferNo" :title="`已生成调拨单：${transferResult.transferNo}`" type="success" style="margin-top: 10px" />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="Scan">
import { scanProduct, scanLocation, scanStock, quickSaleByScan, quickInboundByScan, outboundVerifyByScan, inventoryCheckByScan, transferByScan } from "@/api/business/scan"
import { listOutbound } from "@/api/business/outbound"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const {
  activeWarehouseOptions, activeCustomerOptions, activeSupplierOptions,
  loadWarehouses, loadCustomers, loadSuppliers, getWarehouseName
} = useBusinessOptions()

loadWarehouses(); loadCustomers(); loadSuppliers()

const activeTab = ref("lookup")
const outboundOptions = ref([])
function loadPendingVerifyOutbounds() {
  listOutbound({ pageNum: 1, pageSize: 200 }).then(res => { outboundOptions.value = res.rows || [] }).catch(() => {})
}
loadPendingVerifyOutbounds()

const pendingVerifyOutbounds = computed(() =>
  outboundOptions.value.filter(ob => String(ob.isReversal || "0") !== "1" && String(ob.reversedFlag || "0") !== "1" && String(ob.deliveryStatus || "0") === "0" && String(ob.verifiedFlag || "0") !== "1")
)

const productScanForm = reactive({ barCode: "" })
const locationScanForm = reactive({ locationCode: "" })
const stockScanForm = reactive({ barCode: "", warehouseId: undefined, locationCode: "", batchNo: "" })
const quickSaleForm = reactive({ customerId: undefined, warehouseId: undefined, barCode: "", quantity: 1, remark: "" })
const quickInboundForm = reactive({ warehouseId: undefined, supplierId: undefined, locationCode: "", batchNo: "", productionDate: undefined, expiryDate: undefined, snCodes: "", autoAudit: false, barCode: "", quantity: 1, remark: "" })
const verifyForm = reactive({ outboundId: undefined, barCode: "", quantity: 1 })
const inventoryCheckForm = reactive({ warehouseId: undefined, locationCode: "", batchNo: "", barCode: "", actualQty: 1, remark: "" })
const transferForm = reactive({ outWarehouseId: undefined, inWarehouseId: undefined, outLocationCode: "", inLocationCode: "", batchNo: "", barCode: "", quantity: 1, remark: "" })

const productResult = ref({})
const resolvedScanUnit = computed(() => productResult.value.matchedUnit || productResult.value.unit || null)
const locationResult = ref({})
const stockResult = ref({})
const quickSaleResult = ref({})
const quickInboundResult = ref({})
const verifyResult = ref(null)
const inventoryCheckResult = ref({})
const transferResult = ref({})

const quickSaleItemList = ref([])
const quickInboundList = ref([])
const verifyScanList = ref([])
const inventoryCheckItemList = ref([])
const transferItemList = ref([])

const rules = {
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  warehouseId: [{ required: true, message: "请选择仓库", trigger: "change" }]
}

function handleProductScan() {
  const code = String(productScanForm.barCode || "").trim()
  if (!code) { proxy.$modal.msgWarning("请输入或扫描条码"); return }
  scanProduct(code).then(res => { productResult.value = res.data || {} })
}

function handleLocationScan() {
  const code = String(locationScanForm.locationCode || "").trim()
  if (!code) { proxy.$modal.msgWarning("请输入或扫描库位码"); return }
  scanLocation(code).then(res => { locationResult.value = res.data || {} })
}

function handleStockScan() {
  if (!String(stockScanForm.barCode || "").trim() || !stockScanForm.warehouseId) {
    proxy.$modal.msgWarning("请输入商品条码并选择仓库"); return
  }
  scanStock(stockScanForm).then(res => { stockResult.value = res.data || {} })
}

function appendQuickInboundItem() {
  const code = String(quickInboundForm.barCode || "").trim()
  if (!code) { proxy.$modal.msgWarning("请扫描条码"); return }
  scanProduct(code).then(res => {
    const p = res.data?.product
    const unit = res.data?.matchedUnit || res.data?.unit
    if (!p) { proxy.$modal.msgError("未找到商品"); return }
    const ratio = Number(unit?.convertRatio || 1)
    const unitName = unit?.unitName || p.baseUnit || p.unitName || "件"
    const unitPrice = unit && Number(unit.purchasePrice) > 0 ? Number(unit.purchasePrice) : Number((Number(p.costPrice || 0) * ratio).toFixed(2))
    const batchNo = String(quickInboundForm.batchNo || "").trim()
    const existing = quickInboundList.value.find(i => i.barCode === code && i.batchNo === batchNo && !quickInboundForm.snCodes)
    if (existing) {
      existing.quantity = Number(existing.quantity) + Number(quickInboundForm.quantity || 1)
    } else {
      quickInboundList.value.push({
        barCode: code, productName: p.productName, unitName, convertRatio: ratio,
        batchNo, productionDate: quickInboundForm.productionDate, expiryDate: quickInboundForm.expiryDate,
        snCodes: quickInboundForm.snCodes, quantity: Number(quickInboundForm.quantity || 1), price: unitPrice
      })
    }
    quickInboundForm.barCode = ""
    quickInboundForm.snCodes = ""
    quickInboundForm.quantity = 1
  })
}

function submitQuickInbound() {
  if (!quickInboundForm.warehouseId || !quickInboundList.value.length) {
    proxy.$modal.msgError("请选择入库仓库并扫描至少一条明细"); return
  }
  quickInboundByScan({
    warehouseId: quickInboundForm.warehouseId,
    supplierId: quickInboundForm.supplierId,
    locationCode: quickInboundForm.locationCode,
    autoAudit: quickInboundForm.autoAudit,
    remark: quickInboundForm.remark || "扫码快速入库",
    items: quickInboundList.value
  }).then(res => {
    quickInboundResult.value = res.data || {}
    quickInboundList.value = []
    proxy.$modal.msgSuccess(quickInboundForm.autoAudit ? "扫码入库单生成并审核成功" : "扫码快速入库草稿单生成成功")
  })
}

function appendVerifyItem() {
  const code = String(verifyForm.barCode || "").trim()
  if (!code) { proxy.$modal.msgWarning("请扫描商品条码"); return }
  scanProduct(code).then(res => {
    const p = res.data?.product
    const unit = res.data?.matchedUnit || res.data?.unit
    if (!p) { proxy.$modal.msgError("未找到对应条码商品"); return }
    const unitName = unit?.unitName || p.baseUnit || p.unitName || "件"
    const existing = verifyScanList.value.find(i => i.barCode === code)
    if (existing) {
      existing.quantity = Number(existing.quantity) + Number(verifyForm.quantity || 1)
    } else {
      verifyScanList.value.push({ barCode: code, productId: p.productId, productName: p.productName, unitName, quantity: Number(verifyForm.quantity || 1) })
    }
    verifyForm.barCode = ""
    verifyForm.quantity = 1
  })
}

function submitOutboundVerify() {
  if (!verifyForm.outboundId || !verifyScanList.value.length) {
    proxy.$modal.msgError("请选择待核验出库单并扫入复核明细"); return
  }
  outboundVerifyByScan(verifyForm.outboundId, verifyScanList.value).then(res => {
    verifyResult.value = res.data || {}
    if (verifyResult.value.passed) {
      proxy.$modal.msgSuccess("复核完全通过，已标记复核状态！")
      loadPendingVerifyOutbounds()
    } else {
      proxy.$modal.msgWarning("复核发现差异，请核对明细")
    }
  })
}

function handleAppendQuickSaleItem() {
  const code = String(quickSaleForm.barCode || "").trim()
  if (!code) { proxy.$modal.msgWarning("请扫描商品条码"); return }
  scanProduct(code).then(res => {
    const p = res.data?.product
    const unit = res.data?.matchedUnit || res.data?.unit
    if (!p) { proxy.$modal.msgError("未找到扫码商品"); return }
    const ratio = Number(unit?.convertRatio || 1)
    const unitName = unit?.unitName || p.baseUnit || p.unitName || "件"
    const salePrice = unit && Number(unit.salePrice) > 0 ? Number(unit.salePrice) : Number((Number(p.salePrice || 0) * ratio).toFixed(2))
    const existing = quickSaleItemList.value.find(i => i.barCode === code)
    if (existing) existing.quantity = Number(existing.quantity) + Number(quickSaleForm.quantity || 1)
    else quickSaleItemList.value.push({ barCode: code, productId: p.productId, productCode: p.productCode, productName: p.productName, unitName, quantity: Number(quickSaleForm.quantity || 1), salePrice })
    quickSaleForm.barCode = ""
    quickSaleForm.quantity = 1
  })
}

function handleQuickSale() {
  if (!quickSaleForm.customerId || !quickSaleForm.warehouseId || (!quickSaleItemList.value.length && !quickSaleForm.barCode)) {
    proxy.$modal.msgError("请选择客户、仓库并扫入商品"); return
  }
  quickSaleByScan({ ...quickSaleForm, quickSaleItemList: quickSaleItemList.value }).then(res => {
    quickSaleResult.value = res.data || {}
    quickSaleItemList.value = []
    proxy.$modal.msgSuccess("扫码快速开单成功")
  })
}

function handleAppendInventoryCheckItem() {
  if (!inventoryCheckForm.warehouseId || !inventoryCheckForm.locationCode || !inventoryCheckForm.barCode) {
    proxy.$modal.msgError("请填写仓库、库位码和条码"); return
  }
  Promise.all([
    scanProduct(inventoryCheckForm.barCode),
    scanStock({ barCode: inventoryCheckForm.barCode, warehouseId: inventoryCheckForm.warehouseId, locationCode: inventoryCheckForm.locationCode, batchNo: inventoryCheckForm.batchNo })
  ]).then(([pRes, sRes]) => {
    const p = pRes.data?.product
    const stockQty = Number(sRes.data?.totalQty || 0)
    if (!p) { proxy.$modal.msgError("未找到扫码商品"); return }
    const batchNo = String(inventoryCheckForm.batchNo || "").trim()
    const existing = inventoryCheckItemList.value.find(i => i.barCode === inventoryCheckForm.barCode && i.batchNo === batchNo)
    if (existing) {
      existing.actualQty = Number(existing.actualQty) + Number(inventoryCheckForm.actualQty || 0)
      existing.diffQty = existing.actualQty - existing.stockQty
    } else {
      inventoryCheckItemList.value.push({
        barCode: inventoryCheckForm.barCode, batchNo, productId: p.productId, productName: p.productName,
        stockQty, actualQty: Number(inventoryCheckForm.actualQty || 0), diffQty: Number(inventoryCheckForm.actualQty || 0) - stockQty
      })
    }
    inventoryCheckForm.barCode = ""
    inventoryCheckForm.actualQty = 1
  })
}

function handleInventoryCheck() {
  if (!inventoryCheckItemList.value.length) { proxy.$modal.msgError("请先扫入盘点明细"); return }
  inventoryCheckByScan({
    warehouseId: inventoryCheckForm.warehouseId, locationCode: inventoryCheckForm.locationCode,
    remark: inventoryCheckForm.remark, inventoryCheckItemList: inventoryCheckItemList.value
  }).then(res => {
    inventoryCheckResult.value = res.data || {}
    inventoryCheckItemList.value = []
    proxy.$modal.msgSuccess("扫码盘点单生成成功")
  })
}

function handleAppendTransferItem() {
  if (!transferForm.outWarehouseId || !transferForm.inWarehouseId || !transferForm.outLocationCode || !transferForm.inLocationCode || !transferForm.barCode) {
    proxy.$modal.msgError("请填写调出/调入仓库、库位码和条码"); return
  }
  Promise.all([
    scanProduct(transferForm.barCode),
    scanStock({ barCode: transferForm.barCode, warehouseId: transferForm.outWarehouseId, locationCode: transferForm.outLocationCode, batchNo: transferForm.batchNo })
  ]).then(([pRes, sRes]) => {
    const p = pRes.data?.product
    const availableQty = Number(sRes.data?.availableQty || 0)
    if (!p) { proxy.$modal.msgError("未找到扫码商品"); return }
    const batchNo = String(transferForm.batchNo || "").trim()
    const existing = transferItemList.value.find(i => i.barCode === transferForm.barCode && i.batchNo === batchNo)
    const nextQty = Number(existing?.quantity || 0) + Number(transferForm.quantity || 1)
    if (nextQty > availableQty) { proxy.$modal.msgError("调出库位可用库存不足"); return }
    if (existing) existing.quantity = nextQty
    else transferItemList.value.push({ barCode: transferForm.barCode, batchNo, productId: p.productId, productName: p.productName, availableQty, quantity: nextQty, costPrice: Number(p.costPrice || 0) })
    transferForm.barCode = ""
    transferForm.quantity = 1
  })
}

function handleTransfer() {
  if (!transferItemList.value.length) { proxy.$modal.msgError("请先扫入调拨明细"); return }
  transferByScan({
    outWarehouseId: transferForm.outWarehouseId, inWarehouseId: transferForm.inWarehouseId,
    outLocationCode: transferForm.outLocationCode, inLocationCode: transferForm.inLocationCode,
    remark: transferForm.remark, transferItemList: transferItemList.value
  }).then(res => {
    transferResult.value = res.data || {}
    transferItemList.value = []
    proxy.$modal.msgSuccess("扫码调拨单生成成功")
  })
}
</script>

<style scoped>
.equal-row {
  display: flex;
  flex-wrap: wrap;
}
.equal-col {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.equal-card {
  flex: 1;
}
@media (max-width: 768px) {
  .scan-pda-container :deep(.el-form--inline .el-form-item) {
    display: flex;
    width: 100%;
    margin-right: 0;
  }
  .scan-pda-container :deep(.el-input),
  .scan-pda-container :deep(.el-select) {
    width: 100% !important;
  }
}
</style>
