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

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Networking**: Retrofit + OkHttp
- **Database**: Room for local caching
- **Dependency Injection**: Hilt
- **Async Programming**: Coroutines + Flow
- **Image Loading**: Coil
- **Build System**: Gradle with Kotlin DSL

## Project Structure

```
app/
├── src/main/
│   ├── java/com/proxmoxmobile/
│   │   ├── data/           # Data layer (API, database, repositories)
│   │   ├── domain/         # Domain layer (use cases, models)
│   │   ├── presentation/   # UI layer (screens, view models)
│   │   └── di/            # Dependency injection
│   └── res/               # Resources
└── build.gradle.kts       # App-level build configuration

buildSrc/                  # Shared build logic
gradle/                    # Gradle wrapper
build.gradle.kts          # Project-level build configuration
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 34 (API level 34)
- Kotlin 1.9.0 or later
- JDK 17 or later

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/proxmox-ve-mobile.git
   cd proxmox-ve-mobile
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the app on your device or emulator

### Configuration

1. Add your Proxmox VE server details in the app settings
2. Configure authentication (API token or username/password)
3. Test the connection to ensure proper access

## Development

### Architecture

The app follows Clean Architecture principles with three main layers:

- **Presentation Layer**: UI components and ViewModels
- **Domain Layer**: Business logic and use cases
- **Data Layer**: API calls, database operations, and data sources

### Key Components

- **ProxmoxApiService**: Handles all API communication with Proxmox VE
- **ProxmoxRepository**: Central data access point
- **Use Cases**: Business logic for specific features
- **ViewModels**: UI state management
- **Compose Screens**: Modern declarative UI

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

- [ ] Initial project setup and basic structure
- [ ] Authentication and server management
- [ ] Dashboard and overview screens
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