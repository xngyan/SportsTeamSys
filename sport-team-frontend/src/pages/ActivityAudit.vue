<template>
  <div class="activity-audit">
    <div class="header">
      <el-button @click="goBack" style="margin-right: 20px">← 返回</el-button>
      <h2>报名审核 - {{ activity?.title }}</h2>
    </div>

    <el-card v-if="activity" class="audit-card">
      <div class="audit-actions">
        <div class="stats">
          <span>待审核: {{ pendingRegistrations.length }}</span>
          <span style="margin-left: 20px">剩余名额: {{ activity.maxParticipants - approvedCount }}</span>
        </div>
        <el-button 
          type="success" 
          :disabled="pendingRegistrations.length === 0 || (activity.maxParticipants - approvedCount) <= 0"
          @click="approveAll"
        >
          一键通过所有
        </el-button>
      </div>

      <el-table :data="pendingRegistrations" style="width: 100%; margin-top: 20px">
        <el-table-column prop="userInfo.username" label="用户名" />
        <el-table-column prop="userInfo.phoneNum" label="联系方式" />
        <el-table-column prop="userInfo.level" label="运动等级">
          <template #default="scope">
            Lv.{{ scope.row.userInfo?.level || 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="registrationAt" label="报名时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="success" @click="approveReg(scope.row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectReg(scope.row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="pendingRegistrations.length === 0" class="empty-state">
        暂无待审核的报名
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()
const activityId = route.params.id
const activity = ref(null)
const registrations = ref([])

const pendingRegistrations = computed(() => {
  return registrations.value.filter(r => r.status === 1)
})

const approvedCount = computed(() => {
  return registrations.value.filter(r => r.status === 2).length
})

const fetchActivity = async () => {
  try {
    const res = await api.get(`/activities/${activityId}`)
    activity.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchRegistrations = async () => {
  try {
    const res = await api.get(`/registrations/activity/${activityId}`)
    registrations.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const approveReg = async (id) => {
  try {
    await api.put(`/registrations/${id}/approve`)
    ElMessage.success('已通过')
    fetchRegistrations()
    fetchActivity() // Update remaining slots info if needed
  } catch (e) {
    console.error(e)
  }
}

const rejectReg = async (id) => {
  try {
    await ElMessageBox.confirm('确定要驳回该报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await api.put(`/registrations/${id}/reject`)
    ElMessage.success('已驳回')
    fetchRegistrations()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const approveAll = async () => {
  try {
    await ElMessageBox.confirm(`确定要一键通过所有 ${pendingRegistrations.value.length} 条待审核报名吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await api.post(`/registrations/approve-all?activityId=${activityId}`)
    ElMessage.success('批量通过成功')
    fetchRegistrations()
    fetchActivity()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchActivity()
  fetchRegistrations()
})
</script>

<style scoped>
.activity-audit {
  max-width: 1000px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.audit-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats {
  font-size: 16px;
  color: #606266;
}

.empty-state {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
</style>
