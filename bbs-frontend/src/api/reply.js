import request from './request'

// 获取帖子回复列表
export function getReplies(topicId) {
  return request({
    url: `/topics/${topicId}/replies`,
    method: 'get'
  })
}

// 创建回复
export function createReply(topicId, data) {
  return request({
    url: `/topics/${topicId}/replies`,
    method: 'post',
    data
  })
}

// 删除回复
export function deleteReply(replyId) {
  return request({
    url: `/replies/${replyId}`,
    method: 'delete'
  })
}