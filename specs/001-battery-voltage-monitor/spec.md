# Feature Specification: Battery Voltage Monitor

**Feature Branch**: `001-battery-voltage-monitor`
**Created**: 2026-01-02
**Status**: Draft
**Input**: User description: "Battery Voltage Monitor - Android app to display OS-reported battery voltage in real time for manual charging decisions"

## Overview

A native Android application that displays the device's OS-reported battery voltage in near real-time, enabling users to make informed manual charging decisions. The primary use case is stopping a charge near a user-chosen threshold (~4.0V) to potentially extend battery longevity.

**Target Platform**: Native Android (Kotlin), Android 12+ (API 31)
**Target Devices**: Samsung phones
**Distribution**: Debug APK sideloaded manually (no Play Store)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Current Battery Voltage (Priority: P1)

As a user who wants to preserve battery health, I want to see my phone's current battery voltage displayed prominently so I can decide when to disconnect the charger.

**Why this priority**: This is the core value proposition of the app. Without voltage display, the app has no purpose. A user can derive value from just seeing the voltage number even without warnings.

**Independent Test**: Can be fully tested by launching the app and verifying the voltage readout updates and displays a plausible value (typically 3.6V-4.4V range).

**Acceptance Scenarios**:

1. **Given** the app is launched, **When** the main screen loads, **Then** the current battery voltage is displayed in volts with at least two decimal places (e.g., "3.85 V")
2. **Given** the app is in the foreground, **When** approximately 1 second passes, **Then** the voltage display updates to reflect the current OS-reported value
3. **Given** the app is in the foreground showing voltage, **When** the user views the screen, **Then** the voltage number is prominently displayed and easily readable

---

### User Story 2 - View Charging Status (Priority: P2)

As a user monitoring my charging, I want to see whether my phone is currently charging so I know the context of the voltage reading.

**Why this priority**: Charging status provides essential context for the voltage reading. A voltage of 4.0V while charging means something different than 4.0V while discharging.

**Independent Test**: Can be tested by launching the app while plugged in and unplugged, verifying the charging indicator changes appropriately.

**Acceptance Scenarios**:

1. **Given** the device is connected to a charger, **When** viewing the app, **Then** the display indicates "Charging" or similar status
2. **Given** the device is not connected to a charger, **When** viewing the app, **Then** the display indicates "Not Charging" or similar status
3. **Given** the device charging state changes, **When** the user plugs in or unplugs the charger, **Then** the charging status updates promptly

---

### User Story 3 - Configure Warning Threshold (Priority: P2)

As a user with specific battery health goals, I want to adjust the voltage threshold at which warnings appear so I can customize the app to my preferred charging cutoff point.

**Why this priority**: Different users have different preferences for when to stop charging (e.g., 4.0V, 4.1V, or 3.9V). Making the threshold adjustable transforms the app from a fixed-purpose tool into a flexible utility that serves diverse user needs.

**Independent Test**: Can be tested by adjusting the threshold value and verifying the displayed threshold updates and persists across app restarts.

**Acceptance Scenarios**:

1. **Given** the app is displaying voltage, **When** the user views the threshold slider, **Then** the current threshold value is visible
2. **Given** the user wants to change the threshold, **When** they drag the slider, **Then** the new threshold value is applied immediately
3. **Given** the user has set a custom threshold, **When** they close and reopen the app, **Then** the previously set threshold is restored
4. **Given** the user wants to reset to default, **When** they choose to reset, **Then** the threshold returns to the default value (4.00V)

---

### User Story 4 - Receive Threshold Warnings (Priority: P3)

As a user who wants to stop charging at my chosen voltage, I want to see visual warnings as voltage approaches and reaches my configured threshold so I know when to take action.

**Why this priority**: Warnings enhance usability but the app provides value even without them. Users could manually watch the voltage number, but warnings make it more convenient.

**Independent Test**: Can be tested by setting a threshold and observing the app while the battery charges past the warning and threshold values, verifying color/text changes occur.

**Acceptance Scenarios**:

1. **Given** the battery voltage is more than 0.05V below the user's threshold, **When** viewing the app, **Then** no warning is displayed and the voltage appears in normal styling
2. **Given** the battery voltage is within 0.05V below the user's threshold, **When** viewing the app, **Then** a visual warning is displayed (e.g., orange text, "Approaching threshold" message)
3. **Given** the battery voltage reaches or exceeds the user's threshold, **When** viewing the app, **Then** a strong visual warning is displayed (e.g., red text, "Threshold reached - stop charging" message)
4. **Given** a warning was showing but voltage drops below the threshold, **When** the voltage decreases (e.g., charger unplugged), **Then** the warning is removed and display returns to normal styling

---

### Edge Cases

- What happens when the OS reports an implausible voltage value (e.g., 0 or negative)?
  - Display the value as reported but do not trigger warnings for clearly invalid readings
- What happens when the battery broadcast is temporarily unavailable?
  - Display the last known value; do not crash or show error states for brief interruptions
- What happens when the app is backgrounded?
  - Stop updates; resume when foregrounded. No background monitoring or notifications.
- What happens on devices that report voltage differently or with less precision?
  - Display whatever the OS reports, as the app is informational only
- What happens if the user attempts to set a threshold outside the valid range?
  - Constrain the input to the valid range (3.50V-4.50V); do not allow invalid values
- What happens if the persisted threshold setting is corrupted or missing on app launch?
  - Fall back to the default threshold value (4.00V)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: App MUST display the current battery voltage in volts with at least two decimal places
- **FR-002**: App MUST update the voltage display approximately every 1 second while in the foreground
- **FR-003**: App MUST stop updates when backgrounded and resume when foregrounded
- **FR-004**: App MUST display the current charging status (charging or not charging)
- **FR-005**: App MUST display a warning indicator when voltage is within 0.05V below the user's configured threshold
- **FR-006**: App MUST display a strong warning indicator when voltage reaches or exceeds the user's configured threshold
- **FR-007**: App MUST allow users to adjust the warning threshold voltage via a slider control
- **FR-008**: App MUST persist the user's threshold setting across app restarts
- **FR-009**: App MUST provide a default threshold value of 4.00V
- **FR-010**: App MUST allow users to reset the threshold to the default value
- **FR-011**: App MUST constrain threshold values to a valid range (3.50V to 4.50V)
- **FR-012**: App MUST NOT perform any background monitoring or services
- **FR-013**: App MUST NOT persist or store any historical voltage data
- **FR-014**: App MUST NOT require any special permissions beyond Android defaults
- **FR-015**: App MUST provide a single-screen interface (threshold control integrated, no separate settings screen)
- **FR-016**: App MUST display the voltage prominently (large, central readout)
- **FR-017**: App MUST support dark mode display
- **FR-018**: App MUST maintain stable, readable display during frequent updates (no flicker or layout shifts)

### Non-Functional Requirements

- **NFR-001**: App is read-only and informational; it MUST NOT control or automate charging
- **NFR-002**: App MUST NOT include cloud connectivity, analytics, ads, or user accounts
- **NFR-003**: App MUST NOT implement temperature-based logic or warnings

### Key Entities

- **Voltage Reading**: The current OS-reported battery voltage value (in millivolts from system, displayed in volts to user). Attributes: numeric value, timestamp of reading.
- **Charging State**: The current OS-reported charging status. Attributes: boolean (charging/not charging), source type (if plugged in).
- **Threshold Setting**: The user-configured voltage value at which strong warnings trigger. Attributes: voltage value (volts), valid range (3.50V-4.50V), default value (4.00V), persisted across sessions.
- **Warning State**: Derived state based on current voltage compared to threshold setting. Attributes: warning level (none, approaching, threshold reached).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can view the current battery voltage within 2 seconds of launching the app
- **SC-002**: Voltage display updates at least once per second while the app is in the foreground
- **SC-003**: Users can determine charging status at a glance within 1 second of viewing the screen
- **SC-004**: Users receive visual warning feedback within 1 second of voltage crossing a threshold
- **SC-005**: App remains usable and responsive during extended monitoring sessions (30+ minutes)
- **SC-006**: App consumes minimal resources and does not noticeably impact device performance or battery drain
- **SC-007**: Voltage display is readable from arm's length (approximately 50cm) in both light and dark environments
- **SC-008**: Users can adjust the threshold value in under 5 seconds
- **SC-009**: Threshold setting persists correctly after app restart 100% of the time

## Assumptions

- The OS-reported battery voltage is a reasonable approximation for user decision-making, though it may not reflect the actual cell voltage precisely
- Samsung phones running Android 12+ reliably report battery voltage through standard system broadcasts
- Users understand that thresholds are personal preferences and not safety guarantees
- Users will manually disconnect the charger when desired; the app provides information only
- Typical observed voltage ranges are approximately 3.6V-4.4V, varying by device
- The valid threshold range (3.50V-4.50V) covers reasonable charging cutoff preferences

## Out of Scope

- Charging control, automation, or enforcement of any kind
- Temperature monitoring or temperature-based warnings
- Historical data storage, graphs, or trends
- Multiple screens or navigation (threshold control is integrated on the main screen)
- Separate settings screen or complex preferences UI
- Notifications or alerts (app must be in foreground)
- Play Store distribution or production signing
- Support for devices below Android 12
- Support for non-Samsung devices (may work but not tested/guaranteed)
