<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="退货单号" prop="returnNo">
        <el-input
          v-model="queryParams.returnNo"
          placeholder="请输入退货单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退货类型" prop="returnType">
        <el-input
          v-model="queryParams.returnType"
          placeholder="请输入退货类型"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="退货状态" clearable style="width: 200px">
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
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['business:saleReturn:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:saleReturn:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:saleReturn:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="saleReturnList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="退货编号" align="center" prop="saleReturnId" />
      <el-table-column label="退货单号" align="center" prop="returnNo" />
      <el-table-column label="退货类型" align="center" prop="returnType" />
      <el-table-column label="客户编号" align="center" prop="customerId" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="总数量" align="center" prop="totalQty" />
      <el-table-column label="总金额" align="center" prop="totalAmount" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="auditBy" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.auditTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:saleReturn:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:saleReturn:remove']">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" @click="handleAudit(scope.row)" v-hasPermi="['business:saleReturn:audit']">审核</el-button>
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
      <el-form ref="saleReturnRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="退货单号" prop="returnNo">
              <el-input v-model="form.returnNo" placeholder="请输入退货单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退货类型" prop="returnType">
              <el-input v-model="form.returnType" placeholder="请输入退货类型" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编号" prop="customerId">
              <el-input v-model="form.customerId" placeholder="请输入客户编号" />
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
            <el-form-item label="总数量" prop="totalQty">
              <el-input v-model="form.totalQty" placeholder="请输入总数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input v-model="form.totalAmount" placeholder="请输入总金额" />
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
        <el-row>
          <el-col :span="12">
            <el-form-item label="审核人" prop="auditBy">
              <el-input v-model="form.auditBy" placeholder="请输入审核人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核时间" prop="auditTime">
              <el-date-picker
                v-model="form.auditTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择审核时间"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
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
  </div>
</template>

<script setup name="SaleReturn">
import { listSaleReturn, addSaleReturn, delSaleReturn, getSaleReturn, updateSaleReturn, auditSaleReturn } from "@/api/business/saleReturn"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const saleReturnList = ref([])
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
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    returnNo: undefined,
    returnType: undefined,
    status: undefined
  },
  rules: {
    returnNo: [{ required: true, message: "退货单号不能为空", trigger: "blur" }],
    returnType: [{ required: true, message: "退货类型不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 查询销售退货列表
function getList() {
  loading.value = true
  listSaleReturn(queryParams.value).then(response => {
    saleReturnList.value = response.rows
    total.value = response.total
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
    saleReturnId: undefined,
    returnNo: undefined,
    returnType: undefined,
    customerId: undefined,
    warehouseId: undefined,
    totalQty: undefined,
    totalAmount: undefined,
    status: "0",
    auditBy: undefined,
    auditTime: undefined,
    remark: undefined
  }
  proxy.resetForm("saleReturnRef")
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
  selectedIds.value = selection.map(item => item.saleReturnId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增按钮操作
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加销售退货"
}

// 修改按钮操作
function handleUpdate(row) {
  reset()
  const saleReturnId = row.saleReturnId || selectedIds.value
  getSaleReturn(saleReturnId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改销售退货"
  })
}

// 提交按钮
function submitForm() {
  proxy.$refs["saleReturnRef"].validate(valid => {
    if (valid) {
      if (form.value.saleReturnId != undefined) {
        updateSaleReturn(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSaleReturn(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 审核按钮操作
function handleAudit(row) {
  proxy.$modal.confirm('是否确认审核销售退货编号为"' + row.saleReturnId + '"的数据项？').then(function () {
    return auditSaleReturn(row.saleReturnId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("审核成功")
  }).catch(() => {})
}

// 删除按钮操作
function handleDelete(row) {
  const saleReturnIds = row.saleReturnId || selectedIds.value
  proxy.$modal.confirm('是否确认删除销售退货编号为"' + saleReturnIds + '"的数据项？').then(function () {
    return delSaleReturn(saleReturnIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
