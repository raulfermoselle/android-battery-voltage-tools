package com.example.batteryvoltage.data

/**
 * Warning level based on current voltage relative to threshold.
 */
enum class WarningLevel {
    /** Voltage is below the approaching range */
    NONE,

    /** Voltage is within 0.05V of threshold (approaching) */
    APPROACHING,

    /** Voltage has reached or exceeded threshold */
    REACHED
}
