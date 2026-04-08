<template>
  <div class="admin-users">
    <h2 class="page-title">用户管理</h2>
    
    <!-- 搜索 -->
    <div class="search-bar">
      <el-input v-model="search" placeholder="搜索用户名/邮箱" clearable @change="loadUsers">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    
    <!-- 用户列表 -->
    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="getRoleTagType(row.role)">
            {{ getRoleName(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分" width="80" />
      <el-table-column prop="postCount" label="发帖" width="80" />
      <el-table-column prop="replyCount" label="回帖" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="120">
        <template #default="{ row }">
          {{ formatTime(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.role < 2"
            size="small"
            :type="row.status === 1 ? 'danger' : 'success'"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <span v-else class="readonly">-</span>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadUsers"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserProfile, updateUserProfile } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const users = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const search = ref('')

// 加载用户列表（这里简单模拟，实际需要后端API）
const loadUsers = async () => {
  loading.value = true
  try {
    // 这里需要后端提供用户列表API，暂时模拟
    const res = await getUserProfile()
    users.value = [res.data]
    total.value = 1
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const getRoleName = (role) => {
  const roles = { 0: '普通用户', 1: '版主', 2: '管理员' }
  return roles[role] || '未知'
}

const getRoleTagType = (role) => {
  const types = { 0: '', 1: 'warning', 2: 'danger' }
  return types[role] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString()
}

const handleToggleStatus = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定${user.status === 1 ? '禁用' : '启用'}用户 ${user.nickname} 吗？`,
      '提示',
      { type: 'warning' }
    )
    
    // 这里需要后端提供更新用户状态API
    ElMessage.success(user.status === 1 ? '已禁用' : '已启用')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
  width: 100%;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.search-bar {
  margin-bottom: 20px;
}

.search-bar .el-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.readonly {
  color: #999;
}
</style>