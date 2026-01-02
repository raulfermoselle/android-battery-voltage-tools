# Research: Battery Voltage Monitor

**Date**: 2026-01-02
**Feature**: 001-battery-voltage-monitor

## Overview

This document captures research findings for implementing a real-time battery voltage monitor on Android 12+ (API 31) targeting Samsung devices.

---

## 1. Android Battery Voltage API

### Decision: Use `Intent.ACTION_BATTERY_CHANGED` sticky broadcast

### Rationale

The standard Android approach uses the `ACTION_BATTERY_CHANGED` sticky broadcast from the system. This is a protected system broadcast that fires whenever battery conditions change.

**Key BatteryManager extras:**
- `EXTRA_VOLTAGE` - Current battery voltage in **millivolts** (e.g., 4200 = 4.2V)
- `EXTRA_STATUS` - Charging status (CHARGING, DISCHARGING, NOT_CHARGING, FULL, UNKNOWN)
- `EXTRA_PLUGGED` - Power source (AC, USB, WIRELESS, or 0 for battery)
- `EXTRA_LEVEL` / `EXTRA_SCALE` - Battery percentage

### Alternatives Considered

1. **BatteryManager.getIntProperty()** - Requires API level-specific handling, less universal
2. **Polling via IntentFilter** - More complex, no benefit over sticky broadcast
3. **Third-party battery libraries** - Unnecessary dependency for simple use case

### Implementation Notes

**One-time query (recommended for instant reads):**
```kotlin
val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
val batteryStatus: Intent? = context.registerReceiver(null, filter)
val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
```

**Continuous monitoring:**
```kotlin
// Register in onStart(), unregister in onStop()
// onStart/onStop pair supports multi-window mode
val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
registerReceiver(batteryReceiver, filter)
```

**Android 14+ Note:** System broadcasts like `ACTION_BATTERY_CHANGED` are exempt from the `RECEIVER_EXPORTED` flag requirement.

---

## 2. Samsung-Specific Considerations

### Decision: Use standard Android API, display values as-is

### Rationale

Samsung devices use standard BatteryManager API and report voltage consistently. However:

1. **Voltage accuracy varies** - Different fuel gauge chips produce different precision
2. **Voltage-to-SOC is non-linear** - Don't use voltage alone for percentage estimation
3. **Degraded batteries show higher voltage** - As capacity decreases, voltage at same SOC increases
4. **Some firmware returns 0 for battery current** - Only use documented voltage field

### Alternatives Considered

1. **Samsung Health SDK** - Not publicly available, requires partnership
2. **Samsung diagnostic codes (`*#0228#`)** - Not programmatically accessible
3. **Custom voltage curves** - Too complex for informational display

### Implementation Notes

- Test on actual Samsung devices to validate expected voltage ranges (typically 3.6V-4.4V)
- Display the OS-reported value without adjustment
- Don't rely on voltage for SOC estimation

---

## 3. Jetpack Compose State Management

### Decision: ViewModel + StateFlow + collectAsStateWithLifecycle()

### Rationale

Current best practices (2025-2026) recommend:
- **StateFlow in ViewModel** for business logic state
- **collectAsStateWithLifecycle()** for lifecycle-aware collection (stops in background)
- **Single source of truth** with unidirectional data flow

### Alternatives Considered

1. **mutableStateOf in Composables** - Doesn't survive configuration changes
2. **LiveData** - Older pattern, StateFlow is preferred for Kotlin
3. **remember/rememberSaveable** - Only for ephemeral UI state

### Implementation Notes

```kotlin
// ViewModel
private val _batteryState = MutableStateFlow(BatteryUiState())
val batteryState: StateFlow<BatteryUiState> = _batteryState.asStateFlow()

// Composable
val uiState by viewModel.batteryState.collectAsStateWithLifecycle()
```

**Dependency required:**
```kotlin
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
```

---

## 4. Periodic Updates (~1Hz)

### Decision: LaunchedEffect with delay() controlled by lifecycle

### Rationale

For ~1Hz updates that respect lifecycle:
- Use `LaunchedEffect` with a `while (isActive)` loop
- Combine with `collectAsStateWithLifecycle()` to automatically pause in background
- Keep timer logic in ViewModel for testability

### Alternatives Considered

1. **Handler.postDelayed** - Legacy pattern, not coroutine-aware
2. **Timer/ScheduledExecutorService** - Java patterns, harder to cancel
3. **WorkManager** - Overkill for foreground-only updates

### Implementation Notes

```kotlin
// In ViewModel
fun startMonitoring() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
        while (isActive) {
            val voltage = readBatteryVoltage()
            _batteryState.update { it.copy(voltage = voltage) }
            delay(1000L)
        }
    }
}
```

---

## 5. Dark Mode Support

### Decision: Material 3 with system theme detection

### Rationale

- Use `isSystemInDarkTheme()` to follow system preference
- Define light and dark color schemes in theme
- Support Dynamic Color on Android 12+ with fallback

### Alternatives Considered

1. **Manual theme toggle** - Out of scope per spec (follow system only)
2. **Material 2** - Deprecated, M3 is current standard
3. **Custom theming system** - Unnecessary complexity

### Implementation Notes

```kotlin
@Composable
fun BatteryVoltageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

---

## 6. Data Persistence (Threshold Setting)

### Decision: SharedPreferences via Preferences DataStore

### Rationale

For a single persistent value (threshold voltage):
- SharedPreferences is simple and sufficient
- Modern approach uses Preferences DataStore (coroutine-based wrapper)
- No need for Room database for a single value

### Alternatives Considered

1. **Room Database** - Overkill for single value
2. **Plain SharedPreferences** - Works but DataStore is recommended
3. **File storage** - Unnecessary complexity

### Implementation Notes

```kotlin
// Simple SharedPreferences approach (sufficient for single value)
val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
val threshold = prefs.getFloat("threshold", 4.0f)
prefs.edit().putFloat("threshold", newValue).apply()
```

---

## Summary

| Topic | Decision |
|-------|----------|
| Battery API | `ACTION_BATTERY_CHANGED` sticky broadcast |
| Voltage units | Millivolts from API, display as volts (÷1000) |
| Charging status | `EXTRA_STATUS` and `EXTRA_PLUGGED` from same broadcast |
| Lifecycle | Register in `onStart()`, unregister in `onStop()` |
| State management | ViewModel + StateFlow + `collectAsStateWithLifecycle()` |
| Periodic updates | `LaunchedEffect` with `delay(1000L)` |
| Dark mode | Material 3 + `isSystemInDarkTheme()` |
| Persistence | SharedPreferences for threshold |
