# ShopOps — Domain Model

> Phase 0 artifact. This document is the Mendix-style "domain model" — designed **before** writing code.

## Business context

**Fictional client:** Bloom & Co., a small online shop selling handmade products via Shopify.

**Pain points:**
- Owner checks Shopify manually for new orders
- No alert when popular items run low on stock
- No internal tracking of "we packed this order" vs "still waiting"

**ShopOps solves:** A separate ops dashboard that syncs from Shopify and adds business logic Shopify does not provide.

---

## Entities

### User
Represents someone who logs into ShopOps (shop owner or warehouse staff).

| Attribute | Type | Notes |
|-----------|------|-------|
| id | UUID | Primary key |
| email | String | Unique, used for login |
| passwordHash | String | Never store plain passwords |
| name | String | Display name |
| createdAt | DateTime | Auto-set on creation |

### Product
Synced from Shopify. ShopOps reads product data; Shopify remains the source of truth for catalog.

| Attribute | Type | Notes |
|-----------|------|-------|
| id | UUID | Internal primary key |
| shopifyId | String | Unique — Shopify product ID (for sync upsert) |
| title | String | Product name |
| sku | String | Stock keeping unit (nullable) |
| price | Decimal | Current price |
| inventoryQuantity | Integer | Stock level from Shopify |
| imageUrl | String | Product image (nullable) |
| lastSyncedAt | DateTime | When we last pulled from Shopify |

### Order
Synced from Shopify. ShopOps adds an **internal** fulfillment status on top of Shopify's order data.

| Attribute | Type | Notes |
|-----------|------|-------|
| id | UUID | Internal primary key |
| shopifyId | String | Unique — Shopify order ID |
| orderNumber | String | Human-readable (#1001) |
| shopifyStatus | String | Raw status from Shopify (e.g. `paid`, `fulfilled`) |
| internalStatus | Enum | **Our** workflow: NEW, PROCESSING, SHIPPED, COMPLETED |
| totalPrice | Decimal | Order total |
| customerEmail | String | Customer contact |
| createdAt | DateTime | Order date from Shopify |
| lastSyncedAt | DateTime | When we last pulled from Shopify |

### OrderLine
One line item within an order (e.g. "2x Lavender Candle").

| Attribute | Type | Notes |
|-----------|------|-------|
| id | UUID | Primary key |
| orderId | UUID | FK → Order |
| productId | UUID | FK → Product (nullable if product deleted in Shopify) |
| title | String | Line item name (snapshot at order time) |
| quantity | Integer | How many ordered |
| unitPrice | Decimal | Price per unit at order time |

### StockAlert
Created by **business logic** when stock drops below threshold. Not synced from Shopify — this is ShopOps's own entity.

| Attribute | Type | Notes |
|-----------|------|-------|
| id | UUID | Primary key |
| productId | UUID | FK → Product |
| status | Enum | OPEN, ACKNOWLEDGED, RESOLVED |
| threshold | Integer | The threshold that triggered this alert (e.g. 5) |
| quantityAtAlert | Integer | Stock level when alert was created |
| createdAt | DateTime | When alert was created |
| resolvedAt | DateTime | When marked resolved (nullable) |

---

## Relationships

```
User        (standalone — auth only for MVP)

Product  1 ──────<  OrderLine  >────── 1  Order
   │
   └──────<  StockAlert
```

| From | To | Cardinality | Meaning |
|------|----|-------------|---------|
| Order | OrderLine | 1 : many | One order has multiple line items |
| Product | OrderLine | 1 : many | One product can appear in many orders |
| Product | StockAlert | 1 : many | One product can have multiple alerts over time |

**Why a separate StockAlert entity?**
- A boolean `lowStock` on Product would lose history ("when did we first notice?")
- Alerts have their own lifecycle (open → acknowledged → resolved)
- Same pattern Mendix developers use: model the *business concept*, not just a flag

---

## Workflows (business rules)

### Order fulfillment (internal status)

```
NEW  →  PROCESSING  →  SHIPPED  →  COMPLETED
```

| Status | Meaning | Who acts |
|--------|---------|----------|
| NEW | Order synced, not yet handled | System (on sync) |
| PROCESSING | Warehouse is packing it | User clicks "Start processing" |
| SHIPPED | Package sent | User clicks "Mark shipped" |
| COMPLETED | Done, archived from active view | User clicks "Complete" |

> `shopifyStatus` comes from Shopify and is read-only in ShopOps. `internalStatus` is our ops workflow.

### Stock alert lifecycle

```
(open)  →  ACKNOWLEDGED  →  RESOLVED
```

| Status | Meaning |
|--------|---------|
| OPEN | Alert created, nobody has seen it yet |
| ACKNOWLEDGED | Someone saw it, working on it |
| RESOLVED | Stock replenished or alert dismissed |

### Low-stock business rule

```
WHEN  product.inventoryQuantity < THRESHOLD (default: 5)
AND   no OPEN alert exists for this product
THEN  create StockAlert(status=OPEN)
```

Runs after:
- Manual "Sync products" from Shopify
- Webhook: `inventory_levels/update`

---

## External system: Shopify

| ShopOps entity | Shopify source | Sync direction |
|----------------|----------------|----------------|
| Product | `GET /admin/api/.../products.json` | Shopify → ShopOps |
| Order | `GET /admin/api/.../orders.json` | Shopify → ShopOps |
| OrderLine | Nested in order JSON | Shopify → ShopOps |
| StockAlert | — | ShopOps only (business logic) |
| internalStatus | — | ShopOps only (user actions) |

**Webhooks (Phase 6):**
- `orders/create` — new order appears in dashboard
- `orders/updated` — order status changes in Shopify
- `inventory_levels/update` — stock change triggers alert check

---

## Pages (UI map)

| Page | Purpose | Mendix equivalent |
|------|---------|-------------------|
| Login | Authenticate | Sign-in page |
| Dashboard | Stats: open orders, open alerts, last sync | Home page with widgets |
| Products | List synced products, stock levels | Overview page |
| Orders | List orders, filter by internal status | Overview page |
| Order detail | Line items + status action buttons | Detail page |
| Alerts | Open alerts, acknowledge/resolve | Overview + actions |

---

## Out of scope (for MVP)

- Custom Shopify storefront
- Payment processing
- Multi-user roles (stretch goal)
- Email notifications (stretch goal)
- Editing products in ShopOps (Shopify is source of truth for catalog)
