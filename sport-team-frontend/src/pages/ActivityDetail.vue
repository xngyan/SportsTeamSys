<template>
  <div class="activity-detail">
    <el-button @click="goBack" style="margin-bottom: 20px">← 返回</el-button>
    
    <el-card v-if="activity" class="detail-card">
      <div class="detail-header">
        <div>
          <h1>{{ activity.title }}</h1>
          <div class="tags">
            <span class="tag">{{ activity.sportType }}</span>
            <span :class="['status-tag', getStatusClass(activity.status)]">
              {{ getStatusText(activity.status) }}
            </span>
          </div>
        </div>
        <div v-if="isCreator" class="action-buttons">
          <el-button @click="editActivity">编辑</el-button>
          <el-button type="danger" @click="deleteActivity">删除</el-button>
        </div>
      </div>

      <el-divider></el-divider>

      <div class="detail-content">
        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <div class="info-section">
              <h3>基本信息</h3>
              <div class="info-item">
                <span class="label">发布者:</span>
                <span class="value">{{ activity.creatorName }}</span>
              </div>
              <div class="info-item">
                <span class="label">运动项目:</span>
                <span class="value">{{ activity.sportType }}</span>
              </div>
              <div class="info-item">
                <span class="label">地点:</span>
                <span class="value">{{ activity.spotAddress }}</span>
              </div>
              <div class="info-item">
                <span class="label">人数限制:</span>
                <span class="value">{{ activity.maxParticipants }}人</span>
              </div>
              <div class="info-item">
                <span class="label">已报名人数:</span>
                <span class="value">{{ activity.registrationCount }}/{{ activity.maxParticipants }}</span>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :md="12">
            <div class="info-section">
              <h3>时间信息</h3>
              <div class="info-item">
                <span class="label">报名截止:</span>
                <span class="value">{{ formatDateTime(activity.registrationDdl) }}</span>
              </div>
              <div class="info-item">
                <span class="label">活动开始:</span>
                <span class="value">{{ formatDateTime(activity.startAt) }}</span>
              </div>
              <div class="info-item">
                <span class="label">活动结束:</span>
                <span class="value">{{ formatDateTime(activity.endAt) }}</span>
              </div>
            </div>
          </el-col>
        </el-row>

        <div class="description-section">
          <h3>活动描述</h3>
          <p>{{ activity.description || '暂无描述' }}</p>
        </div>

        <el-divider></el-divider>

        <!-- 参加者列表 -->
        <div class="participants-section">
          <h3>已报名人员</h3>
          <div v-if="isCreator">
            <el-tabs>
              <el-tab-pane label="待审核">
                <div class="registration-list">
                  <div v-if="pendingRegistrations.length === 0" class="empty">暂无待审核的报名</div>
                  <div v-for="reg in pendingRegistrations" :key="reg.id" class="reg-item">
                    <div class="reg-info">
                      <p>用户名: {{ reg.userInfo?.username }}</p>
                      <p>联系方式: {{ reg.userInfo?.phoneNum }}</p>
                      <p>报名时间: {{ reg.registrationAt }}</p>
                    </div>
                    <div class="reg-actions">
                      <el-button size="small" type="success" @click="approveReg(reg.id)">通过</el-button>
                      <el-button size="small" type="danger" @click="rejectReg(reg.id)">驳回</el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="已通过">
                <div class="participant-list">
                  <div v-if="approvedRegistrations.length === 0" class="empty">暂无已通过的报名</div>
                  <div v-for="reg in approvedRegistrations" :key="reg.id" class="participant-item">
                    <p>{{ reg.userInfo?.username }}</p>
                    <p>{{ reg.userInfo?.phoneNum }}</p>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
          <div v-else>
            <div class="participant-list">
              <p>已参加人数: {{ activity.registrationCount }}/{{ activity.maxParticipants }}</p>
              <div v-if="currentUserRegistration && currentUserRegistration.status === 2" class="user-approved">
                <p>你已通过审核，已加入队伍</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-divider></el-divider>

      <div class="action-section">
        <el-button 
          v-if="activity.status === 1 && !isCreator && !hasApplied"
          type="primary" 
          size="large"
          @click="handleSignUp"
        >
          立即报名
        </el-button>
        <el-button 
          v-if="hasApplied && currentUserRegistration?.status === 1"
          type="warning" 
          size="large"
          @click="cancelSignUp"
        >
          取消报名
        </el-button>
        <span v-if="isCreator" class="creator-tip">你是该组队的发布者</span>
      </div>
    </el-card>

    <div v-else class="loading">
      <el-skeleton :rows="10" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { activityAPI, registrationAPI } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activity = ref(null)
const registrations = ref([])

const goBack = () => {
  router.go(-1)
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const getStatusText = (status) => {
  const statusMap = {
    1: '招募中',
    2: '已满人',
    3: '已截止',
    4: '已完成',
    0: '已取消',
  }
  return statusMap[status] || '-'
}

const getStatusClass = (status) => {
  const classMap = {
    1: 'recruiting',
    2: 'full',
    3: 'closed',
    4: 'completed',
    0: 'cancelled',
  }
  return classMap[status] || ''
}

const isCreator = computed(() => {
  return activity.value?.creatorId === userStore.user?.id
})

const hasApplied = computed(() => {
  return !!currentUserRegistration.value
})

const currentUserRegistration = computed(() => {
  return registrations.value.find(r => r.userId === userStore.user?.id)
})

const pendingRegistrations = computed(() => {
  return registrations.value.filter(r => r.status === 1)
})

const approvedRegistrations = computed(() => {
  return registrations.value.filter(r => r.status === 2)
})

const loadActivity = async () => {
  try {
    const response = await activityAPI.getActivity(route.params.id)
    activity.value = response.data
  } catch (error) {
    ElMessage.error('加载活动信息失败')
  }
}

const loadRegistrations = async () => {
  try {
    const response = await registrationAPI.getActivityRegistrations(route.params.id)
    registrations.value = response.data
  } catch (error) {
    if (error.response?.status !== 403) {
      // 只在非权限错误时提示
      // ElMessage.error('加载报名信息失败')
    }
  }
}

const handleSignUp = async () => {
  try {
    await registrationAPI.submitRegistration(route.params.id)
    ElMessage.success('报名成功，等待审核')
    loadActivity()
    loadRegistrations()
  } catch (error) {
    ElMessage.error(error.message || '报名失败')
  }
}

const cancelSignUp = async () => {
  try {
    await registrationAPI.cancelRegistration(currentUserRegistration.value.id)
    ElMessage.success('已取消报名')
    loadActivity()
    loadRegistrations()
  } catch (error) {
    ElMessage.error(error.message || '取消报名失败')
  }
}

const approveReg = async (regId) => {
  try {
    await registrationAPI.approveRegistration(regId)
    ElMessage.success('报名已通过')
    loadActivity()
    loadRegistrations()
  } catch (error) {
    ElMessage.error(error.message || '审核失败')
  }
}

const rejectReg = async (regId) => {
  try {
    await registrationAPI.rejectRegistration(regId)
    ElMessage.success('报名已驳回')
    loadActivity()
    loadRegistrations()
  } catch (error) {
    ElMessage.error(error.message || '驳回失败')
  }
}

const editActivity = () => {
  // TODO: 实现编辑功能
  ElMessage.info('编辑功能开发中')
}

const deleteActivity = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该组队吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await activityAPI.deleteActivity(route.params.id)
    ElMessage.success('组队已删除')
    router.push('/')
  } catch (error) {
    // 用户取消操作
  }
}

onMounted(() => {
  loadActivity()
  loadRegistrations()
})
</script>

<style scoped>
.activity-detail {
  max-width: 1000px;
  margin: 0 auto;
}

.detail-card {
  background: white;
  border-radius: 8px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.detail-header h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
}

.tags {
  display: flex;
  gap: 10px;
}

.tag {
  background: #f56c6c;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.status-tag.recruiting {
  background: #67c26a;
  color: white;
}

.status-tag.full {
  background: #e6a23c;
  color: white;
}

.status-tag.closed {
  background: #909399;
  color: white;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.detail-content {
  padding: 20px 0;
}

.info-section {
  margin-bottom: 30px;
}

.info-section h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.info-item {
  display: flex;
  padding: 8px 0;
}

.info-item .label {
  font-weight: bold;
  color: #606266;
  width: 120px;
  white-space: nowrap;
}

.info-item .value {
  color: #333;
  flex: 1;
}

.description-section {
  margin: 30px 0;
}

.description-section p {
  color: #606266;
  line-height: 1.6;
}

.participants-section {
  margin: 30px 0;
}

.participants-section h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.registration-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reg-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.reg-info p {
  margin: 5px 0;
  font-size: 14px;
}

.reg-actions {
  display: flex;
  gap: 10px;
}

.participant-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.participant-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.participant-item p {
  margin: 5px 0;
  font-size: 14px;
}

.empty {
  text-align: center;
  color: #999;
  padding: 20px;
}

.user-approved {
  padding: 15px;
  background: #f0f9ff;
  border: 1px solid #b3e5fc;
  border-radius: 4px;
  text-align: center;
  color: #0277bd;
}

.action-section {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.creator-tip {
  color: #e6a23c;
  font-weight: bold;
}

.loading {
  padding: 40px;
}
</style>
