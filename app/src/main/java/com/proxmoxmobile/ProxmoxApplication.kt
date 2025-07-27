package com.proxmoxmobile

import android.app.Application
import com.proxmoxmobile.di.AppContainer

class ProxmoxApplication : Application() {
    
    lateinit var appContainer: AppContainer
        private set
    
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer.getInstance(this)
    }
} 