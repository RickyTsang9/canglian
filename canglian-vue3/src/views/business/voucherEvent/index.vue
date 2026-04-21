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
      <el-table-column label="凭证号" align="center" prop="voucherNo" show-overflow-tooltip />
      <el-table-column label="凭证状态" align="center" prop="voucherStatus" />
      <el-table-column label="冲销凭证号" align="center" prop="reverseVoucherNo" show-overflow-tooltip />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260">
        <template #default="scope">
          <el-button link type="primary" icon="DocumentAdd" @click="handleGenerate(scope.row)" v-hasPermi="['business:voucherEvent:generate']">生成</el-button>
          <el-button link type="primary" icon="Finished" @click="handleWriteback(scope.row)" v-hasPermi="['business:voucherEvent:writeback']">回写</el-button>
          <el-button link type="danger" icon="RefreshLeft" @click="handleReverse(scope.row)" v-hasPermi="['business:voucherEvent:reverse']">冲销</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="VoucherEvent">
import { listVoucherEvent, generateVoucher, writebackVoucher, reverseVoucher } from "@/api/business/voucherEvent"

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
  }).finally(() => {
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

// 生成凭证
function handleGenerate(row) {
  generateVoucher(row.voucherEventId).then(() => {
    proxy.$modal.msgSuccess("凭证生成成功")
    getList()
  })
}

// 回写凭证
function handleWriteback(row) {
  writebackVoucher(row.voucherEventId).then(() => {
    proxy.$modal.msgSuccess("凭证回写成功")
    getList()
  })
}

// 冲销凭证
function handleReverse(row) {
  proxy.$modal.confirm('是否确认冲销凭证事件编号为"' + row.voucherEventId + '"的数据项？').then(() => {
    return reverseVoucher(row.voucherEventId)
  }).then(() => {
    proxy.$modal.msgSuccess("凭证冲销成功")
    getList()
  }).catch(() => {})
}

getList()
</script>
