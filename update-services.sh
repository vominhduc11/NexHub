#!/bin/bash

# List of services to update
SERVICES=("blog-service" "user-service" "product-service" "warranty-service" "notification-service")

echo "🔄 Updating all services to use nexhub-common..."

for service in "${SERVICES[@]}"
do
    echo "📦 Processing $service..."
    
    # Add dependency to pom.xml
    POM_FILE="$service/pom.xml"
    if [ -f "$POM_FILE" ]; then
        # Add nexhub-common dependency after <dependencies> tag
        sed -i '/<dependencies>/a\\t\t<!-- NexHub Common Library -->\n\t\t<dependency>\n\t\t\t<groupId>com.devwonder</groupId>\n\t\t\t<artifactId>nexhub-common</artifactId>\n\t\t\t<version>1.0.0</version>\n\t\t</dependency>\n' "$POM_FILE"
        echo "   ✅ Updated pom.xml"
    fi
    
    # Remove old BaseResponse and ApiError files
    BASE_RESPONSE_FILE="$service/src/main/java/com/devwonder/${service//-/_}/dto/BaseResponse.java"
    API_ERROR_FILE="$service/src/main/java/com/devwonder/${service//-/_}/dto/ApiError.java"
    
    if [ -f "$BASE_RESPONSE_FILE" ]; then
        rm "$BASE_RESPONSE_FILE"
        echo "   🗑️  Removed old BaseResponse.java"
    fi
    
    if [ -f "$API_ERROR_FILE" ]; then
        rm "$API_ERROR_FILE"
        echo "   🗑️  Removed old ApiError.java"
    fi
    
    # Update import statements
    find "$service/src/main/java" -name "*.java" -exec sed -i "s|import com.devwonder.${service//-/_}.dto.BaseResponse|import com.devwonder.common.dto.BaseResponse|g" {} \;
    find "$service/src/main/java" -name "*.java" -exec sed -i "s|import com.devwonder.${service//-/_}.dto.ApiError|import com.devwonder.common.dto.ApiError|g" {} \;
    echo "   🔧 Updated import statements"
    
    echo "   ✅ $service updated successfully!"
    echo
done

echo "🎉 All services updated to use nexhub-common!"