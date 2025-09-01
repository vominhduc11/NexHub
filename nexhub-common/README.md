# NexHub Common Library

Shared utilities and components for the NexHub microservices platform.

## Overview

This library provides common utilities, DTOs, exceptions, and configuration templates used across all NexHub microservices.

## Components

### 🔐 Security Components
- **JwtService**: JWT token validation and claims extraction
- **SecurityConstants**: Common security constants and permissions
- **JwtConstants**: JWT-related constants and configuration
- **BaseSecurityConfig**: Base security configuration template

### 📝 DTOs & Responses
- **NotificationEvent**: Shared notification event DTO
- **BaseResponse**: Generic response wrapper
- **ResponseUtil**: Response utility methods

### ❌ Exception Handling
- **BaseException**: Base exception with error codes
- **BusinessException**: Business logic exceptions
- **ResourceNotFoundException**: Resource not found exceptions
- **ValidationException**: Validation error exceptions
- **JWT Exceptions**: JWT-specific exceptions

### ⚙️ Configuration Templates
- **BaseOpenApiConfig**: OpenAPI/Swagger configuration template
- **BaseKafkaProducerConfig**: Kafka producer configuration
- **BaseKafkaConsumerConfig**: Kafka consumer configuration

### ✅ Validation
- **@ValidPhoneNumber**: Phone number validation annotation
- **ValidationUtil**: Common validation utilities

## Usage

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.devwonder</groupId>
    <artifactId>nexhub-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Enable Component Scanning

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.yourservice", "com.devwonder.common"})
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

### 3. Extend Base Configurations

```java
@Configuration
public class SecurityConfig extends BaseSecurityConfig {
    @Override
    protected void configureService(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        );
        requireGatewayHeader(http);
    }
}
```

### 4. Use Common Components

```java
// Using ResponseUtil
@RestController
public class MyController {
    
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<List<User>>> getUsers() {
        List<User> users = userService.findAll();
        return ResponseUtil.success("Users retrieved successfully", users);
    }
    
    @PostMapping("/users")
    public ResponseEntity<BaseResponse<User>> createUser(@Valid @RequestBody User user) {
        User created = userService.save(user);
        return ResponseUtil.created("User created successfully", created);
    }
}

// Using JWT Service
@Service
public class MyService {
    
    @Autowired
    private JwtService jwtService;
    
    public UserInfo validateAndExtractUser(String token) {
        JWTClaimsSet claims = jwtService.validateToken(token);
        return UserInfo.builder()
            .accountId(jwtService.extractAccountId(claims))
            .username(jwtService.extractUsername(claims))
            .roles(jwtService.extractRoles(claims))
            .build();
    }
}
```

## Building the Library

```bash
# Build and install to local repository
cd nexhub-common
mvn clean install

# Build without tests
mvn clean install -DskipTests
```

## Dependencies

- Spring Boot 3.5.5
- Spring Security
- Spring Data JPA
- Spring Kafka
- JWT Libraries (JJWT, Nimbus JOSE)
- OpenAPI/Swagger
- Validation API
- Lombok

## Architecture

```
nexhub-common/
├── src/main/java/com/devwonder/common/
│   ├── config/           # Base configuration templates
│   ├── constants/        # Common constants
│   ├── dto/             # Shared DTOs
│   ├── exception/       # Common exceptions  
│   ├── security/        # Security utilities
│   ├── util/            # Utility classes
│   └── validation/      # Custom validations
└── pom.xml              # Dependencies
```

## Version History

- **0.0.1-SNAPSHOT**: Initial release with core components