<template>
  <div class="home">
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="section">
            <h2 class="section-title">最新文章</h2>
            <div class="article-list" v-loading="loading">
              <div v-for="article in articles" :key="article.id" class="article-item">
                <h3 class="article-title">
                  <router-link :to="{ name: 'article-detail', params: { id: article.id } }">
                    {{ article.title }}
                  </router-link>
                </h3>
                <div class="article-meta">
                  <span>作者：{{ article.author.username }}</span>
                  <span>分类：{{ article.category }}</span>
                  <span>浏览：{{ article.viewCount }}</span>
                  <span>点赞：{{ article.likeCount }}</span>
                </div>
                <div class="article-tags">
                  <el-tag v-for="tag in article.tags" :key="tag" size="small">{{ tag }}</el-tag>
                </div>
                <p class="article-summary">{{ article.content.slice(0, 200) }}...</p>
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
        </el-col>
        <el-col :span="8">
          <div class="section">
            <h2 class="section-title">热门文章</h2>
            <ul class="hot-articles">
              <li v-for="article in hotArticles" :key="article.id">
                <router-link :to="{ name: 'article-detail', params: { id: article.id } }">
                  {{ article.title }}
                </router-link>
                <span class="view-count">{{ article.viewCount }} 浏览</span>
              </li>
            </ul>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '@/api/article'

const loading = ref(false)
const articles = ref([])
const hotArticles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchArticles = async () => {
  loading.value = true
  try {
    const response = await getArticles({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    articles.value = response.content
    total.value = response.totalElements
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchArticles()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchArticles()
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.home {
  min-height: 100%;
}

.section {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}

.section-title {
  margin: 0 0 20px;
  font-size: 1.5rem;
  color: #303133;
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

.article-tags {
  margin-bottom: 10px;
}

.article-tags .el-tag {
  margin-right: 8px;
}

.article-summary {
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.hot-articles {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hot-articles li {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-articles li:last-child {
  border-bottom: none;
}

.hot-articles a {
  color: #606266;
  text-decoration: none;
  flex: 1;
  margin-right: 10px;
}

.hot-articles a:hover {
  color: #409eff;
}

.view-count {
  font-size: 0.9rem;
  color: #909399;
}
</style> 