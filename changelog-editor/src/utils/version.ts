/**
 * 自动递增版本号末位 (x.y.z → x.y.z+1)
 * null/空字符串/无条目前版本 → "0.0.1"
 */
export function bumpVersion(version: string | null | undefined): string {
  const parts = (version || '0.0.0').split('.')
  if (parts.length === 0) return '0.0.1'
  const last = parseInt(parts[parts.length - 1]) || 0
  parts[parts.length - 1] = String(last + 1)
  return parts.join('.')
}