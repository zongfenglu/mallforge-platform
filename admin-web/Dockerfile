# Multi-stage build for Nuxt 3 admin frontend
FROM registry.cn-hangzhou.aliyuncs.com/library/node:22-alpine AS build
WORKDIR /app

COPY package.json ./
RUN npm install --no-audit --no-fund

COPY . .
RUN npm run build

FROM registry.cn-hangzhou.aliyuncs.com/library/node:22-alpine AS production
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall

COPY --from=build /app/.output ./.output

USER mall
EXPOSE 3000

ENV NODE_ENV=production
CMD ["node", ".output/server/index.mjs"]
