<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户编码" prop="customerCode">
        <el-input
          v-model="queryParams.customerCode"
          placeholder="请输入客户编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="客户状态" clearable style="width: 200px">
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
          v-hasPermi="['business:customer:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['business:customer:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:customer:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:customer:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Refresh"
          @click="handleSyncAux"
          v-hasPermi="['business:customer:edit']"
        >同步辅助核算</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['business:customer:export', 'business:customer:list']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="customerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="客户编码" align="center" prop="customerCode" width="110" show-overflow-tooltip />
      <el-table-column label="客户名称" align="center" prop="customerName" min-width="140" show-overflow-tooltip />
      <el-table-column label="联系人" align="center" prop="contactPerson" width="90" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.contactPerson || '-' }}</template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.contactPhone || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户等级" align="center" prop="customerLevel" width="95">
        <template #default="scope">{{ levelLabel(scope.row.customerLevel) }}</template>
      </el-table-column>
      <el-table-column label="信用额度" align="right" prop="creditLimit" width="105">
        <template #default="scope">{{ Number(scope.row.creditLimit || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="期初应收" align="right" prop="openingReceivable" width="105">
        <template #default="scope">{{ Number(scope.row.openingReceivable || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="账期(天)" align="center" prop="receivableDays" width="85" />
      <el-table-column label="地址" align="center" prop="address" min-width="130" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.address || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="76">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <div class="table-action-cell">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:customer:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:customer:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="720px" append-to-body>
      <el-form ref="customerRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编码" prop="customerCode">
              <el-input v-model="form.customerCode" placeholder="请输入客户编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="form.customerName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户等级" prop="customerLevel">
              <el-select v-model="form.customerLevel" placeholder="请选择客户等级" clearable style="width: 100%">
                <el-option label="VIP 客户" value="VIP" />
                <el-option label="一级代理 (L1)" value="L1" />
                <el-option label="二级代理 (L2)" value="L2" />
                <el-option label="普通客户 (NORMAL)" value="NORMAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用额度" prop="creditLimit">
              <el-input-number v-model="form.creditLimit" controls-position="right" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="期初应收" prop="openingReceivable">
              <el-input-number v-model="form.openingReceivable" controls-position="right" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账期天数" prop="receivableDays">
              <el-input-number v-model="form.receivableDays" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="价格策略" prop="pricePolicyType">
              <el-select v-model="form.pricePolicyType" placeholder="默认专属价优先" clearable style="width: 100%">
                <el-option label="专属价优先" value="special_first" />
                <el-option label="等级价优先" value="level_first" />
                <el-option label="最近成交价优先" value="recent_first" />
                <el-option label="仅商品标准价" value="standard_only" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
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
              <el-checkbox v-model="upload.updateSupport" />是否更新已存在的客户数据
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

<script setup name="Customer">
import { getToken } from "@/utils/auth"
import { listCustomer, addCustomer, delCustomer, getCustomer, updateCustomer } from "@/api/business/customer"
import { syncAuxiliaryItems } from "@/api/business/generalLedger"

const LEVEL_MAP = { VIP: "VIP 客户", L1: "一级代理", L2: "二级代理", NORMAL: "普通客户" }
function levelLabel(v) { return LEVEL_MAP[v] || v || "-" }
function handleSyncAux() {
  syncAuxiliaryItems().then(() => proxy.$modal.msgSuccess("已将客户档案同步至财务辅助核算项")).catch(() => {})
}

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const customerList = ref([])
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
  url: import.meta.env.VITE_APP_BASE_API + "/business/customer/importData"
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    customerCode: undefined,
    customerName: undefined,
    status: undefined
  },
  rules: {
    customerCode: [{ required: true, message: "客户编码不能为空", trigger: "blur" }],
    customerName: [{ required: true, message: "客户名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listCustomer(queryParams.value).then(response => {
    customerList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    customerId: undefined,
    customerCode: undefined,
    customerName: undefined,
    contactPerson: undefined,
    contactPhone: undefined,
    customerLevel: "NORMAL",
    creditLimit: 0,
    openingReceivable: 0,
    receivableDays: 30,
    pricePolicyType: "special_first",
    contactEmail: undefined,
    address: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("customerRef")
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
  selectedIds.value = selection.map(item => item.customerId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加客户档案"
}

function handleUpdate(currentRow) {
  reset()
  const customerId = currentRow.customerId || selectedIds.value
  getCustomer(customerId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改客户档案"
  })
}

function submitForm() {
  proxy.$refs["customerRef"].validate(valid => {
    if (valid) {
      if (form.value.customerId != undefined) {
        updateCustomer(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCustomer(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const customerIds = currentRow.customerId || selectedIds.value
  proxy.$modal.confirm('是否确认删除客户编号为"' + customerIds + '"的数据项？').then(function() {
    return delCustomer(customerIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 导入按钮操作
function handleImport() {
  upload.title = "客户导入"
  upload.open = true
  upload.selectedFile = null
}

// 下载模板操作
function importTemplate() {
  proxy.download("business/customer/importTemplate", {}, `customer_template_${new Date().getTime()}.xlsx`)
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
  const plainMsg = String(response.msg || "导入完成").replace(/<br\s*\/?>/gi, "\n").replace(/<[^>]+>/g, "")
  proxy.$alert(plainMsg, "导入结果", { dangerouslyUseHTMLString: false })
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

getList()

function handleExport() {
  proxy.download("business/customer/export", { ...queryParams.value }, `客户档案_${new Date().getTime()}.xlsx`)
}
</script>
