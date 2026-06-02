<template>
  <div>
    <div class="d-flex justify-space-between align-center mb-4">
      <span class="text-grey">共 {{ editorStore.entryCount }} 个版本</span>
      <v-btn color="primary" variant="tonal" @click="editorStore.addEntry()">新增条目</v-btn>
    </div>
    <v-list bg-color="transparent" class="pa-0">
      <EntryCard
        v-for="(entry, displayIndex) in reversedEntries"
        :key="displayIndex"
        :entry="entry"
        :real-index="editorStore.entries.length - 1 - displayIndex"
      />
    </v-list>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useEditorStore } from '../stores/editor'
import EntryCard from './EntryCard.vue'

const editorStore = useEditorStore()
const reversedEntries = computed(() => [...editorStore.entries].reverse())
</script>
