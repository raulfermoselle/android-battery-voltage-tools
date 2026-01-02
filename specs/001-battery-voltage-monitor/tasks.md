# Tasks: Battery Voltage Monitor

**Input**: Design documents from `/specs/001-battery-voltage-monitor/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Tests**: Unit tests included for core logic (ThresholdRepository, BatteryMonitor). UI instrumented tests omitted.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

Based on plan.md structure:
- **App module**: `app/src/main/java/com/example/batteryvoltage/`
- **Resources**: `app/src/main/res/`
- **Unit tests**: `app/src/test/java/com/example/batteryvoltage/`
- **Instrumented tests**: `app/src/androidTest/java/com/example/batteryvoltage/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Android project initialization and build configuration

- [ ] T001 Create Android project structure with app module per implementation plan
- [ ] T002 Configure build.gradle.kts (project-level) with Kotlin 1.9+ and Android Gradle plugin
- [ ] T003 Configure app/build.gradle.kts with compileSdk 35, minSdk 31, Jetpack Compose dependencies
- [ ] T004 [P] Configure settings.gradle.kts with project name and repository settings
- [ ] T005 [P] Configure gradle.properties with JVM and Android configuration
- [ ] T006 Create AndroidManifest.xml with single activity declaration in app/src/main/AndroidManifest.xml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T007 [P] Create Color.kt with light/dark color definitions in app/src/main/java/com/example/batteryvoltage/ui/theme/Color.kt
- [ ] T008 [P] Create Type.kt with typography definitions in app/src/main/java/com/example/batteryvoltage/ui/theme/Type.kt
- [ ] T009 Create Theme.kt with Material 3 theme and dark mode support in app/src/main/java/com/example/batteryvoltage/ui/theme/Theme.kt
- [ ] T010 [P] Create strings.xml with app strings in app/src/main/res/values/strings.xml
- [ ] T011 [P] Create themes.xml for light mode in app/src/main/res/values/themes.xml
- [ ] T012 [P] Create themes.xml for dark mode in app/src/main/res/values-night/themes.xml
- [ ] T013 Create ChargingStatus and PluggedType enums in app/src/main/java/com/example/batteryvoltage/data/BatteryEnums.kt
- [ ] T014 Create WarningLevel enum in app/src/main/java/com/example/batteryvoltage/data/WarningLevel.kt
- [ ] T015 Create BatteryUiState data class with computed properties in app/src/main/java/com/example/batteryvoltage/data/BatteryUiState.kt

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Current Battery Voltage (Priority: P1) 🎯 MVP

**Goal**: Display current battery voltage prominently with real-time updates (~1Hz)

**Independent Test**: Launch app and verify voltage readout displays plausible value (3.6V-4.4V range) and updates every ~1 second

### Implementation for User Story 1

- [ ] T016 [US1] Create BatteryMonitor service with ACTION_BATTERY_CHANGED receiver in app/src/main/java/com/example/batteryvoltage/service/BatteryMonitor.kt
- [ ] T017 [US1] Implement voltage reading from EXTRA_VOLTAGE (millivolts to volts conversion) in app/src/main/java/com/example/batteryvoltage/service/BatteryMonitor.kt
- [ ] T018 [US1] Create VoltageDisplay composable with large voltage readout in app/src/main/java/com/example/batteryvoltage/ui/components/VoltageDisplay.kt
- [ ] T019 [US1] Create BatteryViewModel with StateFlow for BatteryUiState in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt
- [ ] T020 [US1] Implement 1-second polling loop with coroutine delay in BatteryViewModel in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt
- [ ] T021 [US1] Create MainActivity with Compose setup and VoltageDisplay in app/src/main/java/com/example/batteryvoltage/MainActivity.kt
- [ ] T022 [US1] Implement lifecycle-aware registration (onStart/onStop) in app/src/main/java/com/example/batteryvoltage/MainActivity.kt
- [ ] T023 [US1] Handle edge case: invalid voltage readings (0 or negative) display value but don't crash in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt

**Checkpoint**: User Story 1 complete - voltage displays and updates in real-time

---

## Phase 4: User Story 2 - View Charging Status (Priority: P2)

**Goal**: Display charging/not charging status alongside voltage reading

**Independent Test**: Launch app while plugged in and unplugged, verify charging indicator changes appropriately

### Implementation for User Story 2

- [ ] T024 [US2] Extract EXTRA_STATUS and EXTRA_PLUGGED from battery broadcast in app/src/main/java/com/example/batteryvoltage/service/BatteryMonitor.kt
- [ ] T025 [US2] Map BatteryManager status constants to ChargingStatus enum in app/src/main/java/com/example/batteryvoltage/service/BatteryMonitor.kt
- [ ] T026 [US2] Create ChargingStatus composable with status display in app/src/main/java/com/example/batteryvoltage/ui/components/ChargingStatus.kt
- [ ] T027 [US2] Update BatteryUiState with charging status and display string in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt
- [ ] T028 [US2] Add ChargingStatus composable to MainActivity layout in app/src/main/java/com/example/batteryvoltage/MainActivity.kt

**Checkpoint**: User Stories 1 AND 2 complete - voltage and charging status display

---

## Phase 5: User Story 3 - Configure Warning Threshold (Priority: P2)

**Goal**: Allow users to adjust warning threshold via slider with persistence

**Independent Test**: Adjust threshold value, close app, reopen - verify threshold persists

### Implementation for User Story 3

- [ ] T029 [US3] Create ThresholdRepository with SharedPreferences in app/src/main/java/com/example/batteryvoltage/data/ThresholdRepository.kt
- [ ] T030 [US3] Implement getThreshold() with default value (4.00V) in app/src/main/java/com/example/batteryvoltage/data/ThresholdRepository.kt
- [ ] T031 [US3] Implement setThreshold() with range validation (3.50V-4.50V) in app/src/main/java/com/example/batteryvoltage/data/ThresholdRepository.kt
- [ ] T032 [US3] Implement resetToDefault() function in app/src/main/java/com/example/batteryvoltage/data/ThresholdRepository.kt
- [ ] T033 [US3] Create ThresholdSlider composable with slider control in app/src/main/java/com/example/batteryvoltage/ui/components/ThresholdSlider.kt
- [ ] T034 [US3] Add threshold display showing current value in app/src/main/java/com/example/batteryvoltage/ui/components/ThresholdSlider.kt
- [ ] T035 [US3] Add reset to default button in app/src/main/java/com/example/batteryvoltage/ui/components/ThresholdSlider.kt
- [ ] T036 [US3] Connect ThresholdRepository to BatteryViewModel in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt
- [ ] T037 [US3] Add ThresholdSlider composable to MainActivity layout in app/src/main/java/com/example/batteryvoltage/MainActivity.kt
- [ ] T038 [US3] Handle edge case: corrupted/missing threshold defaults to 4.00V in app/src/main/java/com/example/batteryvoltage/data/ThresholdRepository.kt

**Checkpoint**: User Stories 1, 2, AND 3 complete - voltage, status, and configurable threshold

---

## Phase 6: User Story 4 - Receive Threshold Warnings (Priority: P3)

**Goal**: Show visual warnings as voltage approaches and reaches threshold

**Independent Test**: Set threshold near current voltage, observe color/text changes as battery charges past warning and threshold values

### Implementation for User Story 4

- [ ] T039 [US4] Implement calculateWarningLevel function in app/src/main/java/com/example/batteryvoltage/data/BatteryUiState.kt
- [ ] T040 [US4] Update BatteryViewModel to compute WarningLevel from voltage and threshold in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt
- [ ] T041 [US4] Add warning colors (normal, orange/approaching, red/reached) to Color.kt in app/src/main/java/com/example/batteryvoltage/ui/theme/Color.kt
- [ ] T042 [US4] Update VoltageDisplay with warning-based color styling in app/src/main/java/com/example/batteryvoltage/ui/components/VoltageDisplay.kt
- [ ] T043 [US4] Add warning message display to VoltageDisplay in app/src/main/java/com/example/batteryvoltage/ui/components/VoltageDisplay.kt
- [ ] T044 [US4] Handle edge case: invalid readings don't trigger warnings in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt
- [ ] T045 [US4] Handle edge case: warnings clear when voltage drops below threshold in app/src/main/java/com/example/batteryvoltage/BatteryViewModel.kt

**Checkpoint**: All user stories complete - full warning system functional

---

## Phase 7: Unit Tests

**Purpose**: Validate core logic with automated unit tests

- [ ] T046 [P] Create ThresholdRepositoryTest.kt with tests for get/set/reset/validation in app/src/test/java/com/example/batteryvoltage/ThresholdRepositoryTest.kt
- [ ] T047 [P] Create BatteryMonitorTest.kt with tests for voltage conversion and status mapping in app/src/test/java/com/example/batteryvoltage/BatteryMonitorTest.kt
- [ ] T048 Run unit tests and verify all pass

**Checkpoint**: Unit tests passing

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and validation

- [ ] T049 Ensure UI is readable at arm's length (~50cm) with appropriate text sizes in app/src/main/java/com/example/batteryvoltage/ui/components/VoltageDisplay.kt
- [ ] T050 Verify dark mode theme applies correctly to all components in app/src/main/java/com/example/batteryvoltage/ui/theme/Theme.kt
- [ ] T051 Ensure stable display during frequent updates (no flicker/layout shifts) in app/src/main/java/com/example/batteryvoltage/ui/components/VoltageDisplay.kt
- [ ] T052 Validate app stops updates when backgrounded and resumes on foreground in app/src/main/java/com/example/batteryvoltage/MainActivity.kt
- [ ] T053 Run quickstart.md validation scenarios (voltage, charging, threshold, persistence)
- [ ] T054 Validate minimal resource usage during 30+ minute monitoring session (SC-006)
- [ ] T055 Build debug APK and test on Samsung device per quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can proceed sequentially in priority order (P1 → P2 → P2 → P3)
  - Or in parallel if multiple developers available
- **Unit Tests (Phase 7)**: Depends on User Stories 1 and 3 (core logic implementation)
- **Polish (Phase 8)**: Depends on all user stories and unit tests being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Shares BatteryMonitor with US1 but independently testable
- **User Story 3 (P2)**: Can start after Foundational (Phase 2) - Independent of US1/US2
- **User Story 4 (P3)**: Depends on US1 (voltage) and US3 (threshold) being complete for full functionality

### Within Each User Story

- Models/enums before services
- Services before UI components
- Core components before integration into MainActivity
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently

### Parallel Opportunities

- Setup tasks T004, T005 can run in parallel with T002, T003
- Foundational tasks T007, T008, T010, T011, T012 can run in parallel
- T013 and T014 can run in parallel
- User Stories 1, 2, and 3 can be worked on in parallel by different team members
- User Story 4 should wait for US1 and US3 completion

---

## Parallel Example: Setup Phase

```bash
# After T001, launch these in parallel:
Task: "Configure build.gradle.kts (project-level)" (T002)
Task: "Configure settings.gradle.kts" (T004)
Task: "Configure gradle.properties" (T005)
```

## Parallel Example: Foundational Phase

```bash
# Launch all theme files in parallel:
Task: "Create Color.kt" (T007)
Task: "Create Type.kt" (T008)
Task: "Create strings.xml" (T010)
Task: "Create themes.xml light" (T011)
Task: "Create themes.xml dark" (T012)

# Then T009 (Theme.kt depends on T007, T008)

# Launch enums in parallel:
Task: "Create BatteryEnums.kt" (T013)
Task: "Create WarningLevel.kt" (T014)

# Then T015 (BatteryUiState depends on T013, T014)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently - voltage displays and updates
5. Deploy/demo if ready - app shows real-time battery voltage

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Voltage + charging status
4. Add User Story 3 → Test independently → Configurable threshold
5. Add User Story 4 → Test independently → Full warning system
6. Unit Tests phase → Automated validation of core logic
7. Polish phase → Final validation and testing

### Single Developer Strategy

For solo development, execute phases sequentially in priority order:
1. Setup (T001-T006)
2. Foundational (T007-T015)
3. User Story 1 (T016-T023) - MVP milestone
4. User Story 2 (T024-T028)
5. User Story 3 (T029-T038)
6. User Story 4 (T039-T045)
7. Unit Tests (T046-T048)
8. Polish (T049-T055)

---

## Notes

- [P] tasks = different files, no dependencies within phase
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- Target platform: Samsung devices running Android 12+ (API 31)
- No tests generated - not explicitly requested in specification
