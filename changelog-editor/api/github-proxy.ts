import type { VercelRequest, VercelResponse } from '@vercel/node'

const GITHUB_API = 'https://api.github.com'

const ALLOW_ORIGIN = '*'
const ALLOW_METHODS = 'GET, PUT, OPTIONS'
const ALLOW_HEADERS = 'Authorization, Content-Type'

function setCorsHeaders(res: VercelResponse): VercelResponse {
  res.setHeader('Access-Control-Allow-Origin', ALLOW_ORIGIN)
  res.setHeader('Access-Control-Allow-Methods', ALLOW_METHODS)
  res.setHeader('Access-Control-Allow-Headers', ALLOW_HEADERS)
  return res
}

export default async function handler(
  req: VercelRequest,
  res: VercelResponse,
): Promise<void> {
  setCorsHeaders(res)

  // CORS preflight
  if (req.method === 'OPTIONS') {
    res.status(200).end()
    return
  }

  const token = req.headers.authorization
  if (!token || !token.startsWith('Bearer ')) {
    res.status(401).json({ message: 'Missing or invalid Authorization header' })
    return
  }

  try {
    if (req.method === 'GET') {
      const path = req.query.path as string | undefined
      if (!path) {
        res.status(400).json({ message: 'Missing query parameter: path' })
        return
      }

      const url = `${GITHUB_API}${path}`
      const response = await fetch(url, {
        headers: { Authorization: token },
      })

      const body = await response.json()
      res.status(response.status).json(body)
      return
    }

    if (req.method === 'PUT') {
      const { path, owner, repo, content, sha, message } = req.body

      if (!path || !owner || !repo || !content) {
        res.status(400).json({
          message: 'Missing required fields: path, owner, repo, content',
        })
        return
      }

      const url = `${GITHUB_API}/repos/${owner}/${repo}/contents/${path}`
      const body: Record<string, unknown> = {
        message: message ?? `Update ${path}`,
        content: btoa(content),
      }
      if (sha) body.sha = sha

      const response = await fetch(url, {
        method: 'PUT',
        headers: {
          Authorization: token,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      })

      const responseBody = await response.json()
      res.status(response.status).json(responseBody)
      return
    }

    // Method not allowed
    res.status(405).json({ message: `Method ${req.method} not allowed` })
  } catch (error: unknown) {
    const message =
      error instanceof Error ? error.message : 'Internal server error'
    res.status(500).json({ message })
  }
}
