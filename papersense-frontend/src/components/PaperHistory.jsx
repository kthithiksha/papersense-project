import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getAllPapers, deletePaper, analyzePaper } from '../api/papersenseApi.js'
import Processing from './Processing.jsx'

function formatDate(isoString) {
  if (!isoString) return ''
  const date = new Date(isoString)
  return date.toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default function PaperHistory() {
  const navigate = useNavigate()
  const [papers, setPapers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [busyPaperId, setBusyPaperId] = useState(null)

  const loadPapers = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await getAllPapers()
      setPapers(data)
    } catch (err) {
      setError(err.message || 'Failed to load papers.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadPapers()
  }, [])

  const handleDelete = async (paperId) => {
    setBusyPaperId(paperId)
    try {
      await deletePaper(paperId)
      setPapers((prev) => prev.filter((p) => p.id !== paperId))
    } catch (err) {
      setError(err.message || 'Failed to delete paper.')
    } finally {
      setBusyPaperId(null)
    }
  }

  const handleAnalyzeAndView = async (paperId) => {
    setBusyPaperId(paperId)
    try {
      await analyzePaper(paperId)
      navigate(`/analysis/${paperId}`)
    } catch (err) {
      setError(err.message || 'Failed to analyze paper.')
      setBusyPaperId(null)
    }
  }

  if (loading) {
    return <Processing message="Loading your papers..." />
  }

  return (
    <div>
      <h1>Paper History</h1>
      <p>All papers you've uploaded to PaperSense.</p>

      {error && <div className="error-banner">{error}</div>}

      {papers.length === 0 ? (
        <div className="empty-state">
          <p>No papers uploaded yet.</p>
          <Link to="/upload" className="btn btn-primary">Upload your first paper</Link>
        </div>
      ) : (
        <div className="history-list">
          {papers.map((paper) => (
            <div className="history-item" key={paper.id}>
              <div>
                <p className="paper-title">{paper.title}</p>
                <p className="paper-meta">
                  {paper.fileName} · Uploaded {formatDate(paper.uploadedAt)} ·{' '}
                  {paper.analyzed ? (
                    <span className="status-badge success">Analyzed</span>
                  ) : (
                    <span className="status-badge pending">Not analyzed</span>
                  )}
                </p>
              </div>

              <div className="history-actions">
                {paper.analyzed ? (
                  <button
                    className="btn btn-secondary"
                    onClick={() => navigate(`/analysis/${paper.id}`)}
                  >
                    View Analysis
                  </button>
                ) : (
                  <button
                    className="btn btn-primary"
                    onClick={() => handleAnalyzeAndView(paper.id)}
                    disabled={busyPaperId === paper.id}
                  >
                    {busyPaperId === paper.id ? 'Analyzing...' : 'Analyze'}
                  </button>
                )}
                <button
                  className="btn btn-danger"
                  onClick={() => handleDelete(paper.id)}
                  disabled={busyPaperId === paper.id}
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
