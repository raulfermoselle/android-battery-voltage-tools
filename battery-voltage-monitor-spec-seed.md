Feature name: Battery Voltage Monitor

This specification seed defines the Battery Voltage Monitor feature for a native Android app written in Kotlin, targeting Android 12+.

The feature’s purpose is to display the OS-reported battery voltage in near real time so the user can make informed, manual charging decisions (e.g. stopping charge near ~4.0 V).

Context and scope
- Platform: Native Android (Kotlin)
- Min SDK: Android 12 (API 31)
- Target devices: Samsung phones
- Distribution: Debug APK sideloaded manually (no Play Store)
- Feature scope: Read-only monitoring and user-facing warnings

Functional requirements (authoritative)

1. Battery voltage
   - Read battery voltage using Android’s ACTION_BATTERY_CHANGED broadcast.
   - Use BatteryManager.EXTRA_VOLTAGE (millivolts).
   - Convert to volts (V) and display with at least two decimal places.

2. Update behavior
   - Update voltage while the app is in the foreground at approximately 1-second intervals.
   - No background services.
   - No persistence or historical storage.

3. Charging state
   - Display whether the device is currently charging or not.
   - Use OS-reported charging status (EXTRA_STATUS and/or EXTRA_PLUGGED).

4. Threshold warnings
   - Voltage ≥ 3.95 V: show a visual warning (e.g. orange text, “Approaching 4.0 V”).
   - Voltage ≥ 4.00 V: show a strong warning (e.g. red text, “User threshold reached – stop charging”).

Non-goals (explicit)
- No charging control, automation, or enforcement.
- No temperature-based logic.
- No cloud connectivity, analytics, ads, or user accounts.
- No additional permissions beyond Android defaults.
- No navigation, settings, or multi-screen UI.

UI requirements
- Single Activity.
- One screen only.
- Dark-mode friendly.
- Very large, central voltage readout.
- Smaller text for charging state and threshold messages.
- UI must remain stable and readable during frequent updates.

Technical constraints
- Kotlin only (no Java).
- Do not use Jetpack Compose unless explicitly justified; prefer the simplest reliable UI approach.
- Use lifecycle-safe registration and unregistration of the battery BroadcastReceiver.
- Keep code minimal, readable, and easy to audit.

Deliverables (planned)
- Android Studio–compatible project structure.
- Gradle-based build producing a debug APK.
- README section explaining:
  - What the voltage value represents (OS-reported, not direct cell measurement).
  - Typical observed voltage ranges (~3.6–4.4 V, device dependent).
  - That 4.0 V is a user-chosen early-stop threshold, not a safety guarantee.

Process expectation
- Produce a clear feature specification first.
- Then derive a technical plan (classes, responsibilities).
- Then outline an implementation plan in small, verifiable steps.

Do not write implementation code yet.
