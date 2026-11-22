<template>
  <div class="app-container" style="min-height: 75vh;">
    <i-frame v-if="swaggerEnabled" v-model:src="url" />
    <el-card v-else-if="loaded" shadow="never" style="max-width: 720px; margin: 40px auto;">
      <el-result
        icon="info"
        title="系统接口文档（Swagger）已按安全策略关闭"
        sub-title="为防止生产环境接口元数据暴露，后端默认关闭 Swagger UI（SWAGGER_ENABLED=false）。"
      >
        <template #extra>
          <el-descriptions :column="1" border size="small" style="text-align: left; margin-bottom: 16px;">
            <el-descriptions-item label="当前状态">
              <el-tag type="warning">已安全关闭</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开启方式（仅限研发/测试环境）">
              设置环境变量 <code>SWAGGER_ENABLED=true</code> 或启动参数 <code>--swagger.enabled=true</code> 后重启服务即可在此在线调试。
            </el-descriptions-item>
          </el-descriptions>
          <el-button type="primary" @click="checkRuntimeStatus">重新检测状态</el-button>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import iFrame from '@/components/iFrame'
import request from '@/utils/request'

const url = ref(import.meta.env.VITE_APP_BASE_API + "/swagger-ui/index.html")
const swaggerEnabled = ref(false)
const loaded = ref(false)

function checkRuntimeStatus() {
  request({ url: '/system/config/runtimeStatus', method: 'get' })
    .then(res => {
      swaggerEnabled.value = Boolean(res.data && res.data.swaggerEnabled)
    })
    .catch(() => {
      swaggerEnabled.value = false
    })
    .finally(() => {
      loaded.value = true
    })
}

checkRuntimeStatus()
</script>
