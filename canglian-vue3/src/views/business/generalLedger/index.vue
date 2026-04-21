<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="会计科目" name="subject">
        <el-form :model="subjectQuery" ref="subjectQueryRef" :inline="true">
          <el-form-item label="状态" prop="status">
            <el-select v-model="subjectQuery.status" placeholder="请选择状态" clearable style="width: 200px">
              <el-option label="正常" value="0" />
              <el-option label="停用" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getSubjectList">查询</el-button>
            <el-button icon="Refresh" @click="resetSubjectQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="subjectLoading" :data="subjectList">
          <el-table-column label="科目编码" align="center" prop="subjectCode" />
          <el-table-column label="科目名称" align="center" prop="subjectName" />
          <el-table-column label="科目类型" align="center" prop="subjectType" />
          <el-table-column label="余额方向" align="center" prop="balanceDirection" />
          <el-table-column label="状态" align="center" prop="status" />
          <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="辅助核算" name="auxiliary">
        <el-form :model="auxiliaryQuery" ref="auxiliaryQueryRef" :inline="true">
          <el-form-item label="辅助类型" prop="auxiliaryType">
            <el-input v-model="auxiliaryQuery.auxiliaryType" placeholder="如 customer/supplier/product" clearable style="width: 240px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getAuxiliaryList">查询</el-button>
            <el-button icon="Refresh" @click="resetAuxiliaryQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="auxiliaryLoading" :data="auxiliaryList">
          <el-table-column label="辅助类型" align="center" prop="auxiliaryType" />
          <el-table-column label="项目编码" align="center" prop="itemCode" />
          <el-table-column label="项目名称" align="center" prop="itemName" />
          <el-table-column label="关联编号" align="center" prop="relationId" />
          <el-table-column label="状态" align="center" prop="status" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="会计期间" name="period">
        <el-form :model="periodQuery" ref="periodQueryRef" :inline="true">
          <el-form-item label="结账状态" prop="closeStatus">
            <el-select v-model="periodQuery.closeStatus" placeholder="请选择结账状态" clearable style="width: 200px">
              <el-option label="未结账" value="open" />
              <el-option label="已结账" value="closed" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getPeriodList">查询</el-button>
            <el-button icon="Refresh" @click="resetPeriodQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="periodLoading" :data="periodList">
          <el-table-column label="期间编码" align="center" prop="periodCode" />
          <el-table-column label="开始日期" align="center" prop="startDate" />
          <el-table-column label="结束日期" align="center" prop="endDate" />
          <el-table-column label="结账状态" align="center" prop="closeStatus" />
          <el-table-column label="结账人" align="center" prop="closeBy" />
          <el-table-column label="操作" align="center" width="180">
            <template #default="scope">
              <el-button link type="primary" @click="handleClose(scope.row)" v-hasPermi="['business:generalLedger:close']">结账</el-button>
              <el-button link type="warning" @click="handleReopen(scope.row)" v-hasPermi="['business:generalLedger:reopen']">反结账</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="GeneralLedger">
import { listAccountSubject, listAuxiliaryItem, listFiscalPeriod, closeFiscalPeriod, reopenFiscalPeriod } from "@/api/business/generalLedger"

const { proxy } = getCurrentInstance()

const activeTab = ref("subject")
const subjectLoading = ref(false)
const auxiliaryLoading = ref(false)
const periodLoading = ref(false)
const subjectList = ref([])
const auxiliaryList = ref([])
const periodList = ref([])
const subjectQuery = reactive({ status: undefined })
const auxiliaryQuery = reactive({ auxiliaryType: undefined })
const periodQuery = reactive({ closeStatus: undefined })

// 查询会计科目列表
function getSubjectList() {
  subjectLoading.value = true
  listAccountSubject(subjectQuery).then(response => {
    subjectList.value = response.data
  }).finally(() => {
    subjectLoading.value = false
  })
}

// 重置会计科目查询
function resetSubjectQuery() {
  proxy.resetForm("subjectQueryRef")
  getSubjectList()
}

// 查询辅助核算列表
function getAuxiliaryList() {
  auxiliaryLoading.value = true
  listAuxiliaryItem(auxiliaryQuery).then(response => {
    auxiliaryList.value = response.data
  }).finally(() => {
    auxiliaryLoading.value = false
  })
}

// 重置辅助核算查询
function resetAuxiliaryQuery() {
  proxy.resetForm("auxiliaryQueryRef")
  getAuxiliaryList()
}

// 查询会计期间列表
function getPeriodList() {
  periodLoading.value = true
  listFiscalPeriod(periodQuery).then(response => {
    periodList.value = response.data
  }).finally(() => {
    periodLoading.value = false
  })
}

// 重置会计期间查询
function resetPeriodQuery() {
  proxy.resetForm("periodQueryRef")
  getPeriodList()
}

// 结账会计期间
function handleClose(row) {
  closeFiscalPeriod(row.periodId).then(() => {
    proxy.$modal.msgSuccess("结账成功")
    getPeriodList()
  })
}

// 反结账会计期间
function handleReopen(row) {
  reopenFiscalPeriod(row.periodId).then(() => {
    proxy.$modal.msgSuccess("反结账成功")
    getPeriodList()
  })
}

getSubjectList()
getAuxiliaryList()
getPeriodList()
</script>
