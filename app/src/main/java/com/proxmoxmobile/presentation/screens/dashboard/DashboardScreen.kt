package com.proxmoxmobile.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proxmoxmobile.presentation.navigation.Screen
import com.proxmoxmobile.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val currentServer by viewModel.currentServer.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Server Info Card
            currentServer?.let { server ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Connected Server",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${server.host}:${server.port}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "User: ${server.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "VMs",
                    value = "12",
                    icon = Icons.Default.Computer,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.VMList.route) }
                )
                StatCard(
                    title = "Containers",
                    value = "8",
                    icon = Icons.Default.Storage,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.ContainerList.route) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Storage",
                    value = "2.1TB",
                    icon = Icons.Default.Storage,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Storage.route) }
                )
                StatCard(
                    title = "Tasks",
                    value = "3",
                    icon = Icons.Default.Pending,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Tasks.route) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Grid
            Text(
                text = "Management",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    NavigationCard(
                        title = "Virtual Machines",
                        icon = Icons.Default.Computer,
                        onClick = { navController.navigate(Screen.VMList.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Containers",
                        icon = Icons.Default.Storage,
                        onClick = { navController.navigate(Screen.ContainerList.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Storage",
                        icon = Icons.Default.Storage,
                        onClick = { navController.navigate(Screen.Storage.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Network",
                        icon = Icons.Default.Wifi,
                        onClick = { navController.navigate(Screen.Network.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Users",
                        icon = Icons.Default.People,
                        onClick = { navController.navigate(Screen.Users.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Backups",
                        icon = Icons.Default.Backup,
                        onClick = { navController.navigate(Screen.Backups.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Tasks",
                        icon = Icons.Default.Pending,
                        onClick = { navController.navigate(Screen.Tasks.route) }
                    )
                }
                item {
                    NavigationCard(
                        title = "Cluster",
                        icon = Icons.Default.AccountTree,
                        onClick = { navController.navigate(Screen.Cluster.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 