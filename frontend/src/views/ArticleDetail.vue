<template>
  <div class="article-detail-page">
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="section" v-loading="loading">
            <div v-if="article" class="article-content">
              <h1 class="article-title">{{ article.title }}</h1>
              <div class="article-meta">
                <span>作者：{{ article.author.username }}</span>
                <span>分类：{{ article.category }}</span>
                <span>浏览：{{ article.viewCount }}</span>
                <span>点赞：{{ article.likeCount }}</span>
                <span>发布时间：{{ formatDate(article.createdAt) }}</span>
              </div>
              <div class="article-tags">
                <el-tag v-for="tag in article.tags" :key="tag" size="small">{{ tag }}</el-tag>
              </div>
              <div class="content" v-html="article.content"></div>
              <div class="article-actions">
                <el-button
                  type="primary"
                  :icon="isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
                  @click="handleLike"
                >
                  {{ isLiked ? '已点赞' : '点赞' }}
                </el-button>
                <el-button
                  v-if="isAuthor"
                  type="warning"
                  icon="el-icon-edit"
                  @click="handleEdit"
                >
                  编辑
                </el-button>
                <el-button
                  v-if="isAuthor"
                  type="danger"
                  icon="el-icon-delete"
                  @click="handleDelete"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 评论区 -->
          <div class="section">
            <h2 class="section-title">评论</h2>
            <div class="comment-form">
              <el-input
                v-model="commentContent"
                type="textarea"
                :rows="4"
                placeholder="写下你的评论..."
              />
              <div class="form-actions">
                <el-button type="primary" @click="handleComment">发表评论</el-button>
              </div>
            </div>
            <div class="comment-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <span class="username">{{ comment.user.username }}</span>
                  <span class="time">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button type="text" @click="handleReply(comment)">回复</el-button>
                  <el-button
                    v-if="isCommentAuthor(comment)"
                    type="text"
                    @click="handleDeleteComment(comment)"
                  >
                    删除
                  </el-button>
                </div>
                <!-- 回复列表 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <div class="reply-header">
                      <span class="username">{{ reply.user.username }}</span>
                      <span class="time">{{ formatDate(reply.createdAt) }}</span>
                    </div>
                    <div class="reply-content">
                      <template v-if="reply.replyTo">
                        回复 <span class="reply-to">@{{ reply.replyTo.username }}</span>：
                      </template>
                      {{ reply.content }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="section">
            <h2 class="section-title">作者信息</h2>
            <div class="author-info">
              <el-avatar :size="64" :src="article?.author.avatar"></el-avatar>
              <h3>{{ article?.author.username }}</h3>
              <p>{{ article?.author.bio || '这个作者很懒，还没有填写简介' }}</p>
              <el-button type="primary" plain>关注作者</el-button>
            </div>
          </div>
          <div class="section">
            <h2 class="section-title">相关文章</h2>
            <ul class="related-articles">
              <li v-for="item in relatedArticles" :key="item.id">
                <router-link :to="{ name: 'article-detail', params: { id: item.id } }">
                  {{ item.title }}
                </router-link>
              </li>
            </ul>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticle, deleteArticle, toggleLike } from '@/api/article'
import { createComment, deleteComment } from '@/api/comment'
import { formatDate } from '@/utils/date'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const article = ref(null)
const comments = ref([])
const relatedArticles = ref([])
const commentContent = ref('')
const isLiked = ref(false)

const isAuthor = computed(() => {
  return article.value?.author.id === userStore.user?.id
})

const fetchArticle = async () => {
  loading.value = true
  try {
    const response = await getArticle(route.params.id)
    article.value = response
    isLiked.value = response.isLiked
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }

  try {
    await toggleLike(article.value.id)
    isLiked.value = !isLiked.value
    article.value.likeCount += isLiked.value ? 1 : -1
    ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞成功')
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleEdit = () => {
  router.push({ name: 'edit-article', params: { id: article.value.id } })
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      type: 'warning'
    })
    
    await deleteArticle(article.value.id)
    ElMessage.success('删除成功')
    router.push({ name: 'articles' })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除文章失败')
    }
  }
}

const handleComment = async () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }

  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await createComment({
      articleId: article.value.id,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    // 重新获取评论列表
    fetchComments()
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表评论失败')
  }
}

const isCommentAuthor = (comment) => {
  return comment.user.id === userStore.user?.id
}

const handleDeleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })
    
    await deleteComment(comment.id)
    ElMessage.success('删除成功')
    // 重新获取评论列表
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  }
}

const handleReply = (comment) => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }

  commentContent.value = `@${comment.user.username} `
}

onMounted(() => {
  fetchArticle()
})
</script>

<style scoped>
.article-detail-page {
  min-height: 100%;
}

.section {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}

.article-title {
  margin: 0 0 20px;
  font-size: 2rem;
  color: #303133;
}

.article-meta {
  font-size: 0.9rem;
  color: #909399;
  margin-bottom: 20px;
}

.article-meta span {
  margin-right: 20px;
}

.article-tags {
  margin-bottom: 20px;
}

.article-tags .el-tag {
  margin-right: 8px;
}

.content {
  line-height: 1.8;
  color: #303133;
}

.article-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.section-title {
  margin: 0 0 20px;
  font-size: 1.5rem;
  color: #303133;
}

.comment-form {
  margin-bottom: 30px;
}

.form-actions {
  margin-top: 16px;
  text-align: right;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  margin-bottom: 10px;
}

.username {
  font-weight: bold;
  color: #303133;
  margin-right: 10px;
}

.time {
  font-size: 0.9rem;
  color: #909399;
}

.comment-content {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.reply-list {
  margin-top: 16px;
  margin-left: 24px;
  padding-left: 16px;
  border-left: 2px solid #ebeef5;
}

.reply-item {
  margin-bottom: 16px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-to {
  color: #409eff;
  font-weight: bold;
}

.author-info {
  text-align: center;
}

.author-info h3 {
  margin: 16px 0 8px;
}

.author-info p {
  color: #606266;
  margin-bottom: 16px;
}

.related-articles {
  list-style: none;
  padding: 0;
  margin: 0;
}

.related-articles li {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.related-articles li:last-child {
  border-bottom: none;
}

.related-articles a {
  color: #606266;
  text-decoration: none;
}

.related-articles a:hover {
  color: #409eff;
}
</style> 