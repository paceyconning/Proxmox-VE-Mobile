package com.proxmoxmobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proxmoxmobile.data.api.AuthenticationService
import com.proxmoxmobile.data.api.ProxmoxApiClient
import com.proxmoxmobile.data.api.ProxmoxApiService
import com.proxmoxmobile.data.model.LoginResponse
import com.proxmoxmobile.data.model.ServerConfig
import com.proxmoxmobile.data.security.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import android.content.Context
import com.proxmoxmobile.data.model.Node

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    // In-memory cache for nodes
    private var cachedNodes: List<Node>? = null
    fun getCachedNodes(): List<Node>? = cachedNodes
    fun setCachedNodes(nodes: List<Node>) { cachedNodes = nodes }

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

    private val _showConfirmationDialog = MutableStateFlow<ConfirmationDialog?>(null)
    val showConfirmationDialog: StateFlow<ConfirmationDialog?> = _showConfirmationDialog.asStateFlow()

    private var secureStorage: SecureStorage? = null

    data class ConfirmationDialog(
        val title: String,
        val message: String,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit = {}
    )

    fun showConfirmationDialog(dialog: ConfirmationDialog) {
        _showConfirmationDialog.value = dialog
    }

    fun hideConfirmationDialog() {
        _showConfirmationDialog.value = null
    }

    fun initialize(context: Context) {
        secureStorage = SecureStorage(context)
    }

    fun saveCredentials(serverConfig: ServerConfig, password: String, saveCredentials: Boolean) {
        secureStorage?.saveCredentials(
            host = serverConfig.host,
            port = serverConfig.port,
            username = serverConfig.username,
            password = password,
            realm = serverConfig.realm,
            useHttps = serverConfig.useHttps,
            saveCredentials = saveCredentials
        )
    }

    fun loadSavedCredentials(): SecureStorage.SavedCredentials? {
        return secureStorage?.loadSavedCredentials()
    }

    fun clearSavedCredentials() {
        secureStorage?.clearSavedCredentials()
    }

    fun hasSavedCredentials(): Boolean {
        return secureStorage?.hasSavedCredentials() ?: false
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

    // Container Actions
    fun startContainer(node: String, vmid: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.performContainerAction(node, vmid, "start")
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to start container")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting container", e)
                onError("Error starting container: ${e.message}")
            }
        }
    }

    fun stopContainer(node: String, vmid: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.performContainerAction(node, vmid, "stop")
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to stop container")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping container", e)
                onError("Error stopping container: ${e.message}")
            }
        }
    }

    fun deleteContainer(node: String, vmid: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.deleteContainer(node, vmid)
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to delete container")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting container", e)
                onError("Error deleting container: ${e.message}")
            }
        }
    }

    // VM Actions
    fun startVM(node: String, vmid: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.performVMAction(node, vmid, "start")
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to start VM")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting VM", e)
                onError("Error starting VM: ${e.message}")
            }
        }
    }

    fun stopVM(node: String, vmid: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.performVMAction(node, vmid, "stop")
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to stop VM")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping VM", e)
                onError("Error stopping VM: ${e.message}")
            }
        }
    }

    fun deleteVM(node: String, vmid: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.deleteVM(node, vmid)
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to delete VM")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting VM", e)
                onError("Error deleting VM: ${e.message}")
            }
        }
    }

    // Task Actions
    fun deleteTask(node: String, upid: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.deleteTask(node, upid)
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to delete task")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task", e)
                onError("Error deleting task: ${e.message}")
            }
        }
    }

    // User Actions
    fun deleteUser(userid: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.deleteUser(userid)
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to delete user")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting user", e)
                onError("Error deleting user: ${e.message}")
            }
        }
    }

    // Backup Actions
    fun deleteBackup(node: String, storage: String, volume: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = getApiService()
                if (apiService == null) {
                    onError("Not authenticated")
                    return@launch
                }
                
                val response = apiService.deleteBackup(node, storage, volume)
                if (response.success) {
                    onSuccess()
                } else {
                    onError("Failed to delete backup")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting backup", e)
                onError("Error deleting backup: ${e.message}")
            }
        }
    }
} 