<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单据类型" prop="billType">
        <el-input v-model="queryParams.billType" placeholder="请输入单据类型" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="事件类型" prop="eventType">
        <el-input v-model="queryParams.eventType" placeholder="请输入事件类型" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="voucherEventList">
      <el-table-column label="凭证事件编号" align="center" prop="voucherEventId" />
      <el-table-column label="单据类型" align="center" prop="billType" />
      <el-table-column label="单据号" align="center" prop="billNo" />
      <el-table-column label="事件类型" align="center" prop="eventType" />
      <el-table-column label="事件金额" align="center" prop="eventAmount" />
      <el-table-column label="事件日期" align="center" prop="eventDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.eventDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="VoucherEvent">
import { listVoucherEvent } from "@/api/business/voucherEvent"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const voucherEventList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    billType: undefined,
    eventType: undefined,
    status: undefined
  }
})

const { queryParams } = toRefs(data)

// 查询凭证事件列表
function getList() {
  loading.value = true
  listVoucherEvent(queryParams.value).then(response => {
    voucherEventList.value = response.rows
    total.value = response.total
    loading.value = false
  })
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

getList()
</script>
