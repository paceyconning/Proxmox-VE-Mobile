# Proxmox VE Mobile

A modern Android application for managing Proxmox Virtual Environment servers from your mobile device.

## 🚀 Current Status

### ✅ Completed Features
- **Project Setup & Architecture**: Complete Android project with modern architecture
- **Material Design 3**: Beautiful, modern UI with OLED dark mode support
- **Authentication System**: Secure login with encrypted credential storage
- **Network Security**: SSL/TLS support with development-friendly configuration
- **Dashboard**: Modern dashboard with system status widget (CPU, RAM, Uptime)
- **Navigation**: Bulletproof navigation system with proper state management
- **Manual Dependency Injection**: Clean, efficient dependency management
- **JDK Compatibility**: Fixed all Java/Kotlin version compatibility issues
- **LXC Containers**: Full LXC container management with list view and action buttons
- **Virtual Machines**: Complete VM management with list view and action buttons
- **Storage Management**: Storage pool monitoring and management
- **Network Management**: Network interface monitoring and status display
- **User Management**: User listing and details with status indicators
- **Task Monitoring**: Real-time task tracking with status and timing information
- **Backup Management**: Backup listing and details with format indicators
- **Error Handling**: Comprehensive error handling with user-friendly messages
- **Data Validation**: Robust data validation and filtering
- **Secure Storage**: AES256 encrypted credential storage using Android Keystore
- **Login Persistence**: Auto-fill login details with secure encryption
- **Crash Prevention**: Fixed all dashboard and navigation crashes

### 🔄 In Development
- Advanced Features - VM/container actions (start/stop/delete) - UI ready, API integration needed
- Network Interface Actions - Configure network interfaces
- User Management Actions - Create/edit/delete users
- Task Management Actions - Delete tasks and view details
- Backup Management Actions - Download/restore/delete backups

## 🛠 Tech Stack

- **Language**: Kotlin 1.9.21
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Clean Architecture principles
- **Networking**: Retrofit + OkHttp with SSL/TLS support
- **Security**: Android Keystore + EncryptedSharedPreferences
- **Dependency Injection**: Manual DI (clean and efficient)
- **Build System**: Gradle 8.2.0 with Android Gradle Plugin 8.2.0
- **Target SDK**: Android 34 (API 34)
- **Minimum SDK**: Android 7.0 (API 24)

## ✨ Features

### Working Features
- 🔐 **Secure Authentication**: Login with encrypted credential storage
- 📊 **Dashboard**: System status monitoring (CPU, RAM, Uptime)
- 🐳 **LXC Containers**: View and manage LXC containers with action buttons
- 🖥️ **Virtual Machines**: View and manage VMs with action buttons
- 💾 **Storage Management**: Monitor storage pools
- 🌐 **Network Management**: View network interfaces and their status
- 👥 **User Management**: View users and their details
- 📋 **Task Monitoring**: View running tasks and their status
- 💿 **Backup Management**: View backups and their details
- 🎨 **OLED Dark Mode**: Beautiful dark theme optimized for OLED screens
- 💾 **Login Persistence**: Auto-fill login details securely
- 🛡️ **Crash Prevention**: Robust error handling and validation

### In Development
- ⚡ **Advanced Actions**: Start/stop/delete operations for VMs and containers (UI ready, API integration needed)
- 🔧 **Network Interface Actions**: Configure network interfaces
- 👥 **User Management Actions**: Create/edit/delete users
- 📋 **Task Management Actions**: Delete tasks and view details
- 💿 **Backup Management Actions**: Download/restore/delete backups

## 📁 Project Structure

```
app/src/main/java/com/proxmoxmobile/
├── data/
│   ├── api/                 # API services and networking
│   ├── model/               # Data models
│   └── security/            # Secure storage implementation
├── presentation/
│   ├── screens/             # UI screens
│   │   ├── auth/           # Login screen
│   │   ├── dashboard/      # Main dashboard
│   │   ├── containers/     # LXC management
│   │   ├── vms/           # VM management
│   │   └── storage/       # Storage management
│   ├── navigation/         # Navigation components
│   ├── theme/             # UI theming
│   └── viewmodel/         # ViewModels
└── ProxmoxApplication.kt   # Application class
```

## 🚀 Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/paceyconning/Proxmox-VE-Mobile.git
   cd Proxmox-VE-Mobile
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Build and Run**:
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use `./gradlew assembleDebug`

## ⚙️ Configuration

1. **Launch the app** and you'll see the login screen
2. **Enter your Proxmox server details**:
   - Host: Your Proxmox server IP/hostname
   - Port: Usually 8006 (default)
   - Username: Your Proxmox username
   - Password: Your Proxmox password
   - Realm: Usually "pam" (default)
   - Use HTTPS: Check if your server uses HTTPS
3. **Check "Save login details (encrypted)"** to securely save credentials
4. **Tap "Connect to Proxmox"** to authenticate
5. **Navigate the dashboard** to access different features

## 🔒 Security Features

- **Encrypted Storage**: All saved credentials are encrypted using AES256
- **Android Keystore**: Uses Android's secure hardware-backed keystore
- **No Plain Text**: Passwords are never stored in plain text
- **Secure Network**: SSL/TLS encryption for all API communications

## 🎨 Recent Improvements

### Stability & Crash Prevention
- ✅ Fixed dashboard crashes after login
- ✅ Resolved LXC container screen crashes
- ✅ Improved error handling and validation
- ✅ Enhanced navigation stability

### User Experience
- ✅ Added secure password storage with encryption
- ✅ Implemented auto-fill login functionality
- ✅ Created beautiful OLED dark mode
- ✅ Added system status widget (CPU, RAM, Uptime)
- ✅ Prioritized LXC containers over VMs in dashboard

### Code Quality
- ✅ Simplified theme configuration
- ✅ Fixed build system issues
- ✅ Removed problematic dependencies
- ✅ Enhanced data validation and filtering

## 🐛 Troubleshooting

### Build Issues
- **JDK Version**: Ensure you're using Java 17
- **Gradle Sync**: Try "File > Invalidate Caches and Restart"
- **Clean Build**: Run `./gradlew clean` before building

### Runtime Issues
- **Network**: Check your Proxmox server is accessible
- **Authentication**: Verify username/password and realm
- **SSL**: For self-signed certificates, the app handles this automatically

### Common Solutions
- **App Crashes**: The app now has robust crash prevention
- **Login Issues**: Try clearing saved credentials and re-entering
- **Data Loading**: Check network connectivity and server status

## 🗺️ Roadmap

### Phase 1: Core Features ✅
- [x] Project setup and architecture
- [x] Authentication system
- [x] Dashboard with system monitoring
- [x] LXC container management
- [x] Virtual machine management
- [x] Storage management
- [x] Secure credential storage

### Phase 2: Advanced Management 🚧
- [ ] Network interface monitoring
- [ ] User and permission management
- [ ] Task monitoring and management
- [ ] Backup creation and restoration
- [ ] Advanced VM/container operations

### Phase 3: Enhanced Features 📋
- [ ] Real-time monitoring
- [ ] Push notifications
- [ ] Widget support
- [ ] Multi-server management
- [ ] Advanced analytics

### Phase 4: Enterprise Features 🔮
- [ ] Role-based access control
- [ ] Audit logging
- [ ] Advanced security features
- [ ] Integration with enterprise systems

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Proxmox VE team for the excellent virtualization platform
- Android and Jetpack Compose teams for the amazing development tools
- The open-source community for inspiration and support

---

**Note**: This app is designed for managing Proxmox VE servers. Ensure you have proper access permissions to your Proxmox server before using this application. 