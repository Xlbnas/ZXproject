<template>
  <div>
    <div class="page-title">公告管理</div>

    <div style="margin-bottom:16px">
      <el-button type="primary" @click="openAdd">+ 发布新公告</el-button>
    </div>

    <!-- TC-ADMIN-007：公告列表 -->
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
      <el-table-column prop="publishTime" label="发布时间" width="160">
        <template #default="{ row }">{{ formatTime(row.publishTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <!-- TC-ADMIN-009：删除公告 -->
          <el-button size="small" type="danger" @click="deleteAnn(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
                     layout="total, prev, pager, next" @current-change="loadList" />
    </div>

    <!-- 发布/编辑弹窗（TC-ADMIN-008） -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="500px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAnn">{{ isEdit ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api/admin.js'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, title: '', content: '' })

onMounted(() => loadList())

async function loadList() {
  loading.value = true
  try {
    const res = await adminApi.announcementList({ page: page.value, pageSize: pageSize.value })
    if (res.code === 200) {
      list.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  form.value = { id: null, title: '', content: '' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { id: row.id, title: row.title, content: row.content }
  dialogVisible.value = true
}

async function submitAnn() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  let res
  if (isEdit.value) {
    res = await adminApi.updateAnnouncement(form.value.id, {
      title: form.value.title,
      content: form.value.content
    })
  } else {
    res = await adminApi.publishAnnouncement({
      title: form.value.title,
      content: form.value.content
    })
  }
  if (res.code === 200) {
    ElMessage.success(res.msg)
    dialogVisible.value = false
    loadList()
  } else {
    ElMessage.error(res.msg)
  }
}

async function deleteAnn(id) {
  try {
    await ElMessageBox.confirm('确认删除该公告？', '提示', { type: 'warning' })
    const res = await adminApi.deleteAnnouncement(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadList()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {}
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}
</script>

<style scoped>
.page-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
