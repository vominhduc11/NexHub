# 🔗 API WebSocket Demo - Real-time Notification System

## 📚 Overview
Tài liệu demo cho hệ thống WebSocket real-time notification trong NexHub. Hỗ trợ broadcast messages và private messaging với JWT authentication.

---

## 🎯 WebSocket Connection Information
- **WebSocket URL**: `ws://localhost:8081/ws`
- **Protocol**: STOMP over WebSocket  
- **Authentication**: JWT Bearer Token required
- **Supported User Types**: ADMIN, CUSTOMER, DEALER

---

## 🚀 Connection Setup

### JavaScript/React WebSocket Client
```javascript
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Get JWT token from localStorage
const token = localStorage.getItem('authToken');

// Create WebSocket connection
const socket = new SockJS('http://localhost:8081/ws');
const stompClient = Stomp.over(socket);

// Connection headers with JWT token
const headers = {
  'Authorization': `Bearer ${token}`
};

// Connect to WebSocket
stompClient.connect(headers, function (frame) {
  console.log('✅ Connected to WebSocket:', frame);
  
  // Subscribe to channels after successful connection
  subscribeToChannels();
}, function (error) {
  console.error('❌ WebSocket connection failed:', error);
});

function subscribeToChannels() {
  // Subscribe to broadcast notifications (all users)
  stompClient.subscribe('/topic/notifications', function (message) {
    const notification = JSON.parse(message.body);
    console.log('📢 Broadcast notification:', notification);
    displayBroadcastNotification(notification);
  }, headers);

  // Subscribe to private notifications (user-specific)
  stompClient.subscribe('/user/queue/private', function (message) {
    const notification = JSON.parse(message.body);
    console.log('📧 Private notification:', notification);
    displayPrivateNotification(notification);
  }, headers);

  // Subscribe to dealer registration notifications
  stompClient.subscribe('/topic/dealer-registrations', function (message) {
    const notification = JSON.parse(message.body);
    console.log('🏢 Dealer registration:', notification);
    displayDealerRegistration(notification);
  }, headers);
}
```

---

## 📡 WebSocket Endpoints

### 1. **Broadcast Message Endpoint**
**Send broadcast messages to all connected users (ADMIN only)**

#### Endpoint Details
- **Destination**: `/app/broadcast`
- **Method**: `@MessageMapping("/broadcast")`
- **Permission**: ADMIN only
- **Subscription**: All users receive via `/topic/notifications`

#### Send Message (ADMIN only)
```javascript
const headers = {
  'Authorization': `Bearer ${adminToken}`
};

const broadcastMessage = "System maintenance scheduled for tonight at 2 AM";

stompClient.send('/app/broadcast', headers, broadcastMessage);
```

#### Broadcast Message Format
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

#### Example Response Handler
```javascript
function displayBroadcastNotification(notification) {
  const messageDiv = document.createElement('div');
  messageDiv.className = 'broadcast-notification';
  messageDiv.innerHTML = `
    <div class="notification-header">📢 System Broadcast</div>
    <div class="notification-message">${notification.message}</div>
    <div class="notification-time">${new Date(notification.timestamp).toLocaleString()}</div>
  `;
  
  document.getElementById('notifications-container').appendChild(messageDiv);
  
  // Show toast notification
  showToast('System Broadcast', notification.message, 'info');
}
```

---

### 2. **Private Message Endpoint**
**Send private messages from ADMIN to specific CUSTOMER**

#### Endpoint Details
- **Destination**: `/app/private/{targetUser}`
- **Method**: `@MessageMapping("/private/{targetUser}")`
- **Permission**: ADMIN → CUSTOMER only
- **Subscription**: Target user receives via `/user/queue/private`

#### Send Private Message (ADMIN to CUSTOMER)
```javascript
const headers = {
  'Authorization': `Bearer ${adminToken}`
};

const targetCustomer = "customer_john";
const privateMessage = "Your warranty claim has been approved";

stompClient.send(`/app/private/${targetCustomer}`, headers, privateMessage);
```

#### Private Message Format
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

#### Example Response Handler
```javascript
function displayPrivateNotification(notification) {
  const messageDiv = document.createElement('div');
  messageDiv.className = 'private-notification';
  messageDiv.innerHTML = `
    <div class="notification-header">📧 Private Message</div>
    <div class="notification-message">${notification.message}</div>
    <div class="notification-time">${new Date(notification.timestamp).toLocaleString()}</div>
  `;
  
  document.getElementById('private-messages').appendChild(messageDiv);
  
  // Show toast notification
  showToast('Private Message', notification.message, 'success');
  
  // Update unread message count
  updateUnreadCount();
}
```

---

## 📥 Subscription Channels

### 1. **Broadcast Notifications** `/topic/notifications`
**All authenticated users can subscribe**

```javascript
stompClient.subscribe('/topic/notifications', function (message) {
  const notification = JSON.parse(message.body);
  // Handle broadcast notification
}, headers);
```

**Use Cases:**
- System maintenance announcements
- New product releases
- Policy updates
- Emergency notifications

---

### 2. **Private Messages** `/user/queue/private`
**User-specific private queue**

```javascript
stompClient.subscribe('/user/queue/private', function (message) {
  const notification = JSON.parse(message.body);
  // Handle private message
}, headers);
```

**Use Cases:**
- Warranty claim updates
- Order status changes
- Account notifications
- Customer support messages

---

### 3. **Dealer Registrations** `/topic/dealer-registrations`
**Business intelligence notifications**

```javascript
stompClient.subscribe('/topic/dealer-registrations', function (message) {
  const notification = JSON.parse(message.body);
  // Handle new dealer registration
}, headers);
```

**Message Format:**
```json
{
  "type": "DEALER_REGISTERED",
  "username": "new_dealer_001",
  "name": "ABC Company Ltd",
  "email": "contact@abccompany.com",
  "message": "New dealer registered: ABC Company Ltd (new_dealer_001)",
  "timestamp": 1705320600000
}
```

---

## 🔐 Security & Authorization

### JWT Token Requirements
```javascript
// Token must be included in all WebSocket operations
const headers = {
  'Authorization': `Bearer ${jwtToken}`
};

// For CONNECT frame
stompClient.connect(headers, onConnect, onError);

// For SEND operations  
stompClient.send('/app/broadcast', headers, message);

// For SUBSCRIBE operations
stompClient.subscribe('/topic/notifications', callback, headers);
```

### Permission Matrix
| User Type | Send Broadcast | Send Private | Receive Broadcast | Receive Private |
|-----------|---------------|--------------|-------------------|-----------------|
| **ADMIN** | ✅ Yes | ✅ Yes (to CUSTOMER) | ✅ Yes | ✅ Yes |
| **CUSTOMER** | ❌ No | ❌ No | ✅ Yes | ✅ Yes |
| **DEALER** | ❌ No | ❌ No | ✅ Yes | ✅ Yes |

### Authorization Errors
```json
{
  "error": "Access Denied",
  "message": "Only ADMIN can send broadcast messages",
  "code": "INSUFFICIENT_PERMISSIONS"
}
```

---

## 💻 Complete Frontend Example

### React WebSocket Hook
```javascript
import { useState, useEffect, useRef } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const useWebSocket = () => {
  const [connected, setConnected] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [privateMessages, setPrivateMessages] = useState([]);
  const stompClientRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      console.error('No auth token found');
      return;
    }

    const socket = new SockJS('http://localhost:8081/ws');
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;

    const headers = {
      'Authorization': `Bearer ${token}`
    };

    stompClient.connect(headers, 
      function (frame) {
        console.log('✅ WebSocket Connected:', frame);
        setConnected(true);

        // Subscribe to all channels
        stompClient.subscribe('/topic/notifications', (message) => {
          const notification = JSON.parse(message.body);
          setNotifications(prev => [...prev, notification]);
        }, headers);

        stompClient.subscribe('/user/queue/private', (message) => {
          const notification = JSON.parse(message.body);
          setPrivateMessages(prev => [...prev, notification]);
        }, headers);

        stompClient.subscribe('/topic/dealer-registrations', (message) => {
          const notification = JSON.parse(message.body);
          console.log('New dealer:', notification);
        }, headers);
      },
      function (error) {
        console.error('❌ WebSocket Connection Failed:', error);
        setConnected(false);
      }
    );

    return () => {
      if (stompClient.connected) {
        stompClient.disconnect();
      }
    };
  }, []);

  const sendBroadcast = (message) => {
    if (stompClientRef.current && connected) {
      const headers = {
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      };
      stompClientRef.current.send('/app/broadcast', headers, message);
    }
  };

  const sendPrivateMessage = (targetUser, message) => {
    if (stompClientRef.current && connected) {
      const headers = {
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      };
      stompClientRef.current.send(`/app/private/${targetUser}`, headers, message);
    }
  };

  return {
    connected,
    notifications,
    privateMessages,
    sendBroadcast,
    sendPrivateMessage
  };
};
```

### React Component Usage
```jsx
import React, { useState } from 'react';
import { useWebSocket } from './useWebSocket';

const NotificationCenter = () => {
  const { connected, notifications, privateMessages, sendBroadcast, sendPrivateMessage } = useWebSocket();
  const [broadcastText, setBroadcastText] = useState('');
  const [privateText, setPrivateText] = useState('');
  const [targetUser, setTargetUser] = useState('');

  const userRole = localStorage.getItem('userRole');

  return (
    <div className="notification-center">
      <div className="connection-status">
        Status: {connected ? '🟢 Connected' : '🔴 Disconnected'}
      </div>

      {/* Admin Controls */}
      {userRole === 'ADMIN' && (
        <div className="admin-controls">
          <h3>📢 Send Broadcast</h3>
          <textarea 
            value={broadcastText}
            onChange={(e) => setBroadcastText(e.target.value)}
            placeholder="Enter broadcast message..."
          />
          <button onClick={() => {
            sendBroadcast(broadcastText);
            setBroadcastText('');
          }}>
            Send Broadcast
          </button>

          <h3>📧 Send Private Message</h3>
          <input 
            type="text"
            value={targetUser}
            onChange={(e) => setTargetUser(e.target.value)}
            placeholder="Target customer username..."
          />
          <textarea 
            value={privateText}
            onChange={(e) => setPrivateText(e.target.value)}
            placeholder="Enter private message..."
          />
          <button onClick={() => {
            sendPrivateMessage(targetUser, privateText);
            setPrivateText('');
            setTargetUser('');
          }}>
            Send Private Message
          </button>
        </div>
      )}

      {/* Broadcast Notifications */}
      <div className="broadcast-section">
        <h3>📢 Broadcast Notifications</h3>
        {notifications.map((notification, index) => (
          <div key={index} className="notification broadcast">
            <div className="message">{notification.message}</div>
            <div className="timestamp">{new Date(notification.timestamp).toLocaleString()}</div>
          </div>
        ))}
      </div>

      {/* Private Messages */}
      <div className="private-section">
        <h3>📧 Private Messages</h3>
        {privateMessages.map((message, index) => (
          <div key={index} className="notification private">
            <div className="message">{message.message}</div>
            <div className="timestamp">{new Date(message.timestamp).toLocaleString()}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default NotificationCenter;
```

---

## 🔧 Testing with Browser Console

### Connect and Subscribe
```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8081/ws');
const stompClient = Stomp.over(socket);

const token = 'your-jwt-token-here';
const headers = { 'Authorization': `Bearer ${token}` };

stompClient.connect(headers, function(frame) {
  console.log('Connected:', frame);
  
  // Subscribe to notifications
  stompClient.subscribe('/topic/notifications', function(message) {
    console.log('Broadcast:', JSON.parse(message.body));
  }, headers);
  
  stompClient.subscribe('/user/queue/private', function(message) {
    console.log('Private:', JSON.parse(message.body));
  }, headers);
});
```

### Send Messages (ADMIN only)
```javascript
// Send broadcast
stompClient.send('/app/broadcast', headers, 'Test broadcast message');

// Send private message
stompClient.send('/app/private/customer_john', headers, 'Test private message');
```

---

## 📊 Message Flow Diagram

```
ADMIN Client                    WebSocket Server                    CUSTOMER Client
     |                               |                                    |
     |-- CONNECT (JWT) ------------->|                                    |
     |<-- CONNECTED ------------------|                                    |
     |                               |<-- CONNECT (JWT) -----------------|
     |                               |-- CONNECTED -------------------->|
     |                               |                                    |
     |-- SEND /app/broadcast ------->|                                    |
     |                               |-- /topic/notifications --------->| 
     |                               |                                    |
     |-- SEND /app/private/john ---->|                                    |
     |                               |-- /user/john/queue/private ------>|
```

---

## 🚨 Error Handling

### Connection Errors
```javascript
stompClient.connect(headers, onConnect, function(error) {
  console.error('Connection failed:', error);
  
  if (error.includes('Unauthorized')) {
    // Token expired or invalid
    refreshTokenAndReconnect();
  } else {
    // Server unavailable
    scheduleReconnect();
  }
});
```

### Authorization Errors
```javascript
// Handle insufficient permissions
stompClient.onStompError = function(frame) {
  console.error('STOMP Error:', frame);
  
  if (frame.headers.message.includes('Access Denied')) {
    alert('You do not have permission to perform this action');
  }
};
```

---

## 📝 Best Practices

1. **Token Management**
   - Always include JWT token in headers
   - Handle token expiration gracefully
   - Refresh token before it expires

2. **Connection Handling**
   - Implement reconnection logic
   - Handle network disconnections
   - Show connection status to users

3. **Message Processing**
   - Parse JSON messages safely
   - Validate message format
   - Handle malformed messages

4. **UI/UX**
   - Show real-time connection status
   - Display unread message counts
   - Implement notification sounds/vibrations

5. **Security**
   - Validate user permissions on frontend
   - Don't expose sensitive data in messages
   - Implement rate limiting for message sending

---

## 🔗 Integration with Other Services

### Warranty Service Integration
```javascript
// Listen for warranty updates
stompClient.subscribe('/user/queue/private', function(message) {
  const notification = JSON.parse(message.body);
  
  if (notification.message.includes('warranty')) {
    // Update warranty status in UI
    updateWarrantyStatus(notification);
  }
});
```

### Product Service Integration
```javascript
// Listen for product announcements
stompClient.subscribe('/topic/notifications', function(message) {
  const notification = JSON.parse(message.body);
  
  if (notification.message.includes('product')) {
    // Show product notification
    showProductAnnouncement(notification);
  }
});
```