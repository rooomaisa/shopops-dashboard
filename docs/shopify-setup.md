# Shopify setup (Phase 5)

ShopOps pulls products and orders from your **Shopify dev store** via the Admin REST API.

## 1. Create app in Dev Dashboard

1. Open **https://dev.shopify.com/dashboard**
2. **Apps** → **Create app** → name it (e.g. `ShopOps`)
3. Open the app → **Versions** tab:
   - App URL: `https://shopify.dev/apps/default-app-home`
   - Scopes: `read_products`, `read_orders`
   - Leave redirect URLs and app proxy empty
   - Click **Release**
4. **Home** tab (inside your app) → **Install app** on your dev store

## 2. Copy credentials

1. Open your app → **Settings**
2. Copy **Client ID** and **Client secret** (click Reveal)

There is no permanent token in the UI anymore — ShopOps fetches a short-lived token automatically.

## 3. Add to backend/.env

```env
SHOPIFY_STORE_DOMAIN=your-store.myshopify.com
SHOPIFY_CLIENT_ID=paste-client-id-here
SHOPIFY_CLIENT_SECRET=paste-client-secret-here
SHOPIFY_API_VERSION=2025-01
```

Remove any fake `SHOPIFY_ACCESS_TOKEN` line — you don't need it.

Restart the Spring Boot backend after saving `.env`.

## 4. Sync

1. Log in to ShopOps
2. Dashboard → **Sync now**

## Troubleshooting

- **`shop_not_permitted`**: Your dev store must be created from the Dev Dashboard (Winkels), not from the store admin.
- **App not installed**: Install from the app's **Home** tab before syncing.

## Mendix lens

Client credentials = Mendix REST consume with OAuth token refresh. Your microflow calls an auth endpoint first, caches the token, then calls the real API.
