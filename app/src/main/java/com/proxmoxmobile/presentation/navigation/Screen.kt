package com.proxmoxmobile.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ServerList : Screen("server_list")
    object Dashboard : Screen("dashboard")
    object VMList : Screen("vm_list")
    object ContainerList : Screen("container_list")
    object Storage : Screen("storage")
    object Network : Screen("network")
    object Users : Screen("users")
    object Backups : Screen("backups")
    object Tasks : Screen("tasks")
    object Cluster : Screen("cluster")
    object Settings : Screen("settings")
    
    // Detail screens
    object VMDetail : Screen("vm_detail/{vmid}") {
        fun createRoute(vmid: Int) = "vm_detail/$vmid"
    }
    
    object ContainerDetail : Screen("container_detail/{vmid}") {
        fun createRoute(vmid: Int) = "container_detail/$vmid"
    }
    
    object NodeDetail : Screen("node_detail/{node}") {
        fun createRoute(node: String) = "node_detail/$node"
    }
    
    object StorageDetail : Screen("storage_detail/{storage}") {
        fun createRoute(storage: String) = "storage_detail/$storage"
    }
    
    object UserDetail : Screen("user_detail/{userid}") {
        fun createRoute(userid: String) = "user_detail/$userid"
    }
    
    object TaskDetail : Screen("task_detail/{upid}") {
        fun createRoute(upid: String) = "task_detail/$upid"
    }
} 