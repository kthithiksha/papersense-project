import React from 'react'
import { Routes, Route, Link, useLocation } from 'react-router-dom'
import Home from './components/Home.jsx'
import UploadPaper from './components/UploadPaper.jsx'
import AnalysisDashboard from './components/AnalysisDashboard.jsx'
import PaperHistory from './components/PaperHistory.jsx'

function Navbar() {
  const location = useLocation()

  const isActive = (path) => location.pathname === path

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        <span className="logo-mark">PS</span>
        PaperSense
      </Link>
      <div className="navbar-links">
        <Link to="/" className={isActive('/') ? 'active' : ''}>Home</Link>
        <Link to="/upload" className={isActive('/upload') ? 'active' : ''}>Upload</Link>
        <Link to="/history" className={isActive('/history') ? 'active' : ''}>History</Link>
      </div>
    </nav>
  )
}

export default function App() {
  return (
    <div className="app-shell">
      <Navbar />
      <div className="main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/upload" element={<UploadPaper />} />
          <Route path="/analysis/:paperId" element={<AnalysisDashboard />} />
          <Route path="/history" element={<PaperHistory />} />
        </Routes>
      </div>
    </div>
  )
}
