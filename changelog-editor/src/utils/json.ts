import type { ChangelogData, ChangelogEntry } from '../models/ChangelogData'
import type { ChangelogDataExport, ChangelogEntryExport } from './types'

/**
 * 将编辑器状态导出为 JSON 字符串 (Gson 兼容 pretty print, 2-space indent)
 */
export function toExportJson(data: ChangelogData): string {
  const exportData: ChangelogDataExport = {
    entries: data.entries.map(toEntryExport),
  }
  if (data.footer) exportData.footer = data.footer
  if (data.tagColors && Object.keys(data.tagColors).length > 0) {
    exportData.tagColors = data.tagColors
  }
  return JSON.stringify(exportData, null, 2)
}

function toEntryExport(entry: ChangelogEntry): ChangelogEntryExport {
  const e: ChangelogEntryExport = {
    version: entry.version,
    changes: entry.changes,
  }
  if (entry.date) e.date = entry.date
  if (entry.title) e.title = entry.title
  if (entry.type && entry.type.length > 0) e.type = entry.type
  if (entry.tags && entry.tags.length > 0) e.tags = entry.tags
  if (entry.color) e.color = entry.color
  return e
}

/**
 * 从 JSON 字符串解析为 ChangelogData
 */
export function fromImportJson(json: string): ChangelogData {
  const parsed: ChangelogDataExport = JSON.parse(json)
  if (!parsed.entries || !Array.isArray(parsed.entries)) {
    throw new Error('JSON 格式错误: 缺少 entries 数组')
  }
  return {
    footer: parsed.footer || '',
    tagColors: parsed.tagColors || {},
    entries: parsed.entries.map(fromEntryExport),
  }
}

function fromEntryExport(e: ChangelogEntryExport): ChangelogEntry {
  return {
    version: e.version || '',
    date: e.date || '',
    title: e.title || '',
    type: Array.isArray(e.type) ? e.type : [],
    tags: Array.isArray(e.tags) ? e.tags : [],
    color: e.color || '0xFF888888',
    changes: Array.isArray(e.changes) ? e.changes : [],
  }
}

export interface ValidationError {
  index: number
  version: string
  errors: string[]
}

/**
 * 编译校验: 检查所有条目的 version 和 changes 字段
 * 空 version 或空 changes → 错误
 */
export function validateAllEntries(entries: ChangelogEntry[]): ValidationError[] {
  return entries
    .map((e, i) => {
      const errors: string[] = []
      if (!e.version.trim()) errors.push('版本号不能为空')
      if (e.changes.length === 0) errors.push('变更明细不能为空')
      return errors.length > 0 ? { index: i, version: e.version || '(空)', errors } : null
    })
    .filter((v): v is ValidationError => v !== null)
}

/**
 * 校验单个条目
 */
export function validateEntry(entry: ChangelogEntry): string[] {
  const errors: string[] = []
  if (!entry.version.trim()) errors.push('版本号不能为空')
  if (entry.changes.length === 0) errors.push('变更明细不能为空')
  return errors
}