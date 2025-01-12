<template>
  <div class="login-page">
    <h2>登录</h2>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-position="top"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="请输入用户名"
          :prefix-icon="User"
        />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <div class="form-actions">
        <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
          登录
        </el-button>
      </div>

      <div class="form-links">
        <router-link to="/auth/register">没有账号？立即注册</router-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const formData = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 40, message: '密码长度必须在6-40之间', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    await userStore.login(formData.value)
    ElMessage.success('登录成功')

    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  text-align: center;
}

h2 {
  margin: 0 0 30px;
  color: #303133;
}

.form-actions {
  margin-top: 30px;
}

.submit-btn {
  width: 100%;
}

.form-links {
  margin-top: 20px;
  text-align: center;
}

.form-links a {
  color: #409eff;
  text-decoration: none;
  font-size: 0.9rem;
}

.form-links a:hover {
  text-decoration: underline;
}
</style> 