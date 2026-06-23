import api from './client'

export async function loginRequest(email, password) {
  const { data } = await api.post('/api/auth/login', { email, password })
  return data
}

export async function registerRequest(name, email, password) {
  const { data } = await api.post('/api/auth/register', { name, email, password })
  return data
}
