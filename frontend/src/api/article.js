import request from './request'

// 获取文章列表
export function getArticles(params) {
  return request.get('/articles', { params })
}

// 获取文章详情
export function getArticle(id) {
  return request.get(`/articles/${id}`)
}

// 创建文章
export function createArticle(data) {
  return request.post('/articles', data)
}

// 更新文章
export function updateArticle(id, data) {
  return request.put(`/articles/${id}`, data)
}

// 删除文章
export function deleteArticle(id) {
  return request.delete(`/articles/${id}`)
}

// 保存草稿
export function saveDraft(data) {
  return request.post('/articles/draft', data)
}

// 获取草稿列表
export function getDrafts(params) {
  return request.get('/articles/drafts', { params })
}

// 获取草稿详情
export function getDraft(id) {
  return request.get(`/articles/drafts/${id}`)
}

// 删除草稿
export function deleteDraft(id) {
  return request.delete(`/articles/drafts/${id}`)
}

// 点赞文章
export function likeArticle(id) {
  return request.post(`/articles/${id}/like`)
}

// 取消点赞
export function unlikeArticle(id) {
  return request.delete(`/articles/${id}/like`)
}

// 收藏文章
export function favoriteArticle(id) {
  return request.post(`/articles/${id}/favorite`)
}

// 取消收藏
export function unfavoriteArticle(id) {
  return request.delete(`/articles/${id}/favorite`)
}

// 获取分类列表
export function getCategories() {
  return request.get('/categories')
}

// 创建分类
export function createCategory(data) {
  return request.post('/categories', data)
}

// 更新分类
export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

// 删除分类
export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}

// 获取标签列表
export function getTags() {
  return request.get('/tags')
}

// 创建标签
export function createTag(data) {
  return request.post('/tags', data)
}

// 更新标签
export function updateTag(id, data) {
  return request.put(`/tags/${id}`, data)
}

// 删除标签
export function deleteTag(id) {
  return request.delete(`/tags/${id}`)
}

// 获取文章评论列表
export function getArticleComments(articleId, params) {
  return request.get(`/articles/${articleId}/comments`, { params })
}

// 发表评论
export function createComment(articleId, data) {
  return request.post(`/articles/${articleId}/comments`, data)
}

// 删除评论
export function deleteComment(articleId, commentId) {
  return request.delete(`/articles/${articleId}/comments/${commentId}`)
}

// 回复评论
export function replyComment(articleId, commentId, data) {
  return request.post(`/articles/${articleId}/comments/${commentId}/replies`, data)
}

// 获取相关文章
export function getRelatedArticles(articleId) {
  return request.get(`/articles/${articleId}/related`)
}

// 获取热门文章
export function getHotArticles() {
  return request.get('/articles/hot')
}

// 获取推荐文章
export function getRecommendedArticles() {
  return request.get('/articles/recommended')
}

// 搜索文章
export function searchArticles(params) {
  return request.get('/articles/search', { params })
} 