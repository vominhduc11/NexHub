# NexHub - Enterprise Microservices E-Commerce Platform

![NexHub Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

> **Production-Ready Enterprise E-Commerce Platform** built with Spring Boot 3.5.5 microservices architecture, featuring comprehensive JWT security, real-time WebSocket communications, event-driven architecture with Kafka messaging, and complete Docker orchestration.

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Docker & Docker Compose**
- **Maven 3.9+**
- **Node.js** (for React WebSocket demo)

### Option 1: Full Docker Deployment
```bash
# Start all services with Docker
cd infrastructure
docker-compose up -d

# Check service health
docker-compose ps
```

### Option 2: Development Mode (Infrastructure Only)
```bash
# Start only infrastructure services (PostgreSQL, Redis, Kafka)
cd config
docker-compose -f docker-compose-infrastructure.yml up -d

# Build shared library first
cd services/shared/nexhub-common
mvn clean install

# Start services locally in order
cd services/infrastructure/config-server && mvn spring-boot:run
cd services/infrastructure/discovery-service && mvn spring-boot:run
cd services/infrastructure/api-gateway && mvn spring-boot:run

# Start business services
cd services/business/auth-service && mvn spring-boot:run
cd services/business/user-service && mvn spring-boot:run
# ... other services
```

### Option 3: Interactive WebSocket Demo
```bash
cd examples/websocket-demo-react
npm install && npm run dev
# Open http://localhost:5173
```

## 📁 Project Structure

```
nexhub/
├── services/
│   ├── infrastructure/          # Core platform services
│   │   ├── config-server/       # Centralized configuration
│   │   ├── discovery-service/   # Service registry (Eureka)
│   │   └── api-gateway/         # API gateway & routing
│   ├── business/                # Domain services
│   │   ├── auth-service/        # Authentication & authorization
│   │   ├── user-service/        # User & reseller management
│   │   ├── product-service/     # Product catalog
│   │   ├── warranty-service/    # Warranty management
│   │   ├── blog-service/        # Content management
│   │   ├── notification-service/# Real-time notifications
│   │   └── language-service/    # Internationalization
│   └── shared/
│       └── nexhub-common/       # Shared utilities & components
├── infrastructure/
│   └── docker-compose.yml       # Full platform deployment
├── config/
│   ├── application.yml          # Global configuration
│   ├── docker-compose-infrastructure.yml  # Dev infrastructure
│   └── services/               # Individual service configs
├── scripts/
│   ├── database/               # Database initialization
│   └── deployment/             # Deployment scripts
├── docs/
│   ├── PROJECT_OVERVIEW.md     # Detailed documentation
│   ├── api-examples/           # API usage examples
│   └── architecture/           # System diagrams
└── examples/
    ├── websocket-demo-react/   # Interactive WebSocket client
    └── test-data/              # Sample data for testing
```

## 🔧 Key Services

| Service | Port | Purpose |
|---------|------|---------|
| **API Gateway** | 8080 | Main entry point, routing, security |
| **Config Server** | 8888 | Centralized configuration |
| **Discovery Service** | 8761 | Service registry |
| **Auth Service** | 8081 | Authentication & JWT management |
| **User Service** | 8082 | User & reseller profiles |
| **Notification Service** | 8083 | Real-time WebSocket notifications |
| **Product Service** | 8084 | Product catalog management |
| **Warranty Service** | 8085 | Warranty tracking & claims |
| **Blog Service** | 8087 | Content management system |

## 🌐 Access Points

| Resource | URL | Purpose |
|----------|-----|---------|
| **API Documentation** | http://localhost:8080/swagger-ui.html | Complete API docs |
| **Service Discovery** | http://localhost:8761 | Eureka dashboard |
| **WebSocket Demo** | http://localhost:5173 | Interactive WebSocket testing |
| **Kafka Management** | http://localhost:8078 | Kafka UI |
| **Redis Management** | http://localhost:8079 | Redis Commander |

## 🏗️ Architecture Highlights

- **Event-Driven Architecture**: Kafka-based reseller lifecycle management
- **Advanced Security**: Custom AllAuthoritiesAuthorizationManager with JWT
- **Real-time Communication**: Direct WebSocket connections with dual-layer security
- **Database-per-Service**: 6 isolated PostgreSQL databases
- **Shared Library**: nexhub-common for consistency across services
- **Configuration Management**: Centralized config with environment-specific profiles

## 📚 Documentation

- **[Project Overview](docs/PROJECT_OVERVIEW.md)** - Comprehensive system documentation
- **[API Examples](docs/api-examples/)** - Usage examples and demos
- **[Architecture Diagrams](docs/architecture/)** - System design and flow diagrams

## 🚀 Development

### Build Order
```bash
# 1. Build shared library first
cd services/shared/nexhub-common && mvn clean install

# 2. Build all services
mvn clean package -f services/infrastructure/config-server/pom.xml
mvn clean package -f services/infrastructure/discovery-service/pom.xml
mvn clean package -f services/infrastructure/api-gateway/pom.xml
# ... business services
```

### Configuration Management
- **Global Config**: `config/application.yml`
- **Service-Specific**: `config/services/`
- **Environment Profiles**: docker, local, production

## 🔒 Security Features

- **JWT Authentication**: RSA-256 with JWKS endpoint
- **Role-Based Access Control**: ADMIN, DEALER, CUSTOMER
- **Custom Authorization**: AllAuthoritiesAuthorizationManager (AND logic)
- **WebSocket Security**: Dual-layer interceptor architecture
- **Cross-Service Validation**: API key and header validation

## 📊 Monitoring & Health Checks

All services expose health endpoints at `/actuator/health`:
```bash
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
# ... other services
```

## 🤝 Contributing

1. Follow Spring Boot conventions and project structure
2. Use nexhub-common for shared utilities
3. Implement proper health checks and monitoring
4. Add comprehensive tests
5. Update documentation

## 📄 License

Enterprise License - DevWonder Development Team

---

**Version**: 4.1.0  
**Last Updated**: September 2025  
**Maintainer**: DevWonder Development Team