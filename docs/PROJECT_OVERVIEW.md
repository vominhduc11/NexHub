# NexHub - Enterprise Microservices E-Commerce Platform

![NexHub Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.4.0-red)
![Redis](https://img.shields.io/badge/Redis-7-red)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![Security](https://img.shields.io/badge/Security-JWT%20RBAC-green)
![WebSocket](https://img.shields.io/badge/WebSocket-Real--time-yellow)
![Event Driven](https://img.shields.io/badge/Event--Driven-Kafka-orange)

> **Production-Ready Enterprise E-Commerce Platform** built with Spring Boot 3.5.5 microservices architecture, featuring comprehensive JWT security with custom `AllAuthoritiesAuthorizationManager`, real-time WebSocket communications with dual-layer security interceptors, event-driven architecture with Kafka messaging for reseller lifecycle management, notification persistence with database audit trails, and complete Docker orchestration with service discovery.

## 🚀 Latest Updates & Enhancements

### ✨ **Recent Production Improvements** *(September 2025)*
- **✅ Advanced Event-Driven Architecture**: Complete reseller lifecycle management with Kafka event streaming (`reseller-approved`, `reseller-rejected`, `reseller-deleted` topics)
- **✅ Enhanced Account Management**: Sophisticated account status system with approval workflows and soft delete capabilities
- **✅ Advanced Security Framework**: AllAuthoritiesAuthorizationManager requiring ALL specified authorities for maximum protection
- **✅ Real-time WebSocket Notifications**: Dual-layer security interceptors with JWT validation for dealer registration events
- **✅ Cross-Service Communication**: Event-driven synchronization between auth-service and user-service via Kafka with comprehensive event handling
- **✅ Enhanced Entity Design**: Improved Account and Reseller entities with business logic methods and approval status tracking
- **✅ API Endpoint Cleanup**: Streamlined to validation-only endpoints with proper security authorization
- **✅ Database Isolation**: Complete 6-database architecture with proper service boundaries and entity relationships
- **✅ Shared Library Integration**: nexhub-common with comprehensive utilities and base components

### 🏗️ **Event-Driven Microservices Architecture** *(September 2025)*
- **Complete Reseller Lifecycle Management**: Event-driven workflow from registration through approval/rejection to deletion
- **Kafka Event Streaming**: `reseller-approved`, `reseller-rejected`, `reseller-deleted` topics for comprehensive lifecycle tracking
- **Enhanced Account Management**: Account entity with built-in business logic methods (`requiresApproval()`, `canLogin()`, `setInitialStatus()`)
- **Approval Workflow**: Sophisticated ApprovalStatus enum with PENDING, APPROVED, REJECTED states
- **ResellerEventService**: Comprehensive event processing with structured event objects for all lifecycle stages
- **Cross-Service Audit**: ResellerEventListener in auth-service for complete audit trail and account synchronization
- **Dual Authorization Patterns**: Custom AllAuthoritiesAuthorizationManager (AND logic) vs hasAnyAuthority (OR logic)
- **WebSocket Real-time Updates**: Direct connection with JWT authentication for dealer registration notifications

### 🔐 **Advanced Security Framework** *(September 2025)*
- **Custom Authorization Manager**: AllAuthoritiesAuthorizationManager for requiring ALL specified authorities instead of ANY
- **Enhanced JWT Security**: RSA-256 with JWKS endpoint validation and comprehensive role/permission extraction
- **API Gateway Security**: Reactive authorization patterns with custom authorization managers
- **WebSocket Security**: Dual-layer interceptor architecture with real-time JWT validation
- **Service Validation**: Cross-service endpoint validation for warranty service integration

### 📡 **Real-time Communication & Event Processing**
- **WebSocket Authentication**: Multi-layer JWT validation with specialized exception handling
- **Notification System**: Complete dealer registration notifications with database persistence and Kafka integration
- **Event-Driven Architecture**: Kafka-based reseller lifecycle management with audit trails
- **Cross-Service Validation**: User existence validation endpoints for warranty service integration

## Executive Summary

**NexHub** is a production-ready enterprise e-commerce microservices platform built on Spring Boot 3.5.5, designed for scalable product management, warranty tracking, customer operations, and content management. The platform features distributed architecture with Redis caching, Kafka event streaming, WebSocket real-time communication, and comprehensive JWT security across 6 specialized PostgreSQL databases.

**Recent Enhancements (September 2025)**:
- **Complete Event-Driven Architecture**: Sophisticated reseller lifecycle management with comprehensive Kafka event streaming (`reseller-approved`, `reseller-rejected`, `reseller-deleted`)
- **Enhanced Account Management**: Account entity with built-in business logic methods (`requiresApproval()`, `canLogin()`, `setInitialStatus()`)
- **Advanced Approval Workflow**: ApprovalStatus enum system with PENDING, APPROVED, REJECTED states and rejection reason tracking
- **Cross-Service Communication**: ResellerEventService with comprehensive event processing and ResellerEventListener for audit trails
- **Advanced Authorization**: AllAuthoritiesAuthorizationManager requiring ALL specified authorities (AND logic) vs hasAnyAuthority (OR logic)
- **WebSocket Security**: Dual-layer interceptor architecture with real-time JWT validation for dealer registration events
- **API Endpoint Optimization**: Streamlined controllers to validation-only endpoints, removed unnecessary creation endpoints
- **Service Validation**: Cross-service endpoint validation for warranty service integration (reseller/customer existence checks)
- **Database Architecture**: 6-database isolation with complete service boundaries and audit capabilities
- **Enhanced Security Configuration**: Cleaned up security rules and cross-service authentication patterns
- **Real-time Notifications**: Direct WebSocket connection with JWT authentication for instant dealer registration updates

## 🏗️ Architecture Overview

NexHub implements a complete microservices architecture with infrastructure services for service discovery and configuration management, and business services for e-commerce operations. The platform uses event-driven architecture with Kafka for asynchronous communication and WebSocket for real-time features.

### 🔧 Infrastructure Services

| Service | Port | Description | Technology Stack | Status |
|---------|------|-------------|------------------|---------|
| **Config Server** | 8888 | Centralized configuration management with native profile support | Spring Cloud Config, Git/Native | ✅ Production Ready |
| **Discovery Service** | 8761 | Service registry and discovery with Eureka | Spring Cloud Netflix Eureka | ✅ Production Ready |
| **API Gateway** | 8080 | API routing, JWT security, rate limiting, CORS, WebSocket proxying | Spring Cloud Gateway, OAuth2 Resource Server | ✅ Production Ready |

### 💼 Business Services

| Service | Port | Database | Key Features | Technology Stack | Status |
|---------|------|----------|--------------|------------------|---------|
| **Auth Service** | 8081 | nexhub_auth | RSA-256 JWT with JWKS, RBAC, Enhanced Account management with business logic, Reseller registration workflow, Comprehensive event processing (approved/rejected/deleted), Cross-service audit trails, ResellerEventListener | Spring Security, JJWT, JPA, Kafka Producer/Consumer, OpenFeign, nexhub-common, BaseControllerAdvice, Account Entity with business methods | ✅ Production Ready |
| **User Service** | 8082 | nexhub_user | Customer & Reseller CRUD with advanced approval workflow, Soft delete with timestamps, ApprovalStatus management (PENDING/APPROVED/REJECTED), Account integration via accountId, Event publishing for lifecycle changes, Cross-service validation endpoints | Spring Data JPA, Redis caching, MapStruct mapping, Kafka Producer, nexhub-common, BaseControllerAdvice, Enhanced Reseller entity with approval tracking | ✅ Production Ready |
| **Notification Service** | 8083 | nexhub_notification | Real-time WebSocket messaging with dual-layer security, Email notifications, Kafka event processing, Dealer registration notifications, Database persistence with CRUD operations, Direct connection architecture | WebSocket/STOMP, Kafka Consumer, Spring Mail, PostgreSQL, nexhub-common, BaseControllerAdvice, Dual Security Interceptors | ✅ Production Ready |
| **Product Service** | 8084 | nexhub_product | Product catalog, categories, media management, serial tracking, Cross-service validation support | Spring Data JPA, Redis caching, OpenAPI, MapStruct, nexhub-common, BaseControllerAdvice | ✅ Production Ready |
| **Warranty Service** | 8085 | nexhub_warranty | Warranty tracking, claims management, statistics, Cross-service validation integration, User/reseller existence checks | Spring Data JPA, OpenFeign clients, Redis caching, MapStruct, nexhub-common, BaseControllerAdvice | ✅ Production Ready |
| **Blog Service** | 8087 | nexhub_blog | CMS with posts, categories, comments, authors, tags, SEO optimization | Spring Data JPA, Redis caching, OpenAPI, nexhub-common, BaseControllerAdvice | ✅ Production Ready |
| **Language Service** | TBD | TBD | Internationalization support (Planned) | Spring Boot, nexhub-common (when implemented) | 🚧 In Development |

### 📚 Shared Libraries

| Component | Description | Features | Integration Status |
|-----------|-------------|----------|-------------------|
| **nexhub-common** | Centralized shared library with auto-configuration | BaseControllerAdvice (@Hidden), BaseException hierarchy, JWT utilities, BaseResponse, Security utilities, Validation helpers, BaseOpenApiConfig, BaseSecurityConfig | ✅ Integrated across all 8 business services |

### 🗄️ Data & Infrastructure

| Component | Port | Configuration | Purpose |
|-----------|------|---------------|---------|
| **PostgreSQL Cluster** | 5432 | 6 separate databases with service isolation | Primary data storage with proper schema separation |
| **Redis Cache** | 6379 | Password-protected, persistent storage | Distributed caching, session management |
| **Kafka Cluster** | 9092-9094 | 3-broker cluster with Zookeeper ensemble | Event streaming, reseller lifecycle management, real-time notifications |
| **Zookeeper Ensemble** | 2181-2183 | 3-node cluster for high availability | Kafka coordination and metadata management |

**Database Architecture**:
- `nexhub_auth` - Enhanced Account entities with business logic, roles, permissions, account lifecycle management
- `nexhub_user` - Customer and Reseller profiles with approval workflows, soft delete tracking, event-driven data
- `nexhub_notification` - Comprehensive notification system with CRUD operations, dealer registrations, audit trails
- `nexhub_product` - Product catalog, categories, serials, media management
- `nexhub_warranty` - Warranty claims and tracking with cross-service validation
- `nexhub_blog` - Complete CMS content management, posts, comments, categories

**Enhanced Event-Driven Data Flow**:
- **Reseller Registration**: Auth Service → Account Creation (PENDING status) → User Service Profile → Kafka → Notification Service → WebSocket Broadcasting
- **Reseller Approval**: Admin Action → User Service (ApprovalStatus.APPROVED) → Kafka (`reseller-approved`) → Auth Service (ResellerEventListener) → Account Status Update (APPROVED)
- **Reseller Rejection**: Admin Action → User Service (ApprovalStatus.REJECTED + reason) → Kafka (`reseller-rejected`) → Auth Service (ResellerEventListener) → Comprehensive Audit Trail
- **Reseller Deletion**: User Service (Soft Delete with timestamp) → Kafka (`reseller-deleted`) → Auth Service (ResellerEventListener) → Cross-Service Synchronization
- **Cross-Service Validation**: Warranty Service → User Service (existence validation endpoints with proper security)

### 🔧 Development & Monitoring Tools

| Tool | Port | Purpose |
|------|------|---------|
| **Redis Commander** | 8079 | Redis database management and monitoring |
| **Kafka UI** | 8078 | Kafka cluster monitoring and topic management |
| **React WebSocket Demo** | 5173 | Interactive WebSocket testing and demonstration |

## 🔐 Security Architecture

### Enhanced Authentication & Authorization Framework

**Advanced JWT Token System**:
- **Algorithm**: RSA-256 signatures with JWKS endpoint (`/auth/.well-known/jwks.json`)
- **Token Claims**: accountId, username, roles, permissions, userType, standard JWT claims
- **Token Lifecycle**: Configurable expiration (default 24 hours), stateless validation
- **Enhanced Validation**: Custom JwtService with comprehensive signature validation and JWKS integration

**Role-Based Access Control (RBAC)**:
- **Roles**: ADMIN, DEALER, CUSTOMER with hierarchical permissions
- **Permissions**: 15+ granular permissions (USER_*, PRODUCT_*, BLOG_*, WARRANTY_*, NOTIFICATION_*)
- **Authorization**: Both role-based and permission-based authorization at Gateway and service levels
- **Custom Authorization Manager**: `AllAuthoritiesAuthorizationManager` for requiring ALL authorities instead of ANY

**API Gateway Security**:
- **JWT Validation**: Automatic signature validation using JWKS endpoint at `http://auth-service:8081/auth/.well-known/jwks.json`
- **Claims Extraction**: Roles and permissions extracted and forwarded to services
- **Custom Authorization**: Reactive authorization patterns with Spring WebFlux security
- **CORS Configuration**: Configured for development origins with credentials support
- **Service-Specific Authorization**: Different authorization rules per service endpoint

### Advanced Security Features

**Custom Authorization Manager Implementation**:
```java
// AllAuthoritiesAuthorizationManager - Requires ALL specified authorities (AND logic)
public class AllAuthoritiesAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private final String[] requiredAuthorities;
    
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication.map(auth -> {
            boolean hasAll = true;
            for (String authority : requiredAuthorities) {
                if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(authority))) {
                    hasAll = false;
                    break;
                }
            }
            return new AuthorizationDecision(hasAll);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }
}

// Usage Examples:
.pathMatchers(HttpMethod.GET, "/api/notification/all")
.access(new AllAuthoritiesAuthorizationManager("ROLE_ADMIN", "PERM_NOTIFICATION_READ"))

.pathMatchers(HttpMethod.DELETE, "/api/user/reseller/*")
.access(new AllAuthoritiesAuthorizationManager("ROLE_ADMIN", "PERM_RESELLER_DELETE"))
```

**Security Methods Comparison**:
- **hasAnyAuthority()**: User needs ANY of the specified authorities (OR logic) - Standard Spring Security
- **hasAuthority()**: User needs the specific authority - Standard Spring Security  
- **AllAuthoritiesAuthorizationManager**: User needs ALL specified authorities (AND logic) - Custom implementation

**JWT Token Structure**:
```json
{
  "accountId": 1,
  "username": "admin",
  "roles": ["ADMIN"],
  "permissions": ["NOTIFICATION_READ", "USER_MANAGE"],
  "userType": "ADMIN",
  "iss": "auth-service",
  "exp": 1672531200
}
```

**Authority Extraction Logic**:
- **Roles**: Extracted from JWT and prefixed with `ROLE_` (e.g., `ROLE_ADMIN`)
- **Permissions**: Extracted from JWT and prefixed with `PERM_` (e.g., `PERM_NOTIFICATION_READ`)
- **Default Fallback**: `ROLE_CUSTOMER` assigned when no authorities found

### Advanced WebSocket Security & Direct Connection

**Direct Connection Architecture** (Bypassing API Gateway):
- **Connection Endpoint**: `ws://localhost:8083/ws/notifications` (Direct to Notification Service)
- **Performance**: Reduced latency by eliminating gateway proxy layer
- **Security**: Maintained through dual interceptor architecture with real-time JWT validation
- **Protocol**: Pure STOMP over SockJS with direct JWT validation

**Dual-Layer Interceptor Security Architecture**:
```java
// Layer 1: WebSocketAuthenticationInterceptor (CONNECT frames)
@Component
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // JWT validation and Principal creation
            String token = extractToken(accessor);
            JWTClaimsSet claims = jwtService.validateToken(token);
            accessor.setUser(new WebSocketPrincipal(claims));
        }
        return message;
    }
}

// Layer 2: WebSocketAuthorizationInterceptor (SEND/SUBSCRIBE frames)
@Component  
public class WebSocketAuthorizationInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            // Real-time JWT validation for each message
            List<String> userRoles = getUserRoles(accessor);
            boolean isAdmin = userRoles.stream().anyMatch("ADMIN"::equalsIgnoreCase);
            
            // Broadcast messages - Only ADMIN can send
            if ("/app/broadcast".equals(accessor.getDestination())) {
                if (!isAdmin) {
                    throw new AccessDeniedException("Only ADMIN can send broadcast messages");
                }
            }
        }
        return message;
    }
}
```

**Role-Based Message Authorization**:
- **SEND Permissions** (Message Sending):
  - **Broadcast Messages**: Only ADMIN users can send (`@MessageMapping("/broadcast")`)
  - **Private Messages**: Only ADMIN → CUSTOMER communication allowed (`@MessageMapping("/private/{targetUser}")`)
- **SUBSCRIBE Permissions** (Message Receiving):
  - **Public Topics**: All authenticated users can subscribe (`/topic/notifications`)
  - **Admin Topics**: ADMIN-only subscription (`/topic/dealer-registrations`)
  - **Private Queues**: User-specific subscription only (`/user/queue/private`)

### Cross-Service Security & Validation

**Service-to-Service Authentication**:
- **API Key Validation**: Inter-service calls secured with API keys
- **Gateway Header Validation**: Services validate `X-Gateway-Request` header
- **JWKS Integration**: Shared JWT validation across services

**Cross-Service Validation Endpoints**:
```java
// User Service - Validation endpoints for other services
@GetMapping("/user/reseller/{accountId}/exists")
public ResponseEntity<Boolean> resellerExists(@PathVariable Long accountId) {
    // Used by warranty-service for validation
}

@GetMapping("/user/customer/{accountId}/exists") 
public ResponseEntity<Boolean> customerExists(@PathVariable Long accountId) {
    // Used by warranty-service for validation
}

@GetMapping("/user/admin/{accountId}/exists")
public ResponseEntity<Boolean> adminExists(@PathVariable Long accountId) {
    // Available for future service integrations
}
```

### Security Flow Architecture
```
REST API Flow:
Client Request → API Gateway (JWT + AllAuthoritiesAuthorizationManager) → Service (Header Validation) → Database

WebSocket Flow (Direct):
Client WebSocket → Notification Service (Port 8083)
                       ↓
         WebSocketAuthenticationInterceptor (JWT Authentication)
                       ↓ (Principal creation, stateless validation)
       WebSocketAuthorizationInterceptor (Real-time Authorization)
                       ↓ (Fresh JWT validation + role extraction from token)
               @MessageMapping Controllers
                       ↓
           SimpMessagingTemplate (Broadcast/Private)

Event-Driven Flow:
Service Action → Kafka Event → Consuming Service → Database/WebSocket Update
```

## 📊 Database Architecture

### Database-per-Service Pattern
NexHub implements complete database isolation with 6 dedicated PostgreSQL databases:

```
nexhub_auth         → Account management, roles, permissions, RBAC
nexhub_user         → Customer and Reseller profiles, account mappings
nexhub_notification → Notification records, dealer registrations
nexhub_product      → Product catalog, categories, media, serial numbers
nexhub_warranty     → Warranty tracking, purchase records, claims
nexhub_blog         → Blog posts, categories, authors, tags, comments
```

### Entity Relationship Overview

**Authentication Domain (nexhub_auth)**:
- **Accounts**: Core authentication entities with username/password, account lifecycle management
- **Roles**: ADMIN, DEALER, CUSTOMER with Many-to-Many mapping to accounts, hierarchical permissions
- **Permissions**: 15+ granular permissions (USER_*, PRODUCT_*, BLOG_*, WARRANTY_*, NOTIFICATION_*) with Many-to-Many mapping to roles
- **Junction Tables**: account_roles, role_permissions for flexible RBAC implementation

**User Management Domain (nexhub_user)**:
- **Customers**: Customer profiles with personal information, linked to auth accounts via accountId
- **Resellers**: Dealer profiles with business information (name, address, phone, email, district, city)
- **Account Integration**: Foreign key references to auth service account IDs for seamless authentication
- **Soft Delete**: Proper deletion tracking with deletedAt timestamps

**Notification Domain (nexhub_notification)**:
- **Notifications**: Notification records with title, message, type, read status, timestamps, chronological ordering with `findAllByOrderByCreatedAtDesc()`
- **Dealer Registrations**: Specialized notifications for dealer registration events with complete audit trails
- **Database Persistence**: All notifications saved for audit and history tracking with CRUD operations
- **Type Classification**: DEALER_REGISTRATION, EMAIL_NOTIFICATION, SYSTEM_ALERT with proper enum management
- **Lombok Integration**: Clean code patterns with `@RequiredArgsConstructor` and `@Slf4j` for reduced boilerplate

**Product Catalog Domain (nexhub_product)**:
- **Products**: Core product entities with specifications, pricing, warranty information, SEO fields
- **Categories**: Hierarchical product categorization with slug-based URLs and soft delete support
- **ProductImages**: Media management with display ordering, alt text, and type classification
- **ProductVideos**: Video content with thumbnails, duration tracking, and type management
- **ProductSerials**: Individual product serial number tracking for warranty purposes
- **ProductFeatures**: Dynamic feature management with icons, images, and detailed descriptions

**Blog/CMS Domain (nexhub_blog)**:
- **BlogPosts**: Content management with SEO optimization, featured images, publication status (DRAFT, PUBLISHED, SCHEDULED, ARCHIVED)
- **BlogCategories**: Content categorization with color coding, icons, and visibility controls
- **BlogAuthors**: Author management with social media links, bio, and article count tracking
- **BlogTags**: Tag-based content classification with color coding and usage statistics
- **BlogComments**: Nested comment system with approval workflow and author information

**Warranty Domain (nexhub_warranty)**:
- **PurchasedProducts**: Purchase tracking with warranty expiration calculations and remaining days
- **WarrantyClaims**: Claims management with status workflow (PENDING, IN_PROGRESS, COMPLETED, REJECTED)
- **Service Integration**: Links to product and user services via OpenFeign clients for validation
- **Business Logic**: Automatic warranty status determination and expiration tracking

## 🚀 Technology Stack

### Core Platform Technologies
| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Runtime** | Eclipse Temurin Java | 17 | Application runtime environment |
| **Framework** | Spring Boot | 3.5.5 | Microservices foundation with auto-configuration |
| **Cloud Platform** | Spring Cloud | 2024.0.0 | Distributed system patterns and service mesh |
| **Build Tool** | Apache Maven | 3.9+ | Multi-module build automation and dependency management |
| **Database** | PostgreSQL | 15-alpine | ACID-compliant relational data storage |
| **Caching** | Redis | 7-alpine | In-memory data structure store with persistence |
| **Message Broker** | Apache Kafka | 7.4.0 | High-throughput distributed event streaming platform with 3-broker cluster |
| **Coordination** | Apache Zookeeper | 3.8.4 | Distributed configuration and coordination service |
| **Service Discovery** | Netflix Eureka | 2025.0.0 | Dynamic service registration and health monitoring |
| **API Gateway** | Spring Cloud Gateway | 2024.0.0 | Reactive API gateway with routing and filtering |
| **Documentation** | SpringDoc OpenAPI | 2.2.0 | OpenAPI 3.0 documentation with Swagger UI |

### Service Communication & Integration
| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **HTTP Client** | OpenFeign | 4.2.0 | Declarative REST client with load balancing |
| **Circuit Breaker** | Spring Cloud Circuit Breaker | 3.1.2 | Fault tolerance and resilience patterns |
| **Load Balancing** | Spring Cloud LoadBalancer | 4.1.4 | Client-side load balancing with health checks |
| **Configuration** | Spring Cloud Config | 4.1.3 | Centralized configuration management |
| **Message Queue** | Apache Kafka | 7.4.0 | Event-driven architecture with partitioning |
| **WebSocket** | Spring WebSocket + STOMP | 6.1.12 | Real-time bidirectional communication |
| **Email Integration** | Spring Boot Mail + Gmail SMTP | 3.5.5 | Transactional email delivery |
| **Template Engine** | Thymeleaf | 3.1.2 | Server-side email template rendering |

### Data Management & Persistence
| Category | Technology | Version | Configuration Details |
|----------|------------|---------|----------------------|
| **ORM Framework** | Hibernate (via Spring Data JPA) | 6.6.0 | Entity lifecycle management with lazy loading |
| **Database Driver** | PostgreSQL JDBC | 42.7.4 | High-performance database connectivity |
| **Schema Management** | JPA DDL Auto (update) | 6.6.0 | Automatic schema evolution and validation |
| **Caching Layer** | Redis with Spring Cache | 3.5.5 | Distributed caching with 600-second TTL |
| **Cache Abstraction** | Spring Cache | 6.1.12 | Annotation-driven caching with custom key generation |
| **Data Validation** | Hibernate Validator | 8.0.1 | Bean validation with custom constraint annotations |
| **Object Mapping** | MapStruct | 1.5.5 | Compile-time type-safe entity-to-DTO mapping |
| **Database Pooling** | HikariCP | 5.1.0 | High-performance connection pooling (default) |

### Security & Authentication
| Category | Technology | Version | Implementation Details |
|----------|------------|---------|------------------------|
| **JWT Library** | JJWT | 0.11.5 | RSA-256 signed tokens with JWKS endpoint |
| **Authentication** | Spring Security | 6.3.3 | Multi-layer security with custom interceptors |
| **Authorization** | Custom RBAC + Aspects | 1.0.0 | Role-based access control with granular permissions |
| **Password Security** | BCrypt | Built-in | Adaptive hashing with configurable strength |
| **CORS Management** | Spring Security CORS | 6.3.3 | Fine-grained cross-origin resource sharing |
| **Rate Limiting** | Custom Filter Chain | 1.0.0 | API throttling with Redis-backed counters |
| **WebSocket Security** | Dual Interceptor Chain | 1.0.0 | Authentication + Authorization for real-time connections |

### Frontend & Development Tools
| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **WebSocket Demo** | React | 19.1.1 | Interactive WebSocket testing client |
| **WebSocket Client** | @stomp/stompjs | 7.1.1 | STOMP protocol WebSocket client |
| **Build Tool** | Vite | Latest | Fast development server and build tool |
| **Package Manager** | npm | Latest | JavaScript package management |

## 🐳 Docker Architecture & Deployment

### Multi-Stage Docker Build Strategy
All services use optimized multi-stage Dockerfiles:

**Build Stage**:
- **Base Image**: maven:3.9-eclipse-temurin-17
- **Dependency Caching**: Separate layer for dependencies
- **Common Library**: Builds nexhub-common first for all services
- **Build Optimization**: Skip tests in container builds

**Production Stage**:
- **Base Image**: eclipse-temurin:17-jre (minimal JRE)
- **Health Checks**: Built-in health check endpoints
- **Security**: Non-root execution and minimal attack surface

### Service Orchestration
Docker Compose manages the complete platform with 597 lines of comprehensive configuration:

**Infrastructure Dependencies**:
```
PostgreSQL → Redis → Zookeeper Ensemble → Kafka Cluster
     ↓
Config Server → Discovery Service → API Gateway
     ↓
Business Services (Auth, User, Product, Warranty, Blog, Notification)
```

**Network Architecture**:
- **Custom Bridge Network**: `next-network` for service isolation
- **Service Discovery**: Internal DNS resolution for service names
- **Port Mappings**: Strategic port assignments avoiding conflicts
- **Volume Management**: Persistent storage for databases and configuration

### Health Monitoring & Resilience
| Feature | Implementation | Configuration |
|---------|----------------|---------------|
| **Health Checks** | Spring Boot Actuator `/actuator/health` | 30-second intervals, 3 retries |
| **Service Dependencies** | Docker Compose health conditions | Controlled startup ordering |
| **Auto-Recovery** | `restart: unless-stopped` policy | Automatic service restart on failure |
| **Graceful Shutdown** | Spring Boot shutdown hooks | Clean resource cleanup |

## ⚡ Real-time Communication & Performance

### Enhanced WebSocket Architecture (Direct Connection)
**Implementation**: Pure STOMP messaging with @MessageMapping controllers, bypassing API Gateway
**Direct Endpoint**: `ws://localhost:8083/ws/notifications` (No gateway proxy)

**Message Mapping Architecture**:
```java
@MessageMapping("/broadcast")           // ADMIN-only broadcasts
@MessageMapping("/private/{targetUser}") // ADMIN → CUSTOMER private messaging
```

**Advanced Security Architecture**:
- **Direct Connection**: Eliminates gateway proxy for improved performance  
- **Dual Interceptors**: JWT authentication + role-based authorization
- **Username Validation**: Pattern-based validation for target user identification
- **Session-based Auth**: User context stored from JWT claims, no header dependencies

**Permission Matrix**:
```javascript
// Message sending permissions
Broadcast: ADMIN only → All users
Private:   ADMIN only → CUSTOMER users only

// Topic subscriptions (receiving)
/topic/notifications          // All authenticated users
/user/queue/private          // User-specific private messages
/topic/dealer-registrations  // System notifications
```

**React WebSocket Demo Client**: Interactive testing with real-time validation and permission checks

### Redis Caching Strategy
**Implementation**: Distributed caching across Product, User, and Warranty services
**Configuration**: Password-protected Redis with persistent storage

**Cache Patterns**:
- **Entity Caching**: Individual entity caching with composite keys
- **Query Result Caching**: Paginated search results and filter queries
- **TTL Management**: 10-minute default expiration with refresh strategies
- **Cache Keys**: Service-prefixed keys for namespace isolation

**Performance Impact**: Sub-millisecond cache retrieval vs. database queries

## 🌐 API Gateway & Service Routing

### Centralized API Management
**Main Entry Point**: `http://localhost:8080`
**Documentation**: Aggregated Swagger UI at `/swagger-ui.html`
**WebSocket Endpoint**: `ws://localhost:8083/ws/notifications` (Direct connection, bypassing gateway)

### Service Routing Configuration
```
Authentication & Identity:
/api/auth/**           → Auth Service (8081)
/api/user/**           → User Service (8082)

Business Operations:
/api/product/**        → Product Service (8084)
/api/warranty/**       → Warranty Service (8085)
/api/blog/**           → Blog Service (8086)

Communication:
/api/notification/**   → Notification Service (8083)
WebSocket:             → ws://localhost:8083/ws/notifications
Email Notifications:   → Via Kafka messaging (internal)

Health & Monitoring:
/actuator/health       → Health checks across all services
```

### Enhanced CORS & Security Configuration
**Allowed Origins**: `http://localhost:3000,http://localhost:9000,http://localhost:5173`
**Allowed Methods**: `GET,POST,PUT,DELETE,OPTIONS`
**Credentials**: Enabled for JWT authentication
**Security Headers**: Comprehensive security header forwarding
**WebSocket CORS**: Configured for WebSocket connections

## 🔄 Inter-Service Communication Patterns

### Event-Driven Architecture with Kafka

**Reseller Lifecycle Management**:
```java
// User Service - Publishes reseller deletion events
@Transactional
public void softDeleteReseller(Long accountId) {
    // 1. Soft delete reseller
    reseller.setDeletedAt(LocalDateTime.now());
    resellerRepository.save(reseller);
    
    // 2. Publish event to Kafka
    ResellerDeletedEvent event = ResellerDeletedEvent.of(
        accountId, 
        reseller.getName(), 
        reseller.getEmail(),
        "Admin deletion via API"
    );
    kafkaTemplate.send("reseller-deleted", event);
}

// Auth Service - Consumes reseller deletion events
@KafkaListener(topics = "reseller-deleted", groupId = "auth-service-group")
@Transactional
public void handleResellerDeleted(ResellerDeletedEvent event) {
    // Audit trail and potential account management
    log.info("Reseller '{}' (accountId: {}) was deleted at {} for reason: {}", 
        event.getResellerName(), 
        event.getAccountId(), 
        event.getDeletedAt(), 
        event.getReason());
    
    // Optional: Could implement account soft delete for consistency
}
```

**Kafka Event Topics**:
- `email-notifications`: Email-based notifications
- `websocket-notifications`: Real-time WebSocket notifications  
- `reseller-deleted`: Cross-service reseller lifecycle events

### Synchronous Communication (OpenFeign)

**Cross-Service Validation Pattern**:
```java
// Warranty Service - Validates user existence via User Service
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/user/reseller/{accountId}/exists")
    Boolean resellerExists(@PathVariable Long accountId);
    
    @GetMapping("/user/customer/{accountId}/exists")
    Boolean customerExists(@PathVariable Long accountId);
}

// Usage in Warranty Service
public void validateWarrantyCreation(Long resellerId, Long customerId) {
    // Validate Reseller exists
    Boolean resellerExists = userServiceClient.resellerExists(resellerId);
    if (!Boolean.TRUE.equals(resellerExists)) {
        throw new IllegalArgumentException("Reseller with ID " + resellerId + " does not exist");
    }
    
    // Validate Customer exists
    Boolean customerExists = userServiceClient.customerExists(customerId);
    if (!Boolean.TRUE.equals(customerExists)) {
        throw new IllegalArgumentException("Customer with ID " + customerId + " does not exist");
    }
}
```

**Features**:
- **Load Balancing**: Automatic load balancing across service instances
- **Circuit Breaker**: Built-in resilience patterns
- **Request/Response Logging**: Comprehensive logging for debugging

### Real-time Communication (WebSocket)

**Enhanced WebSocket Integration with Event Streaming**:
```java
// Auth Service - Registration triggers real-time notifications
@Transactional
public ResellerRegistrationResponse registerReseller(ResellerRegistrationRequest request) {
    // ... create account and reseller profile ...
    
    // Send real-time notification via Kafka -> WebSocket
    NotificationEvent webSocketEvent = new NotificationEvent(
        "WEBSOCKET_DEALER_REGISTRATION",
        savedAccount.getId(),
        savedAccount.getUsername(),
        request.getEmail(),
        request.getName(),
        "New Dealer Registration",
        "A new dealer has been registered: " + request.getName(),
        LocalDateTime.now()
    );
    notificationService.sendNotificationEvent(webSocketEvent);
}

// Notification Service - Consumes and broadcasts via WebSocket
@KafkaListener(topics = "websocket-notifications")
public void consumeWebSocketNotification(NotificationEvent event) {
    if ("WEBSOCKET_DEALER_REGISTRATION".equals(event.getEventType())) {
        // Save to database for audit
        notificationRepository.save(notification);
        
        // Real-time broadcast to ADMIN users
        simpMessagingTemplate.convertAndSend(
            "/topic/dealer-registrations", 
            dealerRegistrationMessage
        );
    }
}
```

**Event-Driven Real-time Flow**:
```
Registration Request → Auth Service → User Service (Profile Creation)
                              ↓
                    Kafka (websocket-notifications)
                              ↓
           Notification Service → Database Save → WebSocket Broadcast
                              ↓
                    ADMIN Users receive real-time notification
```

### Service Integration Security

**API Key Authentication for Inter-Service Calls**:
```java
// Auth Service calls User Service with API key
@PostMapping("/user/reseller")
public ResponseEntity<ResellerResponse> createReseller(
    @RequestBody CreateResellerRequest request,
    @RequestHeader("X-API-Key") String apiKey
) {
    // API key validation for auth-service
}
```

**Gateway Header Validation**:
```java
// Services validate calls from API Gateway
@Configuration
public class SecurityConfig extends BaseSecurityConfig {
    @Override
    protected void configureServiceEndpoints(AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            .requestMatchers("/user/reseller/*/exists", "/user/admin/*/exists", "/user/customer/*/exists")
            .access(gatewayHeaderRequired())  // Validates X-Gateway-Request header
    }
}
```

## 📋 Comprehensive API Endpoints

| Endpoint | Method | Purpose | Security |
|----------|---------|---------|----------|
| `/api/auth/login` | POST | User authentication | Public |
| `/api/auth/reseller/register` | POST | Reseller registration with event publishing | Public |
| `/api/auth/.well-known/jwks.json` | GET | JWT public keys | Public |
| `/api/user/customers` | GET/POST | Customer management | ADMIN/DEALER |
| `/api/user/resellers` | GET/POST | Reseller management with event handling | ADMIN |
| `/api/user/reseller/{id}/exists` | GET | Cross-service validation | Gateway Header Required |

### Notification Management
| Endpoint | Method | Purpose | Security |
|----------|---------|---------|----------|
| `/api/notification/all` | GET | Get all notifications (newest first) | **AllAuthoritiesAuthorizationManager** (ROLE_ADMIN + PERM_NOTIFICATION_READ) |
| `/api/notification/{id}/read` | PUT | Mark notification as read | **AllAuthoritiesAuthorizationManager** (ROLE_ADMIN + PERM_NOTIFICATION_UPDATE) |
| `ws://localhost:8083/ws/notifications` | WebSocket | Real-time messaging with dual-layer security | JWT Authentication + Role Authorization |

> **Note**: Notification endpoints use custom `AllAuthoritiesAuthorizationManager` requiring ALL specified authorities instead of ANY.

### Product Catalog Management
| Endpoint | Method | Purpose | Security |
|----------|---------|---------|----------|
| `/api/product/products` | GET | Product listing | Public |
| `/api/product/products` | POST/PUT/DELETE | Product management | ADMIN/PRODUCT_* |
| `/api/product/categories` | GET | Category listing | Public |
| `/api/product-serials` | GET/POST | Serial tracking | ADMIN/DEALER |

### Content Management System
| Endpoint | Method | Purpose | Security |
|----------|---------|---------|----------|
| `/api/blog/posts` | GET | Blog post listing | Public |
| `/api/blog/posts` | POST/PUT/DELETE | Post management | ADMIN/BLOG_* |
| `/api/blog/comments` | POST | Comment creation | CUSTOMER/ADMIN |
| `/api/blog/categories` | GET/POST | Category management | ADMIN |

### Warranty & Claims Management
| Endpoint | Method | Purpose | Security |
|----------|---------|---------|----------|
| `/api/warranty/records` | GET/POST | Warranty tracking with user validation | ADMIN/DEALER/CUSTOMER |
| `/api/warranty/claims` | GET/POST/PUT | Claims management | ADMIN/DEALER |
| `/api/warranty/statistics` | GET | Warranty analytics | ADMIN |

### Real-time Communication (Direct WebSocket)
| Endpoint | Protocol | Purpose | Security |
|----------|----------|---------|----------|
| `ws://localhost:8083/ws/notifications` | WebSocket/STOMP | Direct real-time messaging | JWT + Dual-layer Authorization |
| `@MessageMapping("/broadcast")` | STOMP | Broadcast to all users | ADMIN only |
| `@MessageMapping("/private/{targetUser}")` | STOMP | Private messaging | ADMIN → CUSTOMER only |
| `/topic/notifications` | STOMP | Receive broadcasts | All authenticated users |
| `/topic/dealer-registrations` | STOMP | Dealer registration notifications | ADMIN only |
| `/user/queue/private` | STOMP | Receive private messages | User-specific validation |

### Cross-Service Validation Endpoints
| Endpoint | Method | Purpose | Used By |
|----------|---------|---------|---------|
| `/user/reseller/{accountId}/exists` | GET | Validate reseller existence | Warranty Service |
| `/user/customer/{accountId}/exists` | GET | Validate customer existence | Warranty Service |
| `/user/admin/{accountId}/exists` | GET | Validate admin existence | Available for future integrations |

### Event-Driven Endpoints (Kafka Integration)
| Event Type | Producer | Consumer | Purpose |
|------------|----------|----------|---------|
| `reseller-deleted` | User Service | Auth Service (ResellerEventListener) | Cross-service audit and lifecycle management |
| `websocket-notifications` | Auth Service | Notification Service | Real-time dealer registration notifications |
| `email-notifications` | Auth Service | Notification Service | Email-based notification delivery |

## 🚦 Development Setup & Getting Started

### Prerequisites
- **Java**: 17 or higher
- **Docker & Docker Compose**: Latest stable version
- **Maven**: 3.9+ for local development
- **Node.js**: Latest LTS for React WebSocket demo
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Quick Start (Docker Compose)
```bash
# Clone and navigate to project
git clone <repository-url>
cd NexHub

# Start complete platform
docker-compose up -d

# Verify all services are healthy
docker-compose ps

# Access main documentation
open http://localhost:8080/swagger-ui.html

# Test WebSocket with React demo
cd websocket-demo-react
npm install
npm run dev
open http://localhost:5173

# Monitor logs
docker-compose logs -f <service-name>
```

### Development Mode Setup
```bash
# Start only infrastructure services
docker-compose up -d postgres redis kafka1 kafka2 kafka3 zookeeper1 zookeeper2 zookeeper3

# Build common library (required for all services)
cd nexhub-common && mvn clean install

# Start services in dependency order
cd config-server && mvn spring-boot:run
cd discovery-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run

# Start business services
cd auth-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
# ... other services
```

### Testing & Validation
```bash
# Health check all services
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8083/actuator/health  # Notification Service

# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Test WebSocket endpoint (direct connection, requires authentication)
wscat -c "ws://localhost:8083/ws/notifications" \
  -H "Authorization: Bearer <jwt-token>"

# Access service discovery
curl http://localhost:8761/eureka/apps
```

## 📊 Current Implementation Status

### ✅ Production-Ready Components

**Enhanced Infrastructure & Security**:
- Complete service discovery and configuration management
- Advanced JWT-based authentication with JWKS and custom AllAuthoritiesAuthorizationManager
- API Gateway with routing, CORS, rate limiting, and reactive security patterns
- Role-based authorization with 15+ granular permissions and dual authorization logic (AND/OR)
- Cross-service validation endpoints for warranty service integration
- Centralized security configuration with BaseSecurityConfig

**Business Services with Event-Driven Architecture**:
- Full CRUD operations across all domain services with nexhub-common integration
- Database-per-service architecture with proper isolation (6 databases)
- Redis caching implementation across critical services
- Event-driven reseller lifecycle management with Kafka integration
- Real-time WebSocket notifications with database persistence and dual-layer security

**Advanced Real-time Features**:
- WebSocket messaging with dual-layer security interceptors and JWT validation
- Kafka-based event streaming for reseller lifecycle and cross-service communication
- Real-time dealer registration notifications with complete audit trails
- Cross-service validation for warranty creation workflows

**Data Management & Event Processing**:
- 6 PostgreSQL databases with comprehensive entity models and event-driven data flow
- Notification service with database persistence for audit trails and event processing
- Automated database initialization with test data across all services
- ResellerEventListener for cross-service audit and lifecycle management

### 🔧 Recent Major Enhancements (September 2025)

**Event-Driven Architecture Implementation**:
- Complete reseller lifecycle management with Kafka event streaming
- ResellerDeletedEvent with cross-service propagation from user-service to auth-service
- ResellerEventListener in auth-service for audit trails and account lifecycle tracking
- Real-time notification broadcasting for dealer registration events

**Advanced Security Framework Enhancements**:
- AllAuthoritiesAuthorizationManager implementation requiring ALL authorities (AND logic)
- Enhanced API Gateway security with reactive authorization patterns
- Dual-layer WebSocket security with real-time JWT validation
- Cross-service validation endpoints with proper security controls

**Cross-Service Integration**:
- User existence validation endpoints for warranty service integration
- Event-driven data synchronization between auth-service and user-service
- Enhanced service-to-service communication with proper error handling
- Complete audit trail implementation across service boundaries

**Development & Operational Improvements**:
- Comprehensive data initialization across all services with account linking
- Enhanced Docker orchestration with proper service dependencies
- Complete Git version control with detailed change tracking
- Improved service discovery integration with health monitoring

### ⚠️ Key Architecture Patterns & Considerations

**Event-Driven Architecture Patterns**:
- **Reseller Lifecycle Events**: Complete event-driven workflow for reseller creation, deletion, and audit
- **Cross-Service Synchronization**: Kafka-based event propagation between user-service and auth-service
- **Event Sourcing**: Audit trail maintenance through ResellerEventListener and database persistence
- **Eventual Consistency**: Event-driven data synchronization with proper error handling and retry mechanisms

**Authorization Architecture Patterns**:
- **hasAnyAuthority()**: Grants access if user has ANY of the specified authorities (OR logic) - Standard Spring Security
- **AllAuthoritiesAuthorizationManager**: Grants access only if user has ALL specified authorities (AND logic) - Custom implementation
- Example: `/api/notification/all` requires BOTH `ROLE_ADMIN` AND `PERM_NOTIFICATION_READ`
- Implementation: Iterates through required authorities, returns false if ANY are missing

**Cross-Service Communication Patterns**:
- **Synchronous Validation**: OpenFeign clients for real-time user existence validation
- **Asynchronous Events**: Kafka event streaming for lifecycle management and notifications
- **Direct WebSocket**: Bypasses API Gateway for optimal real-time performance
- **Service Security**: API key authentication and gateway header validation

**WebSocket Architecture Considerations**:
- Direct connection to port 8083 (optimized for performance, bypasses gateway)
- Dual-layer security with authentication and authorization interceptors
- Real-time JWT validation on each message for security
- Role-based topic subscriptions and message sending permissions
- Proper error handling for authentication and authorization failures

**Database Design Patterns**:
- **Database-per-Service**: 6 separate PostgreSQL databases for complete service isolation
- **Event-Driven Data Flow**: Cross-service data synchronization via Kafka events
- **Audit Trail Implementation**: ResellerEventListener maintains audit logs across service boundaries
- **Test Data Initialization**: Comprehensive DataInitializer components with proper account linking

**Security Implementation Details**:
- **AllAuthoritiesAuthorizationManager Implementation**: Custom reactive authorization manager
- **JWT Token Validation**: Real-time validation with role extraction for WebSocket communications
- **Service-to-Service Security**: API keys and gateway headers for inter-service calls
- **Cross-Service Validation**: Dedicated endpoints for warranty service user validation

## 🔍 Monitoring & Troubleshooting

### Health Monitoring
All services expose comprehensive health endpoints:
```bash
# Service health checks
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # User Service
curl http://localhost:8083/actuator/health  # Notification Service
curl http://localhost:8084/actuator/health  # Product Service
curl http://localhost:8085/actuator/health  # Warranty Service
curl http://localhost:8087/actuator/health  # Blog Service
```

### Infrastructure Monitoring
```bash
# Service discovery status
curl http://localhost:8761/eureka/apps

# Database connectivity test
docker exec -it nexhub-postgres psql -U nexhub -d nexhub_auth -c "\dt"

# Redis connectivity test
docker exec -it nexhub_redis redis-cli -a voduc123 ping

# Kafka cluster status via Kafka UI
open http://localhost:8078

# WebSocket connection test with React demo (connects directly to port 8083)
open http://localhost:5173
```

### Common Troubleshooting
```bash
# Check all service status
docker-compose ps

# View service logs
docker-compose logs -f auth-service
docker-compose logs -f notification-service

# Restart specific service
docker-compose restart notification-service

# Database connection issues
docker-compose logs postgres
docker-compose restart postgres

# WebSocket connection issues
docker-compose logs api-gateway
docker-compose logs notification-service

# Network connectivity test
docker exec nexhub-auth-service ping nexhub-postgres

# Build common library if services fail to start
cd nexhub-common && mvn clean install
```

## 🎯 Future Development Roadmap

### Immediate Priorities

**Service Completions**:
- Complete Language Service for internationalization support
- Implement comprehensive Blog Service author and tag management
- Enhanced admin dashboard with real-time monitoring
- Advanced WebSocket message routing and filtering

**Security Enhancements**:
- OAuth2 integration for external authentication providers
- Advanced rate limiting with Redis-based sliding window
- Service-to-service mTLS communication
- WebSocket message encryption for sensitive data

### Medium-term Enhancements

**Performance & Scalability**:
- Database connection pooling optimization
- Advanced caching strategies with cache warming
- Service mesh integration (Istio/Linkerd)
- WebSocket horizontal scaling with Redis Pub/Sub

**Feature Expansions**:
- Advanced product search with Elasticsearch
- Real-time inventory management with WebSocket updates
- Customer portal with self-service features
- Mobile API optimization with GraphQL
- Enhanced React WebSocket client with advanced features

### Long-term Vision

**Advanced Architecture**:
- Multi-tenant architecture support
- Event sourcing and CQRS patterns
- Advanced analytics and reporting with real-time dashboards
- AI-powered recommendations with WebSocket delivery

**Integration & Ecosystem**:
- Payment gateway integrations
- Shipping provider integrations
- Third-party marketplace connectors
- Advanced monitoring with Prometheus/Grafana
- Kubernetes deployment with Helm charts

## 📚 Additional Resources

### Documentation Access Points
| Resource | URL | Purpose |
|----------|-----|---------|
| **Aggregated API Docs** | http://localhost:8080/swagger-ui.html | Complete API documentation |
| **Service Discovery** | http://localhost:8761 | Eureka dashboard |
| **Kafka Management** | http://localhost:8078 | Kafka UI for topic management |
| **Redis Management** | http://localhost:8079 | Redis Commander interface |
| **WebSocket Demo** | http://localhost:5173 | Interactive React WebSocket client |

### Development Tools
| Tool | Location | Purpose |
|------|----------|---------|
| **React WebSocket Demo** | `./websocket-demo-react/` | Modern React-based WebSocket testing |
| **Database Init Scripts** | `./database/init-databases.sql` | Database initialization |
| **Architecture Diagrams** | `./classdiagram.drawio`, `./kafka.drawio` | System architecture visualization |
| **Common Library** | `./nexhub-common/` | Shared utilities and components |

### Key Configuration Files
| File | Purpose |
|------|---------|
| `docker-compose.yml` | Complete platform orchestration (597 lines) |
| `nexhub-common/pom.xml` | Shared library dependencies |
| `config-server/src/main/resources/configs/` | Centralized service configurations |
| `notification-service/src/main/java/com/devwonder/notification_service/config/` | WebSocket and security configurations |

---

## Project Metadata

**Platform**: NexHub Enterprise E-Commerce Microservices  
**Version**: 4.1.0  
**Last Updated**: September 2025  
**Status**: Production-Ready with Sophisticated Event-Driven Architecture and Advanced Account Management  
**Architecture**: Spring Boot 3.5.5 Microservices with Enhanced Business Logic Entities and Comprehensive Event-Driven Communication  
**Infrastructure**: Docker, PostgreSQL, Redis, Kafka, WebSocket, Eureka Discovery, nexhub-common Shared Library  
**Security**: Enhanced JWT with RSA-256, JWKS, AllAuthoritiesAuthorizationManager, Dual-layer WebSocket Security, Account-level Business Logic  
**Event Processing**: Comprehensive Kafka-based reseller lifecycle management (`approved`, `rejected`, `deleted`) with cross-service audit trails  
**Account Management**: Advanced approval workflows with built-in business logic methods and ApprovalStatus tracking  
**Maintainer**: DevWonder Development Team  

**Total Services**: 11+ (7 Business + 3 Infrastructure + Monitoring Tools)  
**Database Count**: 6 PostgreSQL databases with event-driven data flow and complete service isolation  
**Message Brokers**: 3-node Kafka cluster with Zookeeper ensemble for event streaming and lifecycle management  
**Caching**: Redis with distributed caching strategy and 600-second TTL  
**Authorization**: Custom AllAuthoritiesAuthorizationManager for AND logic authorization requiring ALL specified authorities  
**Security Framework**: Reactive Spring WebFlux security with dual-layer WebSocket protection and real-time JWT validation  
**Real-time Communication**: Advanced WebSocket system with JWT authentication, role-based authorization, and direct connection architecture  
**Event-Driven Architecture**: Complete reseller lifecycle management with Kafka event streaming and cross-service synchronization  

**Latest Major Enhancements (September 2025)**:
- **Sophisticated Event-Driven Architecture**: Complete reseller lifecycle management with comprehensive Kafka event streaming (`reseller-approved`, `reseller-rejected`, `reseller-deleted`)
- **Enhanced Account Management**: Account entity with built-in business logic methods for approval workflows and authentication checks
- **Advanced Approval System**: ApprovalStatus enum with PENDING, APPROVED, REJECTED states and comprehensive rejection reason tracking
- **AllAuthoritiesAuthorizationManager**: Custom reactive authorization manager requiring ALL specified authorities (AND logic)
- **Cross-Service Event Processing**: ResellerEventListener in auth-service for comprehensive audit trails and account synchronization
- **Real-time Notifications**: Direct WebSocket connection with dual-layer security and comprehensive CRUD operations
- **Enhanced Entity Design**: Sophisticated database entities with built-in business logic and lifecycle management
- **Shared Library Integration**: nexhub-common with comprehensive utilities, base components, and auto-configuration

**Architecture Highlights**:
- **Event-Driven Patterns**: Kafka-based reseller lifecycle management with cross-service event propagation
- **Dual Authorization Logic**: AllAuthoritiesAuthorizationManager (AND) vs hasAnyAuthority (OR) security patterns
- **Real-time WebSocket**: Direct connection with JWT validation bypassing API Gateway for optimal performance
- **Cross-Service Integration**: User existence validation endpoints for warranty service business logic
- **Database-per-Service**: Complete 6-database isolation with event-driven data synchronization
- **Service Discovery**: Eureka-based registration with comprehensive health monitoring
- **Audit Trail System**: ResellerEventListener for cross-service lifecycle tracking and compliance

This documentation represents the current state of NexHub as of September 4, 2025, reflecting all implemented features including sophisticated event-driven architecture with comprehensive reseller lifecycle management, enhanced account management with built-in business logic, advanced security implementations with AllAuthoritiesAuthorizationManager, comprehensive cross-service integration patterns, and detailed development roadmap.