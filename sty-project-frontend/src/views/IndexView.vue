<template>
  <el-button @click="doLogout">
    退出登录
  </el-button>
</template>

<script setup lang="ts">

import {logout} from "@/net";
import router from "@/router";

const doLogout = (): void => {
  logout(() => {
    router.push('/')
  })
}

</script>

<style scoped>

</style>