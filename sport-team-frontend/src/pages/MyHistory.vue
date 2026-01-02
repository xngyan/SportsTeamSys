<template>
  <div class="my-history">
    <el-tabs v-model="activeTab">
      <!-- 我发布的组队 -->
      <el-tab-pane label="我发布的组队" name="published">
        <div v-if="publishedActivities.length === 0" class="empty-state">
          <p>还没有发布过组队</p>
          <router-link to="/">
            <el-button type="primary">去发布组队</el-button>
          </router-link>
        </div>
        <div v-else class="activity-list">
          <el-card v-for="activity in publishedActivities" :key="activity.id" class="activity-card">
            <div class="activity-item">
              <div class="activity-info">
                <h3>{{ activity.title }}</h3>
                <div class="meta">
                  <span class="sport-tag">{{ activity.sportType }}</span>
                  <span :class="['status-tag', getStatusClass(activity.status)]">
                    {{ getStatusText(activity.status) }}
                  </span>
                </div>
                <p class="details">
                  {{ activity.maxParticipants }}人 | 
                  {{ activity.registrationCount }}/{{ activity.maxParticipants }}已报名
                </p>
              </div>
              <div class="activity-actions">
                <router-link :to="`/activity/audit/${activity.id}`" v-if="activity.status === 1" style="margin-right: 12px">
                  <el-badge :value="activity.pendingRegistrationCount" :hidden="!activity.pendingRegistrationCount" class="item">
                    <el-button size="small" type="primary" plain>审核报名</el-button>
                  </el-badge>
                </router-link>
                <router-link :to="`/activity/${activity.id}`">
                  <el-button size="small">查看详情</el-button>
                </router-link>
                <el-button 
                  v-if="activity.status === 1"
                  size="small"
                  @click="editActivity(activity.id)"
                >
                  编辑
                </el-button>
                <el-button 
                  v-if="activity.status === 1"
                  size="small" 
                  type="danger"
                  @click="deletePublishedActivity(activity.id)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 我报名的组队 -->
      <el-tab-pane label="我报名的组队" name="registered">
        <div v-if="userRegistrations.length === 0" class="empty-state">
          <p>还没有报名过组队</p>
          <router-link to="/">
            <el-button type="primary">去报名组队</el-button>
          </router-link>
        </div>
        <div v-else class="registration-list">
          <el-card v-for="reg in userRegistrations" :key="reg.id" class="registration-card">
            <div class="registration-item">
              <div class="registration-info">
                <h3>{{ reg.activityInfo?.title }}</h3>
                <div class="meta">
                  <span class="sport-tag">{{ reg.activityInfo?.sportType }}</span>
                  <span :class="['status-tag', getRegStatusClass(reg.status)]">
                    {{ getRegStatusText(reg.status) }}
                  </span>
                  <span class="time">报名时间: {{ reg.registrationAt }}</span>
                </div>
                <p class="details">
                  时间: {{ reg.activityInfo?.startAt }} | 
                  地点: {{ reg.activityInfo?.spotAddress }}
                </p>
              </div>
              <div class="registration-actions">
                <router-link :to="`/activity/${reg.activityId}`">
                  <el-button size="small">查看组队</el-button>
                </router-link>
                <el-button 
                  v-if="reg.status === 1"
                  size="small" 
                  type="warning"
                  @click="cancelUserRegistration(reg.id)"
                >
                  取消报名
                </el-button>
                <el-button 
                  v-if="reg.status === 2"
                  size="small" 
                  type="success"
                  disabled
                >
                  已加入
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { activityAPI, registrationAPI } from '@/api'

const activeTab = ref('published')
const publishedActivities = ref([])
const userRegistrations = ref([])

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

const getRegStatusText = (status) => {
  const statusMap = {
    1: '待审核',
    2: '已通过',
    3: '已驳回',
    4: '已取消',
  }
  return statusMap[status] || '-'
}

const getRegStatusClass = (status) => {
  const classMap = {
    1: 'pending',
    2: 'approved',
    3: 'rejected',
    4: 'cancelled',
  }
  return classMap[status] || ''
}

const loadPublishedActivities = async () => {
  try {
    const response = await activityAPI.getMyActivities()
    publishedActivities.value = response.data
  } catch (error) {
    ElMessage.error('加载我发布的组队失败')
  }
}

const loadUserRegistrations = async () => {
  try {
    const response = await registrationAPI.getMyRegistrations()
    userRegistrations.value = response.data
  } catch (error) {
    ElMessage.error('加载我的报名失败')
  }
}

const editActivity = (activityId) => {
  ElMessage.info('编辑功能开发中')
}

const deletePublishedActivity = async (activityId) => {
  try {
    await ElMessageBox.confirm('确定要删除该组队吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await activityAPI.deleteActivity(activityId)
    ElMessage.success('组队已删除')
    loadPublishedActivities()
  } catch (error) {
    // 用户取消操作
  }
}

const cancelUserRegistration = async (registrationId) => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    await registrationAPI.cancelRegistration(registrationId)
    ElMessage.success('报名已取消')
    loadUserRegistrations()
  } catch (error) {
    // 用户取消操作
  }
}

onMounted(() => {
  loadPublishedActivities()
  loadUserRegistrations()
})
</script>

<style scoped>
.my-history {
  max-width: 1000px;
  margin: 0 auto;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 16px;
}

.activity-list,
.registration-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-card,
.registration-card {
  background: white;
  border-radius: 8px;
}

.activity-item,
.registration-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  gap: 20px;
}

.activity-info,
.registration-info {
  flex: 1;
}

.activity-info h3,
.registration-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: bold;
}

.meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.sport-tag {
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

.status-tag.completed {
  background: #606266;
  color: white;
}

.status-tag.pending {
  background: #e6a23c;
  color: white;
}

.status-tag.approved {
  background: #67c26a;
  color: white;
}

.status-tag.rejected {
  background: #f56c6c;
  color: white;
}

.status-tag.cancelled {
  background: #909399;
  color: white;
}

.time {
  color: #999;
  font-size: 12px;
}

.details {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.activity-actions,
.registration-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .activity-item,
  .registration-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .activity-actions,
  .registration-actions {
    width: 100%;
    margin-top: 10px;
  }
}
</style>
