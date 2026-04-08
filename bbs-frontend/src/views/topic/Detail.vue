<template>
  <div class="topic-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">BBS论坛</router-link>
          <span class="separator">/</span>
          <router-link :to="`/forum/${topic?.forumId}/topics`" class="current-forum">
            {{ topic?.forumName }}
          </router-link>
        </div>
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.avatar">
                  {{ userStore.nickname?.charAt(0) }}
                </el-avatar>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="messages">我的消息</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login"><el-button>登录</el-button></router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main">
      <div class="container">
        <!-- 帖子内容 -->
        <div class="topic-content">
          <!-- 帖子头部 -->
          <div class="topic-header">
            <div class="topic-tags">
              <el-tag v-if="topic?.isTop === 1" type="danger">全局置顶</el-tag>
              <el-tag v-else-if="topic?.isTop === 2" type="warning">版块置顶</el-tag>
              <el-tag v-if="topic?.isEssence === 1" type="success">精华</el-tag>
              <el-tag v-if="topic?.isLock === 1" type="info">锁定</el-tag>
            </div>
            <h1 class="topic-title">{{ topic?.title }}</h1>
            <div class="topic-meta">
              <span class="author">
                <el-avatar :size="32" :src="topic?.avatar">{{ topic?.nickname?.charAt(0) }}</el-avatar>
                <span class="name">{{ topic?.nickname }}</span>
                <span class="username">@{{ topic?.username }}</span>
              </span>
              <span class="time">发布于 {{ formatTime(topic?.createdAt) }}</span>
              <span class="stats">
                <el-icon><View /></el-icon> {{ topic?.viewCount }}
                <el-icon><ChatDotRound /></el-icon> {{ topic?.replyCount }}
                <el-icon><Star /></el-icon> {{ topic?.likeCount }}
              </span>
            </div>
            <!-- 管理操作 -->
            <div v-if="userStore.isAdmin || (userStore.userId === topic?.userId)" class="topic-actions">
              <el-button v-if="userStore.isAdmin" size="small" @click="handleSetTop">
                {{ topic?.isTop ? '取消置顶' : '设为置顶' }}
              </el-button>
              <el-button v-if="userStore.isAdmin" size="small" @click="handleSetEssence">
                {{ topic?.isEssence ? '取消精华' : '设为精华' }}
              </el-button>
              <el-button v-if="userStore.isAdmin" size="small" @click="handleSetLock">
                {{ topic?.isLock ? '解锁' : '锁定' }}
              </el-button>
              <el-button v-if="userStore.userId === topic?.userId" size="small" type="primary" @click="goToEdit">
                编辑
              </el-button>
              <el-button v-if="userStore.userId === topic?.userId || userStore.isAdmin" size="small" type="danger" @click="handleDelete">
                删除
              </el-button>
            </div>
          </div>
          
          <!-- 帖子正文 -->
          <div class="topic-body" v-html="topic?.content"></div>
          
          <!-- 帖子操作 -->
          <div class="topic-footer">
            <el-button :type="hasLiked ? 'primary' : 'default'" :icon="Star" circle @click="handleLike" />
            <el-button :type="hasFavorited ? 'warning' : 'default'" :icon="StarFilled" circle @click="handleFavorite" />
          </div>
        </div>

        <!-- 回复列表 -->
        <div class="replies-section">
          <h2 class="section-title">回复列表 ({{ replies.length }})</h2>
          
          <!-- 回复输入框 -->
          <div v-if="userStore.isLoggedIn && topic?.isLock !== 1" class="reply-form">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="4"
              placeholder="写下你的回复..."
            />
            <el-button type="primary" :loading="replyLoading" @click="submitReply">
              提交回复
            </el-button>
          </div>
          <div v-else-if="topic?.isLock === 1" class="reply-tip">
            帖子已锁定，无法回复
          </div>
          <div v-else class="reply-tip">
            <router-link to="/login">登录</router-link>后参与回复
          </div>
          
          <!-- 回复列表 -->
          <div v-if="replies.length === 0" class="empty-replies">
            暂无回复，快来抢沙发吧
          </div>
          
          <div v-else class="replies-list">
            <div v-for="reply in replies" :key="reply.id" class="reply-item">
              <!-- 一级回复 -->
              <div class="reply-main">
                <div class="reply-author">
                  <el-avatar :size="36" :src="reply.avatar">{{ reply.nickname?.charAt(0) }}</el-avatar>
                  <div class="author-info">
                    <span class="nickname">{{ reply.nickname }}</span>
                    <span class="time">{{ formatTime(reply.createdAt) }}</span>
                  </div>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div class="reply-actions">
                  <el-button size="small" text @click="handleReplyLike(reply)">
                    <Star /> {{ reply.likeCount }}
                  </el-button>
                  <el-button v-if="userStore.isLoggedIn" size="small" text @click="showReplyInput(reply)">
                    回复
                  </el-button>
                  <el-button v-if="userStore.userId === reply.userId || userStore.isAdmin" size="small" text type="danger" @click="handleDeleteReply(reply.id)">
                    删除
                  </el-button>
                </div>
              </div>
              
              <!-- 子回复（楼中楼） -->
              <div v-if="reply.children && reply.children.length > 0" class="reply-children">
                <div v-for="child in reply.children" :key="child.id" class="reply-child">
                  <span class="child-author">{{ child.nickname }}</span>
                  <span v-if="child.replyToUsername">回复 @{{ child.replyToUsername }}：</span>
                  <span class="child-content">{{ child.content }}</span>
                  <div class="child-actions">
                    <span class="child-time">{{ formatTime(child.createdAt) }}</span>
                    <el-button v-if="userStore.isLoggedIn" size="small" text @click="showReplyInput(reply, child)">回复</el-button>
                  </div>
                </div>
              </div>
              
              <!-- 二级回复输入框 -->
              <div v-if="reply.showReplyInput" class="child-reply-form">
                <el-input v-model="reply.childContent" placeholder="写下你的回复..." />
                <el-button size="small" type="primary" @click="submitChildReply(reply)">提交</el-button>
                <el-button size="small" @click="reply.showReplyInput = false">取消</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getTopicDetail, createTopic, updateTopic, deleteTopic, setTopicTop, setTopicEssence, setTopicLock } from '@/api/topic'
import { getReplies, createReply, deleteReply } from '@/api/reply'
import { toggleLike, toggleFavorite } from '@/api/interaction'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, ChatDotRound, Star, StarFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const topicId = ref(route.params.topicId)
const topic = ref(null)
const replies = ref([])
const loading = ref(false)
const replyContent = ref('')
const replyLoading = ref(false)
const hasLiked = ref(false)
const hasFavorited = ref(false)

// 加载帖子详情
const loadTopic = async () => {
  loading.value = true
  try {
    const res = await getTopicDetail(topicId.value)
    topic.value = res.data
  } catch (error) {
    ElMessage.error('获取帖子详情失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 加载回复列表
const loadReplies = async () => {
  try {
    const res = await getReplies(topicId.value)
    replies.value = res.data?.list || []
  } catch (error) {
    console.error('获取回复列表失败', error)
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString()
}

// 点赞
const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    await toggleLike({ targetType: 1, targetId: topicId.value })
    hasLiked.value = !hasLiked.value
    ElMessage.success(hasLiked.value ? '点赞成功' : '取消点赞成功')
    loadTopic()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

// 收藏
const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    await toggleFavorite({ postId: topicId.value })
    hasFavorited.value = !hasFavorited.value
    ElMessage.success(hasFavorited.value ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    await createReply(topicId.value, { content: replyContent.value })
    ElMessage.success('回复成功')
    replyContent.value = ''
    loadReplies()
    loadTopic()
  } catch (error) {
    ElMessage.error(error.message || '回复失败')
  } finally {
    replyLoading.value = false
  }
}

// 显示回复输入框
const showReplyInput = (reply, child) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  reply.showReplyInput = true
  reply.childReplyToUserId = child?.userId
  reply.childParentReplyId = child ? child.id : reply.id
}

// 提交子回复
const submitChildReply = async (reply) => {
  if (!reply.childContent?.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await createReply(topicId.value, {
      content: reply.childContent,
      parentReplyId: reply.childParentReplyId,
      replyToUserId: reply.childReplyToUserId
    })
    ElMessage.success('回复成功')
    reply.showReplyInput = false
    reply.childContent = ''
    loadReplies()
    loadTopic()
  } catch (error) {
    ElMessage.error(error.message || '回复失败')
  }
}

// 删除回复
const handleDeleteReply = async (replyId) => {
  try {
    await ElMessageBox.confirm('确定删除这条回复吗？', '提示', { type: 'warning' })
    await deleteReply(replyId)
    ElMessage.success('删除成功')
    loadReplies()
    loadTopic()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 管理操作
const handleSetTop = async () => {
  try {
    const topType = topic.value.isTop ? 0 : 1
    await setTopicTop(topicId.value, topType)
    ElMessage.success(topType ? '置顶成功' : '取消置顶成功')
    loadTopic()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const handleSetEssence = async () => {
  try {
    const isEssence = topic.value.isEssence ? 0 : 1
    await setTopicEssence(topicId.value, isEssence)
    ElMessage.success(isEssence ? '设为精华成功' : '取消精华成功')
    loadTopic()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const handleSetLock = async () => {
  try {
    const isLock = topic.value.isLock ? 0 : 1
    await setTopicLock(topicId.value, isLock)
    ElMessage.success(isLock ? '锁定成功' : '解锁成功')
    loadTopic()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

// 编辑帖子
const goToEdit = () => {
  router.push(`/topic/${topicId.value}/edit`)
}

// 删除帖子
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定删除这篇帖子吗？', '提示', { type: 'warning' })
    await deleteTopic(topicId.value)
    ElMessage.success('删除成功')
    router.push(`/forum/${topic.value.forumId}/topics`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleReplyLike = async (reply) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    await toggleLike({ targetType: 2, targetId: reply.id })
    loadReplies()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'messages':
      router.push('/messages')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      break
  }
}

onMounted(() => {
  loadTopic()
  loadReplies()
})
</script>

<style scoped>
.topic-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部 */
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.separator {
  color: #ccc;
}

.current-forum {
  color: #666;
  text-decoration: none;
}

/* 主体 */
.main {
  padding: 20px 0;
}

/* 帖子内容 */
.topic-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
}

.topic-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 16px;
  margin-bottom: 16px;
}

.topic-tags {
  margin-bottom: 12px;
}

.topic-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  line-height: 1.5;
  margin-bottom: 16px;
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #999;
  font-size: 14px;
}

.topic-meta .author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.topic-meta .name {
  color: #333;
  font-weight: 500;
}

.topic-meta .username {
  color: #999;
}

.topic-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.topic-body {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  padding: 20px 0;
}

.topic-footer {
  border-top: 1px solid #eee;
  padding-top: 16px;
  display: flex;
  gap: 12px;
}

/* 回复区域 */
.replies-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.reply-form {
  margin-bottom: 24px;
}

.reply-form .el-button {
  margin-top: 12px;
}

.reply-tip {
  text-align: center;
  color: #999;
  padding: 20px;
  margin-bottom: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

.reply-tip a {
  color: #409eff;
}

.empty-replies {
  text-align: center;
  color: #999;
  padding: 40px;
}

.replies-list {
  margin-top: 20px;
}

.reply-item {
  border-bottom: 1px solid #eee;
  padding: 16px 0;
}

.reply-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-info .nickname {
  font-weight: 500;
  color: #333;
}

.author-info .time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  padding-left: 48px;
  color: #333;
  line-height: 1.6;
}

.reply-actions {
  padding-left: 48px;
  display: flex;
  gap: 12px;
}

/* 子回复 */
.reply-children {
  margin-left: 48px;
  margin-top: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 4px;
}

.reply-child {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.reply-child:last-child {
  border-bottom: none;
}

.child-author {
  color: #409eff;
  font-weight: 500;
  margin-right: 8px;
}

.child-content {
  color: #333;
}

.child-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
}

.child-time {
  font-size: 12px;
  color: #999;
}

.child-reply-form {
  margin-left: 48px;
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.child-reply-form .el-input {
  flex: 1;
}
</style>