<template>
  <div>
    <div v-if="errors.length === 0" class="d-flex align-center ga-2">
      <v-icon color="success">mdi-check-circle</v-icon>
      <span class="text-success">校验通过 — 所有版本的 version 和 changes 字段完整</span>
    </div>
    <div v-else>
      <div class="d-flex align-center ga-2 mb-3">
        <v-icon color="error">mdi-alert-circle</v-icon>
        <span class="text-error">共 {{ errors.length }} 个版本校验未通过</span>
      </div>
      <v-card v-for="err in errors" :key="err.index" variant="outlined" color="error" class="mb-2 pa-3">
        <div class="text-subtitle-2 font-weight-bold">{{ err.version || '(空版本)' }}</div>
        <div v-for="msg in err.errors" :key="msg" class="text-caption text-error">• {{ msg }}</div>
      </v-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useEditorStore } from '../stores/editor'
import { validateAllEntries } from '../utils/json'

const editorStore = useEditorStore()
const errors = computed(() => validateAllEntries(editorStore.entries))
</script>