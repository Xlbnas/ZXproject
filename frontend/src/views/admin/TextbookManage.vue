<template>
  <div>
    <div class="page-title">教材管理</div>

    <div class="search-bar">
      <el-select v-model="filterStatus" placeholder="审核状态筛选" clearable style="width:140px"
                 @change="loadTextbooks">
        <el-option label="全部" :value="null" />
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已驳回" :value="2" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索书名/ISBN" clearable style="width:240px"
                @keyup.enter="doSearch" />
      <el-button type="primary" @click="doSearch">搜索</el-button>
    </div>

    <el-table :data="books" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="bookName" label="书名" min-width="150" />
      <el-table-column prop="isbn" label="ISBN" width="140" />
      <el-table-column prop="conditionDesc" label="品相" width="80" />
      <el-table-column prop="expectedPrice" label="期望价" width="80">
        <template #default="{ row }">¥{{ row.expectedPrice }}</template>
      </el-table-column>
      <el-table-column label="审核状态" width="90">
        <template #default="{ row }">
          <el-tag :type="auditTag(row.auditStatus)" size="small">
            {{ auditLabel(row.auditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已上架' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <!-- 待审核时显示审核按钮（TC-ADMIN-004通过，TC-ADMIN-005驳回） -->
          <template v-if="row.auditStatus === 0">
            <el-button size="small" type="success" @click="audit(row, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="openReject(row)">驳回</el-button>
          </template>
          <!-- 已上架时显示下架按钮（TC-ADMIN-006） -->
          <el-button v-if="row.status === 1" size="small" @click="offShelf(row.id)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
                     layout="total, prev, pager, next" @current-change="loadTextbooks" />
    </div>

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="填写驳回原因" width="380px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请填写驳回原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api/admin.js'

const books = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const filterStatus = ref(null)
const keyword = ref('')

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejectTarget = ref(null)

onMounted(() => loadTextbooks())

async function loadTextbooks() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (filterStatus.value !== null) params.auditStatus = filterStatus.value
    if (keyword.value) params.keyword = keyword.value
    const res = await adminApi.textbookList(params)
    if (res.code === 200) {
      books.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

function doSearch() {
  page.value = 1
  loadTextbooks()
}

async function audit(row, status) {
  const res = await adminApi.auditTextbook({ bookId: row.id, auditStatus: status })
  if (res.code === 200) {
    ElMessage.success(res.msg)
    loadTextbooks()
  } else {
    ElMessage.error(res.msg)
  }
}

function openReject(row) {
  rejectTarget.value = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

async function confirmReject() {
  const res = await adminApi.auditTextbook({
    bookId: rejectTarget.value.id,
    auditStatus: 2,
    rejectReason: rejectReason.value
  })
  if (res.code === 200) {
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadTextbooks()
  } else {
    ElMessage.error(res.msg)
  }
}

async function offShelf(id) {
  const res = await adminApi.offShelf(id)
  if (res.code === 200) {
    ElMessage.success('下架成功')
    loadTextbooks()
  } else {
    ElMessage.error(res.msg)
  }
}

function auditLabel(s) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '-'
}
function auditTag(s) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info'
}
</script>

<style scoped>
.page-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
