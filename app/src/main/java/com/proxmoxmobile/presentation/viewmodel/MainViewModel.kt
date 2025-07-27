package com.proxmoxmobile.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proxmoxmobile.data.api.AuthenticationService
import com.proxmoxmobile.data.api.ProxmoxApiClient
import com.proxmoxmobile.data.api.ProxmoxApiService
import com.proxmoxmobile.data.model.LoginResponse
import com.proxmoxmobile.data.model.ServerConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences

class MainViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        private const val PREFS_NAME = "ProxmoxPrefs"
        private const val KEY_HOST = "host"
        private const val KEY_PORT = "port"
        private const val KEY_USERNAME = "username"
        private const val KEY_REALM = "realm"
        private const val KEY_USE_HTTPS = "use_https"
        private const val KEY_SAVE_CREDENTIALS = "save_credentials"
    }

    private val authenticationService = AuthenticationService()
    private val apiClient = ProxmoxApiClient()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentServer = MutableStateFlow<ServerConfig?>(null)
    val currentServer: StateFlow<ServerConfig?> = _currentServer.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _csrfToken = MutableStateFlow<String?>(null)
    val csrfToken: StateFlow<String?> = _csrfToken.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var sharedPreferences: SharedPreferences? = null

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCredentials(serverConfig: ServerConfig, saveCredentials: Boolean) {
        sharedPreferences?.let { prefs ->
            prefs.edit().apply {
                putString(KEY_HOST, serverConfig.host)
                putInt(KEY_PORT, serverConfig.port)
                putString(KEY_USERNAME, serverConfig.username)
                putString(KEY_REALM, serverConfig.realm)
                putBoolean(KEY_USE_HTTPS, serverConfig.useHttps)
                putBoolean(KEY_SAVE_CREDENTIALS, saveCredentials)
                apply()
            }
        }
    }

    fun loadSavedCredentials(): ServerConfig? {
        sharedPreferences?.let { prefs ->
            val saveCredentials = prefs.getBoolean(KEY_SAVE_CREDENTIALS, false)
            if (saveCredentials) {
                val host = prefs.getString(KEY_HOST, "")
                val port = prefs.getInt(KEY_PORT, 8006)
                val username = prefs.getString(KEY_USERNAME, "")
                val realm = prefs.getString(KEY_REALM, "pam")
                val useHttps = prefs.getBoolean(KEY_USE_HTTPS, true)
                
                if (host?.isNotBlank() == true && username?.isNotBlank() == true) {
                    return ServerConfig(
                        host = host,
                        port = port,
                        username = username,
                        password = "", // Don't save password for security
                        realm = realm ?: "pam",
                        useHttps = useHttps
                    )
                }
            }
        }
        return null
    }

    fun clearSavedCredentials() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

    fun hasSavedCredentials(): Boolean {
        return sharedPreferences?.getBoolean(KEY_SAVE_CREDENTIALS, false) == true
    }

    fun authenticate(serverConfig: ServerConfig) {
        Log.d(TAG, "Starting authentication process")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Log.d(TAG, "Calling authentication service")
                val result = authenticationService.authenticate(serverConfig)
                result.fold(
                    onSuccess = { loginResponse ->
                        Log.d(TAG, "Authentication successful, setting tokens")
                        _authToken.value = loginResponse.data.ticket
                        _csrfToken.value = loginResponse.data.csrfToken
                        _currentServer.value = serverConfig
                        _isAuthenticated.value = true
                        _errorMessage.value = "✅ Authentication successful!"
                        Log.d(TAG, "Authentication completed successfully")
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Authentication failed", exception)
                        _errorMessage.value = "❌ ${exception.message}"
                        _isAuthenticated.value = false
                        _authToken.value = null
                        _csrfToken.value = null
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Network error during authentication", e)
                _errorMessage.value = "❌ Network error: ${e.message}"
                _isAuthenticated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getApiService(): ProxmoxApiService? {
        try {
            val server = _currentServer.value ?: return null
            val token = _authToken.value
            val csrf = _csrfToken.value
            
            Log.d(TAG, "Creating API service for server: ${server.host}")
            Log.d(TAG, "Auth token available: ${!token.isNullOrBlank()}")
            Log.d(TAG, "CSRF token available: ${!csrf.isNullOrBlank()}")
            
            if (server.host.isBlank()) {
                Log.e(TAG, "Server host is blank")
                return null
            }
            
            if (token.isNullOrBlank()) {
                Log.e(TAG, "Auth token is null or blank")
                return null
            }
            
            return apiClient.createApiService(server, token, csrf)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating API service", e)
            return null
        }
    }

    fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated.value = authenticated
    }

    fun setCurrentServer(server: ServerConfig?) {
        _currentServer.value = server
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun logout() {
        Log.d(TAG, "Logging out user")
        viewModelScope.launch {
            _isAuthenticated.value = false
            _currentServer.value = null
            _authToken.value = null
            _csrfToken.value = null
            _errorMessage.value = null
        }
    }
} 