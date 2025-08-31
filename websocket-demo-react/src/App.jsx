import React, { useState, useEffect, useRef } from 'react'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import './App.css'

function App() {
  const [connected, setConnected] = useState(false)
  const [connecting, setConnecting] = useState(false)
  const [serverUrl, setServerUrl] = useState('http://localhost:8083/ws/notifications')
  const [logs, setLogs] = useState([])
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [token, setToken] = useState('')
  const [userRole, setUserRole] = useState('')
  const [loginForm, setLoginForm] = useState({
    username: '',
    password: '',
    userType: 'CUSTOMER'
  })
  const [loggingIn, setLoggingIn] = useState(false)
  const [messages, setMessages] = useState([])
  const [broadcastMessage, setBroadcastMessage] = useState('')
  const [privateMessage, setPrivateMessage] = useState('')
  const [targetUsername, setTargetUsername] = useState('')
  const [isValidCustomer, setIsValidCustomer] = useState(true)
  const [subscriptions, setSubscriptions] = useState([])
  
  const stompClientRef = useRef(null)
  const logContainerRef = useRef(null)

  const addLog = (message, type = 'info') => {
    const timestamp = new Date().toLocaleTimeString()
    const emoji = {
      'info': 'ℹ️',
      'success': '✅',
      'error': '❌',
      'debug': '🔍'
    }[type] || 'ℹ️'
    
    const newLog = {
      id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      message: `${emoji} [${timestamp}] ${message}`,
      type
    }
    
    setLogs(prevLogs => [...prevLogs, newLog])
  }

  const isCustomerUsername = (username) => {
    if (!username || username.trim().length === 0) return true // Allow empty for now
    
    const lower = username.toLowerCase().trim()
    
    return lower.includes('customer') ||
           lower.includes('user') ||
           lower.includes('client') ||
           lower.startsWith('cust') ||
           lower.match(/.*customer.*/) ||
           (!lower.includes('admin') && !lower.includes('dealer'))
  }

  const addMessage = (content, type, topic = null) => {
    const timestamp = new Date().toLocaleTimeString()
    const newMessage = {
      id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      content,
      type,
      topic,
      timestamp
    }
    
    setMessages(prevMessages => [...prevMessages, newMessage])
    addLog(`📨 Received ${type} message: ${content}`, 'success')
  }

  useEffect(() => {
    // Auto-scroll to bottom when new log is added
    if (logContainerRef.current) {
      logContainerRef.current.scrollTop = logContainerRef.current.scrollHeight
    }
  }, [logs])

  useEffect(() => {
    addLog('🚀 React WebSocket Demo loaded', 'info')
    addLog('📝 Login first, then connect to test WebSocket', 'info')
    addLog('⚡ Using pure WebSocket messaging (no REST APIs)', 'info')
    
    // Check for saved token and role
    const savedToken = localStorage.getItem('authToken')
    const savedRole = localStorage.getItem('userRole')
    if (savedToken) {
      setToken(savedToken)
      setUserRole(savedRole || 'CUSTOMER')
      setIsLoggedIn(true)
      addLog('🔑 Found saved token - ready to connect', 'success')
      addLog(`👤 Role: ${savedRole || 'CUSTOMER'}`, 'info')
    }
    
    // Cleanup on component unmount
    return () => {
      disconnect()
    }
  }, [])

  const login = async () => {
    if (!loginForm.username || !loginForm.password) {
      addLog('Please enter username and password!', 'error')
      return
    }

    setLoggingIn(true)
    addLog(`🔐 Logging in as: ${loginForm.username}`, 'info')

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: loginForm.username,
          password: loginForm.password,
          userType: loginForm.userType
        })
      })

      if (response.ok) {
        const apiResponse = await response.json()
        
        if (apiResponse.success && apiResponse.data) {
          const loginData = apiResponse.data
          const authToken = loginData.token
          
          if (authToken) {
            const userType = loginData.user?.userType || loginForm.userType
            setToken(authToken)
            setUserRole(userType)
            setIsLoggedIn(true)
            localStorage.setItem('authToken', authToken)
            localStorage.setItem('userRole', userType)
            addLog('✅ Login successful! Token saved.', 'success')
            addLog(`👤 Logged in as: ${loginForm.username} (${userType})`, 'success')
            addLog(`🎯 User ID: ${loginData.user?.id || 'N/A'}`, 'info')
            addLog(`🔑 Token type: ${loginData.tokenType || 'Bearer'}`, 'info')
          } else {
            addLog('❌ Login failed: No token received', 'error')
          }
        } else {
          addLog(`❌ Login failed: ${apiResponse.message || 'Invalid response format'}`, 'error')
        }
      } else {
        const errorResponse = await response.json().catch(() => ({}))
        const errorMessage = errorResponse.message || errorResponse.error || response.statusText
        addLog(`❌ Login failed: ${errorMessage}`, 'error')
        if (errorResponse.code) {
          addLog(`📋 Error code: ${errorResponse.code}`, 'error')
        }
      }
    } catch (error) {
      addLog(`❌ Login error: ${error.message}`, 'error')
    }

    setLoggingIn(false)
  }

  const logout = () => {
    setToken('')
    setUserRole('')
    setIsLoggedIn(false)
    localStorage.removeItem('authToken')
    localStorage.removeItem('userRole')
    setLoginForm({ username: '', password: '', userType: 'CUSTOMER' })
    addLog('👋 Logged out successfully', 'info')
    
    // Disconnect if connected
    if (connected) {
      disconnect()
    }
  }

  const connect = () => {
    if (!serverUrl.trim()) {
      addLog('Please enter a valid WebSocket URL!', 'error')
      return
    }

    if (!isLoggedIn || !token) {
      addLog('Please login first!', 'error')
      return
    }

    setConnecting(true)
    addLog(`🔄 Attempting to connect to: ${serverUrl}`, 'info')
    addLog(`🔑 Using authentication token`, 'info')
    
    try {
      // Create SockJS connection
      const socket = new SockJS(serverUrl)
      const stompClient = Stomp.over(socket)
      
      // Store reference
      stompClientRef.current = stompClient

      // Enable debugging
      stompClient.debug = (str) => {
        addLog(str, 'debug')
      }

      // Set heartbeat
      stompClient.heartbeat = { outgoing: 20000, incoming: 20000 }

      // Handle connection with JWT token
      const headers = {
        'Authorization': `Bearer ${token}`
      }
      
      addLog(`🔐 Connecting with JWT token...`, 'debug')
      
      stompClient.connect(
        headers, // Include JWT token in headers
        (frame) => {
          addLog('🎉 WebSocket connection established successfully!', 'success')
          addLog(`👤 Connected as: ${frame.headers['user-name'] || 'Anonymous User'}`, 'success')
          addLog(`🆔 Session ID: ${frame.headers['session'] || 'N/A'}`, 'info')
          addLog('✅ Ready to communicate via WebSocket!', 'success')
          
          // Subscribe to topics based on user type
          subscribeToTopics(stompClient)
          
          setConnected(true)
          setConnecting(false)
        },
        (error) => {
          addLog('💥 WebSocket connection failed!', 'error')
          addLog(`📋 Error details: ${error}`, 'error')
          if (error.headers) {
            addLog(`📋 Error headers: ${JSON.stringify(error.headers)}`, 'debug')
          }
          setConnected(false)
          setConnecting(false)
        }
      )

      // Handle SockJS events
      socket.onopen = () => {
        addLog('🔌 SockJS transport connection opened', 'info')
      }

      socket.onclose = (event) => {
        addLog(`🔌 SockJS connection closed - Code: ${event.code}`, 'info')
        if (event.reason) {
          addLog(`📋 Close reason: ${event.reason}`, 'info')
        }
        setConnected(false)
        setConnecting(false)
      }

      socket.onerror = (error) => {
        addLog(`💥 SockJS transport error: ${error}`, 'error')
      }
      
    } catch (error) {
      addLog(`💥 Failed to initialize connection: ${error.message}`, 'error')
      setConnected(false)
      setConnecting(false)
    }
  }

  const subscribeToTopics = (stompClient) => {
    const subs = []
    
    // Subscribe to broadcast notifications (all users can receive)
    const broadcastSub = stompClient.subscribe('/topic/notifications', (message) => {
      addMessage(message.body, 'Broadcast Notification', '/topic/notifications')
    })
    subs.push(broadcastSub)
    addLog('📡 Subscribed to broadcast notifications', 'success')
    
    // Subscribe to private messages for current user
    if (loginForm.username) {
      const privateSub = stompClient.subscribe(`/user/${loginForm.username}/queue/private`, (message) => {
        addMessage(message.body, 'Private Message', `/user/${loginForm.username}/queue/private`)
      })
      subs.push(privateSub)
      addLog(`📡 Subscribed to private messages for ${loginForm.username}`, 'success')
    }
    
    setSubscriptions(subs)
  }

  const disconnect = () => {
    // Unsubscribe from all topics
    subscriptions.forEach(sub => {
      if (sub && sub.unsubscribe) {
        sub.unsubscribe()
      }
    })
    setSubscriptions([])
    
    if (stompClientRef.current && stompClientRef.current.connected) {
      addLog('🔄 Disconnecting from WebSocket...', 'info')
      stompClientRef.current.disconnect(() => {
        addLog('👋 WebSocket disconnected successfully', 'success')
        setConnected(false)
        setConnecting(false)
      })
    } else {
      addLog('⚠️ No active connection to disconnect', 'info')
      setConnected(false)
      setConnecting(false)
    }
  }

  const sendBroadcast = () => {
    if (!broadcastMessage.trim()) {
      addLog('Please enter broadcast message!', 'error')
      return
    }

    if (!stompClientRef.current || !stompClientRef.current.connected) {
      addLog('❌ Not connected to WebSocket!', 'error')
      return
    }

    // Check if user is ADMIN
    if (userRole !== 'ADMIN') {
      addLog(`❌ Access denied: Only ADMIN can send broadcast messages (you are ${userRole})`, 'error')
      return
    }

    try {
      stompClientRef.current.send('/app/broadcast', {}, broadcastMessage)
      addLog('📢 Broadcast sent to all users via WebSocket', 'success')
      setBroadcastMessage('')
    } catch (error) {
      addLog(`❌ Error sending broadcast: ${error.message}`, 'error')
    }
  }

  const sendPrivateMessage = () => {
    if (!privateMessage.trim()) {
      addLog('Please enter private message!', 'error')
      return
    }
    if (!targetUsername.trim()) {
      addLog('Please enter target username!', 'error')
      return
    }

    if (!stompClientRef.current || !stompClientRef.current.connected) {
      addLog('❌ Not connected to WebSocket!', 'error')
      return
    }

    // Check if user is ADMIN
    if (userRole !== 'ADMIN') {
      addLog(`❌ Access denied: Only ADMIN can send private messages (you are ${userRole})`, 'error')
      return
    }

    // Check if target is valid customer
    if (!isCustomerUsername(targetUsername)) {
      addLog(`❌ Invalid target: '${targetUsername}' is not identified as a CUSTOMER username`, 'error')
      return
    }

    try {
      stompClientRef.current.send(`/app/private/${targetUsername}`, {}, privateMessage)
      addLog(`📧 Private message sent to CUSTOMER ${targetUsername} via WebSocket`, 'success')
      setPrivateMessage('')
      setTargetUsername('')
      setIsValidCustomer(true)
    } catch (error) {
      addLog(`❌ Error sending private message: ${error.message}`, 'error')
    }
  }

  const clearLogs = () => {
    setLogs([{
      id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      message: 'ℹ️ Log cleared - Ready for new connection test...',
      type: 'info'
    }])
  }

  const clearMessages = () => {
    setMessages([])
    addLog('🧹 Messages cleared', 'info')
  }

  const getStatusDisplay = () => {
    if (connecting) return { text: '🟡 Connecting...', className: 'status-connecting' }
    if (connected) return { text: '🟢 Connected Successfully', className: 'status-connected' }
    return { text: '🔴 Disconnected', className: 'status-disconnected' }
  }

  const status = getStatusDisplay()

  return (
    <div className="app">
      <div className="container">
        <h1>🔌 React WebSocket Direct Connection Demo</h1>
        
        {/* Login Section */}
        {!isLoggedIn ? (
          <div className="login-section">
            <h3>🔐 Authentication Required</h3>
            <div className="login-form">
              <div className="form-group">
                <label>Username:</label>
                <input
                  type="text"
                  value={loginForm.username}
                  onChange={(e) => setLoginForm(prev => ({ ...prev, username: e.target.value }))}
                  className="form-input"
                  placeholder="Enter username"
                  disabled={loggingIn}
                />
              </div>
              <div className="form-group">
                <label>Password:</label>
                <input
                  type="password"
                  value={loginForm.password}
                  onChange={(e) => setLoginForm(prev => ({ ...prev, password: e.target.value }))}
                  className="form-input"
                  placeholder="Enter password"
                  disabled={loggingIn}
                  onKeyPress={(e) => e.key === 'Enter' && login()}
                />
              </div>
              <div className="form-group">
                <label>User Type:</label>
                <select
                  value={loginForm.userType}
                  onChange={(e) => setLoginForm(prev => ({ ...prev, userType: e.target.value }))}
                  className="form-input"
                  disabled={loggingIn}
                >
                  <option value="CUSTOMER">👤 Customer</option>
                  <option value="DEALER">🏪 Dealer</option>
                  <option value="ADMIN">👑 Admin</option>
                </select>
              </div>
              <button 
                onClick={login}
                disabled={loggingIn || !loginForm.username || !loginForm.password}
                className="btn btn-login"
              >
                {loggingIn ? '🔄 Logging in...' : '🔐 Login'}
              </button>
            </div>
          </div>
        ) : (
          <>
            {/* User Info */}
            <div className="user-info">
              <span className="user-status">👤 Logged in as: <strong>{loginForm.username}</strong> ({userRole})</span>
              <button onClick={logout} className="btn btn-logout">
                🚪 Logout
              </button>
            </div>

            {/* WebSocket Section */}
            <div className="url-section">
              <label><strong>WebSocket Server URL:</strong></label>
              <input
                type="text"
                value={serverUrl}
                onChange={(e) => setServerUrl(e.target.value)}
                className="url-input"
                placeholder="Enter WebSocket server URL"
                disabled={connecting || connected}
              />
            </div>

            <div className={`status ${status.className}`}>
              {status.text}
            </div>

            <div className="controls">
              <button 
                onClick={connect} 
                disabled={connecting || connected}
                className="btn btn-connect"
              >
                🔌 Connect
              </button>
              <button 
                onClick={disconnect} 
                disabled={connecting || !connected}
                className="btn btn-disconnect"
              >
                🔌 Disconnect
              </button>
            </div>

            {/* Message Sending Section */}
            {connected && (
              <div className="messaging-section">
                <h3>📨 Send Messages</h3>
                
                {/* Broadcast to All - Only for ADMIN */}
                <div className="message-form">
                  <h4>📢 Broadcast to All Users (ADMIN only)</h4>
                  {userRole !== 'ADMIN' && (
                    <div style={{color: '#ff6b6b', marginBottom: '10px', fontSize: '14px'}}>
                      ⚠️ Only ADMIN users can send broadcast messages to all users
                    </div>
                  )}
                  <div className="form-group">
                    <input
                      type="text"
                      value={broadcastMessage}
                      onChange={(e) => setBroadcastMessage(e.target.value)}
                      className="form-input"
                      placeholder="Enter broadcast message for all users"
                      onKeyPress={(e) => e.key === 'Enter' && sendBroadcast()}
                      disabled={userRole !== 'ADMIN'}
                    />
                    <button 
                      onClick={sendBroadcast}
                      disabled={userRole !== 'ADMIN' || !broadcastMessage.trim()}
                      className="btn btn-send"
                    >
                      📢 Send Broadcast
                    </button>
                  </div>
                </div>

                {/* Private Message - Only for ADMIN */}
                <div className="message-form">
                  <h4>📧 Private Message (ADMIN → CUSTOMER only)</h4>
                  {userRole !== 'ADMIN' && (
                    <div style={{color: '#ff6b6b', marginBottom: '10px', fontSize: '14px'}}>
                      ⚠️ Only ADMIN users can send private messages to CUSTOMER users
                    </div>
                  )}
                  <div style={{color: '#4a90e2', marginBottom: '10px', fontSize: '12px'}}>
                    💡 Target username must be identifiable as CUSTOMER (e.g., 'customer', 'user123', 'client_name')
                  </div>
                  <div className="form-group">
                    <input
                      type="text"
                      value={targetUsername}
                      onChange={(e) => {
                        const value = e.target.value
                        setTargetUsername(value)
                        setIsValidCustomer(isCustomerUsername(value))
                      }}
                      className={`form-input ${!isValidCustomer && targetUsername.trim() ? 'invalid-input' : ''}`}
                      placeholder="CUSTOMER username (e.g., customer123, user456, client_name)"
                      disabled={userRole !== 'ADMIN'}
                    />
                    {!isValidCustomer && targetUsername.trim() && (
                      <div style={{color: '#ff6b6b', fontSize: '12px', marginTop: '4px'}}>
                        ⚠️ Username '{targetUsername}' might not be recognized as CUSTOMER
                      </div>
                    )}
                    <input
                      type="text"
                      value={privateMessage}
                      onChange={(e) => setPrivateMessage(e.target.value)}
                      className="form-input"
                      placeholder="Enter private message for CUSTOMER"
                      onKeyPress={(e) => e.key === 'Enter' && sendPrivateMessage()}
                      disabled={userRole !== 'ADMIN'}
                    />
                    <button 
                      onClick={sendPrivateMessage}
                      disabled={userRole !== 'ADMIN' || !privateMessage.trim() || !targetUsername.trim() || !isValidCustomer}
                      className="btn btn-send"
                    >
                      📧 Send Private Message
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* Received Messages Section */}
            {connected && (
              <div className="messages-section">
                <h3>📨 Received Messages</h3>
                <div className="messages-container">
                  {messages.length === 0 ? (
                    <div className="no-messages">No messages received yet...</div>
                  ) : (
                    messages.map(message => (
                      <div key={message.id} className={`message-item ${message.type.toLowerCase().replace(' ', '-')}`}>
                        <div className="message-header">
                          <span className="message-type">{message.type}</span>
                          <span className="message-time">{message.timestamp}</span>
                        </div>
                        <div className="message-content">{message.content}</div>
                        {message.topic && (
                          <div className="message-topic">Topic: {message.topic}</div>
                        )}
                      </div>
                    ))
                  )}
                </div>
                <div className="messages-controls">
                  <button onClick={clearMessages} className="btn btn-clear">
                    🧹 Clear Messages
                  </button>
                </div>
              </div>
            )}
          </>
        )}

        <div className="log-section">
          <h3>📋 Connection Log</h3>
          <div 
            ref={logContainerRef}
            className="log-container"
          >
            {logs.map(log => (
              <div key={log.id} className={`log-entry log-${log.type}`}>
                {log.message}
              </div>
            ))}
          </div>
          <div className="log-controls">
            <button onClick={clearLogs} className="btn btn-clear">
              🧹 Clear Log
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App
