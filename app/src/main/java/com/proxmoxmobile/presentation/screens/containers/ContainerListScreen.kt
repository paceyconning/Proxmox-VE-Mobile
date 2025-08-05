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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.style.TextAlign
import com.proxmoxmobile.presentation.navigation.Screen
import androidx.compose.ui.graphics.vector.ImageVector


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
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    modifier = Modifier.padding(16.dp)
                )
            }
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
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Text(
                            text = "LXC Containers (${containers.size})",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                        )
                    }

                    items(containers) { container ->
                        ContainerCard(
                            container = container,
                            viewModel = viewModel,
                            node = nodeName ?: "pve",
                            onActionSuccess = { message ->
                                snackbarMessage = message
                                showSnackbar = true
                            },
                            onActionError = { message ->
                                snackbarMessage = message
                                showSnackbar = true
                            }
                        ) {
                            navController.navigate(Screen.ContainerDetail.createRoute(container.vmid))
                        }
                    }
                }
            }
        }
    }
    
    // Show snackbar when there's a message
    LaunchedEffect(showSnackbar, snackbarMessage) {
        if (showSnackbar && snackbarMessage != null) {
            snackbarHostState.showSnackbar(snackbarMessage!!)
            showSnackbar = false
            snackbarMessage = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerCard(
    container: Container, 
    viewModel: MainViewModel,
    node: String = "pve", // Default node, should be passed from parent
    onActionSuccess: (String) -> Unit = {},
    onActionError: (String) -> Unit = {},
    onClick: () -> Unit = {}
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isActionInProgress by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = container.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${container.vmid}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Status: ${container.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (container.status) {
                            "running" -> Color.Green
                            "stopped" -> Color.Red
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (container.status) {
                                "running" -> Color.Green
                                "stopped" -> Color.Red
                                else -> Color.Gray
                            },
                            shape = CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (!isActionInProgress) {
                            isActionInProgress = true
                            scope.launch {
                                viewModel.startContainer(
                                    node = node,
                                    vmid = container.vmid,
                                    onSuccess = {
                                        isActionInProgress = false
                                        onActionSuccess("Container ${container.name} started successfully")
                                    },
                                    onError = { error ->
                                        isActionInProgress = false
                                        onActionError("Failed to start container: $error")
                                    }
                                )
                            }
                        }
                    },
                    enabled = container.status != "running" && !isActionInProgress,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    if (isActionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Start")
                }
                
                Button(
                    onClick = {
                        if (!isActionInProgress) {
                            isActionInProgress = true
                            scope.launch {
                                viewModel.stopContainer(
                                    node = node,
                                    vmid = container.vmid,
                                    onSuccess = {
                                        isActionInProgress = false
                                        onActionSuccess("Container ${container.name} stopped successfully")
                                    },
                                    onError = { error ->
                                        isActionInProgress = false
                                        onActionError("Failed to stop container: $error")
                                    }
                                )
                            }
                        }
                    },
                    enabled = container.status == "running" && !isActionInProgress,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    if (isActionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Stop, contentDescription = "Stop")
                    }
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
                    onClick = { showDeleteConfirmation = true },
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
    
    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Container") },
            text = { Text("Are you sure you want to delete container '${container.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        if (!isActionInProgress) {
                            isActionInProgress = true
                            scope.launch {
                                viewModel.deleteContainer(
                                    node = node,
                                    vmid = container.vmid,
                                    onSuccess = {
                                        isActionInProgress = false
                                        onActionSuccess("Container ${container.name} deleted successfully")
                                    },
                                    onError = { error ->
                                        isActionInProgress = false
                                        onActionError("Failed to delete container: $error")
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 

// Add ContainerDetailScreen
@Composable
fun ContainerDetailScreen(
    vmid: Int,
    viewModel: MainViewModel,
    navController: NavController
) {
    var container by remember { mutableStateOf<Container?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var cpu by remember { mutableStateOf(0.0) }
    var ram by remember { mutableStateOf(0L) }
    var maxCpu by remember { mutableStateOf(0) }
    var maxRam by remember { mutableStateOf(0L) }
    var isUpdatingResources by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vmid) {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null
                
                // Get the API service
                val apiService = viewModel.getApiService()
                if (apiService == null) {
                    errorMessage = "Not authenticated"
                    isLoading = false
                    return@launch
                }

                // Find the container by searching all nodes
                val nodes = viewModel.getCachedNodes() ?: emptyList()
                var foundContainer: Container? = null
                var foundNode: String? = null
                
                for (node in nodes) {
                    try {
                        val containers = apiService.getContainers(node.node).data ?: emptyList()
                        val container = containers.find { it.vmid == vmid }
                        if (container != null) {
                            foundContainer = container
                            foundNode = node.node
                            break
                        }
                    } catch (e: Exception) {
                        // Continue searching other nodes
                    }
                }

                if (foundContainer != null && foundNode != null) {
                    container = foundContainer
                    cpu = foundContainer.cpu
                    ram = foundContainer.mem
                    maxCpu = foundContainer.maxcpu
                    maxRam = foundContainer.maxmem
                } else {
                    errorMessage = "Container not found"
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load container: ${e.message}"
            } finally {
                isLoading = false
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
                },
                actions = {
                    // Refresh button
                    IconButton(
                        onClick = {
                            scope.launch {
                                // Reload container data
                                try {
                                    isLoading = true
                                    errorMessage = null
                                    
                                    val apiService = viewModel.getApiService()
                                    if (apiService == null) {
                                        errorMessage = "Not authenticated"
                                        isLoading = false
                                        return@launch
                                    }

                                    // Find the container by searching all nodes
                                    val nodes = viewModel.getCachedNodes() ?: emptyList()
                                    var foundContainer: Container? = null
                                    var foundNode: String? = null
                                    
                                    for (node in nodes) {
                                        try {
                                            val containers = apiService.getContainers(node.node).data ?: emptyList()
                                            val container = containers.find { it.vmid == vmid }
                                            if (container != null) {
                                                foundContainer = container
                                                foundNode = node.node
                                                break
                                            }
                                        } catch (e: Exception) {
                                            // Continue searching other nodes
                                        }
                                    }

                                    if (foundContainer != null && foundNode != null) {
                                        val containerData = foundContainer!!
                                        container = containerData
                                        cpu = containerData.cpu
                                        ram = containerData.mem
                                        maxCpu = containerData.maxcpu
                                        maxRam = containerData.maxmem
                                    } else {
                                        errorMessage = "Container not found"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Failed to load container: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (container != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Container Info Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Container Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Name: ${container!!.name}")
                            Text("ID: ${container!!.vmid}")
                            Text(
                                text = "Status: ${container!!.status}",
                                color = when (container!!.status) {
                                    "running" -> Color.Green
                                    "stopped" -> Color.Red
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Text("Uptime: ${(container!!.uptime / 3600).toInt()}h")
                        }
                    }
                }

                // Current Resource Usage Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Current Resource Usage",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text("CPU Usage: ${(cpu * 100).format(1)}%")
                            Text("CPU Cores: ${maxCpu} cores")
                            Text("RAM Usage: ${formatBytes(ram)}")
                            Text("RAM Allocated: ${formatBytes(maxRam)}")
                        }
                    }
                }

                // Resource Management Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Resource Management",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // CPU Configuration Card
                            ResourceCard(
                                title = "CPU Cores",
                                currentValue = "${maxCpu} cores allocated",
                                icon = Icons.Filled.Memory,
                                onClick = {
                                    // TODO: Show CPU configuration dialog
                                }
                            )
                            
                            // RAM Configuration Card
                            ResourceCard(
                                title = "RAM Allocation",
                                currentValue = "${formatBytes(maxRam)} allocated",
                                icon = Icons.Filled.Storage,
                                onClick = {
                                    // TODO: Show RAM configuration dialog
                                }
                            )
                            
                            // Apply Changes Button
                            Button(
                                onClick = {
                                    scope.launch {
                                        isUpdatingResources = true
                                        try {
                                            // TODO: Implement API call to update container resources
                                            // This would require additional API endpoints for resource management
                                            errorMessage = "Resource management API not yet implemented"
                                        } catch (e: Exception) {
                                            errorMessage = "Failed to update resources: ${e.message}"
                                        } finally {
                                            isUpdatingResources = false
                                        }
                                    }
                                },
                                enabled = !isUpdatingResources,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isUpdatingResources) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Filled.Save, contentDescription = null)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("Apply Resource Changes")
                            }
                        }
                    }
                }

                // Action Buttons Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Container Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            // TODO: Implement start container action
                                            errorMessage = "Start action not yet implemented"
                                        }
                                    },
                                    enabled = container!!.status != "running",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Start")
                                }
                                
                                Button(
                                    onClick = {
                                        scope.launch {
                                            // TODO: Implement stop container action
                                            errorMessage = "Stop action not yet implemented"
                                        }
                                    },
                                    enabled = container!!.status == "running",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Stop, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Stop")
                                }
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    // TODO: Implement console access
                                    errorMessage = "Console access not yet implemented"
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Terminal, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Open Console")
                            }
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
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { navController.navigateUp() }) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}

// Helper function to format bytes
fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> "${(bytes.toDouble() / 1024 / 1024 / 1024).format(1)}GB"
        bytes >= 1024 * 1024 -> "${(bytes.toDouble() / 1024 / 1024).format(1)}MB"
        bytes >= 1024 -> "${(bytes.toDouble() / 1024).format(1)}KB"
        else -> "${bytes}B"
    }
}

@Composable
fun ResourceCard(
    title: String,
    currentValue: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Edit indicator
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
} 