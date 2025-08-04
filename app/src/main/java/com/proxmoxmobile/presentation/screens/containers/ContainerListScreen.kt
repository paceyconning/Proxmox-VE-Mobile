@file:OptIn(ExperimentalMaterial3Api::class)
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
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

fun Double.format(digits: Int) = String.format(Locale.US, "%.${digits}f", this)

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
                    // Sort: by VMID ascending
                    containers = validContainers.sortedBy { it.vmid }
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
                        ContainerCard(container = container) {
                            navController.navigate("containerDetail/${container.vmid}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerCard(container: Container, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = container.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "#${container.vmid}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Resource monitor
                Text(
                    text = "CPU: ${(container.cpu * 100).format(1)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "RAM: ${if (container.mem > 1024*1024*1024) "${(container.mem / 1024.0 / 1024.0 / 1024.0).format(1)}GB" else "${(container.mem / 1024.0 / 1024.0).format(1)}MB"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = container.status.capitalize(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (container.status == "running") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { /* TODO: Start container */ },
                    enabled = container.status != "running",
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
                    Spacer(Modifier.width(4.dp))
                    Text("Start")
                }
                Button(
                    onClick = { /* TODO: Stop container */ },
                    enabled = container.status == "running",
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stop")
                    Spacer(Modifier.width(4.dp))
                    Text("Stop")
                }
                OutlinedButton(
                    onClick = { /* TODO: Open console */ },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(Icons.Filled.Terminal, contentDescription = "Console")
                    Spacer(Modifier.width(4.dp))
                    Text("Console")
                }
                OutlinedButton(
                    onClick = { /* TODO: Delete container */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
} 

// Add ContainerDetailScreen
@Composable
fun ContainerDetailScreen(
    vmid: Int,
    viewModel: MainViewModel,
    navController: NavController
) {
    val apiService = viewModel.getApiService()
    var container by remember { mutableStateOf<Container?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var cpu by remember { mutableStateOf(0.0) }
    var ram by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(apiService, vmid) {
        if (apiService != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                try {
                    // Find the node and container (stub: search all nodes in cache)
                    val nodes = viewModel.getCachedNodes() ?: emptyList()
                    val found = nodes.flatMap { node ->
                        try {
                            apiService.getContainers(node.node).data ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }.find { it.vmid == vmid }
                    container = found
                    cpu = found?.cpu ?: 0.0
                    ram = found?.mem ?: 0L
                } catch (e: Exception) {
                    errorMessage = "Failed to load container: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Container Details: $vmid") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (container != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Name: ${container!!.name}", style = MaterialTheme.typography.titleLarge)
                Text("Status: ${container!!.status}")
                Text("CPU: ${(cpu * 100).format(1)}%", style = MaterialTheme.typography.bodyLarge)
                Text("RAM: ${if (ram >= 1024 * 1024 * 1024) "${(ram.toDouble() / 1024 / 1024 / 1024).format(1)}GB" else "${(ram.toDouble() / 1024 / 1024).format(1)}MB"}", style = MaterialTheme.typography.bodyLarge)
                Text("Uptime: ${(container!!.uptime / 3600).toInt()}h")
                // Editable fields
                Spacer(Modifier.height(16.dp))
                Text("Edit Resources", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("CPU (%)", Modifier.width(80.dp))
                    Slider(
                        value = (cpu * 100).toFloat(),
                        onValueChange = { cpu = it.toDouble() / 100.0 },
                        valueRange = 1f..100f,
                        steps = 99,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(cpu * 100).format(1)}%", Modifier.width(60.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("RAM (MB)", Modifier.width(80.dp))
                    Slider(
                        value = (ram / 1024f / 1024f).toFloat(),
                        onValueChange = { ram = (it * 1024 * 1024).toLong() },
                        valueRange = 128f..65536f,
                        steps = 65535,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(ram / 1024 / 1024)}MB", Modifier.width(60.dp))
                }
                Button(onClick = {
                    // TODO: Call API to update resources
                    errorMessage = "Live resource editing not yet implemented."
                }) {
                    Text("Apply Changes")
                }
                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
        } else if (errorMessage != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
} 