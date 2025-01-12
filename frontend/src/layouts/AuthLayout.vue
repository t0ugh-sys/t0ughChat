<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-box">
        <div class="logo">
          <router-link to="/">论坛系统</router-link>
        </div>
        <router-view />
        <div class="auth-footer">
          <router-link to="/">返回首页</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.auth-container {
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.auth-box {
  background-color: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.logo {
  text-align: center;
  margin-bottom: 40px;
}

.logo a {
  font-size: 2rem;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.auth-footer {
  margin-top: 20px;
  text-align: center;
}

.auth-footer a {
  color: #909399;
  text-decoration: none;
  font-size: 0.9rem;
}

.auth-footer a:hover {
  color: #409eff;
}
</style> 