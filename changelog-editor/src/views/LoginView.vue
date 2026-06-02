<template>
  <v-container class="fill-height d-flex align-center justify-center">
    <v-card class="pa-8 text-center" max-width="440" width="100%" elevation="4">
      <v-icon icon="mdi-github" size="64" color="dark" class="mb-4" />

      <h1 class="text-h5 font-weight-bold mb-2">更新日志配置工具</h1>
      <p class="text-body-2 text-medium-emphasis mb-6">
        通过 GitHub 登录以管理您的更新日志文件
      </p>

      <v-btn
        size="x-large"
        color="dark"
        variant="elevated"
        block
        class="text-none"
        prepend-icon="mdi-github"
        @click="login"
      >
        使用 GitHub 登录
      </v-btn>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
const clientId = import.meta.env.VITE_GITHUB_CLIENT_ID
const redirectUri = `${window.location.origin}/callback`

function login() {
  const params = new URLSearchParams({
    client_id: clientId,
    redirect_uri: redirectUri,
    scope: 'repo',
  })
  window.location.href = `https://github.com/login/oauth/authorize?${params.toString()}`
}
</script>

<style scoped>
.fill-height {
  min-height: 100vh;
}
</style>
