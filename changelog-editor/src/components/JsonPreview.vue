<template>
  <pre class="json-preview pa-4 bg-grey-lighten-4 rounded-lg overflow-auto" style="max-height: calc(100vh - 200px); font-family: monospace; font-size: 12px;">{{ previewJson }}</pre>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useEditorStore } from '../stores/editor'
import { toExportJson } from '../utils/json'

const editorStore = useEditorStore()
const previewJson = computed(() => toExportJson(editorStore.allData))
</script>