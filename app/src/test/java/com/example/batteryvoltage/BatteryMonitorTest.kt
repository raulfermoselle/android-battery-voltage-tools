package com.example.batteryvoltage

import android.os.BatteryManager
import com.example.batteryvoltage.data.ChargingStatus
import com.example.batteryvoltage.data.PluggedType
import com.example.batteryvoltage.service.BatteryMonitor
import com.example.batteryvoltage.service.BatteryReading
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryMonitorTest {

    @Test
    fun `BatteryReading converts millivolts to volts correctly`() {
        val reading = BatteryReading(
            voltageMillivolts = 4200,
            chargingStatus = ChargingStatus.CHARGING,
            pluggedType = PluggedType.AC
        )

        assertEquals(4.2f, reading.voltageVolts, 0.001f)
    }

    @Test
    fun `BatteryReading converts 3850 millivolts correctly`() {
        val reading = BatteryReading(
            voltageMillivolts = 3850,
            chargingStatus = ChargingStatus.DISCHARGING,
            pluggedType = PluggedType.NONE
        )

        assertEquals(3.85f, reading.voltageVolts, 0.001f)
    }

    @Test
    fun `BatteryReading isCharging returns true when CHARGING`() {
        val reading = BatteryReading(
            voltageMillivolts = 4000,
            chargingStatus = ChargingStatus.CHARGING,
            pluggedType = PluggedType.USB
        )

        assertTrue(reading.isCharging)
    }

    @Test
    fun `BatteryReading isCharging returns true when FULL`() {
        val reading = BatteryReading(
            voltageMillivolts = 4350,
            chargingStatus = ChargingStatus.FULL,
            pluggedType = PluggedType.AC
        )

        assertTrue(reading.isCharging)
    }

    @Test
    fun `BatteryReading isCharging returns false when DISCHARGING`() {
        val reading = BatteryReading(
            voltageMillivolts = 3800,
            chargingStatus = ChargingStatus.DISCHARGING,
            pluggedType = PluggedType.NONE
        )

        assertFalse(reading.isCharging)
    }

    @Test
    fun `BatteryReading isCharging returns false when NOT_CHARGING`() {
        val reading = BatteryReading(
            voltageMillivolts = 4100,
            chargingStatus = ChargingStatus.NOT_CHARGING,
            pluggedType = PluggedType.NONE
        )

        assertFalse(reading.isCharging)
    }

    @Test
    fun `BatteryReading isValid returns true for valid voltage`() {
        val reading = BatteryReading(
            voltageMillivolts = 4000,
            chargingStatus = ChargingStatus.CHARGING,
            pluggedType = PluggedType.AC
        )

        assertTrue(reading.isValid)
    }

    @Test
    fun `BatteryReading isValid returns false for zero voltage`() {
        val reading = BatteryReading(
            voltageMillivolts = 0,
            chargingStatus = ChargingStatus.UNKNOWN,
            pluggedType = PluggedType.NONE
        )

        assertFalse(reading.isValid)
    }

    @Test
    fun `BatteryReading isValid returns false for negative voltage`() {
        val reading = BatteryReading(
            voltageMillivolts = -100,
            chargingStatus = ChargingStatus.UNKNOWN,
            pluggedType = PluggedType.NONE
        )

        assertFalse(reading.isValid)
    }

    @Test
    fun `BatteryReading isValid returns false for excessive voltage`() {
        val reading = BatteryReading(
            voltageMillivolts = 15000,
            chargingStatus = ChargingStatus.UNKNOWN,
            pluggedType = PluggedType.NONE
        )

        assertFalse(reading.isValid)
    }
}
