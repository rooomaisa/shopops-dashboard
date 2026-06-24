import { useEffect, useState } from 'react'
import { fetchProducts } from '../api/products'
import DataSourceBadge from '../components/DataSourceBadge'
import InfoBanner from '../components/InfoBanner'

export default function ProductsPage() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchProducts()
      .then(setProducts)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p className="text-slate-400">Loading products...</p>

  const shopifyCount = products.filter((p) => p.lastSyncedAt).length
  const demoCount = products.length - shopifyCount

  return (
    <div>
      <div className="flex flex-wrap items-center gap-3 mb-6">
        <h2 className="text-2xl font-bold">Products</h2>
        {shopifyCount > 0 && <DataSourceBadge source="shopify" />}
      </div>

      {shopifyCount > 0 && (
        <InfoBanner title="Catalog from Shopify" variant="info">
          {shopifyCount} product{shopifyCount === 1 ? '' : 's'} synced from your Shopify dev store
          {demoCount > 0 ? ` · ${demoCount} legacy demo item${demoCount === 1 ? '' : 's'} not updated yet` : ''}.
          Stock levels update when you click Sync on the dashboard.
        </InfoBanner>
      )}

      <div className="rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-800 text-slate-400">
            <tr>
              <th className="text-left p-3">Product</th>
              <th className="text-left p-3">Source</th>
              <th className="text-left p-3">SKU</th>
              <th className="text-right p-3">Price</th>
              <th className="text-right p-3">Stock</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id} className="border-t border-slate-700 hover:bg-slate-800/50">
                <td className="p-3 font-medium">{p.title}</td>
                <td className="p-3">
                  <DataSourceBadge source={p.lastSyncedAt ? 'shopify' : 'demo'} />
                </td>
                <td className="p-3 text-slate-400">{p.sku ?? '—'}</td>
                <td className="p-3 text-right">€{p.price}</td>
                <td className={`p-3 text-right font-medium ${p.inventoryQuantity < 5 ? 'text-red-400' : 'text-emerald-400'}`}>
                  {p.inventoryQuantity}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
