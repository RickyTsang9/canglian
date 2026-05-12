<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable style="width: 200px">
          <el-option
            v-for="warehouseItem in warehouseOptions"
            :key="warehouseItem.warehouseId"
            :label="warehouseItem.warehouseName"
            :value="warehouseItem.warehouseId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="库位编码" prop="locationCode">
        <el-input
          v-model="queryParams.locationCode"
          placeholder="请输入库位编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="库位名称" prop="locationName">
        <el-input
          v-model="queryParams.locationName"
          placeholder="请输入库位名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="库位状态" clearable style="width: 200px">
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
          v-hasPermi="['business:location:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="isSingleDisabled"
          @click="handleUpdate"
          v-hasPermi="['business:location:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="isMultipleDisabled"
          @click="handleDelete"
          v-hasPermi="['business:location:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="locationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="库位编号" align="center" prop="locationId" />
      <el-table-column label="所属仓库" align="center" prop="warehouseId">
        <template #default="scope">
          <span>{{ formatWarehouseName(scope.row.warehouseId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库位编码" align="center" prop="locationCode" />
      <el-table-column label="库位名称" align="center" prop="locationName" />
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
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:location:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:location:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="locationRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option
              v-for="warehouseItem in warehouseOptions"
              :key="warehouseItem.warehouseId"
              :label="warehouseItem.warehouseName"
              :value="warehouseItem.warehouseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库位编码" prop="locationCode">
          <el-input v-model="form.locationCode" placeholder="请输入库位编码" />
        </el-form-item>
        <el-form-item label="库位名称" prop="locationName">
          <el-input v-model="form.locationName" placeholder="请输入库位名称" />
        </el-form-item>
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

<script setup name="Location">
import { listLocation, addLocation, delLocation, getLocation, updateLocation } from "@/api/business/location"
import { listWarehouse } from "@/api/business/warehouse"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const locationList = ref([])
const warehouseOptions = ref([])
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
    warehouseId: undefined,
    locationCode: undefined,
    locationName: undefined,
    status: undefined
  },
  rules: {
    warehouseId: [{ required: true, message: "所属仓库不能为空", trigger: "change" }],
    locationCode: [{ required: true, message: "库位编码不能为空", trigger: "blur" }],
    locationName: [{ required: true, message: "库位名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 查询库位档案列表
function getList() {
  loading.value = true
  listLocation(queryParams.value).then(response => {
    locationList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

// 查询仓库下拉选项
function getWarehouseOptions() {
  listWarehouse({ pageNum: 1, pageSize: 1000, status: "0" }).then(response => {
    warehouseOptions.value = response.rows || []
  })
}

// 格式化仓库名称
function formatWarehouseName(warehouseId) {
  const matchedWarehouse = warehouseOptions.value.find(warehouseItem => warehouseItem.warehouseId === warehouseId)
  return matchedWarehouse ? matchedWarehouse.warehouseName : warehouseId
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    locationId: undefined,
    warehouseId: undefined,
    locationCode: undefined,
    locationName: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("locationRef")
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
  selectedIds.value = selection.map(locationItem => locationItem.locationId)
  isSingleDisabled.value = selection.length !== 1
  isMultipleDisabled.value = !selection.length
}

// 新增按钮操作
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加库位档案"
}

// 修改按钮操作
function handleUpdate(row) {
  reset()
  const locationId = row.locationId || selectedIds.value
  getLocation(locationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改库位档案"
  })
}

// 提交按钮
function submitForm() {
  proxy.$refs["locationRef"].validate(valid => {
    if (valid) {
      if (form.value.locationId !== undefined) {
        updateLocation(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addLocation(form.value).then(() => {
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
  const locationIds = row.locationId || selectedIds.value
  proxy.$modal.confirm('是否确认删除库位编号为"' + locationIds + '"的数据项？').then(function() {
    return delLocation(locationIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getWarehouseOptions()
getList()
</script>
