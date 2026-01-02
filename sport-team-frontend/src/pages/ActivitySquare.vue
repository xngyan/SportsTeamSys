<template>
  <div class="activity-square">
    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索组队信息..." 
        @keyup.enter="handleSearch"
        style="width: 300px"
      >
        <template #suffix>
          <el-icon class="is-clickable" @click="handleSearch">
            <Search />
          </el-icon>
        </template>
      </el-input>

      <el-select v-model="filterSportType" placeholder="选择运动项目" clearable style="width: 150px">
        <el-option label="篮球" value="篮球" />
        <el-option label="足球" value="足球" />
        <el-option label="羽毛球" value="羽毛球" />
        <el-option label="乒乓球" value="乒乓球" />
        <el-option label="排球" value="排球" />
        <el-option label="游泳" value="游泳" />
        <el-option label="跑步" value="跑步" />
        <el-option label="其他" value="其他" />
      </el-select>

      <el-select v-model="filterStatus" placeholder="招募状态" clearable style="width: 150px">
        <el-option label="招募中" :value="1" />
        <el-option label="已满人" :value="2" />
        <el-option label="已截止" :value="3" />
        <el-option label="已完成" :value="4" />
      </el-select>

      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="showPublishDialog = true">+ 发布组队</el-button>
    </div>

    <!-- 发布组队对话框 -->
    <el-dialog v-model="showPublishDialog" title="发布新的组队" width="600px">
      <el-form :model="newActivityForm" :rules="activityRules" label-width="120px">
        <el-form-item label="组队标题" prop="title">
          <el-input v-model="newActivityForm.title" placeholder="请输入组队标题" />
        </el-form-item>
        <el-form-item label="运动项目" prop="sportType">
          <el-select v-model="newActivityForm.sportType" placeholder="选择运动项目">
            <el-option label="篮球" value="篮球" />
            <el-option label="足球" value="足球" />
            <el-option label="羽毛球" value="羽毛球" />
            <el-option label="乒乓球" value="乒乓球" />
            <el-option label="排球" value="排球" />
            <el-option label="游泳" value="游泳" />
            <el-option label="跑步" value="跑步" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="赛点位置" prop="spotId">
          <el-select v-model="newActivityForm.spotId" placeholder="选择赛点位置">
            <el-option label="学校体育馆" :value="1" />
            <el-option label="足球场" :value="2" />
            <el-option label="羽毛球馆" :value="3" />
            <el-option label="游泳池" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="最多参加人数" prop="maxParticipants">
          <el-input-number v-model="newActivityForm.maxParticipants" :min="1" />
        </el-form-item>
        <el-form-item label="最低运动等级" prop="minLevelRequired">
          <el-input-number v-model="newActivityForm.minLevelRequired" :min="1" />
        </el-form-item>
        <el-form-item label="报名截止时间" prop="registrationDdl">
          <el-date-picker 
            v-model="newActivityForm.registrationDdl" 
            type="datetime"
            placeholder="选择截止时间"
          />
        </el-form-item>
        <el-form-item label="活动开始时间" prop="startAt">
          <el-date-picker 
            v-model="newActivityForm.startAt" 
            type="datetime"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="活动结束时间" prop="endAt">
          <el-date-picker 
            v-model="newActivityForm.endAt" 
            type="datetime"
            placeholder="选择结束时间"
          />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input 
            v-model="newActivityForm.description" 
            type="textarea"
            rows="4"
            placeholder="请输入活动描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePublishActivity">发布</el-button>
      </template>
    </el-dialog>

    <!-- 活动列表 -->
    <div class="activity-list">
      <div v-if="activities.length === 0" class="empty-state">
        <p>暂无组队信息</p>
      </div>
      <el-card v-for="activity in activities" :key="activity.id" class="activity-card">
        <div class="activity-header">
          <div class="activity-title">
            <h3>{{ activity.title }}</h3>
            <span class="sport-tag">{{ activity.sportType }}</span>
            <span :class="['status-tag', getStatusClass(activity.status)]">
              {{ getStatusText(activity.status) }}
            </span>
          </div>
          <div class="activity-info-grid">
            <div class="info-item">
              <span class="label">发布者:</span>
              <span class="value">{{ activity.creatorName }}</span>
            </div>
            <div class="info-item">
              <span class="label">时间:</span>
              <span class="value">{{ formatDateTime(activity.startAt) }}</span>
            </div>
            <div class="info-item">
              <span class="label">地点:</span>
              <span class="value">{{ activity.spotAddress }}</span>
            </div>
            <div class="info-item">
              <span class="label">报名截止:</span>
              <span class="value">{{ formatDateTime(activity.registrationDdl) }}</span>
            </div>
            <div class="info-item">
              <span class="label">人数:</span>
              <span class="value">{{ activity.registrationCount }}/{{ activity.maxParticipants }}</span>
            </div>
            <div class="info-item">
              <span class="label">剩余:</span>
              <span class="value">{{ activity.maxParticipants - activity.registrationCount }}人</span>
            </div>
          </div>
        </div>
        <div class="activity-footer">
          <router-link :to="`/activity/${activity.id}`" class="btn-detail">
            <el-button>查看详情</el-button>
          </router-link>
          <el-button 
            v-if="activity.status === 1"
            :type="activity.isRegistered ? 'danger' : 'primary'" 
            @click="handleSignUp(activity)"
          >
            {{ activity.isRegistered ? '取消报名' : '立即报名' }}
          </el-button>
          <el-button v-else disabled>
            {{ getStatusText(activity.status) }}
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @change="loadActivities"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { activityAPI, registrationAPI } from '@/api'

const showPublishDialog = ref(false)
const activities = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchKeyword = ref('')
const filterSportType = ref('')
const filterStatus = ref('')

const newActivityForm = ref({
  title: '',
  sportType: '',
  spotId: null,
  maxParticipants: 10,
  minLevelRequired: 1,
  registrationDdl: null,
  startAt: null,
  endAt: null,
  description: '',
})

const activityRules = {
  title: [{ required: true, message: '请输入组队标题', trigger: 'blur' }],
  sportType: [{ required: true, message: '请选择运动项目', trigger: 'change' }],
  spotId: [{ required: true, message: '请选择赛点位置', trigger: 'change' }],
  maxParticipants: [{ required: true, message: '请输入最多参加人数', trigger: 'blur' }],
  registrationDdl: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
  startAt: [{ required: true, message: '请选择活动开始时间', trigger: 'change' }],
  endAt: [{ required: true, message: '请选择活动结束时间', trigger: 'change' }],
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

const loadActivities = async () => {
  try {
    const response = await activityAPI.searchActivities({
      keyword: searchKeyword.value,
      sportType: filterSportType.value,
      status: filterStatus.value,
      page: currentPage.value - 1,
      size: pageSize.value,
    })
    activities.value = response.data.content || response.data
    total.value = response.data.totalElements || response.data.length
  } catch (error) {
    ElMessage.error('加载组队信息失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadActivities()
}

const handlePublishActivity = async () => {
  try {
    await activityAPI.createActivity(newActivityForm.value)
    ElMessage.success('组队发布成功')
    showPublishDialog.value = false
    newActivityForm.value = {
      title: '',
      sportType: '',
      spotId: null,
      maxParticipants: 10,
      minLevelRequired: 1,
      registrationDdl: null,
      startAt: null,
      endAt: null,
      description: '',
    }
    loadActivities()
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
  }
}

const handleSignUp = async (activity) => {
  try {
    if (activity.isRegistered) {
      await registrationAPI.cancelByActivityId(activity.id)
      ElMessage.success('取消报名成功')
    } else {
      await registrationAPI.submitRegistration(activity.id)
      ElMessage.success('报名成功')
    }
    loadActivities()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-square {
  max-width: 1200px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
}

.activity-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.activity-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.activity-title {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.activity-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
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

.activity-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  gap: 10px;
}

.info-item .label {
  font-weight: bold;
  color: #606266;
  white-space: nowrap;
}

.info-item .value {
  color: #333;
  flex: 1;
}

.activity-footer {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-detail {
  text-decoration: none;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
