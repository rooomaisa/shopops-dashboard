import api from './client'

export async function fetchOrders() {
  const { data } = await api.get('/api/orders')
  return data
}

export async function fetchOrder(id) {
  const { data } = await api.get(`/api/orders/${id}`)
  return data
}

export async function updateOrderStatus(id, internalStatus) {
  const { data } = await api.patch(`/api/orders/${id}/status`, { internalStatus })
  return data
}
