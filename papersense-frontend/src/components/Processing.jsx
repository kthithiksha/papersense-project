import React from 'react'

export default function Processing({ message = 'Analyzing your research paper...' }) {
  return (
    <div className="processing-panel">
      <div className="spinner"></div>
      <h2>{message}</h2>
      <p>This may take a moment for longer papers. Please don't close this tab.</p>
    </div>
  )
}
