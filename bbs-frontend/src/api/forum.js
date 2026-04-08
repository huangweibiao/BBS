import request from './request'

// 获取版块列表
export function getForums() {
  return request({
    url: '/forums',
    method: 'get'
  })
}

// 获取版块详情
export function getForumDetail(forumId) {
  return request({
    url: `/forums/${forumId}`,
    method: 'get'
  })
}

// 创建版块（管理员）
export function createForum(data) {
  return request({
    url: '/admin/forums',
    method: 'post',
    data
  })
}

// 更新版块（管理员）
export function updateForum(forumId, data) {
  return request({
    url: `/admin/forums/${forumId}`,
    method: 'put',
    data
  })
}

// 删除版块（管理员）
export function deleteForum(forumId) {
  return request({
    url: `/admin/forums/${forumId}`,
    method: 'delete'
  })
}