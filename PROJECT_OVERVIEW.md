# NexHub - Microservices Project Overview

## 🏗️ Architecture Overview

**NexHub** is a comprehensive e-commerce microservices platform built with Spring Boot, featuring a complete ecosystem of infrastructure and business services focused on product management, warranty tracking, customer management, and reseller operations. The platform implements a distributed architecture with advanced caching, event-driven communication, and robust security features.

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
| **User Service** | 8082 | nexhub_user | Customer, Admin, Reseller management with complete CRUD operations | ✅ Complete Implementation |
| **Notification Service** | 8083 | nexhub_notification | Async email notifications via Kafka with Gmail SMTP integration | ✅ Complete Implementation |
| **Product Service** | 8084 | nexhub_product | Complete product ecosystem with Redis caching, media management, categories, and serial tracking | ✅ Complete Implementation |
| **Warranty Service** | 8085 | nexhub_warranty | Warranty tracking & product purchases with expiration management | ⚠️ Entities implemented |
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
- **Gateway Integration**: JWT forwarding filter with claim extraction

### Authorization Levels
- **Admin**: Full system access across all services
- **Customer**: Customer-specific operations, purchase history
- **Reseller**: Reseller management, customer creation, product sales

### API Security
- JWT-based authentication with role validation
- Gateway-level CORS configuration for multi-origin support
- **JWT Forwarding Filter**: Automatic JWT claim extraction and header forwarding
- Service-to-service communication via X-Gateway-Request headers
- **Role-Based Access Control**: Header-based authorization in downstream services
- Database isolation per service with dedicated schemas
- **Security Headers**: X-JWT-Subject, X-JWT-Username, X-JWT-Account-ID, X-User-Roles, X-JWT-Authorities

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

customers:
├── account_id (PK)    # Maps to auth_service.accounts.id
├── full_name         # Customer full name
├── email (UNIQUE)    # Email address
├── phone (UNIQUE)    # Phone number
├── date_of_birth     # Date of birth
├── address           # Full address
├── ward, district, city, province # Location details
├── is_active         # Account status
└── created_at/updated_at
```

#### Product Service (nexhub_product)
```sql
products:
├── id (PK)                    # Auto-generated product ID
├── name                       # Product name
├── subtitle                   # Product subtitle
├── description                # Short description
├── long_description           # Detailed description
├── category_id (FK)           # Reference to categories
├── specifications (JSONB)     # Technical specifications
├── availability_status        # AVAILABLE, OUT_OF_STOCK, DISCONTINUED
├── release_date               # Product release date
├── estimated_delivery         # Delivery timeframe
├── warranty_period           # Warranty duration in months
├── warranty_coverage         # What's covered
├── warranty_conditions       # Warranty terms
├── warranty_excludes         # What's not covered
├── warranty_registration_required # Boolean flag
├── highlights                # Key features
├── target_audience           # Intended users
├── use_cases                 # Usage scenarios
├── popularity                # Popularity score
├── rating                    # Average rating
├── review_count              # Number of reviews
├── tags                      # Searchable tags
├── sku (UNIQUE)              # Stock keeping unit
├── related_product_ids (JSONB) # Related products
├── accessories               # Available accessories
├── seo_title, seo_description # SEO metadata
├── created_at, updated_at    # Timestamps
└── published_at              # Publication date

categories:
├── id (PK)                   # Category ID
├── name                      # Category name
├── description               # Category description
└── slug (UNIQUE)             # URL-friendly identifier

product_images:
├── id (PK)                   # Image ID
├── product_id (FK)           # Reference to products
├── image_url                 # Image URL
├── alt_text                  # Accessibility text
├── display_order             # Sort order
└── is_primary                # Primary image flag

product_features:
├── id (PK)                   # Feature ID
├── product_id (FK)           # Reference to products
├── feature_name              # Feature name
├── feature_value             # Feature value
└── display_order             # Sort order

product_serials:
├── id (PK)                   # Serial ID
├── product_id (FK)           # Reference to products
├── serial_number (UNIQUE)    # Unique serial number
├── status                    # AVAILABLE, SOLD, RESERVED
└── created_at                # Registration date
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

## ⚡ Performance & Caching

### Redis Caching Implementation
The Product Service now implements comprehensive Redis caching for optimal performance:

**Cache Configuration:**
- **Redis Host**: Centralized Redis instance with password protection
- **Connection Pool**: Lettuce connection pooling with optimized settings
- **TTL Management**: 10-minute default cache expiration
- **Cache Type**: Redis-based caching with Spring Boot

**Caching Strategies:**
```java
// Product listings with pagination parameters
@Cacheable(value = "products", key = "'page:' + #page + ':size:' + #size")
public Page<ProductResponse> getAllProducts(int page, int size)

// Category-based product filtering
@Cacheable(value = "products-by-category", key = "'cat:' + #categoryId + ':page:' + #page + ':size:' + #size") 
public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size)

// Search results caching
@Cacheable(value = "product-search", key = "'search:' + #keyword + ':page:' + #page + ':size:' + #size")
public Page<ProductResponse> searchProducts(String keyword, int page, int size)

// Cache eviction on updates
@CacheEvict(value = {"products", "products-by-category", "product-search"}, allEntries = true)
public ProductResponse createProduct(ProductRequest request)
```

**Performance Benefits:**
- **Database Load Reduction**: Cached queries reduce PostgreSQL database hits
- **Response Time Improvement**: Sub-millisecond cache retrieval vs database queries
- **Scalability**: Better performance under high concurrent load
- **Smart Cache Keys**: Granular caching with composite key strategies

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
- **Redis Integration**: Centralized caching with password-protected access
- **Product Service Caching**: Advanced caching with @Cacheable annotations
  - Product listings cached by page/size parameters
  - Category-based product filtering with cache keys
  - Search results caching with keyword-based keys
  - Cache eviction on product updates for data consistency
- **Session Management**: Centralized session storage with Redis
- **TTL Management**: 10-minute default TTL for cached data

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

### Code Quality & Testing
- **Lombok**: Reduced boilerplate code
- **Validation**: JSR-303 bean validation with comprehensive DTOs
  - ProductRequest/Response for product management
  - ProductImageRequest/Response for image management  
  - ProductVideoRequest/Response for video management
- **Exception Handling**: Centralized error management with ApiError responses
- **Logging**: Structured logging with SLF4J
- **Unit Testing**: MockMvc tests with security validation
- **Integration Testing**: Service layer tests with role-based access control

## 🌐 API Endpoints & Gateway Configuration

### Gateway Route Mapping
```
/api/auth/**         → Auth Service (8081)     ✅ Implemented
/api/user/**         → User Service (8082)     ✅ Implemented  
/api/notification/** → Notification Service (8083) ✅ Implemented
/api/product/**      → Product Service (8084)  ⚠️ Partial
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
├── 🛍️ Product Service
│   ├── Product Management
│   ├── Category Management
│   ├── Product Images
│   └── Product Videos
├── 📝 Blog Service (planned)
└── 🌐 Language Service (planned)
```

### Product Service API Endpoints
```
Public Endpoints (via API Gateway):
├── GET  /api/product/products                    # Get all products (paginated)
├── GET  /api/product/products/category/{id}      # Get products by category (paginated) 
└── GET  /api/product/products/search             # Search products by keyword (paginated)

Category Management:
├── GET  /api/product/categories                  # Get all categories (public)
├── POST /api/product/categories                  # Create category (ADMIN only)
├── PUT  /api/product/categories/{id}             # Update category (ADMIN only)
└── DELETE /api/product/categories/{id}           # Delete category (ADMIN only)

Product Management (ADMIN only):
├── POST /api/product/products                    # Create new product
├── PUT  /api/product/products/{id}               # Update product (in development)
└── DELETE /api/product/products/{id}             # Delete product (in development)

Product Media Management:
Images:
├── GET  /api/product/products/{id}/images        # Get product images (public)
├── POST /api/product/products/{id}/images        # Add product image (ADMIN only)
├── PUT  /api/product/products/{id}/images/{imageId}  # Update image (ADMIN only)
└── DELETE /api/product/products/{id}/images/{imageId} # Delete image (ADMIN only)

Videos:
├── GET  /api/product/products/{id}/videos        # Get product videos (public)
├── POST /api/product/products/{id}/videos        # Add product video (ADMIN only)
├── PUT  /api/product/products/{id}/videos/{videoId}  # Update video (ADMIN only)
└── DELETE /api/product/products/{id}/videos/{videoId} # Delete video (ADMIN only)
```

### Product Media Management System
The Product Service now includes a comprehensive media management system:

**Image Management Features:**
- **CRUD Operations**: Complete create, read, update, delete for product images
- **Display Ordering**: Configurable image display order
- **Primary Image Support**: Designation of primary product images  
- **Alt Text**: Accessibility support with alt text for images
- **URL-based Storage**: Flexible image URL management

**Video Management Features:**
- **CRUD Operations**: Complete create, read, update, delete for product videos
- **Thumbnail Support**: Video thumbnail URL management
- **Metadata**: Title, description, and duration tracking
- **Display Ordering**: Configurable video display order

**Security & Access Control:**
- **Public Access**: Anyone can view product media
- **ADMIN Controls**: Only ADMIN users can create, update, or delete media
- **JWT Integration**: Secure authentication via API Gateway
- **Role Validation**: Server-side role checking with SecurityUtil

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
| **Auth Service** | ✅ Production Ready | Complete | ✅ Swagger | nexhub_auth | RSA-256 JWT, Role-based auth, Kafka integration, JWKS endpoint |
| **User Service** | ✅ Production Ready | Complete | ✅ Swagger | nexhub_user | Full Customer & Reseller CRUD, Account mapping, Redis caching |
| **Notification Service** | ✅ Production Ready | Complete | ✅ Swagger | nexhub_notification | Async email via Kafka, Gmail SMTP, Event processing |
| **Product Service** | ✅ Complete Implementation | Full CRUD + Media + Caching | ✅ Swagger | nexhub_product | Redis caching, Media management, Categories, Serial tracking, Security |
| **Warranty Service** | ⚠️ Basic Implementation | Entities only | ✅ Swagger | nexhub_warranty | Purchase tracking, warranty calculation, expiration management |
| **Blog Service** | 🚧 Skeleton Only | Entities only | 🔄 Planned | nexhub_blog | Content management structure |
| **Language Service** | 🚧 Skeleton Only | Entities only | 🔄 Planned | nexhub_language | I18n support structure |

### Recent Development Focus
- ✅ **Authentication System**: Complete JWT implementation with JWKS and Gateway JWT forwarding
- ✅ **Async Notifications**: Kafka-based email system with Gmail SMTP integration
- ✅ **API Gateway Integration**: Centralized Swagger documentation with JWT forwarding filter
- ✅ **Database Architecture**: Multi-database setup with proper isolation (7 databases)
- ✅ **Product Service**: Complete product ecosystem with full CRUD operations and Redis caching
- ✅ **Product Media Management**: Complete image and video management systems with ADMIN controls
- ✅ **Customer Management**: Full Customer service implementation with CRUD operations
- ✅ **Redis Integration**: Advanced caching strategy across Product and User services
- ✅ **Security Enhancement**: Gateway-level JWT forwarding and service-level role validation
- ✅ **Testing Framework**: Comprehensive unit tests for product controller with security validation
- ⚠️ **Warranty Service**: Business logic implementation for purchase and warranty tracking

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
- Product Service with complete product ecosystem including media management
- Advanced product catalog with categories, images, videos, features, serial numbers, and comprehensive data model
- Complete media management system for product images and videos with ADMIN controls
- Enhanced security with JWT forwarding and role-based access control

### In Progress ⚠️
- Warranty service business logic implementation with purchase workflow
- Product inventory management and pricing system
- Advanced product features: stock tracking and availability management
- Customer purchase workflow integration with warranty service
- Performance optimization and monitoring enhancements

### Planned 🚧
- Blog/CMS functionality for marketing content and SEO
- Language service for full internationalization support
- Advanced warranty tracking with reporting and analytics
- Customer portal with self-service features and purchase history
- Admin dashboard for comprehensive system management
- Mobile API optimizations and rate limiting
- Advanced monitoring and observability with distributed tracing

---

**Last Updated**: August 23, 2025  
**Version**: 1.5.0  
**Architecture**: Spring Boot 3.5.4 Microservices with Redis Caching  
**Implementation Status**: Production-Ready Core Services (Auth, User, Product, Notification)  
**Maintainer**: DevWonder Team