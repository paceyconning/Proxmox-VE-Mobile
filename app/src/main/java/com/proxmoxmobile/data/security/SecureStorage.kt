package com.proxmoxmobile.data.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureStorage(private val context: Context) {
    
    companion object {
        private const val MASTER_KEY_ALIAS = "proxmox_master_key"
        private const val PREFS_NAME = "proxmox_secure_prefs"
        private const val KEY_HOST = "host"
        private const val KEY_PORT = "port"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REALM = "realm"
        private const val KEY_USE_HTTPS = "use_https"
        private const val KEY_SAVE_CREDENTIALS = "save_credentials"
    }
    
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()
    }
    
    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    fun saveCredentials(
        host: String,
        port: Int,
        username: String,
        password: String,
        realm: String,
        useHttps: Boolean,
        saveCredentials: Boolean
    ) {
        encryptedPrefs.edit().apply {
            putString(KEY_HOST, host)
            putInt(KEY_PORT, port)
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password) // This is now encrypted
            putString(KEY_REALM, realm)
            putBoolean(KEY_USE_HTTPS, useHttps)
            putBoolean(KEY_SAVE_CREDENTIALS, saveCredentials)
            apply()
        }
    }
    
    fun loadSavedCredentials(): SavedCredentials? {
        val saveCredentials = encryptedPrefs.getBoolean(KEY_SAVE_CREDENTIALS, false)
        if (!saveCredentials) return null
        
        val host = encryptedPrefs.getString(KEY_HOST, "")
        val port = encryptedPrefs.getInt(KEY_PORT, 8006)
        val username = encryptedPrefs.getString(KEY_USERNAME, "")
        val password = encryptedPrefs.getString(KEY_PASSWORD, "")
        val realm = encryptedPrefs.getString(KEY_REALM, "pam")
        val useHttps = encryptedPrefs.getBoolean(KEY_USE_HTTPS, true)
        
        if (host?.isNotBlank() == true && username?.isNotBlank() == true && password?.isNotBlank() == true) {
            return SavedCredentials(
                host = host,
                port = port,
                username = username,
                password = password,
                realm = realm ?: "pam",
                useHttps = useHttps
            )
        }
        return null
    }
    
    fun clearSavedCredentials() {
        encryptedPrefs.edit().clear().apply()
    }
    
    fun hasSavedCredentials(): Boolean {
        return encryptedPrefs.getBoolean(KEY_SAVE_CREDENTIALS, false)
    }
    
    data class SavedCredentials(
        val host: String,
        val port: Int,
        val username: String,
        val password: String,
        val realm: String,
        val useHttps: Boolean
    )
} 