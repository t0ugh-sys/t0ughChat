import request from '@/utils/request'

// 文章相关接口
export function getArticles(params) {
  return request({
    url: '/admin/articles',
    method: 'get',
    params
  })
}

export function getArticle(id) {
  return request({
    url: `/admin/articles/${id}`,
    method: 'get'
  })
}

export function createArticle(data) {
  return request({
    url: '/admin/articles',
    method: 'post',
    data
  })
}

export function updateArticle(id, data) {
  return request({
    url: `/admin/articles/${id}`,
    method: 'put',
    data
  })
}

export function deleteArticle(id) {
  return request({
    url: `/admin/articles/${id}`,
    method: 'delete'
  })
}

export function updateArticleStatus(id, status) {
  return request({
    url: `/admin/articles/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function saveDraft(data) {
  return request({
    url: '/admin/articles/draft',
    method: 'post',
    data
  })
}

// 分类相关接口
export function getCategories() {
  return request({
    url: '/admin/categories',
    method: 'get'
  })
}

export function createCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'post',
    data
  })
}

// 标签相关接口
export function getTags() {
  return request({
    url: '/admin/tags',
    method: 'get'
  })
} 