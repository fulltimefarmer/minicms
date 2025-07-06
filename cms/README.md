# Company Management System (CMS)

基于Spring Boot的多模块企业管理系统，采用RBAC权限模型，支持用户管理、权限管理、资产管理等功能。

## 项目架构

这是一个多模块项目（非微服务），包含以下模块：

### 模块结构

```
company-management-system/
├── common-service/         # 公共模块
│   ├── 审计日志功能
│   ├── 基础实体类
│   ├── 通用工具类
│   └── 公共配置
├── auth-service/          # 权限模块
│   ├── RBAC权限管理
│   ├── 角色管理
│   └── 权限管理
├── user-service/          # 用户管理模块
│   ├── 用户管理
│   ├── 用户认证
│   └── 登录功能
├── asset-service/         # 资产管理模块
│   ├── 资产管理
│   ├── 资产分类
│   └── 资产状态管理
└── bootloader/           # 启动模块
    ├── 应用启动器
    ├── 配置集成
    └── 数据库迁移
```

## 技术栈

### 核心框架
- **Spring Boot 3.5.3**: 主框架，提供自动配置和约定优于配置
- **Java 21**: LTS版本，支持最新语言特性
- **Maven**: 项目构建和依赖管理工具

### 数据存储
- **PostgreSQL 12+**: 主数据库，支持ACID事务和高级SQL特性
- **MyBatis Plus 3.5.5**: ORM框架，简化数据库操作
  - 自动分页插件
  - 逻辑删除支持
  - 自动填充审计字段
- **Flyway 10.17.0**: 数据库版本管理和迁移工具

### 安全认证
- **Spring Security**: 安全框架，提供认证和授权
- **JWT (jjwt 0.11.5)**: 无状态token认证
- **RBAC模型**: 基于角色的访问控制

### API文档
- **SpringDoc OpenAPI 3**: 自动生成API文档
- **Swagger UI**: 在线API测试界面

### 开发工具
- **Lombok**: 减少样板代码，自动生成getter/setter等
- **Spring DevTools**: 开发时热重载支持
- **Apache Commons Lang3**: 常用工具类库

### 监控观测
- **Spring Actuator**: 应用健康检查和监控端点
- **自定义审计日志**: AOP切面自动记录操作日志

### 架构模式
- **多模块项目**: 按业务域划分模块，便于维护
- **分层架构**: Controller-Service-Repository分层
- **依赖注入**: 基于Spring IoC容器
- **面向切面编程**: 使用AOP实现横切关注点

## 本地启动完整流程

### 步骤一：环境检查

确保您的开发环境满足以下要求：

```bash
# 检查Java版本 (需要21+)
java -version

# 检查Maven版本 (需要3.8+)
mvn -version

# 检查PostgreSQL是否运行
psql --version
```

**环境要求**:
- **JDK 21+** (推荐使用OpenJDK或Oracle JDK)
- **Maven 3.8+** (或使用项目自带的mvnw)
- **PostgreSQL 12+** (推荐14或15)
- **Git** (用于克隆代码)

### 步骤二：项目获取和编译

```bash
# 1. 克隆项目 (如果还没有)
git clone <repository-url>

# 2. 进入项目目录
cd cms

# 3. 检查项目结构
ls -la

# 4. 首次编译 - 下载依赖
./mvnw clean compile
# 或者使用系统Maven
mvn clean compile
```

### 步骤三：数据库环境准备

#### 3.1 安装PostgreSQL (如果未安装)

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**MacOS (使用Homebrew):**
```bash
brew install postgresql
brew services start postgresql
```

**Windows:**
- 下载PostgreSQL官方安装包
- 或使用Docker: `docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:13`

#### 3.2 创建数据库和用户

```bash
# 1. 登录PostgreSQL
sudo -u postgres psql

# 2. 在psql命令行中执行以下SQL
CREATE DATABASE cms;
CREATE USER cms_user WITH PASSWORD 'cms_password';
GRANT ALL PRIVILEGES ON DATABASE cms TO cms_user;

# 3. 验证连接 (退出psql后执行)
\q
psql -h localhost -U cms_user -d cms
```

#### 3.3 验证数据库连接

```bash
# 测试连接
psql -h localhost -U cms_user -d cms -c "SELECT version();"
```

### 步骤四：应用配置

#### 4.1 配置文件说明

主配置文件位于: `bootloader/src/main/resources/application.yml`

```yaml
# 数据库配置 - 根据实际情况修改
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cms
    username: cms_user
    password: cms_password
    
# 如果需要修改端口
server:
  port: 8080
  
# 如果需要修改日志级别
logging:
  level:
    org.max.cms: DEBUG  # 改为INFO可减少日志
```

#### 4.2 环境变量配置 (可选)

您也可以通过环境变量覆盖配置：

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=cms
export DB_USERNAME=cms_user
export DB_PASSWORD=cms_password
export SERVER_PORT=8080
```

### 步骤五：启动应用

#### 5.1 开发模式启动 (推荐)

```bash
# 进入项目根目录
cd cms

# 方法1: 使用Maven包装器 (推荐)
./mvnw spring-boot:run -pl bootloader

# 方法2: 使用系统Maven
mvn spring-boot:run -pl bootloader

# 方法3: 带调试模式启动
./mvnw spring-boot:run -pl bootloader -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

#### 5.2 生产模式启动

```bash
# 1. 打包应用
./mvnw clean package -pl bootloader

# 2. 运行JAR包
java -jar bootloader/target/bootloader-0.0.1-SNAPSHOT.jar

# 3. 后台运行
nohup java -jar bootloader/target/bootloader-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

#### 5.3 启动日志检查

正常启动时，您应该看到类似以下的日志：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.3)

2024-07-04 10:30:00.000  INFO --- [           main] org.max.cms.Application                  : Starting Application using Java 21
2024-07-04 10:30:02.000  INFO --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080
2024-07-04 10:30:03.000  INFO --- [           main] org.max.cms.Application                  : Started Application in 3.456 seconds
```

### 步骤六：验证启动成功

#### 6.1 基本连通性测试

```bash
# 1. 健康检查
curl http://localhost:8080/api/actuator/health

# 2. 检查应用信息
curl http://localhost:8080/api/actuator/info

# 3. 查看所有端点
curl http://localhost:8080/api/actuator
```

#### 6.2 数据库验证

```bash
# 检查数据库表是否创建成功
psql -h localhost -U cms_user -d cms -c "\dt"

# 检查初始数据是否插入
psql -h localhost -U cms_user -d cms -c "SELECT count(*) FROM users;"
```

#### 6.3 访问应用

🌐 **主要访问地址：**

| 服务 | URL | 说明 |
|------|-----|------|
| 应用主页 | http://localhost:8080/api | API根路径 |
| API文档 | http://localhost:8080/api/swagger-ui.html | Swagger UI界面 |
| API规范 | http://localhost:8080/api/v3/api-docs | OpenAPI 3.0规范 |
| 健康检查 | http://localhost:8080/api/actuator/health | 应用健康状态 |
| 应用信息 | http://localhost:8080/api/actuator/info | 应用版本信息 |
| 指标监控 | http://localhost:8080/api/actuator/metrics | 应用性能指标 |

### 步骤七：测试登录功能 (开发完成后)

```bash
# 使用默认管理员账户测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 故障排除

### 常见问题及解决方案

#### 1. 数据库连接问题

**问题**: `Connection refused` 或 `could not connect to server`
```bash
# 解决方案：
# 1. 检查PostgreSQL是否运行
sudo systemctl status postgresql

# 2. 检查端口是否被占用
netstat -tulpn | grep :5432

# 3. 检查防火墙设置
sudo ufw status

# 4. 重启PostgreSQL服务
sudo systemctl restart postgresql
```

**问题**: `password authentication failed`
```bash
# 解决方案：
# 1. 重新创建用户
sudo -u postgres psql -c "DROP USER IF EXISTS cms_user;"
sudo -u postgres psql -c "CREATE USER cms_user WITH PASSWORD 'cms_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE cms TO cms_user;"

# 2. 检查pg_hba.conf配置
sudo nano /etc/postgresql/13/main/pg_hba.conf
# 确保有: local all cms_user md5
```

#### 2. 编译和构建问题

**问题**: `mvn: command not found`
```bash
# 解决方案：使用项目自带的Maven包装器
./mvnw clean compile
```

**问题**: `JAVA_HOME not set`
```bash
# 解决方案：设置JAVA_HOME
export JAVA_HOME=/path/to/java21
# 或在.bashrc中永久设置
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
```

**问题**: 依赖下载失败
```bash
# 解决方案：
# 1. 清理本地仓库
rm -rf ~/.m2/repository

# 2. 使用阿里云镜像 (编辑 ~/.m2/settings.xml)
# 添加镜像配置
```

#### 3. 启动问题

**问题**: 端口被占用 `Port 8080 was already in use`
```bash
# 解决方案：
# 1. 查找占用进程
lsof -i :8080

# 2. 杀死进程
kill -9 <PID>

# 3. 或修改端口
# 在application.yml中修改: server.port: 8081
```

**问题**: Flyway迁移失败
```bash
# 解决方案：
# 1. 检查数据库权限
psql -h localhost -U cms_user -d cms -c "SELECT current_user, session_user;"

# 2. 手动清理迁移表
psql -h localhost -U cms_user -d cms -c "DROP TABLE IF EXISTS flyway_schema_history;"

# 3. 重新启动应用
```

#### 4. 性能问题

**问题**: 启动速度慢
```bash
# 解决方案：
# 1. 增加JVM内存
export MAVEN_OPTS="-Xmx2048m -Xms1024m"

# 2. 跳过测试
./mvnw spring-boot:run -pl bootloader -DskipTests

# 3. 使用开发配置
java -jar bootloader/target/bootloader-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 调试模式

#### 启用远程调试
```bash
# 启动时添加调试参数
./mvnw spring-boot:run -pl bootloader -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# 然后在IDE中连接到localhost:5005
```

#### 查看详细日志
```bash
# 修改application.yml中的日志级别
logging:
  level:
    org.max.cms: DEBUG
    org.springframework: DEBUG
    com.baomidou.mybatisplus: DEBUG
```

### 数据库管理工具

推荐使用以下工具管理数据库：
- **pgAdmin**: 官方图形化管理工具
- **DBeaver**: 通用数据库管理工具
- **DataGrip**: JetBrains的数据库IDE

```bash
# 安装pgAdmin (Ubuntu)
sudo apt install pgadmin4
```

## 默认账户

系统预置了以下测试账户：

| 用户名 | 密码 | 角色 | 说明 | 权限范围 |
|--------|------|------|------|----------|
| admin | admin123 | SUPER_ADMIN | 超级管理员 | 所有功能权限 |
| manager | admin123 | ADMIN | 管理员 | 用户管理、资产管理 |
| user | admin123 | USER | 普通用户 | 基础查看权限 |

💡 **安全提示**: 生产环境请立即修改默认密码！

## 功能特性

### 🔐 权限管理
- 基于RBAC模型的权限控制
- 支持角色继承和权限分配
- 细粒度的API和菜单权限控制

### 👥 用户管理
- 用户CRUD操作
- 用户状态管理
- 密码重置和修改
- 登录失败锁定机制

### 📊 审计日志
- 自动记录用户操作
- 请求响应日志记录
- 操作时间和IP地址跟踪
- 支持日志查询和过滤

### 🏢 资产管理
- 公司资产登记和管理
- 资产状态跟踪
- 资产分类和搜索
- 资产责任人管理

### 📚 API文档
- 集成Swagger UI
- 自动生成API文档
- 支持在线测试

## 开发指南

### 模块开发规范

1. **实体类**: 继承 `BaseEntity`，包含审计字段
2. **Repository**: 使用 MyBatis Plus 的 `BaseMapper`
3. **Service**: 标准的服务层模式
4. **Controller**: RESTful API设计
5. **审计日志**: 使用 `@Auditable` 注解自动记录

### 添加新功能

1. 在相应模块中创建实体类
2. 创建Repository接口
3. 实现Service层逻辑
4. 创建Controller提供API
5. 编写Flyway迁移脚本
6. 添加权限配置

### 数据库迁移

使用Flyway管理数据库版本：

```bash
# 查看迁移状态
mvn flyway:info -pl bootloader

# 执行迁移
mvn flyway:migrate -pl bootloader

# 清理数据库（慎用）
mvn flyway:clean -pl bootloader
```

## 部署指南

### 方式一：Docker Compose部署 (推荐)

#### 1. 创建Docker Compose文件

创建 `docker-compose.prod.yml`:
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:13
    container_name: cms-postgres
    environment:
      POSTGRES_DB: cms
      POSTGRES_USER: cms_user
      POSTGRES_PASSWORD: cms_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U cms_user -d cms"]
      interval: 30s
      timeout: 10s
      retries: 3

  cms-app:
    build: .
    container_name: cms-application
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/cms
      - SPRING_DATASOURCE_USERNAME=cms_user
      - SPRING_DATASOURCE_PASSWORD=cms_password
      - SPRING_PROFILES_ACTIVE=prod
    ports:
      - "8080:8080"
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
```

#### 2. 创建Dockerfile

创建项目根目录下的 `Dockerfile`:
```dockerfile
FROM openjdk:21-jdk-slim

# 安装curl用于健康检查
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制Maven包装器和pom文件
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY */pom.xml ./*/

# 下载依赖
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY . .

# 构建应用
RUN ./mvnw clean package -pl bootloader -DskipTests

# 运行应用
EXPOSE 8080
CMD ["java", "-jar", "bootloader/target/bootloader-0.0.1-SNAPSHOT.jar"]
```

#### 3. 启动服务

```bash
# 构建和启动
docker-compose -f docker-compose.prod.yml up -d

# 查看日志
docker-compose -f docker-compose.prod.yml logs -f

# 停止服务
docker-compose -f docker-compose.prod.yml down
```

### 方式二：传统JAR包部署

#### 1. 构建生产包

```bash
# 构建应用
./mvnw clean package -pl bootloader -DskipTests

# 验证JAR包
ls -la bootloader/target/bootloader-0.0.1-SNAPSHOT.jar
```

#### 2. 生产环境配置

创建 `application-prod.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/cms
    username: ${DB_USERNAME:cms_user}
    password: ${DB_PASSWORD:cms_password}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      
  jpa:
    show-sql: false
    
logging:
  level:
    org.max.cms: INFO
    com.baomidou.mybatisplus: WARN
  file:
    name: /var/log/cms/application.log
    
server:
  port: 8080
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
    min-response-size: 1024

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

#### 3. 启动脚本

创建 `start.sh`:
```bash
#!/bin/bash

# 设置环境变量
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
export DB_USERNAME="your_db_user"
export DB_PASSWORD="your_db_password"

# 创建日志目录
mkdir -p /var/log/cms

# 启动应用
nohup java $JAVA_OPTS \
  -jar bootloader/target/bootloader-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  > /var/log/cms/application.log 2>&1 &

echo "CMS应用已启动，进程ID: $!"
```

### 方式三：Kubernetes部署

#### 1. 创建ConfigMap

`k8s/configmap.yaml`:
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: cms-config
data:
  application-k8s.yml: |
    spring:
      datasource:
        url: jdbc:postgresql://postgres-service:5432/cms
        username: cms_user
        password: cms_password
```

#### 2. 创建Deployment

`k8s/deployment.yaml`:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cms-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: cms
  template:
    metadata:
      labels:
        app: cms
    spec:
      containers:
      - name: cms
        image: your-registry/cms:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        livenessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 监控和日志

#### 1. 应用监控

集成Prometheus监控：
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

#### 2. 日志收集

使用ELK Stack或其他日志管理系统：
```yaml
# logback-spring.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <arguments/>
            </providers>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### 性能优化

#### 1. JVM优化

```bash
# 生产环境JVM参数
JAVA_OPTS="
  -Xmx4g -Xms2g
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+UseStringDeduplication
  -XX:+PrintGC
  -XX:+PrintGCDetails
  -Djava.awt.headless=true
  -Dspring.profiles.active=prod
"
```

#### 2. 数据库优化

```sql
-- PostgreSQL配置优化
-- shared_buffers = 256MB
-- effective_cache_size = 1GB
-- work_mem = 4MB
-- maintenance_work_mem = 64MB

-- 创建索引优化查询
CREATE INDEX CONCURRENTLY idx_audit_logs_created_at_user ON audit_logs(created_at, user_id);
CREATE INDEX CONCURRENTLY idx_users_enabled_deleted ON users(enabled, deleted);
```

### 安全配置

#### 1. HTTPS配置

```yaml
# application-prod.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
  port: 8443
```

#### 2. 防火墙规则

```bash
# Ubuntu UFW配置
sudo ufw allow 22/tcp     # SSH
sudo ufw allow 8080/tcp   # 应用端口
sudo ufw allow 5432/tcp   # PostgreSQL (仅内网)
sudo ufw enable
```

## 项目结构详解

```
cms/
├── common-service/                    # 🔧 公共服务模块
│   ├── src/main/java/org/max/cms/common/
│   │   ├── annotation/                # 自定义注解
│   │   │   └── Auditable.java        # 审计注解
│   │   ├── aspect/                   # 切面编程
│   │   │   └── AuditAspect.java      # 审计切面
│   │   ├── config/                   # 配置类
│   │   │   ├── CommonConfig.java     # 公共配置
│   │   │   └── MybatisPlusConfig.java# MyBatis Plus配置
│   │   ├── dto/                      # 数据传输对象
│   │   │   └── ResponseDto.java      # 统一响应格式
│   │   ├── entity/                   # 实体类
│   │   │   ├── BaseEntity.java       # 基础实体
│   │   │   └── AuditLog.java         # 审计日志实体
│   │   ├── repository/               # 数据访问层
│   │   │   └── AuditLogRepository.java
│   │   └── service/                  # 服务层
│   │       └── AuditLogService.java  # 审计日志服务
│   └── pom.xml                       # 模块依赖配置
├── auth-service/                      # 🔐 认证授权模块
│   ├── src/main/java/org/max/cms/auth/
│   │   ├── config/AuthConfig.java    # 认证配置
│   │   └── entity/                   # 权限相关实体
│   │       ├── Role.java             # 角色实体
│   │       ├── Permission.java       # 权限实体
│   │       └── RolePermission.java   # 角色权限关联
│   └── pom.xml
├── user-service/                      # 👥 用户管理模块
│   ├── src/main/java/org/max/cms/user/
│   │   └── entity/User.java          # 用户实体
│   └── pom.xml
├── asset-service/                     # 🏢 资产管理模块
│   ├── src/main/java/org/max/cms/asset/
│   │   └── entity/Asset.java         # 资产实体
│   └── pom.xml
├── bootloader/                        # 🚀 启动模块
│   ├── src/main/java/org/max/cms/
│   │   └── Application.java          # 主启动类
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置文件
│   │   └── db/migration/             # Flyway迁移脚本
│   │       ├── V1__Create_base_tables.sql
│   │       └── V000002__initial_baseline_data.sql
│   └── pom.xml
├── pom.xml                           # 父POM配置
├── README.md                         # 项目文档
├── PROJECT_STRUCTURE.md              # 项目结构说明
└── docker-compose.yml                # Docker开发环境
```

## 常见问题 (FAQ)

### Q1: 如何添加新的业务模块？
**A**: 按照以下步骤：
1. 在根目录创建新模块目录，如 `finance-service`
2. 创建模块的 `pom.xml`，继承父POM
3. 在父POM中添加模块引用
4. 创建标准的包结构: entity、repository、service、controller
5. 在bootloader的Application.java中添加包扫描路径

### Q2: 如何自定义数据库连接池？
**A**: 在application.yml中配置HikariCP参数：
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50        # 最大连接数
      minimum-idle: 10             # 最小空闲连接
      connection-timeout: 30000    # 连接超时
      idle-timeout: 600000         # 空闲超时
      max-lifetime: 1800000        # 连接最大生存时间
```

### Q3: 如何集成其他数据库？
**A**: 修改以下配置：
1. 更换数据库驱动依赖
2. 修改application.yml中的数据源配置
3. 调整MyBatis Plus的数据库类型
4. 修改Flyway迁移脚本的SQL语法

### Q4: 如何启用HTTPS？
**A**: 
1. 生成SSL证书
2. 配置application.yml
3. 重新启动应用

### Q5: 如何扩展审计日志功能？
**A**: 
1. 修改AuditLog实体，添加新字段
2. 更新数据库迁移脚本
3. 在AuditAspect中添加新的逻辑
4. 使用@Auditable注解标记需要审计的方法

### Q6: 生产环境建议的配置是什么？
**A**: 
```yaml
# JVM参数
JAVA_OPTS: "-Xmx4g -Xms2g -XX:+UseG1GC"

# 数据库连接池
spring.datasource.hikari.maximum-pool-size: 50

# 日志级别
logging.level.org.max.cms: INFO

# 启用压缩
server.compression.enabled: true
```

## 技术特性总结

### ✅ 已实现功能
- [x] 多模块项目架构
- [x] RBAC权限模型
- [x] 审计日志系统
- [x] 数据库版本管理 (Flyway)
- [x] API文档生成 (Swagger)
- [x] 基础实体和通用响应
- [x] 开发工具集成
- [x] 健康检查端点
- [x] 完整的配置体系

### 🚧 待开发功能
- [ ] 用户认证API (登录/登出)
- [ ] JWT Token管理
- [ ] 角色权限API
- [ ] 用户管理API  
- [ ] 资产管理API
- [ ] 文件上传功能
- [ ] 数据验证和异常处理
- [ ] 单元测试和集成测试
- [ ] 缓存机制 (Redis)
- [ ] 消息队列集成

### 🔄 扩展建议
- 添加Redis缓存提升性能
- 集成ElasticSearch实现全文搜索
- 添加消息队列支持异步处理
- 实现多租户架构
- 添加工作流引擎
- 集成第三方认证 (OAuth2/SAML)

## 贡献指南

### 开发规范
1. **代码风格**: 遵循Google Java Style Guide
2. **提交规范**: 使用Conventional Commits格式
3. **分支管理**: Git Flow工作流
4. **测试要求**: 单元测试覆盖率 > 80%

### 提交流程
```bash
# 1. Fork项目
git clone https://github.com/your-username/cms.git

# 2. 创建功能分支
git checkout -b feature/new-feature

# 3. 开发和测试
./mvnw test

# 4. 提交代码
git commit -m "feat: 添加新功能"

# 5. 推送分支
git push origin feature/new-feature

# 6. 创建Pull Request
```

### 代码审查标准
- 功能完整性
- 代码可读性
- 性能影响
- 安全性考虑
- 文档完备性

## 技术支持

### 获取帮助
- 📧 **邮件支持**: cms-support@company.com
- 💬 **在线讨论**: GitHub Issues
- 📖 **详细文档**: /docs目录
- 🎥 **视频教程**: 待添加

### 报告问题
请在GitHub Issues中报告问题，包含：
1. 问题描述
2. 重现步骤
3. 环境信息
4. 错误日志
5. 期望结果

## 版本历史

| 版本 | 发布日期 | 主要功能 |
|------|----------|----------|
| v0.1.0 | 2024-07-04 | 初始多模块架构 |
| v0.2.0 | 计划中 | 认证授权API |
| v0.3.0 | 计划中 | 完整业务API |
| v1.0.0 | 计划中 | 生产就绪版本 |

## 许可证

本项目采用 **MIT License** 开源协议。

```
MIT License

Copyright (c) 2024 Company Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

**🎉 感谢使用 Company Management System！**

如果这个项目对您有帮助，请考虑给我们一个 ⭐️ Star!