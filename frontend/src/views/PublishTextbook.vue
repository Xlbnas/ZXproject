<template>
  <div>
    <AppHeader />
    <div class="page-container">
      <div class="page-title">发布教材</div>

      <div class="form-card">
        <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
          <el-form-item label="书名" prop="bookName">
            <el-input v-model="form.bookName" placeholder="请输入教材书名" />
          </el-form-item>
          <el-form-item label="ISBN号" prop="isbn">
            <el-input v-model="form.isbn" placeholder="请输入13位ISBN号" maxlength="13" />
          </el-form-item>
          <el-form-item label="品相" prop="conditionDesc">
            <el-select v-model="form.conditionDesc" placeholder="请选择品相">
              <el-option label="全新" value="全新" />
              <el-option label="良好" value="良好" />
              <el-option label="一般" value="一般" />
              <el-option label="有笔记" value="有笔记" />
            </el-select>
          </el-form-item>
          <el-form-item label="交易方式" prop="transType">
            <el-select v-model="form.transType" placeholder="请选择交易方式">
              <el-option label="赠送" value="赠送" />
              <el-option label="低价转让" value="低价转让" />
            </el-select>
          </el-form-item>
          <el-form-item label="期望价格">
            <el-input-number v-model="form.expectedPrice" :min="0" :precision="2"
                             placeholder="0表示赠送" />
            <span style="margin-left:10px; color:#999; font-size:12px">元（赠送填0）</span>
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
            <span style="margin-left:10px; color:#999; font-size:12px">元</span>
          </el-form-item>
          <el-form-item label="教材描述">
            <el-input v-model="form.description" type="textarea" :rows="3"
                      placeholder="请描述教材的使用情况、有无划线等" />
          </el-form-item>
          <el-form-item label="封面图片">
            <el-upload
              :before-upload="beforeUpload"
              :http-request="handleUpload"
              :show-file-list="false"
              accept=".jpg,.jpeg,.png,.gif">
              <img v-if="form.imageUrl" :src="form.imageUrl" class="preview-img" />
              <el-button v-else>点击上传图片</el-button>
            </el-upload>
            <div style="color:#999; font-size:12px; margin-top:4px">
              支持 jpg/png/gif，大小不超过5MB
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="loading">
              提交发布
            </el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppHeader from '../components/AppHeader.vue'
import { textbookApi } from '../api/textbook.js'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  bookName: '',
  isbn: '',
  conditionDesc: '',
  transType: '',
  expectedPrice: 0,
  originalPrice: 0,
  description: '',
  imageUrl: ''
})

const rules = {
  bookName: [{ required: true, message: '书名不能为空', trigger: 'blur' }],
  isbn: [
    { required: true, message: 'ISBN号不能为空', trigger: 'blur' },
    { len: 13, message: 'ISBN号格式不正确，需要13位', trigger: 'blur' }
  ],
  conditionDesc: [{ required: true, message: '请选择品相', trigger: 'change' }],
  transType: [{ required: true, message: '请选择交易方式', trigger: 'change' }]
}

// 图片上传前校验（TC-PUBLISH-004、TC-PUBLISH-005）
function beforeUpload(file) {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  if (!isImage) {
    ElMessage.error('请上传图片格式文件')
    return false
  }
  const isLimit = file.size / 1024 / 1024 < 5
  if (!isLimit) {
    ElMessage.error('图片大小过大，请上传5MB以内的图片')
    return false
  }
  return true
}

async function handleUpload({ file }) {
  const formData = new FormData()
  formData.append('file', file)
  const res = await textbookApi.uploadImage(formData)
  if (res.code === 200) {
    form.imageUrl = res.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await textbookApi.publish({ ...form })
    if (res.code === 200) {
      ElMessage.success('发布成功，等待管理员审核')
      router.push('/my/profile')
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  max-width: 700px;
  margin: 20px auto;
  padding: 0 16px;
}
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}
.form-card {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.preview-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ddd;
}
</style>
