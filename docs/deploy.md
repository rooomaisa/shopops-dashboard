# Deploy ShopOps — step by step

You deploy **two things**:

| What | Where | URL looks like |
|------|--------|----------------|
| **Backend** (Java API) | [Render](https://render.com) | `https://shopops-api-xxxx.onrender.com` |
| **Frontend** (React UI) | [Vercel](https://vercel.com) | `https://shopops-dashboard.vercel.app` |

Database stays on **Supabase** (you already have this).

---

## Before you start (5 minutes)

1. Code is on GitHub: `rooomaisa/shopops-dashboard` on branch `main`
2. Open your local `backend/.env` — you will copy values from here into Render
3. Generate a **new** JWT secret for production (not your local one):

```bash
openssl rand -base64 32
```

Copy the output — you need it in step 2.

---

## Part 1 — Deploy backend on Render

### Step 1 — Account

1. Go to **https://render.com**
2. Click **Get Started** (or **Sign In**)
3. Sign in with **GitHub** (same account as your repo)

### Step 2 — New web service

1. On the Render dashboard, click the blue **New +** button (top right)
2. Click **Web Service**
3. If asked, click **Connect GitHub** and allow access to your repos
4. Find **shopops-dashboard** → click **Connect**

### Step 3 — Settings (fill in exactly)

| Field | Value |
|-------|--------|
| **Name** | `shopops-api` (or anything you like) |
| **Region** | Frankfurt or closest to EU |
| **Branch** | `main` |
| **Root Directory** | `backend` |
| **Runtime** | **Docker** |
| **Instance type** | **Free** |

Render should detect the `Dockerfile` automatically.

Click **Advanced** if you need to set:

- **Health Check Path:** `/api/health`

### Step 4 — Environment variables

Scroll to **Environment Variables** → **Add Environment Variable**.

Add **each row** (copy values from your local `backend/.env`):

| Key | Value |
|-----|--------|
| `DATABASE_URL` | your Supabase JDBC URL |
| `DATABASE_USERNAME` | your Supabase username |
| `DATABASE_PASSWORD` | your Supabase password |
| `JWT_SECRET` | the new random string from `openssl` |
| `SEED_ENABLED` | `false` |
| `CORS_ALLOWED_ORIGINS` | leave empty for now — you fill this after Vercel |
| `SHOPIFY_STORE_DOMAIN` | your store, e.g. `bloom-and-co-dev.myshopify.com` |
| `SHOPIFY_CLIENT_ID` | from Dev Dashboard |
| `SHOPIFY_CLIENT_SECRET` | from Dev Dashboard |
| `SHOPIFY_API_VERSION` | `2026-01` |

### Step 5 — Deploy

1. Click **Create Web Service** (bottom)
2. Wait 5–10 minutes — first build is slow
3. When status is **Live**, copy your URL from the top, e.g.  
   `https://shopops-api.onrender.com`

### Step 6 — Test backend

Open in browser:

```
https://YOUR-RENDER-URL.onrender.com/api/health
```

You should see JSON like:

```json
{"status":"ok","service":"shopops-api","database":"connected"}
```

If `database` is `disconnected`, check your Supabase env vars on Render.

---

## Part 2 — Deploy frontend on Vercel

### Step 1 — Account

1. Go to **https://vercel.com**
2. Sign in with **GitHub**

### Step 2 — Import project

1. Click **Add New…** (top right) → **Project**
2. Find **shopops-dashboard** → click **Import**

### Step 3 — Configure project

| Field | Value |
|-------|--------|
| **Framework Preset** | Vite (auto-detected) |
| **Root Directory** | Click **Edit** → type `frontend` → **Continue** |

### Step 4 — Environment variable

Before clicking Deploy, expand **Environment Variables**:

| Name | Value |
|------|--------|
| `VITE_API_URL` | your Render URL, e.g. `https://shopops-api.onrender.com` |

**No trailing slash.** No `/api` at the end.

### Step 5 — Deploy

1. Click **Deploy**
2. Wait ~2 minutes
3. Copy your live URL, e.g. `https://shopops-dashboard.vercel.app`

### Step 6 — Test frontend

1. Open your Vercel URL
2. Log in:
   - Email: `demo@shopops.dashboard`
   - Password: `DemoShopOps2025!`
3. First load may be slow (Render free tier wakes up)

---

## Part 3 — Connect frontend and backend (CORS)

Render must allow requests from your Vercel URL.

1. Go back to **Render** → your **shopops-api** service
2. Click **Environment** in the left menu
3. Edit `CORS_ALLOWED_ORIGINS` → set to your **full Vercel URL**, e.g.  
   `https://shopops-dashboard.vercel.app`
4. Click **Save Changes** — Render redeploys automatically

Refresh your Vercel site and try login again.

---

## Part 4 — Screenshots for your portfolio

1. Log in to your **live** Vercel app
2. Take 3 screenshots:
   - **Dashboard** (with sync panel)
   - **Products** (with “Live from Shopify” badges)
   - **Orders** (workflow + demo badge)
3. Save as PNG in `docs/screenshots/`:
   - `dashboard.png`
   - `products.png`
   - `orders.png`
4. Commit and push (or add to README after)

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Login fails on Vercel | Check `VITE_API_URL` and `CORS_ALLOWED_ORIGINS` |
| API very slow first time | Render free tier sleeps — wait 30–60 sec |
| Health check fails | Check Render logs → **Logs** tab |
| Sync fails on production | Shopify env vars must be on Render too |

---

## Demo login (put on your CV / LinkedIn)

- **App:** your Vercel URL  
- **Email:** `demo@shopops.dashboard`  
- **Password:** `DemoShopOps2025!`

See [demo-script.md](demo-script.md) for a 2-minute walkthrough to practice before interviews.
