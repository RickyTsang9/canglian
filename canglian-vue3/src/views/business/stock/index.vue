<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable filterable style="width: 190px">
          <el-option v-for="w in warehouseOptions" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" placeholder="请选择商品" clearable filterable style="width: 210px">
          <el-option v-for="p in productOptions" :key="p.productId" :label="`${p.productCode || ''} - ${p.productName}`" :value="p.productId" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="viewMode === 'warning'" label="预警类型" prop="warningType">
        <el-select v-model="queryParams.warningType" placeholder="请选择预警类型" clearable style="width: 170px">
          <el-option v-for="w in warningTypeOptions" :key="w.value" :label="w.label" :value="w.value" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="viewMode === 'expiry'" label="效期状态" prop="expiryStatus">
        <el-select v-model="queryParams.expiryStatus" placeholder="全部效期" clearable style="width: 150px">
          <el-option label="正常" value="normal" />
          <el-option label="临期预警" value="near_expiry" />
          <el-option label="已过期" value="expired" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-radio-group v-model="viewMode" @change="handleModeChange">
          <el-radio-button value="normal">实时库存</el-radio-button>
          <el-radio-button value="warning">上下限/滞销预警</el-radio-button>
          <el-radio-button value="expiry">批次效期预警(FEFO)</el-radio-button>
        </el-radio-group>
      </el-col>
      <el-col v-if="viewMode === 'normal'" :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['business:stock:import']">期初导入</el-button>
      </el-col>
      <el-col v-if="viewMode === 'normal'" :span="1.5">
        <el-tag type="info" effect="plain" style="line-height: 30px; height: 32px">库存数量由入/出/调/盘业务单据自动维护，仅支持预警阈值设置与质检冻结</el-tag>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:stock:export', 'business:stock:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="stockList">
      <el-table-column label="仓库" align="center" min-width="95" show-overflow-tooltip>
        <template #default="scope">{{ getWarehouseName(scope.row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column label="库位" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">{{ getLocationName(scope.row.locationId) }}</template>
      </el-table-column>
      <el-table-column label="商品" align="center" min-width="140" show-overflow-tooltip>
        <template #default="scope">{{ getProductName(scope.row.productId) }}</template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" width="132" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.batchNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="现存" align="right" prop="quantity" width="70" />
      <el-table-column label="可用" align="right" prop="availableQuantity" width="70" />
      <el-table-column label="锁定" align="right" prop="lockedQuantity" width="66" />
      <el-table-column label="冻结" align="right" prop="frozenQuantity" width="66" />
      <el-table-column label="生产日期" align="center" prop="productionDate" width="102">
        <template #default="scope">{{ parseTime(scope.row.productionDate, '{y}-{m}-{d}') || '-' }}</template>
      </el-table-column>
      <el-table-column label="有效期至" align="center" prop="expiryDate" width="102">
        <template #default="scope">{{ parseTime(scope.row.expiryDate, '{y}-{m}-{d}') || '-' }}</template>
      </el-table-column>
      <el-table-column label="效期状态" align="center" width="106">
        <template #default="scope">
          <el-tag v-if="scope.row.expiryStatus === 'expired'" type="danger">已过期({{ scope.row.remainDays }}天)</el-tag>
          <el-tag v-else-if="scope.row.expiryStatus === 'near_expiry'" type="warning">临期({{ scope.row.remainDays }}天)</el-tag>
          <el-tag v-else-if="scope.row.expiryDate" type="success">正常({{ scope.row.remainDays ?? '-' }}天)</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column v-if="viewMode === 'warning'" label="预警类型" align="center" width="105" fixed="right">
        <template #default="scope">
          <el-tag :type="getWarningTagType(scope.row.warningType)">{{ getWarningTypeLabel(scope.row.warningType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="viewMode === 'warning'" label="预警说明" align="center" prop="warningMessage" min-width="180" show-overflow-tooltip />
      <el-table-column v-if="viewMode === 'normal'" label="操作" width="225" fixed="right" align="center">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="warning" icon="Lock" @click="openFreezeDialog(scope.row, 'freeze')" v-hasPermi="['business:stock:freeze']">冻结</el-button>
            <el-button link type="success" icon="Unlock" :disabled="Number(scope.row.frozenQuantity || 0) <= 0" @click="openFreezeDialog(scope.row, 'unfreeze')" v-hasPermi="['business:stock:freeze']">解冻</el-button>
            <el-button link type="primary" icon="Setting" @click="handleUpdateThreshold(scope.row)" v-hasPermi="['business:stock:edit']">预警设置</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 库存预警阈值设置弹窗（禁止手工直改库存数量） -->
    <el-dialog title="设置库存上下限与效期属性" v-model="open" width="600px" append-to-body>
      <el-form ref="stockRef" :model="form" label-width="95px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="仓库"><el-input :model-value="getWarehouseName(form.warehouseId)" disabled /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库位"><el-input :model-value="getLocationName(form.locationId)" disabled /></el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品"><el-input :model-value="getProductName(form.productId)" disabled /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="现存数量"><el-input :model-value="String(form.quantity ?? 0)" disabled /></el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期至" prop="expiryDate">
              <el-date-picker v-model="form.expiryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最小预警" prop="warningMinQty">
              <el-input-number v-model="form.warningMinQty" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大预警" prop="warningMaxQty">
              <el-input-number v-model="form.warningMaxQty" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">保存设置</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 库存冻结/解冻弹窗 -->
    <el-dialog :title="freezeForm.mode === 'freeze' ? '质检/残次库存冻结' : '释放冻结库存'" v-model="freezeOpen" width="460px" append-to-body>
      <el-form :model="freezeForm" label-width="100px">
        <el-form-item label="商品">{{ getProductName(freezeForm.productId) }}</el-form-item>
        <el-form-item label="当前可用">{{ freezeForm.availableQuantity }} &nbsp;|&nbsp; 已冻结：{{ freezeForm.frozenQuantity }}</el-form-item>
        <el-form-item :label="freezeForm.mode === 'freeze' ? '冻结数量' : '解冻数量'" required>
          <el-input-number
            v-model="freezeForm.quantity"
            :min="0.01"
            :max="freezeForm.mode === 'freeze' ? Number(freezeForm.availableQuantity || 0) : Number(freezeForm.frozenQuantity || 0)"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="原因说明">
          <el-input v-model="freezeForm.reason" placeholder="如：质检待复核 / 复检合格放行" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitFreeze">确 定</el-button>
        <el-button @click="freezeOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog :title="upload.title" v-model="upload.open" width="420px" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :on-error="handleFileError" :on-change="handleFileChange" :on-remove="handleFileRemove" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip"><el-checkbox v-model="upload.updateSupport" />是否更新已存在的库存数据</div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Stock">
import { getToken } from "@/utils/auth"
import { listStock, listStockWarning, listExpiryWarning, freezeStock, unfreezeStock, getStock, updateStock } from "@/api/business/stock"
import { useRoute } from "vue-router"
import { useBusinessOptions } from "@/utils/businessOptions"

const { proxy } = getCurrentInstance()
const route = useRoute()
const { warehouseOptions, productOptions, loadWarehouses, loadProducts, loadLocations, getWarehouseName, getProductName, getLocationName } = useBusinessOptions()

const stockList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const viewMode = ref(route.query.warningMode === "1" ? "warning" : "normal")

const freezeOpen = ref(false)
const freezeForm = reactive({ mode: "freeze", stockId: undefined, productId: undefined, availableQuantity: 0, frozenQuantity: 0, quantity: 1, reason: "" })

const upload = reactive({
  open: false, title: "", isUploading: false, updateSupport: 0, selectedFile: null,
  headers: { Authorization: "Bearer " + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + "/business/stock/importData"
})

const warningTypeOptions = [
  { label: "负可用库存", value: "negative_available" },
  { label: "低库存", value: "low_stock" },
  { label: "超储库存", value: "over_stock" },
  { label: "滞销库存", value: "unsold" }
]

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, warehouseId: undefined, productId: undefined, warningType: undefined, expiryStatus: undefined }
})

const { queryParams, form } = toRefs(data)

function getList() {
  loading.value = true
  const req = viewMode.value === "warning" ? listStockWarning : (viewMode.value === "expiry" ? listExpiryWarning : listStock)
  req(queryParams.value).then(res => {
    stockList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function handleModeChange() {
  queryParams.value.pageNum = 1
  getList()
}

function getWarningTypeLabel(t) {
  const o = warningTypeOptions.find(i => i.value === t)
  return o ? o.label : t
}

function getWarningTagType(t) {
  if (t === "negative_available") return "danger"
  if (t === "low_stock") return "warning"
  if (t === "over_stock") return "info"
  return "success"
}

function openFreezeDialog(row, mode) {
  Object.assign(freezeForm, {
    mode,
    stockId: row.stockId,
    productId: row.productId,
    availableQuantity: Number(row.availableQuantity || 0),
    frozenQuantity: Number(row.frozenQuantity || 0),
    quantity: 1,
    reason: mode === "freeze" ? "质检暂冻" : "复检放行"
  })
  freezeOpen.value = true
}

function submitFreeze() {
  if (!freezeForm.quantity || Number(freezeForm.quantity) <= 0) {
    proxy.$modal.msgError("数量必须大于0")
    return
  }
  const fn = freezeForm.mode === "freeze" ? freezeStock : unfreezeStock
  fn({ stockId: freezeForm.stockId, quantity: freezeForm.quantity, reason: freezeForm.reason }).then(() => {
    proxy.$modal.msgSuccess(freezeForm.mode === "freeze" ? "冻结成功" : "解冻成功")
    freezeOpen.value = false
    getList()
  })
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function handleUpdateThreshold(row) {
  getStock(row.stockId).then(res => {
    form.value = res.data || {}
    open.value = true
  })
}

function submitForm() {
  updateStock({
    stockId: form.value.stockId,
    productionDate: form.value.productionDate,
    expiryDate: form.value.expiryDate,
    warningMinQty: form.value.warningMinQty,
    warningMaxQty: form.value.warningMaxQty
  }).then(() => {
    proxy.$modal.msgSuccess("预警与效期属性保存成功")
    open.value = false
    getList()
  })
}

function handleImport() { upload.title = "期初库存导入"; upload.open = true; upload.selectedFile = null }
function importTemplate() { proxy.download("business/stock/importTemplate", {}, `stock_template_${Date.now()}.xlsx`) }
const handleFileUploadProgress = () => { upload.isUploading = true }
const handleFileChange = (file) => { upload.selectedFile = file }
const handleFileRemove = () => { upload.selectedFile = null }
const handleFileSuccess = (res, file) => {
  upload.open = false; upload.isUploading = false
  proxy.$refs["uploadRef"].handleRemove(file)
  const plainMsg = String(res.msg || "导入完成").replace(/<br\s*\/?>/gi, "\n").replace(/<[^>]+>/g, "")
  proxy.$alert(plainMsg, "导入结果", { dangerouslyUseHTMLString: false })
  getList()
}
const handleFileError = () => { upload.isUploading = false; proxy.$modal.msgError("文件导入失败") }
function submitFileForm() {
  if (!upload.selectedFile || !/\.(xls|xlsx)$/i.test(upload.selectedFile.name)) {
    proxy.$modal.msgError("请选择 xls 或 xlsx 文件")
    return
  }
  proxy.$refs["uploadRef"].submit()
}

watch(() => route.query.warningMode, (val) => {
  viewMode.value = val === "1" ? "warning" : "normal"
  queryParams.value.pageNum = 1
  getList()
})

loadWarehouses(); loadProducts(); loadLocations(); getList()

function handleExport() {
  proxy.download("business/stock/export", { ...queryParams.value }, `实时库存台账_${new Date().getTime()}.xlsx`)
}
</script>
