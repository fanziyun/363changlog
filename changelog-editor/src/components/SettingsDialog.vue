<template>
  <v-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" max-width="560">
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon icon="mdi-cog" start />
        设置
      </v-card-title>

      <v-divider />

      <v-card-text class="pt-4">
        <!-- Footer 区域 -->
        <div class="text-subtitle-1 font-weight-bold mb-2">页脚文本</div>
        <v-text-field
          v-model="editorStore.footer"
          label="页脚"
          placeholder="输入页脚文字..."
          variant="outlined"
          density="compact"
          hide-details
          class="mb-6"
        />

        <v-divider class="mb-4" />

        <!-- tagColors 区域 -->
        <div class="d-flex align-center justify-space-between mb-2">
          <span class="text-subtitle-1 font-weight-bold">标签颜色</span>
          <v-btn
            variant="tonal"
            size="small"
            prepend-icon="mdi-plus"
            @click="addTagColor"
          >
            添加
          </v-btn>
        </div>

        <div v-if="tagColorRows.length === 0" class="text-body-2 text-medium-emphasis py-2">
          暂无标签颜色，点击"添加"按钮添加。
        </div>

        <v-row
          v-for="(row, index) in tagColorRows"
          :key="row.id"
          align="center"
          class="mb-2"
          no-gutters
        >
          <v-col cols="5" class="pr-2">
            <v-text-field
              v-model="row.key"
              label="标签名"
              variant="outlined"
              density="compact"
              hide-details
              placeholder="例如: 优化"
            />
          </v-col>
          <v-col cols="5" class="pr-2">
            <v-text-field
              v-model="row.value"
              label="颜色值"
              variant="outlined"
              density="compact"
              hide-details
              placeholder="例如: #FFAA00"
            />
          </v-col>
          <v-col cols="2">
            <v-btn
              icon="mdi-delete-outline"
              variant="text"
              size="small"
              color="error"
              @click="removeTagColor(index)"
            />
          </v-col>
        </v-row>

        <v-divider class="my-4" />

        <!-- 主题切换区域 -->
        <div class="d-flex align-center justify-space-between">
          <div>
            <div class="text-subtitle-1 font-weight-bold">主题切换</div>
            <div class="text-body-2 text-medium-emphasis">
              当前: {{ currentThemeName }}
            </div>
          </div>
          <v-switch
            :model-value="isDark"
            @update:model-value="toggleTheme"
            color="primary"
            hide-details
            inset
          />
        </div>
      </v-card-text>

      <v-divider />

      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="elevated"
          color="primary"
          @click="handleSave"
        >
          保存
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { reactive, computed, watch } from 'vue'
import { useTheme } from 'vuetify'
import { useEditorStore } from '../stores/editor'

interface TagColorRow {
  id: number
  key: string
  value: string
}

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const editorStore = useEditorStore()
const theme = useTheme()

let nextId = 1

const tagColorRows = reactive<TagColorRow[]>([])

const isDark = computed(() => theme.global.current.value.dark)
const currentThemeName = computed(() => isDark.value ? '深色' : '浅色')

// 打开对话框时，将 store 中的 tagColors 同步到本地行数组
watch(() => props.modelValue, (open) => {
  if (open) {
    syncFromStore()
  }
})

function syncFromStore() {
  tagColorRows.splice(0, tagColorRows.length)
  for (const [key, value] of Object.entries(editorStore.tagColors)) {
    tagColorRows.push({ id: nextId++, key, value })
  }
}

function addTagColor() {
  tagColorRows.push({ id: nextId++, key: '', value: '' })
}

function removeTagColor(index: number) {
  tagColorRows.splice(index, 1)
}

function handleSave() {
  // 将本地行数组转换为 Record 写回 store
  const colors: Record<string, string> = {}
  for (const row of tagColorRows) {
    if (row.key.trim()) {
      colors[row.key.trim()] = row.value
    }
  }
  editorStore.tagColors = colors

  emit('update:modelValue', false)
}

function toggleTheme(value: boolean | null) {
  theme.global.name.value = value ? 'dark' : 'light'
}
</script>