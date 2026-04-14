<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="盘点单号" prop="checkNo">
        <el-input
          v-model="queryParams.checkNo"
          placeholder="请输入盘点单号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="盘点状态" clearable style="width: 200px">
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
          v-hasPermi="['business:inventoryCheck:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:inventoryCheck:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:inventoryCheck:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="inventoryCheckList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="盘点编号" align="center" prop="checkId" />
      <el-table-column label="盘点单号" align="center" prop="checkNo" />
      <el-table-column label="仓库编号" align="center" prop="warehouseId" />
      <el-table-column label="差异数量合计" align="center" prop="totalDiffQty" />
      <el-table-column label="差异金额合计" align="center" prop="totalDiffAmount" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:inventoryCheck:edit']">修改</el-button>
          <el-button link type="primary" icon="CircleCheck" :disabled="scope.row.status !== '0'" @click="handleAudit(scope.row)" v-hasPermi="['business:inventoryCheck:audit']">审核</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:inventoryCheck:remove']">删除</el-button>
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
      <el-form ref="inventoryCheckRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="盘点单号" prop="checkNo">
              <el-input v-model="form.checkNo" placeholder="请输入盘点单号" />
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
            <el-form-item label="差异数量合计" prop="totalDiffQty">
              <el-input v-model="form.totalDiffQty" placeholder="请输入差异数量合计" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="差异金额合计" prop="totalDiffAmount">
              <el-input v-model="form.totalDiffAmount" placeholder="请输入差异金额合计" />
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
  </div>
</template>

<script setup name="InventoryCheck">
import { listInventoryCheck, addInventoryCheck, delInventoryCheck, getInventoryCheck, updateInventoryCheck, auditInventoryCheck } from "@/api/business/inventoryCheck"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const inventoryCheckList = ref([])
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
    checkNo: undefined,
    status: undefined
  },
  rules: {
    checkNo: [{ required: true, message: "盘点单号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listInventoryCheck(queryParams.value).then(response => {
    inventoryCheckList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    checkId: undefined,
    checkNo: undefined,
    warehouseId: undefined,
    status: "0",
    totalDiffQty: undefined,
    totalDiffAmount: undefined,
    remark: undefined
  }
  proxy.resetForm("inventoryCheckRef")
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
  selectedIds.value = selection.map(item => item.checkId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加盘点单"
}

function handleUpdate(currentRow) {
  reset()
  const checkId = currentRow.checkId || selectedIds.value
  getInventoryCheck(checkId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改盘点单"
  })
}

// 审核按钮操作
function handleAudit(currentRow) {
  const checkId = currentRow.checkId
  proxy.$modal.confirm("确认审核当前盘点单吗？").then(function() {
    return auditInventoryCheck(checkId)
  }).then(() => {
    proxy.$modal.msgSuccess("审核成功")
    getList()
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs["inventoryCheckRef"].validate(valid => {
    if (valid) {
      if (form.value.checkId != undefined) {
        updateInventoryCheck(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addInventoryCheck(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  const checkIds = currentRow.checkId || selectedIds.value
  proxy.$modal.confirm('是否确认删除盘点编号为"' + checkIds + '"的数据项？').then(function() {
    return delInventoryCheck(checkIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
