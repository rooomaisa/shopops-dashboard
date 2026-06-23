import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'

export default function DashboardPage() {
  const { user, logout } = useAuth()
  const [apiStatus, setApiStatus] = useState('checking...')

  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((data) => setApiStatus(data.database ?? data.status ?? 'unknown'))
      .catch(() => setApiStatus('offline'))
  }, [])

  return (
    <div className="min-h-screen bg-slate-900 text-white">
      <header className="border-b border-slate-700 px-6 py-4 flex items-center justify-between">
        <h1 className="text-xl font-bold">ShopOps Dashboard</h1>
        <button
          type="button"
          onClick={logout}
          className="text-sm text-slate-400 hover:text-white"
        >
          Log out
        </button>
      </header>

      <main className="p-6 max-w-2xl">
        <p className="text-slate-300">
          Welcome, <span className="text-white font-medium">{user?.name}</span>
        </p>
        <p className="mt-2 text-slate-400 text-sm">
          Phase 3 complete — you are logged in. Database: <span className="text-emerald-400">{apiStatus}</span>
        </p>
        <p className="mt-4 text-slate-500 text-sm">
          Next up: products, orders, and alerts pages (Phase 4).
        </p>
      </main>
    </div>
  )
}
