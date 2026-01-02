import api from '@/utils/api'

export const authAPI = {
  // 用户注册
  register(data) {
    return api.post('/auth/register', data)
  },

  // 用户登录
  login(data) {
    return api.post('/auth/login', data)
  },

  // 获取当前用户信息
  getUserInfo() {
    return api.get('/auth/user-info')
  },
}

export const userAPI = {
  // 获取用户信息
  getUser(id) {
    return api.get(`/users/${id}`)
  },

  // 获取个人资料
  getProfile() {
    return api.get('/users/profile')
  },

  // 更新个人资料
  updateProfile(data) {
    return api.put('/users/profile', data)
  },
}

export const activityAPI = {
  // 创建组队
  createActivity(data) {
    return api.post('/activities', data)
  },

  // 获取活动详情
  getActivity(id) {
    return api.get(`/activities/${id}`)
  },

  // 搜索活动
  searchActivities(params) {
    return api.get('/activities', { params })
  },

  // 获取我的组队
  getMyActivities() {
    return api.get('/activities/my-activities')
  },

  // 更新活动
  updateActivity(id, data) {
    return api.put(`/activities/${id}`, data)
  },

  // 删除活动
  deleteActivity(id) {
    return api.delete(`/activities/${id}`)
  },
}

export const registrationAPI = {
  // 提交报名
  submitRegistration(activityId) {
    return api.post('/registrations', null, { params: { activityId } })
  },

  // 获取我的报名
  getMyRegistrations() {
    return api.get('/registrations/my-registrations')
  },

  // 获取活动的报名列表
  getActivityRegistrations(activityId) {
    return api.get(`/registrations/activity/${activityId}`)
  },

  // 通过报名
  approveRegistration(id) {
    return api.put(`/registrations/${id}/approve`)
  },

  // 驳回报名
  rejectRegistration(id) {
    return api.put(`/registrations/${id}/reject`)
  },

  // 取消报名
  cancelRegistration(id) {
    return api.put(`/registrations/${id}/cancel`)
  },

  // 取消报名 (通过活动ID)
  cancelByActivityId(activityId) {
    return api.post('/registrations/cancel', null, { params: { activityId } })
  },
}
