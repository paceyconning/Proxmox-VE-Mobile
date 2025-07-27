package com.proxmoxmobile.data.api

import android.util.Log
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

class ProxmoxApiClient {
    
    companion object {
        private const val TAG = "ProxmoxApiClient"
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
    
    private fun createOkHttpClient(authToken: String? = null, csrfToken: String? = null): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "HTTP: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val trustManager = createTrustManager()
        
        val sslContext = try {
            SSLContext.getInstance("TLSv1.2")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Failed to create SSL context", e)
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
                Log.d(TAG, "Making request to: ${original.url}")
                
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "ProxmoxVEMobile/1.0")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                
                // Add authentication headers if available
                authToken?.let { token ->
                    requestBuilder.header("Cookie", "PVEAuthCookie=$token")
                    Log.d(TAG, "Added auth token to request")
                }
                
                csrfToken?.let { csrf ->
                    requestBuilder.header("CSRFPreventionToken", csrf)
                    Log.d(TAG, "Added CSRF token to request")
                }
                
                requestBuilder.method(original.method, original.body)
                chain.proceed(requestBuilder.build())
            }
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { hostname, session ->
                Log.d(TAG, "Hostname verification: $hostname")
                true // Accept all hostnames for development
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    fun createApiService(serverConfig: ServerConfig, authToken: String? = null, csrfToken: String? = null): ProxmoxApiService {
        val protocol = if (serverConfig.useHttps) "https" else "http"
        val baseUrl = "$protocol://${serverConfig.host}:${serverConfig.port}/"
        
        Log.d(TAG, "Creating API service with base URL: $baseUrl")
        
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(authToken, csrfToken))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(ProxmoxApiService::class.java)
    }
} 