import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function checkUsername(username) {
  return request.get('/auth/check-username', { params: { username } })
}

export function checkEmail(email) {
  return request.get('/auth/check-email', { params: { email } })
}

export function getUserProfile() {
  return request.get('/users/profile')
}

export function updateUserProfile(data) {
  return request.put('/users/profile', data)
}

export function changePassword(data) {
  return request.put('/users/change-password', data)
} 