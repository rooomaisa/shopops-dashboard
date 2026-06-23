import { useEffect, useState } from 'react'

export default function App() {
  const [apiStatus, setApiStatus] = useState('checking...')

  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((data) => setApiStatus(data.status ?? 'unknown'))
      .catch(() => setApiStatus('offline'))
  }, [])

  return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-4 bg-slate-900 text-white">
      <h1 className="text-3xl font-bold">ShopOps Dashboard</h1>
      <p className="text-slate-400">Phase 1 scaffold — API status: {apiStatus}</p>
    </div>
  )
}
