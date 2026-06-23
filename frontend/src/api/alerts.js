import api from './client'

export async function fetchAlerts() {
  const { data } = await api.get('/api/alerts')
  return data
}

export async function acknowledgeAlert(id) {
  const { data } = await api.patch(`/api/alerts/${id}/acknowledge`)
  return data
}

export async function resolveAlert(id) {
  const { data } = await api.patch(`/api/alerts/${id}/resolve`)
  return data
}
