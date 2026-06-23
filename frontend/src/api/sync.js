import api from './client'

export function fetchSyncStatus() {
  return api.get('/api/sync/status').then((res) => res.data)
}

export function syncProducts() {
  return api.post('/api/sync/products').then((res) => res.data)
}

export function syncOrders() {
  return api.post('/api/sync/orders').then((res) => res.data)
}

export function syncAll() {
  return api.post('/api/sync/all').then((res) => res.data)
}
