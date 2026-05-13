<template>
  <div>
    <AppHeader />
    <div class="page-container">
      <div class="page-title">我的评价</div>

      <el-tabs v-model="activeTab" @tab-change="loadReviews">
        <el-tab-pane label="我发出的评价" name="sent">
          <div v-if="sentList.length === 0" class="empty-tip">暂无发出的评价</div>
          <div v-for="r in sentList" :key="r.id" class="review-card">
            <div class="review-header">
              <span>评价交易ID：{{ r.transId }}</span>
              <el-rate :model-value="r.rating" disabled size="small" />
            </div>
            <div class="review-content">{{ r.content || '（无评价内容）' }}</div>
            <div class="review-time">{{ formatTime(r.reviewTime) }}</div>
            <div class="review-actions">
              <el-button size="small" @click="openEdit(r)">修改</el-button>
              <el-button size="small" type="danger" @click="deleteReview(r.id)">删除</el-button>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我收到的评价" name="received">
          <div v-if="receivedList.length === 0" class="empty-tip">暂无收到的评价</div>
          <div v-for="r in receivedList" :key="r.id" class="review-card">
            <div class="review-header">
              <span>来自交易ID：{{ r.transId }}</span>
              <el-rate :model-value="r.rating" disabled size="small" />
            </div>
            <div class="review-content">{{ r.content || '（无评价内容）' }}</div>
            <div class="review-time">{{ formatTime(r.reviewTime) }}</div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>

  <!-- 修改评价弹窗 -->
  <el-dialog v-model="editDialogVisible" title="修改评价" width="400px">
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="editForm.rating" />
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="editForm.content" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveEdit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '../components/AppHeader.vue'
import { reviewApi } from '../api/review.js'

const activeTab = ref('sent')
const sentList = ref([])
const receivedList = ref([])

const editDialogVisible = ref(false)
const editForm = ref({ id: null, rating: 5, content: '' })

onMounted(() => loadReviews())

async function loadReviews() {
  if (activeTab.value === 'sent') {
    const res = await reviewApi.mySent({ page: 1, pageSize: 20 })
    if (res.code === 200) sentList.value = res.data.list || []
  } else {
    const res = await reviewApi.myReceived({ page: 1, pageSize: 20 })
    if (res.code === 200) receivedList.value = res.data.list || []
  }
}

function openEdit(r) {
  editForm.value = { id: r.id, rating: r.rating, content: r.content }
  editDialogVisible.value = true
}

async function saveEdit() {
  const res = await reviewApi.update(editForm.value.id, {
    rating: editForm.value.rating,
    content: editForm.value.content
  })
  if (res.code === 200) {
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadReviews()
  } else {
    ElMessage.error(res.msg)
  }
}

async function deleteReview(id) {
  try {
    await ElMessageBox.confirm('确认删除此评价？', '提示', { type: 'warning' })
    const res = await reviewApi.delete(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadReviews()
    }
  } catch (e) {}
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}
</script>

<style scoped>
.page-container { max-width: 800px; margin: 20px auto; padding: 0 16px; }
.page-title { font-size: 20px; font-weight: bold; margin-bottom: 16px; }
.review-card {
  background: #fff;
  border-radius: 6px;
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}
.review-content { font-size: 14px; margin-bottom: 6px; }
.review-time { font-size: 12px; color: #999; margin-bottom: 8px; }
.review-actions { display: flex; gap: 8px; }
.empty-tip { text-align: center; padding: 60px; color: #999; }
</style>
