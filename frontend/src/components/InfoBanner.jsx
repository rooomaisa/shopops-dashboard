export default function InfoBanner({ title, children, variant = 'neutral' }) {
  const styles = {
    neutral: 'border-slate-600 bg-slate-800/80 text-slate-300',
    info: 'border-sky-500/30 bg-sky-500/10 text-sky-100',
  }

  return (
    <div className={`rounded-xl border p-4 text-sm mb-6 ${styles[variant]}`}>
      {title && <p className="font-medium text-white mb-1">{title}</p>}
      <div className="text-slate-300 leading-relaxed">{children}</div>
    </div>
  )
}
