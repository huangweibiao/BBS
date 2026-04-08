import request from './request'

// 获取版块帖子列表
export function getTopics(forumId, params) {
  return request({
    url: `/forums/${forumId}/topics`,
    method: 'get',
    params
  })
}

// 获取热门帖子
export function getHotTopics(params) {
  return request({
    url: '/topics/hot',
    method: 'get',
    params
  })
}

// 获取帖子详情
export function getTopicDetail(topicId) {
  return request({
    url: `/topics/${topicId}`,
    method: 'get'
  })
}

// 创建帖子
export function createTopic(data) {
  return request({
    url: '/topics',
    method: 'post',
    data
  })
}

// 更新帖子
export function updateTopic(topicId, data) {
  return request({
    url: `/topics/${topicId}`,
    method: 'put',
    data
  })
}

// 删除帖子
export function deleteTopic(topicId) {
  return request({
    url: `/topics/${topicId}`,
    method: 'delete'
  })
}

// 获取版块详情
export function getForumDetail(forumId) {
  return request({
    url: `/forums/${forumId}`,
    method: 'get'
  })
}

// 帖子置顶（管理员）
export function setTopicTop(topicId, topType) {
  return request({
    url: `/admin/topics/${topicId}/top`,
    method: 'put',
    params: { topType }
  })
}

// 帖子加精（管理员）
export function setTopicEssence(topicId, isEssence) {
  return request({
    url: `/admin/topics/${topicId}/essence`,
    method: 'put',
    params: { isEssence }
  })
}

// 帖子锁定（管理员）
export function setTopicLock(topicId, isLock) {
  return request({
    url: `/admin/topics/${topicId}/lock`,
    method: 'put',
    params: { isLock }
  })
}