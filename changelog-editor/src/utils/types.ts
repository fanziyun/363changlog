/**
 * 导出用接口 — 仅序列化非空字段 (Gson 兼容: null/empty 字段被省略)
 */
export interface ChangelogEntryExport {
  version: string
  date?: string
  title?: string
  type?: Array<'major' | 'minor' | 'patch' | 'hotfix' | 'danger'>
  tags?: string[]
  color?: string
  changes: string[]
}

export interface ChangelogDataExport {
  footer?: string
  tagColors?: Record<string, string>
  entries: ChangelogEntryExport[]
}