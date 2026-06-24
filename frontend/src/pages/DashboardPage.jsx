import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchDashboardStats } from '../api/dashboard'
import { fetchSyncStatus, syncAll } from '../api/sync'
import InfoBanner from '../components/InfoBanner'

function StatCard({ label, value, to }) {
  return (
    <Link to={to} className="block rounded-xl bg-slate-800 border border-slate-700 p-5 hover:border-emerald-600/50 transition">
      <p className="text-slate-400 text-sm">{label}</p>
      <p className="text-3xl font-bold mt-1">{value}</p>
    </Link>
  )
}

function formatSyncedAt(value) {
  if (!value) return 'Never'
  return new Date(value).toLocaleString()
}

export default function DashboardPage() {
  const [stats, setStats] = useState(null)
  const [syncStatus, setSyncStatus] = useState(null)
  const [error, setError] = useState('')
  const [warning, setWarning] = useState('')
  const [syncMessage, setSyncMessage] = useState('')
  const [syncing, setSyncing] = useState(false)

  const load = useCallback(() => {
    Promise.all([fetchDashboardStats(), fetchSyncStatus()])
      .then(([dashboardStats, status]) => {
        setStats(dashboardStats)
        setSyncStatus(status)
      })
      .catch(() => setError('Could not load dashboard'))
  }, [])

  useEffect(() => {
    load()
  }, [load])

  async function handleSync() {
    setSyncing(true)
    setSyncMessage('')
    setError('')
    setWarning('')
    try {
      const response = await syncAll()
      const results = response.results ?? response
      const warnings = response.warnings ?? []
      const summary = results
        .map((r) => `${r.resource}: ${r.created} new, ${r.updated} updated`)
        .join(' · ')
      setSyncMessage(`Sync complete — ${summary}`)
      if (warnings.length > 0) {
        setWarning(warnings.join(' '))
      }
      load()
    } catch (err) {
      setError(err.response?.data?.message || 'Sync failed. Check Shopify credentials in backend/.env')
    } finally {
      setSyncing(false)
    }
  }

  if (error && !stats) {
    return <p className="text-red-400">{error}</p>
  }

  if (!stats) {
    return <p className="text-slate-400">Loading...</p>
  }

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Dashboard</h2>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard label="Products" value={stats.totalProducts} to="/products" />
        <StatCard label="Open orders" value={stats.openOrders} to="/orders" />
        <StatCard label="Processing" value={stats.processingOrders} to="/orders" />
        <StatCard label="Open alerts" value={stats.openAlerts} to="/alerts" />
      </div>

      {syncStatus?.shopifyConfigured && (
        <InfoBanner title="How this demo works" variant="info">
          <strong className="text-white">Products</strong> sync live from Shopify ({syncStatus.shopifySyncedProductCount} synced).
          {' '}<strong className="text-white">Orders</strong> use internal workflow data
          {syncStatus.shopifySyncedOrderCount > 0
            ? ` (${syncStatus.shopifySyncedOrderCount} from Shopify, ${syncStatus.demoOrderCount} demo)`
            : ` (${syncStatus.demoOrderCount} demo orders — Shopify blocks order API on dev apps)`}.
          Click an order and advance it through Processing → Shipped to see the ops flow.
        </InfoBanner>
      )}

      <section className="rounded-xl bg-slate-800 border border-slate-700 p-5">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h3 className="text-lg font-semibold">Shopify sync</h3>
            <p className="text-slate-400 text-sm mt-1">
              Pull your product catalog from Shopify. Order workflow uses internal demo data.
            </p>
            {syncStatus && (
              <p className="text-slate-500 text-xs mt-2">
                Products: {formatSyncedAt(syncStatus.productsLastSyncedAt)} · Orders:{' '}
                {formatSyncedAt(syncStatus.ordersLastSyncedAt)}
              </p>
            )}
            {!syncStatus?.shopifyConfigured && (
              <p className="text-amber-400 text-xs mt-2">
                Shopify not configured — add SHOPIFY_STORE_DOMAIN, SHOPIFY_CLIENT_ID and SHOPIFY_CLIENT_SECRET to backend/.env
              </p>
            )}
          </div>
          <button
            type="button"
            onClick={handleSync}
            disabled={syncing || !syncStatus?.shopifyConfigured}
            className="shrink-0 rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium hover:bg-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {syncing ? 'Syncing…' : 'Sync now'}
          </button>
        </div>
        {syncMessage && <p className="text-emerald-400 text-sm mt-3">{syncMessage}</p>}
        {warning && <p className="text-amber-400 text-sm mt-3">{warning}</p>}
        {error && stats && <p className="text-red-400 text-sm mt-3">{error}</p>}
      </section>
    </div>
  )
}
