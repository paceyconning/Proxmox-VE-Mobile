# Proxmox VE Mobile

A modern, minimalist Android app for Proxmox VE that provides full access to all features available in the desktop web UI.

## Features

- **Complete Proxmox VE Management**: Access all features from the desktop web UI
- **Modern Material Design 3**: Clean, minimalist interface following Google's design guidelines
- **Real-time Monitoring**: Live system statistics and resource usage
- **VM Management**: Create, start, stop, pause, and manage virtual machines
- **Container Management**: Full LXC container support
- **Storage Management**: Configure and monitor storage pools
- **Network Management**: Network interface configuration
- **User Management**: User and permission management
- **Backup Management**: Backup and restore operations
- **Task Monitoring**: Real-time task progress and logs
- **Multi-server Support**: Manage multiple Proxmox servers
- **Offline Capabilities**: View cached data when offline
- **Dark/Light Theme**: Automatic theme switching based on system preferences

## Tech Stack

- **Language**: Kotlin 1.9.21
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Networking**: Retrofit + OkHttp with SSL/TLS support
- **Database**: Room for local caching (planned)
- **Dependency Injection**: Manual DI with AppContainer
- **Async Programming**: Coroutines + Flow
- **Image Loading**: Coil (planned)
- **Build System**: Gradle with Kotlin DSL
- **Android Gradle Plugin**: 8.2.0
- **Target SDK**: Android 34 (API level 34)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/proxmoxmobile/
│   │   ├── data/           # Data layer (API, models)
│   │   │   ├── api/        # ProxmoxApiService, AuthenticationService, ProxmoxApiClient
│   │   │   └── model/      # ProxmoxModels (data classes)
│   │   ├── di/            # Dependency injection (AppContainer)
│   │   └── presentation/   # UI layer (screens, view models, navigation)
│   │       ├── screens/    # All UI screens
│   │       ├── viewmodel/  # MainViewModel
│   │       ├── navigation/ # Navigation components
│   │       └── theme/      # Material3 theming
│   └── res/               # Resources
└── build.gradle.kts       # App-level build configuration

gradle/                    # Gradle wrapper and version catalog
build.gradle.kts          # Project-level build configuration
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 34 (API level 34)
- Kotlin 1.9.21
- JDK 17

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/paceyconning/Proxmox-VE-Mobile.git
   cd Proxmox-VE-Mobile
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the app on your device or emulator

### Configuration

1. Launch the app and enter your Proxmox VE server details:
   - **Host**: Your Proxmox server IP (e.g., `192.168.1.100`)
   - **Port**: Default is `8006` (or your custom port)
   - **Username**: Your Proxmox username (e.g., `root`)
   - **Password**: Your Proxmox password
   - **Realm**: Default is `pam` (or your custom realm)
   - **HTTPS**: Enable/disable based on your server configuration
2. Click "Connect to Proxmox" to authenticate
3. The dashboard will show your server status and available nodes

## Development

### Architecture

The app follows Clean Architecture principles with three main layers:

- **Presentation Layer**: UI components and ViewModels
- **Domain Layer**: Business logic and use cases (planned)
- **Data Layer**: API calls and data sources

### Key Components

- **AuthenticationService**: Handles Proxmox authentication with SSL/TLS support
- **ProxmoxApiService**: Defines all API endpoints for Proxmox VE
- **ProxmoxApiClient**: Creates authenticated API service instances
- **MainViewModel**: Central state management for authentication and data
- **AppContainer**: Manual dependency injection container
- **Compose Screens**: Modern declarative UI with Material Design 3

### Current Status

✅ **Completed Features:**
- Project setup with Android Gradle Plugin 8.2.0
- Material Design 3 theming and UI components
- Authentication system with SSL/TLS support
- Network security configuration for development
- Dashboard with real Proxmox data display
- Navigation system with all planned screens
- Manual dependency injection (replaced Hilt)
- JDK compatibility fixes and build optimization

🔄 **In Progress:**
- VM management screen implementation
- Container management screen implementation
- Storage and network management screens
- User management and permissions

📋 **Planned Features:**
- Real-time monitoring and notifications
- Offline capabilities with Room database
- Advanced features (clustering, HA)
- Performance optimizations
- Testing and quality assurance

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Proxmox VE team for the excellent virtualization platform
- Google for Material Design 3 and Jetpack Compose
- The Android developer community for inspiration and best practices

## Roadmap

- [x] Initial project setup and basic structure
- [x] Authentication and server management
- [x] Dashboard and overview screens
- [ ] VM management features
- [ ] Container management features
- [ ] Storage and network management
- [ ] User and permission management
- [ ] Backup and restore functionality
- [ ] Real-time monitoring and notifications
- [ ] Offline capabilities
- [ ] Advanced features (clustering, HA, etc.)
- [ ] Performance optimizations
- [ ] Testing and quality assurance
- [ ] Release preparation 