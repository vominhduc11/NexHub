# NexHub - Microservices Access Guide

## 🚀 Core Services Overview

| Service | URL | Description |
|---------|-----|-------------|
| **API Gateway** | http://localhost:8080 | Main entry point for all APIs |
| **Discovery Service** | http://localhost:8761 | Eureka service registry |
| **Config Server** | http://localhost:8888 | Configuration management |
| **Auth Service** | http://localhost:8081 | Authentication & authorization |

## 🗄️ Database Management

### PostgreSQL Database
- **Host**: localhost
- **Port**: 5432
- **Username**: `nexhub`
- **Password**: `nexhub123`
- **Databases**: 
  - `nexhub_auth` (main)
  - `nexhub_user`
  - `nexhub_blog`
  - `nexhub_product`
  - `nexhub_warranty`
  - `nexhub_notification`
  - `nexhub_language`

### Connection Command
```bash
# Connect via psql
PGPASSWORD=nexhub123 psql -h localhost -p 5432 -U nexhub -d nexhub_auth

# Connect via Docker exec
docker exec -it nexhub-postgres psql -U nexhub -d nexhub_auth
```

## 🔴 Redis Cache

### Redis Server
- **Host**: localhost
- **Port**: 6379
- **Password**: `voduc123`

### Connection Command
```bash
# Connect via redis-cli
redis-cli -h localhost -p 6379 -a voduc123

# Connect via Docker exec
docker exec -it dishub_redis redis-cli -a voduc123
```

## 📨 Message Queue (Kafka)

### Kafka Cluster
- **Kafka 1**: localhost:9092
- **Kafka 2**: localhost:9093  
- **Kafka 3**: localhost:9094

### Zookeeper Cluster
- **Zookeeper 1**: localhost:2181
- **Zookeeper 2**: localhost:2182
- **Zookeeper 3**: localhost:2183

## 🐳 Docker Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps

# Stop all services
docker-compose down

# Restart specific service
docker-compose restart <service-name>
```

## 🔧 Troubleshooting

### PostgreSQL Connection Issues
If you encounter "Connection refused" errors:

1. **Check if host PostgreSQL is running:**
   ```bash
   sudo systemctl status postgresql
   ```

2. **Stop host PostgreSQL if needed:**
   ```bash
   sudo systemctl stop postgresql
   sudo systemctl disable postgresql  # To prevent auto-start
   ```

3. **Restart Docker PostgreSQL:**
   ```bash
   docker-compose restart postgres
   ```

4. **Verify connection:**
   ```bash
   PGPASSWORD=nexhub123 psql -h localhost -p 5432 -U nexhub -d nexhub_auth
   ```

### Port Conflicts
- PostgreSQL: 5432
- Redis: 6379
- Kafka: 9092, 9093, 9094
- Zookeeper: 2181, 2182, 2183

Make sure these ports are not occupied by other services.

## 📊 Health Checks

- **Config Server**: http://localhost:8888/actuator/health
- **Discovery Service**: http://localhost:8761/actuator/health
- **API Gateway**: http://localhost:8080/actuator/health
- **Auth Service**: http://localhost:8081/actuator/health

## 🔧 Development Notes

- All services use Docker network: `next-network`
- Data persistence: PostgreSQL and Redis data are stored in Docker volumes
- Services start in dependency order (Config → Discovery → Gateway/Auth)
- CORS enabled for frontend development on ports 3000, 5731
- **Important**: Stop host PostgreSQL service to avoid port conflicts

---
*Updated: 2025-08-18 - After resolving PostgreSQL port conflicts*
