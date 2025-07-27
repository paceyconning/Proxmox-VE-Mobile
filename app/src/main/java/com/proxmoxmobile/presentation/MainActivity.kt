package com.proxmoxmobile.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.proxmoxmobile.presentation.navigation.ProxmoxNavHost
import com.proxmoxmobile.presentation.theme.ProxmoxTheme
import com.proxmoxmobile.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        setContent {
            val navController = rememberNavController()
            val viewModel = remember { MainViewModel() }
            
            // Initialize ViewModel with context for SharedPreferences
            LaunchedEffect(Unit) {
                viewModel.initialize(this@MainActivity)
            }
            
            // Check for saved credentials and auto-login
            LaunchedEffect(Unit) {
                val savedConfig = viewModel.loadSavedCredentials()
                if (savedConfig != null) {
                    // Auto-login with saved credentials (user will need to enter password)
                    viewModel.setCurrentServer(savedConfig)
                }
            }
            
            ProxmoxTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                DisposableEffect(systemUiController, useDarkIcons) {
                    systemUiController.setSystemBarsColor(
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                    onDispose {}
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProxmoxNavHost(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
} 