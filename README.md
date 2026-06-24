# ShopOps Dashboard

**Internal operations dashboard for e-commerce** — sync products from Shopify, run warehouse order workflows, and track low-stock alerts.

[![Live Demo](https://img.shields.io/badge/demo-live-22c55e?style=for-the-badge)](https://shopops-dashboard.vercel.app)
[![React](https://img.shields.io/badge/React-19-61dafb?style=flat-square&logo=react&logoColor=white)](https://react.dev)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6db33f?style=flat-square&logo=spring&logoColor=white)](https://spring.io)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169e1?style=flat-square&logo=postgresql&logoColor=white)](https://supabase.com)
[![Shopify](https://img.shields.io/badge/Shopify-Admin_API-95bf47?style=flat-square&logo=shopify&logoColor=white)](https://shopify.dev)

> Built as a portfolio project for **business application development** — domain modeling, workflow states, REST integrations, and a deployable full-stack app.

---

## Try it live

| | |
|---|---|
| **App** | **[shopops-dashboard.vercel.app](https://shopops-dashboard.vercel.app)** |
| **Login** | `demo@shopops.dashboard` |
| **Password** | `DemoShopOps2025!` |

Click **Sync now** on the dashboard to pull products from a real Shopify dev store.

---

## What it does

ShopOps sits **next to** Shopify — Shopify handles sales, ShopOps handles operations:

- **Product catalog** — live sync from Shopify Admin API (REST + OAuth client credentials)
- **Order workflow** — internal status flow: New → Processing → Shipped → Completed
- **Stock alerts** — low-inventory warnings for warehouse staff
- **Clear data labels** — live Shopify data vs demo workflow data, shown in the UI

```
Shopify (sales)  →  product sync  →  ShopOps (ops dashboard)  →  workflow + alerts
```

---

## Screenshots

### Dashboard
![ShopOps dashboard with stats and Shopify sync](docs/screenshots/dashboard.png)

### Products — live from Shopify
![Product list with Live from Shopify badges](docs/screenshots/products.png)

### Orders — internal fulfillment workflow
![Orders list with workflow status badges](docs/screenshots/orders.png)

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Frontend | React, Vite, Tailwind CSS |
| Backend | Java 21, Spring Boot 3, JWT auth |
| Database | PostgreSQL (Supabase) |
| Integration | Shopify Admin API |
| Hosting | Vercel (frontend) + Render (backend) |

**API health:** [shopops-api-d83u.onrender.com/api/health](https://shopops-api-d83u.onrender.com/api/health)

---

## Why this project (Mendix / low-code angle)

Mirrors how business apps are built on platforms like Mendix:

| Concept | In ShopOps |
|---------|------------|
| Domain model | Product, Order, OrderLine, StockAlert, User |
| Enumerations | Order status, alert status |
| Microflows | Status transitions, stock threshold checks |
| REST consume | Shopify product sync |
| Pages | Dashboard, products, orders, alerts |

---

## Run locally

```bash
# Backend — copy backend/.env.example → backend/.env first
cd backend && ./mvnw spring-boot:run

# Frontend
cd frontend && npm run dev
```

Open `http://localhost:5174` — same demo login as above.

---

## Project docs

| Doc | Purpose |
|-----|---------|
| [domain-model.md](docs/domain-model.md) | Entities, business rules |
| [portfolio-case-study.md](docs/portfolio-case-study.md) | Interview talking points |
| [deploy.md](docs/deploy.md) | Render + Vercel setup |

---

## Author

**Romy van Dam** — junior developer · Mendix traineeship candidate  
[GitHub](https://github.com/rooomaisa) · [Live demo](https://shopops-dashboard.vercel.app)
