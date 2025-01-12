<template>
  <div class="profile-page">
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="section user-card">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="userInfo.avatar"></el-avatar>
              <el-upload
                class="avatar-uploader"
                action="/api/users/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-button size="small" type="primary">更换头像</el-button>
              </el-upload>
            </div>
            <h3>{{ userInfo.username }}</h3>
            <p class="email">{{ userInfo.email }}</p>
            <p class="bio">{{ userInfo.bio || '这个用户很懒，还没有填写简介' }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userStats.articleCount }}</div>
                <div class="stat-label">文章</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.commentCount }}</div>
                <div class="stat-label">评论</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.likeCount }}</div>
                <div class="stat-label">获赞</div>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :span="18">
          <div class="section">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="个人资料" name="profile">
                <el-form
                  ref="profileFormRef"
                  :model="profileForm"
                  :rules="profileRules"
                  label-width="100px"
                >
                  <el-form-item label="用户名" prop="username">
                    <el-input v-model="profileForm.username" disabled />
                  </el-form-item>
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="profileForm.email" />
                  </el-form-item>
                  <el-form-item label="个人简介" prop="bio">
                    <el-input
                      v-model="profileForm.bio"
                      type="textarea"
                      :rows="4"
                      placeholder="介绍一下自己吧"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleUpdateProfile" :loading="updating">
                      保存修改
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>

              <el-tab-pane label="修改密码" name="password">
                <el-form
                  ref="passwordFormRef"
                  :model="passwordForm"
                  :rules="passwordRules"
                  label-width="100px"
                >
                  <el-form-item label="当前密码" prop="oldPassword">
                    <el-input
                      v-model="passwordForm.oldPassword"
                      type="password"
                      show-password
                      placeholder="请输入当前密码"
                    />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      show-password
                      placeholder="请输入新密码"
                    />
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      show-password
                      placeholder="请再次输入新密码"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
                      修改密码
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>

              <el-tab-pane label="我的文章" name="articles">
                <div class="article-list" v-loading="loadingArticles">
                  <div v-for="article in articles" :key="article.id" class="article-item">
                    <h3 class="article-title">
                      <router-link :to="{ name: 'article-detail', params: { id: article.id } }">
                        {{ article.title }}
                      </router-link>
                    </h3>
                    <div class="article-meta">
                      <span>分类：{{ article.category }}</span>
                      <span>浏览：{{ article.viewCount }}</span>
                      <span>点赞：{{ article.likeCount }}</span>
                      <span>发布时间：{{ formatDate(article.createTime) }}</span>
                    </div>
                    <div class="article-actions">
                      <el-button type="primary" size="small" @click="handleEditArticle(article)">
                        编辑
                      </el-button>
                      <el-button type="danger" size="small" @click="handleDeleteArticle(article)">
                        删除
                      </el-button>
                    </div>
                  </div>
                  <div class="pagination">
                    <el-pagination
                      v-model:current-page="currentPage"
                      v-model:page-size="pageSize"
                      :total="total"
                      :page-sizes="[10, 20, 30, 50]"
                      layout="total, sizes, prev, pager, next"
                      @size-change="handleSizeChange"
                      @current-change="handleCurrentChange"
                    />
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="我的评论" name="comments">
                <div class="comment-list" v-loading="loadingComments">
                  <div v-for="comment in comments" :key="comment.id" class="comment-item">
                    <div class="comment-content">{{ comment.content }}</div>
                    <div class="comment-meta">
                      <router-link
                        :to="{ name: 'article-detail', params: { id: comment.articleId } }"
                        class="article-link"
                      >
                        {{ comment.articleTitle }}
                      </router-link>
                      <span>{{ formatDate(comment.createTime) }}</span>
                    </div>
                    <div class="comment-actions">
                      <el-button type="danger" size="small" @click="handleDeleteComment(comment)">
                        删除
                      </el-button>
                    </div>
                  </div>
                  <div class="pagination">
                    <el-pagination
                      v-model:current-page="commentCurrentPage"
                      v-model:page-size="commentPageSize"
                      :total="commentTotal"
                      :page-sizes="[10, 20, 30, 50]"
                      layout="total, sizes, prev, pager, next"
                      @size-change="handleCommentSizeChange"
                      @current-change="handleCommentCurrentChange"
                    />
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserProfile, updateUserProfile, changePassword } from '@/api/auth'
import { getArticlesByUser, deleteArticle } from '@/api/article'
import { getUserComments, deleteComment } from '@/api/comment'
import { formatDate } from '@/utils/date'

const userStore = useUserStore()
const activeTab = ref('profile')

// 用户信息
const userInfo = ref({})
const userStats = ref({
  articleCount: 0,
  commentCount: 0,
  likeCount: 0
})

// 个人资料表单
const profileFormRef = ref(null)
const updating = ref(false)
const profileForm = ref({
  username: '',
  email: '',
  bio: ''
})

const profileRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 修改密码表单
const passwordFormRef = ref(null)
const changingPassword = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 40, message: '密码长度必须在6-40之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

// 文章列表
const articles = ref([])
const loadingArticles = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 评论列表
const comments = ref([])
const loadingComments = ref(false)
const commentCurrentPage = ref(1)
const commentPageSize = ref(10)
const commentTotal = ref(0)

// 获取用户信息
const fetchUserProfile = async () => {
  try {
    const response = await getUserProfile()
    userInfo.value = response
    profileForm.value = {
      username: response.username,
      email: response.email,
      bio: response.bio
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 更新个人资料
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  try {
    await profileFormRef.value.validate()
    updating.value = true

    await updateUserProfile({
      email: profileForm.value.email,
      bio: profileForm.value.bio
    })

    ElMessage.success('个人资料更新成功')
    await fetchUserProfile()
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('更新失败，请重试')
    }
  } finally {
    updating.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true

    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })

    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/auth/login')
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('修改失败，请重试')
    }
  } finally {
    changingPassword.value = false
  }
}

// 获取文章列表
const fetchArticles = async () => {
  loadingArticles.value = true
  try {
    const response = await getArticlesByUser(userStore.userInfo.id, {
      page: currentPage.value - 1,
      size: pageSize.value
    })
    articles.value = response.content
    total.value = response.totalElements
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loadingArticles.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  loadingComments.value = true
  try {
    const response = await getUserComments(userStore.userInfo.id, {
      page: commentCurrentPage.value - 1,
      size: commentPageSize.value
    })
    comments.value = response.content
    commentTotal.value = response.totalElements
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  } finally {
    loadingComments.value = false
  }
}

// 删除文章
const handleDeleteArticle = async (article) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      type: 'warning'
    })
    
    await deleteArticle(article.id)
    ElMessage.success('删除成功')
    fetchArticles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除文章失败')
    }
  }
}

// 删除评论
const handleDeleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })
    
    await deleteComment(comment.id)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  }
}

// 编辑文章
const handleEditArticle = (article) => {
  router.push({ name: 'edit-article', params: { id: article.id } })
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchArticles()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchArticles()
}

const handleCommentSizeChange = (val) => {
  commentPageSize.value = val
  fetchComments()
}

const handleCommentCurrentChange = (val) => {
  commentCurrentPage.value = val
  fetchComments()
}

// 头像上传
const handleAvatarSuccess = (response) => {
  userInfo.value.avatar = response.url
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

onMounted(() => {
  fetchUserProfile()
})

// 监听标签页切换
watch(activeTab, (newVal) => {
  if (newVal === 'articles') {
    fetchArticles()
  } else if (newVal === 'comments') {
    fetchComments()
  }
})
</script>

<style scoped>
.profile-page {
  min-height: 100%;
}

.section {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}

.user-card {
  text-align: center;
}

.avatar-wrapper {
  margin-bottom: 20px;
}

.avatar-uploader {
  margin-top: 10px;
}

.user-card h3 {
  margin: 10px 0;
  color: #303133;
}

.email {
  color: #909399;
  margin-bottom: 10px;
}

.bio {
  color: #606266;
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 0.9rem;
  color: #909399;
}

.article-item {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.article-item:last-child {
  border-bottom: none;
}

.article-title {
  margin: 0 0 10px;
}

.article-title a {
  color: #303133;
  text-decoration: none;
  font-size: 1.2rem;
}

.article-title a:hover {
  color: #409eff;
}

.article-meta {
  font-size: 0.9rem;
  color: #909399;
  margin-bottom: 10px;
}

.article-meta span {
  margin-right: 20px;
}

.article-actions {
  display: flex;
  gap: 10px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  color: #303133;
  margin-bottom: 10px;
}

.comment-meta {
  font-size: 0.9rem;
  color: #909399;
  margin-bottom: 10px;
}

.article-link {
  color: #409eff;
  text-decoration: none;
  margin-right: 20px;
}

.article-link:hover {
  text-decoration: underline;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 