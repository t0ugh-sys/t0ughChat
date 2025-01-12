<template>
  <div class="article-edit">
    <el-card shadow="never" class="edit-container">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑文章' : '新建文章' }}</span>
          <div class="header-actions">
            <el-button @click="handleSaveDraft" :loading="savingDraft">
              保存草稿
            </el-button>
            <el-button type="primary" @click="handlePublish" :loading="publishing">
              {{ isEdit ? '更新' : '发布' }}
            </el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="articleForm"
        :rules="rules"
        label-width="80px"
        class="article-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="articleForm.title"
            placeholder="请输入文章标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="articleForm.categoryId"
            placeholder="请选择分类"
            class="category-select"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <el-button type="primary" link @click="handleAddCategory">
            <el-icon><Plus /></el-icon> 新增分类
          </el-button>
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="articleForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择标签（可输入创建）"
            class="tag-select"
          >
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="articleForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容" prop="content" class="content-item">
          <div class="editor-container">
            <div class="toolbar-container">
              <el-button-group>
                <el-button @click="handleToolbar('bold')">
                  <el-icon><Bold /></el-icon>
                </el-button>
                <el-button @click="handleToolbar('italic')">
                  <el-icon><Italic /></el-icon>
                </el-button>
                <el-button @click="handleToolbar('heading')">
                  <el-icon><TopRight /></el-icon>
                </el-button>
              </el-button-group>
              <el-button-group>
                <el-button @click="handleToolbar('link')">
                  <el-icon><Link /></el-icon>
                </el-button>
                <el-button @click="handleToolbar('image')">
                  <el-icon><Picture /></el-icon>
                </el-button>
                <el-button @click="handleToolbar('code')">
                  <el-icon><Terminal /></el-icon>
                </el-button>
              </el-button-group>
              <el-button-group>
                <el-button @click="handleToolbar('list-ul')">
                  <el-icon><List /></el-icon>
                </el-button>
                <el-button @click="handleToolbar('list-ol')">
                  <el-icon><Finished /></el-icon>
                </el-button>
                <el-button @click="handleToolbar('quote')">
                  <el-icon><ChatDotSquare /></el-icon>
                </el-button>
              </el-button-group>
            </div>
            <div class="editor-wrapper">
              <el-input
                v-model="articleForm.content"
                type="textarea"
                :rows="20"
                placeholder="请输入文章内容（支持 Markdown 格式）"
                @input="handleContentChange"
              />
            </div>
          </div>
          <div class="preview-container" v-html="previewContent"></div>
        </el-form-item>

        <el-form-item label="封面" prop="cover">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="articleForm.cover" :src="articleForm.cover" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 新增分类对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      title="新增分类"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleCategorySubmit"
            :loading="submittingCategory"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Plus,
  Bold,
  Italic,
  TopRight,
  Link,
  Picture,
  Terminal,
  List,
  Finished,
  ChatDotSquare
} from '@element-plus/icons-vue'
import {
  getArticle,
  createArticle,
  updateArticle,
  saveDraft,
  getCategories,
  getTags,
  createCategory
} from '@/api/admin'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

// 文章表单
const formRef = ref(null)
const articleForm = ref({
  title: '',
  categoryId: '',
  tags: [],
  summary: '',
  content: '',
  cover: ''
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2-100个字符之间', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
}

// 分类和标签
const categories = ref([])
const tags = ref([])

// 新增分类
const categoryDialogVisible = ref(false)
const categoryFormRef = ref(null)
const submittingCategory = ref(false)
const categoryForm = ref({
  name: '',
  description: ''
})

const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符之间', trigger: 'blur' }
  ]
}

// 上传配置
const uploadUrl = import.meta.env.VITE_API_URL + '/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// 保存和发布状态
const savingDraft = ref(false)
const publishing = ref(false)

// Markdown 预览
const previewContent = computed(() => {
  if (!articleForm.value.content) return ''
  const html = marked(articleForm.value.content)
  return DOMPurify.sanitize(html)
})

// 获取文章详情
const fetchArticle = async (id) => {
  try {
    const article = await getArticle(id)
    articleForm.value = {
      ...article,
      tags: article.tags.map(tag => tag.id)
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
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

// 获取标签列表
const fetchTags = async () => {
  try {
    const response = await getTags()
    tags.value = response
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
  }
}

// 工具栏操作
const handleToolbar = (type) => {
  const textarea = document.querySelector('.editor-wrapper textarea')
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = textarea.value

  let insertion = ''
  let newStart = start
  let newEnd = end

  switch (type) {
    case 'bold':
      insertion = `**${text.substring(start, end) || '粗体文本'}**`
      newEnd = newStart + insertion.length
      break
    case 'italic':
      insertion = `*${text.substring(start, end) || '斜体文本'}*`
      newEnd = newStart + insertion.length
      break
    case 'heading':
      insertion = `\n### ${text.substring(start, end) || '标题'}\n`
      newEnd = newStart + insertion.length
      break
    case 'link':
      insertion = `[${text.substring(start, end) || '链接文本'}](url)`
      newEnd = newStart + insertion.length
      break
    case 'image':
      insertion = `![${text.substring(start, end) || '图片描述'}](url)`
      newEnd = newStart + insertion.length
      break
    case 'code':
      insertion = `\`\`\`\n${text.substring(start, end) || '代码块'}\n\`\`\``
      newEnd = newStart + insertion.length
      break
    case 'list-ul':
      insertion = `\n- ${text.substring(start, end) || '列表项'}`
      newEnd = newStart + insertion.length
      break
    case 'list-ol':
      insertion = `\n1. ${text.substring(start, end) || '列表项'}`
      newEnd = newStart + insertion.length
      break
    case 'quote':
      insertion = `\n> ${text.substring(start, end) || '引用文本'}`
      newEnd = newStart + insertion.length
      break
  }

  articleForm.value.content = text.substring(0, start) + insertion + text.substring(end)
  textarea.focus()
  setTimeout(() => {
    textarea.selectionStart = newStart
    textarea.selectionEnd = newEnd
  })
}

// 内容变化处理
const handleContentChange = () => {
  // 可以在这里添加自动保存逻辑
}

// 上传封面
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }

  return isImage && isLt2M
}

const handleCoverSuccess = (response) => {
  articleForm.value.cover = response.url
  ElMessage.success('封面上传成功')
}

// 新增分类
const handleAddCategory = () => {
  categoryForm.value = {
    name: '',
    description: ''
  }
  categoryDialogVisible.value = true
}

const handleCategorySubmit = async () => {
  if (!categoryFormRef.value) return

  try {
    await categoryFormRef.value.validate()
    submittingCategory.value = true

    const response = await createCategory(categoryForm.value)
    categories.value.push(response)
    articleForm.value.categoryId = response.id
    
    ElMessage.success('分类创建成功')
    categoryDialogVisible.value = false
  } catch (error) {
    console.error('分类创建失败:', error)
    ElMessage.error('分类创建失败')
  } finally {
    submittingCategory.value = false
  }
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    savingDraft.value = true
    const draft = { ...articleForm.value, status: 'draft' }
    
    if (isEdit.value) {
      await updateArticle(route.params.id, draft)
    } else {
      await saveDraft(draft)
    }
    
    ElMessage.success('草稿保存成功')
  } catch (error) {
    console.error('草稿保存失败:', error)
    ElMessage.error('草稿保存失败')
  } finally {
    savingDraft.value = false
  }
}

// 发布文章
const handlePublish = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    publishing.value = true

    const article = { ...articleForm.value, status: 'published' }
    
    if (isEdit.value) {
      await updateArticle(route.params.id, article)
      ElMessage.success('文章更新成功')
    } else {
      await createArticle(article)
      ElMessage.success('文章发布成功')
    }
    
    router.push('/admin/article')
  } catch (error) {
    console.error(isEdit.value ? '文章更新失败:' : '文章发布失败:', error)
    ElMessage.error(isEdit.value ? '文章更新失败' : '文章发布失败')
  } finally {
    publishing.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchTags()])
  if (isEdit.value) {
    await fetchArticle(route.params.id)
  }
})
</script>

<style scoped>
.article-edit {
  padding: 20px;
}

.edit-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.article-form {
  margin-top: 20px;
}

.category-select {
  width: 200px;
  margin-right: 10px;
}

.tag-select {
  width: 100%;
}

.content-item {
  margin-bottom: 0;
}

.editor-container {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  margin-bottom: 20px;
}

.toolbar-container {
  padding: 8px;
  border-bottom: 1px solid var(--el-border-color);
  background-color: var(--el-fill-color-light);
  display: flex;
  gap: 8px;
}

.editor-wrapper {
  padding: 16px;
}

.editor-wrapper :deep(.el-textarea__inner) {
  border: none;
  padding: 0;
}

.preview-container {
  padding: 16px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  background-color: var(--el-fill-color-light);
  min-height: 200px;
}

.cover-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration);
}

.cover-uploader:hover {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.cover {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
</style> 