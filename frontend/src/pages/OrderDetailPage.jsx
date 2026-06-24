import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { fetchOrder, updateOrderStatus } from '../api/orders'
import StatusBadge from '../components/StatusBadge'
import DataSourceBadge from '../components/DataSourceBadge'
import { nextOrderActionLabel, nextOrderStatus, orderStatusClass } from '../utils/status'

export default function OrderDetailPage() {
  const { id } = useParams()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [updating, setUpdating] = useState(false)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    fetchOrder(id)
      .then(setOrder)
      .catch(() => setError('Order not found'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [id])

  const handleAdvance = async () => {
    const next = nextOrderStatus[order.internalStatus]
    if (!next) return
    setUpdating(true)
    setError('')
    try {
      const updated = await updateOrderStatus(id, next)
      setOrder(updated)
    } catch (err) {
      setError(err.response?.data?.message || 'Could not update status')
    } finally {
      setUpdating(false)
    }
  }

  if (loading) return <p className="text-slate-400">Loading order...</p>
  if (!order) return <p className="text-red-400">{error || 'Order not found'}</p>

  const nextStatus = nextOrderStatus[order.internalStatus]
  const actionLabel = nextOrderActionLabel[order.internalStatus]

  return (
    <div>
      <Link to="/orders" className="text-sm text-slate-400 hover:text-white">← Back to orders</Link>
      <div className="mt-4 flex items-start justify-between gap-4">
        <div>
          <div className="flex flex-wrap items-center gap-3">
            <h2 className="text-2xl font-bold">{order.orderNumber}</h2>
            <DataSourceBadge source={order.lastSyncedAt ? 'shopify' : 'demo'} />
          </div>
          <p className="text-slate-400 mt-1">{order.customerEmail ?? 'No customer email (demo or Shopify PII restricted)'}</p>
        </div>
        <StatusBadge label={order.internalStatus} className={orderStatusClass(order.internalStatus)} />
      </div>

      <div className="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
        <div className="rounded-lg bg-slate-800 p-4 border border-slate-700">
          <p className="text-slate-400">Shopify status</p>
          <p className="mt-1 font-medium">{order.shopifyStatus}</p>
        </div>
        <div className="rounded-lg bg-slate-800 p-4 border border-slate-700">
          <p className="text-slate-400">Total</p>
          <p className="mt-1 font-medium">€{order.totalPrice}</p>
        </div>
        <div className="rounded-lg bg-slate-800 p-4 border border-slate-700">
          <p className="text-slate-400">Order date</p>
          <p className="mt-1 font-medium">{new Date(order.orderCreatedAt).toLocaleDateString()}</p>
        </div>
      </div>

      <h3 className="text-lg font-semibold mt-8 mb-3">Line items</h3>
      <div className="rounded-xl border border-slate-700 overflow-hidden mb-6">
        <table className="w-full text-sm">
          <thead className="bg-slate-800 text-slate-400">
            <tr>
              <th className="text-left p-3">Item</th>
              <th className="text-right p-3">Qty</th>
              <th className="text-right p-3">Unit price</th>
            </tr>
          </thead>
          <tbody>
            {order.lines.map((line) => (
              <tr key={line.id} className="border-t border-slate-700">
                <td className="p-3">{line.title}</td>
                <td className="p-3 text-right">{line.quantity}</td>
                <td className="p-3 text-right">€{line.unitPrice}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {nextStatus && (
        <button
          type="button"
          onClick={handleAdvance}
          disabled={updating}
          className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium hover:bg-emerald-500 disabled:opacity-50"
        >
          {updating ? 'Updating...' : actionLabel}
        </button>
      )}

      {error && <p className="mt-3 text-red-400 text-sm">{error}</p>}
    </div>
  )
}
