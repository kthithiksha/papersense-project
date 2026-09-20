import React from 'react'
import { useNavigate } from 'react-router-dom'

const FEATURES = [
  {
    icon: '📄',
    title: 'Paper Overview',
    description: 'Get a clear summary and problem statement in plain language.',
  },
  {
    icon: '🧠',
    title: 'AI Technique Explanations',
    description: 'Understand the AI/ML methods used, explained simply.',
  },
  {
    icon: '⚖️',
    title: 'Strengths & Limitations',
    description: 'See the paper\'s advantages and shortcomings side by side.',
  },
  {
    icon: '🔍',
    title: 'Research Gap Finder',
    description: 'Our flagship feature — surfaces unexplored areas and future directions.',
  },
]

export default function Home() {
  const navigate = useNavigate()

  return (
    <div>
      <section className="hero">
        <h1>Understand Research. Discover Gaps.</h1>
        <p className="subtitle">
          Upload a research paper and let PaperSense explain the research, identify
          limitations, and discover opportunities for future work.
        </p>
        <button className="btn btn-primary" onClick={() => navigate('/upload')}>
          📤 Upload a Paper
        </button>

        <div className="feature-grid">
          {FEATURES.map((feature) => (
            <div className="feature-card" key={feature.title}>
              <div className="feature-icon">{feature.icon}</div>
              <h3>{feature.title}</h3>
              <p>{feature.description}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
