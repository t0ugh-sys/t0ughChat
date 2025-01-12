import request from './request'

// 获取用户统计信息
export function getUserStats(userId) {
  return request.get(`/users/${userId}/stats`)
}

// 获取用户文章列表
export function getArticlesByUser(userId, params) {
  return request.get(`/users/${userId}/articles`, { params })
}

// 获取用户评论列表
export function getUserComments(userId, params) {
  return request.get(`/users/${userId}/comments`, { params })
}

// 上传头像
export function uploadAvatar(formData) {
  return request.post('/users/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 关注用户
export function followUser(userId) {
  return request.post(`/users/${userId}/follow`)
}

// 取消关注用户
export function unfollowUser(userId) {
  return request.delete(`/users/${userId}/follow`)
}

// 获取用户关注列表
export function getFollowings(userId, params) {
  return request.get(`/users/${userId}/followings`, { params })
}

// 获取用户粉丝列表
export function getFollowers(userId, params) {
  return request.get(`/users/${userId}/followers`, { params })
}

// 获取用户收藏的文章列表
export function getFavoriteArticles(userId, params) {
  return request.get(`/users/${userId}/favorites`, { params })
}

// 获取用户点赞的文章列表
export function getLikedArticles(userId, params) {
  return request.get(`/users/${userId}/likes`, { params })
} 