package com.proxmoxmobile.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proxmoxmobile.data.model.Node
import com.proxmoxmobile.presentation.navigation.Screen
import com.proxmoxmobile.presentation.viewmodel.MainViewModel
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var nodes by remember { mutableStateOf<List<Node>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch nodes when screen loads
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        
        try {
            val apiService = viewModel.getApiService()
            if (apiService != null) {
                val response = apiService.getNodes()
                nodes = response.data
                Log.d("DashboardScreen", "Loaded ${nodes.size} nodes")
            } else {
                errorMessage = "Not authenticated"
            }
        } catch (e: Exception) {
            Log.e("DashboardScreen", "Failed to load nodes", e)
            errorMessage = "Failed to load nodes: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proxmox VE Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { 
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome to Proxmox VE",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Manage your virtual machines, containers, and infrastructure",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Error Message
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage!!,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Loading State
            if (isLoading) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Loading nodes...")
                        }
                    }
                }
            }

            // Nodes Section
            if (nodes.isNotEmpty()) {
                item {
                    Text(
                        text = "Nodes (${nodes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(nodes) { node ->
                    NodeCard(
                        node = node,
                        onClick = {
                            navController.navigate("${Screen.VMList.route}/${node.node}")
                        }
                    )
                }
            }

            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Virtual Machines",
                        icon = Icons.Default.Computer,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (nodes.isNotEmpty()) {
                                navController.navigate("${Screen.VMList.route}/${nodes.first().node}")
                            }
                        }
                    )
                    
                    QuickActionCard(
                        title = "Containers",
                        icon = Icons.Default.Storage,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (nodes.isNotEmpty()) {
                                navController.navigate("${Screen.ContainerList.route}/${nodes.first().node}")
                            }
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Storage",
                        icon = Icons.Default.Storage,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.Storage.route) }
                    )
                    
                    QuickActionCard(
                        title = "Network",
                        icon = Icons.Default.Wifi,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.Network.route) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Users",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.Users.route) }
                    )
                    
                    QuickActionCard(
                        title = "Backups",
                        icon = Icons.Default.Backup,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.Backups.route) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Tasks",
                        icon = Icons.Default.List,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.Tasks.route) }
                    )
                    
                    QuickActionCard(
                        title = "Cluster",
                        icon = Icons.Default.AccountTree,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.Cluster.route) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeCard(
    node: Node,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.node,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Status: ${node.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
} 