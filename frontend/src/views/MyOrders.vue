<template>
  <div>
    <AppHeader />
    <div class="page-container">
      <div class="page-title">我的订单</div>

      <!-- 状态筛选（TC-ORDER-002） -->
      <div class="filter-bar">
        <el-radio-group v-model="activeStatus" @change="loadOrders">
          <el-radio-button :value="null">全部</el-radio-button>
          <el-radio-button :value="1">待确认</el-radio-button>
          <el-radio-button :value="2">已确认</el-radio-button>
          <el-radio-button :value="3">已完成</el-radio-button>
          <el-radio-button :value="4">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="orders.length === 0" class="empty-tip">暂无订单</div>

      <div class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-id">订单号：{{ order.id }}</span>
            <el-tag :type="statusType(order.transStatus)" size="small">
              {{ statusLabel(order.transStatus) }}
            </el-tag>
          </div>
          <div class="order-body">
            <div class="order-info">
              <div>教材ID：{{ order.bookId }}</div>
              <div>下单时间：{{ formatTime(order.transTime) }}</div>
              <div v-if="order.confirmTime">确认时间：{{ formatTime(order.confirmTime) }}</div>
              <div v-if="order.completeTime">完成时间：{{ formatTime(order.completeTime) }}</div>
              <div class="role-label">
                <span v-if="order.requesterId === currentUserId" class="buyer">我是买家</span>
                <span v-else class="seller">我是卖家</span>
              </div>
            </div>
            <div class="order-actions">
              <!-- 待确认：持有者可确认，任一方可取消 -->
              <el-button v-if="order.transStatus === 1 && order.ownerId === currentUserId"
                         type="success" size="small" @click="confirmTrans(order.id)">
                确认交易
              </el-button>
              <el-button v-if="order.transStatus === 1"
                         type="danger" size="small" @click="cancelOrder(order.id)">
                取消订单
              </el-button>
              <!-- 已取消状态不可再操作 -->
              <el-tooltip v-if="order.transStatus === 2 && false" content="只有待确认状态可取消">
                <el-button disabled size="small">取消订单</el-button>
              </el-tooltip>
              <!-- 已确认：可确认完成（TC-ORDER-005） -->
              <el-button v-if="order.transStatus === 2"
                         type="primary" size="small" @click="completeOrder(order.id)">
                确认完成
              </el-button>
              <!-- 已完成：可以评价 -->
              <el-button v-if="order.transStatus === 3"
                         size="small" @click="goReview(order)">
                去评价
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
                       layout="total, prev, pager, next" @current-change="loadOrders" />
      </div>
    </div>
  </div>

  <!-- 评价弹窗 -->
  <el-dialog v-model="reviewDialogVisible" title="交易评价" width="450px">
    <el-form :model="reviewForm" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="reviewForm.rating" />
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="reviewForm.content" type="textarea" :rows="3" placeholder="写下你的评价" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reviewDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitReview">提交评价</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '../components/AppHeader.vue'
import { transactionApi } from '../api/transaction.js'
import { reviewApi } from '../api/review.js'
import { useUserStore } from '../store/user.js'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.userId)

const orders = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const activeStatus = ref(null)

// 评价弹窗
const reviewDialogVisible = ref(false)
const reviewForm = ref({ transId: null, revieweeId: null, rating: 5, content: '' })
const currentOrder = ref(null)

onMounted(() => loadOrders())

async function loadOrders() {
  const params = { page: page.value, pageSize: pageSize.value }
  if (activeStatus.value !== null) params.status = activeStatus.value
  const res = await transactionApi.myOrders(params)
  if (res.code === 200) {
    orders.value = res.data.list || []
    total.value = res.data.total || 0
  }
}

async function cancelOrder(id) {
  try {
    await ElMessageBox.confirm('确认取消此订单吗？', '提示', { type: 'warning' })
    const res = await transactionApi.cancel(id)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadOrders()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {}
}

async function confirmTrans(id) {
  const res = await transactionApi.confirm(id)
  if (res.code === 200) {
    ElMessage.success('已确认交易')
    loadOrders()
  } else {
    ElMessage.error(res.msg)
  }
}

async function completeOrder(id) {
  try {
    await ElMessageBox.confirm('确认已完成线下交接吗？', '提示', { type: 'success' })
    const res = await transactionApi.complete(id)
    if (res.code === 200) {
      ElMessage.success('订单已完成')
      loadOrders()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {}
}

function goReview(order) {
  currentOrder.value = order
  // 买家评价卖家
  const revieweeId = order.requesterId === currentUserId.value ? order.ownerId : order.requesterId
  reviewForm.value = { transId: order.id, revieweeId, rating: 5, content: '' }
  reviewDialogVisible.value = true
}

async function submitReview() {
  const res = await reviewApi.add({
    transId: reviewForm.value.transId,
    revieweeId: reviewForm.value.revieweeId,
    reviewType: 1,
    content: reviewForm.value.content,
    rating: reviewForm.value.rating
  })
  if (res.code === 200) {
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
  } else {
    ElMessage.error(res.msg)
  }
}

function statusLabel(s) {
  return { 1: '待确认', 2: '已确认', 3: '已完成', 4: '已取消', 5: '纠纷中' }[s] || '未知'
}
function statusType(s) {
  return { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[s] || 'info'
}
function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}
</script>

<style scoped>
.page-container { max-width: 900px; margin: 20px auto; padding: 0 16px; }
.page-title { font-size: 20px; font-weight: bold; margin-bottom: 16px; }
.filter-bar { margin-bottom: 16px; }
.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-card {
  background: #fff;
  border-radius: 6px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}
.order-id { font-size: 13px; color: #999; }
.order-body { display: flex; justify-content: space-between; align-items: flex-start; }
.order-info { font-size: 14px; line-height: 2; }
.order-actions { display: flex; flex-direction: column; gap: 8px; }
.role-label { margin-top: 4px; }
.buyer { color: #409eff; background: #ecf5ff; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.seller { color: #67c23a; background: #f0f9eb; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.empty-tip { text-align: center; padding: 60px; color: #999; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
