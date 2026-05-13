<template>
  <div>
    <div class="page-title">用户管理</div>

    <!-- 搜索栏（TC-ADMIN-001） -->
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/姓名/手机/邮箱"
                clearable style="width:300px" @keyup.enter="doSearch" />
      <el-button type="primary" @click="doSearch">搜索</el-button>
      <el-button @click="keyword=''; doSearch()">重置</el-button>
    </div>

    <el-table :data="users" border stripe style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="major" label="专业" />
      <el-table-column prop="phone" label="手机" width="120" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.userType === 2 ? 'danger' : 'primary'" size="small">
            {{ row.userType === 2 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <!-- TC-ADMIN-002：提升为管理员 -->
          <el-button v-if="row.userType === 1" size="small" type="warning"
                     @click="promoteAdmin(row)">提升为管理员</el-button>
          <el-button v-else size="small" @click="demoteUser(row)">降为普通用户</el-button>
          <!-- TC-ADMIN-003：禁用/启用 -->
          <el-button v-if="row.status === 1" size="small" type="danger"
                     @click="toggleStatus(row, 0)">禁用</el-button>
          <el-button v-else size="small" type="success"
                     @click="toggleStatus(row, 1)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
                     layout="total, prev, pager, next" @current-change="loadUsers" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api/admin.js'

const users = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const keyword = ref('')

onMounted(() => loadUsers())

async function loadUsers() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    const res = await adminApi.userList(params)
    if (res.code === 200) {
      users.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

function doSearch() {
  page.value = 1
  loadUsers()
}

async function promoteAdmin(row) {
  try {
    await ElMessageBox.confirm(`确认将用户 ${row.realName} 提升为管理员？`, '提示', { type: 'warning' })
    const res = await adminApi.updateUserType({ userId: row.id, userType: 2 })
    if (res.code === 200) {
      ElMessage.success('更新成功，用户类型变更')
      loadUsers()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {}
}

async function demoteUser(row) {
  const res = await adminApi.updateUserType({ userId: row.id, userType: 1 })
  if (res.code === 200) {
    ElMessage.success('已降为普通用户')
    loadUsers()
  }
}

async function toggleStatus(row, status) {
  const label = status === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确认${label}用户 ${row.realName}？`, '提示', { type: 'warning' })
    const res = await adminApi.updateUserStatus({ userId: row.id, status })
    if (res.code === 200) {
      ElMessage.success(`${label}成功`)
      loadUsers()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {}
}
</script>

<style scoped>
.page-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
