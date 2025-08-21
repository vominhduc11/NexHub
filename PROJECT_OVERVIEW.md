# NexHub - Microservices Project Overview

## 🏗️ Architecture Overview

**NexHub** is a comprehensive microservices platform built with Spring Boot, featuring a complete ecosystem of infrastructure and business services.

### 🔧 Infrastructure Services

| Service | Port | Description | Technology |
|---------|------|-------------|------------|
| **Config Server** | 8888 | Centralized configuration management | Spring Cloud Config |
| **Discovery Service** | 8761 | Service registry and discovery | Netflix Eureka |
| **API Gateway** | 8080 | Main entry point and routing | Spring Cloud Gateway |
| **Auth Service** | 8081 | Authentication & authorization | Spring Security + JWT |

### 💼 Business Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **User Service** | 8082 | nexhub_user | Customer, Admin, Reseller management |
| **Blog Service** | TBD | nexhub_blog | Content management system |
| **Product Service** | TBD | nexhub_product | Product catalog management |
| **Warranty Service** | TBD | nexhub_warranty | Warranty tracking system |
| **Notification Service** | 8083 | - | Email & WebSocket notifications |
| **Language Service** | TBD | nexhub_language | Internationalization |

### 🗄️ Data & Infrastructure

| Component | Port | Description | Configuration |
|-----------|------|-------------|---------------|
| **PostgreSQL** | 5432 | Primary database | 7 separate schemas |
| **Redis** | 6379 | Cache & session store | Password: voduc123 |
| **Kafka Cluster** | 9092-9094 | Message streaming | 3 brokers |
| **Zookeeper** | 2181-2183 | Kafka coordination | 3 nodes cluster |

## 🔐 Security Architecture

### Authentication Flow
```
Client → API Gateway → Auth Service → JWT Token → Protected Resources
```

### Authorization Levels
- **Admin**: Full system access
- **Customer**: Customer-specific operations
- **Reseller**: Reseller management functions

### API Security
- JWT-based authentication
- Gateway-level CORS configuration
- Service-to-service communication via headers

## 📊 Database Design

### Database-per-Service Pattern
```sql
├── nexhub_auth        # User accounts & authentication
├── nexhub_user        # User profiles & roles
├── nexhub_blog        # Blog content & metadata
├── nexhub_product     # Product catalog
├── nexhub_warranty    # Warranty records
├── nexhub_notification # Notification history
└── nexhub_language    # Localization data
```

### Data Models
- **Account Entity**: Core authentication (username, password)
- **User Entities**: Customer, Admin, Reseller with timestamps
- **JPA Relationships**: Account ID as foreign key reference

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

### Service Communication
- **Apache Kafka 7.4** for event streaming
- **Spring Kafka** with consumer groups and producers
- **OpenFeign** for service-to-service calls
- **Spring Cloud LoadBalancer** for client-side load balancing
- **Spring Mail** for SMTP email delivery

### Documentation & Monitoring
- **SpringDoc OpenAPI 3** for API documentation
- **Spring Boot Actuator** for health monitoring
- **Centralized Swagger UI** at Gateway level

## 🐳 Deployment Strategy

### Docker Composition
```yaml
Services: Config → Discovery → Gateway → Business Services
Dependencies: PostgreSQL, Redis, Kafka, Zookeeper
Network: Isolated bridge network (next-network)
```

### Service Startup Order
1. **Infrastructure**: PostgreSQL, Redis, Kafka, Zookeeper
2. **Config Server**: Configuration management
3. **Discovery Service**: Service registry
4. **API Gateway**: Request routing
5. **Business Services**: Auth, User, etc.

### Health Monitoring
- All services expose `/actuator/health` endpoints
- Docker health checks with retry logic
- Auto-restart on failure (unless-stopped)

## 📈 Scalability Features

### Horizontal Scaling
- **Eureka Service Discovery**: Dynamic service registration
- **Load Balancing**: Built-in client-side load balancing
- **Stateless Services**: Enable easy scaling

### Event-Driven Architecture
- **Kafka Streaming**: Asynchronous communication
- **Topic-based Messaging**: Decoupled service interactions
- **Event Sourcing**: Audit trail and data consistency

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
├── 📝 Blog Service (planned)
├── 🛍️ Product Service (planned)
└── 🛡️ Warranty Service (planned)
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

## 🌐 API Endpoints

### Gateway Routes
```
/api/auth/**        → Authentication Service
/api/user/**        → User Management Service
/api/blog/**        → Blog Service
/api/product/**     → Product Service
/api/warranty/**    → Warranty Service
/api/notification/** → Notification Service
/api/language/**    → Language Service
```

### CORS Configuration
- **Allowed Origins**: localhost:3000, localhost:8080, localhost:5731
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Credentials**: Enabled for authentication

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

## 📋 Service Status

| Service | Status | Implementation | API Docs | Features |
|---------|--------|----------------|----------|----------|
| Config Server | ✅ Active | Complete | N/A | External config |
| Discovery Service | ✅ Active | Complete | Health Check | Eureka registry |
| API Gateway | ✅ Active | Complete | Route Config | Security, CORS, Routing |
| Auth Service | ✅ Active | Complete | ✅ Swagger | JWT, Kafka, Redis |
| User Service | ✅ Active | Complete | ✅ Swagger | JPA, Redis, CRUD |
| Notification Service | ✅ Active | Basic | ✅ Swagger | Kafka, Mail, WebSocket |
| Blog Service | 🔄 Planned | Not Started | 🔄 Pending | - |
| Product Service | 🔄 Planned | Not Started | 🔄 Pending | - |
| Warranty Service | 🔄 Planned | Not Started | 🔄 Pending | - |
| Language Service | 🔄 Planned | Not Started | 🔄 Pending | - |

## 🔍 Monitoring & Debugging

### Health Endpoints
```
Config Server:        http://localhost:8888/actuator/health
Discovery Service:    http://localhost:8761/actuator/health
API Gateway:          http://localhost:8080/actuator/health
Auth Service:         http://localhost:8081/actuator/health
User Service:         http://localhost:8082/actuator/health
Notification Service: http://localhost:8083/actuator/health
```

### Troubleshooting
- Check service logs: `docker-compose logs -f <service-name>`
- Verify network connectivity: `docker network inspect next-network`
- Database connection: `docker exec -it nexhub-postgres psql -U nexhub -d nexhub_auth`
- Redis connection: `docker exec -it nexhub_redis redis-cli -a voduc123`

---

**Last Updated**: August 2025  
**Version**: 1.0.0  
**Maintainer**: DevWonder Team