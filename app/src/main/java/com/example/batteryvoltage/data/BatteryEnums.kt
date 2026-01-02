package com.example.batteryvoltage.data

/**
 * Charging status as reported by Android BatteryManager.
 */
enum class ChargingStatus {
    UNKNOWN,
    CHARGING,
    DISCHARGING,
    NOT_CHARGING,
    FULL
}

/**
 * Power source type when device is plugged in.
 */
enum class PluggedType {
    NONE,
    AC,
    USB,
    WIRELESS
}
