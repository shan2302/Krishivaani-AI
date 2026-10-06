# KV-031 — Create Dockerfile for React Frontend
**Assignee:** Shantanu | **Priority:** Medium | **Points:** 3

---

## What You Need to Create

```
frontend/
  Dockerfile          ← NEW
  .dockerignore       ← NEW
```

---

## Step 1 — Create `frontend/Dockerfile`

Create the file `Dockerfile` inside the `frontend/` folder. This uses a **2-stage build**: Node.js to build the static files, and Nginx to serve them efficiently.

```dockerfile
# ── Stage 1: Build ───────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files first to cache npm install
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the source code and build
COPY . .
RUN npm run build

# ── Stage 2: Serve ───────────────────────────────────────────
FROM nginx:alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Add custom config to support React Router (SPA routing fallback to index.html)
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Copy the built React app from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

---

## Step 2 — Create `frontend/.dockerignore`

Create `.dockerignore` inside the `frontend/` folder to prevent copying huge local directories.

```
node_modules/
dist/
.git
.gitignore
*.md
.idea/
.vscode/
```

---

## Step 3 — Build the Docker Image

Run from inside the `frontend/` folder (use `sudo` if your Docker permissions haven't applied yet):

```bash
cd frontend
sudo docker build -t krishivaani-frontend:latest .
```

Expected output ends with:
```
Successfully built <image-id>
Successfully tagged krishivaani-frontend:latest
```

---

## Step 4 — Run the Container

Map port 80 inside the container to port 3000 on your machine:

```bash
sudo docker run -p 3000:80 krishivaani-frontend:latest
```

---

## Step 5 — Verify It Works

Open your browser and navigate to:
**http://localhost:3000**

You should see your React frontend loading perfectly.

---

## Completion Checklist

- [ ] `frontend/Dockerfile` created
- [ ] `frontend/.dockerignore` created
- [ ] `docker build` → completes successfully
- [ ] `docker run` → container starts, UI accessible on port 3000
