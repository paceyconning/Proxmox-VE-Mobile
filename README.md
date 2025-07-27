# Proxmox VE Mobile

A modern Android application for managing Proxmox Virtual Environment servers from your mobile device.

## 🚀 Current Status

✅ **Completed Features:**
- Project setup with Android Gradle Plugin 8.2.0
- Material Design 3 theming and UI components
- Authentication system with SSL/TLS support
- Network security configuration for development
- **Stable dashboard with SystemStatusCard widget**
- **Bulletproof navigation system with crash prevention**
- **Comprehensive error handling and null safety**
- **Robust data validation for all API responses**
- **Functional LXC container management screen**
- **Functional VM management screen**
- **Functional storage management screen**
- Manual dependency injection (replaced Hilt)
- JDK compatibility fixes and build optimization
- **Modern UI with LXC prioritization and improved UX**

🔄 **In Progress:**
- Network management screen implementation
- User management and permissions
- Task monitoring and management
- Backup management system

📋 **Planned Features:**
- Real-time monitoring and notifications
- Offline capabilities with Room database
- Advanced features (clustering, HA)
- Performance optimizations
- Testing and quality assurance

## 🛠️ Tech Stack

- **Language**: Kotlin 1.9.21
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with ViewModels
- **Networking**: Retrofit + OkHttp
- **Authentication**: Proxmox API with SSL/TLS support
- **Dependency Injection**: Manual DI (AppContainer)
- **Build System**: Gradle with Kotlin DSL
- **Android Gradle Plugin**: 8.2.0
- **Target SDK**: 34
- **Minimum SDK**: 24

## 📱 Features

### ✅ Working Features
- **Secure Authentication**: Login to Proxmox servers with username/password
- **Dashboard**: View system status, CPU, memory, and uptime
- **LXC Containers**: List and view container details (prioritized)
- **Virtual Machines**: List and view VM details
- **Storage Management**: View storage pools and usage
- **Node Management**: View and navigate between nodes
- **Modern UI**: Material Design 3 with improved UX
- **Error Handling**: Comprehensive error messages and recovery
- **Crash Prevention**: Bulletproof null safety and validation

### 🔄 In Development
- Network interface management
- User and permission management
- Task monitoring
- Backup management

## 🏗️ Project Structure

```
app/src/main/java/com/proxmoxmobile/
├── data/
│   ├── api/
│   │   ├── AuthenticationService.kt    # Authentication and API client
│   │   └── ProxmoxApiService.kt        # Proxmox API endpoints
│   └── model/
│       └── ProxmoxModels.kt            # Data models
├── di/
│   └── NetworkModule.kt                # Manual dependency injection
├── presentation/
│   ├── MainActivity.kt                 # Main activity
│   ├── navigation/
│   │   ├── ProxmoxNavHost.kt          # Navigation setup
│   │   └── Screen.kt                   # Screen definitions
│   ├── screens/
│   │   ├── auth/
│   │   │   └── LoginScreen.kt         # Authentication screen
│   │   ├── dashboard/
│   │   │   └── DashboardScreen.kt     # Main dashboard
│   │   ├── containers/
│   │   │   └── ContainerListScreen.kt # LXC container management
│   │   ├── vms/
│   │   │   └── VMListScreen.kt        # VM management
│   │   ├── storage/
│   │   │   └── StorageScreen.kt       # Storage management
│   │   └── [other screens...]
│   ├── theme/
│   │   ├── Theme.kt                   # Material 3 theme
│   │   └── Type.kt                    # Typography
│   └── viewmodel/
│       └── MainViewModel.kt            # Main view model
└── ProxmoxApplication.kt              # Application class
```

## 🚀 Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/paceyconning/Proxmox-VE-Mobile.git
   cd Proxmox-VE-Mobile
   ```

2. **Open in Android Studio:**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Build and run:**
   - Connect an Android device or start an emulator
   - Click the "Run" button or use `./gradlew assembleDebug`

## ⚙️ Configuration

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17 (required for AGP 8.2.0)
- Android device or emulator (API level 24+)

### Setup
1. **Build the app** using Android Studio or Gradle
2. **Install on device** via ADB or Android Studio
3. **Launch the app** and enter your Proxmox server details:
   - **Host**: Your Proxmox server IP/hostname
   - **Username**: Your Proxmox username
   - **Password**: Your Proxmox password
4. **Login** to access the dashboard and manage your infrastructure

### Network Configuration
The app includes network security configuration for development:
- Cleartext traffic enabled for HTTP connections
- SSL certificate validation disabled for development
- Custom network security policy

## 🔧 Recent Improvements

### Stability & Crash Prevention
- **Comprehensive null safety** throughout the codebase
- **Data validation** for all API responses
- **Try-catch blocks** around all navigation calls
- **Robust error handling** with specific HTTP status codes
- **Bulletproof UI components** with fallback values

### User Experience
- **SystemStatusCard widget** showing real-time system metrics
- **LXC containers prioritized** over VMs in the dashboard
- **Modern Material Design 3** interface
- **Improved error messages** with actionable feedback
- **Logout functionality** in dashboard
- **Better loading states** and progress indicators

### Code Quality
- **Detailed logging** for debugging and monitoring
- **Clean architecture** with separation of concerns
- **Consistent error handling** patterns
- **Type-safe navigation** with proper parameter passing
- **Optimized build configuration**

## 🐛 Troubleshooting

### Common Issues

**Build fails with JDK errors:**
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
./gradlew clean assembleDebug
```

**App crashes on login:**
- Check your Proxmox server credentials
- Ensure the server is accessible from your device
- Verify SSL/TLS configuration if using HTTPS

**Navigation crashes:**
- The app now includes comprehensive crash prevention
- All navigation calls are wrapped in try-catch blocks
- Check logs for specific error messages

### Debugging
Enable detailed logging:
```bash
adb logcat | grep -E "(proxmoxmobile|AuthenticationService|MainViewModel)"
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🗺️ Roadmap

### Phase 1: Core Infrastructure ✅
- [x] Project setup and configuration
- [x] Authentication system
- [x] Basic navigation
- [x] Dashboard with system status
- [x] LXC container management
- [x] VM management
- [x] Storage management

### Phase 2: Advanced Management 🔄
- [ ] Network interface management
- [ ] User and permission management
- [ ] Task monitoring and management
- [ ] Backup management system
- [ ] Cluster management

### Phase 3: Enhanced Features 📋
- [ ] Real-time monitoring
- [ ] Push notifications
- [ ] Offline capabilities
- [ ] Advanced VM/container actions
- [ ] Performance optimizations

### Phase 4: Production Ready 📋
- [ ] Comprehensive testing
- [ ] Security audit
- [ ] Performance optimization
- [ ] Documentation
- [ ] Release preparation

## 📞 Support

For support, please open an issue on GitHub or contact the development team.

---

**Note**: This app is designed for managing Proxmox VE servers. Ensure you have proper access permissions and follow your organization's security policies when connecting to production environments. 