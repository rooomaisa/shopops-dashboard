import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchOrders } from '../api/orders'
import StatusBadge from '../components/StatusBadge'
import { orderStatusClass } from '../utils/status'

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchOrders()
      .then(setOrders)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p className="text-slate-400">Loading orders...</p>

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Orders</h2>
      <div className="rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-800 text-slate-400">
            <tr>
              <th className="text-left p-3">Order</th>
              <th className="text-left p-3">Customer</th>
              <th className="text-left p-3">Shopify</th>
              <th className="text-left p-3">Internal status</th>
              <th className="text-right p-3">Total</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.id} className="border-t border-slate-700 hover:bg-slate-800/50">
                <td className="p-3">
                  <Link to={`/orders/${o.id}`} className="text-emerald-400 hover:underline font-medium">
                    {o.orderNumber}
                  </Link>
                </td>
                <td className="p-3 text-slate-400">{o.customerEmail ?? '—'}</td>
                <td className="p-3 text-slate-400">{o.shopifyStatus}</td>
                <td className="p-3">
                  <StatusBadge label={o.internalStatus} className={orderStatusClass(o.internalStatus)} />
                </td>
                <td className="p-3 text-right">€{o.totalPrice}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
