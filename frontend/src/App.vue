<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './store/user.js'

const userStore = useUserStore()
// 刷新页面后：用后端最新资料覆盖 localStorage 里可能过期的姓名等（曾出现编码修复后仍显示旧乱码）
onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.refreshFromServer()
  }
})
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
body {
  font-family: "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  background-color: #f5f7fa;
  color: #333;
}
</style>
