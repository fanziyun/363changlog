export const TYPE_OPTIONS = ['major', 'minor', 'patch', 'hotfix', 'danger'] as const
export type UpdateType = typeof TYPE_OPTIONS[number]

export const TYPE_CHINESE: Record<UpdateType, string> = {
  major: '重大更新',
  minor: '功能更新',
  patch: '修复补丁',
  hotfix: '热修复',
  danger: '危险更新',
}

export const TYPE_COLORS: Record<UpdateType, string> = {
  major: '#FF5722',
  minor: '#4CAF50',
  patch: '#2196F3',
  hotfix: '#FF9800',
  danger: '#F44336',
}
