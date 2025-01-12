<template>
  <div class="layout">
    <header class="header">
      <div class="container header-content">
        <div class="logo">
          <router-link to="/">论坛系统</router-link>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/articles" class="nav-item">文章</router-link>
        </nav>
        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                {{ userStore.username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin">管理后台</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/auth/login" class="nav-item">登录</router-link>
            <router-link to="/auth/register" class="nav-item">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <div class="container">
        <p>&copy; 2023 论坛系统. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    case 'logout':
      userStore.logout()
      router.push('/auth/login')
      break
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo a {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: #606266;
  text-decoration: none;
  font-size: 1rem;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #409eff;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.main {
  flex: 1;
  margin-top: 60px;
  background-color: #f5f7fa;
  padding: 20px 0;
}

.footer {
  background-color: #fff;
  padding: 20px 0;
  text-align: center;
  color: #909399;
}
</style> 