# NexHub - Enterprise Microservices E-Commerce Platform

## Executive Summary

**NexHub** is a production-ready enterprise e-commerce microservices platform built on Spring Boot 3.5.5, designed for scalable product management, warranty tracking, customer operations, and content management. The platform features distributed architecture with Redis caching, Kafka event streaming, WebSocket real-time communication, and comprehensive JWT security across 6 specialized PostgreSQL databases.

**Recent Enhancements (August 2025)**:
- Direct WebSocket connection bypassing API Gateway for improved performance
- Advanced role-based authorization with dual-layer interceptor architecture (JWT + Permissions)
- Pure STOMP messaging with @MessageMapping controllers (no REST endpoints for messaging)
- Enhanced WebSocket security with username pattern validation for target users
- React-based WebSocket demonstration client with real-time validation
- Shared nexhub-common library for code reusability and maintainability

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
| **Auth Service** | 8081 | nexhub_auth | RSA-256 JWT with JWKS, RBAC, Account management | Spring Security, JJWT, JPA, Kafka Producer | ✅ Production Ready |
| **User Service** | 8082 | nexhub_user | Customer & Reseller CRUD, Profile management | Spring Data JPA, Redis, MapStruct | ✅ Production Ready |
| **Notification Service** | 8083 | nexhub_notification | Direct WebSocket messaging, role-based permissions, email notifications, STOMP messaging | Pure WebSocket/STOMP, Dual Interceptor Architecture, Kafka Consumer, Spring Mail | ✅ Production Ready |
| **Product Service** | 8084→8080 | nexhub_product | Product catalog, categories, media, serial tracking | Spring Data JPA, Redis caching, OpenAPI | ✅ Production Ready |
| **Warranty Service** | 8085 | nexhub_warranty | Warranty tracking, claims management, statistics | Spring Data JPA, OpenFeign, Redis | ✅ Production Ready |
| **Blog Service** | 8087→8080 | nexhub_blog | CMS with posts, categories, comments, SEO | Spring Data JPA, SEO optimization | ✅ Production Ready |
| **Language Service** | TBD | TBD | Internationalization support (Planned) | Spring Boot | 🚧 In Development |

### 📚 Shared Libraries

| Component | Description | Features |
|-----------|-------------|----------|
| **nexhub-common** | Common utilities and shared components | JWT utilities, logging aspects, validation annotations, shared DTOs, authorization aspects, security utilities |

### 🗄️ Data & Infrastructure

| Component | Port | Configuration | Purpose |
|-----------|------|---------------|---------|
| **PostgreSQL Cluster** | 5432 | 6 separate databases with service isolation | Primary data storage with proper schema separation |
| **Redis Cache** | 6379 | Password-protected, persistent storage | Distributed caching, session management |
| **Kafka Cluster** | 9092-9094 | 3-broker cluster with Zookeeper ensemble | Event streaming, asynchronous communication |
| **Zookeeper Ensemble** | 2181-2183 | 3-node cluster for high availability | Kafka coordination and metadata management |

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
- **Permissions**: 15+ granular permissions (USER_*, PRODUCT_*, BLOG_*, WARRANTY_*, NOTIFICATION_ACCESS)
- **Authorization**: Both role-based and permission-based authorization at Gateway and service levels
- **Aspect-Based Security**: Custom authorization aspects in nexhub-common library

**API Gateway Security**:
- **JWT Validation**: Automatic signature validation using JWKS endpoint
- **Claims Extraction**: Roles and permissions extracted and forwarded to services
- **Header Forwarding**: X-JWT-Subject, X-JWT-Username, X-JWT-Account-ID, X-User-Roles, X-User-Permissions
- **CORS Configuration**: Configured for development origins with credentials support
- **WebSocket Security**: JWT token validation for WebSocket connections through gateway

### Advanced WebSocket Security & Direct Connection

**Direct Connection Architecture** (Bypassing API Gateway):
- **Connection Endpoint**: `ws://localhost:8083/ws/notifications` (Direct to Notification Service)
- **Performance**: Reduced latency by eliminating gateway proxy layer
- **Security**: Maintained through dual interceptor architecture with real-time JWT validation
- **Protocol**: Pure STOMP over SockJS with direct JWT validation

**Dual-Layer Interceptor Security with Pure JWT Token Validation**:
- **Layer 1**: WebSocketJwtChannelInterceptor for authentication (CONNECT frames)
  - Initial JWT validation and Principal creation
  - Stateless authentication without session storage
- **Layer 2**: WebSocketRoleChannelInterceptor for authorization (SEND frames)
  - Real-time JWT token validation on each message
  - Fresh role extraction directly from token claims
  - Token expiration handling during active sessions
  - Pure JWT-based without session dependencies
- **Token Security**: Custom JwtService with JWKS validation and comprehensive expiration checks
- **Architecture**: Fully stateless WebSocket security with no session storage

**Role-Based Message Authorization**:
- **Broadcast Messages**: Only ADMIN users can send (`@MessageMapping("/broadcast")`)
- **Private Messages**: Only ADMIN → CUSTOMER communication allowed (`@MessageMapping("/private/{targetUser}")`)
- **Username Validation**: Pattern-based validation ensures target users are identified as CUSTOMER
- **Real-time Validation**: Both client-side and server-side validation with visual feedback

### Security Flow Architecture
```
REST API Flow:
Client Request → API Gateway (JWT Validation + RBAC) → Service (Permission Check) → Database

WebSocket Flow (Direct):
Client WebSocket → Notification Service (Port 8083)
                       ↓
         WebSocketJwtChannelInterceptor (Authentication)
                       ↓ (Principal creation, no session storage)
       WebSocketRoleChannelInterceptor (Real-time Authorization)
                       ↓ (Fresh JWT validation + role extraction from token)
               @MessageMapping Controllers
                       ↓
           SimpMessagingTemplate (Broadcast/Private)
```

## 📊 Database Architecture

### Database-per-Service Pattern
NexHub implements complete database isolation with 6 dedicated PostgreSQL databases:

```
nexhub_auth         → Account management, roles, permissions, RBAC
nexhub_user         → Customer and Reseller profiles, account mappings
nexhub_product      → Product catalog, categories, media, serial numbers
nexhub_warranty     → Warranty tracking, purchase records, claims
nexhub_blog         → Blog posts, categories, authors, tags, comments
nexhub_notification → Email history, notification tracking, audit logs
```

### Entity Relationship Overview

**Authentication Domain (nexhub_auth)**:
- **Accounts**: Core authentication entities with username/password
- **Roles**: ADMIN, DEALER, CUSTOMER with Many-to-Many mapping to accounts
- **Permissions**: Granular permissions with Many-to-Many mapping to roles
- **Junction Tables**: account_roles, role_permissions for flexible RBAC

**User Management Domain (nexhub_user)**:
- **Customers**: Customer profiles linked to auth accounts
- **Resellers**: Dealer profiles with business information
- **Account Integration**: Foreign key references to auth service account IDs

**Product Catalog Domain (nexhub_product)**:
- **Products**: Core product entities with specifications and pricing
- **Categories**: Hierarchical product categorization
- **ProductImages/Videos**: Media management with display ordering
- **ProductSerials**: Individual product serial tracking
- **ProductFeatures**: Dynamic feature management

**Blog/CMS Domain (nexhub_blog)**:
- **BlogPosts**: Content management with SEO optimization fields
- **BlogCategories**: Content categorization and organization
- **BlogAuthors**: Author management and attribution
- **BlogTags**: Tag-based content classification
- **BlogComments**: Nested comment system with approval workflow

**Warranty Domain (nexhub_warranty)**:
- **WarrantyRecords**: Purchase tracking with expiration calculations
- **WarrantyClaims**: Claims management with status workflow
- **Integration**: Links to product and user services via Feign clients

**Notification Domain (nexhub_notification)**:
- **Email History**: Comprehensive email notification tracking
- **Notification Types**: Categorized notification management
- **Audit Logs**: Real-time messaging and WebSocket interaction logs

## 🚀 Technology Stack

### Core Platform Technologies
| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Runtime** | Java | 17 | Application runtime and language |
| **Framework** | Spring Boot | 3.5.5 | Microservices framework and dependency injection |
| **Build Tool** | Maven | 3.9+ | Dependency management and build automation |
| **Database** | PostgreSQL | 15-alpine | Primary relational data storage |
| **Caching** | Redis | 7-alpine | Distributed caching and session management |
| **Messaging** | Apache Kafka | 7.4.0 | Event streaming and asynchronous communication |
| **Service Discovery** | Netflix Eureka | 2025.0.0 | Service registration and discovery |
| **API Gateway** | Spring Cloud Gateway | 2024.0.0 | API routing, security, and rate limiting |
| **Documentation** | SpringDoc OpenAPI | 2.2.0 | API documentation and Swagger UI |

### Service Communication & Integration
| Category | Technology | Purpose |
|----------|------------|---------|
| **Inter-Service Communication** | OpenFeign | Type-safe HTTP client for service-to-service calls |
| **Load Balancing** | Spring Cloud LoadBalancer | Client-side load balancing for service discovery |
| **Circuit Breaker** | Built-in resilience patterns | Fault tolerance and resilience |
| **Message Broker** | Apache Kafka with Zookeeper | Event-driven architecture and async processing |
| **Real-time Communication** | WebSocket with STOMP | Real-time notifications and messaging |
| **Email Services** | Spring Mail with Gmail SMTP | Email notification delivery |

### Data Management & Caching
| Category | Technology | Configuration |
|----------|------------|---------------|
| **ORM** | Spring Data JPA with Hibernate | Entity management and database abstraction |
| **Database Migration** | DDL Auto (update) | Schema evolution and database initialization |
| **Caching Strategy** | Redis with Spring Cache | Distributed caching with 10-minute TTL |
| **Data Validation** | Bean Validation (Hibernate Validator) | Request/response validation |
| **Data Mapping** | MapStruct | Type-safe entity-to-DTO mapping |

### Security & Authentication
| Category | Technology | Implementation |
|----------|------------|----------------|
| **Authentication** | JWT with JJWT 0.11.5 | RSA-256 signed tokens with JWKS |
| **Authorization** | Spring Security + Custom Aspects | RBAC with roles and permissions |
| **Password Encryption** | BCrypt | Secure password hashing |
| **CORS** | Spring Security CORS | Cross-origin resource sharing |
| **Rate Limiting** | Custom Global Filter | API rate limiting and throttling |

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
/api/product/**        → Product Service (8084→8080)  # Internal port 8080
/api/warranty/**       → Warranty Service (8085)
/api/blog/**           → Blog Service (8087→8080)     # Internal port 8080

Communication:
WebSocket Direct:      → ws://localhost:8083/ws/notifications (Bypassing gateway)
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

### Synchronous Communication (OpenFeign)
**Implementation**: Type-safe HTTP clients with service discovery integration

**Example Integration**:
```java
// Warranty Service → User Service validation
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/user/reseller/{accountId}/exists")
    Boolean resellerExists(@PathVariable Long accountId);
}
```

**Features**:
- **Load Balancing**: Automatic load balancing across service instances
- **Circuit Breaker**: Built-in resilience patterns
- **Request/Response Logging**: Comprehensive logging for debugging

### Asynchronous Communication (Kafka)
**Event-Driven Architecture**: Producer-Consumer pattern for decoupled services

**Message Flow**:
```
Auth Service → Kafka Topic (email-notifications) → Notification Service
             → Email Service → SMTP Gateway
             → WebSocket Controller → Real-time Client Updates (with JWT validation)
```

**Kafka Configuration**:
- **Brokers**: 3-broker cluster for high availability
- **Topics**: Auto-creation enabled with configurable replication
- **Consumer Groups**: Service-specific consumer groups for message processing
- **Serialization**: JSON-based message serialization

### Real-time Communication (WebSocket)
**Enhanced WebSocket Integration**:
- **JWT Authentication**: Multi-layer validation with JWKS support
- **Custom Exceptions**: Specialized exception handling for WebSocket authentication
- **Session Context**: Rich user information available in WebSocket sessions
- **STOMP Protocol**: Full STOMP implementation with SockJS fallback

## 📋 Comprehensive API Endpoints

### Authentication & User Management
| Endpoint | Method | Purpose | Security |
|----------|---------|---------|----------|
| `/api/auth/login` | POST | User authentication | Public |
| `/api/auth/.well-known/jwks.json` | GET | JWT public keys | Public |
| `/api/user/customers` | GET/POST | Customer management | ADMIN/DEALER |
| `/api/user/resellers` | GET/POST | Reseller management | ADMIN |

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
| `/api/warranty/records` | GET/POST | Warranty tracking | ADMIN/DEALER/CUSTOMER |
| `/api/warranty/claims` | GET/POST/PUT | Claims management | ADMIN/DEALER |
| `/api/warranty/statistics` | GET | Warranty analytics | ADMIN |

### Real-time Communication (Direct WebSocket)
| Endpoint | Protocol | Purpose | Security |
|----------|----------|---------|----------|
| `ws://localhost:8083/ws/notifications` | WebSocket/STOMP | Direct real-time messaging | JWT + Role validation |
| `@MessageMapping("/broadcast")` | STOMP | Broadcast to all users | ADMIN only |
| `@MessageMapping("/private/{targetUser}")` | STOMP | Private messaging | ADMIN → CUSTOMER only |
| `/topic/notifications` | STOMP | Receive broadcasts | All authenticated users |
| `/user/queue/private` | STOMP | Receive private messages | User-specific validation |

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
- Advanced JWT-based authentication with JWKS and custom validation
- API Gateway with routing, CORS, rate limiting, and WebSocket proxying
- Role-based authorization with 15+ granular permissions
- nexhub-common shared library for code reusability

**Business Services**:
- Full CRUD operations across all domain services
- Database-per-service architecture with proper isolation
- Redis caching implementation across critical services
- Email notification system with Kafka integration

**Advanced Real-time Features**:
- Direct WebSocket connection with dual-layer interceptor security
- Role-based messaging with ADMIN → CUSTOMER restrictions  
- Pure STOMP messaging with @MessageMapping controllers
- React-based WebSocket demonstration client with real-time validation
- Kafka-based event streaming for asynchronous processing

**Data Management**:
- 6 PostgreSQL databases with comprehensive entity models
- Automated database initialization and schema management
- Service integration with OpenFeign clients

### 🔧 Recent Major Enhancements (August 2025)

**Security & Architecture Improvements**:
- Direct WebSocket connection architecture bypassing API Gateway
- Dual-layer interceptor security with real-time JWT validation on each message
- Enhanced username pattern validation for target user identification
- Custom JwtService with JWKS validation and comprehensive exception handling
- Token expiration handling during active WebSocket sessions
- nexhub-common library with shared utilities and authorization aspects

**Development & Testing Enhancements**:
- React-based WebSocket demo application with modern React 19.1.1
- Comprehensive JWT validation exceptions for better error handling
- Improved Docker builds with multi-stage optimization
- Enhanced health check monitoring and resilience

**Code Quality & Maintainability**:
- Centralized common utilities in nexhub-common module
- Removed code duplication across services (BaseResponse, GlobalExceptionHandler)
- Improved separation of concerns with dedicated interceptors
- Enhanced logging and debugging capabilities

### ⚠️ Known Limitations & Considerations

**Service Port Mapping**:
- Product Service: External 8084 → Internal 8080 (architectural decision for container optimization)
- Blog Service: External 8087 → Internal 8080 (architectural decision for container optimization)

**Incomplete Features**:
- Language Service: Directory exists but service not fully configured
- Blog Service: Author and Tag management controllers need completion

**Development Considerations**:
- WebSocket connects directly to port 8083 (bypasses API Gateway)
- Broadcast messages require ADMIN role for sending
- Private messages restricted to ADMIN → CUSTOMER communication only
- Target usernames validated by pattern matching for CUSTOMER identification
- Database initialization requires proper startup sequence  
- Redis password configuration needed for local development
- nexhub-common library must be built before other services

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
**Version**: 3.2.0  
**Last Updated**: August 31, 2025  
**Status**: Production-Ready with Active Development  
**Architecture**: Spring Boot 3.5.5 Microservices with Direct WebSocket Communication  
**Infrastructure**: Docker, PostgreSQL, Redis, Kafka, Direct WebSocket, React Demo  
**Security**: Enhanced JWT with RSA-256, JWKS, Dual-Layer WebSocket Interceptors, Role-Based Messaging  
**Maintainer**: DevWonder Development Team  

**Total Services**: 10 (7 Business + 3 Infrastructure)  
**Database Count**: 6 PostgreSQL databases with service isolation  
**Message Brokers**: 3-node Kafka cluster with Zookeeper  
**Caching**: Redis with distributed caching strategy  
**Real-time**: Direct WebSocket with STOMP, Dual-Layer Security, Role-Based Messaging, and React demo client  
**Shared Libraries**: nexhub-common for code reusability and maintainability  
**Demo Applications**: React-based WebSocket testing client with modern UI  

**Recent Major Changes**:
- **Direct WebSocket Architecture**: Bypassing API Gateway for improved performance
- **Stateless WebSocket Security**: Pure JWT-based authentication without session storage
- **Dual-Layer Interceptor Security**: JWT authentication + real-time JWT validation for authorization
- **Role-Based Messaging**: ADMIN-only broadcasts, ADMIN → CUSTOMER private messaging
- **Real-time Token Validation**: Fresh JWT validation on each WebSocket message
- **Token Expiration Handling**: Automatic session termination when tokens expire
- **Username Pattern Validation**: Server-side validation for target user identification
- **Pure STOMP Implementation**: @MessageMapping controllers replacing REST endpoints
- **Enhanced React Demo**: Real-time validation with visual feedback
- **nexhub-common shared library**: Code reusability and maintainability improvements

This documentation represents the current state of NexHub as of August 31, 2025, reflecting all implemented features, recent enhancements, architectural improvements, and comprehensive development roadmap.