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
  const [loginForm, setLoginForm] = useState({
    username: '',
    password: '',
    userType: 'CUSTOMER'
  })
  const [loggingIn, setLoggingIn] = useState(false)
  
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

  useEffect(() => {
    // Auto-scroll to bottom when new log is added
    if (logContainerRef.current) {
      logContainerRef.current.scrollTop = logContainerRef.current.scrollHeight
    }
  }, [logs])

  useEffect(() => {
    addLog('🚀 React WebSocket Demo loaded', 'info')
    addLog('📝 Login first, then connect to test WebSocket', 'info')
    
    // Check for saved token
    const savedToken = localStorage.getItem('authToken')
    if (savedToken) {
      setToken(savedToken)
      setIsLoggedIn(true)
      addLog('🔑 Found saved token - ready to connect', 'success')
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
            setToken(authToken)
            setIsLoggedIn(true)
            localStorage.setItem('authToken', authToken)
            addLog('✅ Login successful! Token saved.', 'success')
            addLog(`👤 Logged in as: ${loginForm.username} (${loginData.user?.userType || loginForm.userType})`, 'success')
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
    setIsLoggedIn(false)
    localStorage.removeItem('authToken')
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

  const disconnect = () => {
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

  const clearLogs = () => {
    setLogs([{
      id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      message: 'ℹ️ Log cleared - Ready for new connection test...',
      type: 'info'
    }])
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
        <h1>🔌 React WebSocket Connection Demo</h1>
        
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
              <span className="user-status">👤 Logged in as: <strong>{loginForm.username}</strong></span>
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
