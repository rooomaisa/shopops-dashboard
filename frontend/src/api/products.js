import api from './client'

export async function fetchProducts() {
  const { data } = await api.get('/api/products')
  return data
}
