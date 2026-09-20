import React, { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { uploadPaper, analyzePaper } from '../api/papersenseApi.js'
import Processing from './Processing.jsx'

function formatFileSize(bytes) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
}

export default function UploadPaper() {
  const navigate = useNavigate()
  const fileInputRef = useRef(null)

  const [selectedFile, setSelectedFile] = useState(null)
  const [isDragging, setIsDragging] = useState(false)
  const [uploadStatus, setUploadStatus] = useState('idle') // idle | uploading | uploaded
  const [uploadedPaperId, setUploadedPaperId] = useState(null)
  const [isAnalyzing, setIsAnalyzing] = useState(false)
  const [error, setError] = useState(null)

  const resetSelection = () => {
    setSelectedFile(null)
    setUploadStatus('idle')
    setUploadedPaperId(null)
    setError(null)
  }

  const handleFileChosen = (file) => {
    setError(null)
    if (!file) return
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      setError('Only PDF files are supported.')
      return
    }
    setSelectedFile(file)
    setUploadStatus('idle')
    setUploadedPaperId(null)
  }

  const handleDrop = (e) => {
    e.preventDefault()
    setIsDragging(false)
    const file = e.dataTransfer.files?.[0]
    handleFileChosen(file)
  }

  const handleUpload = async () => {
    if (!selectedFile) return
    setUploadStatus('uploading')
    setError(null)
    try {
      const response = await uploadPaper(selectedFile)
      setUploadedPaperId(response.paperId)
      setUploadStatus('uploaded')
    } catch (err) {
      setError(err.message || 'Upload failed. Please try again.')
      setUploadStatus('idle')
    }
  }

  const handleAnalyze = async () => {
    if (!uploadedPaperId) return
    setIsAnalyzing(true)
    setError(null)
    try {
      await analyzePaper(uploadedPaperId)
      navigate(`/analysis/${uploadedPaperId}`)
    } catch (err) {
      setError(err.message || 'Analysis failed. Please try again.')
      setIsAnalyzing(false)
    }
  }

  if (isAnalyzing) {
    return <Processing message="Analyzing your research paper..." />
  }

  return (
    <div>
      <h1>Upload a Research Paper</h1>
      <p>PDF files only, up to 15MB.</p>

      {error && <div className="error-banner">{error}</div>}

      <div
        className={`dropzone ${isDragging ? 'dragging' : ''}`}
        onClick={() => fileInputRef.current?.click()}
        onDragOver={(e) => {
          e.preventDefault()
          setIsDragging(true)
        }}
        onDragLeave={() => setIsDragging(false)}
        onDrop={handleDrop}
      >
        <div className="dropzone-icon">📄</div>
        <p style={{ margin: 0, fontWeight: 600, color: 'var(--color-text)' }}>
          Drag & drop your PDF here, or click to browse
        </p>
        <input
          ref={fileInputRef}
          type="file"
          accept="application/pdf"
          style={{ display: 'none' }}
          onChange={(e) => handleFileChosen(e.target.files?.[0])}
        />
      </div>

      {selectedFile && (
        <div className="file-card">
          <div className="file-meta">
            <div className="file-icon">📄</div>
            <div>
              <p className="file-name">{selectedFile.name}</p>
              <p className="file-size">{formatFileSize(selectedFile.size)}</p>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
            {uploadStatus === 'uploaded' ? (
              <span className="status-badge success">✓ Uploaded</span>
            ) : uploadStatus === 'uploading' ? (
              <span className="status-badge pending">Uploading...</span>
            ) : null}

            {uploadStatus === 'uploaded' ? (
              <button className="btn btn-primary" onClick={handleAnalyze}>
                🔍 Analyze Paper
              </button>
            ) : (
              <button
                className="btn btn-primary"
                onClick={handleUpload}
                disabled={uploadStatus === 'uploading'}
              >
                {uploadStatus === 'uploading' ? 'Uploading...' : 'Upload'}
              </button>
            )}

            <button className="btn btn-secondary" onClick={resetSelection}>
              Remove
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
