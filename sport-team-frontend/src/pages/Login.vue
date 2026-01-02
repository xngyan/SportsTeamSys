<template>
  <div class="login-container">
    <div class="login-box">
      <h1>运动组队系统</h1>
      <el-form ref="formRef" :model="loginForm" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <div class="button-group">
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </div>
      </el-form>
      <div class="demo-account">
        <p>演示账号: admin / password</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  try {
    const response = await authAPI.login(loginForm.value)
    userStore.setToken(response.data.token)
    userStore.setUser(response.data.user)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.button-group :deep(.el-button) {
  flex: 1;
}

.demo-account {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: 15px;
}
</style>
