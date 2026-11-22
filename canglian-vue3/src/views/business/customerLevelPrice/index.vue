<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户等级" prop="customerLevel">
        <el-input v-model="queryParams.customerLevel" placeholder="请输入客户等级" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 220px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
          <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:customerLevelPrice:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:customerLevelPrice:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:customerLevelPrice:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="customerLevelPriceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="客户等级" align="center" prop="customerLevel" width="110">
        <template #default="scope">{{ levelLabel(scope.row.customerLevel) }}</template>
      </el-table-column>
      <el-table-column label="商品" align="center" min-width="180" show-overflow-tooltip>
        <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
      </el-table-column>
      <el-table-column label="销售单价" align="right" prop="salePrice" width="100" />
      <el-table-column label="生效日期" align="center" width="115">
        <template #default="scope">{{ scope.row.effectiveDate ? parseTime(scope.row.effectiveDate, '{y}-{m}-{d}') : '长期' }}</template>
      </el-table-column>
      <el-table-column label="失效日期" align="center" width="115">
        <template #default="scope">{{ scope.row.expireDate ? parseTime(scope.row.expireDate, '{y}-{m}-{d}') : '长期' }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope"><dict-tag :options="sys_normal_disable" :value="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:customerLevelPrice:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:customerLevelPrice:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="customerLevelPriceRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户等级" prop="customerLevel">
              <el-select v-model="form.customerLevel" placeholder="请选择或输入等级" allow-create filterable style="width: 100%">
                <el-option label="VIP" value="VIP" />
                <el-option label="一级代理" value="L1" />
                <el-option label="二级代理" value="L2" />
                <el-option label="普通客户" value="NORMAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品" prop="productId">
              <el-select v-model="form.productId" placeholder="请选择商品" filterable style="width: 100%">
                <el-option v-for="p in activeProductOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="生效日期" prop="effectiveDate">
              <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" placeholder="空表示立即生效" clearable style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效日期" prop="expireDate">
              <el-date-picker v-model="form.expireDate" type="date" value-format="YYYY-MM-DD" placeholder="空表示长期有效" clearable style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="销售单价" prop="salePrice">
              <el-input-number v-model="form.salePrice" controls-position="right" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="CustomerLevelPrice">
import { listCustomerLevelPrice, getCustomerLevelPrice, addCustomerLevelPrice, updateCustomerLevelPrice, delCustomerLevelPrice } from "@/api/business/customerLevelPrice"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")
const { productOptions, activeProductOptions, loadProducts, getProductName } = useBusinessOptions()
const LEVEL_MAP = { VIP: "VIP 客户", L1: "一级代理", L2: "二级代理", NORMAL: "普通客户" }
function levelLabel(v) { return LEVEL_MAP[v] || v || "-" }

loadProducts()

const customerLevelPriceList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, customerLevel: undefined, productId: undefined, status: undefined },
  rules: {
    customerLevel: [{ required: true, message: "客户等级不能为空", trigger: "change" }],
    productId: [{ required: true, message: "请选择商品", trigger: "change" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listCustomerLevelPrice(queryParams.value).then(res => {
    customerLevelPriceList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}
function cancel() { open.value = false; reset() }
function reset() {
  form.value = { levelPriceId: undefined, customerLevel: undefined, productId: undefined, salePrice: 0, effectiveDate: undefined, expireDate: undefined, status: "0", remark: undefined }
  proxy.resetForm("customerLevelPriceRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.levelPriceId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}
function handleAdd() { reset(); open.value = true; title.value = "新增客户等级价" }
function handleUpdate(row) {
  reset()
  getCustomerLevelPrice(row.levelPriceId || selectedIds.value).then(res => { form.value = res.data; open.value = true; title.value = "修改客户等级价" })
}
function submitForm() {
  proxy.$refs["customerLevelPriceRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.levelPriceId ? updateCustomerLevelPrice(form.value) : addCustomerLevelPrice(form.value)
    fn.then(() => { proxy.$modal.msgSuccess(form.value.levelPriceId ? "修改成功" : "新增成功"); open.value = false; getList() })
  })
}
function handleDelete(row) {
  const ids = row.levelPriceId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中客户等级价？").then(() => delCustomerLevelPrice(ids)).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {})
}
getList()
</script>
