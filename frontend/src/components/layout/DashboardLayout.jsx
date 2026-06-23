import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const navItems = [
  { to: '/dashboard', label: 'Dashboard', end: true },
  { to: '/products', label: 'Products' },
  { to: '/orders', label: 'Orders' },
  { to: '/alerts', label: 'Alerts' },
]

export default function DashboardLayout() {
  const { user, logout } = useAuth()

  return (
    <div className="min-h-screen bg-slate-900 text-white flex">
      <aside className="w-56 border-r border-slate-700 p-4 flex flex-col">
        <h1 className="text-lg font-bold text-emerald-400 mb-6">ShopOps</h1>
        <nav className="space-y-1 flex-1">
          {navItems.map(({ to, label, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `block rounded-lg px-3 py-2 text-sm ${
                  isActive ? 'bg-emerald-600/20 text-emerald-300' : 'text-slate-400 hover:text-white hover:bg-slate-800'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="border-t border-slate-700 pt-4 text-sm">
          <p className="text-slate-300 truncate">{user?.name}</p>
          <button type="button" onClick={logout} className="mt-2 text-slate-500 hover:text-white text-xs">
            Log out
          </button>
        </div>
      </aside>
      <main className="flex-1 p-6 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
