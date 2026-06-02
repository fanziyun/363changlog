import type { VercelRequest, VercelResponse } from '@vercel/node'

const GITHUB_TOKEN_URL = 'https://github.com/login/oauth/access_token'

function setCorsHeaders(res: VercelResponse) {
  res.setHeader('Access-Control-Allow-Origin', '*')
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS')
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type')
}

export default async function handler(req: VercelRequest, res: VercelResponse) {
  // CORS preflight
  if (req.method === 'OPTIONS') {
    setCorsHeaders(res)
    return res.status(200).end()
  }

  setCorsHeaders(res)

  // Only allow POST
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' })
  }

  const { code } = req.body

  if (!code || typeof code !== 'string') {
    return res.status(400).json({ error: 'Missing required field: code' })
  }

  const clientId = process.env.GITHUB_CLIENT_ID
  const clientSecret = process.env.GITHUB_CLIENT_SECRET

  if (!clientId || !clientSecret) {
    return res.status(500).json({ error: 'Server configuration error: missing OAuth credentials' })
  }

  try {
    const response = await fetch(GITHUB_TOKEN_URL, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        client_id: clientId,
        client_secret: clientSecret,
        code,
      }),
    })

    const data = await response.json()

    // GitHub returns { error: "..." } on failure
    if (data.error) {
      return res.status(400).json({ error: data.error_description || data.error })
    }

    // Success — return the access token
    if (!data.access_token) {
      return res.status(502).json({ error: 'Unexpected response from GitHub' })
    }

    return res.status(200).json({ access_token: data.access_token })
  } catch (err) {
    return res.status(502).json({ error: 'Failed to communicate with GitHub' })
  }
}
