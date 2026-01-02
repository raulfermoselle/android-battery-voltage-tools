package com.example.batteryvoltage.data

import androidx.compose.runtime.Immutable

/**
 * Aggregate UI state for the battery monitor screen.
 */
@Immutable
data class BatteryUiState(
    val voltageVolts: Float = 0f,
    val isCharging: Boolean = false,
    val chargingStatus: ChargingStatus = ChargingStatus.UNKNOWN,
    val thresholdVolts: Float = DEFAULT_THRESHOLD,
    val warningLevel: WarningLevel = WarningLevel.NONE,
    val isValid: Boolean = false
) {
    /**
     * Formatted voltage string for display (e.g., "4.05 V").
     */
    val voltageDisplay: String
        get() = if (isValid) "%.2f V".format(voltageVolts) else "-- V"

    /**
     * Display string for charging status.
     */
    val chargingStatusDisplay: String
        get() = when (chargingStatus) {
            ChargingStatus.CHARGING -> "Charging"
            ChargingStatus.FULL -> "Full"
            ChargingStatus.DISCHARGING,
            ChargingStatus.NOT_CHARGING -> "Not Charging"
            ChargingStatus.UNKNOWN -> "Unknown"
        }

    /**
     * Warning message to display, or null if no warning.
     */
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

        /**
         * Calculate warning level based on voltage and threshold.
         */
        fun calculateWarningLevel(voltage: Float, threshold: Float, isValid: Boolean): WarningLevel {
            if (!isValid) return WarningLevel.NONE
            return when {
                voltage >= threshold -> WarningLevel.REACHED
                voltage >= threshold - APPROACHING_OFFSET -> WarningLevel.APPROACHING
                else -> WarningLevel.NONE
            }
        }
    }
}
