<template>
  <div class="dashboard">
    <!-- 数据概览 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
              <el-tag type="success" size="small">{{ stats.userGrowth }}%</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="value">{{ stats.totalUsers }}</div>
            <div class="chart">
              <el-progress
                :percentage="stats.userGrowth"
                :color="customColors"
                :show-text="false"
                :stroke-width="6"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总文章数</span>
              <el-tag type="warning" size="small">{{ stats.articleGrowth }}%</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="value">{{ stats.totalArticles }}</div>
            <div class="chart">
              <el-progress
                :percentage="stats.articleGrowth"
                :color="customColors"
                :show-text="false"
                :stroke-width="6"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总评论数</span>
              <el-tag type="info" size="small">{{ stats.commentGrowth }}%</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="value">{{ stats.totalComments }}</div>
            <div class="chart">
              <el-progress
                :percentage="stats.commentGrowth"
                :color="customColors"
                :show-text="false"
                :stroke-width="6"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总访问量</span>
              <el-tag type="danger" size="small">{{ stats.viewGrowth }}%</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="value">{{ stats.totalViews }}</div>
            <div class="chart">
              <el-progress
                :percentage="stats.viewGrowth"
                :color="customColors"
                :show-text="false"
                :stroke-width="6"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-radio-group v-model="userChartType" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <div ref="userChartRef" style="width: 100%; height: 300px"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>文章发布趋势</span>
              <el-radio-group v-model="articleChartType" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <div ref="articleChartRef" style="width: 100%; height: 300px"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新用户</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>
          <div class="user-list">
            <div v-for="user in latestUsers" :key="user.id" class="user-item">
              <el-avatar :size="40" :src="user.avatar" />
              <div class="user-info">
                <div class="username">{{ user.username }}</div>
                <div class="time">注册时间：{{ formatDate(user.createTime) }}</div>
              </div>
              <el-tag size="small" :type="user.status === 'active' ? 'success' : 'danger'">
                {{ user.status === 'active' ? '正常' : '禁用' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新文章</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>
          <div class="article-list">
            <div v-for="article in latestArticles" :key="article.id" class="article-item">
              <div class="article-info">
                <div class="title">{{ article.title }}</div>
                <div class="meta">
                  <span>作者：{{ article.author }}</span>
                  <span>分类：{{ article.category }}</span>
                  <span>发布时间：{{ formatDate(article.createTime) }}</span>
                </div>
              </div>
              <div class="article-stats">
                <el-tooltip content="浏览量" placement="top">
                  <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
                </el-tooltip>
                <el-tooltip content="评论数" placement="top">
                  <span><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
                </el-tooltip>
                <el-tooltip content="点赞数" placement="top">
                  <span><el-icon><Star /></el-icon> {{ article.likeCount }}</span>
                </el-tooltip>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { View, ChatDotRound, Star } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { formatDate } from '@/utils/date'
import { getStats, getLatestUsers, getLatestArticles } from '@/api/admin'

// 数据统计
const stats = ref({
  totalUsers: 0,
  totalArticles: 0,
  totalComments: 0,
  totalViews: 0,
  userGrowth: 0,
  articleGrowth: 0,
  commentGrowth: 0,
  viewGrowth: 0
})

// 图表相关
const userChartRef = ref(null)
const articleChartRef = ref(null)
const userChartType = ref('week')
const articleChartType = ref('week')
let userChart = null
let articleChart = null

// 最新动态
const latestUsers = ref([])
const latestArticles = ref([])

// 自定义进度条颜色
const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 }
]

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await getStats()
    stats.value = response
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取最新用户
const fetchLatestUsers = async () => {
  try {
    const response = await getLatestUsers()
    latestUsers.value = response
  } catch (error) {
    console.error('获取最新用户失败:', error)
  }
}

// 获取最新文章
const fetchLatestArticles = async () => {
  try {
    const response = await getLatestArticles()
    latestArticles.value = response
  } catch (error) {
    console.error('获取最新文章失败:', error)
  }
}

// 初始化用户增长趋势图表
const initUserChart = () => {
  if (!userChartRef.value) return

  userChart = echarts.init(userChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [150, 230, 224, 218, 135, 147, 260],
        type: 'line',
        smooth: true,
        areaStyle: {
          opacity: 0.3
        }
      }
    ]
  }
  userChart.setOption(option)
}

// 初始化文章发布趋势图表
const initArticleChart = () => {
  if (!articleChartRef.value) return

  articleChart = echarts.init(articleChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [120, 200, 150, 80, 70, 110, 130],
        type: 'bar'
      }
    ]
  }
  articleChart.setOption(option)
}

// 监听图表类型变化
watch(userChartType, () => {
  // 根据类型更新图表数据
})

watch(articleChartType, () => {
  // 根据类型更新图表数据
})

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  userChart?.resize()
  articleChart?.resize()
}

onMounted(async () => {
  await Promise.all([
    fetchStats(),
    fetchLatestUsers(),
    fetchLatestArticles()
  ])

  initUserChart()
  initArticleChart()

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  userChart?.dispose()
  articleChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.data-card {
  height: 160px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(100% - 40px);
}

.value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart {
  margin-top: 10px;
}

.chart-row {
  margin-top: 20px;
}

.chart-container {
  padding: 10px;
}

.activity-row {
  margin-top: 20px;
}

.user-list,
.article-list {
  max-height: 400px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.user-item:last-child {
  border-bottom: none;
}

.user-info {
  flex: 1;
  margin: 0 15px;
}

.username {
  font-weight: bold;
  color: #303133;
}

.time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.article-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.article-item:last-child {
  border-bottom: none;
}

.article-info {
  margin-bottom: 10px;
}

.title {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.meta {
  font-size: 12px;
  color: #909399;
}

.meta span {
  margin-right: 15px;
}

.article-stats {
  display: flex;
  gap: 15px;
  color: #606266;
  font-size: 13px;
}

.article-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}
</style> 