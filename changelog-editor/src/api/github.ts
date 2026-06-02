import { Octokit } from '@octokit/rest'

function getOctokit(token: string): Octokit {
  return new Octokit({ auth: token })
}

export interface GithubForkInfo {
  fullName: string
  defaultBranch: string
}

export interface GithubFileInfo {
  content: string
  sha: string
}

/** 列出当前用户的 fork 仓库 */
export async function listForks(token: string): Promise<GithubForkInfo[]> {
  const octokit = getOctokit(token)
  try {
    const { data } = await octokit.repos.listForAuthenticatedUser({
      type: 'owner',
      sort: 'updated',
      per_page: 50,
    })
    return data
      .filter((repo) => repo.fork)
      .map((repo) => ({
        fullName: repo.full_name,
        defaultBranch: repo.default_branch,
      }))
  } catch (e: unknown) {
    const err = e as any
    if (err.status === 401) throw new Error('GitHub 认证失败，请重新登录')
    if (err.status === 403) throw new Error('GitHub API 频率限制，请稍后再试')
    throw new Error(`获取 fork 列表失败: ${err.message}`)
  }
}

/** 从 GitHub 拉取文件内容 */
export async function getFile(
  token: string,
  owner: string,
  repo: string,
  path: string,
): Promise<GithubFileInfo> {
  const octokit = getOctokit(token)
  try {
    const { data } = await octokit.repos.getContent({ owner, repo, path })
    if (Array.isArray(data)) {
      throw new Error(`路径 ${path} 是一个目录，不是文件`)
    }
    if (data.type !== 'file') {
      throw new Error(`路径 ${path} 不是一个文件`)
    }
    return {
      content: atob(data.content),
      sha: data.sha,
    }
  } catch (e: unknown) {
    const err = e as any
    if (err.status === 404) throw new Error(`文件不存在: ${path}`)
    if (err.status === 401) throw new Error('GitHub 认证失败，请重新登录')
    if (err.status === 403) throw new Error('GitHub API 频率限制，请稍后再试')
    throw new Error(`拉取文件失败: ${err.message}`)
  }
}

/** 上传文件到 GitHub（创建/更新 commit） */
export async function uploadFile(
  token: string,
  owner: string,
  repo: string,
  path: string,
  content: string,
  sha?: string,
  message: string = '更新 changelog',
): Promise<string> {
  const octokit = getOctokit(token)
  try {
    const { data } = await octokit.repos.createOrUpdateFileContents({
      owner,
      repo,
      path,
      message,
      content: btoa(content),
      sha,
    })
    if (!data.commit.sha) throw new Error('GitHub 未返回 commit SHA')
    return data.commit.sha
  } catch (e: unknown) {
    const err = e as any
    if (err.status === 409) throw new Error('文件已被他人修改，请先拉取最新版本')
    if (err.status === 422 && sha) throw new Error('乐观锁冲突：文件 SHA 不匹配')
    if (err.status === 401) throw new Error('GitHub 认证失败，请重新登录')
    if (err.status === 403) throw new Error('GitHub API 频率限制，请稍后再试')
    throw new Error(`上传文件失败: ${err.message}`)
  }
}

/** 获取当前登录用户信息 */
export async function getCurrentUser(
  token: string,
): Promise<{ login: string; avatarUrl: string }> {
  const octokit = getOctokit(token)
  try {
    const { data } = await octokit.users.getAuthenticated()
    return { login: data.login, avatarUrl: data.avatar_url }
  } catch (e: unknown) {
    const err = e as any
    if (err.status === 401) throw new Error('GitHub 认证失败，请重新登录')
    if (err.status === 403) throw new Error('GitHub API 频率限制，请稍后再试')
    throw new Error(`获取用户信息失败: ${err.message}`)
  }
}