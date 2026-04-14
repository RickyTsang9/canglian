<template>
  <div class="app-container">
    <el-form ref="calcRef" :model="form" :rules="rules" label-width="90px" style="max-width: 760px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="商品编号" prop="productId">
            <el-input v-model="form.productId" placeholder="请输入商品编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="仓库编号" prop="warehouseId">
            <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="单据类型" prop="billType">
            <el-input v-model="form.billType" placeholder="请输入单据类型" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单据编号" prop="billId">
            <el-input v-model="form.billId" placeholder="请输入单据编号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="出入库" prop="inOut">
            <el-select v-model="form.inOut" placeholder="请选择" style="width: 200px">
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
            <el-select v-model="form.costMethod" placeholder="请选择" style="width: 200px">
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
      <template #header>
        <div class="clearfix">
          <span>核算结果</span>
        </div>
      </template>
      <el-descriptions :column="2">
        <el-descriptions-item label="成本账户">{{ result.costAccountId }}</el-descriptions-item>
        <el-descriptions-item label="出入库">{{ result.inOut }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ result.quantity }}</el-descriptions-item>
        <el-descriptions-item label="单价">{{ result.price }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ result.amount }}</el-descriptions-item>
        <el-descriptions-item label="算法">{{ result.costMethod }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup name="CostCalculate">
import { calculateCost } from "@/api/business/costCalculate"

const { proxy } = getCurrentInstance()

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
    costMethod: "moving"
  },
  rules: {
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }],
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
    inOut: [{ required: true, message: "出入库不能为空", trigger: "change" }],
    quantity: [{ required: true, message: "数量不能为空", trigger: "blur" }]
  }
})

const { form, rules } = toRefs(data)

function submitForm() {
  proxy.$refs["calcRef"].validate(valid => {
    if (valid) {
      calculateCost(form.value).then(response => {
        result.value = response.data
        proxy.$modal.msgSuccess("核算成功")
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
    costMethod: "moving"
  }
  proxy.resetForm("calcRef")
}
</script>
