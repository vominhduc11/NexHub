#!/bin/bash

# NexHub Microservices Stop Script
# This script stops all running services and infrastructure

set -e

echo "🛑 Stopping NexHub Microservices Platform..."

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

# Function to kill processes on specific ports
kill_port() {
    local port=$1
    local service_name=$2
    
    local pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        kill -9 $pid 2>/dev/null
        print_success "Stopped $service_name (port $port, PID $pid)"
    else
        print_warning "$service_name not running on port $port"
    fi
}

# Stop Spring Boot services
print_status "Stopping Spring Boot services..."
kill_port 8888 "Config Server"
kill_port 8761 "Discovery Service" 
kill_port 8080 "API Gateway"
kill_port 8081 "Auth Service"
kill_port 8082 "User Service"
kill_port 8083 "Notification Service"
kill_port 8084 "Product Service"
kill_port 8085 "Warranty Service"
kill_port 8087 "Blog Service"

# Stop additional processes that might be running
print_status "Cleaning up additional Maven processes..."
pkill -f "spring-boot:run" 2>/dev/null && print_success "Stopped remaining Maven processes" || print_warning "No Maven processes found"

# Stop Docker infrastructure services
print_status "Stopping Docker infrastructure services..."
cd config
docker-compose -f docker-compose-infrastructure.yml down

print_status "Stopping full Docker stack (if running)..."
cd ../infrastructure
docker-compose down 2>/dev/null || print_warning "Full Docker stack not running"

# Clean up Docker containers (optional)
read -p "$(echo -e ${YELLOW}[QUESTION]${NC} Do you want to remove Docker volumes? This will delete all data (y/N): )" -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_status "Removing Docker volumes..."
    cd ../config
    docker-compose -f docker-compose-infrastructure.yml down -v
    cd ../infrastructure  
    docker-compose down -v 2>/dev/null || true
    print_success "Docker volumes removed"
else
    print_status "Docker volumes preserved"
fi

echo ""
print_success "🎉 NexHub Platform stopped successfully!"
echo ""
print_status "To restart the platform:"
echo "   • Development mode: ./scripts/deployment/start-services.sh"
echo "   • Full Docker mode: cd infrastructure && docker-compose up -d"