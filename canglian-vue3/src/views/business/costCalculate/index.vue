<template>
  <div class="app-container">
    <el-card shadow="never" style="margin-bottom: 16px">
      <template #header>
        <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px">
          <span style="font-weight: 600">全局库存成本核算算法配置</span>
          <el-tag :type="costConfig.costMethod === 'fifo' ? 'warning' : 'primary'">
            当前算法：{{ costConfig.costMethodLabel || '移动加权平均' }}
          </el-tag>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item label="默认成本算法">
          <el-select v-model="selectedMethod" style="width: 220px" :disabled="!costConfig.canSwitch">
            <el-option label="移动加权平均" value="moving" />
            <el-option label="先进先出" value="fifo" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :disabled="!costConfig.canSwitch || selectedMethod === costConfig.costMethod"
            :loading="savingConfig"
            @click="handleSaveConfig"
            v-hasPermi="['business:costCalculate:calculate']"
          >保存算法</el-button>
        </el-form-item>
        <el-form-item>
          <span style="color: #909399; font-size: 13px">
            历史成本流水：{{ costConfig.historyLogCount ?? 0 }} 条
            <template v-if="!costConfig.canSwitch">（已存在历史流水，系统自动锁定跨算法切换以保障账实一致）</template>
          </span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="期末未结账成本重算" name="recalculate">
        <el-alert
          title="按库存流水时间顺序重演所选仓库/商品的成本账簿与 FIFO 成本层，用于修正补录单据导致的均价偏差（仅限未结账会计期间）。"
          type="info"
          show-icon
          :closable="false"
          style="margin-bottom: 14px"
        />
        <el-form :inline="true" :model="recalcForm">
          <el-form-item label="仓库（选填）">
            <el-select v-model="recalcForm.warehouseId" placeholder="全部仓库" clearable filterable style="width: 200px">
              <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品（选填）">
            <el-select v-model="recalcForm.productId" placeholder="全部商品" clearable filterable style="width: 220px">
              <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              icon="Refresh"
              :loading="recalculating"
              @click="handleRecalculate"
              v-hasPermi="['business:costCalculate:recalculate']"
            >执行期末成本重算</el-button>
          </el-form-item>
        </el-form>

        <el-card v-if="recalcResult" shadow="never" style="margin-top: 12px">
          <template #header>
            <span>重算结果：共重算 {{ recalcResult.recalculatedAccounts }} 个成本账户，回放 {{ recalcResult.replayedLogs }} 条库存流水</span>
          </template>
          <el-table :data="recalcResult.details || []" border size="small">
            <el-table-column label="仓库" align="center">
              <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
            </el-table-column>
            <el-table-column label="商品" align="center">
              <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
            </el-table-column>
            <el-table-column label="重算前均价" align="center" prop="beforeAvgCost" />
            <el-table-column label="重算后数量" align="center" prop="afterTotalQty" />
            <el-table-column label="重算后总金额" align="center" prop="afterTotalAmount" />
            <el-table-column label="重算后均价" align="center" prop="afterAvgCost" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="受控成本调价" name="adjust">
        <el-alert
          title="当发生期初成本修正或运费分摊需调整结存成本均价时使用，系统将写入不可篡改的成本调价流水与凭证事件。"
          type="warning"
          show-icon
          :closable="false"
          style="margin-bottom: 14px"
        />
        <el-form ref="adjustRef" :model="adjustForm" :rules="adjustRules" label-width="128px" style="max-width: 680px">
          <el-form-item label="仓库" prop="warehouseId">
            <el-select v-model="adjustForm.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
              <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品" prop="productId">
            <el-select v-model="adjustForm.productId" placeholder="请选择商品" filterable style="width: 100%">
              <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
            </el-select>
          </el-form-item>
          <el-form-item label="调整后单位成本" prop="newAvgCost">
            <el-input v-model="adjustForm.newAvgCost" placeholder="请输入调整后的单位成本（>=0）" />
          </el-form-item>
          <el-form-item label="调价原因" prop="reason">
            <el-input v-model="adjustForm.reason" type="textarea" :rows="3" placeholder="请写明调价依据（必填，计入审计流水）" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="adjusting" @click="submitAdjust" v-hasPermi="['business:costCalculate:adjust']">提交受控调价</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="单笔成本核算" name="single">
        <el-form ref="calcRef" :model="form" :rules="rules" label-width="90px" style="max-width: 760px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="商品" prop="productId">
                <el-select v-model="form.productId" placeholder="请选择商品" filterable style="width: 100%">
                  <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="仓库" prop="warehouseId">
                <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
                  <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="单据类型" prop="billType">
                <el-select v-model="form.billType" placeholder="请选择单据类型" allow-create filterable style="width: 100%">
                  <el-option label="采购入库" value="inbound" />
                  <el-option label="销售出库" value="outbound" />
                  <el-option label="手工模拟" value="manual" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="来源单据" prop="billId">
                <el-select v-model="form.billId" placeholder="可选，自动关联单据" clearable filterable allow-create style="width: 100%">
                  <el-option label="手工模拟测算(0)" :value="0" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="出入库" prop="inOut">
                <el-select v-model="form.inOut" placeholder="请选择" style="width: 100%">
                  <el-option label="入库" value="1" />
                  <el-option label="出库" value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="数量" prop="quantity">
                <el-input v-model="form.quantity" placeholder="请输入数量" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="单价" prop="price">
                <el-input v-model="form.price" placeholder="请输入单价" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="成本算法" prop="costMethod">
                <el-select v-model="form.costMethod" placeholder="默认跟随全局配置" clearable style="width: 100%">
                  <el-option label="移动加权" value="moving" />
                  <el-option label="先进先出" value="fifo" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <div style="margin-top: 10px">
            <el-button type="primary" @click="submitForm">核算</el-button>
            <el-button @click="reset">重置</el-button>
          </div>
        </el-form>

        <el-card v-if="result" style="margin-top: 16px; max-width: 760px">
          <template #header><span>核算结果</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="成本账户">{{ result.costAccountId }}</el-descriptions-item>
            <el-descriptions-item label="出入库">{{ result.inOut === '1' ? '入库' : '出库' }}</el-descriptions-item>
            <el-descriptions-item label="数量">{{ result.quantity }}</el-descriptions-item>
            <el-descriptions-item label="单价">{{ result.price }}</el-descriptions-item>
            <el-descriptions-item label="金额">{{ result.amount }}</el-descriptions-item>
            <el-descriptions-item label="算法">{{ result.costMethod === 'fifo' ? '先进先出' : '移动加权' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="CostCalculate">
import { getCostConfig, updateCostConfig, calculateCost, recalculateCost, adjustCost } from "@/api/business/costCalculate"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { productOptions, warehouseOptions, loadProducts, loadWarehouses, getProductName, getWarehouseName } = useBusinessOptions()

loadProducts()
loadWarehouses()

const activeTab = ref("recalculate")
const costConfig = ref({ costMethod: "moving", costMethodLabel: "移动加权平均", historyLogCount: 0, canSwitch: false })
const selectedMethod = ref("moving")
const savingConfig = ref(false)

const recalcForm = reactive({ warehouseId: undefined, productId: undefined })
const recalculating = ref(false)
const recalcResult = ref(null)

const adjusting = ref(false)
const adjustForm = reactive({ warehouseId: undefined, productId: undefined, newAvgCost: undefined, reason: "" })
const adjustRules = {
  warehouseId: [{ required: true, message: "请选择仓库", trigger: "change" }],
  productId: [{ required: true, message: "请选择商品", trigger: "change" }],
  newAvgCost: [{ required: true, message: "请输入调整后单位成本", trigger: "blur" }],
  reason: [{ required: true, message: "请填写调价原因", trigger: "blur" }]
}

const result = ref(null)
const data = reactive({
  form: {
    productId: undefined,
    warehouseId: undefined,
    billType: undefined,
    billId: undefined,
    inOut: "1",
    quantity: undefined,
    price: undefined,
    costMethod: undefined
  },
  rules: {
    productId: [{ required: true, message: "请选择商品", trigger: "change" }],
    warehouseId: [{ required: true, message: "请选择仓库", trigger: "change" }],
    inOut: [{ required: true, message: "请选择出入库方向", trigger: "change" }],
    quantity: [{ required: true, message: "请输入大于0的数量", trigger: "blur" }]
  }
})
const { form, rules } = toRefs(data)

function loadCostConfig() {
  getCostConfig().then(res => {
    costConfig.value = res.data || {}
    selectedMethod.value = costConfig.value.costMethod || "moving"
  }).catch(() => {})
}

function handleSaveConfig() {
  savingConfig.value = true
  updateCostConfig({ costMethod: selectedMethod.value }).then(res => {
    costConfig.value = res.data || {}
    proxy.$modal.msgSuccess("全局成本算法配置已保存")
  }).finally(() => {
    savingConfig.value = false
  })
}

function handleRecalculate() {
  proxy.$modal.confirm("确认按库存流水时间序重算所选范围的库存成本台账与成本流水吗？").then(() => {
    recalculating.value = true
    return recalculateCost({ warehouseId: recalcForm.warehouseId, productId: recalcForm.productId })
  }).then(res => {
    recalcResult.value = res.data || {}
    proxy.$modal.msgSuccess(`成本重算完成：更新 ${recalcResult.value.recalculatedAccounts || 0} 个账户`)
    loadCostConfig()
  }).catch(() => {}).finally(() => {
    recalculating.value = false
  })
}

function submitAdjust() {
  proxy.$refs["adjustRef"].validate(valid => {
    if (!valid) return
    adjusting.value = true
    adjustCost(adjustForm).then(() => {
      proxy.$modal.msgSuccess("受控成本调价成功，已写入成本流水与凭证事件")
      adjustForm.newAvgCost = undefined
      adjustForm.reason = ""
      loadCostConfig()
    }).finally(() => {
      adjusting.value = false
    })
  })
}

function submitForm() {
  proxy.$refs["calcRef"].validate(valid => {
    if (valid) {
      calculateCost(form.value).then(response => {
        result.value = response.data
        proxy.$modal.msgSuccess("核算成功")
        loadCostConfig()
      })
    }
  })
}

function reset() {
  form.value = {
    productId: undefined,
    warehouseId: undefined,
    billType: undefined,
    billId: undefined,
    inOut: "1",
    quantity: undefined,
    price: undefined,
    costMethod: undefined
  }
  result.value = null
  proxy.resetForm("calcRef")
}

loadCostConfig()
</script>
