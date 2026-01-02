package com.example.batteryvoltage.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Repository for persisting the warning threshold setting.
 */
class ThresholdRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Get the current threshold value.
     * Returns default if not set or corrupted.
     */
    fun getThreshold(): Float {
        val value = prefs.getFloat(KEY_THRESHOLD, BatteryUiState.DEFAULT_THRESHOLD)
        return if (isValidThreshold(value)) {
            value
        } else {
            // Reset to default if corrupted
            resetToDefault()
        }
    }

    /**
     * Set the threshold value with validation.
     * Values outside valid range are clamped.
     */
    fun setThreshold(threshold: Float) {
        val clampedValue = threshold.coerceIn(
            BatteryUiState.MIN_THRESHOLD,
            BatteryUiState.MAX_THRESHOLD
        )
        prefs.edit().putFloat(KEY_THRESHOLD, clampedValue).apply()
    }

    /**
     * Reset threshold to default value.
     * @return The default threshold value.
     */
    fun resetToDefault(): Float {
        prefs.edit().putFloat(KEY_THRESHOLD, BatteryUiState.DEFAULT_THRESHOLD).apply()
        return BatteryUiState.DEFAULT_THRESHOLD
    }

    /**
     * Check if threshold value is within valid range.
     */
    private fun isValidThreshold(value: Float): Boolean {
        return value in BatteryUiState.MIN_THRESHOLD..BatteryUiState.MAX_THRESHOLD
    }

    companion object {
        private const val PREFS_NAME = "battery_voltage_prefs"
        private const val KEY_THRESHOLD = "threshold_voltage"
    }
}
