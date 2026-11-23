<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 210px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 210px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['business:recentTransactionPrice:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="isSingleDisabled" @click="handleUpdate" v-hasPermi="['business:recentTransactionPrice:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="isMultipleDisabled" @click="handleDelete" v-hasPermi="['business:recentTransactionPrice:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recentTransactionPriceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="客户" align="center" min-width="140" show-overflow-tooltip>
        <template #default="scope">{{ getCustomerName(scope.row.customerId) }}</template>
      </el-table-column>
      <el-table-column label="商品" align="center" min-width="180" show-overflow-tooltip>
        <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
      </el-table-column>
      <el-table-column label="最近成交价(基础单位)" align="right" prop="salePrice" width="165">
        <template #default="scope">{{ Number(scope.row.salePrice || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="来源类型" align="center" prop="sourceBillType" width="130">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.sourceBillType === 'outbound' ? 'success' : 'info'">{{ sourceTypeLabel(scope.row.sourceBillType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="业务日期" align="center" width="125">
        <template #default="scope">{{ parseTime(scope.row.businessDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="145" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:recentTransactionPrice:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:recentTransactionPrice:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="recentTransactionPriceRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
                <el-option v-for="c in customerOptions" :key="c.customerId" :label="`${c.customerCode || ''} - ${c.customerName}`" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品" prop="productId">
              <el-select v-model="form.productId" placeholder="请选择商品" filterable style="width: 100%">
                <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="成交单价" prop="salePrice">
              <el-input-number v-model="form.salePrice" controls-position="right" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker v-model="form.businessDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="来源类型" prop="sourceBillType">
          <el-select v-model="form.sourceBillType" placeholder="默认手工录入" style="width: 100%">
            <el-option label="手工录入" value="manual" />
            <el-option label="销货订单" value="sale_order" />
            <el-option label="销售出库" value="outbound" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="RecentTransactionPrice">
import { listRecentTransactionPrice, getRecentTransactionPrice, addRecentTransactionPrice, updateRecentTransactionPrice, delRecentTransactionPrice } from "@/api/business/recentTransactionPrice"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const { customerOptions, productOptions, activeCustomerOptions, activeProductOptions, loadCustomers, loadProducts, getCustomerName, getProductName } = useBusinessOptions()
const SOURCE_TYPE_MAP = { manual: "手工录入", sale_order: "销货订单", outbound: "销售出库签收", sale_return: "销售退货" }
function sourceTypeLabel(v) { return SOURCE_TYPE_MAP[v] || v || "-" }

loadCustomers(); loadProducts()

const recentTransactionPriceList = ref([])
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
  queryParams: { pageNum: 1, pageSize: 10, customerId: undefined, productId: undefined },
  rules: {
    customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
    productId: [{ required: true, message: "请选择商品", trigger: "change" }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listRecentTransactionPrice(queryParams.value).then(res => {
    recentTransactionPriceList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => { loading.value = false })
}
function cancel() { open.value = false; reset() }
function reset() {
  form.value = { recentPriceId: undefined, customerId: undefined, productId: undefined, salePrice: 0, sourceBillType: "manual", businessDate: undefined, remark: undefined }
  proxy.resetForm("recentTransactionPriceRef")
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(i => i.recentPriceId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}
function handleAdd() { reset(); open.value = true; title.value = "新增最近成交价" }
function handleUpdate(row) {
  reset()
  getRecentTransactionPrice(row.recentPriceId || selectedIds.value).then(res => { form.value = res.data; open.value = true; title.value = "修改最近成交价" })
}
function submitForm() {
  proxy.$refs["recentTransactionPriceRef"].validate(valid => {
    if (!valid) return
    const fn = form.value.recentPriceId ? updateRecentTransactionPrice(form.value) : addRecentTransactionPrice(form.value)
    fn.then(() => { proxy.$modal.msgSuccess(form.value.recentPriceId ? "修改成功" : "新增成功"); open.value = false; getList() })
  })
}
function handleDelete(row) {
  const ids = row.recentPriceId || selectedIds.value
  proxy.$modal.confirm("是否确认删除选中记录？").then(() => delRecentTransactionPrice(ids)).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {})
}
getList()
</script>
