<template>
  <div>
    <AppHeader />
    <div class="page-container">

      <!-- 环保统计面板（TC-ECO-004：首页展示全局统计） -->
      <div class="eco-panel">
        <h3>🌱 平台环保贡献统计</h3>
        <div class="eco-items">
          <div class="eco-item">
            <div class="eco-num">{{ ecoStats.totalBooks }}</div>
            <div class="eco-label">累计交易本数</div>
          </div>
          <div class="eco-item">
            <div class="eco-num">¥{{ ecoStats.totalSaved }}</div>
            <div class="eco-label">累计节省金额</div>
          </div>
          <div class="eco-item">
            <div class="eco-num">{{ ecoStats.totalCO2 }} kg</div>
            <div class="eco-label">累计减少碳排放</div>
          </div>
        </div>
      </div>

      <div class="content-row">
        <!-- 公告栏 -->
        <div class="announcement-section">
          <div class="section-title">📢 系统公告</div>
          <div v-if="announcements.length === 0" class="empty-tip">暂无公告</div>
          <div v-for="item in announcements" :key="item.id" class="announcement-item">
            <div class="ann-title">{{ item.title }}</div>
            <div class="ann-time">{{ formatTime(item.publishTime) }}</div>
            <div class="ann-content">{{ item.content }}</div>
          </div>
        </div>

        <!-- 最新教材 -->
        <div class="textbook-section">
          <div class="section-title">
            📖 最新教材
            <router-link to="/textbook/list" class="more-link">查看全部 →</router-link>
          </div>
          <div v-if="textbooks.length === 0" class="empty-tip">暂无教材</div>
          <div class="textbook-grid">
            <div v-for="book in textbooks" :key="book.id" class="textbook-card"
                 @click="$router.push(`/textbook/detail/${book.id}`)">
              <div class="book-img">
                <img v-if="book.imageUrl" :src="book.imageUrl" alt="封面" />
                <div v-else class="no-img">📚</div>
              </div>
              <div class="book-info">
                <div class="book-name">{{ book.bookName }}</div>
                <div class="book-isbn">ISBN: {{ book.isbn }}</div>
                <div class="book-price">
                  <span class="price-tag">¥{{ book.expectedPrice || '面议' }}</span>
                  <span class="condition-tag">{{ book.conditionDesc }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import { ecoApi } from '../api/eco.js'
import { announcementApi } from '../api/announcement.js'
import { textbookApi } from '../api/textbook.js'

const ecoStats = ref({ totalBooks: 0, totalSaved: '0.00', totalCO2: '0.00' })
const announcements = ref([])
const textbooks = ref([])

onMounted(async () => {
  // 加载环保统计
  const ecoRes = await ecoApi.globalStats()
  if (ecoRes.code === 200) {
    ecoStats.value = {
      totalBooks: ecoRes.data.totalBooks || 0,
      totalSaved: (ecoRes.data.totalSaved || 0).toFixed(2),
      totalCO2: (ecoRes.data.totalCO2 || 0).toFixed(2)
    }
  }

  // 加载公告
  const annRes = await announcementApi.list({ page: 1, pageSize: 5 })
  if (annRes.code === 200) {
    announcements.value = annRes.data.list || []
  }

  // 加载最新教材
  const bookRes = await textbookApi.list({ page: 1, pageSize: 8 })
  if (bookRes.code === 200) {
    textbooks.value = bookRes.data.list || []
  }
})

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 16px;
}

/* 环保统计面板 */
.eco-panel {
  background: linear-gradient(90deg, #e8f5e9, #f1f8e9);
  border-radius: 8px;
  padding: 20px 30px;
  margin-bottom: 20px;
  border: 1px solid #c8e6c9;
}
.eco-panel h3 {
  color: #2e7d32;
  margin-bottom: 16px;
  font-size: 16px;
}
.eco-items {
  display: flex;
  gap: 40px;
}
.eco-item {
  text-align: center;
}
.eco-num {
  font-size: 28px;
  font-weight: bold;
  color: #388e3c;
}
.eco-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.content-row {
  display: flex;
  gap: 20px;
}

/* 公告栏 */
.announcement-section {
  width: 300px;
  flex-shrink: 0;
}
.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.more-link {
  font-size: 13px;
  color: #409eff;
  text-decoration: none;
  font-weight: normal;
}
.announcement-item {
  background: #fff;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.ann-title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
}
.ann-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}
.ann-content {
  font-size: 13px;
  color: #555;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 最新教材 */
.textbook-section { flex: 1; }
.textbook-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
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
  height: 120px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.book-img img { width: 100%; height: 100%; object-fit: cover; }
.no-img { font-size: 40px; }
.book-info { padding: 10px; }
.book-name {
  font-size: 14px;
  font-weight: bold;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}
.book-isbn { font-size: 11px; color: #999; margin-bottom: 6px; }
.book-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price-tag { color: #e6a23c; font-weight: bold; font-size: 14px; }
.condition-tag {
  font-size: 11px;
  background: #e6f7ff;
  color: #409eff;
  padding: 1px 6px;
  border-radius: 10px;
}
.empty-tip { color: #999; font-size: 14px; padding: 20px 0; text-align: center; }
</style>
