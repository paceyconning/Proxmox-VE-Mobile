package com.proxmoxmobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proxmoxmobile.presentation.screens.auth.LoginScreen
import com.proxmoxmobile.presentation.screens.dashboard.DashboardScreen
import com.proxmoxmobile.presentation.screens.servers.ServerListScreen
import com.proxmoxmobile.presentation.screens.settings.SettingsScreen
import com.proxmoxmobile.presentation.screens.vms.VMListScreen
import com.proxmoxmobile.presentation.screens.containers.ContainerListScreen
import com.proxmoxmobile.presentation.screens.storage.StorageScreen
import com.proxmoxmobile.presentation.screens.network.NetworkScreen
import com.proxmoxmobile.presentation.screens.users.UserManagementScreen
import com.proxmoxmobile.presentation.screens.backups.BackupScreen
import com.proxmoxmobile.presentation.screens.tasks.TaskScreen
import com.proxmoxmobile.presentation.screens.cluster.ClusterScreen
import com.proxmoxmobile.presentation.viewmodel.MainViewModel

@Composable
fun ProxmoxNavHost(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.ServerList.route) {
            ServerListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.VMList.route) {
            VMListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.ContainerList.route) {
            ContainerListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Storage.route) {
            StorageScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Network.route) {
            NetworkScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Users.route) {
            UserManagementScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Backups.route) {
            BackupScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Tasks.route) {
            TaskScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Cluster.route) {
            ClusterScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
} 