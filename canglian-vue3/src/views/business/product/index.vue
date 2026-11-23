<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="商品编码" prop="productCode">
        <el-input
          v-model="queryParams.productCode"
          placeholder="请输入商品编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input
          v-model="queryParams.productName"
          placeholder="请输入商品名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="商品状态" clearable style="width: 200px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Upload"
          @click="handleImport"
          v-hasPermi="['business:product:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['business:product:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:product:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:product:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="48" align="center" />
      <el-table-column label="商品编码" align="center" prop="productCode" width="105" show-overflow-tooltip />
      <el-table-column label="商品名称" align="center" prop="productName" min-width="130" show-overflow-tooltip />
      <el-table-column label="规格" align="center" prop="productSpec" width="95" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.productSpec || '-' }}</template>
      </el-table-column>
      <el-table-column label="条码" align="center" prop="barCode" width="115" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.barCode || '-' }}</template>
      </el-table-column>
      <el-table-column label="图片" align="center" prop="productImage" width="70">
        <template #default="scope">
          <image-preview v-if="scope.row.productImage" :src="scope.row.productImage" :width="40" :height="40" />
          <span v-else style="color: #9ca3af">-</span>
        </template>
      </el-table-column>
      <el-table-column label="基础单位" align="center" prop="unitName" width="80">
        <template #default="scope">{{ scope.row.unitName || '-' }}</template>
      </el-table-column>
      <el-table-column label="采/销单位" align="center" width="90">
        <template #default="scope">{{ scope.row.purchaseUnit || scope.row.unitName || '-' }} / {{ scope.row.saleUnit || scope.row.unitName || '-' }}</template>
      </el-table-column>
      <el-table-column label="管控属性" align="center" width="108">
        <template #default="scope">
          <el-tag v-if="scope.row.enableBatch === '1'" size="small" type="info" style="margin-right: 4px">批次</el-tag>
          <el-tag v-if="scope.row.enableSerial === '1'" size="small" type="warning">SN</el-tag>
          <span v-if="scope.row.enableBatch !== '1' && scope.row.enableSerial !== '1'">-</span>
        </template>
      </el-table-column>
      <el-table-column label="保质期(天)" align="center" prop="shelfLifeDays" width="90">
        <template #default="scope">{{ scope.row.shelfLifeDays > 0 ? scope.row.shelfLifeDays : '-' }}</template>
      </el-table-column>
      <el-table-column label="预警区间" align="center" width="96">
        <template #default="scope">
          <span v-if="!scope.row.warningMinQty && !scope.row.warningMaxQty">-</span>
          <span v-else>{{ scope.row.warningMinQty || 0 }} ~ {{ scope.row.warningMaxQty || '不限' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本价" align="right" prop="costPrice" width="90">
        <template #default="scope">{{ Number(scope.row.costPrice || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="售价" align="right" prop="salePrice" width="90">
        <template #default="scope">{{ Number(scope.row.salePrice || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="74">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="205" fixed="right" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="success" icon="ScaleToOriginal" @click="openUnitDrawer(scope.row)" v-hasPermi="['business:product:edit']">多单位</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:product:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:product:remove']">删除</el-button>
          </div>
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

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="商品编码" prop="productCode">
              <el-input v-model="form.productCode" placeholder="请输入商品编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="规格" prop="productSpec">
              <el-input v-model="form.productSpec" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基础单位" prop="unitName">
              <el-input v-model="form.unitName" placeholder="如：瓶/件/台（库存基准单位）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="条码" prop="barCode">
              <el-input v-model="form.barCode" placeholder="请输入条码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brandName">
              <el-input v-model="form.brandName" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="采购默认单位" prop="purchaseUnit">
              <el-input v-model="form.purchaseUnit" placeholder="默认同基础单位，可在多单位中配置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售默认单位" prop="saleUnit">
              <el-input v-model="form.saleUnit" placeholder="默认同基础单位，可在多单位中配置" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="启用批次" prop="enableBatch">
              <el-radio-group v-model="form.enableBatch">
                <el-radio value="1">是</el-radio>
                <el-radio value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用序列号" prop="enableSerial">
              <el-radio-group v-model="form.enableSerial">
                <el-radio value="1">是</el-radio>
                <el-radio value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="保质天数" prop="shelfLifeDays">
              <el-input-number v-model="form.shelfLifeDays" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="临期预警天" prop="warningBeforeDays">
              <el-input-number v-model="form.warningBeforeDays" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="成本价" prop="costPrice">
              <el-input-number v-model="form.costPrice" controls-position="right" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价" prop="salePrice">
              <el-input-number v-model="form.salePrice" controls-position="right" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="最小库存" prop="warningMinQty">
              <el-input-number v-model="form.warningMinQty" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大库存" prop="warningMaxQty">
              <el-input-number v-model="form.warningMaxQty" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品图片" prop="productImage">
          <image-upload v-model="form.productImage" :limit="1" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="upload.title" v-model="upload.open" width="420px" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :on-error="handleFileError" :on-change="handleFileChange" :on-remove="handleFileRemove" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已存在的商品数据
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

    <!-- 商品多单位与条码换算抽屉 -->
    <el-drawer v-model="unitDrawerOpen" :title="`多单位管理 - ${currentUnitProduct.productName || ''}`" size="780px">
      <div style="margin-bottom: 12px">
        <el-form :inline="true" :model="unitForm" size="small">
          <el-form-item label="单位名">
            <el-input v-model="unitForm.unitName" placeholder="如：箱/盒" style="width: 95px" />
          </el-form-item>
          <el-form-item label="换算率">
            <el-input-number v-model="unitForm.convertRatio" :min="0.0001" :step="1" controls-position="right" style="width: 100px" />
          </el-form-item>
          <el-form-item label="条码">
            <el-input v-model="unitForm.barCode" placeholder="包装条码" style="width: 130px" />
          </el-form-item>
          <el-form-item label="采/售价">
            <el-input-number v-model="unitForm.purchasePrice" :min="0" :precision="2" placeholder="采" controls-position="right" style="width: 88px" />
            <el-input-number v-model="unitForm.salePrice" :min="0" :precision="2" placeholder="售" controls-position="right" style="width: 88px; margin-left: 4px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitAddUnit">新增单位</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="productUnitList" border size="small">
        <el-table-column label="单位" prop="unitName" width="80" align="center" />
        <el-table-column label="基准单位" width="85" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isBaseUnit === '1' ? 'success' : 'info'">{{ scope.row.isBaseUnit === '1' ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="换算率(1=N基)" prop="convertRatio" width="115" align="right" />
        <el-table-column label="包装条码" prop="barCode" min-width="120" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.barCode || '-' }}</template>
        </el-table-column>
        <el-table-column label="采购价" prop="purchasePrice" width="90" align="right">
          <template #default="scope">{{ Number(scope.row.purchasePrice || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="销售价" prop="salePrice" width="90" align="right">
          <template #default="scope">{{ Number(scope.row.salePrice || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="96" fixed="right" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.isBaseUnit !== '1'" link type="danger" @click="handleDeleteUnit(scope.row)">删除</el-button>
            <el-tag v-else size="small" type="info">基准锁定</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="Product">
import { getToken } from "@/utils/auth"
import { listProduct, addProduct, delProduct, getProduct, updateProduct } from "@/api/business/product"
import { listUnitsByProduct, addProductUnit, delProductUnit } from "@/api/business/productUnit"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const productList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedIds = ref([])
const isSingleDisabled = ref(true)
const isMultipleDisabled = ref(true)
const total = ref(0)
const title = ref("")
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
  url: import.meta.env.VITE_APP_BASE_API + "/business/product/importData"
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productCode: undefined,
    productName: undefined,
    status: undefined
  },
  rules: {
    productCode: [{ required: true, message: "商品编码不能为空", trigger: "blur" }],
    productName: [{ required: true, message: "商品名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 查询商品档案列表
function getList() {
  loading.value = true
  listProduct(queryParams.value).then(response => {
    productList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    productId: undefined,
    productCode: undefined,
    productName: undefined,
    productSpec: undefined,
    unitName: undefined,
    purchaseUnit: undefined,
    saleUnit: undefined,
    baseUnit: undefined,
    unitConvertRatio: 1,
    enableBatch: "1",
    shelfLifeDays: 0,
    warningBeforeDays: 30,
    enableSerial: "0",
    warningMinQty: 0,
    warningMaxQty: 0,
    barCode: undefined,
    categoryName: undefined,
    brandName: undefined,
    productImage: undefined,
    costPrice: 0,
    salePrice: 0,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("productRef")
}

// 搜索按钮操作
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置按钮操作
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.productId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增按钮操作
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加商品档案"
}

// 修改按钮操作
function handleUpdate(row) {
  reset()
  const productId = row.productId || selectedIds.value
  getProduct(productId).then(response => {
    form.value = response.data
    form.value.enableBatch = form.value.enableBatch ?? "1"
    form.value.enableSerial = form.value.enableSerial ?? "0"
    form.value.warningMinQty = form.value.warningMinQty ?? 0
    form.value.warningMaxQty = form.value.warningMaxQty ?? 0
    open.value = true
    title.value = "修改商品档案"
  })
}

// 提交按钮
function submitForm() {
  proxy.$refs["productRef"].validate(valid => {
    if (valid) {
      form.value.baseUnit = form.value.unitName || form.value.baseUnit || "件"
      if (form.value.productId != undefined) {
        updateProduct(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addProduct(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除按钮操作
function handleDelete(row) {
  const productIds = row.productId || selectedIds.value
  proxy.$modal.confirm('是否确认删除商品编号为"' + productIds + '"的数据项？').then(function() {
    return delProduct(productIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 导入按钮操作
function handleImport() {
  upload.title = "商品导入"
  upload.open = true
  upload.selectedFile = null
}

// 下载模板操作
function importTemplate() {
  proxy.download("business/product/importTemplate", {}, `product_template_${new Date().getTime()}.xlsx`)
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

// 文件上传失败处理
const handleFileError = () => {
  upload.isUploading = false
  proxy.$modal.msgError("文件导入失败，请检查文件格式或网络后重试")
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

const unitDrawerOpen = ref(false)
const currentUnitProduct = ref({})
const productUnitList = ref([])
const unitForm = reactive({ unitName: "", convertRatio: 1, barCode: "", purchasePrice: 0, salePrice: 0, isBaseUnit: "0" })

function openUnitDrawer(row) {
  currentUnitProduct.value = row
  Object.assign(unitForm, { unitName: "", convertRatio: 1, barCode: "", purchasePrice: Number(row.costPrice || 0), salePrice: Number(row.salePrice || 0), isBaseUnit: "0" })
  unitDrawerOpen.value = true
  loadProductUnitList(row.productId)
}

function loadProductUnitList(productId) {
  listUnitsByProduct(productId).then(res => {
    productUnitList.value = res.data || []
  })
}

function submitAddUnit() {
  if (!unitForm.unitName) {
    proxy.$modal.msgError("请输入单位名称")
    return
  }
  addProductUnit({
    productId: currentUnitProduct.value.productId,
    unitName: unitForm.unitName,
    convertRatio: unitForm.convertRatio,
    barCode: unitForm.barCode,
    purchasePrice: unitForm.purchasePrice,
    salePrice: unitForm.salePrice,
    isBaseUnit: productUnitList.value.length === 0 ? "1" : "0",
    status: "0"
  }).then(() => {
    proxy.$modal.msgSuccess("新增单位成功")
    unitForm.unitName = ""
    unitForm.barCode = ""
    loadProductUnitList(currentUnitProduct.value.productId)
  })
}

function handleDeleteUnit(row) {
  proxy.$modal.confirm(`确认删除单位"${row.unitName}"？`).then(() => delProductUnit(row.unitId)).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    loadProductUnitList(currentUnitProduct.value.productId)
  }).catch(() => {})
}

getList()
</script>
