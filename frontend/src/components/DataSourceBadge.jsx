const STYLES = {
  shopify: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30',
  demo: 'bg-amber-500/15 text-amber-300 border-amber-500/30',
}

export default function DataSourceBadge({ source, className = '' }) {
  const label = source === 'shopify' ? 'Live from Shopify' : 'Demo workflow'
  return (
    <span
      className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium ${STYLES[source]} ${className}`}
    >
      {label}
    </span>
  )
}
