<template>
  <div>
    <AppHeader />
    <div class="page-container" v-if="book">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/textbook/list' }">教材列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ book.bookName }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-card">
        <div class="detail-left">
          <div class="book-img">
            <img v-if="book.imageUrl" :src="book.imageUrl" alt="封面" />
            <div v-else class="no-img">📚</div>
          </div>
          <!-- 收藏按钮 -->
          <el-button :icon="isFav ? 'StarFilled' : 'Star'"
                     :type="isFav ? 'warning' : 'default'"
                     style="width:100%; margin-top:10px"
                     @click="toggleFav" v-if="isLoggedIn">
            {{ isFav ? '已收藏' : '收藏教材' }}
          </el-button>
        </div>

        <div class="detail-right">
          <h2>{{ book.bookName }}</h2>
          <div class="info-row">
            <span class="label">ISBN号：</span><span>{{ book.isbn }}</span>
          </div>
          <div class="info-row">
            <span class="label">品相：</span>
            <el-tag size="small">{{ book.conditionDesc || '未知' }}</el-tag>
          </div>
          <div class="info-row">
            <span class="label">交易方式：</span><span>{{ book.transType }}</span>
          </div>
          <div class="info-row">
            <span class="label">原价：</span>
            <span style="text-decoration:line-through;color:#999">¥{{ book.originalPrice }}</span>
          </div>
          <div class="info-row price-row">
            <span class="label">期望价格：</span>
            <span class="price">¥{{ book.expectedPrice || '面议' }}</span>
          </div>
          <div class="info-row">
            <span class="label">描述：</span><span>{{ book.description || '暂无描述' }}</span>
          </div>
          <div class="info-row">
            <span class="label">发布时间：</span><span>{{ formatTime(book.publishTime) }}</span>
          </div>

          <div class="action-area">
            <template v-if="isLoggedIn">
              <el-button type="primary" size="large" @click="handleBuy"
                         :disabled="isOwnBook" :loading="buying">
                {{ isOwnBook ? '这是我发布的教材' : '发起交易' }}
              </el-button>
            </template>
            <template v-else>
              <el-button type="primary" size="large" @click="$router.push('/login')">
                登录后发起交易
              </el-button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="loading-tip">加载中...</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '../components/AppHeader.vue'
import { textbookApi } from '../api/textbook.js'
import { transactionApi } from '../api/transaction.js'
import { favoriteApi } from '../api/favorite.js'
import { useUserStore } from '../store/user.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const book = ref(null)
const isFav = ref(false)
const buying = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isOwnBook = computed(() => {
  if (!book.value || !userStore.userInfo) return false
  return book.value.publisherId === userStore.userInfo.userId
})

onMounted(async () => {
  const id = route.params.id
  const res = await textbookApi.detail(id)
  if (res.code === 200) {
    book.value = res.data
    // 检查是否收藏
    if (userStore.isLoggedIn) {
      const favRes = await favoriteApi.check(id)
      if (favRes.code === 200) isFav.value = favRes.data
    }
  }
})

async function toggleFav() {
  const res = await favoriteApi.toggle(book.value.id)
  if (res.code === 200) {
    isFav.value = !isFav.value
    ElMessage.success(res.msg)
  }
}

// 发起交易（TC-TRADE-001~004）
async function handleBuy() {
  try {
    await ElMessageBox.confirm('确认要购买这本教材吗？', '提示', { type: 'info' })
    buying.value = true
    const res = await transactionApi.create(book.value.id)
    if (res.code === 200) {
      ElMessage.success('交易发起成功，请等待对方确认')
      router.push('/my/orders')
    } else {
      ElMessage.error(res.msg || '发起失败')
    }
  } catch (e) {
    // 用户点了取消
  } finally {
    buying.value = false
  }
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 16px;
}
.detail-card {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  margin-top: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.detail-left { width: 200px; flex-shrink: 0; }
.book-img {
  height: 240px;
  background: #f5f7fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.book-img img { width: 100%; height: 100%; object-fit: cover; }
.no-img { font-size: 60px; }
.detail-right { flex: 1; }
.detail-right h2 { font-size: 20px; margin-bottom: 16px; }
.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  font-size: 14px;
}
.label { color: #999; width: 80px; flex-shrink: 0; }
.price-row .price { color: #e6a23c; font-size: 22px; font-weight: bold; }
.action-area { margin-top: 24px; }
.loading-tip { text-align: center; padding: 80px; color: #999; }
</style>
