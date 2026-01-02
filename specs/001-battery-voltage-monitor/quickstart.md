# Quickstart: Battery Voltage Monitor

**Date**: 2026-01-02
**Feature**: 001-battery-voltage-monitor

## Prerequisites

- **Android Studio**: Ladybug or newer (2024.2+)
- **JDK**: 17 or newer
- **Android SDK**: API 31+ (Android 12)
- **Device/Emulator**: Android 12+ device or emulator (physical Samsung device recommended for accurate voltage readings)

## Setup

### 1. Clone and Open Project

```bash
cd android-battery-voltage-tools
```

Open the project in Android Studio.

### 2. Gradle Sync

Android Studio should automatically sync Gradle. If not:
- **File** → **Sync Project with Gradle Files**
- Or run: `./gradlew build`

### 3. Build Debug APK

```bash
# From project root
./gradlew assembleDebug
```

Output APK location: `app/build/outputs/apk/debug/app-debug.apk`

### 4. Install on Device

**Via ADB:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Via Android Studio:**
- Select your device in the device dropdown
- Click **Run** (▶) or press `Shift+F10`

## Development Workflow

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests

```bash
# Requires connected device or emulator
./gradlew connectedAndroidTest
```

### Check Code Style

```bash
./gradlew ktlintCheck
```

### Build Release APK (unsigned)

```bash
./gradlew assembleRelease
```

## Project Structure Overview

```
app/src/main/java/com/example/batteryvoltage/
├── MainActivity.kt          # App entry point, hosts Compose UI
├── ui/
│   ├── theme/              # Material 3 theme (colors, typography)
│   └── components/         # Reusable Compose components
├── data/
│   └── ThresholdRepository.kt  # Persistence for threshold setting
└── service/
    └── BatteryMonitor.kt   # Battery broadcast receiver
```

## Key Dependencies

| Dependency | Purpose |
|------------|---------|
| `androidx.compose.material3` | Material 3 UI components |
| `androidx.lifecycle:lifecycle-runtime-compose` | `collectAsStateWithLifecycle()` |
| `androidx.activity:activity-compose` | Compose Activity integration |

## Testing on Device

### Verify Voltage Reading
1. Launch the app
2. Verify a voltage value appears (typically 3.6V-4.4V)
3. The value should update every ~1 second

### Test Charging Detection
1. With device unplugged, verify "Not Charging" status
2. Plug in device, verify "Charging" status appears

### Test Threshold Warnings
1. Set threshold to a value close to current voltage
2. Wait for voltage to approach/exceed threshold
3. Verify warning colors/messages appear

### Test Persistence
1. Adjust threshold slider to a new value
2. Force-close the app
3. Reopen and verify threshold persists

## Emulator Limitations

- Emulators may report artificial voltage values (often 0 or fixed)
- Use a physical device for accurate voltage testing
- Charging status typically works on emulator via power controls

## Troubleshooting

### Voltage shows 0 or invalid
- Normal on emulators; use physical device
- Check that device reports `EXTRA_VOLTAGE` in battery broadcast

### Build fails with SDK errors
- Ensure `compileSdk = 35` and `minSdk = 31` in `app/build.gradle.kts`
- Run **Tools** → **SDK Manager** to install required SDK platforms

### App crashes on launch
- Check Logcat for errors
- Ensure device is Android 12+ (API 31)
