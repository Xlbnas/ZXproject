# 校园教材循环利用平台

基于 Java Web（Spring Boot 3.x + Vue 3 + MySQL 8）的校园教材循环利用平台，武汉工商学院计算机科学与技术专业2022级毕业设计。

---

## 项目结构

```
ZXproject/
├── backend/          # Spring Boot 3.2 后端
├── frontend/         # Vue 3 + Element Plus 前端
├── init.sql          # 数据库初始化脚本
├── docker-compose.yml
└── README.md
```

---

## 一键Docker部署（推荐，最简单）

### 前提条件
- 已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)（Windows/Mac）或 Docker + Docker Compose（Linux）
- 确保 80、8080、3306 端口未被占用

### 启动步骤

```bash
# 1. 进入项目根目录
cd /path/to/ZXproject

# 2. 一键启动（首次会自动拉镜像、构建、初始化数据库，需要等几分钟）
docker compose up -d

# 3. 查看启动状态（等待全部变成 healthy/running）
docker compose ps

# 4. 查看后端日志（如有问题）
docker compose logs -f backend
```

### 访问地址

| 服务 | 地址 |
|------|------|
| **前端网页** | http://localhost:38080 |
| **后端接口** | http://localhost:38081/api |
| **MySQL数据库** | localhost:33306 (root / root123) |

### 停止服务

```bash
# 停止但保留数据
docker compose stop

# 完全清除（包括数据库数据）
docker compose down -v
```

---

## 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | `admin` | `admin123` | 可访问后台管理所有功能 |
| 普通用户 | `zhangsan` | `test123` | 测试用户，已有教材数据 |
| 普通用户 | `lisi` | `test123` | 测试用户 |
| 普通用户 | `wangwu` | `test123` | 测试用户 |

---

## 本地开发运行（不用Docker）

需要先安装：JDK 17、Maven 3.8+、Node.js 18+、MySQL 8.0

### 1. 初始化数据库

```sql
-- 在MySQL中执行
source /path/to/ZXproject/init.sql
```

### 2. 启动后端

```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库连接信息
mvn spring-boot:run
# 后端启动在 http://localhost:8080
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
# 前端启动在 http://localhost:5173
```

---

## 主要功能说明

### 普通用户功能
- **注册/登录**：填写用户名、密码、姓名等信息注册账号
- **教材检索**：按书名关键词、ISBN号、价格范围检索教材
- **发布教材**：填写教材信息（ISBN、品相、价格）等待管理员审核
- **发起交易**：选择教材后发起购买请求，等待持有者确认
- **订单管理**：查看待确认/已确认/已完成/已取消订单，可取消待确认订单、确认完成交易
- **交易评价**：交易完成后对对方进行评价，可修改和删除自己的评价
- **收藏教材**：收藏感兴趣的教材
- **个人中心**：查看/修改个人信息、我发布的教材、我的环保贡献

### 管理员功能（账号 admin/admin123）
- **用户管理**：搜索用户、提升/降级权限、禁用/启用账号
- **教材管理**：审核待审核教材（通过/驳回）、强制下架违规教材
- **交易监控**：查看所有交易、强制取消、标记纠纷
- **公告管理**：发布、编辑、删除系统公告

### 环保统计
- 首页显示平台累计：交易本数、节省金额、减碳量（kg）
- 每成功完成一笔交易后自动更新统计数据
- 个人中心显示本人的环保贡献

---

## 常见问题

**Q: 启动后访问 http://localhost 显示 404 / 502？**
> 后端可能还在启动中，等待30~60秒后刷新。可以用 `docker compose logs -f backend` 查看后端是否启动完成（出现 "Started TextbookApplication" 字样）。

**Q: 数据库初始化失败，提示 "Table doesn't exist"？**
> 重新执行：`docker compose down -v && docker compose up -d`，删除旧数据卷重新初始化。

**Q: 3306端口被占用？**
> 说明本机已有MySQL在运行。可以修改 `docker-compose.yml` 中的端口映射，比如改为 `"3307:3306"`。

**Q: 前端页面空白或路由不对？**
> 确保访问的是 http://localhost（80端口），不是 http://localhost:5173（开发端口）。

**Q: 上传图片后不显示？**
> 图片存储在Docker容器内 `/app/uploads/` 目录，通过 `/uploads/` 路径访问。确保nginx代理配置正确。

**Q: Windows下docker compose命令失败？**
> 较新版本Docker Desktop使用 `docker compose`（不加横杠），旧版用 `docker-compose`。

---

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2 |
| 持久层 | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT (jjwt 0.12.5) |
| 安全 | Spring Security |
| 前端框架 | Vue 3 + Vite |
| UI组件 | Element Plus 2.7 |
| 状态管理 | Pinia |
| HTTP请求 | Axios |
| 部署 | Docker + Nginx |
