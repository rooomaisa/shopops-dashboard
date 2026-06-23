import { useEffect, useState } from 'react'
import { acknowledgeAlert, fetchAlerts, resolveAlert } from '../api/alerts'
import StatusBadge from '../components/StatusBadge'
import { alertStatusClass } from '../utils/status'

export default function AlertsPage() {
  const [alerts, setAlerts] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    fetchAlerts()
      .then(setAlerts)
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  const handleAcknowledge = async (id) => {
    await acknowledgeAlert(id)
    load()
  }

  const handleResolve = async (id) => {
    await resolveAlert(id)
    load()
  }

  if (loading) return <p className="text-slate-400">Loading alerts...</p>

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Stock alerts</h2>
      <div className="rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-800 text-slate-400">
            <tr>
              <th className="text-left p-3">Product</th>
              <th className="text-left p-3">Status</th>
              <th className="text-right p-3">Stock at alert</th>
              <th className="text-right p-3">Threshold</th>
              <th className="text-right p-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            {alerts.map((a) => (
              <tr key={a.id} className="border-t border-slate-700 hover:bg-slate-800/50">
                <td className="p-3 font-medium">{a.productTitle}</td>
                <td className="p-3">
                  <StatusBadge label={a.status} className={alertStatusClass(a.status)} />
                </td>
                <td className="p-3 text-right text-red-400">{a.quantityAtAlert}</td>
                <td className="p-3 text-right text-slate-400">{a.threshold}</td>
                <td className="p-3 text-right space-x-2">
                  {a.status === 'OPEN' && (
                    <button
                      type="button"
                      onClick={() => handleAcknowledge(a.id)}
                      className="text-xs text-amber-400 hover:underline"
                    >
                      Acknowledge
                    </button>
                  )}
                  {a.status !== 'RESOLVED' && (
                    <button
                      type="button"
                      onClick={() => handleResolve(a.id)}
                      className="text-xs text-emerald-400 hover:underline"
                    >
                      Resolve
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {alerts.length === 0 && (
          <p className="p-6 text-center text-slate-500">No alerts yet.</p>
        )}
      </div>
    </div>
  )
}
