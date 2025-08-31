# NexHub - Enterprise Microservices E-Commerce Platform

## Executive Summary

**NexHub** is a production-ready enterprise e-commerce microservices platform built on Spring Boot 3.5.5, designed for scalable product management, warranty tracking, customer operations, and content management. The platform features distributed architecture with Redis caching, Kafka event streaming, WebSocket real-time communication, and comprehensive JWT security across 6 specialized PostgreSQL databases.

**Recent Enhancements (August 2025)**:
- Advanced WebSocket security with dual-layer JWT authentication
- Shared nexhub-common library for code reusability and maintainability
- Enhanced API Gateway security with comprehensive JWT validation
- React-based WebSocket demonstration client
- Improved microservices architecture with better separation of concerns

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
| **Notification Service** | 8083 | nexhub_notification | Email notifications, WebSocket real-time messaging, advanced JWT validation | Kafka Consumer, WebSocket/STOMP, Spring Mail, Custom JWT Service | ✅ Production Ready |
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

### Advanced WebSocket Security

**Multi-Layer Authentication**:
- **Gateway Level**: Initial JWT validation and routing
- **Application Level**: WebSocketJwtChannelInterceptor for STOMP frame validation
- **Session Management**: User information stored in WebSocket session attributes
- **Token Validation**: Custom JwtService with JWKS validation and expiration checks

**Authorization Architecture**:
- **Connection Authorization**: CUSTOMER role required for WebSocket connections
- **Subscription Authorization**: Per-topic authorization with role verification
- **Principal Management**: Custom Principal creation from JWT claims
- **Session Attributes**: accountId, username, userType, roles, permissions stored per session

**WebSocket Endpoints**:
- **Connection Endpoint**: `/api/notification/ws/notifications`
- **Protocol**: STOMP over SockJS with fallback support
- **Authentication Methods**: Authorization header (Bearer token) or custom token header

### Security Flow Architecture
```
Client Request → API Gateway (JWT Validation + RBAC + WebSocket Routing) → Service (Permission Check) → Database
                      ↓
           JWT Claims Forwarded as Headers
                      ↓
    Notification Service (Kafka + WebSocket) → Email/Real-time Client Updates
                      ↓
              WebSocketJwtChannelInterceptor (STOMP Frame Validation)
                      ↓
                Custom JwtService (JWKS + Signature Validation)
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

### Enhanced WebSocket Architecture
**Implementation**: Complete STOMP-based messaging with SockJS fallback and advanced security
**Endpoint**: `/api/notification/ws/notifications` with comprehensive JWT validation

**Advanced Security Architecture**:
- **Gateway Level**: JWT validation with WebSocket proxying support
- **Application Level**: WebSocketJwtChannelInterceptor with custom JwtService
- **JWKS Integration**: Real-time public key fetching and validation
- **Exception Handling**: Comprehensive JWT validation exceptions (TokenExpiredException, InvalidTokenSignatureException, JwksRetrievalException, JwtValidationException)
- **Session Management**: Rich user context stored in WebSocket session attributes

**Message Patterns**:
```javascript
// Public broadcasts
/topic/dealer-registrations    // Dealer registration notifications
/topic/admin-notifications     // Admin-only system notifications  
/topic/dealer-updates          // Dealer-specific updates

// Private messages
/user/queue/notifications      // User-specific private notifications
```

**React WebSocket Demo Client**: Interactive testing client with modern React 19.1.1 and @stomp/stompjs integration

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
**WebSocket Endpoint**: `ws://localhost:8080/api/notification/ws/notifications`

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
/api/notification/**   → Notification Service (8083)
/ws/notifications      → WebSocket endpoint (STOMP over SockJS)

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

### Real-time Communication
| Endpoint | Protocol | Purpose | Security |
|----------|----------|---------|----------|
| `/api/notification/ws/notifications` | WebSocket/STOMP | Real-time messaging | CUSTOMER+ with JWT validation |
| `/topic/dealer-registrations` | STOMP | Public notifications | Authenticated subscribers |
| `/user/queue/notifications` | STOMP | Private messages | User-specific with JWT validation |

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

# Test WebSocket endpoint (requires authentication)
wscat -c "ws://localhost:8080/api/notification/ws/notifications" \
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
- WebSocket implementation with multi-layer JWT security
- React-based WebSocket demonstration client
- Kafka-based event streaming for asynchronous processing
- Real-time dealer registration notifications with authentication

**Data Management**:
- 6 PostgreSQL databases with comprehensive entity models
- Automated database initialization and schema management
- Service integration with OpenFeign clients

### 🔧 Recent Major Enhancements (August 2025)

**Security & Architecture Improvements**:
- Advanced WebSocket security with WebSocketJwtChannelInterceptor
- Custom JwtService with JWKS validation and comprehensive exception handling
- nexhub-common library with shared utilities and authorization aspects
- Enhanced JWT signature validation across all services

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
- WebSocket requires CUSTOMER+ role for authentication
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

# WebSocket connection test with React demo
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
**Version**: 3.1.0  
**Last Updated**: August 31, 2025  
**Status**: Production-Ready with Active Development  
**Architecture**: Spring Boot 3.5.5 Microservices with Event-Driven Communication  
**Infrastructure**: Docker, PostgreSQL, Redis, Kafka, WebSocket, React Demo  
**Security**: Enhanced JWT with RSA-256, JWKS, Multi-layer WebSocket Authentication  
**Maintainer**: DevWonder Development Team  

**Total Services**: 10 (7 Business + 3 Infrastructure)  
**Database Count**: 6 PostgreSQL databases with service isolation  
**Message Brokers**: 3-node Kafka cluster with Zookeeper  
**Caching**: Redis with distributed caching strategy  
**Real-time**: Advanced WebSocket with STOMP, JWT validation, and React demo client  
**Shared Libraries**: nexhub-common for code reusability and maintainability  
**Demo Applications**: React-based WebSocket testing client with modern UI  

**Recent Major Changes**:
- Enhanced WebSocket security with dual-layer JWT authentication
- React WebSocket demonstration client with modern React 19.1.1
- nexhub-common shared library implementation
- Advanced JWT validation with JWKS integration
- Comprehensive exception handling for WebSocket authentication
- Improved Docker architecture and service orchestration

This documentation represents the current state of NexHub as of August 31, 2025, reflecting all implemented features, recent enhancements, architectural improvements, and comprehensive development roadmap.