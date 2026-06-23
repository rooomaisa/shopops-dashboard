const orderStatusStyles = {
  NEW: 'bg-blue-500/20 text-blue-300',
  PROCESSING: 'bg-amber-500/20 text-amber-300',
  SHIPPED: 'bg-purple-500/20 text-purple-300',
  COMPLETED: 'bg-emerald-500/20 text-emerald-300',
}

const alertStatusStyles = {
  OPEN: 'bg-red-500/20 text-red-300',
  ACKNOWLEDGED: 'bg-amber-500/20 text-amber-300',
  RESOLVED: 'bg-slate-500/20 text-slate-400',
}

export function orderStatusClass(status) {
  return orderStatusStyles[status] ?? 'bg-slate-500/20 text-slate-300'
}

export function alertStatusClass(status) {
  return alertStatusStyles[status] ?? 'bg-slate-500/20 text-slate-300'
}

export const nextOrderStatus = {
  NEW: 'PROCESSING',
  PROCESSING: 'SHIPPED',
  SHIPPED: 'COMPLETED',
}

export const nextOrderActionLabel = {
  NEW: 'Start processing',
  PROCESSING: 'Mark shipped',
  SHIPPED: 'Complete',
}
