package com.proxmoxmobile.presentation.screens.vms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proxmoxmobile.data.model.VirtualMachine
import com.proxmoxmobile.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VMListScreen(
    navController: NavController,
    viewModel: MainViewModel,
    nodeName: String? = null
) {
    var vms by remember { mutableStateOf<List<VirtualMachine>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val apiService = viewModel.getApiService()

    // Load VMs when the screen is first displayed
    LaunchedEffect(apiService, nodeName) {
        if (apiService != null && !nodeName.isNullOrBlank()) {
            scope.launch {
                try {
                    isLoading = true
                    errorMessage = null
                    
                    Log.d("VMListScreen", "Loading VMs for node: $nodeName")
                    val response = apiService.getVirtualMachines(nodeName)
                    Log.d("VMListScreen", "API response received: ${response.data?.size ?: 0} VMs")
                    
                    // Safely handle the response data
                    val vmList = response.data ?: emptyList()
                    Log.d("VMListScreen", "Processed ${vmList.size} VMs")
                    
                    // Validate each VM before adding to the list
                    val validVMs = vmList.filter { vm ->
                        try {
                            vm.vmid > 0 && 
                            vm.name.isNotBlank() && 
                            vm.status.isNotBlank() &&
                            vm.cpu >= 0 &&
                            vm.mem >= 0 &&
                            vm.maxmem >= 0 &&
                            vm.uptime >= 0
                        } catch (e: Exception) {
                            Log.w("VMListScreen", "Invalid VM data: ${e.message}")
                            false
                        }
                    }
                    
                    vms = validVMs
                    Log.d("VMListScreen", "Successfully loaded ${vms.size} valid VMs")
                    
                } catch (e: retrofit2.HttpException) {
                    Log.e("VMListScreen", "HTTP error loading VMs: ${e.code()}", e)
                    when (e.code()) {
                        401 -> errorMessage = "Authentication required - please login again"
                        403 -> errorMessage = "Access forbidden - check permissions"
                        404 -> errorMessage = "Node not found: $nodeName"
                        500 -> errorMessage = "Server error - please try again"
                        else -> errorMessage = "Failed to load VMs: HTTP ${e.code()}"
                    }
                } catch (e: Exception) {
                    Log.e("VMListScreen", "Failed to load VMs", e)
                    errorMessage = "Failed to load VMs: ${e.message}"
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
                title = { Text("Virtual Machines - ${nodeName ?: "Unknown"}") },
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
            } else if (vms.isEmpty() && errorMessage == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Virtual Machines Found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "This node doesn't have any VMs configured",
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
                            text = "Virtual Machines (${vms.size})",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    items(vms) { vm ->
                        VMCard(vm = vm)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VMCard(vm: VirtualMachine) {
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
                        text = vm.name.ifBlank { "Unknown VM" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "VMID: ${vm.vmid}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = when (vm.status) {
                        "running" -> Icons.Default.PlayArrow
                        "stopped" -> Icons.Default.Stop
                        "paused" -> Icons.Default.Pause
                        else -> Icons.Default.Help
                    },
                    contentDescription = vm.status,
                    tint = when (vm.status) {
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
                        text = "${vm.cpu.toInt()}%",
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
                        text = "${(vm.mem / 1024 / 1024 / 1024).toInt()}GB / ${(vm.maxmem / 1024 / 1024 / 1024).toInt()}GB",
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
                        text = "${(vm.uptime / 3600).toInt()}h",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            if (!vm.tags.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tags: ${vm.tags}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 