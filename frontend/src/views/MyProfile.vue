<template>
  <div>
    <AppHeader />
    <div class="page-container">
      <div class="profile-layout">

        <!-- 左侧：个人信息 -->
        <div class="profile-card">
          <div class="avatar">
            <el-avatar :size="80" icon="UserFilled" />
            <div class="name">{{ userInfo.realName }}</div>
            <div class="username">@{{ userInfo.username }}</div>
          </div>

          <el-divider />

          <!-- 个人环保贡献（TC-ECO-002） -->
          <div class="eco-section">
            <div class="eco-title">🌱 我的环保贡献</div>
            <div class="eco-row">
              <span>卖出教材</span>
              <span class="eco-val">{{ eco.sellCount }} 本</span>
            </div>
            <div class="eco-row">
              <span>帮他人节省</span>
              <span class="eco-val">¥{{ eco.savedAmount }}</span>
            </div>
            <div class="eco-row">
              <span>减少碳排放</span>
              <span class="eco-val">{{ eco.co2Reduction }} kg</span>
            </div>
          </div>

          <el-divider />

          <div class="quick-links">
            <router-link to="/my/orders">我的订单</router-link>
            <router-link to="/my/favorites">我的收藏</router-link>
            <router-link to="/my/reviews">我的评价</router-link>
          </div>
        </div>

        <!-- 右侧：编辑信息 -->
        <div class="edit-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form :model="profileForm" label-width="80px">
                <el-form-item label="用户名">
                  <el-input :value="userInfo.username" disabled />
                </el-form-item>
                <el-form-item label="姓名">
                  <el-input v-model="profileForm.realName" />
                </el-form-item>
                <el-form-item label="专业">
                  <el-input v-model="profileForm.major" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveProfile" :loading="saving">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
              <el-form :model="pwdForm" label-width="80px">
                <el-form-item label="原密码">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePassword" :loading="savingPwd">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="我发布的教材" name="published">
              <div v-if="myBooks.length === 0" class="empty-tip">
                还没有发布教材，<router-link to="/textbook/publish">去发布</router-link>
              </div>
              <div v-for="book in myBooks" :key="book.id" class="book-row">
                <div>
                  <span class="book-name">{{ book.bookName }}</span>
                  <span class="book-isbn">{{ book.isbn }}</span>
                </div>
                <div style="display:flex; gap:8px; align-items:center">
                  <el-tag :type="auditTag(book.auditStatus)" size="small">
                    {{ auditLabel(book.auditStatus) }}
                  </el-tag>
                  <el-button size="small" type="danger"
                             v-if="book.status === 1"
                             @click="offShelf(book.id)">下架</el-button>
                  <el-button size="small" type="primary"
                             @click="$router.push(`/textbook/detail/${book.id}`)">
                    查看
                  </el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import AppHeader from '../components/AppHeader.vue'
import { userApi } from '../api/user.js'
import { textbookApi } from '../api/textbook.js'
import { ecoApi } from '../api/eco.js'
import { useUserStore } from '../store/user.js'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})
const activeTab = ref('info')
const saving = ref(false)
const savingPwd = ref(false)
const myBooks = ref([])
const eco = ref({ sellCount: 0, savedAmount: '0.00', co2Reduction: '0.00' })

const profileForm = reactive({
  realName: '',
  major: '',
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: ''
})

onMounted(async () => {
  // 加载用户信息
  const res = await userApi.getInfo()
  if (res.code === 200) {
    const u = res.data
    profileForm.realName = u.realName || ''
    profileForm.major = u.major || ''
    profileForm.phone = u.phone || ''
    profileForm.email = u.email || ''
  }

  // 加载我发布的教材
  const bookRes = await textbookApi.myPublished()
  if (bookRes.code === 200) myBooks.value = bookRes.data || []

  // 加载环保贡献
  const ecoRes = await ecoApi.personalStats()
  if (ecoRes.code === 200) {
    eco.value = {
      sellCount: ecoRes.data.sellCount || 0,
      savedAmount: (ecoRes.data.savedAmount || 0).toFixed(2),
      co2Reduction: (ecoRes.data.co2Reduction || 0).toFixed(2)
    }
  }
})

async function saveProfile() {
  saving.value = true
  try {
    const res = await userApi.updateProfile(profileForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      // 更新store中的姓名
      if (userStore.userInfo) {
        userStore.userInfo.realName = profileForm.realName
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      }
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  savingPwd.value = true
  try {
    const res = await userApi.changePassword(pwdForm)
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      window.location.href = '/login'
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } finally {
    savingPwd.value = false
  }
}

async function offShelf(id) {
  const res = await textbookApi.offShelf(id)
  if (res.code === 200) {
    ElMessage.success('下架成功')
    const bookRes = await textbookApi.myPublished()
    if (bookRes.code === 200) myBooks.value = bookRes.data || []
  }
}

function auditLabel(s) {
  return { 0: '待审核', 1: '已上架', 2: '已驳回' }[s] || '未知'
}
function auditTag(s) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info'
}
</script>

<style scoped>
.page-container { max-width: 1000px; margin: 20px auto; padding: 0 16px; }
.profile-layout { display: flex; gap: 20px; }
.profile-card {
  width: 240px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
  flex-shrink: 0;
}
.avatar { text-align: center; margin-bottom: 10px; }
.name { font-size: 18px; font-weight: bold; margin-top: 8px; }
.username { color: #999; font-size: 13px; }
.eco-section { padding: 4px 0; }
.eco-title { font-size: 14px; font-weight: bold; margin-bottom: 10px; color: #2e7d32; }
.eco-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 6px;
  color: #555;
}
.eco-val { color: #388e3c; font-weight: bold; }
.quick-links {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.quick-links a { color: #409eff; text-decoration: none; font-size: 14px; }
.edit-card {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.book-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.book-name { font-size: 14px; font-weight: bold; margin-right: 10px; }
.book-isbn { font-size: 12px; color: #999; }
.empty-tip { text-align: center; padding: 30px; color: #999; font-size: 14px; }
.empty-tip a { color: #409eff; }
</style>
