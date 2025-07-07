# 使用官方的OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY src ./src

# 安装Maven
RUN apt-get update && apt-get install -y maven

# 构建应用
RUN mvn clean package -DskipTests

# 创建uploads目录
RUN mkdir -p /app/uploads

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 运行应用
CMD ["java", "-jar", "target/workflow-service-1.0.0.jar"]