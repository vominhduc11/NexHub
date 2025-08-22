# NexHub - Microservices Project Overview

## 🏗️ Architecture Overview

**NexHub** is a comprehensive e-commerce microservices platform built with Spring Boot, featuring a complete ecosystem of infrastructure and business services focused on product management, warranty tracking, and reseller operations.

### 🔧 Infrastructure Services

| Service | Port | Description | Technology | Implementation Status |
|---------|------|-------------|------------|---------------------|
| **Config Server** | 8888 | Centralized configuration management | Spring Cloud Config | ✅ Complete |
| **Discovery Service** | 8761 | Service registry and discovery | Netflix Eureka | ✅ Complete |
| **API Gateway** | 8080 | Main entry point and routing | Spring Cloud Gateway | ✅ Complete |

### 🔐 Security & Authentication

| Service | Port | Database | Description | Key Features |
|---------|------|----------|-------------|--------------|
| **Auth Service** | 8081 | nexhub_auth | JWT-based authentication & authorization | RSA-256 JWT, Role-based access, Kafka notifications |

**Authentication Flow:**
- JWT tokens with RSA-256 signatures
- Role-based access control (Admin, Customer, Reseller)
- Account-to-service user mapping
- JWKS endpoint for token validation
- Redis session management

### 💼 Business Services

| Service | Port | Database | Description | Implementation Status |
|---------|------|----------|-------------|---------------------|
| **User Service** | 8082 | nexhub_user | Customer, Admin, Reseller management | ✅ Complete |
| **Notification Service** | 8083 | - | Async email notifications via Kafka | ✅ Complete |
| **Product Service** | 8084 | nexhub_product | Product catalog with serial numbers | 🚧 Skeleton only |
| **Warranty Service** | 8085 | nexhub_warranty | Warranty tracking & product purchases | ✅ Basic structure |
| **Language Service** | 8086 | nexhub_language | Internationalization support | 🚧 Skeleton only |
| **Blog Service** | 8087 | nexhub_blog | Content management system | 🚧 Skeleton only |

### 🗄️ Data & Infrastructure

| Component | Port | Description | Configuration |
|-----------|------|-------------|---------------|
| **PostgreSQL** | 5432 | Primary database | 7 separate databases with proper isolation |
| **Redis** | 6379 | Cache & session store | Password-protected, persistent storage |
| **Kafka Cluster** | 9092-9094 | Message streaming | 3-broker cluster with Zookeeper |
| **Zookeeper** | 2181-2183 | Kafka coordination | 3-node ensemble for HA |

## 🔐 Security Architecture

### Authentication Flow
```
Client → API Gateway → Auth Service → JWT Token (RSA-256) → Protected Resources
                    ↓
            Notification Service (via Kafka) → Email notifications
```

### JWT Implementation Details
- **RSA-256 Signatures**: Self-generated 2048-bit RSA key pairs
- **JWKS Endpoint**: `/auth/.well-known/jwks.json` for public key distribution
- **Token Claims**: accountId, username, userType, roles, permissions
- **Token Expiration**: 24 hours (configurable)
- **Refresh Strategy**: Client-managed re-authentication

### Authorization Levels
- **Admin**: Full system access across all services
- **Customer**: Customer-specific operations, purchase history
- **Reseller**: Reseller management, customer creation, product sales

### API Security
- JWT-based authentication with role validation
- Gateway-level CORS configuration for multi-origin support
- Service-to-service communication via X-Gateway-Request headers
- Database isolation per service with dedicated schemas

## 📊 Database Design

### Database-per-Service Pattern
```sql
├── nexhub_auth        # Authentication accounts, roles, permissions
├── nexhub_user        # User profiles (Customer, Admin, Reseller)
├── nexhub_product     # Product catalog, categories, serials
├── nexhub_warranty    # Purchased products, warranty tracking
├── nexhub_blog        # Blog posts, authors, categories
├── nexhub_notification # Notification history
└── nexhub_language    # Localization data
```

### Key Data Models

#### Auth Service (nexhub_auth)
```sql
accounts:
├── id (PK)             # Auto-generated account ID
├── username (UNIQUE)   # Login username
└── password           # BCrypt hashed password

account_roles:
├── account_id (FK)    # Reference to accounts
└── role_id (FK)      # Reference to roles

roles:
├── id (PK)           # Role ID
└── name             # Role name (ADMIN, CUSTOMER, RESELLER)

permissions:
├── id (PK)           # Permission ID
└── name             # Permission name
```

#### User Service (nexhub_user)
```sql
resellers:
├── account_id (PK)    # Maps to auth_service.accounts.id
├── name              # Business name
├── address, phone    # Contact information
├── district, city    # Location
└── created_at/updated_at
```

#### Warranty Service (nexhub_warranty)
```sql
purchased_products:
├── id (PK)                    # Purchase record ID
├── purchase_date             # Purchase date
├── expiration_date          # Warranty expiration
├── warranty_remaining_days  # Auto-calculated remaining days
├── id_product_serial (FK)   # Product serial reference
├── id_reseller (FK)        # Selling reseller
└── id_customer (FK)        # Customer who purchased
```

### Cross-Service Data Relations
- **Account ID Mapping**: Auth service account IDs used as foreign keys in user services
- **Service Communication**: OpenFeign clients for inter-service calls
- **Event-Driven Updates**: Kafka events for data consistency across services

## 🚀 Technology Stack

### Backend Framework
- **Java 17** with **Spring Boot 3.5.4**
- **Spring Cloud 2025.0.0** for microservices
- **Maven** for dependency management

### Data Technologies
- **Spring Data JPA** with **Hibernate**
- **PostgreSQL 15** as primary database
- **Redis 7** for caching and sessions
- **Spring Data Redis** with **Lettuce** connection pool

### Message Streaming & Communication
- **Apache Kafka 7.4** with 3-broker cluster for event streaming
- **Spring Kafka** with consumer groups and producers
- **Async Email Notifications**: Auth Service → Kafka → Notification Service
- **OpenFeign** for synchronous service-to-service calls
- **Spring Cloud LoadBalancer** for client-side load balancing
- **Spring Mail** with Gmail SMTP for email delivery

### Documentation & Monitoring
- **SpringDoc OpenAPI 3** for API documentation
- **Spring Boot Actuator** for health monitoring
- **Centralized Swagger UI** at Gateway level with service grouping
- **Comprehensive API Documentation** for all implemented services

## 🐳 Deployment Strategy

### Docker Composition Architecture
```yaml
Infrastructure Layer:
├── PostgreSQL (with init scripts for 7 databases)
├── Redis (password-protected, persistent volumes)
├── Zookeeper Ensemble (3-node cluster)
└── Kafka Cluster (3 brokers)

Service Layer:
├── Config Server → Discovery Service → API Gateway
└── Business Services (Auth, User, Notification, Warranty)

Network: Isolated bridge network (next-network)
```

### Service Startup Dependencies
```
1. Infrastructure: PostgreSQL, Redis, Zookeeper Cluster, Kafka Cluster
2. Config Server (health check required)
3. Discovery Service (depends on Config Server)
4. API Gateway (depends on Config + Discovery)
5. Business Services (parallel startup with dependencies)
```

### Health Monitoring & Resilience
- All services expose `/actuator/health` endpoints
- Docker health checks with 30s intervals, 3 retries
- Auto-restart policy: `unless-stopped`
- Service-specific health check URLs and timeouts
- Database connection validation in health checks

## 📈 Scalability Features

### Horizontal Scaling
- **Eureka Service Discovery**: Dynamic service registration
- **Load Balancing**: Built-in client-side load balancing
- **Stateless Services**: Enable easy scaling

### Event-Driven Architecture
- **Kafka Streaming**: Asynchronous notification system
- **Email Notifications**: Auth Service → Kafka → Notification Service
- **Topic-based Messaging**: `email-notifications` topic for decoupled communication
- **Consumer Groups**: Reliable message processing with offset management

### Caching Strategy
- **Redis Cluster**: Distributed caching
- **Session Management**: Centralized session storage
- **Application Cache**: Performance optimization

## 🔧 Development Features

### API Documentation
```
Main Swagger UI: http://localhost:8080/swagger-ui.html
├── 🔐 Authentication Service
├── 👤 User Management Service  
├── 📬 Notification Service
├── 🛡️ Warranty Service
├── 📝 Blog Service (planned)
└── 🛍️ Product Service (planned)
```

### Configuration Management
- **External Configuration**: Environment-specific settings
- **Hot Reload**: Runtime configuration updates
- **Secret Management**: Secure credential handling

### Code Quality
- **Lombok**: Reduced boilerplate code
- **Validation**: JSR-303 bean validation
- **Exception Handling**: Centralized error management
- **Logging**: Structured logging with SLF4J

## 🌐 API Endpoints & Gateway Configuration

### Gateway Route Mapping
```
/api/auth/**         → Auth Service (8081)     ✅ Implemented
/api/user/**         → User Service (8082)     ✅ Implemented  
/api/notification/** → Notification Service (8083) ✅ Implemented
/api/product/**      → Product Service (8084)  🚧 Skeleton
/api/warranty/**     → Warranty Service (8085) ✅ Basic
/api/language/**     → Language Service (8086) 🚧 Skeleton
/api/blog/**         → Blog Service (8087)     🚧 Skeleton
```

### Swagger UI Integration
```
Main Hub: http://localhost:8080/swagger-ui.html
├── 🔐 Authentication Service
├── 👤 User Management Service  
├── 📬 Notification Service
├── 🛡️ Warranty Service
├── 🛍️ Product Service (planned)
├── 📝 Blog Service (planned)
└── 🌐 Language Service (planned)
```

### CORS Configuration
- **Allowed Origins**: `localhost:3000,localhost:8080,localhost:5731`
- **Allowed Methods**: `GET,POST,PUT,DELETE,OPTIONS`
- **Allowed Headers**: Full header support including Authorization
- **Credentials**: Enabled for JWT authentication

## 🚦 Getting Started

### Prerequisites
- **Java 17+**
- **Docker & Docker Compose**
- **Maven 3.6+**

### Quick Start
```bash
# Clone repository
git clone <repository-url>
cd NexHub

# Start all services
docker-compose up -d

# Verify services
docker-compose ps

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

### Development Mode
```bash
# Start infrastructure only
docker-compose up -d postgres redis kafka1 kafka2 kafka3

# Run services locally
cd config-server && mvn spring-boot:run
cd discovery-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
```

## 📋 Implementation Status & Current State

### Infrastructure Services
| Service | Status | Implementation | Health Check | Key Features |
|---------|--------|----------------|--------------|--------------|
| **Config Server** | ✅ Production Ready | Complete | ✅ Active | Native file-based config, Docker integration |
| **Discovery Service** | ✅ Production Ready | Complete | ✅ Active | Eureka server, service registry |
| **API Gateway** | ✅ Production Ready | Complete | ✅ Active | Route aggregation, CORS, Swagger proxy |

### Business Services
| Service | Status | Implementation | API Docs | Database | Key Features |
|---------|--------|----------------|----------|----------|--------------|
| **Auth Service** | ✅ Production Ready | Complete | ✅ Swagger | nexhub_auth | RSA-256 JWT, Role-based auth, Kafka integration |
| **User Service** | ✅ Production Ready | Complete | ✅ Swagger | nexhub_user | Reseller CRUD, Account mapping |
| **Notification Service** | ✅ Production Ready | Complete | ✅ Swagger | - | Async email via Kafka, Gmail SMTP |
| **Warranty Service** | ⚠️ Basic Implementation | Entities only | ✅ Swagger | nexhub_warranty | Purchase tracking, warranty calculation |
| **Product Service** | 🚧 Skeleton Only | Entities only | 🔄 Planned | nexhub_product | Product catalog structure |
| **Blog Service** | 🚧 Skeleton Only | Entities only | 🔄 Planned | nexhub_blog | Content management structure |
| **Language Service** | 🚧 Skeleton Only | Entities only | 🔄 Planned | nexhub_language | I18n support structure |

### Recent Development Focus
- ✅ **Authentication System**: Complete JWT implementation with JWKS
- ✅ **Async Notifications**: Kafka-based email system
- ✅ **API Gateway Integration**: Centralized Swagger documentation
- ✅ **Database Architecture**: Multi-database setup with proper isolation
- ⚠️ **Business Logic**: Limited to auth, user management, and notifications
- 🚧 **Product Operations**: Requires implementation for e-commerce functionality

## 🔍 Monitoring & Debugging

### Health Endpoints
```
Infrastructure:
├── Config Server:        http://localhost:8888/actuator/health
├── Discovery Service:    http://localhost:8761/actuator/health
└── API Gateway:          http://localhost:8080/actuator/health

Business Services:
├── Auth Service:         http://localhost:8081/actuator/health
├── User Service:         http://localhost:8082/actuator/health  
├── Notification Service: http://localhost:8083/actuator/health
├── Product Service:      http://localhost:8084/actuator/health
├── Warranty Service:     http://localhost:8085/actuator/health
├── Language Service:     http://localhost:8086/actuator/health
└── Blog Service:         http://localhost:8087/actuator/health
```

### Troubleshooting Guide
```bash
# Service logs
docker-compose logs -f <service-name>
docker-compose logs -f auth-service

# Network connectivity
docker network inspect next-network

# Database access
docker exec -it nexhub-postgres psql -U nexhub -d nexhub_auth
docker exec -it nexhub-postgres psql -U nexhub -d nexhub_user

# Redis connection
docker exec -it nexhub_redis redis-cli -a voduc123

# Kafka cluster status
docker exec -it nexhub_kafka1 kafka-topics --bootstrap-server kafka1:9092 --list

# Service discovery status
curl http://localhost:8761/eureka/apps
```

## 🎯 Development Roadmap

### Completed ✅
- Complete microservices infrastructure (Config, Discovery, Gateway)
- JWT authentication with RSA-256 signing and JWKS endpoint
- Role-based authorization (Admin, Customer, Reseller)
- Async notification system via Kafka
- User management service for resellers
- Centralized Swagger UI documentation
- Multi-database architecture with proper isolation

### In Progress ⚠️
- Warranty service business logic implementation
- Product catalog management system
- Customer purchase workflow

### Planned 🚧
- Complete product service with inventory management
- Blog/CMS functionality for marketing content
- Language service for internationalization
- Advanced warranty tracking and reporting
- Customer portal and self-service features
- Admin dashboard for system management

---

**Last Updated**: August 22, 2025  
**Version**: 1.2.0  
**Architecture**: Spring Boot 3.5.4 Microservices  
**Maintainer**: DevWonder Team