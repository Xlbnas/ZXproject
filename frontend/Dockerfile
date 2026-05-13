# 第一阶段：Node.js构建
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json .
RUN npm install

COPY . .
RUN npm run build

# 第二阶段：Nginx静态服务
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
