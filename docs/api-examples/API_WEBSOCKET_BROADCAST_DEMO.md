# 📢 WebSocket Broadcast API Demo

## 📚 Overview
Tài liệu demo cho WebSocket broadcast endpoint - cho phép ADMIN gửi thông báo đến tất cả users trong hệ thống NexHub.

---

## 🎯 Endpoint Information
- **Destination**: `/app/broadcast`
- **Method**: `@MessageMapping("/broadcast")`
- **Protocol**: STOMP over WebSocket
- **WebSocket URL**: `ws://localhost:8081/ws`
- **Permission**: ADMIN only
- **Subscription Channel**: `/topic/notifications`

---

## ✅ Connection Requirements

### Authentication
- **Required**: JWT Bearer Token
- **User Role**: ADMIN only
- **Header**: `Authorization: Bearer {jwt-token}`

### WebSocket Protocol
- **Protocol**: STOMP over WebSocket
- **Connection**: `ws://localhost:8081/ws`
```

---

## 📤 Send Broadcast Message

### Request Format
- **Destination**: `/app/broadcast`
- **Headers**: `Authorization: Bearer {admin-jwt-token}`
- **Body**: Plain text message (String)

### Request Example
```
Destination: /app/broadcast
Headers: {
  "Authorization": "Bearer eyJhbGciOiJSUzI1NiIs..."
}
Body: "System maintenance scheduled for tonight at 2 AM"
```

---

## 📥 Response Format

### Broadcast Notification (Received by all users via `/topic/notifications`)
```json
{
  "type": "BROADCAST_NOTIFICATION",
  "username": "system",
  "name": "System", 
  "email": "",
  "message": "[Broadcast from ADMIN admin_user]: System maintenance scheduled for tonight at 2 AM",
  "timestamp": 1705320600000
}
```

### Response Fields
- **type**: Always "BROADCAST_NOTIFICATION"
- **username**: "system" (fixed value)
- **name**: "System" (fixed value)  
- **email**: Empty string
- **message**: Formatted message with sender info
- **timestamp**: Unix timestamp in milliseconds

---

## 🔐 Security & Authorization

### Permission Requirements
- **Send Permission**: ADMIN role only
- **Receive Permission**: All authenticated users (ADMIN, CUSTOMER, DEALER)

### JWT Token Validation
```javascript
// Token must be valid ADMIN token
const headers = {
  'Authorization': `Bearer ${adminToken}`
};

// Token validation happens on server side
// Invalid token = connection refused
// Non-ADMIN token = send operation blocked
```

### Error Handling
```javascript
stompClient.onStompError = function(frame) {
  console.error('STOMP Error:', frame);
  
  if (frame.headers.message?.includes('Access Denied')) {
    alert('Error: Only ADMIN users can send broadcast messages');
  } else if (frame.headers.message?.includes('Unauthorized')) {
    alert('Error: Please login as ADMIN first');
    redirectToLogin();
  }
};
```

---

## ✅ Message Validation

### Input Requirements
- **Type**: String (plain text)
- **Length**: No specific limit (recommend max 1000 characters)
- **Content**: Any text message
- **Special Characters**: Allowed
- **Empty Message**: Not allowed (client-side validation recommended)

---

## 📱 Use Cases

### 1. System Maintenance Announcements
```javascript
sendBroadcastMessage("⚠️ System maintenance scheduled for tonight 2-4 AM. Services will be temporarily unavailable.");
```

### 2. New Product Announcements  
```javascript
sendBroadcastMessage("🚀 New iPhone 15 Pro Max now available! Check out our latest deals in the product catalog.");
```

### 3. Policy Updates
```javascript
sendBroadcastMessage("📋 Updated warranty policy effective from Jan 1, 2024. Please review the new terms in your dashboard.");
```

### 4. Emergency Notifications
```javascript
sendBroadcastMessage("🚨 Security update: Please change your passwords immediately. Click here for instructions.");
```

### 5. Holiday Announcements
```javascript
sendBroadcastMessage("🎄 Happy Holidays! Our customer service hours will be limited Dec 24-26. Emergency support available 24/7.");
```

---

## 🚀 Testing with cURL

### Not Applicable
WebSocket endpoints cannot be tested with cURL as they require persistent WebSocket connections. Use WebSocket testing tools or browser-based testing instead.

---

## 📊 Message Flow

```
ADMIN Client              WebSocket Server              All Connected Users
     |                         |                              |
     |-- SEND /app/broadcast -->|                              |
     |   + JWT Token           |                              |
     |   + Message Text        |                              |
     |                         |                              |
     |                         |-- /topic/notifications ---->|
     |                         |   (Formatted notification)  |
```

---

## 🚨 Error Responses

### 1. Unauthorized User (Non-ADMIN)
```json
{
  "error": "Access Denied",
  "message": "Only ADMIN can send broadcast messages",
  "code": "INSUFFICIENT_PERMISSIONS"
}
```

### 2. Invalid JWT Token
```json
{
  "error": "Authentication Failed", 
  "message": "Invalid or expired JWT token",
  "code": "UNAUTHORIZED"
}
```

### 3. Missing Authorization Header
```json
{
  "error": "Missing Authorization",
  "message": "Authorization header required",
  "code": "MISSING_AUTH"
}
```

---

## 📈 Performance Notes

### Message Size
- Keep messages concise (recommended max: 1000 characters)
- Large messages may impact WebSocket performance

### Frequency Limits
- Avoid sending broadcasts too frequently
- Recommended: Max 1 broadcast per minute
- Server-side rate limiting recommended

---

## 📝 Best Practices

### Message Content
- Keep messages clear and actionable
- Include important information only
- Use appropriate formatting

### Security
- Always validate ADMIN role on server
- Log all broadcast activities
- Implement rate limiting

### Performance
- Limit message frequency
- Keep message size reasonable
- Monitor WebSocket connection status