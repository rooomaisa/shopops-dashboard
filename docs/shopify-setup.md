# Shopify setup (Phase 5)

ShopOps pulls products and orders from your **Shopify dev store** via the Admin REST API.

## 1. Create a Custom App

1. Open your dev store admin: `https://YOUR-STORE.myshopify.com/admin`
2. **Settings → Apps and sales channels → Develop apps**
3. **Create an app** (e.g. `ShopOps Dashboard`)
4. **Configure Admin API scopes**:
   - `read_products`
   - `read_orders`
   - `read_inventory` (optional, for future phases)
5. **Install app** on your store
6. Copy the **Admin API access token** (`shpat_...`) — shown once after install

## 2. Add credentials to backend

In `backend/.env`:

```env
SHOPIFY_STORE_DOMAIN=your-store.myshopify.com
SHOPIFY_ACCESS_TOKEN=shpat_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
SHOPIFY_API_VERSION=2025-01
```

Restart the Spring Boot backend after changing `.env`.

## 3. Sync from the dashboard

1. Log in to ShopOps (`demo@shopops.dashboard` / `DemoShopOps2025!` or your account)
2. Open **Dashboard**
3. Click **Sync now**

Products and orders are upserted by Shopify ID. Existing orders keep their **internal** warehouse status; only Shopify fields are refreshed.

## Mendix lens

This is the same pattern as a Mendix **REST consume** action: call an external API, map JSON to entities, persist with upsert logic. Webhooks (Phase 6) are like Mendix **published REST** endpoints that Shopify calls.
