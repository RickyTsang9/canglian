<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="费用类别名称" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入费用类别名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="费用类别状态" clearable style="width: 200px">
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
          v-hasPermi="['business:expenseCategory:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Sort"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="expenseCategoryList"
      row-key="categoryId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="categoryName" label="费用类别名称" />
      <el-table-column prop="categoryCode" label="费用类别编码" align="center" />
      <el-table-column prop="orderNum" label="排序" align="center" width="80" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['business:expenseCategory:edit']">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['business:expenseCategory:add']">新增</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['business:expenseCategory:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="expenseCategoryRef" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级类别" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="expenseCategoryOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="选择上级类别"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用类别名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入费用类别名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用类别编码" prop="categoryCode">
              <el-input v-model="form.categoryCode" placeholder="请输入费用类别编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
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

<script setup name="ExpenseCategory">
import { listExpenseCategory, treeselectExpenseCategory, addExpenseCategory, updateExpenseCategory, getExpenseCategory, delExpenseCategory } from "@/api/business/expenseCategory"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const expenseCategoryList = ref([])
const expenseCategoryOptions = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const isExpandAll = ref(false)
const refreshTable = ref(true)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    categoryName: undefined,
    status: undefined
  },
  rules: {
    parentId: [{ required: true, message: "上级类别不能为空", trigger: "change" }],
    categoryName: [{ required: true, message: "费用类别名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listExpenseCategory(queryParams.value).then(response => {
    expenseCategoryList.value = proxy.handleTree(response.data, "categoryId", "parentId")
    loading.value = false
  })
}

function getTreeselect() {
  treeselectExpenseCategory().then(response => {
    expenseCategoryOptions.value = []
    const rootNode = { id: 0, label: "顶级类别", children: [] }
    rootNode.children = response.data
    expenseCategoryOptions.value.push(rootNode)
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    categoryId: undefined,
    parentId: 0,
    categoryName: undefined,
    categoryCode: undefined,
    orderNum: 0,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("expenseCategoryRef")
}

function handleQuery() {
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleAdd(currentRow) {
  reset()
  getTreeselect()
  if (currentRow != null && currentRow.categoryId) {
    form.value.parentId = currentRow.categoryId
  } else {
    form.value.parentId = 0
  }
  open.value = true
  title.value = "添加费用类别"
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleUpdate(currentRow) {
  reset()
  getTreeselect()
  getExpenseCategory(currentRow.categoryId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改费用类别"
  })
}

function submitForm() {
  proxy.$refs["expenseCategoryRef"].validate(valid => {
    if (valid) {
      if (form.value.categoryId != undefined) {
        updateExpenseCategory(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addExpenseCategory(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(currentRow) {
  proxy.$modal.confirm('是否确认删除费用类别名称为"' + currentRow.categoryName + '"的数据项？').then(function() {
    return delExpenseCategory(currentRow.categoryId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
