# Multi-stage build for Nuxt 3 admin frontend
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:20-alpine AS production
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall

COPY --from=build /app/.output ./.output

USER mall
EXPOSE 3000

ENV NODE_ENV=production
CMD ["node", ".output/server/index.mjs"]
