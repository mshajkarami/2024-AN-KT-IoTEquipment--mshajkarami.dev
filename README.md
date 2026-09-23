# Industrial IoT Equipment & Maintenance Management System (Android)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_14_%28API_34%29-green.svg?style=flat&logo=android)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture%20%2B%20MVVM-blue.svg)](https://developer.android.com/topic/architecture)
[![Hilt](https://img.shields.io/badge/DI-Dagger_Hilt-orange.svg)](https://dagger.dev/hilt/)
[![Jetpack](https://img.shields.io/badge/Android%20Jetpack-Navigation%20%7C%20Room%20%7C%20DataBinding-blueviolet.svg)](https://developer.android.com/jetpack)
[![ML Kit](https://img.shields.io/badge/Vision-Google_ML_Kit_%2B_CameraX-brightgreen.svg)](https://developers.google.com/ml-kit)

An enterprise-grade, offline-capable Android application developed for industrial facilities to catalog, track, inspect, and maintain IoT devices, PLCs, industrial controllers, sensors, and field equipment. 

Designed for field engineers, maintenance supervisors, and systems administrators, the app integrates real-time hardware QR/Barcode scanning, comprehensive Bill of Materials (LOM) inspection, maintenance log tracking with Persian/Gregorian date pickers, technical document downloads, and Role-Based Access Control (RBAC).

---

## 📑 Table of Contents
- [Executive Overview](#-executive-overview)
- [Key Features & Capabilities](#-key-features--capabilities)
- [Architecture & Design Patterns](#-architecture--design-patterns)
- [Application Flow & Screen Inventory](#-application-flow--screen-inventory)
- [Codebase Metrics & Statistics](#-codebase-metrics--statistics)
- [Technology Stack & Third-Party Libraries](#-technology-stack--third-party-libraries)
- [Authentication & Security](#-authentication--security)
- [Data Models & Schema](#-data-models--schema)
- [Engineering Highlights & Problem Solving](#-engineering-highlights--problem-solving)

---

## 🎯 Executive Overview

In large industrial plants, factories, and utility facilities, thousands of IoT instruments and controllers are distributed across remote cabinets, field panels, and machinery. Maintaining these physical assets requires rapid on-site identification, access to wiring schematics and user manuals, historical maintenance tracking, and strict role permissions.

This system was engineered as a centralized mobile hub for plant technicians to:
1. **Identify Hardware Instantly**: Point the camera at any physical asset's QR code or barcode to pull up its live identity, production history, and specifications.
2. **Access Deep Technical Specs**: Inspect hardware revisions, firmware versions, and sub-component Bills of Materials (LOM).
3. **Log Field Maintenance & Repairs**: Create, edit, and timestamp work orders and maintenance records directly on the plant floor.
4. **Manage Technical Documentation**: Download and view wiring diagrams, technical data sheets, and calibration reports even when working disconnected from central networks.
5. **Operate in Dual Locales**: Seamlessly toggle between English (LTR) and Persian (RTL) interfaces with automatic Gregorian-to-Jalali date conversions.

---

## ✨ Key Features & Capabilities

### 1. 🔍 Asset Discovery & Real-Time Search Catalog
- **Multi-parameter Search**: Search equipment by Unique Identifier (e.g., `EQ-PLC-101`), model name, manufacturer, or technical description.
- **Auto-Refresh & Polling**: Background coroutine-powered polling updates equipment statuses automatically while conserving battery.
- **Skeleton Shimmer Loading**: Polished shimmer skeleton cards provide visual feedback while data loads.
- **Swipe-to-Refresh**: Native pull-down refresh gesture across list views.

### 2. 📷 High-Speed QR Code & Barcode Scanner
- **CameraX + Google ML Kit**: Embedded computer vision pipeline that processes frames in real time with high accuracy, even in low-light industrial environments.
- **Gallery & Manual Scan**: Supports scanning from saved camera roll images as well as live camera frames.
- **Direct Navigation**: Auto-resolves scanned barcodes to the corresponding equipment detail screen without extra clicks.

### 3. ⚙️ Detailed Equipment Specifications & Bill of Materials (LOM)
- **Modular Hardware Profile**: Displays equipment title, GUID, production timestamp, firmware version, and device type badge.
- **Horizontal LOM Component Grid**: Interactive 2-row grid displaying sub-assemblies, spare parts, quantities, and suppliers (e.g., Siemens CPU modules, Phoenix Contact power supplies).
- **Responsive Media Header**: Dynamic image display with Coil caching, crossfade effects, and fallback placeholder vectors.

### 4. 📝 Maintenance Work Orders & Lifecycle Records
- **Chronological Audit Trail**: Full history of maintenance interventions, safety checks, firmware upgrades, and calibration procedures.
- **Add / Edit Work Orders**: Field technicians can file new service reports with custom descriptions, technician signatures, and file attachments.
- **Bilingual Date Handling**: Integrated Material Date Picker paired with a Jalali (Persian) calendar converter (`PersianDate`), displaying dates accurately based on the active locale.
- **Log Sharing & Export**: Fast sharing of maintenance summaries to clipboard or messaging apps.

### 5. 📂 Document & Attachment Management
- **File Format Recognition**: Visual file badges and MIME type mapping for `.pdf`, `.docx`, `.xlsx`, and images.
- **Local File Downloader**: Background file downloader with Android `FileProvider` integration, allowing direct opening in external PDF/Office viewers.
- **Batch Actions**: Top-bar action menu support for downloading all associated equipment manuals or sharing attachments.

### 6. 👤 Role-Based Access Control (RBAC) & Security
- **Role Permissions**: Categorizes users into `ADMIN`, `ENGINEER`, `USER1`, and `USER2`.
- **Conditional UI Gating**: Action buttons (such as "Add Maintenance Log" or "Edit Record") dynamically show or hide based on the user's role privileges.
- **Secure Storage**: Tokens and sensitive preferences encrypted via `EncryptedSharedPreferences` with AES256 encryption.

### 7. 🌐 Dual-Locale & Dynamic Theming
- **Dynamic RTL/LTR Switching**: Full Right-to-Left (Persian) and Left-to-Right (English) support without app restart glitches.
- **Theme Engine**: Dark mode and Light mode toggles using Material Design 3 color palettes.

---

## 🏛 Architecture & Design Patterns

The application is structured following **Modern Android Architecture (Clean Architecture + MVVM)** principles recommended by Google:

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│   Activities  │  Fragments  │  ViewModels  │  Adapters │
│       (Jetpack Navigation, ViewBinding, DataBinding)   │
└───────────────────────────┬────────────────────────────┘
                            │ observes LiveData / State
┌───────────────────────────▼────────────────────────────┐
│                      Domain Layer                      │
│     RoleViewModel  │  UserRole  │  Type Converters     │
└───────────────────────────┬────────────────────────────┘
                            │ requests data
┌───────────────────────────▼────────────────────────────┐
│                       Data Layer                       │
│  EquipmentRepository  │  AuthRepository  │  AppAuth    │
│  ─────────────┬──────────────────────────┬───────────  │
│               │                          │             │
│       ┌───────▼───────┐          ┌───────▼───────┐     │
│       │  Local Cache  │          │ Remote / Mock │     │
│       │  Room DB      │          │ Retrofit /    │     │
│       │  EncryptedPref│          │ Mock Datasets │     │
│       └───────────────┘          └───────────────┘     │
└────────────────────────────────────────────────────────┘
```

### Architectural Principles:
1. **Separation of Concerns**: UI components (`Fragments`, `Activities`) only render state and capture user events. All business logic is encapsulated in `ViewModels`.
2. **Repository Pattern**: Repositories (`EquipmentRepository`, `AuthRepository`) act as the single source of truth, abstracting data sources and local caching from view models.
3. **Dependency Injection**: Powered by **Dagger Hilt**. Modules (`NetworkModule`, `DatabaseModule`, `AuthModule`) supply singleton dependencies across the application lifecycle.
4. **Reactive UI State**: Utilizes Android Architecture Components (`LiveData`, `MediatorLiveData`, Coroutine `viewModelScope` with `Dispatchers.IO` and `Dispatchers.Default`) for asynchronous, thread-safe data flow.
5. **Assisted Injection Factories**: Parameterized view models (like `DetailViewModel` and `AttachmentsViewModel` requiring dynamic `equipmentId` or `recordId`) leverage Dagger `@AssistedInject` and `@AssistedFactory`.

---

## 📱 Application Flow & Screen Inventory

The project features **4 Activities** and **17 Distinct Screen Components (Fragments, Bottom Sheets, Dialogs)**:

| Screen / Component | Type | Responsibility & Features |
|---|---|---|
| **Splash Screen** | `SplashFragment` | Initial app loader; verifies token existence and routes to Auth, Onboarding, or Main. |
| **Authentication** | `AuthFragment` | Branded login portal; handles OAuth 2.0 OpenID Connect authentication. |
| **Onboarding: Welcome** | `WelcomeFragment` | Slide 1 introducing the platform's core asset tracking value proposition. |
| **Onboarding: Features & Theme** | `FeaturesThemeFragment` | Slide 2 showcasing feature highlights with an interactive theme preference switch. |
| **Onboarding: Get Started** | `GetStartedFragment` | Slide 3 personalizing the greeting with the user's name and launching the app. |
| **Home Dashboard** | `HomeFragment` | Quick launchpad with a prominent search bar and instant QR scanner trigger. |
| **Search & Catalog** | `SearchFragment` | Full inventory catalog with real-time filtering, shimmer loading, and auto-refresh. |
| **Equipment Master Detail** | `DetailFragment` | Hosts `ViewPager2` and `TabLayoutMediator` managing sub-tabs for specs and logs. |
| **Equipment Specs Tab** | `DetailEquipmentFragment` | Displays controller details, version specs, and 2-row horizontal LOM grid. |
| **Equipment Logs Tab** | `LogListFragment` | Chronological list of service logs with Jalali date formatting and status tags. |
| **Log Detail** | `LogDetailFragment` | Detailed inspection of a specific maintenance log with Edit, Share, and Print. |
| **Add / Edit Log Form** | `AddLogFragment` | Form with text input, live countdown character counter, date picker, and file uploader. |
| **Attachments Browser** | `AttachmentsFragment` | Grid/list of manuals, schematics, and test sheets with download state indicators. |
| **Settings & Profile** | `SettingsFragment` | Displays user details, online badge, language switcher, and theme settings. |
| **QR Camera Scanner** | `QrScannerActivity` | Fullscreen CameraX barcode scanner with real-time bounding box recognition. |
| **Language Modal** | `LanguageBottomSheet` | Bottom sheet for dynamic English/Persian language selection. |
| **QR Code Result Modal** | `QRCodeBottomDialog` | Pops up upon barcode recognition to confirm device ID before navigating. |
| **Offline Alert Modal** | `NoInternetBottomSheet` | Graceful connectivity loss dialog that auto-dismisses on reconnection. |
| **About Screen** | `AboutFragment` | Application version, release notes, and organizational information. |
| **Support Screen** | `SupportFragment` | Technical support contact details and direct helpdesk links. |
| **System Messages** | `MessagesFragment` | System notices and broadcast alerts for field operators. |

---

## 📊 Codebase Metrics & Statistics

Accurate metrics calculated from the project repository:

```
┌────────────────────────────────────────────────────────┐
│              CODEBASE METRICS BREAKDOWN                │
├────────────────────────────┬───────────┬───────────────┤
│ Component                  │ Files     │ Lines of Code │
├────────────────────────────┼───────────┼───────────────┤
│ Kotlin Source Code (.kt)   │ 89 files  │ 4,663 LOC     │
│ XML Layouts & Resources    │ 132 files │ 5,444 LOC     │
├────────────────────────────┼───────────┼───────────────┤
│ TOTAL CODEBASE             │ 221 files │ 10,107 LOC    │
└────────────────────────────┴───────────┴───────────────┘
```

- **Architecture Layer Breakdown**:
  - `ui/`: 36 Kotlin files (Activities, Fragments, ViewModels, Adapters)
  - `data/`: 28 Kotlin files (Models, Remote APIs, Local Room DAOs, Repositories)
  - `di/`: 4 Kotlin files (Hilt Modules & Application class)
  - `domain/`: 3 Kotlin files (Role models, converters, RBAC logic)
  - `utils/`: 8 Kotlin files (Networking, Image Analysis, File I/O, Localization)

---

## 🛠 Technology Stack & Third-Party Libraries

### Core Platform & Languages
- **Kotlin 2.0.21**: Modern idiomatic Kotlin utilizing Coroutines, Extension Functions, and Type-safe Builders.
- **Android SDK (API 26 – API 34)**: Modern Android development targeting Android 14.
- **Java 21**: JDK runtime for Gradle 8.13 and Android Gradle Plugin 8.12.

### Android Jetpack
- **Jetpack Navigation Component**: Single-activity navigation graph (`main_nav_graph.xml`, `auth_nav_graph.xml`, `onboarding_nav_graph.xml`) with SafeArgs.
- **ViewBinding & DataBinding**: Eliminates `findViewById` boilerplate with type-safe XML bindings.
- **Room Database 2.6.1**: SQLite abstraction layer for caching user profiles and account records.
- **ViewModel & LiveData**: Lifecycle-aware state holders preventing memory leaks across configuration changes.
- **CameraX 1.4.1**: Lifecycle-bound camera pipeline for hardware scanning.

### Dependency Injection & Asynchronous Processing
- **Dagger Hilt 2.55**: Standardized dependency injection across Android framework classes.
- **Kotlin Coroutines**: Non-blocking asynchronous threading with `Dispatchers.IO` and `Dispatchers.Default`.

### Computer Vision & Media
- **Google ML Kit Barcode Scanning 17.3.0**: On-device machine vision for QR codes, Aztec codes, and industrial 1D/2D barcodes.
- **Coil 2.7.0**: Fast, lightweight Kotlin image loading library with disk caching and crossfade animations.
- **Lottie 6.6.3**: Vector animations for splash, search loaders, and visual micro-interactions.

### Networking & Security
- **Retrofit 3.0.0 & OkHttp 3**: REST API client with logging interceptors and authorization header injectors.
- **AppAuth-Android 0.11.1**: Industry-standard OAuth 2.0 / OpenID Connect client for enterprise SSO/Keycloak identity providers.
- **AndroidX Security Crypto 1.1.0-alpha06**: Encrypted hardware-backed SharedPreferences (`MasterKeys.AES256_GCM_SPEC`).

### Internationalization & Persian Calendar
- **PersianDate 1.7.1**: Accurate conversion between Unix epoch timestamps and Persian (Solar Hijri / Jalali) calendars.
- **Dynamic RTL Layouts**: Bidirectional layout mirroring with tailored strings and drawable assets (`values/` and `values-fa/`).

---

## 🔐 Authentication & Security

The system is architected for enterprise identity platforms:
1. **OAuth 2.0 & OpenID Connect (OIDC)**: Integrated with **Keycloak** identity servers using the Authorization Code Flow with PKCE (`iem` client ID).
2. **JWT Token Decomposition**: Parses JWT claims from the access token payload (Base64 URL decoded) to extract:
   - `preferred_username`, `email`, `name`
   - `realm_access.roles` and `resource_access.iem.roles`
3. **Role Mapping**: Translates raw JWT role strings directly into enum types:
   ```kotlin
   enum class UserRole { ADMIN, ENGINEER, USER1, USER2, UNKNOWN }
   ```
4. **Encrypted Persistence**: Auth tokens and refresh credentials are encrypted at rest using AES256-SIV and AES256-GCM.
5. **Offline Portfolio Mode**: Built-in mock authorization bypassing remote server timeouts, generating a fully authenticated Admin session for demonstration purposes.

---

## 💾 Data Models & Schema

The core domain entity is `Items` (Equipment), representing an industrial machine:

```kotlin
data class Items(
    val id: Int?,
    val title: String?,                     // e.g., "PLC Siemens S7-1500 Controller"
    val equipmentUniqueId: String?,         // e.g., "EQ-PLC-101"
    val description: String?,               // Detailed functional summary
    val productionDateTime: String?,        // ISO-8601 manufacturing timestamp
    val version: Version?,                  // Firmware revision & technical specs
    val equipmentType: EquipmentType?,      // Hardware classification & category
    val equipmentLOM: ArrayList<EquipmentLOM>, // Bill of Materials (components & vendors)
    val records: ArrayList<Records>,        // Maintenance logs & inspection audit trail
    val documents: ArrayList<Document>,     // Attached schematics, manuals, checklists
    val imageUrl: String?                   // Hardware photo URL / placeholder
)
```

---

## 💡 Engineering Highlights & Problem Solving

- **Hardware Camera Lifecycle Synchronization**: Integrated CameraX with an `ImageAnalysis.Analyzer` that safely disposes of unused image proxy frames, avoiding native memory buffer exhaustion.
- **Dual-Calendar Date Formatting Engine**: Solved the challenge of displaying timestamps across different international standards by creating a unified date formatter that seamlessly converts ISO-8601 timestamps to either Gregorian (`yyyy-MM-dd`) or Persian (`Y/m/d`) depending on the active locale.
- **Dynamic RTL Mirroring**: Programmatically computes layout direction (`View.LAYOUT_DIRECTION_RTL`) and swaps navigation chevrons and arrow directions so iconography matches text orientation.
- **Zero-Dependency Mock Data Engine**: Built a reactive, in-memory repository that supports full CRUD operations (adding logs, updating records, deleting logs, filtering by keyword or ID), enabling full demonstration capabilities independently of live backend services.

---

## 👨‍💻 Author & Project Context

- **Developer**: Saleh Hajkarami
- **Focus**: Native Android Development (Kotlin, Jetpack, Clean Architecture)
- **Organization & Context**: Developed for **Irisa Company (ایریسا)** as an enterprise IoT project during my internship period.
- **Use Case**: Portfolio Demonstration & Enterprise IoT Asset Management