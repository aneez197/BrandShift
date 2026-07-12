# BrandShift

**"Change Your App's Identity."**

BrandShift is a production-grade educational Android application that demonstrates how to dynamically change the launcher icon and launcher name of an application from within its own user interface, without requiring an update or reinstallation.

This showcase application is built using the latest modern Android development recommendations, implementing **Jetpack Compose (Material 3)**, **Clean Architecture**, and **Dependency Injection with Hilt**.

---

## Technical Overview: How Dynamic Icon Switching Works

On Android, application launcher profiles (shortcuts in the app drawer and home screen) are compiled from the `AndroidManifest.xml` at install time. Standard apps declare a single main activity containing the `LAUNCHER` intent filter:

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
```

To swap icons dynamically, BrandShift uses **Activity Aliases** (`<activity-alias>`). 

1. **MainActivity Configuration**: The base `MainActivity` is declared *without* any launcher intent filters. It is simply a container activity.
2. **Activity Aliases**: We register eight `<activity-alias>` elements. Each alias:
   - Targets `.MainActivity` (`android:targetActivity=".MainActivity"`).
   - Contains the launcher `intent-filter` declaration.
   - Points to a unique launcher icon drawable (`android:icon`) and title label (`android:label`).
3. **Default Launch**: Only one default alias (`MainActivityAliasBrandShift`) is enabled by default (`android:enabled="true"`). The remaining seven are disabled (`android:enabled="false"`).
4. **Programmatic Toggle**: When the user switches to a different profile, we use the `PackageManager` API:
   ```kotlin
   packageManager.setComponentEnabledSetting(
       ComponentName(packageName, targetAliasClassName),
       PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
       PackageManager.DONT_KILL_APP // or 0
   )
   ```
   We enable the target alias and disable all other remaining aliases to prevent multiple launcher shortcuts.

---

## Platform Limitations & OEM Quirks

While dynamic branding is powerful, engineers must account for specific platform behaviors:

* **App Closure / Process Kill**: When you enable/disable activity components, Android halts the current application process to update launcher registrations. In BrandShift, this is handled by warning the user via a confirmation dialog and then recycling the process (`Process.killProcess(Process.myPid())`) to force immediate updates.
* **Launcher Caching & Delays**: Different OEM system launchers (e.g. Samsung One UI, Xiaomi MIUI, Pixel Launcher) cache icons aggressively. While some launchers update the shortcut instantly, others may take up to 10 seconds or require navigating out/in of the home screen to refresh.
* **Shortcut Removal**: On older Android versions, disabling a running alias might remove the icon from the home screen grid, forcing the user to re-drag the shortcut from the app drawer.

---

## Features

- **Predefined Brand Profiles**: Cycle between eight distinct identities (BrandShift Rocket, DevX Programmer, GameBox, OfficeMate, Quick Notes, Calculator Hide Mode, Music Player, and Camera).
- **Adaptive Icon Architecture**: Fully supports modern Vector Adaptive Icons (background and foreground layers).
- **Interactive Mock Launcher Preview**: Preview visual updates inside a realistic, simulated Android phone mockup before committing changes.
- **Material 3 UI**: Full support for Dark Mode, dynamic Material You color matching, and adaptive grids.
- **Deep Clean Architecture**: Pure Kotlin domain models, isolated business use cases, data-layer repository caching (Jetpack DataStore), and clean dependency lookup.

---

## Directory Structure

```
com.aneez.brandshift/
├── core/
│   ├── designsystem/
│   │   ├── colors/        # Brand color assets & linear gradients
│   │   ├── typography/    # Type scales
│   │   ├── theme/         # Material 3 Theme wrapper
│   │   └── components/    # Reusable UI widgets (Toolbar, Cards, Buttons, Dialogs)
│   ├── datastore/         # Preferences key definitions
│   ├── dispatcher/        # Coroutine dispatcher abstractions
│   ├── common/            # State wrappers
│   ├── extensions/        # Context helper conveniences
│   └── utils/             # Launcher managers & package tools
├── feature/
│   ├── splash/            # App startup entry screen
│   ├── home/              # Active profile dashboard
│   ├── identities/        # Lazy vertical profiles grid
│   ├── preview/           # Interactive launcher mockup
│   ├── settings/          # Display toggles & reference links
│   └── about/             # Architectural specs & roadmaps
├── domain/
│   ├── model/             # Static domain entities
│   ├── repository/        # Repository contracts
│   └── usecase/           # Domain execution scripts
├── data/
│   ├── datasource/        # Jetpack DataStore preferences IO
│   └── repository/        # Repository implementations
└── di/                    # Dagger Hilt Modules
```

---

## Architecture Diagram

```
                  +-----------------------+
                  |  Presentation (UI)    |
                  |  (Compose, ViewModel) |
                  +-----------+-----------+
                              |
                              v
                  +-----------------------+
                  |      Domain Layer     |
                  |  (UseCases, Models)   |
                  +-----------+-----------+
                              |
                              | (via Interface)
                              v
                  +-----------------------+
                  |      Data Layer       |
                  | (Repository, DataSrc) |
                  +-----------+-----------+
                              |
               +--------------+--------------+
               v                             v
     +-------------------+         +-------------------+
     | Jetpack DataStore |         | PackageManager API|
     |  (User settings)  |         | (Activity Alias)  |
     +-------------------+         +-------------------+
```

---

## Tech Stack & Libraries

- **Kotlin**: Core language.
- **Jetpack Compose**: Declarative screen UI layouts.
- **Material Design 3**: System components.
- **Dagger Hilt**: Dependency Injection container.
- **Jetpack Navigation**: Compose screen routing.
- **Jetpack DataStore**: Persistent preference caching.
- **Kotlin Coroutines & Flow**: Reactive data streams.
- **JUnit & Mockito**: Test coverage.

---

## Future Roadmap

- [ ] **Custom Icon Uploads**: Allow users to load custom PNG/SVG files and map them as adaptive icon assets.
- [ ] **Widgets & Short Cuts**: Include interactive desktop widgets reflecting active brand layouts.
- [ ] **Remote Config Overrides**: Toggle seasonal launcher skins (e.g. Christmas/Halloween) dynamically from a remote dashboard.
- [ ] **Dynamic App Shortcuts**: Register pinned launcher actions matching active identity shortcuts.
- [ ] **Wear OS Sync**: Update launcher skins on paired smartwatch watchfaces.

---

## Installation & Launch

1. Clone or download the project folder.
2. Open the directory in **Android Studio (Ladybug or newer)**.
3. Let Gradle sync and download version catalog dependencies.
4. Run the project on an Android Emulator or physical device (Min SDK 24, Android 7.0+).

---

## License

```
Copyright 2026 BrandShift Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
