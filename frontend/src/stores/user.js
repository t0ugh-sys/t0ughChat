import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getUserProfile } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const loading = ref(false)

  const isAuthenticated = computed(() => !!token.value)

  // 登录
  const loginAction = async (credentials) => {
    try {
      loading.value = true
      const response = await login(credentials)
      token.value = response.token
      localStorage.setItem('token', response.token)
      await fetchUserProfile()
      return response
    } finally {
      loading.value = false
    }
  }

  // 注册
  const registerAction = async (userData) => {
    try {
      loading.value = true
      const response = await register(userData)
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const fetchUserProfile = async () => {
    if (!token.value) return
    try {
      loading.value = true
      const response = await getUserProfile()
      userInfo.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  // 检查并恢复用户会话
  const checkAuth = async () => {
    if (token.value && !userInfo.value) {
      await fetchUserProfile()
    }
  }

  return {
    token,
    userInfo,
    loading,
    isAuthenticated,
    loginAction,
    registerAction,
    fetchUserProfile,
    logout,
    checkAuth
  }
}) 