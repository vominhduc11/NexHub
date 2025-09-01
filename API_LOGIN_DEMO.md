# 🔐 API Login Demo - Request & Response Data

## 📚 Overview
Tài liệu demo dữ liệu gửi và nhận của API login trong hệ thống NexHub.

---

## 🎯 Endpoint Information
- **URL**: `POST http://localhost:8080/api/auth/login`
- **Content-Type**: `application/json`
- **Purpose**: Authenticate user và trả về JWT token

---

## 📤 Request Examples

### 1. ADMIN Login
```json
{
  "username": "admin",
  "password": "admin123",
  "userType": "ADMIN"
}
```

### 2. CUSTOMER Login
```json
{
  "username": "customer",
  "password": "customer123",
  "userType": "CUSTOMER"
}
```

### 3. DEALER Login
```json
{
  "username": "dealer",
  "password": "dealer123",
  "userType": "DEALER"
}
```

---

## 📥 Response Examples

### ✅ Successful Login Response (BaseResponse Format)
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImF1dGgtc2VydmljZS1rZXkifQ.eyJzdWIiOiIxMjMiLCJ1c2VybmFtZSI6ImFkbWluIiwidXNlclR5cGUiOiJBRE1JTiIsInJvbGVzIjpbIkFETUlOIl0sInBlcm1pc3Npb25zIjpbIlJFQUQiLCJXUklURSIsIkRFTEVURSJdLCJpYXQiOjE2OTMzNjk2MDAsImV4cCI6MTY5MzQ1NjAwMH0.signature-here",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 123,
      "username": "admin",
      "userType": "ADMIN",
      "email": "admin@nexhub.com",
      "fullName": "System Administrator",
      "roles": ["ADMIN"],
      "permissions": ["READ", "WRITE", "DELETE"],
      "createdAt": "2024-01-01T00:00:00",
      "lastLogin": "2024-01-15T10:30:00"
    }
  },
  "error": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

### ✅ CUSTOMER Success Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImF1dGgtc2VydmljZS1rZXkifQ.eyJzdWIiOiI0NTYiLCJ1c2VybmFtZSI6ImN1c3RvbWVyIiwidXNlclR5cGUiOiJDVVNUT01FUiIsInJvbGVzIjpbIkNVU1RPTUVSIl0sInBlcm1pc3Npb25zIjpbIlJFQUQiXSwiaWF0IjoxNjkzMzY5NjAwLCJleHAiOjE2OTM0NTYwMDB9.signature-here",
    "tokenType": "Bearer", 
    "expiresIn": 86400,
    "user": {
      "id": 456,
      "username": "customer",
      "userType": "CUSTOMER", 
      "email": "customer@email.com",
      "fullName": "John Customer",
      "roles": ["CUSTOMER"],
      "permissions": ["READ"],
      "createdAt": "2024-01-10T00:00:00",
      "lastLogin": "2024-01-15T09:15:00"
    }
  },
  "error": null,
  "timestamp": "2024-01-15T09:15:00"
}
```

---

## ❌ Error Response Examples (BaseResponse Format)

### 1. Invalid Credentials  
```json
{
  "success": false,
  "message": "Invalid username or password",
  "data": null,
  "error": {
    "code": "AUTHENTICATION_FAILED",
    "details": null
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 2. Account Locked
```json
{
  "success": false,
  "message": "Account is locked due to multiple failed attempts",
  "data": null,
  "error": {
    "code": "ACCOUNT_LOCKED",
    "details": "Please contact administrator or try again later"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 3. User Type Mismatch
```json
{
  "success": false,
  "message": "User type does not match account type",
  "data": null,
  "error": {
    "code": "USER_TYPE_MISMATCH",
    "details": "Requested: ADMIN, Actual: CUSTOMER"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 4. Account Disabled
```json
{
  "success": false,
  "message": "Account has been disabled",
  "data": null,
  "error": {
    "code": "ACCOUNT_DISABLED",
    "details": "Please contact administrator to reactivate your account"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 5. Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "username": "Username is required",
      "password": "Password must be at least 6 characters",
      "userType": "User type must be one of: ADMIN, CUSTOMER, DEALER"
    }
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 6. Internal Server Error
```json
{
  "success": false,
  "message": "An unexpected error occurred",
  "data": null,
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "details": null
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🔑 JWT Token Structure

### Header
```json
{
  "alg": "RS256",
  "typ": "JWT", 
  "kid": "auth-service-key"
}
```

### Payload (Claims)
```json
{
  "sub": "123",                    // Account ID
  "username": "admin",             // Username  
  "userType": "ADMIN",             // User type
  "roles": ["ADMIN"],              // User roles
  "permissions": ["READ", "WRITE", "DELETE"], // Permissions
  "iat": 1693369600,              // Issued at
  "exp": 1693456000               // Expires at
}
```

---

## ✅ Request Validation Rules

### Username Validation
- **Required**: Yes
- **Type**: String
- **Format**: Any valid string

### Password Validation  
- **Required**: Yes
- **Type**: String
- **Format**: Any valid string

### UserType Validation
- **Required**: Yes
- **Type**: String (Enum)
- **Valid Values**: `"ADMIN"`, `"CUSTOMER"`, `"DEALER"`

---

## 📊 HTTP Status Codes

| Status | Code | Description |
|--------|------|-------------|
| ✅ Success | 200 | Login successful |
| ❌ Bad Request | 400 | Invalid request data |
| ❌ Unauthorized | 401 | Invalid credentials |
| ❌ Forbidden | 403 | Account disabled |
| ❌ Locked | 423 | Account locked |
| ❌ Server Error | 500 | Internal server error |

---

## 🚀 Testing with cURL

### ADMIN Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "userType": "ADMIN"
  }'
```

### CUSTOMER Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer", 
    "password": "customer123",
    "userType": "CUSTOMER"
  }'
```

---

## 📝 Notes

1. **Token Expiry**: JWT tokens expire after 24 hours (86400 seconds)
2. **Token Storage**: Store token securely (preferably httpOnly cookies)
3. **Token Usage**: Include token in Authorization header: `Bearer <token>`
4. **Refresh Logic**: Implement token refresh before expiration
5. **Security**: Never log or expose full JWT tokens in production

---

## 🔒 Security Best Practices

- ✅ Use HTTPS in production
- ✅ Implement rate limiting
- ✅ Hash passwords with bcrypt
- ✅ Validate input data
- ✅ Use secure JWT signing keys
- ✅ Implement logout functionality
- ✅ Monitor failed login attempts