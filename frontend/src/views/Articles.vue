<template>
  <div class="articles-page">
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="section">
            <div class="filter-bar">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索文章"
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #append>
                  <el-button @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
              <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="handleCategoryChange">
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
              <el-select v-model="sortBy" placeholder="排序方式" @change="handleSortChange">
                <el-option label="最新发布" value="createdAt" />
                <el-option label="最多浏览" value="viewCount" />
                <el-option label="最多点赞" value="likeCount" />
              </el-select>
            </div>
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
                  <span>发布时间：{{ formatDate(article.createdAt) }}</span>
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
            <h2 class="section-title">分类</h2>
            <div class="category-list">
              <el-tag
                v-for="category in categories"
                :key="category.id"
                :class="{ active: selectedCategory === category.id }"
                @click="handleCategoryClick(category.id)"
                class="category-tag"
              >
                {{ category.name }}
              </el-tag>
            </div>
          </div>
          <div class="section">
            <h2 class="section-title">热门标签</h2>
            <div class="tag-cloud">
              <el-tag
                v-for="tag in popularTags"
                :key="tag.name"
                :type="tag.type"
                class="tag-item"
              >
                {{ tag.name }} ({{ tag.count }})
              </el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, getArticlesByCategory, searchArticles } from '@/api/article'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const articles = ref([])
const categories = ref([])
const popularTags = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('createdAt')

const fetchArticles = async () => {
  loading.value = true
  try {
    let response
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: sortBy.value
    }

    if (selectedCategory.value) {
      response = await getArticlesByCategory(selectedCategory.value, params)
    } else if (searchKeyword.value) {
      response = await searchArticles(searchKeyword.value, params)
    } else {
      response = await getArticles(params)
    }

    articles.value = response.content
    total.value = response.totalElements
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchArticles()
}

const handleCategoryChange = () => {
  currentPage.value = 1
  fetchArticles()
}

const handleCategoryClick = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1
  fetchArticles()
}

const handleSortChange = () => {
  currentPage.value = 1
  fetchArticles()
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
.articles-page {
  min-height: 100%;
}

.section {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
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

.section-title {
  margin: 0 0 20px;
  font-size: 1.5rem;
  color: #303133;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-tag {
  cursor: pointer;
}

.category-tag.active {
  background-color: #409eff;
  color: #fff;
  border-color: #409eff;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  cursor: pointer;
}
</style> 