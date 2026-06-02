import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ChangelogData, ChangelogEntry } from '../models/ChangelogData'

export const useEditorStore = defineStore('editor', () => {
  const footer = ref('')
  const tagColors = ref<Record<string, string>>({})
  const entries = ref<ChangelogEntry[]>([])

  const entryCount = computed(() => entries.value.length)

  function addEntry() {
    const last = entries.value[entries.value.length - 1]
    const bump = (v: string | undefined): string => {
      const parts = (v || '0.0.0').split('.')
      const lastNum = parseInt(parts[parts.length - 1]) || 0
      parts[parts.length - 1] = String(lastNum + 1)
      return parts.join('.')
    }
    const entry: ChangelogEntry = {
      version: bump(last?.version),
      date: new Date().toISOString().split('T')[0],
      title: '新版本',
      type: last ? [...last.type] : [],
      tags: [],
      color: last?.color || '0xFF888888',
      changes: [],
    }
    entries.value.push(entry)
  }

  function removeEntry(index: number) {
    entries.value.splice(index, 1)
  }

  function moveEntry(index: number, direction: 'up' | 'down') {
    const target = direction === 'up' ? index - 1 : index + 1
    if (target < 0 || target >= entries.value.length) return
    const tmp = entries.value[index]
    entries.value[index] = entries.value[target]
    entries.value[target] = tmp
  }

  function importData(data: ChangelogData) {
    footer.value = data.footer ?? ''
    tagColors.value = { ...data.tagColors }
    entries.value = data.entries.map(e => ({ ...e, type: [...e.type], tags: [...e.tags], changes: [...e.changes] }))
  }

  function clearAll() {
    footer.value = ''
    tagColors.value = {}
    entries.value = []
  }

  const allData = computed<ChangelogData>(() => ({
    footer: footer.value,
    tagColors: { ...tagColors.value },
    entries: entries.value,
  }))

  return { footer, tagColors, entries, entryCount, addEntry, removeEntry, moveEntry, importData, clearAll, allData }
}, {
  persist: {
    key: 'changelog-editor-draft',
    storage: localStorage,
  },
})