# ShopOps Dashboard — Portfolio Case Study

**Internal ops dashboard for e-commerce** — live Shopify product sync + warehouse fulfillment workflow.

## Elevator pitch (30 seconds)

> ShopOps sits next to Shopify. Shopify handles sales; ShopOps handles operations — which orders need picking, what's low on stock, and moving orders through our internal workflow. I built a REST integration that syncs the product catalog from a real Shopify dev store. Order workflow runs on internal data because Shopify's Dev Dashboard blocks order API access until customer-data approval — a real-world integration constraint I documented in the UI.

## What works today

| Feature | Data source | Demo value |
|---------|-------------|------------|
| Product catalog | **Live from Shopify** (Admin API) | Proves external integration |
| Order workflow | Demo / internal data | Proves business logic & UI |
| Stock alerts | Seed + future sync | Proves domain rules |
| Auth | JWT + PostgreSQL | Standard full-stack pattern |

## Architecture

```
┌─────────────┐     REST API      ┌──────────────┐     JPA      ┌────────────┐
│   Shopify   │ ────────────────► │ Spring Boot  │ ───────────► │ PostgreSQL │
│  dev store  │   products sync   │   backend    │              │  Supabase  │
└─────────────┘                   └──────┬───────┘              └────────────┘
                                         │ REST + JWT
                                         ▼
                                  ┌──────────────┐
                                  │ React + Vite │
                                  │  dashboard   │
                                  └──────────────┘
```

## Mendix mapping (for interviews)

| Mendix | ShopOps |
|--------|---------|
| Entity + associations | Product, Order, OrderLine, StockAlert |
| Enumerations | InternalOrderStatus, AlertStatus |
| Microflow | Order status transitions, stock threshold check |
| REST consume | Shopify product sync (client credentials OAuth) |
| Pages | Dashboard, Products, Orders, Alerts |

## Shopify integration notes

- **Auth:** Client ID + secret → short-lived token (Shopify Dev Dashboard, 2026 flow)
- **Products:** `GET /admin/api/.../products.json` — working
- **Orders:** Blocked by Shopify protected-customer-data policy on Dev Dashboard apps — documented in UI with "Demo workflow" badges

## Demo login

- URL: `http://localhost:5174` (local)
- Email: `demo@shopops.dashboard`
- Password: `DemoShopOps2025!`

## Tech stack

React · Vite · Tailwind · Java 21 · Spring Boot 3 · PostgreSQL · Shopify Admin API

## Author

Romy van Dam — [GitHub](https://github.com/rooomaisa)
