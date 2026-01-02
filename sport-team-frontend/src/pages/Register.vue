<template>
  <div class="register-container">
    <div class="register-box">
      <h1>注册账号</h1>
      <el-form ref="formRef" :model="registerForm" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="学号" prop="stuId">
          <el-input v-model="registerForm.stuId" placeholder="请输入学号（可选）" />
        </el-form-item>
        <el-form-item label="电话号码" prop="phoneNum">
          <el-input v-model="registerForm.phoneNum" placeholder="请输入电话号码（可选）" />
        </el-form-item>
        <el-form-item label="运动等级" prop="level">
          <el-input-number v-model="registerForm.level" :min="1" />
        </el-form-item>
        <div class="button-group">
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <el-button @click="goToLogin">返回登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api'

const router = useRouter()

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  stuId: '',
  phoneNum: '',
  level: 1,
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  try {
    await authAPI.register(registerForm.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
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
</style>
