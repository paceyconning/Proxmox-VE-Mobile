# Proxmox VE Mobile

A modern Android app for managing Proxmox VE servers with a clean, intuitive interface.

## ✨ **Latest Features (Real-time & Clean UI)**

### 🔄 **Real-time Data Updates**
- **Automatic Data Refresh**: All screens now update automatically without manual refresh buttons
- **Dashboard**: Real-time system status with live CPU, Memory, and Uptime data
- **VM Management**: Auto-refresh every 15 seconds with clean action buttons
- **Container Management**: Real-time LXC container monitoring with improved UI
- **Task Monitoring**: Live task statistics and status updates every 10 seconds

### 🎨 **Clean & Modern UI**
- **Removed Auto-refresh Buttons**: Cleaner interface without cluttered refresh controls
- **Improved Login Screen**: Better scaling and responsive design
- **Enhanced Container Cards**: Modern design with proper spacing and action buttons
- **Real-time Status Indicators**: Live data display with proper formatting
- **Streamlined Navigation**: Simplified top bar with essential actions only

### 🔧 **Technical Improvements**
- **Real-time Monitoring**: Continuous data updates without user intervention
- **Better Error Handling**: Improved error messages and recovery
- **Optimized Performance**: Reduced UI complexity for better responsiveness
- **Clean Code**: Removed unused parameters and simplified function signatures

## 🚀 **Working Features**

### ✅ **Core Functionality**
- **Authentication**: Secure login with credential storage
- **Real-time Dashboard**: Live system status and node monitoring
- **VM Management**: Start, stop, delete virtual machines with real-time updates
- **LXC Container Management**: Full container lifecycle management with clean UI
- **Task Monitoring**: Real-time task statistics and management
- **Settings**: Comprehensive configuration options
- **Navigation**: Smooth navigation between all screens

### ✅ **Real-time Updates**
- **Dashboard**: Auto-refresh every 30 seconds with live system metrics
- **VM List**: Real-time updates every 15 seconds with action progress indicators
- **Container List**: Live monitoring every 15 seconds with improved card design
- **Task Screen**: Continuous updates every 10 seconds with statistics
- **System Status**: Live CPU, Memory, and Uptime display

## 🛠 **Tech Stack**

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Clean Architecture
- **Networking**: Retrofit + OkHttp with SSL/TLS support
- **Security**: Android Keystore + EncryptedSharedPreferences
- **Dependency Injection**: Manual DI
- **Build System**: Gradle
- **Target API**: Android 8.0+ (API 26+)

## 📁 **Project Structure**

```
app/src/main/java/com/proxmoxmobile/
├── data/
│   ├── api/           # API interfaces and clients
│   ├── model/         # Data models
│   └── security/      # Secure storage
├── di/                # Dependency injection
├── presentation/
│   ├── screens/       # UI screens
│   │   ├── auth/      # Login screen
│   │   ├── dashboard/ # Main dashboard
│   │   ├── vms/       # VM management
│   │   ├── containers/# LXC management
│   │   ├── tasks/     # Task monitoring
│   │   └── settings/  # Configuration
│   ├── navigation/    # Navigation components
│   ├── theme/         # UI theming
│   └── viewmodel/     # ViewModels
└── ProxmoxApplication.kt
```

## ⚙️ **Configuration**

1. **Build the Project**:
   ```bash
   ./gradlew assembleDebug
   ```

2. **Install on Device**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Connect to Proxmox**:
   - Launch the app
   - Enter your Proxmox server details
   - Navigate the dashboard with real-time updates

## 🔧 **Recent Improvements**

### **Real-time Monitoring & Auto-refresh**
- ✅ Automatic data updates across all screens
- ✅ Live system status with real CPU/Memory data
- ✅ Continuous monitoring without manual intervention
- ✅ Optimized refresh intervals for different data types

### **Enhanced User Experience**
- ✅ Clean interface without cluttered refresh buttons
- ✅ Improved login screen scaling and responsiveness
- ✅ Modern container card design with better spacing
- ✅ Streamlined navigation with essential actions only

### **Improved Settings & Configuration**
- ✅ Comprehensive settings screen with various options
- ✅ Better credential management and security
- ✅ Enhanced configuration options for different use cases

### **Enhanced Task Management**
- ✅ Real-time task monitoring with statistics
- ✅ Live task status updates and management
- ✅ Improved task card design and functionality

### **Better Visual Design**
- ✅ Modern Material Design 3 implementation
- ✅ Consistent theming across all screens
- ✅ Improved typography and spacing
- ✅ Better color scheme and visual hierarchy

## 🐛 **Troubleshooting**

### **Common Issues**
- **Connection Failed**: Check server URL, port, and credentials
- **Authentication Error**: Verify username, password, and realm
- **No Data**: Ensure Proxmox server is running and accessible
- **Real-time Updates**: Data refreshes automatically - no manual refresh needed

### **Performance**
- **Slow Loading**: Check network connection and server response time
- **High Battery Usage**: Real-time updates are optimized for efficiency
- **Memory Usage**: App uses efficient caching and data management

## 📋 **Roadmap**

### **Phase 1: Core Features** ✅
- [x] Authentication and secure credential storage
- [x] Dashboard with real-time system status
- [x] VM management (start, stop, delete)
- [x] LXC container management
- [x] Task monitoring and statistics
- [x] Real-time monitoring and auto-refresh

### **Phase 2: Advanced Management** ✅
- [x] Enhanced task monitoring with statistics
- [x] Improved settings and configuration
- [x] Better error handling and user feedback
- [x] Clean UI design and real-time updates

### **Phase 3: Enhanced Features** 🚧
- [ ] Console access for VMs and containers
- [ ] Backup management and scheduling
- [ ] Storage management and monitoring
- [ ] Network interface management
- [ ] User management and permissions
- [ ] Cluster management features

### **Phase 4: Enterprise Features** 📋
- [ ] Multi-server management
- [ ] Advanced monitoring and alerts
- [ ] Backup and restore operations
- [ ] Performance analytics
- [ ] Custom dashboard widgets
- [ ] API rate limiting and optimization

## 🤝 **Contributing**

This project is actively developed. Contributions are welcome!

## 📄 **License**

This project is licensed under the MIT License - see the LICENSE file for details. 