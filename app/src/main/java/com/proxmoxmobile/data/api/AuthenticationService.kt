package com.proxmoxmobile.data.api

import android.util.Log
import com.proxmoxmobile.data.model.LoginRequest
import com.proxmoxmobile.data.model.LoginResponse
import com.proxmoxmobile.data.model.ServerConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AuthenticationService {
    
    companion object {
        private const val TAG = "AuthenticationService"
    }
    
    private fun createTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                Log.d(TAG, "checkClientTrusted: authType=$authType")
            }
            
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                Log.d(TAG, "checkServerTrusted: authType=$authType")
                // Accept all certificates for development
            }
            
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }
    
    private fun createOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "HTTP: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val trustManager = createTrustManager()
        
        val sslContext = try {
            SSLContext.getInstance("TLSv1.2")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Failed to create SSL context with TLSv1.2, falling back to TLS", e)
            SSLContext.getInstance("TLS")
        }
        
        try {
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        } catch (e: KeyManagementException) {
            Log.e(TAG, "Failed to initialize SSL context", e)
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                Log.d(TAG, "Making authentication request to: ${original.url}")
                
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "ProxmoxVEMobile/1.0")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)
                
                chain.proceed(requestBuilder.build())
            }
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { hostname, session ->
                Log.d(TAG, "Hostname verification for auth: $hostname")
                true // Accept all hostnames for development
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    fun createApiService(serverConfig: ServerConfig): ProxmoxApiService {
        val protocol = if (serverConfig.useHttps) "https" else "http"
        val baseUrl = "$protocol://${serverConfig.host}:${serverConfig.port}/"
        
        Log.d(TAG, "Creating authentication API service with base URL: $baseUrl")
        
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(ProxmoxApiService::class.java)
    }
    
    suspend fun authenticate(serverConfig: ServerConfig): Result<LoginResponse> {
        return try {
            Log.d(TAG, "Starting authentication for ${serverConfig.username}@${serverConfig.host}:${serverConfig.port}")
            
            // Validate server config
            if (serverConfig.host.isBlank()) {
                throw IllegalArgumentException("Host cannot be empty")
            }
            if (serverConfig.username.isBlank()) {
                throw IllegalArgumentException("Username cannot be empty")
            }
            if (serverConfig.password.isNullOrBlank()) {
                throw IllegalArgumentException("Password cannot be empty")
            }
            
            Log.d(TAG, "Creating API service and attempting login")
            
            val apiService = createApiService(serverConfig)
            val loginRequest = LoginRequest(
                username = serverConfig.username,
                password = serverConfig.password,
                realm = serverConfig.realm
            )
            
            Log.d(TAG, "Sending login request with realm: ${serverConfig.realm}")
            val response = apiService.login(loginRequest)
            
            if (response.data.ticket.isBlank()) {
                throw IllegalStateException("Received empty authentication ticket")
            }
            
            Log.d(TAG, "Login successful for user: ${response.data.username}")
            Log.d(TAG, "Auth ticket received: ${response.data.ticket.take(10)}...")
            
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                401 -> {
                    Log.e(TAG, "Authentication failed - invalid credentials (401)")
                    Result.failure(Exception("Invalid username or password"))
                }
                403 -> {
                    Log.e(TAG, "Authentication failed - access forbidden (403)")
                    Result.failure(Exception("Access forbidden - check user permissions"))
                }
                500 -> {
                    Log.e(TAG, "Authentication failed - server error (500)")
                    Result.failure(Exception("Server error - please try again"))
                }
                else -> {
                    Log.e(TAG, "Authentication failed with HTTP ${e.code()}", e)
                    Result.failure(Exception("Authentication failed: HTTP ${e.code()}"))
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Invalid server configuration", e)
            Result.failure(e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Authentication response invalid", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed with exception", e)
            Result.failure(e)
        }
    }
} 