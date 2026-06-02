export interface ChangelogEntry {
  version: string
  date?: string
  title?: string
  type: Array<'major' | 'minor' | 'patch' | 'hotfix' | 'danger'>
  tags: string[]
  color?: string
  changes: string[]
}

export interface ChangelogData {
  footer?: string
  tagColors?: Record<string, string>
  entries: ChangelogEntry[]
}