// VITE_API_BASE_URL is set in Vercel for production and defaults locally.
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api')
  .replace(/\/$/, '')

/**
 * Small helper that parses JSON responses and turns non-OK responses
 * into thrown Errors with a readable message (using the backend's
 * { "error": "..." } shape when available).
 */
async function handleResponse(response) {
  let data = null
  try {
    data = await response.json()
  } catch (e) {
    // No JSON body (e.g. 204 No Content) — that's fine.
  }

  if (!response.ok) {
    const message = data && data.error ? data.error : `Request failed with status ${response.status}`
    throw new Error(message)
  }

  return data
}

export async function uploadPaper(file) {
  const formData = new FormData()
  formData.append('paper', file)

  const response = await fetch(`${API_BASE_URL}/papers/upload`, {
    method: 'POST',
    body: formData,
  })

  return handleResponse(response)
}

export async function analyzePaper(paperId) {
  const response = await fetch(`${API_BASE_URL}/papers/${paperId}/analyze`, {
    method: 'POST',
  })

  return handleResponse(response)
}

export async function getAnalysis(paperId) {
  const response = await fetch(`${API_BASE_URL}/papers/${paperId}/analysis`)
  return handleResponse(response)
}

export async function getAllPapers() {
  const response = await fetch(`${API_BASE_URL}/papers`)
  return handleResponse(response)
}

export async function deletePaper(paperId) {
  const response = await fetch(`${API_BASE_URL}/papers/${paperId}`, {
    method: 'DELETE',
  })
  return handleResponse(response)
}
