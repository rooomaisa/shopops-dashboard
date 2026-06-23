# ShopOps Dashboard

**Internal operations dashboard for e-commerce** — sync products and orders from Shopify, track fulfillment, and get low-stock alerts.

> Portfolio project demonstrating business application development: domain modeling, workflow automation, and external system integration (Shopify Admin API + webhooks).

**Status:** Phase 1 scaffold complete — backend + frontend run locally

---

## The problem

Small e-commerce businesses sell through **Shopify**, but Shopify is built for *selling*, not for *operating*:

- Warehouse staff have no single view of which orders still need shipping
- Low-stock products are easy to miss until a customer orders something unavailable
- Ops teams must log into Shopify admin for tasks that could live in a focused internal tool

## The solution

ShopOps Dashboard sits **next to** Shopify (not replacing it):

```
Shopify (sales)  →  sync  →  ShopOps (operations)  →  alerts + fulfillment workflow
```

1. **Sync** products and orders from a Shopify development store
2. **Track** internal order status: New → Processing → Shipped → Completed
3. **Alert** when product stock drops below a threshold
4. **Acknowledge** and resolve stock alerts from a dedicated dashboard

## Architecture (planned)

| Layer | Technology |
|-------|------------|
| Frontend | React, Vite, Tailwind CSS |
| Backend | Java 21, Spring Boot |
| Database | PostgreSQL (Supabase) |
| External | Shopify Admin API + webhooks |
| Deployment | Vercel (frontend) + Render (backend) |

See [docs/domain-model.md](docs/domain-model.md) for the data model and business rules.

## Relevance to low-code / Mendix

This project mirrors how Mendix developers build business apps:

| Mendix concept | ShopOps implementation |
|----------------|------------------------|
| Domain model | Product, Order, OrderLine, StockAlert, User |
| Microflows | Low-stock check, order status transitions |
| Pages | Dashboard, product list, order detail, alerts |
| REST integration | Shopify Admin API sync |
| Event handlers | Shopify webhooks for real-time updates |

## Development approach

Built incrementally with git branches and pull requests — one feature per PR.

| Phase | Branch | Focus |
|-------|--------|-------|
| 0 | `docs/domain-model` | Planning, domain model, Shopify dev store |
| 1 | `chore/project-scaffold` | Spring Boot + React scaffold |
| 2 | `feat/data-model` | Database entities and migrations |
| 3 | `feat/auth` | JWT authentication |
| 4 | `feat/core-crud` | Dashboard UI with seed data |
| 5 | `feat/shopify-sync` | Shopify Admin API integration |
| 6 | `feat/shopify-webhooks` | Real-time webhook updates |
| 7 | `feat/stock-alerts` | Business logic + alerts UI |
| 8 | `feat/deploy-and-docs` | Deploy + portfolio case study |

## Author

Romy van Dam — [GitHub](https://github.com/rooomaisa)
