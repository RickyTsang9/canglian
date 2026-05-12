<template>
  <div class="app-container">
    <el-card class="backup-card">
      <template #header>
        <div class="card-header">
          <span>数据备份</span>
          <el-tag type="info">人工执行</el-tag>
        </div>
      </template>
      <el-alert
        title="当前版本暂未接入在线备份执行接口"
        type="warning"
        show-icon
        :closable="false"
      />
      <div class="backup-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="建议方式">由运维在数据库服务器执行 MySQL 备份命令，并校验备份文件可恢复。</el-descriptions-item>
          <el-descriptions-item label="备份范围">业务库、系统配置、菜单权限、定时任务和上传文件目录。</el-descriptions-item>
          <el-descriptions-item label="恢复要求">恢复前先保留当前库快照，确认目标环境字符集、时区和版本一致。</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup name="DataBackup">
// 当前页面用于承接菜单入口，后续可接入后端备份任务接口。
</script>

<style scoped>
.backup-card {
  min-height: 360px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.backup-content {
  margin-top: 18px;
}
</style>
