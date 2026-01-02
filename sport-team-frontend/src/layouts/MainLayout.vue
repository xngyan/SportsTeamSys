<template>
  <div class="main-layout">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h1 class="logo">🏃 运动组队系统</h1>
        </div>
        <div class="header-right">
          <router-link to="/" class="nav-link">组队广场</router-link>
          <router-link to="/my-history" class="nav-link">
            <el-badge :is-dot="pendingCount > 0" class="badge-item">
              我的组队
            </el-badge>
          </router-link>
          <router-link to="/profile" class="nav-link">个人中心</router-link>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userStore.user?.username }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { ArrowDown } from '@element-plus/icons-vue'
import api from '@/utils/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const pendingCount = ref(0)

const checkPending = async () => {
  if (!userStore.user) return
  try {
    const res = await api.get('/activities/my-activities')
    if (res.data) {
      pendingCount.value = res.data.reduce((sum, activity) => {
        return sum + (activity.pendingRegistrationCount || 0)
      }, 0)
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  checkPending()
})

watch(() => route.path, () => {
  checkPending()
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.badge-item :deep(.el-badge__content.is-fixed) {
  top: 5px;
  right: -5px;
}
</style>

<style scoped>
.main-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background-color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #667eea;
}

.nav-link.router-link-exact-active {
  color: #667eea;
  border-bottom: 2px solid #667eea;
  padding-bottom: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #333;
  display: flex;
  align-items: center;
  gap: 5px;
}

.main-content {
  padding: 20px;
}

:deep(.el-header) {
  height: 70px;
}
</style>
