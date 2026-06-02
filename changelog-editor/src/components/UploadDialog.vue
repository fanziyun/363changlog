<template>
  <v-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" max-width="640">
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon start :icon="titleIcon" />
        {{ titleText }}
      </v-card-title>

      <v-divider />

      <v-card-text class="pt-4">
        <!-- Confirm state: preview + commit message -->
        <template v-if="uploadState === 'confirm'">
          <div class="text-subtitle-2 font-weight-bold mb-1">上传预览</div>
          <pre class="upload-preview pa-3 bg-grey-lighten-4 rounded-lg overflow-auto mb-4" style="max-height: 320px; font-family: monospace; font-size: 12px; white-space: pre-wrap;">{{ previewJson }}</pre>

          <v-text-field
            v-model="commitMessage"
            label="Commit 信息"
            variant="outlined"
            density="compact"
            hide-details
            class="mb-2"
          />
        </template>

        <!-- Uploading state -->
        <template v-if="uploadState === 'uploading'">
          <div class="d-flex flex-column align-center py-6">
            <v-progress-circular indeterminate color="primary" size="48" class="mb-4" />
            <span class="text-body-1">正在上传到 GitHub...</span>
          </div>
        </template>

        <!-- Success state -->
        <template v-if="uploadState === 'success'">
          <div class="d-flex flex-column align-center py-6">
            <v-icon icon="mdi-check-circle" color="success" size="64" class="mb-3" />
            <div class="text-h6 font-weight-bold mb-1">上传成功</div>
            <div class="text-body-2 text-medium-emphasis">
              Commit SHA: <code class="font-weight-medium">{{ commitSha }}</code>
            </div>
          </div>
        </template>

        <!-- Conflict state -->
        <template v-if="uploadState === 'conflict'">
          <div class="d-flex flex-column align-center py-4">
            <v-icon icon="mdi-alert-circle-outline" color="warning" size="64" class="mb-3" />
            <div class="text-h6 font-weight-bold mb-1">文件已被他人修改</div>
            <div class="text-body-2 text-medium-emphasis mb-4 text-center">
              远程仓库的 changelog.json 已被其他人修改，您的更改与远程版本存在冲突。
              <br />
              请选择处理方式：
            </div>
            <div class="d-flex flex-wrap justify-center ga-2">
              <v-btn variant="outlined" color="warning" prepend-icon="mdi-upload" @click="handleForceOverwrite">
                覆盖
              </v-btn>
              <v-btn variant="text" @click="closeDialog">
                取消
              </v-btn>
              <v-btn variant="elevated" color="primary" prepend-icon="mdi-download" @click="handlePullLatest">
                先拉取最新版本
              </v-btn>
            </div>
            <v-expand-transition>
              <div v-if="errorMessage" class="mt-3 text-caption text-medium-emphasis text-center">
                {{ errorMessage }}
              </div>
            </v-expand-transition>
          </div>
        </template>

        <!-- Error state -->
        <template v-if="uploadState === 'error'">
          <div class="d-flex flex-column align-center py-4">
            <v-icon icon="mdi-close-circle-outline" color="error" size="64" class="mb-3" />
            <div class="text-h6 font-weight-bold mb-1">上传失败</div>
            <div class="text-body-2 text-medium-emphasis mb-4 text-center">
              {{ errorMessage }}
            </div>
            <v-btn variant="elevated" color="primary" prepend-icon="mdi-refresh" @click="uploadState = 'confirm'">
              重试
            </v-btn>
          </div>
        </template>
      </v-card-text>

      <v-divider v-if="uploadState === 'confirm'" />

      <!-- Actions: only show for confirm state -->
      <v-card-actions v-if="uploadState === 'confirm'">
        <v-spacer />
        <v-btn variant="text" @click="closeDialog">
          取消
        </v-btn>
        <v-btn variant="elevated" color="primary" prepend-icon="mdi-upload" @click="handleUpload">
          确认上传
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useEditorStore } from '../stores/editor'
import { useGithubStore } from '../stores/github'
import { getFile, uploadFile } from '../api/github'
import { toExportJson } from '../utils/json'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'upload-success': [sha: string]
  'pull-latest': []
}>()

const editorStore = useEditorStore()
const githubStore = useGithubStore()

const commitMessage = ref('')
const uploadState = ref<'confirm' | 'uploading' | 'success' | 'conflict' | 'error'>('confirm')
const errorMessage = ref('')
const commitSha = ref('')

// Computed preview JSON
const previewJson = computed(() => toExportJson(editorStore.allData))

// Compute latest version for default commit message
const latestVersion = computed(() => {
  const entries = editorStore.entries
  if (entries.length > 0) {
    return entries[0].version || ''
  }
  return ''
})

// Derived title text and icon from state
const titleIcon = computed(() => {
  switch (uploadState.value) {
    case 'success': return 'mdi-check-circle'
    case 'conflict': return 'mdi-alert-circle-outline'
    case 'error': return 'mdi-close-circle-outline'
    default: return 'mdi-github'
  }
})

const titleText = computed(() => {
  switch (uploadState.value) {
    case 'uploading': return '上传中'
    case 'success': return '上传成功'
    case 'conflict': return '上传冲突'
    case 'error': return '上传失败'
    default: return '上传到 GitHub'
  }
})

// Reset state when dialog opens
watch(() => props.modelValue, (open) => {
  if (open) {
    uploadState.value = 'confirm'
    errorMessage.value = ''
    commitSha.value = ''
    commitMessage.value = `更新日志配置: v${latestVersion.value}`
  } else {
    // Allow reset for next open
  }
})

function closeDialog() {
  emit('update:modelValue', false)
}

async function handleUpload() {
  if (!githubStore.token || !githubStore.selectedFork) {
    errorMessage.value = '请先登录 GitHub 并选择 Fork'
    uploadState.value = 'error'
    return
  }

  uploadState.value = 'uploading'
  try {
    const [owner, repo] = githubStore.selectedFork.split('/')
    const content = previewJson.value

    // First get current file to get latest SHA (optimistic lock)
    const fileInfo = await getFile(githubStore.token, owner, repo, 'changelog.json')
    const sha = fileInfo.sha

    // Then upload with SHA (fails if remote has changed)
    const result = await uploadFile(githubStore.token, owner, repo, 'changelog.json', content, sha, commitMessage.value)
    commitSha.value = result
    uploadState.value = 'success'

    // Auto-close after success
    setTimeout(() => {
      emit('upload-success', result)
      closeDialog()
    }, 1500)
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : '未知错误'
    if (msg.includes('SHA') || msg.includes('已被修改') || msg.includes('409') || msg.includes('422')) {
      uploadState.value = 'conflict'
      errorMessage.value = msg
    } else {
      uploadState.value = 'error'
      errorMessage.value = msg
    }
  }
}

async function handleForceOverwrite() {
  if (!githubStore.token || !githubStore.selectedFork) return

  uploadState.value = 'uploading'
  try {
    const [owner, repo] = githubStore.selectedFork.split('/')
    const content = previewJson.value

    // Upload WITHOUT sha to force overwrite
    const result = await uploadFile(githubStore.token, owner, repo, 'changelog.json', content, undefined, commitMessage.value)
    commitSha.value = result
    uploadState.value = 'success'

    setTimeout(() => {
      emit('upload-success', result)
      closeDialog()
    }, 1500)
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : '未知错误'
    uploadState.value = 'error'
    errorMessage.value = msg
  }
}

function handlePullLatest() {
  emit('pull-latest')
  closeDialog()
}
</script>

<style scoped>
.upload-preview {
  line-height: 1.5;
}
</style>
