<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库编号" prop="warehouseId">
        <el-input
          v-model="queryParams.warehouseId"
          placeholder="请输入仓库编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item v-if="warningMode" label="预警类型" prop="warningType">
        <el-select v-model="queryParams.warningType" placeholder="请选择预警类型" clearable style="width: 200px">
          <el-option v-for="warningOption in warningTypeOptions" :key="warningOption.value" :label="warningOption.label" :value="warningOption.value" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="warningMode" label="滞销天数" prop="warningDays">
        <el-input-number v-model="queryParams.warningDays" :min="1" :step="30" controls-position="right" style="width: 160px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          :type="warningMode ? 'warning' : 'primary'"
          plain
          icon="Warning"
          @click="handleWarningMode"
          v-hasPermi="['business:stock:list']"
        >{{ warningMode ? "返回库存" : "预警中心" }}</el-button>
      </el-col>
      <el-col v-if="!warningMode" :span="1.5">
        <el-button
          type="info"
          plain
          icon="Upload"
          @click="handleImport"
          v-hasPermi="['business:stock:import']"
        >导入</el-button>
      </el-col>
      <el-col v-if="!warningMode" :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['business:stock:add']"
        >新增</el-button>
      </el-col>
      <el-col v-if="!warningMode" :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:stock:edit']"
        >修改</el-button>
      </el-col>
      <el-col v-if="!warningMode" :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:stock:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="库存编号" align="center" prop="stockId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="库存数量" align="center" prop="quantity" />
      <el-table-column label="可用数量" align="center" prop="availableQuantity" />
      <el-table-column label="锁定数量" align="center" prop="lockedQuantity" />
      <el-table-column label="冻结数量" align="center" prop="frozenQuantity" />
      <el-table-column label="最小预警" align="center" prop="warningMinQty" />
      <el-table-column label="最大预警" align="center" prop="warningMaxQty" />
      <el-table-column v-if="warningMode" label="预警类型" align="center" prop="warningType" width="120">
        <template #default="scope">
          <el-tag :type="getWarningTagType(scope.row.warningType)">{{ getWarningTypeLabel(scope.row.warningType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="warningMode" label="预警说明" align="center" prop="warningMessage" min-width="220" />
      <el-table-column v-if="warningMode" label="滞销天数" align="center" prop="unsoldDays" />
      <el-table-column v-if="warningMode" label="最近出库时间" align="center" prop="lastOutboundTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.lastOutboundTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!warningMode" label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:stock:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:stock:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="720px" append-to-body>
      <el-form ref="stockRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="仓库编号" prop="warehouseId">
              <el-input v-model="form.warehouseId" placeholder="请输入仓库编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品编号" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入商品编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入库存数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="锁定数量" prop="lockedQuantity">
              <el-input v-model="form.lockedQuantity" placeholder="请输入锁定数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最小预警" prop="warningMinQty">
              <el-input v-model="form.warningMinQty" placeholder="请输入最小预警" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大预警" prop="warningMaxQty">
              <el-input v-model="form.warningMaxQty" placeholder="请输入最大预警" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="upload.title" v-model="upload.open" width="420px" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :on-change="handleFileChange" :on-remove="handleFileRemove" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已存在的库存数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Stock">
import { getToken } from "@/utils/auth"
import { listStock, listStockWarning, addStock, delStock, getStock, updateStock } from "@/api/business/stock"
import { useRoute } from "vue-router"

const { proxy } = getCurrentInstance()
const route = useRoute()

const stockList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")
const warningMode = ref(route.query.warningMode === "1")
const upload = reactive({
  // 是否显示导入弹窗
  open: false,
  // 导入弹窗标题
  title: "",
  // 是否正在上传
  isUploading: false,
  // 是否更新已存在数据
  updateSupport: 0,
  // 已选择文件
  selectedFile: null,
  // 请求头
  headers: { Authorization: "Bearer " + getToken() },
  // 上传地址
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
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    warehouseId: undefined,
    productId: undefined,
    warningType: undefined,
    warningDays: 90
  },
  rules: {
    warehouseId: [{ required: true, message: "仓库编号不能为空", trigger: "blur" }],
    productId: [{ required: true, message: "商品编号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  const requestMethod = warningMode.value ? listStockWarning : listStock
  requestMethod(queryParams.value).then(response => {
    stockList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 切换库存预警中心
function handleWarningMode() {
  warningMode.value = !warningMode.value
  queryParams.value.pageNum = 1
  getList()
}

// 获取预警类型名称
function getWarningTypeLabel(warningType) {
  const warningOption = warningTypeOptions.find(item => item.value === warningType)
  return warningOption ? warningOption.label : warningType
}

// 获取预警标签样式
function getWarningTagType(warningType) {
  if (warningType === "negative_available") {
    return "danger"
  }
  if (warningType === "low_stock") {
    return "warning"
  }
  if (warningType === "over_stock") {
    return "info"
  }
  return "success"
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    stockId: undefined,
    warehouseId: undefined,
    productId: undefined,
    quantity: undefined,
    lockedQuantity: undefined,
    frozenQuantity: undefined,
    warningMinQty: undefined,
    warningMaxQty: undefined
  }
  proxy.resetForm("stockRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.stockId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加库存"
}

function handleUpdate(currentRow) {
  reset()
  const stockId = currentRow.stockId || selectedIds.value
  getStock(stockId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改库存"
  })
}

function submitForm() {
  proxy.$refs["stockRef"].validate(valid => {
    if (valid) {
      if (form.value.stockId != undefined) {
        updateStock(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addStock(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const stockIds = currentRow.stockId || selectedIds.value
  proxy.$modal.confirm('是否确认删除库存编号为"' + stockIds + '"的数据项？').then(function() {
    return delStock(stockIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 导入按钮操作
function handleImport() {
  upload.title = "期初库存导入"
  upload.open = true
  upload.selectedFile = null
}

// 下载模板操作
function importTemplate() {
  proxy.download("business/stock/importTemplate", {}, `stock_template_${new Date().getTime()}.xlsx`)
}

// 文件上传中处理
const handleFileUploadProgress = () => {
  upload.isUploading = true
}

// 文件选择处理
const handleFileChange = (file) => {
  upload.selectedFile = file
}

// 文件删除处理
const handleFileRemove = () => {
  upload.selectedFile = null
}

// 文件上传成功处理
const handleFileSuccess = (response, file) => {
  upload.open = false
  upload.isUploading = false
  proxy.$refs["uploadRef"].handleRemove(file)
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
  getList()
}

// 提交上传文件
function submitFileForm() {
  const selectedFile = upload.selectedFile
  if (!selectedFile || !selectedFile.name || (!selectedFile.name.toLowerCase().endsWith(".xls") && !selectedFile.name.toLowerCase().endsWith(".xlsx"))) {
    proxy.$modal.msgError("请选择后缀为 xls 或 xlsx 的文件。")
    return
  }
  proxy.$refs["uploadRef"].submit()
}

getList()
</script>
