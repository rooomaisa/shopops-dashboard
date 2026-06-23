import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchDashboardStats } from '../api/dashboard'

function StatCard({ label, value, to }) {
  return (
    <Link to={to} className="block rounded-xl bg-slate-800 border border-slate-700 p-5 hover:border-emerald-600/50 transition">
      <p className="text-slate-400 text-sm">{label}</p>
      <p className="text-3xl font-bold mt-1">{value}</p>
    </Link>
  )
}

export default function DashboardPage() {
  const [stats, setStats] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchDashboardStats()
      .then(setStats)
      .catch(() => setError('Could not load dashboard stats'))
  }, [])

  if (error) {
    return <p className="text-red-400">{error}</p>
  }

  if (!stats) {
    return <p className="text-slate-400">Loading...</p>
  }

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Dashboard</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Products" value={stats.totalProducts} to="/products" />
        <StatCard label="Open orders" value={stats.openOrders} to="/orders" />
        <StatCard label="Processing" value={stats.processingOrders} to="/orders" />
        <StatCard label="Open alerts" value={stats.openAlerts} to="/alerts" />
      </div>
    </div>
  )
}
