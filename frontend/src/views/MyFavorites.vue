<template>
  <div>
    <AppHeader />
    <div class="page-container">
      <div class="page-title">我的收藏</div>

      <div v-if="list.length === 0" class="empty-tip">
        暂无收藏，去<router-link to="/textbook/list">浏览教材</router-link>吧
      </div>

      <div class="fav-grid">
        <div v-for="item in list" :key="item.id" class="fav-card">
          <div class="fav-info">
            <span>教材ID：{{ item.bookId }}</span>
            <span class="fav-time">收藏于 {{ formatTime(item.favTime) }}</span>
          </div>
          <div class="fav-actions">
            <el-button size="small" type="primary" @click="$router.push(`/textbook/detail/${item.bookId}`)">
              查看详情
            </el-button>
            <el-button size="small" type="danger" @click="removeFav(item.bookId)">
              取消收藏
            </el-button>
          </div>
        </div>
      </div>

      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
                       layout="total, prev, pager, next" @current-change="loadFavorites" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AppHeader from '../components/AppHeader.vue'
import { favoriteApi } from '../api/favorite.js'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(12)

onMounted(() => loadFavorites())

async function loadFavorites() {
  const res = await favoriteApi.myFavorites({ page: page.value, pageSize: pageSize.value })
  if (res.code === 200) {
    list.value = res.data.list || []
    total.value = res.data.total || 0
  }
}

async function removeFav(bookId) {
  const res = await favoriteApi.toggle(bookId)
  if (res.code === 200) {
    ElMessage.success('已取消收藏')
    loadFavorites()
  }
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.page-container { max-width: 900px; margin: 20px auto; padding: 0 16px; }
.page-title { font-size: 20px; font-weight: bold; margin-bottom: 16px; }
.fav-grid { display: flex; flex-direction: column; gap: 10px; }
.fav-card {
  background: #fff;
  border-radius: 6px;
  padding: 14px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.fav-info { display: flex; gap: 20px; font-size: 14px; align-items: center; }
.fav-time { color: #999; font-size: 12px; }
.fav-actions { display: flex; gap: 8px; }
.empty-tip { text-align: center; padding: 60px; color: #999; font-size: 15px; }
.empty-tip a { color: #409eff; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
