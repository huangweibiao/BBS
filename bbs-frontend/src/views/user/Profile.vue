<template>
  <div class="profile-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">BBS论坛</router-link>
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
                <el-dropdown-item command="home">返回首页</el-dropdown-item>
                <el-dropdown-item command="messages">我的消息</el-dropdown-item>
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
        <div class="profile-content">
          <h1 class="page-title">个人中心</h1>
          
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
            <el-form-item label="头像">
              <el-avatar :size="80" :src="form.avatar">
                {{ form.nickname?.charAt(0) }}
              </el-avatar>
              <el-button size="small" style="margin-left: 16px">更换头像</el-button>
            </el-form-item>
            
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="积分">
              <span class="points">{{ userInfo?.points || 0 }}</span>
              <el-button size="small" type="primary" :loading="signLoading" @click="handleSignIn" style="margin-left: 16px">
                每日签到
              </el-button>
            </el-form-item>
            
            <el-form-item label="注册时间">
              <span>{{ formatTime(userInfo?.createdAt) }}</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleSubmit">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserProfile, updateUserProfile, signIn } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const signLoading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  avatar: ''
})

const userInfo = ref(null)

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

onMounted(async () => {
  try {
    const res = await getUserProfile()
    userInfo.value = res.data
    form.username = res.data.username
    form.nickname = res.data.nickname
    form.email = res.data.email
    form.avatar = res.data.avatar
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
})

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await updateUserProfile({
      nickname: form.nickname,
      email: form.email,
      avatar: form.avatar
    })
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}

const handleSignIn = async () => {
  signLoading.value = true
  try {
    const res = await signIn()
    ElMessage.success(res.data)
    userStore.fetchUserInfo()
    const profileRes = await getUserProfile()
    userInfo.value = profileRes.data
  } catch (error) {
    ElMessage.error(error.message || '签到失败')
  } finally {
    signLoading.value = false
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'home':
      router.push('/')
      break
    case 'messages':
      router.push('/messages')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 800px;
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

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

/* 主体 */
.main {
  padding: 30px 0;
}

.profile-content {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #333;
}

.profile-form {
  max-width: 600px;
}

.points {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}
</style>