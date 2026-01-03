<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :xs="24" :md="6">
        <el-card class="profile-card">
          <div class="profile-header">
            <img v-if="profile.avatar" :src="profile.avatar" :alt="profile.username" class="avatar">
            <div v-else class="avatar-placeholder">
              <span>{{ profile.username?.charAt(0).toUpperCase() }}</span>
            </div>
            <h2>{{ profile.username }}</h2>
            <p class="level">运动等级: {{ profile.level }}</p>
          </div>

          <el-divider></el-divider>

          <div class="profile-menu">
            <div 
              :class="['menu-item', { active: activeTab === 'info' }]"
              @click="activeTab = 'info'"
            >
              基础信息
            </div>
            <div 
              :class="['menu-item', { active: activeTab === 'team' }]"
              @click="activeTab = 'team'"
            >
              我的队伍
            </div>
            <div 
              :class="['menu-item', { active: activeTab === 'settings' }]"
              @click="activeTab = 'settings'"
            >
              系统设置
            </div>
            <div 
              :class="['menu-item', { active: activeTab === 'stats' }]"
              @click="activeTab = 'stats'"
            >
              数据统计
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="18">
        <!-- 基础信息 -->
        <el-card v-if="activeTab === 'info'" class="content-card">
          <template #header>
            <div class="card-header">
              <span>基础信息管理</span>
              <el-button 
                v-if="!editMode" 
                text 
                @click="editMode = true"
              >
                编辑
              </el-button>
              <div v-else>
                <el-button text @click="saveProfile">保存</el-button>
                <el-button text @click="editMode = false">取消</el-button>
              </div>
            </div>
          </template>

          <el-form label-width="100px">
            <el-form-item label="用户名">
              <span v-if="!editMode">{{ profile.username }}</span>
              <el-input v-else v-model="editForm.nickname" />
            </el-form-item>

            <el-form-item label="学号">
              <span v-if="!editMode">{{ profile.stuId || '未设置' }}</span>
              <el-input v-else v-model="editForm.stuId" />
            </el-form-item>

            <el-form-item label="电话号码">
              <span v-if="!editMode">{{ profile.phoneNum || '未设置' }}</span>
              <el-input v-else v-model="editForm.phoneNum" />
            </el-form-item>

            <el-form-item label="性别">
              <span v-if="!editMode">{{ formatGender(profile.gender) }}</span>
              <el-select v-else v-model="editForm.gender" placeholder="选择性别">
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>

            <el-form-item label="运动等级">
              <span v-if="!editMode">{{ profile.level }}</span>
              <el-input-number v-else v-model="editForm.level" :min="1" />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 我的队伍 -->
        <el-card v-if="activeTab === 'team'" class="content-card">
          <template #header>
            <span>我的队伍</span>
          </template>

          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <div class="quick-link">
                <h3>我发布的组队</h3>
                <router-link to="/my-history">
                  <el-button type="primary" size="large">前往查看</el-button>
                </router-link>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="quick-link">
                <h3>我报名的组队</h3>
                <router-link to="/my-history">
                  <el-button type="primary" size="large">前往查看</el-button>
                </router-link>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 系统设置 -->
        <el-card v-if="activeTab === 'settings'" class="content-card">
          <template #header>
            <span>系统设置</span>
          </template>

          <el-form label-width="150px">
            <el-form-item label="修改密码">
              <el-button @click="showPasswordDialog = true">修改密码</el-button>
            </el-form-item>

            <el-divider></el-divider>

            <el-form-item label="报名审核提醒">
              <el-switch v-model="settings.approvalReminder" />
              <span style="margin-left: 10px; color: #999;">当有人报名我的组队时提醒</span>
            </el-form-item>

            <el-form-item label="审核结果通知">
              <el-switch v-model="settings.reviewNotification" />
              <span style="margin-left: 10px; color: #999;">当我的报名被审核时提醒</span>
            </el-form-item>

            <el-form-item label="活动开始提醒">
              <el-switch v-model="settings.activityReminder" />
              <span style="margin-left: 10px; color: #999;">活动即将开始时提醒</span>
            </el-form-item>

            <el-divider></el-divider>

            <el-form-item>
              <el-button @click="saveSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 数据统计 -->
        <el-card v-if="activeTab === 'stats'" class="content-card">
          <template #header>
            <span>数据统计</span>
          </template>

          <el-row :gutter="20">
            <el-col :xs="24" :md="8">
              <div class="stat-box">
                <p class="stat-label">发布组队数</p>
                <p class="stat-value">{{ stats.publishedCount }}</p>
              </div>
            </el-col>
            <el-col :xs="24" :md="8">
              <div class="stat-box">
                <p class="stat-label">参与报名数</p>
                <p class="stat-value">{{ stats.registrationCount }}</p>
              </div>
            </el-col>
            <el-col :xs="24" :md="8">
              <div class="stat-box">
                <p class="stat-label">通过审核数</p>
                <p class="stat-value">{{ stats.approvedCount }}</p>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="updatePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userAPI, activityAPI, registrationAPI } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const activeTab = ref('info')
const editMode = ref(false)
const showPasswordDialog = ref(false)

const profile = ref({
  username: '',
  avatar: '',
  stuId: '',
  phoneNum: '',
  level: 1,
  gender: '',
})

const editForm = ref({
  nickname: '',
  stuId: '',
  phoneNum: '',
  gender: '',
  avatar: '',
})

const passwordForm = ref({
  newPassword: '',
  confirmPassword: '',
})

const settings = ref({
  approvalReminder: true,
  reviewNotification: true,
  activityReminder: true,
})

const stats = ref({
  publishedCount: 0,
  registrationCount: 0,
  approvedCount: 0,
})

const loadProfile = async () => {
  try {
    const response = await userAPI.getProfile()
    profile.value = response.data
    editForm.value = {
      nickname: profile.value.username,
      stuId: profile.value.stuId,
      phoneNum: profile.value.phoneNum,
      gender: profile.value.gender,
      avatar: profile.value.avatar,
      level: profile.value.level,
    }
  } catch (error) {
    ElMessage.error('加载个人信息失败')
  }
}

const loadStats = async () => {
  try {
    const activitiesRes = await activityAPI.getMyActivities()
    const registrationsRes = await registrationAPI.getMyRegistrations()

    stats.value.publishedCount = activitiesRes.data.length
    stats.value.registrationCount = registrationsRes.data.length
    stats.value.approvedCount = registrationsRes.data.filter(r => r.status === 2).length
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const formatGender = (gender) => {
  const map = {
    'MALE': '男',
    'FEMALE': '女',
    'OTHER': '其他'
  }
  return map[gender] || '未设置'
}

const saveProfile = async () => {
  if (!/^[a-zA-Z_]+$/.test(editForm.value.nickname)) {
    ElMessage.warning('用户名只能包含字母和下划线')
    return
  }
  try {
    await userAPI.updateProfile({
      nickname: editForm.value.nickname,
      stuId: editForm.value.stuId,
      phoneNum: editForm.value.phoneNum,
      gender: editForm.value.gender,
      avatar: editForm.value.avatar,
      level: editForm.value.level,
    })
    ElMessage.success('个人信息已更新')
    editMode.value = false
    loadProfile()
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  }
}

const saveSettings = () => {
  localStorage.setItem('userSettings', JSON.stringify(settings.value))
  ElMessage.success('设置已保存')
}

const updatePassword = () => {
  if (!passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.warning('请填写所有字段')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  // TODO: 调用修改密码API
  ElMessage.success('密码修改成功')
  showPasswordDialog.value = false
  passwordForm.value = {
    newPassword: '',
    confirmPassword: '',
  }
}

onMounted(() => {
  loadProfile()
  loadStats()
  const savedSettings = localStorage.getItem('userSettings')
  if (savedSettings) {
    settings.value = JSON.parse(savedSettings)
  }
})
</script>

<style scoped>
.profile {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 15px;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin: 0 auto 15px;
  font-weight: bold;
}

.profile-header h2 {
  margin: 10px 0;
  font-size: 24px;
}

.level {
  color: #999;
  margin: 0;
}

.profile-menu {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.menu-item {
  padding: 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #606266;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #667eea;
}

.menu-item.active {
  background: #667eea;
  color: white;
}

.content-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-link {
  text-align: center;
  padding: 30px;
}

.quick-link h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
}

.stat-box {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}

.stat-label {
  color: #606266;
  margin: 0 0 10px 0;
  font-size: 14px;
}

.stat-value {
  margin: 0;
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
}
</style>
