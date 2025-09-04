#!/bin/bash

# NexHub Microservices Startup Script
# This script starts all services in the correct order for development

set -e

echo "🚀 Starting NexHub Microservices Platform..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running from project root
if [ ! -f "README.md" ]; then
    print_error "Please run this script from the NexHub project root directory"
    exit 1
fi

# Step 1: Start Infrastructure Services
print_status "Starting infrastructure services (PostgreSQL, Redis, Kafka)..."
cd config
docker-compose -f docker-compose-infrastructure.yml up -d

print_status "Waiting for infrastructure services to be ready..."
sleep 30

# Step 2: Build Shared Library
print_status "Building shared library (nexhub-common)..."
cd ../services/shared/nexhub-common
mvn clean install -q
if [ $? -eq 0 ]; then
    print_success "nexhub-common built successfully"
else
    print_error "Failed to build nexhub-common"
    exit 1
fi

cd ../../..

# Step 3: Start Infrastructure Services in Order
print_status "Starting Config Server..."
cd services/infrastructure/config-server
mvn spring-boot:run &
CONFIG_PID=$!
cd ../../..

sleep 15

print_status "Starting Discovery Service..."
cd services/infrastructure/discovery-service
mvn spring-boot:run &
DISCOVERY_PID=$!
cd ../../..

sleep 15

print_status "Starting API Gateway..."
cd services/infrastructure/api-gateway
mvn spring-boot:run &
GATEWAY_PID=$!
cd ../../..

sleep 20

# Step 4: Start Business Services
print_status "Starting Auth Service..."
cd services/business/auth-service
mvn spring-boot:run &
AUTH_PID=$!
cd ../../..

sleep 10

print_status "Starting User Service..."
cd services/business/user-service
mvn spring-boot:run &
USER_PID=$!
cd ../../..

sleep 10

print_status "Starting Notification Service..."
cd services/business/notification-service
mvn spring-boot:run &
NOTIFICATION_PID=$!
cd ../../..

sleep 10

print_status "Starting Product Service..."
cd services/business/product-service
mvn spring-boot:run &
PRODUCT_PID=$!
cd ../../..

sleep 10

print_status "Starting Warranty Service..."
cd services/business/warranty-service
mvn spring-boot:run &
WARRANTY_PID=$!
cd ../../..

sleep 10

print_status "Starting Blog Service..."
cd services/business/blog-service
mvn spring-boot:run &
BLOG_PID=$!
cd ../../..

# Wait a bit for all services to start
sleep 30

# Health check
print_status "Checking service health..."

health_check() {
    local service_name=$1
    local port=$2
    local response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${port}/actuator/health)
    if [ "$response" == "200" ]; then
        print_success "$service_name is healthy (port $port)"
        return 0
    else
        print_error "$service_name is not healthy (port $port)"
        return 1
    fi
}

echo ""
print_status "Health Check Results:"
health_check "Config Server" 8888
health_check "Discovery Service" 8761
health_check "API Gateway" 8080
health_check "Auth Service" 8081
health_check "User Service" 8082
health_check "Notification Service" 8083
health_check "Product Service" 8084
health_check "Warranty Service" 8085
health_check "Blog Service" 8087

echo ""
print_success "🎉 NexHub Platform started successfully!"
echo ""
echo "📊 Access Points:"
echo "   • API Documentation: http://localhost:8080/swagger-ui.html"
echo "   • Service Discovery: http://localhost:8761"
echo "   • Kafka UI: http://localhost:8078"
echo "   • Redis Commander: http://localhost:8079"
echo ""
echo "📝 Process IDs (for stopping services):"
echo "   • Config Server: $CONFIG_PID"
echo "   • Discovery Service: $DISCOVERY_PID"
echo "   • API Gateway: $GATEWAY_PID"
echo "   • Auth Service: $AUTH_PID"
echo "   • User Service: $USER_PID"
echo "   • Notification Service: $NOTIFICATION_PID"
echo "   • Product Service: $PRODUCT_PID"
echo "   • Warranty Service: $WARRANTY_PID"
echo "   • Blog Service: $BLOG_PID"
echo ""
print_warning "To stop all services, run: ./scripts/deployment/stop-services.sh"