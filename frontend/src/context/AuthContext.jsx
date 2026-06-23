import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { loginRequest, registerRequest } from '../api/auth'

const AuthContext = createContext(null)

function readStoredUser() {
  const raw = localStorage.getItem('shopops_user')
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)
  const [token, setToken] = useState(() => localStorage.getItem('shopops_token'))
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(false)
  }, [])

  const persistAuth = (authData) => {
    localStorage.setItem('shopops_token', authData.token)
    localStorage.setItem('shopops_user', JSON.stringify(authData.user))
    setToken(authData.token)
    setUser(authData.user)
  }

  const login = async (email, password) => {
    const authData = await loginRequest(email, password)
    persistAuth(authData)
    return authData
  }

  const register = async (name, email, password) => {
    const authData = await registerRequest(name, email, password)
    persistAuth(authData)
    return authData
  }

  const logout = () => {
    localStorage.removeItem('shopops_token')
    localStorage.removeItem('shopops_user')
    setToken(null)
    setUser(null)
  }

  const value = useMemo(
    () => ({
      user,
      token,
      loading,
      isAuthenticated: Boolean(token),
      login,
      register,
      logout,
    }),
    [user, token, loading]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
