<template>
  <div>
    <div class="page-title">交易监控</div>

    <!-- TC-ADMIN-007：查看所有交易，支持状态筛选和关键词搜索 -->
    <div class="search-bar">
      <el-select v-model="filterStatus" placeholder="交易状态" clearable style="width:130px"
                 @change="loadTrans">
        <el-option label="全部" :value="null" />
        <el-option label="待确认" :value="1" />
        <el-option label="已确认" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
        <el-option label="纠纷中" :value="5" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索书名/ISBN" clearable style="width:240px"
                @keyup.enter="doSearch" />
      <el-button type="primary" @click="doSearch">搜索</el-button>
    </div>

    <el-table :data="transactions" border stripe v-loading="loading">
      <el-table-column prop="id" label="交易ID" width="80" />
      <el-table-column prop="bookId" label="教材ID" width="80" />
      <el-table-column prop="requesterId" label="买家ID" width="80" />
      <el-table-column prop="ownerId" label="卖家ID" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.transStatus)" size="small">
            {{ statusLabel(row.transStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="transTime" label="发起时间" width="160">
        <template #default="{ row }">{{ formatTime(row.transTime) }}</template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.transStatus !== 3 && row.transStatus !== 4"
                     size="small" type="danger"
                     @click="openCancel(row)">强制取消</el-button>
          <el-button v-if="row.transStatus === 2"
                     size="small" type="warning"
                     @click="markDispute(row.id)">标记纠纷</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
                     layout="total, prev, pager, next" @current-change="loadTrans" />
    </div>

    <!-- 强制取消弹窗 -->
    <el-dialog v-model="cancelDialogVisible" title="强制取消交易" width="380px">
      <el-input v-model="cancelReason" placeholder="请填写取消原因" />
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api/admin.js'

const transactions = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const filterStatus = ref(null)
const keyword = ref('')

const cancelDialogVisible = ref(false)
const cancelReason = ref('')
const cancelTarget = ref(null)

onMounted(() => loadTrans())

async function loadTrans() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (filterStatus.value !== null) params.status = filterStatus.value
    if (keyword.value) params.keyword = keyword.value
    const res = await adminApi.transactionList(params)
    if (res.code === 200) {
      transactions.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

function doSearch() {
  page.value = 1
  loadTrans()
}

function openCancel(row) {
  cancelTarget.value = row
  cancelReason.value = ''
  cancelDialogVisible.value = true
}

async function confirmCancel() {
  const res = await adminApi.cancelTransaction(cancelTarget.value.id, cancelReason.value)
  if (res.code === 200) {
    ElMessage.success('取消成功')
    cancelDialogVisible.value = false
    loadTrans()
  } else {
    ElMessage.error(res.msg)
  }
}

async function markDispute(id) {
  const res = await adminApi.markDispute(id)
  if (res.code === 200) {
    ElMessage.success('已标记为纠纷中')
    loadTrans()
  }
}

function statusLabel(s) {
  return { 1: '待确认', 2: '已确认', 3: '已完成', 4: '已取消', 5: '纠纷中' }[s] || '-'
}
function statusTag(s) {
  return { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[s] || 'info'
}
function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}
</script>

<style scoped>
.page-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
