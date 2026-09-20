import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getAnalysis } from '../api/papersenseApi.js'
import Processing from './Processing.jsx'

function TextCard({ icon, title, text }) {
  return (
    <div className="analysis-card">
      <h3 className="card-title">
        <span className="card-icon">{icon}</span> {title}
      </h3>
      <p className="card-text">{text && text.trim() ? text : 'Not enough information was found for this section.'}</p>
    </div>
  )
}

function ListCard({ icon, title, items, fullWidth = false }) {
  return (
    <div className={`analysis-card ${fullWidth ? 'full-width' : ''}`}>
      <h3 className="card-title">
        <span className="card-icon">{icon}</span> {title}
      </h3>
      {items && items.length > 0 ? (
        <ul>
          {items.map((item, idx) => (
            <li key={idx}>{item}</li>
          ))}
        </ul>
      ) : (
        <p className="card-text">Nothing identified for this section.</p>
      )}
    </div>
  )
}

export default function AnalysisDashboard() {
  const { paperId } = useParams()
  const [analysis, setAnalysis] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let isMounted = true

    async function fetchAnalysis() {
      setLoading(true)
      setError(null)
      try {
        const data = await getAnalysis(paperId)
        if (isMounted) setAnalysis(data)
      } catch (err) {
        if (isMounted) setError(err.message || 'Failed to load analysis.')
      } finally {
        if (isMounted) setLoading(false)
      }
    }

    fetchAnalysis()
    return () => {
      isMounted = false
    }
  }, [paperId])

  if (loading) {
    return <Processing message="Loading analysis..." />
  }

  if (error) {
    return (
      <div>
        <div className="error-banner">{error}</div>
        <Link to="/upload" className="btn btn-secondary">← Back to Upload</Link>
      </div>
    )
  }

  if (!analysis) return null

  return (
    <div>
      <div className="dashboard-header">
        <div>
          <h1>Analysis Dashboard</h1>
          <p style={{ margin: 0 }}>Paper ID: {analysis.paperId}</p>
        </div>
        <Link to="/history" className="btn btn-secondary">View All Papers</Link>
      </div>

      <div className="card-grid">
        <TextCard icon="📋" title="Paper Overview" text={analysis.summary} />
        <TextCard icon="🎯" title="Problem Statement" text={analysis.problemStatement} />

        <ListCard icon="🛠️" title="Technologies / AI Techniques" items={analysis.technologies} />
        <TextCard icon="🧠" title="AI / Technology Explanation" text={analysis.aiExplanation} />

        <TextCard icon="🔬" title="Methodology" text={analysis.methodology} />

        <ListCard icon="✅" title="Strengths" items={analysis.advantages} />
        <ListCard icon="⚠️" title="Limitations" items={analysis.limitations} />

        {/* Research Gaps — the flagship feature, visually emphasized */}
        <div className="research-gap-card">
          <h3 className="card-title">
            <span className="card-icon">🔍</span> Research Gaps
            <span className="flagship-tag">Flagship Feature</span>
          </h3>
          {analysis.researchGaps && analysis.researchGaps.length > 0 ? (
            <ul>
              {analysis.researchGaps.map((gap, idx) => (
                <li key={idx}>{gap}</li>
              ))}
            </ul>
          ) : (
            <p className="card-text">No significant research gaps were identified.</p>
          )}
        </div>

        <ListCard icon="💡" title="Suggested Improvements" items={analysis.suggestedImprovements} fullWidth />
        <ListCard icon="🚀" title="Future Research Directions" items={analysis.futureScope} fullWidth />
      </div>
    </div>
  )
}
