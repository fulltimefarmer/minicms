# Maven Spring Boot Setup - CMS Project

## Overview
The CMS project is fully configured to support Maven Spring Boot commands. Both the Maven wrapper and system-wide Maven are available for running the application.

## Available Commands

### Using Maven Wrapper (Recommended)
```bash
cd /workspace/cms
./mvnw spring-boot:run
```

### Using System-wide Maven
```bash
cd /workspace/cms
mvn spring-boot:run
```

## Project Structure
The project is a multi-module Maven project with the following structure:

```
cms/
├── pom.xml                 # Root POM (parent project)
├── mvnw                    # Maven wrapper script (Linux/Mac)
├── mvnw.cmd               # Maven wrapper script (Windows)
├── .mvn/                  # Maven wrapper configuration
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── common-service/        # Common utilities and shared code
├── auth-service/          # Authentication service
├── user-service/          # User management service
├── asset-service/         # Asset/Document management service
└── bootloader/            # Main application entry point
    └── pom.xml           # Contains Spring Boot plugin configuration
```

## Key Configuration Details

### Root POM (`pom.xml`)
- **Parent**: `spring-boot-starter-parent:3.5.3`
- **Java Version**: 21
- **Packaging**: `pom` (multi-module project)
- **Spring Boot Plugin**: Configured with `<skip>true</skip>` in root

### Bootloader Module (`bootloader/pom.xml`)
- **Main Class**: `org.max.cms.Application`
- **Spring Boot Plugin**: Configured with `<skip>false</skip>`
- **Dependencies**: Includes all service modules and web dependencies

### Main Application Class
Located at: `bootloader/src/main/java/org/max/cms/Application.java`

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.max.cms.common",
    "org.max.cms.auth", 
    "org.max.cms.user",
    "org.max.cms.asset"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## Running the Application

### Prerequisites
- Java 21 or higher
- PostgreSQL database running on localhost:5432 (or configure different database settings)

### Commands
1. **Start the application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   or
   ```bash
   mvn spring-boot:run
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean compile
   ```

3. **Package the application**:
   ```bash
   ./mvnw clean package
   ```

4. **Run tests**:
   ```bash
   ./mvnw test
   ```

## Configuration
- **Server Port**: 8080 (default)
- **Context Path**: `/api`
- **Active Profile**: `dev`
- **Database**: PostgreSQL (configured in application properties)

## Notes
- The application will fail to start if PostgreSQL is not running, which is expected behavior
- The Spring Boot DevTools is enabled for hot reloading during development
- All service modules are automatically included in the bootloader's classpath
- The application uses MyBatis Plus for database operations
- Flyway is configured for database migrations

## Troubleshooting
If you encounter issues:
1. Ensure Java 21 is installed and configured
2. Check that PostgreSQL is running on localhost:5432
3. Verify all dependencies are downloaded: `./mvnw dependency:resolve`
4. Clean and rebuild: `./mvnw clean compile`

## Maven Versions
- **Maven Wrapper**: Uses Maven 3.9.9 (as configured in `.mvn/wrapper/maven-wrapper.properties`)
- **System Maven**: Apache Maven 3.9.9
- **Spring Boot**: 3.5.3