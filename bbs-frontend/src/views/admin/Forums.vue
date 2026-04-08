<template>
  <div class="admin-forums">
    <h2 class="page-title">版块管理</h2>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="showCreateDialog">创建版块</el-button>
    </div>
    
    <!-- 版块列表 -->
    <el-table :data="forums" v-loading="loading" stripe row-key="id">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="版块名称" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="topicCount" label="主题数" width="80" />
      <el-table-column prop="postCount" label="帖子数" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '开放' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="showEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑版块' : '创建版块'"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="版块名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入版块名称" />
        </el-form-item>
        <el-form-item label="版块描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入版块描述" />
        </el-form-item>
        <el-form-item label="父版块">
          <el-select v-model="form.parentId" placeholder="顶级版块" clearable>
            <el-option
              v-for="forum in parentForums"
              :key="forum.id"
              :label="forum.name"
              :value="forum.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getForums, createForum, updateForum, deleteForum } from '@/api/forum'
import { ElMessage, ElMessageBox } from 'element-plus'

const forums = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({
  name: '',
  description: '',
  parentId: 0,
  sortOrder: 0,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入版块名称', trigger: 'blur' }
  ]
}

// 可作为父级筛选的版块
const parentForums = ref([])

// 加载版块列表
const loadForums = async () => {
  loading.value = true
  try {
    const res = await getForums()
    forums.value = res.data || []
    // 扁平化用于父级选择
    parentForums.value = forums.value.filter(f => !f.parentId || f.parentId === 0)
  } catch (error) {
    ElMessage.error('获取版块列表失败')
  } finally {
    loading.value = false
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  Object.assign(form, {
    name: '',
    description: '',
    parentId: 0,
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, {
    name: row.name,
    description: row.description,
    parentId: row.parentId || 0,
    sortOrder: row.sortOrder,
    status: row.status
  })
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!form.name) {
    ElMessage.warning('请输入版块名称')
    return
  }
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateForum(currentId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createForum(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadForums()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除版块 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteForum(row.id)
    ElMessage.success('删除成功')
    loadForums()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadForums()
})
</script>

<style scoped>
.admin-forums {
  width: 100%;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.action-bar {
  margin-bottom: 20px;
}
</style>