<template>
  <div class="topic-edit-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-left">
          <router-link to="/" class="logo">BBS论坛</router-link>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main">
      <div class="container">
        <div class="edit-form">
          <h1 class="page-title">编辑帖子</h1>
          
          <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="帖子标题" prop="title">
              <el-input
                v-model="form.title"
                placeholder="请输入帖子标题"
                maxlength="120"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="帖子内容" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="15"
                placeholder="请输入帖子内容，支持Markdown格式"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleSubmit">
                保存修改
              </el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getTopicDetail, updateTopic } from '@/api/topic'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const topicId = ref(route.params.topicId)
const formRef = ref()
const loading = ref(false)

const form = reactive({
  title: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { max: 120, message: '标题长度不能超过120位', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' }
  ]
}

// 加载帖子详情
onMounted(async () => {
  try {
    const res = await getTopicDetail(topicId.value)
    form.title = res.data.title
    form.content = res.data.content
  } catch (error) {
    ElMessage.error('获取帖子信息失败')
    router.back()
  }
})

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await updateTopic(topicId.value, form)
    ElMessage.success('保存成功')
    router.push(`/topic/${topicId.value}`)
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.topic-edit-page {
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

.edit-form {
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
</style>