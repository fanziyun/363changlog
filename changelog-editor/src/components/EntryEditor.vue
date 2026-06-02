<template>
  <v-row dense>
    <!-- Version + Date -->
    <v-col cols="6">
      <v-text-field v-model="entry.version" label="版本号" density="compact" variant="outlined" hide-details />
    </v-col>
    <v-col cols="6">
      <v-text-field v-model="entry.date" label="日期" density="compact" variant="outlined" hide-details />
    </v-col>
  </v-row>

  <!-- Title -->
  <v-text-field v-model="entry.title" label="标题" density="compact" variant="outlined" hide-details class="mt-3" />

  <!-- Type Pills -->
  <div class="text-caption text-grey mt-3 mb-1">更新类型</div>
  <v-chip-group v-model="selectedTypes" multiple column>
    <v-chip
      v-for="t in typeOptions"
      :key="t"
      :value="t"
      filter
      :color="getTypeColor(t)"
      :variant="selectedTypes.includes(t) ? 'flat' : 'outlined'"
      size="small"
      label
    >{{ getTypeLabel(t) }}</v-chip>
  </v-chip-group>

  <!-- Tags -->
  <v-text-field
    :model-value="entry.tags.join(', ')"
    @update:model-value="onTagsChange"
    label="自定义标签（逗号分隔）"
    density="compact"
    variant="outlined"
    hide-details
    class="mt-3"
  />

  <!-- Color -->
  <div class="d-flex align-center ga-2 mt-3">
    <v-text-field v-model="entry.color" label="颜色 (0xAARRGGBB)" density="compact" variant="outlined" hide-details style="max-width: 200px" />
    <div class="color-preview" :style="{ width: '24px', height: '24px', backgroundColor: colorPreview, borderRadius: '4px', border: '1px solid #888' }" />
  </div>

  <!-- Changes -->
  <div class="text-caption text-grey mt-4 mb-1">变更明细</div>
  <div v-for="(_, ci) in entry.changes" :key="ci" class="d-flex align-center ga-2 mb-2">
    <v-text-field
      v-model="entry.changes[ci]"
      :label="`变更 #${ci + 1}`"
      density="compact"
      variant="outlined"
      hide-details
    />
    <v-btn icon="mdi-close" size="small" variant="text" color="error" @click="entry.changes.splice(ci, 1)" />
  </div>
  <v-btn size="small" variant="outlined" class="mt-2" @click="entry.changes.push('')" block>添加变更明细</v-btn>
</template>

<script setup lang="ts">
import { computed, watch, ref } from 'vue'
import type { ChangelogEntry } from '../models/ChangelogData'
import { TYPE_OPTIONS, TYPE_CHINESE, TYPE_COLORS, type UpdateType } from '../models/constants'
import { parseToRgbHex } from '../utils/color'

const props = defineProps<{ entry: ChangelogEntry; index: number }>()

const typeOptions = [...TYPE_OPTIONS]
const selectedTypes = ref<UpdateType[]>([...props.entry.type])

watch(selectedTypes, (val) => {
  props.entry.type = [...val]
}, { deep: true })

watch(() => props.entry.type, (val) => {
  selectedTypes.value = [...val]
})

function getTypeColor(t: string): string {
  return TYPE_COLORS[t as keyof typeof TYPE_COLORS] || '#888888'
}

function getTypeLabel(t: string): string {
  return TYPE_CHINESE[t as keyof typeof TYPE_CHINESE] || t
}

function onTagsChange(val: string) {
  props.entry.tags = val.split(',').map(s => s.trim()).filter(s => s)
}

const colorPreview = computed(() => parseToRgbHex(props.entry.color || ''))
</script>
