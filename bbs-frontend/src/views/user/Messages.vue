<template>
  <div class="messages-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">BBS论坛</router-link>
          <span class="separator">/</span>
          <span class="current-page">消息通知</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.avatar">
                {{ userStore.nickname?.charAt(0) }}
              </el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="home">返回首页</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main">
      <div class="container">
        <div class="messages-content">
          <div class="messages-header">
            <h1 class="page-title">消息通知</h1>
            <el-button type="primary" text @click="handleMarkAllRead" :disabled="unreadCount === 0">
              全部标记已读
            </el-button>
          </div>
          
          <!-- 消息类型筛选 -->
          <div class="filter-tabs">
            <el-radio-group v-model="type" @change="loadMessages">
              <el-radio-button :value="undefined">全部</el-radio-button>
              <el-radio-button :value="1">系统通知</el-radio-button>
              <el-radio-button :value="2">@提醒</el-radio-button>
              <el-radio-button :value="3">回复提醒</el-radio-button>
              <el-radio-button :value="4">私信</el-radio-button>
            </el-radio-group>
          </div>
          
          <!-- 消息列表 -->
          <div v-if="loading" class="loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            加载中...
          </div>
          
          <div v-else-if="messages.length === 0" class="empty">
            <el-icon :size="60"><Bell /></el-icon>
            <p>暂无消息</p>
          </div>
          
          <div v-else class="messages-list">
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="message-item"
              :class="{ unread: msg.isRead === 0 }"
              @click="handleRead(msg)"
            >
              <div class="message-icon">
                <el-icon :size="24">
                  <Bell v-if="msg.type === 1" />
                  <ChatDotRound v-else-if="msg.type === 2 || msg.type === 3" />
                  <Message v-else-if="msg.type === 4" />
                </el-icon>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="message-title">{{ msg.title }}</span>
                  <el-tag size="small" :type="getTagType(msg.type)">{{ msg.typeName }}</el-tag>
                </div>
                <p class="message-text">{{ msg.content }}</p>
                <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
              </div>
              <div v-if="msg.isRead === 0" class="unread-dot"></div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="messages.length > 0" class="pagination">
            <el-pagination
              v-model:current-page="pageNum"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="loadMessages"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMessages, markAsRead, markAllAsRead, getUnreadCount } from '@/api/interaction'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Bell, ChatDotRound, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const messages = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const type = ref(undefined)
const unreadCount = ref(0)

// 加载消息列表
const loadMessages = async () => {
  loading.value = true
  try {
    const res = await getMessages({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: type.value
    })
    messages.value = res.data?.list || []
    total.value = res.data?.list?.length || 0
    unreadCount.value = res.data?.unreadCount || 0
  } catch (error) {
    ElMessage.error('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

// 获取未读数量
const loadUnreadCount = async () => {
  try {
    unreadCount.value = await getUnreadCount()
  } catch (error) {
    console.error('获取未读数量失败', error)
  }
}

// 标记已读
const handleRead = async (msg) => {
  if (msg.isRead === 0) {
    try {
      await markAsRead(msg.id)
      msg.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('标记已读失败', error)
    }
  }
}

// 全部标记已读
const handleMarkAllRead = async () => {
  try {
    await markAllAsRead()
    ElMessage.success('全部已标记为已读')
    loadMessages()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
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

// 获取标签类型
const getTagType = (type) => {
  const types = {
    1: '',      // 系统通知 - 默认
    2: 'warning', // @提醒
    3: 'success', // 回复提醒
    4: 'info'    // 私信
  }
  return types[type] || ''
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'home':
      router.push('/')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 900px;
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

.current-page {
  color: #333;
}

/* 主体 */
.main {
  padding: 20px 0;
}

.messages-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.messages-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.filter-tabs {
  margin-bottom: 20px;
}

.loading, .empty {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty p {
  margin-top: 16px;
}

.messages-list {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.message-item:last-child {
  border-bottom: none;
}

.message-item:hover {
  background: #f9f9f9;
}

.message-item.unread {
  background: #f0f9ff;
}

.message-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e6f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
  margin-right: 12px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.message-title {
  font-weight: 500;
  color: #333;
}

.message-text {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f56c6c;
  position: absolute;
  top: 50%;
  right: 16px;
  transform: translateY(-50%);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>