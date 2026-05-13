# 第一阶段：Maven构建
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 先复制pom.xml并下载依赖（利用Docker缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# 第二阶段：运行（使用 jammy 版本，支持 ARM64/Apple Silicon）
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 创建目录
RUN mkdir -p /app/uploads/books

# 复制应用 jar
COPY --from=build /app/target/*.jar app.jar

# 复制种子图片（首次启动时会同步到 uploads 卷）
COPY seed-images/ /app/seed-images/

# 复制启动脚本
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["./entrypoint.sh"]
