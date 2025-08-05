package com.proxmoxmobile.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proxmoxmobile.presentation.navigation.Screen
import com.proxmoxmobile.presentation.viewmodel.MainViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var showClearCredentialsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var autoRefreshInterval by remember { mutableStateOf(30) } // seconds
    var enableNotifications by remember { mutableStateOf(true) }
    var enableBiometric by remember { mutableStateOf(false) }
    var enableAutoLogin by remember { mutableStateOf(viewModel.hasSavedCredentials()) }
    var enableDarkMode by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Info Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Proxmox VE Mobile",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Modern Android client for Proxmox VE",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Display Settings Section
            item {
                SettingsSection(title = "Display") {
                    SettingsSwitchItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Use dark theme",
                        checked = enableDarkMode,
                        onCheckedChange = { enableDarkMode = it }
                    )
                }
            }

            // Auto-refresh Settings Section
            item {
                SettingsSection(title = "Auto-refresh") {
                    SettingsSliderItem(
                        icon = Icons.Default.Refresh,
                        title = "Refresh Interval",
                        subtitle = "Update data every ${autoRefreshInterval} seconds",
                        value = autoRefreshInterval.toFloat(),
                        onValueChange = { autoRefreshInterval = it.toInt() },
                        valueRange = 10f..60f,
                        steps = 5
                    )
                }
            }

            // Security Settings Section
            item {
                SettingsSection(title = "Security") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Security,
                        title = "Biometric Authentication",
                        subtitle = "Use fingerprint or face unlock",
                        checked = enableBiometric,
                        onCheckedChange = { enableBiometric = it }
                    )
                    
                    SettingsSwitchItem(
                        icon = Icons.Default.Login,
                        title = "Auto-login",
                        subtitle = "Automatically login with saved credentials",
                        checked = enableAutoLogin,
                        onCheckedChange = { enableAutoLogin = it }
                    )
                    
                    SettingsButtonItem(
                        icon = Icons.Default.Delete,
                        title = "Clear Saved Credentials",
                        subtitle = "Remove encrypted login details",
                        onClick = { showClearCredentialsDialog = true }
                    )
                }
            }

            // Notification Settings Section
            item {
                SettingsSection(title = "Notifications") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Enable Notifications",
                        subtitle = "Show system status alerts",
                        checked = enableNotifications,
                        onCheckedChange = { enableNotifications = it }
                    )
                }
            }

            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingsButtonItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        subtitle = "App information and credits",
                        onClick = { showAboutDialog = true }
                    )
                    
                    SettingsButtonItem(
                        icon = Icons.Default.BugReport,
                        title = "Report Bug",
                        subtitle = "Send feedback or report issues",
                        onClick = { /* TODO: Implement bug reporting */ }
                    )
                }
            }

            // Logout Section
            item {
                SettingsSection(title = "Account") {
                    SettingsButtonItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Logout",
                        subtitle = "Sign out of current session",
                        onClick = {
                            viewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Clear credentials confirmation dialog
    if (showClearCredentialsDialog) {
        AlertDialog(
            onDismissRequest = { showClearCredentialsDialog = false },
            title = { Text("Clear Saved Credentials") },
            text = { Text("Are you sure you want to clear all saved login credentials? You'll need to enter them again next time.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearSavedCredentials()
                        enableAutoLogin = false
                        showClearCredentialsDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCredentialsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // About dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Proxmox VE Mobile") },
            text = { 
                Column {
                    Text("Version: 1.0.0")
                    Text("Build: 2024.1.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A modern Android client for managing Proxmox Virtual Environment servers.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Features:")
                    Text("• Secure authentication with encrypted storage")
                    Text("• Real-time monitoring and management")
                    Text("• LXC container and VM management")
                    Text("• Storage and network monitoring")
                    Text("• User and task management")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Built with:")
                    Text("• Jetpack Compose")
                    Text("• Material Design 3")
                    Text("• Kotlin Coroutines")
                    Text("• Retrofit for API communication")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            content()
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
fun SettingsSliderItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { 
            Column {
                Text(subtitle)
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = steps,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        leadingContent = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun SettingsButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    ListItem(
        headlineContent = { 
            Text(
                text = title,
                color = textColor
            )
        },
        supportingContent = { 
            Text(
                text = subtitle,
                color = textColor.copy(alpha = 0.7f)
            )
        },
        leadingContent = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor
            )
        },
        trailingContent = { 
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.6f)
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
} 