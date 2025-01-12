<template>
  <div class="article-list">
    <!-- 搜索和过滤 -->
    <el-card shadow="never" class="filter-container">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="标题">
          <el-input
            v-model="filterForm.title"
            placeholder="请输入文章标题"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="作者">
          <el-input
            v-model="filterForm.author"
            placeholder="请输入作者"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filterForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 文章列表 -->
    <el-card shadow="never" class="list-container">
      <template #header>
        <div class="card-header">
          <span>文章列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新建文章
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="articleList"
        style="width: 100%"
        border
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" :underline="false" @click="handleView(row)">
              {{ row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" min-width="120">
          <template #default="{ row }">
            <div class="author-info">
              <el-avatar :size="24" :src="row.authorAvatar" />
              <span>{{ row.authorName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag
              v-for="tag in row.tags"
              :key="tag.id"
              size="small"
              class="tag-item"
            >
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="100" align="center" />
        <el-table-column prop="likeCount" label="点赞" width="100" align="center" />
        <el-table-column prop="commentCount" label="评论" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'info'" size="small">
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" link @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleToggleStatus(row)"
                :loading="row.statusLoading"
              >
                {{ row.status === 'published' ? '下架' : '发布' }}
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import { useRouter } from 'vue-router'
import {
  getArticles,
  getCategories,
  updateArticleStatus,
  deleteArticle
} from '@/api/admin'

const router = useRouter()

// 过滤表单
const filterForm = ref({
  title: '',
  author: '',
  categoryId: '',
  status: ''
})

// 文章列表
const loading = ref(false)
const articleList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 分类列表
const categories = ref([])

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...filterForm.value
    }
    const response = await getArticles(params)
    articleList.value = response.content
    total.value = response.totalElements
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchArticles()
}

// 重置
const handleReset = () => {
  filterForm.value = {
    title: '',
    author: '',
    categoryId: '',
    status: ''
  }
  handleSearch()
}

// 查看文章
const handleView = (row) => {
  router.push(`/article/${row.id}`)
}

// 新建文章
const handleAdd = () => {
  router.push('/admin/article/edit')
}

// 编辑文章
const handleEdit = (row) => {
  router.push(`/admin/article/edit/${row.id}`)
}

// 更新文章状态
const handleToggleStatus = async (row) => {
  try {
    row.statusLoading = true
    const newStatus = row.status === 'published' ? 'draft' : 'published'
    await updateArticleStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  } finally {
    row.statusLoading = false
  }
}

// 删除文章
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该文章吗？此操作不可恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteArticle(row.id)
    ElMessage.success('删除成功')
    if (articleList.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    fetchArticles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchArticles()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchArticles()
}

onMounted(async () => {
  await Promise.all([fetchArticles(), fetchCategories()])
})
</script>

<style scoped>
.article-list {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.list-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag-item {
  margin-right: 4px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 