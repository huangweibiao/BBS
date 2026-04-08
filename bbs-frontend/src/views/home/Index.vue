<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">BBS论坛</router-link>
        </div>
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.avatar">
                  {{ userStore.nickname?.charAt(0) }}
                </el-avatar>
                <span class="username">{{ userStore.nickname }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="messages">我的消息</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin">管理后台</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login">
              <el-button>登录</el-button>
            </router-link>
            <router-link to="/register">
              <el-button type="primary">注册</el-button>
            </router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main">
      <div class="container">
        <!-- 热门帖子 -->
        <div class="section">
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            热门帖子
          </h2>
          <div class="hot-topics">
            <div
              v-for="topic in hotTopics"
              :key="topic.id"
              class="hot-topic-item"
              @click="goToTopic(topic.id)"
            >
              <span class="topic-title">{{ topic.title }}</span>
              <span class="topic-replies">{{ topic.replyCount }}回复</span>
            </div>
          </div>
        </div>

        <!-- 版块列表 -->
        <div class="section">
          <h2 class="section-title">
            <el-icon><Grid /></el-icon>
            版块列表
          </h2>
          <div class="forums-grid">
            <div
              v-for="forum in forums"
              :key="forum.id"
              class="forum-card"
              @click="goToForum(forum.id)"
            >
              <div class="forum-icon">
                <el-icon :size="40"><Folder /></el-icon>
              </div>
              <div class="forum-info">
                <h3 class="forum-name">{{ forum.name }}</h3>
                <p class="forum-desc">{{ forum.description || '暂无描述' }}</p>
                <div class="forum-stats">
                  <span>{{ forum.topicCount }} 主题</span>
                  <span>{{ forum.postCount }} 帖子</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 发帖按钮 -->
        <div class="action-bar">
          <router-link v-if="userStore.isLoggedIn" to="/topic/create">
            <el-button type="primary" size="large" :icon="Edit">
              发布帖子
            </el-button>
          </router-link>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 BBS论坛系统 - 基于Spring Boot + Vue 3</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useForumStore } from '@/stores/forum'
import { getHotTopics } from '@/api/topic'
import { ElMessage } from 'element-plus'
import { Star, Grid, Folder, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const forumStore = useForumStore()

const hotTopics = ref([])
const forums = ref([])

onMounted(async () => {
  // 获取用户信息
  if (userStore.token) {
    await userStore.fetchUserInfo()
  }
  
  // 获取版块列表
  await forumStore.fetchForums()
  forums.value = forumStore.forums
  
  // 获取热门帖子
  try {
    const res = await getHotTopics({ pageNum: 1, pageSize: 10 })
    hotTopics.value = res.data || []
  } catch (error) {
    console.error('获取热门帖子失败', error)
  }
})

const goToForum = (forumId) => {
  router.push(`/forum/${forumId}/topics`)
}

const goToTopic = (topicId) => {
  router.push(`/topic/${topicId}`)
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'messages':
      router.push('/messages')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      break
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
  position: sticky;
  top: 0;
  z-index: 100;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  color: #333;
}

/* 主体 */
.main {
  flex: 1;
  padding: 20px 0;
}

.section {
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  margin-bottom: 16px;
  color: #333;
}

/* 热门帖子 */
.hot-topics {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.hot-topic-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-topic-item:last-child {
  border-bottom: none;
}

.hot-topic-item:hover .topic-title {
  color: #409eff;
}

.topic-title {
  color: #333;
  font-size: 14px;
}

.topic-replies {
  color: #999;
  font-size: 12px;
}

/* 版块列表 */
.forums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.forum-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  gap: 16px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.forum-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.forum-icon {
  color: #409eff;
}

.forum-info {
  flex: 1;
}

.forum-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.forum-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.forum-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

/* 操作栏 */
.action-bar {
  text-align: center;
  margin-top: 20px;
}

/* 页脚 */
.footer {
  background: #fff;
  padding: 20px 0;
  text-align: center;
  color: #999;
  margin-top: 40px;
}
</style>