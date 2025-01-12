<template>
  <div class="edit-article">
    <div class="container">
      <div class="page-header">
        <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
      </div>

      <el-form
        ref="formRef"
        :model="articleForm"
        :rules="rules"
        label-width="100px"
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
            placeholder="请选择文章分类"
            class="category-select"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <el-button type="primary" plain @click="showCategoryDialog">
            新建分类
          </el-button>
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="articleForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入标签"
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
          <div class="editor-toolbar">
            <el-button-group>
              <el-button type="default" @click="handleBold">
                <el-icon><Bold /></el-icon>
              </el-button>
              <el-button type="default" @click="handleItalic">
                <el-icon><Italic /></el-icon>
              </el-button>
              <el-button type="default" @click="handleHeader">
                <el-icon><TopRight /></el-icon>
              </el-button>
            </el-button-group>
            <el-button-group>
              <el-button type="default" @click="handleLink">
                <el-icon><Link /></el-icon>
              </el-button>
              <el-button type="default" @click="handleImage">
                <el-icon><Picture /></el-icon>
              </el-button>
              <el-button type="default" @click="handleCode">
                <el-icon><Terminal /></el-icon>
              </el-button>
            </el-button-group>
            <el-button-group>
              <el-button type="default" @click="handlePreview">
                预览
              </el-button>
            </el-button-group>
          </div>
          <el-input
            v-model="articleForm.content"
            type="textarea"
            :rows="20"
            placeholder="请输入文章内容（支持Markdown格式）"
            v-show="!isPreview"
          />
          <div class="markdown-preview" v-show="isPreview" v-html="renderedContent"></div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '发布文章' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
          <el-button
            v-if="!isEdit"
            type="info"
            @click="handleSaveDraft"
            :loading="savingDraft"
          >
            保存草稿
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 新建分类对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      title="新建分类"
      width="30%"
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
          <el-button type="primary" @click="handleCreateCategory" :loading="creatingCategory">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bold, Italic, TopRight, Link, Picture, Terminal } from '@element-plus/icons-vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import {
  getArticle,
  createArticle,
  updateArticle,
  saveDraft,
  getCategories,
  createCategory,
  getTags
} from '@/api/article'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const submitting = ref(false)
const savingDraft = ref(false)

// 文章表单
const articleForm = ref({
  title: '',
  categoryId: '',
  tags: [],
  summary: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符之间', trigger: 'blur' }
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

// 预览相关
const isPreview = ref(false)
const renderedContent = computed(() => {
  if (!articleForm.value.content) return ''
  const html = marked(articleForm.value.content)
  return DOMPurify.sanitize(html)
})

// 新建分类相关
const categoryDialogVisible = ref(false)
const categoryFormRef = ref(null)
const creatingCategory = ref(false)
const categoryForm = ref({
  name: '',
  description: ''
})

const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '分类名称长度在2-20个字符之间', trigger: 'blur' }
  ]
}

// 获取文章详情
const fetchArticle = async (id) => {
  try {
    const response = await getArticle(id)
    articleForm.value = {
      title: response.title,
      categoryId: response.categoryId,
      tags: response.tags.map(tag => tag.id),
      summary: response.summary,
      content: response.content
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

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await updateArticle(route.params.id, articleForm.value)
      ElMessage.success('文章更新成功')
    } else {
      await createArticle(articleForm.value)
      ElMessage.success('文章发布成功')
    }

    router.push('/articles')
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error(isEdit.value ? '更新失败' : '发布失败')
    }
  } finally {
    submitting.value = false
  }
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    savingDraft.value = true
    await saveDraft(articleForm.value)
    ElMessage.success('草稿保存成功')
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存草稿失败')
  } finally {
    savingDraft.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  ElMessageBox.confirm('确定要离开吗？未保存的内容将会丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      router.back()
    })
    .catch(() => {})
}

// 新建分类
const showCategoryDialog = () => {
  categoryDialogVisible.value = true
  categoryForm.value = {
    name: '',
    description: ''
  }
}

const handleCreateCategory = async () => {
  if (!categoryFormRef.value) return

  try {
    await categoryFormRef.value.validate()
    creatingCategory.value = true

    const response = await createCategory(categoryForm.value)
    categories.value.push(response)
    articleForm.value.categoryId = response.id
    categoryDialogVisible.value = false
    ElMessage.success('分类创建成功')
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('创建分类失败')
    }
  } finally {
    creatingCategory.value = false
  }
}

// Markdown编辑器工具栏功能
const handleBold = () => {
  insertText('**', '**', '加粗文本')
}

const handleItalic = () => {
  insertText('*', '*', '斜体文本')
}

const handleHeader = () => {
  insertText('### ', '', '标题')
}

const handleLink = () => {
  insertText('[', '](url)', '链接文本')
}

const handleImage = () => {
  insertText('![', '](image_url)', '图片描述')
}

const handleCode = () => {
  insertText('```\n', '\n```', '代码块')
}

const handlePreview = () => {
  isPreview.value = !isPreview.value
}

// 在光标位置插入文本
const insertText = (before, after, placeholder) => {
  const textarea = document.querySelector('.content-item textarea')
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = articleForm.value.content
  const selectedText = text.substring(start, end) || placeholder

  articleForm.value.content = 
    text.substring(0, start) +
    before +
    selectedText +
    after +
    text.substring(end)

  // 恢复光标位置
  setTimeout(() => {
    textarea.focus()
    const newCursorPos = start + before.length + selectedText.length
    textarea.setSelectionRange(newCursorPos, newCursorPos)
  })
}

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchTags()])
  if (isEdit.value) {
    await fetchArticle(route.params.id)
  }
})
</script>

<style scoped>
.edit-article {
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.article-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.category-select {
  width: 200px;
  margin-right: 10px;
}

.tag-select {
  width: 100%;
}

.content-item :deep(.el-form-item__content) {
  display: block;
}

.editor-toolbar {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.editor-toolbar .el-button-group {
  margin-right: 10px;
}

.editor-toolbar .el-button {
  padding: 8px 15px;
}

.markdown-preview {
  min-height: 500px;
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
}

.markdown-preview :deep(h1),
.markdown-preview :deep(h2),
.markdown-preview :deep(h3),
.markdown-preview :deep(h4),
.markdown-preview :deep(h5),
.markdown-preview :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-preview :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-preview :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
}

.markdown-preview :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 3px;
}

.markdown-preview :deep(pre code) {
  padding: 0;
  margin: 0;
  font-size: 100%;
  background-color: transparent;
  border: 0;
}

.markdown-preview :deep(img) {
  max-width: 100%;
  box-sizing: content-box;
  background-color: #fff;
}

.markdown-preview :deep(blockquote) {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin: 0 0 16px;
}
</style> 