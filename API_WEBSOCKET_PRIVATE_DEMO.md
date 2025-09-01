# 📧 WebSocket Private Message API Demo

## 📚 Overview
Tài liệu demo cho WebSocket private messaging endpoint - cho phép ADMIN gửi tin nhắn riêng tư đến specific CUSTOMER trong hệ thống NexHub.

---

## 🎯 Endpoint Information
- **Destination**: `/app/private/{targetUser}`
- **Method**: `@MessageMapping("/private/{targetUser}")`
- **Protocol**: STOMP over WebSocket
- **WebSocket URL**: `ws://localhost:8081/ws`
- **Permission**: ADMIN → CUSTOMER only
- **Subscription Channel**: `/user/queue/private` (user-specific)

---

## ✅ Connection Requirements

### Authentication
- **Required**: JWT Bearer Token
- **Send Permission**: ADMIN only
- **Receive Permission**: All authenticated users
- **Header**: `Authorization: Bearer {jwt-token}`

### WebSocket Protocol
- **Protocol**: STOMP over WebSocket
- **Connection**: `ws://localhost:8081/ws`
- **Subscription Channel**: `/user/queue/private`
```

---

## 📤 Send Private Message (ADMIN Only)

### Request Format
- **Destination**: `/app/private/{targetUser}`
- **Headers**: `Authorization: Bearer {admin-jwt-token}`
- **Path Parameter**: `{targetUser}` - Target customer username
- **Body**: Plain text message (String)

### Request Example
```
Destination: /app/private/customer_john
Headers: {
  "Authorization": "Bearer eyJhbGciOiJSUzI1NiIs..."
}
Body: "Your warranty claim has been approved"
```

### Path Parameter Validation
- **targetUser**: Must be valid customer username
- **Format**: String (customer username)
- **Required**: Yes
```

---

## 📥 Response Format

### Private Message (Received by target customer via `/user/queue/private`)
```json
{
  "type": "PRIVATE_MESSAGE",
  "username": "customer_john",
  "name": "",
  "email": "",
  "message": "[Private from ADMIN admin_user]: Your warranty claim has been approved",
  "timestamp": 1705320600000
}
```

### Response Fields
- **type**: Always "PRIVATE_MESSAGE"
- **username**: Target customer username
- **name**: Empty string (not used)
- **email**: Empty string (not used)
- **message**: Formatted message with sender info
- **timestamp**: Unix timestamp in milliseconds

---

## 🔐 Security & Authorization

### Permission Matrix
| Sender | Target | Permission |
|--------|--------|------------|
| ADMIN | CUSTOMER | ✅ Allowed |
| ADMIN | DEALER | ❌ Not implemented |
| ADMIN | ADMIN | ❌ Not needed |
| CUSTOMER | Any | ❌ Forbidden |
| DEALER | Any | ❌ Forbidden |

### Authorization Requirements
- **Send**: ADMIN role required
- **Receive**: Any authenticated user (receives own messages)
- **Token**: Valid JWT with appropriate role

---

## ✅ Message Validation

### Input Requirements
- **Message**: String (plain text)
- **Length**: No specific limit (recommend max 1000 characters)
- **Content**: Any text message
- **Special Characters**: Allowed
- **Empty Message**: Not allowed

### Target User Validation
- **Required**: Yes
- **Format**: Valid customer username
- **Type**: String
- **Target Role**: Must be CUSTOMER user

---

## 📱 Use Cases

### 1. Warranty Claim Updates
```javascript
sendPrivateMessage('customer_john', '✅ Your warranty claim #WC-2024-001 has been approved. Replacement part will be shipped within 2 business days.');
```

### 2. Order Status Notifications
```javascript
sendPrivateMessage('customer_jane', '📦 Your order #ORD-12345 has been shipped via FedEx. Tracking number: 1234567890123456');
```

### 3. Account Security Alerts
```javascript
sendPrivateMessage('customer_mike', '🔐 New login detected from IP 192.168.1.100. If this wasn\'t you, please contact support immediately.');
```

### 4. Payment Confirmations
```javascript
sendPrivateMessage('customer_sarah', '💳 Payment of $299.99 for Invoice #INV-789 has been processed successfully. Thank you for your business!');
```

### 5. Support Follow-ups
```javascript
sendPrivateMessage('customer_david', '🎧 Our support team has resolved your technical issue. Please reply if you need any further assistance.');
```

---

## 🚀 Testing with cURL

### Not Applicable
WebSocket endpoints cannot be tested with cURL as they require persistent WebSocket connections. Use WebSocket testing tools or browser-based testing instead.

---

## 📊 Message Flow

```
ADMIN Client              WebSocket Server              Target CUSTOMER
     |                         |                              |
     |-- SEND /app/private/ -->|                              |
     |   /customer_john        |                              |
     |   + JWT Token           |                              |
     |   + Message Text        |                              |
     |                         |                              |
     |                         |-- /user/customer_john    -->|
     |                         |   /queue/private             |
     |                         |   (Targeted delivery)        |
```

---

## 🚨 Error Responses

### 1. Unauthorized User (Non-ADMIN)
```json
{
  "error": "Access Denied",
  "message": "Only ADMIN can send private messages",
  "code": "INSUFFICIENT_PERMISSIONS"
}
```

### 2. Invalid Target User
```json
{
  "error": "Invalid Target",
  "message": "Target user not found or invalid",
  "code": "INVALID_TARGET"
}
```

### 3. Missing Authorization
```json
{
  "error": "Missing Authorization",
  "message": "Authorization header required",
  "code": "MISSING_AUTH"
}
```

### 4. Invalid JWT Token
```json
{
  "error": "Authentication Failed",
  "message": "Invalid or expired JWT token",
  "code": "UNAUTHORIZED"
}
```

---

## 📈 Performance Notes

### Message Delivery
- Messages are delivered in real-time to online users
- Offline users may not receive messages (depends on implementation)
- Message queuing for offline delivery is implementation-specific

### Message Size
- Keep messages concise (recommended max: 1000 characters)
- Large messages may impact WebSocket performance

---

## 📝 Best Practices

### Message Content
- Be clear and specific
- Include action items when needed
- Use appropriate tone for customer communication

### Security
- Always validate ADMIN role on server
- Validate target user exists
- Log all private message activities
- Implement rate limiting per user

### Performance
- Keep message size reasonable
- Implement message queuing for offline users
- Monitor WebSocket connection status
- Clean up old messages periodically