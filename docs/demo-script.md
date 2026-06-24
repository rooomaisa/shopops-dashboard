# 2-minute demo script (for interviews)

Practice this on your **live Vercel URL** before a call.

---

## Setup (before the interview)

- Open the app in one tab
- Have this script on another screen
- Render free tier: click the app once 1 minute early so it’s awake

**Login:** `demo@shopops.dashboard` / `DemoShopOps2025!`

---

## The script (~2 minutes)

### 1. The problem (15 sec)

> "Small shops use Shopify to sell, but warehouse staff need a separate tool to see what to ship and what’s low on stock. ShopOps is that internal ops dashboard."

### 2. Dashboard (20 sec)

> "This is the overview — open orders, processing count, alerts. Down here I sync the product catalog from Shopify with one click."

*Click **Sync now** if you want to show live integration.*

### 3. Products (25 sec)

> "Products with the green badge are **live from Shopify** — real REST integration with OAuth client credentials. Stock and prices come from my dev store."

*Open **Products** in the sidebar.*

### 4. Orders / workflow (35 sec)

> "Orders use **demo workflow data** — Shopify blocks order API on dev apps without customer-data approval. But this is the core business logic: internal status separate from Shopify’s payment status."

*Open **Orders** → click an order → **Mark as processing**.*

> "Warehouse staff advance orders: New → Processing → Shipped → Completed. In Mendix this would be a microflow with enumeration transitions."

### 5. Wrap-up (15 sec)

> "Stack is React, Spring Boot, PostgreSQL on Supabase, Shopify Admin API. Built with feature branches and PRs — happy to walk through the domain model or integration."

---

## If they ask about Shopify orders

> "Product sync is live. Order sync needs protected-customer-data approval from Shopify — their Dev Dashboard doesn’t expose that UI yet. The workflow is fully built; only the external order pull is gated by Shopify policy."

---

## If something breaks live

- Render sleeping → refresh, wait 60 seconds
- Login fails → mention you’d check CORS and API URL in production config
- Stay calm — explaining architecture counts more than a perfect click-through
