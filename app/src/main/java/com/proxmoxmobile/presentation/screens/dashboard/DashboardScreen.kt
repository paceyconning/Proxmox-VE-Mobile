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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proxmoxmobile.data.model.Node
import com.proxmoxmobile.presentation.navigation.Screen
import com.proxmoxmobile.presentation.viewmodel.MainViewModel
import android.util.Log
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var nodes by remember { mutableStateOf<List<Node>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch nodes when screen loads
    LaunchedEffect(Unit) {
        try {
            scope.launch {
                try {
                    // Add a longer delay to ensure authentication is complete
                    kotlinx.coroutines.delay(2000)
                    
                    isLoading = true
                    errorMessage = null
                    
                    Log.d("DashboardScreen", "Starting to load nodes")
                    
                    // Check if we have a valid API service
                    val apiService = try {
                        viewModel.getApiService()
                    } catch (e: Exception) {
                        Log.e("DashboardScreen", "Failed to get API service", e)
                        errorMessage = "Authentication failed - please login again"
                        return@launch
                    }
                    
                    if (apiService == null) {
                        Log.e("DashboardScreen", "API service is null")
                        errorMessage = "Not authenticated - please login again"
                        return@launch
                    }
                    
                    Log.d("DashboardScreen", "API service available, fetching nodes")
                    
                    // Wrap the API call in additional error handling
                    val response = try {
                        apiService.getNodes()
                    } catch (e: Exception) {
                        Log.e("DashboardScreen", "API call failed", e)
                        errorMessage = "Failed to connect to server: ${e.message}"
                        return@launch
                    }
                    
                    Log.d("DashboardScreen", "Nodes response received: ${response.data?.size ?: 0} nodes")
                    
                    // Safely handle the response data
                    val nodeList = response.data ?: emptyList()
                    Log.d("DashboardScreen", "Processed ${nodeList.size} nodes")
                    
                    // Validate each node before adding to the list
                    val validNodes = nodeList.filter { node ->
                        try {
                            node.node.isNotBlank() && 
                            node.status.isNotBlank() &&
                            node.cpu >= 0 &&
                            node.mem >= 0 &&
                            node.uptime >= 0
                        } catch (e: Exception) {
                            Log.w("DashboardScreen", "Invalid node data: ${e.message}")
                            false
                        }
                    }
                    
                    nodes = validNodes
                    Log.d("DashboardScreen", "Successfully loaded ${nodes.size} valid nodes")
                    
                } catch (e: retrofit2.HttpException) {
                    Log.e("DashboardScreen", "HTTP error loading nodes: ${e.code()}", e)
                    when (e.code()) {
                        401 -> errorMessage = "Authentication required - please login again"
                        403 -> errorMessage = "Access forbidden - check permissions"
                        500 -> errorMessage = "Server error - please try again"
                        else -> errorMessage = "Failed to load nodes: HTTP ${e.code()}"
                    }
                } catch (e: Exception) {
                    Log.e("DashboardScreen", "Failed to load nodes", e)
                    errorMessage = "Failed to load nodes: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            Log.e("DashboardScreen", "Critical error in LaunchedEffect", e)
            errorMessage = "An unexpected error occurred"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Proxmox VE",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // System Status Widget - Only show if we have valid node data
            if (nodes.isNotEmpty()) {
                val firstNode = nodes.first()
                if (isValidNode(firstNode)) {
                    item {
                        SystemStatusCard(firstNode)
                    }
                }
            }

            // Welcome Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Welcome to Proxmox VE",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Manage your virtual machines and containers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Error message
            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Loading state
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Nodes Section
            if (nodes.isNotEmpty()) {
                item {
                    Text(
                        text = "Nodes (${nodes.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(nodes) { node ->
                    NodeCard(
                        node = node,
                        onClick = {
                            try {
                                navController.navigate("${Screen.VMList.route}/${node.node}")
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Navigation error", e)
                            }
                        }
                    )
                }
            }

            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "LXC",
                        subtitle = "Containers",
                        icon = Icons.Default.Storage,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            try {
                                if (nodes.isNotEmpty()) {
                                    navController.navigate("${Screen.ContainerList.route}/${nodes.first().node}")
                                }
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "LXC navigation error", e)
                            }
                        }
                    )
                    
                    QuickActionCard(
                        title = "VM",
                        subtitle = "Machines",
                        icon = Icons.Default.Computer,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            try {
                                if (nodes.isNotEmpty()) {
                                    navController.navigate("${Screen.VMList.route}/${nodes.first().node}")
                                }
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "VM navigation error", e)
                            }
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Storage",
                        subtitle = "Pools",
                        icon = Icons.Default.Storage,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            try {
                                if (nodes.isNotEmpty()) {
                                    navController.navigate("${Screen.Storage.route}/${nodes.first().node}")
                                }
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Storage navigation error", e)
                            }
                        }
                    )
                    
                    QuickActionCard(
                        title = "Network",
                        subtitle = "Interfaces",
                        icon = Icons.Default.Wifi,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            try {
                                navController.navigate(Screen.Network.route)
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Network navigation error", e)
                            }
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Users",
                        subtitle = "Management",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            try {
                                navController.navigate(Screen.Users.route)
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Users navigation error", e)
                            }
                        }
                    )
                    
                    QuickActionCard(
                        title = "Backups",
                        subtitle = "Restore",
                        icon = Icons.Default.Backup,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            try {
                                navController.navigate(Screen.Backups.route)
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Backups navigation error", e)
                            }
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Tasks",
                        subtitle = "Monitor",
                        icon = Icons.AutoMirrored.Filled.List,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            try {
                                navController.navigate(Screen.Tasks.route)
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Tasks navigation error", e)
                            }
                        }
                    )
                    
                    QuickActionCard(
                        title = "Cluster",
                        subtitle = "Status",
                        icon = Icons.Default.AccountTree,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            try {
                                navController.navigate(Screen.Cluster.route)
                            } catch (e: Exception) {
                                Log.e("DashboardScreen", "Cluster navigation error", e)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun isValidNode(node: Node): Boolean {
    return node.node.isNotBlank() && 
           node.cpu >= 0 && 
           node.mem >= 0 && 
           node.uptime >= 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemStatusCard(node: Node) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "System Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusItem(
                    label = "CPU",
                    value = "${node.cpu.toInt()}%",
                    icon = Icons.Default.Memory,
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatusItem(
                    label = "Memory",
                    value = "${(node.mem / 1024 / 1024 / 1024).toInt()}GB",
                    icon = Icons.Default.Storage,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                StatusItem(
                    label = "Uptime",
                    value = "${(node.uptime / 3600).toInt()}h",
                    icon = Icons.Default.Schedule,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun StatusItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.7f)
        )
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Status: ${node.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 