import { useEffect, useState } from 'react'
import { fetchProducts } from '../api/products'

export default function ProductsPage() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchProducts()
      .then(setProducts)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p className="text-slate-400">Loading products...</p>

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Products</h2>
      <div className="rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-800 text-slate-400">
            <tr>
              <th className="text-left p-3">Product</th>
              <th className="text-left p-3">SKU</th>
              <th className="text-right p-3">Price</th>
              <th className="text-right p-3">Stock</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id} className="border-t border-slate-700 hover:bg-slate-800/50">
                <td className="p-3 font-medium">{p.title}</td>
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
