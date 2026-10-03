# ---------- Stage 1: Build -----------------
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files first to cache npm install
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the source code and build
COPY . .
RUN npm run build

# ---------- Stage 2 : Serve --------------------------------
FROM nginx:alpine

# Remove default Nginx config
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

# copy the built React app from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start nginx in the foreground to keep container running
CMD ["nginx", "-g", "daemon off;"]
