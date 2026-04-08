<template>
  <div class="topic-list-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">BBS论坛</router-link>
          <span class="separator">/</span>
          <span class="current-forum">{{ forum?.name }}</span>
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
            <router-link to="/register"><el-button type="primary">注册</el-button></router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main">
      <div class="container">
        <!-- 操作栏 -->
        <div class="action-bar">
          <div class="sort-tabs">
            <el-radio-group v-model="sort" @change="loadTopics">
              <el-radio-button value="latest">最新</el-radio-button>
              <el-radio-button value="hot">热门</el-radio-button>
            </el-radio-group>
          </div>
          <router-link v-if="userStore.isLoggedIn" :to="`/topic/create?forumId=${forumId}`">
            <el-button type="primary" :icon="Edit">发布帖子</el-button>
          </router-link>
        </div>

        <!-- 帖子列表 -->
        <div class="topic-list">
          <div v-if="loading" class="loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            加载中...
          </div>
          
          <div v-else-if="topics.length === 0" class="empty">
            <el-icon :size="60"><Document /></el-icon>
            <p>暂无帖子</p>
            <router-link v-if="userStore.isLoggedIn" :to="`/topic/create?forumId=${forumId}`">
              <el-button type="primary">发布第一个帖子</el-button>
            </router-link>
          </div>
          
          <div v-else class="topics">
            <div
              v-for="topic in topics"
              :key="topic.id"
              class="topic-item"
              @click="goToTopic(topic.id)"
            >
              <!-- 置顶/精华标签 -->
              <div class="topic-tags">
                <el-tag v-if="topic.isTop === 1" type="danger" size="small">全局置顶</el-tag>
                <el-tag v-else-if="topic.isTop === 2" type="warning" size="small">版块置顶</el-tag>
                <el-tag v-if="topic.isEssence === 1" type="success" size="small">精华</el-tag>
                <el-tag v-if="topic.isLock === 1" type="info" size="small">锁定</el-tag>
              </div>
              
              <!-- 帖子标题 -->
              <h3 class="topic-title">{{ topic.title }}</h3>
              
              <!-- 帖子信息 -->
              <div class="topic-meta">
                <span class="author">
                  <el-avatar :size="20" :src="topic.avatar">{{ topic.nickname?.charAt(0) }}</el-avatar>
                  {{ topic.nickname }}
                </span>
                <span class="time">{{ formatTime(topic.createdAt) }}</span>
                <span class="stats">
                  <el-icon><View /></el-icon> {{ topic.viewCount }}
                  <el-icon><ChatDotRound /></el-icon> {{ topic.replyCount }}
                  <el-icon><Star /></el-icon> {{ topic.likeCount }}
                </span>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="topics.length > 0" class="pagination">
            <el-pagination
              v-model:current-page="pageNum"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="loadTopics"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getTopics, getForumDetail } from '@/api/topic'
import { ElMessage } from 'element-plus'
import { Edit, Loading, Document, View, ChatDotRound, Star } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const forumId = ref(route.params.forumId)
const forum = ref(null)
const topics = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const sort = ref('latest')

// 加载版块信息
const loadForum = async () => {
  try {
    const res = await getForumDetail(forumId.value)
    forum.value = res.data
  } catch (error) {
    console.error('获取版块信息失败', error)
  }
}

// 加载帖子列表
const loadTopics = async () => {
  loading.value = true
  try {
    const res = await getTopics(forumId.value, {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      sort: sort.value
    })
    topics.value = res.data?.list || []
    total.value = res.data?.list?.length || 0
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
  } finally {
    loading.value = false
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

// 跳转到帖子详情
const goToTopic = (topicId) => {
  router.push(`/topic/${topicId}`)
}

// 路由参数变化时重新加载
watch(() => route.params.forumId, (newId) => {
  if (newId) {
    forumId.value = newId
    loadForum()
    loadTopics()
  }
})

onMounted(() => {
  loadForum()
  loadTopics()
})

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
</script>

<style scoped>
.topic-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
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
  font-size: 16px;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 主体 */
.main {
  padding: 20px 0;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
}

/* 帖子列表 */
.topic-list {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.loading, .empty {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty p {
  margin: 16px 0;
}

.topic-item {
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.topic-item:hover {
  background: #f9f9f9;
}

.topic-item:last-child {
  border-bottom: none;
}

.topic-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.topic-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.5;
}

.topic-item:hover .topic-title {
  color: #409eff;
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.topic-meta .author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.topic-meta .stats {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>