package com.example.batteryvoltage.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.batteryvoltage.data.ChargingStatus
import com.example.batteryvoltage.data.PluggedType

/**
 * Battery reading data from the system.
 */
data class BatteryReading(
    val voltageMillivolts: Int,
    val chargingStatus: ChargingStatus,
    val pluggedType: PluggedType,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Voltage in volts (e.g., 4200 mV -> 4.2 V).
     */
    val voltageVolts: Float
        get() = voltageMillivolts / 1000f

    /**
     * Whether the device is currently charging.
     */
    val isCharging: Boolean
        get() = chargingStatus == ChargingStatus.CHARGING || chargingStatus == ChargingStatus.FULL

    /**
     * Whether the voltage reading is valid (within reasonable range).
     */
    val isValid: Boolean
        get() = voltageMillivolts > 0 && voltageMillivolts < 10000
}

/**
 * Service for reading battery information from the Android system.
 */
class BatteryMonitor(private val context: Context) {

    /**
     * Read current battery state from the system.
     * Uses the sticky ACTION_BATTERY_CHANGED broadcast.
     */
    fun readBatteryState(): BatteryReading {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, filter)

        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val plugged = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0

        return BatteryReading(
            voltageMillivolts = voltage,
            chargingStatus = mapChargingStatus(status),
            pluggedType = mapPluggedType(plugged)
        )
    }

    /**
     * Map Android BatteryManager status constants to ChargingStatus enum.
     */
    fun mapChargingStatus(status: Int): ChargingStatus {
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> ChargingStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> ChargingStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> ChargingStatus.NOT_CHARGING
            BatteryManager.BATTERY_STATUS_FULL -> ChargingStatus.FULL
            else -> ChargingStatus.UNKNOWN
        }
    }

    /**
     * Map Android BatteryManager plugged constants to PluggedType enum.
     */
    fun mapPluggedType(plugged: Int): PluggedType {
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> PluggedType.AC
            BatteryManager.BATTERY_PLUGGED_USB -> PluggedType.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> PluggedType.WIRELESS
            else -> PluggedType.NONE
        }
    }
}
