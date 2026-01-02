# Data Model: Battery Voltage Monitor

**Date**: 2026-01-02
**Feature**: 001-battery-voltage-monitor

## Overview

This document defines the data entities, their attributes, relationships, and state transitions for the Battery Voltage Monitor application.

---

## Entities

### 1. BatteryReading

**Description**: Represents a single battery state snapshot from the OS.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `voltageMillivolts` | `Int` | Raw voltage from BatteryManager | 0..10000 (0V-10V range) |
| `chargingStatus` | `ChargingStatus` | Current charging state | Enum value |
| `pluggedType` | `PluggedType` | Power source type | Enum value |
| `timestamp` | `Long` | Reading timestamp (epochMillis) | > 0 |

**Computed Properties:**
- `voltageVolts: Float` = `voltageMillivolts / 1000f` (e.g., 4200 → 4.2)
- `isCharging: Boolean` = `chargingStatus == CHARGING || chargingStatus == FULL`
- `isValid: Boolean` = `voltageMillivolts > 0 && voltageMillivolts < 10000`

**Source**: `Intent.ACTION_BATTERY_CHANGED` broadcast

---

### 2. ChargingStatus (Enum)

**Description**: Possible charging states reported by Android BatteryManager.

| Value | BatteryManager Constant | User Display |
|-------|------------------------|--------------|
| `UNKNOWN` | `BATTERY_STATUS_UNKNOWN` | "Unknown" |
| `CHARGING` | `BATTERY_STATUS_CHARGING` | "Charging" |
| `DISCHARGING` | `BATTERY_STATUS_DISCHARGING` | "Not Charging" |
| `NOT_CHARGING` | `BATTERY_STATUS_NOT_CHARGING` | "Not Charging" |
| `FULL` | `BATTERY_STATUS_FULL` | "Full" |

---

### 3. PluggedType (Enum)

**Description**: Power source types when device is plugged in.

| Value | BatteryManager Constant | Description |
|-------|------------------------|-------------|
| `NONE` | `0` | On battery |
| `AC` | `BATTERY_PLUGGED_AC` | AC charger |
| `USB` | `BATTERY_PLUGGED_USB` | USB port |
| `WIRELESS` | `BATTERY_PLUGGED_WIRELESS` | Wireless charging |

---

### 4. ThresholdSetting

**Description**: User-configured voltage threshold for warnings.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `thresholdVolts` | `Float` | Warning threshold | 3.50..4.50 |

**Constants:**
- `DEFAULT_THRESHOLD` = `4.00f`
- `MIN_THRESHOLD` = `3.50f`
- `MAX_THRESHOLD` = `4.50f`
- `APPROACHING_OFFSET` = `0.05f` (warning triggers at threshold - 0.05V)

**Persistence**: SharedPreferences key `"threshold_voltage"`

---

### 5. WarningLevel (Enum)

**Description**: Derived warning state based on current voltage vs threshold.

| Value | Condition | Visual Style |
|-------|-----------|--------------|
| `NONE` | `voltage < threshold - 0.05` | Normal (default text color) |
| `APPROACHING` | `threshold - 0.05 <= voltage < threshold` | Orange/amber styling |
| `REACHED` | `voltage >= threshold` | Red styling, alert message |

**Derivation Formula:**
```kotlin
fun calculateWarningLevel(voltage: Float, threshold: Float): WarningLevel {
    return when {
        voltage >= threshold -> WarningLevel.REACHED
        voltage >= threshold - APPROACHING_OFFSET -> WarningLevel.APPROACHING
        else -> WarningLevel.NONE
    }
}
```

---

### 6. BatteryUiState

**Description**: Aggregate UI state representing everything needed to render the main screen.

| Field | Type | Description |
|-------|------|-------------|
| `voltageVolts` | `Float` | Current voltage (formatted for display) |
| `voltageDisplay` | `String` | Formatted string, e.g., "4.05 V" |
| `isCharging` | `Boolean` | Whether device is charging |
| `chargingStatusDisplay` | `String` | "Charging" or "Not Charging" |
| `thresholdVolts` | `Float` | Current threshold setting |
| `warningLevel` | `WarningLevel` | Current warning state |
| `warningMessage` | `String?` | Optional warning text |
| `isValid` | `Boolean` | Whether voltage reading is valid |

**Immutability**: This class should be annotated with `@Immutable` for Compose optimization.

---

## Relationships

```
┌─────────────────┐
│  BatteryReading │◄──── Updated every ~1 second from OS broadcast
└────────┬────────┘
         │ derives
         ▼
┌─────────────────┐     ┌──────────────────┐
│  BatteryUiState │◄────│ ThresholdSetting │ (persisted)
└────────┬────────┘     └──────────────────┘
         │ contains
         ▼
┌─────────────────┐
│  WarningLevel   │ (computed from voltage + threshold)
└─────────────────┘
```

---

## State Transitions

### Warning Level State Machine

```
                    voltage < threshold - 0.05
         ┌─────────────────────────────────────────┐
         │                                         │
         ▼                                         │
    ┌─────────┐                               ┌────┴────┐
    │  NONE   │────voltage >= threshold - 0.05────▶│APPROACHING│
    └─────────┘                               └────┬────┘
         ▲                                         │
         │                                         │
         │    voltage < threshold - 0.05           │ voltage >= threshold
         │                                         │
         │                               ┌─────────▼─────────┐
         └───────────────────────────────│    REACHED        │
                 voltage < threshold     └───────────────────┘
```

### Charging Status Transitions

Charging status is entirely driven by OS broadcasts and is not controlled by the app. The app merely reflects the current state.

---

## Validation Rules

### Threshold Setting
1. Value MUST be >= 3.50V
2. Value MUST be <= 4.50V
3. Value MUST be stored with at least 2 decimal precision
4. On invalid/missing persistence, default to 4.00V

### Voltage Reading
1. Values <= 0 are considered invalid (display but don't trigger warnings)
2. Values > 10000mV (10V) are considered invalid
3. Invalid readings retain last known valid voltage for display

---

## Kotlin Data Classes

```kotlin
@Immutable
data class BatteryUiState(
    val voltageVolts: Float = 0f,
    val isCharging: Boolean = false,
    val chargingStatusDisplay: String = "Unknown",
    val thresholdVolts: Float = DEFAULT_THRESHOLD,
    val warningLevel: WarningLevel = WarningLevel.NONE,
    val isValid: Boolean = false
) {
    val voltageDisplay: String
        get() = "%.2f V".format(voltageVolts)

    val warningMessage: String?
        get() = when (warningLevel) {
            WarningLevel.NONE -> null
            WarningLevel.APPROACHING -> "Approaching threshold"
            WarningLevel.REACHED -> "Threshold reached - stop charging"
        }

    companion object {
        const val DEFAULT_THRESHOLD = 4.00f
        const val MIN_THRESHOLD = 3.50f
        const val MAX_THRESHOLD = 4.50f
        const val APPROACHING_OFFSET = 0.05f
    }
}

enum class ChargingStatus {
    UNKNOWN, CHARGING, DISCHARGING, NOT_CHARGING, FULL
}

enum class PluggedType {
    NONE, AC, USB, WIRELESS
}

enum class WarningLevel {
    NONE, APPROACHING, REACHED
}
```

---

## Persistence Schema

**SharedPreferences file**: `"battery_voltage_prefs"`

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `threshold_voltage` | Float | 4.00f | User's warning threshold |

**Migration**: Not required (single field, new app)
