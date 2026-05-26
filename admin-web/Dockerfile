FROM docker.m.daocloud.io/node:22-alpine AS build
WORKDIR /app

COPY package.json ./
RUN npm install --no-audit --no-fund && \
    # 修复 ETXTBSY: 重新安装 esbuild
    node node_modules/esbuild/install.js 2>/dev/null || true && \
    npm rebuild esbuild 2>/dev/null || true

COPY . .
RUN npm run build

FROM docker.m.daocloud.io/node:22-alpine AS production
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall

COPY --from=build /app/.output ./.output

USER mall
EXPOSE 3000

ENV NODE_ENV=production
CMD ["node", ".output/server/index.mjs"]
