package com.proxmoxmobile.presentation.screens.containers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proxmoxmobile.data.model.Container
import com.proxmoxmobile.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerListScreen(
    navController: NavController,
    viewModel: MainViewModel,
    nodeName: String? = null
) {
    var containers by remember { mutableStateOf<List<Container>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val apiService = viewModel.getApiService()

    // Load containers when the screen is first displayed
    LaunchedEffect(apiService, nodeName) {
        if (apiService != null && !nodeName.isNullOrBlank()) {
            scope.launch {
                try {
                    isLoading = true
                    errorMessage = null
                    
                    Log.d("ContainerListScreen", "Loading containers for node: $nodeName")
                    val response = apiService.getContainers(nodeName)
                    Log.d("ContainerListScreen", "API response received: ${response.data?.size ?: 0} containers")
                    
                    // Safely handle the response data
                    val containerList = response.data ?: emptyList()
                    Log.d("ContainerListScreen", "Processed ${containerList.size} containers")
                    
                    // Validate each container before adding to the list
                    val validContainers = containerList.filter { container ->
                        try {
                            container.vmid > 0 && 
                            container.name.isNotBlank() && 
                            container.status.isNotBlank() &&
                            container.cpu >= 0 &&
                            container.mem >= 0 &&
                            container.maxmem >= 0 &&
                            container.uptime >= 0
                        } catch (e: Exception) {
                            Log.w("ContainerListScreen", "Invalid container data: ${e.message}")
                            false
                        }
                    }
                    
                    containers = validContainers
                    Log.d("ContainerListScreen", "Successfully loaded ${containers.size} valid containers")
                    
                } catch (e: retrofit2.HttpException) {
                    Log.e("ContainerListScreen", "HTTP error loading containers: ${e.code()}", e)
                    when (e.code()) {
                        401 -> errorMessage = "Authentication required - please login again"
                        403 -> errorMessage = "Access forbidden - check permissions"
                        404 -> errorMessage = "Node not found: $nodeName"
                        500 -> errorMessage = "Server error - please try again"
                        else -> errorMessage = "Failed to load containers: HTTP ${e.code()}"
                    }
                } catch (e: Exception) {
                    Log.e("ContainerListScreen", "Failed to load containers", e)
                    errorMessage = "Failed to load containers: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        } else {
            errorMessage = "Invalid node name or API service not available"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LXC Containers - ${nodeName ?: "Unknown"}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (containers.isEmpty() && errorMessage == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No LXC Containers Found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "This node doesn't have any LXC containers configured",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "LXC Containers (${containers.size})",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    items(containers) { container ->
                        ContainerCard(container = container)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerCard(container: Container) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = container.name.ifBlank { "Unknown Container" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CTID: ${container.vmid}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = when (container.status) {
                        "running" -> Icons.Default.PlayArrow
                        "stopped" -> Icons.Default.Stop
                        "paused" -> Icons.Default.Pause
                        else -> Icons.Default.Help
                    },
                    contentDescription = container.status,
                    tint = when (container.status) {
                        "running" -> MaterialTheme.colorScheme.primary
                        "stopped" -> MaterialTheme.colorScheme.error
                        "paused" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CPU",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${container.cpu.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column {
                    Text(
                        text = "Memory",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(container.mem / 1024 / 1024 / 1024).toInt()}GB / ${(container.maxmem / 1024 / 1024 / 1024).toInt()}GB",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column {
                    Text(
                        text = "Uptime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(container.uptime / 3600).toInt()}h",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            if (!container.tags.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tags: ${container.tags}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 