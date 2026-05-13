<template>
  <div>
    <AppHeader />
    <div class="page-container">
      <div class="page-title">教材列表</div>

      <!-- 检索条件（TC-SEARCH系列） -->
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="请输入书名关键词" clearable style="width:200px"
                  @keyup.enter="doSearch" />
        <el-input v-model="query.isbn" placeholder="输入ISBN号精确查找" clearable style="width:200px"
                  @keyup.enter="doSearch" />
        <el-input v-model="query.minPrice" placeholder="最低价格" type="number" style="width:110px" />
        <span style="color:#999">—</span>
        <el-input v-model="query.maxPrice" placeholder="最高价格" type="number" style="width:110px" />
        <el-button type="primary" @click="doSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <!-- 教材列表 -->
      <div v-if="loading" class="loading-tip">加载中...</div>
      <div v-else-if="list.length === 0" class="empty-tip">
        暂无相关教材，<router-link to="/textbook/publish">去发布教材</router-link>
      </div>
      <div v-else class="textbook-grid">
        <div v-for="book in list" :key="book.id" class="textbook-card"
             @click="$router.push(`/textbook/detail/${book.id}`)">
          <div class="book-img">
            <img v-if="book.imageUrl" :src="book.imageUrl" alt="封面" />
            <div v-else class="no-img">📚</div>
          </div>
          <div class="book-info">
            <div class="book-name" :title="book.bookName">{{ book.bookName }}</div>
            <div class="book-isbn">ISBN: {{ book.isbn }}</div>
            <div class="book-desc">{{ book.description || '暂无描述' }}</div>
            <div class="book-footer">
              <span class="price-tag">¥{{ book.expectedPrice || '面议' }}</span>
              <span class="condition-tag">{{ book.conditionDesc }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import { textbookApi } from '../api/textbook.js'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  keyword: '',
  isbn: '',
  minPrice: '',
  maxPrice: '',
  page: 1,
  pageSize: 12
})

onMounted(() => loadList())

async function loadList() {
  loading.value = true
  try {
    const params = { page: query.page, pageSize: query.pageSize }
    if (query.keyword) params.keyword = query.keyword
    if (query.isbn) params.isbn = query.isbn
    if (query.minPrice) params.minPrice = query.minPrice
    if (query.maxPrice) params.maxPrice = query.maxPrice

    const res = await textbookApi.list(params)
    if (res.code === 200) {
      list.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

function doSearch() {
  query.page = 1
  loadList()
}

function resetSearch() {
  query.keyword = ''
  query.isbn = ''
  query.minPrice = ''
  query.maxPrice = ''
  doSearch()
}
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 16px;
}
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}
.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  background: #fff;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.textbook-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.textbook-card {
  background: #fff;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
  cursor: pointer;
  transition: box-shadow .2s;
}
.textbook-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,.12); }
.book-img {
  height: 140px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.book-img img { width: 100%; height: 100%; object-fit: cover; }
.no-img { font-size: 48px; }
.book-info { padding: 12px; }
.book-name {
  font-size: 15px;
  font-weight: bold;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}
.book-isbn { font-size: 11px; color: #999; margin-bottom: 4px; }
.book-desc {
  font-size: 12px;
  color: #666;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8px;
  min-height: 32px;
}
.book-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price-tag { color: #e6a23c; font-weight: bold; font-size: 15px; }
.condition-tag {
  font-size: 11px;
  background: #e6f7ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 10px;
}
.empty-tip { color: #999; text-align: center; padding: 60px 0; font-size: 15px; }
.empty-tip a { color: #409eff; }
.loading-tip { text-align: center; padding: 60px; color: #999; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
