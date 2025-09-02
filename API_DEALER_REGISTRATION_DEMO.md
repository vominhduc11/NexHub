# 🏢 API Dealer Registration Demo - Request & Response Data

## 📚 Overview
Tài liệu demo dữ liệu gửi và nhận của API đăng ký làm đại lý trong hệ thống NexHub.

---

## 🎯 Endpoint Information
- **URL**: `POST http://localhost:8080/api/auth/reseller/register`
- **Content-Type**: `application/json`
- **Purpose**: Đăng ký tài khoản đại lý mới với quyền DEALER
- **Authentication**: No authentication required (public endpoint)

---

## 📤 Request Example

### Dealer Registration Request
```json
{
  "username": "dealervn001",
  "password": "securePassword123",
  "name": "4thitek",
  "address": "123 Đường ABC, Phường XYZ",
  "phone": "0901234567",
  "email": "dealer@company.com",
  "district": "Quận 1",
  "city": "Hồ Chí Minh"
}
```

### Required Fields
- **username**: 5-20 characters
- **password**: At least 8 characters
- **name**: Company name (cannot be empty)
- **address**: Address (cannot be empty)
- **phone**: 10 digits, must start with 0
- **email**: Valid email format (cannot be empty)
- **district**: District (cannot be empty)
- **city**: City (cannot be empty)

---

## 📥 Response Examples

### ✅ Successful Registration Response (BaseResponse Format)
```json
{
  "success": true,
  "message": "Reseller account created successfully",
  "data": {
    "id": 789,
    "username": "dealer_vn001",
    "message": "Your dealer account has been created successfully. You can now login with DEALER role."
  },
  "error": null,
  "timestamp": "2024-01-15T11:30:00"
}
```

---

## ❌ Error Response Examples (BaseResponse Format)

### 1. Username Already Exists
```json
{
  "success": false,
  "message": "Username already exists in the system",
  "data": null,
  "error": {
    "code": "USERNAME_EXISTS",
    "details": null
  },
  "timestamp": "2024-01-15T11:30:00"
}
```

### 2. Validation Errors
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "username": "Username must be between 6 and 50 characters",
      "password": "Password must be between 6 and 100 characters",
      "name": "Full name is required",
      "email": "Invalid email format",
      "phone": "Phone number format is invalid"
    }
  },
  "timestamp": "2024-01-15T11:30:00"
}
```

### 3. Invalid Username Format
```json
{
  "success": false,
  "message": "Invalid input data",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "username": "Username must start with a letter and contain both letters and numbers"
    }
  },
  "timestamp": "2024-01-15T11:30:00"
}
```

### 4. Role Configuration Error
```json
{
  "success": false,
  "message": "DEALER role not found in system",
  "data": null,
  "error": {
    "code": "ROLE_NOT_FOUND",
    "details": null
  },
  "timestamp": "2024-01-15T11:30:00"
}
```

### 5. Internal Server Error
```json
{
  "success": false,
  "message": "An unexpected error occurred",
  "data": null,
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "details": null
  },
  "timestamp": "2024-01-15T11:30:00"
}
```

---

## ✅ Request Validation Rules

### Username Validation
- **Required**: Yes
- **Type**: String
- **Length**: 5-20 characters
- **Message**: "Username không được để trống", "Username phải từ 5-20 ký tự"

### Password Validation
- **Required**: Yes
- **Type**: String
- **Length**: At least 8 characters
- **Message**: "Password không được để trống", "Password phải có ít nhất 8 ký tự"

### Name Validation (Company Name)
- **Required**: Yes
- **Type**: String
- **Message**: "Tên công ty không được để trống"

### Address Validation
- **Required**: Yes
- **Type**: String
- **Message**: "Địa chỉ không được để trống"

### Phone Validation
- **Required**: Yes
- **Type**: String
- **Pattern**: Must start with 0 and have exactly 10 digits
- **Regex**: `^(0[0-9]{9})$`
- **Message**: "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và đủ 10 số)"

### Email Validation
- **Required**: Yes
- **Type**: String (Email format)
- **Format**: Valid email format (example@domain.com)
- **Message**: "Email không được để trống", "Email không hợp lệ"

### District Validation
- **Required**: Yes
- **Type**: String
- **Message**: "Quận không được để trống"

### City Validation
- **Required**: Yes
- **Type**: String
- **Message**: "Thành phố không được để trống"

---

## 📊 HTTP Status Codes

| Status | Code | Description |
|--------|------|-------------|
| ✅ Created | 201 | Dealer account created successfully |
| ❌ Bad Request | 400 | Invalid input data or validation errors |
| ❌ Conflict | 409 | Username already exists |
| ❌ Server Error | 500 | Internal server error or role configuration issue |

---

## 🚀 Testing with cURL

### Successful Registration
```bash
curl -X POST http://localhost:8080/api/auth/reseller/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dealer_vn001",
    "password": "securePassword123",
    "name": "Nguyễn Văn A",
    "address": "123 Đường ABC, Phường XYZ",
    "phone": "0901234567",
    "email": "dealer@company.com",
    "district": "Quận 1",
    "city": "Hồ Chí Minh"
  }'
```

### Minimal Required Fields
```bash
curl -X POST http://localhost:8080/api/auth/reseller/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dealer_minimal",
    "password": "password123",
    "name": "Test Dealer",
    "email": "test@dealer.com"
  }'
```

---

## 🔄 Registration Flow

1. **Submit Registration Form**
   - User fills dealer registration form
   - Frontend validates required fields
   - Send POST request to registration endpoint

2. **Server Processing**
   - Validate input data format
   - Check username uniqueness
   - Create account with DEALER role
   - Create dealer profile record

3. **Success Response**
   - Return dealer account details
   - User redirected to login page
   - Can login immediately with DEALER role

4. **Error Handling**
   - Display validation errors
   - Show username conflict message
   - Handle server errors gracefully

---

## 🔐 After Registration

### Next Steps for New Dealers:
1. **Login**: Use the registered username/password with `userType: "DEALER"`
2. **Profile Setup**: Complete additional business information
3. **Product Access**: Browse and manage product catalog
4. **Order Management**: Handle customer orders and warranties
5. **Dashboard Access**: Access dealer-specific features

### Login Request After Registration:
```json
{
  "username": "dealer_vn001",
  "password": "securePassword123",
  "userType": "DEALER"
}
```

---

## 📝 Business Rules

1. **Username Requirements**:
   - Must be unique across all user types (ADMIN, CUSTOMER, DEALER)
   - 6-50 characters
   - Must start with letter
   - Can contain letters, numbers, underscores

2. **Password Requirements**:
   - 6-100 characters
   - Stored securely with bcrypt hashing

3. **Email Requirements**:
   - Must be valid email format
   - Used for notifications and communications

4. **Phone Format**:
   - Flexible format allowing international numbers
   - Supports parentheses, spaces, hyphens, plus sign

5. **Auto-Role Assignment**:
   - All registered dealers automatically get DEALER role
   - Can access dealer-specific features after login

---

## 🔒 Security Features

- ✅ Input validation and sanitization
- ✅ Password hashing with bcrypt
- ✅ Username uniqueness validation
- ✅ Email format validation
- ✅ SQL injection protection
- ✅ Rate limiting (recommended)
- ✅ HTTPS in production (recommended)

---

## 🆘 Common Issues & Solutions

### Issue: Username Already Exists
**Solution**: Choose a different username or contact support if you believe this is an error.

### Issue: Invalid Email Format
**Solution**: Ensure email follows format: `username@domain.com`

### Issue: Password Too Short
**Solution**: Use password with at least 6 characters.

### Issue: Phone Number Format Error
**Solution**: Use format like: `0901234567`, `(84) 901-234-567`, `+84-901-234-567`

### Issue: Role Not Found Error
**Solution**: Contact system administrator - this indicates system configuration issue.