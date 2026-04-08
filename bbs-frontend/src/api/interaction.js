import request from './request'

// 点赞/取消点赞
export function toggleLike(data) {
  return request({
    url: '/like',
    method: 'post',
    data
  })
}

// 收藏/取消收藏
export function toggleFavorite(data) {
  return request({
    url: '/favorite',
    method: 'post',
    data
  })
}

// 获取消息列表
export function getMessages(params) {
  return request({
    url: '/messages',
    method: 'get',
    params
  })
}

// 获取未读消息数量
export function getUnreadCount() {
  return request({
    url: '/messages/unread',
    method: 'get'
  })
}

// 标记消息已读
export function markAsRead(messageId) {
  return request({
    url: `/messages/${messageId}/read`,
    method: 'put'
  })
}

// 标记所有消息已读
export function markAllAsRead() {
  return request({
    url: '/messages/read-all',
    method: 'put'
  })
}