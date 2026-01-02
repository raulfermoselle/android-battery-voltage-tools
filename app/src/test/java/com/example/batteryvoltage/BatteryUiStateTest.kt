package com.example.batteryvoltage

import com.example.batteryvoltage.data.BatteryUiState
import com.example.batteryvoltage.data.WarningLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryUiStateTest {

    @Test
    fun `calculateWarningLevel returns NONE when voltage below threshold minus offset`() {
        val result = BatteryUiState.calculateWarningLevel(
            voltage = 3.90f,
            threshold = 4.00f,
            isValid = true
        )

        assertEquals(WarningLevel.NONE, result)
    }

    @Test
    fun `calculateWarningLevel returns APPROACHING when voltage within offset of threshold`() {
        val result = BatteryUiState.calculateWarningLevel(
            voltage = 3.96f, // 4.00 - 0.05 = 3.95, so 3.96 is approaching
            threshold = 4.00f,
            isValid = true
        )

        assertEquals(WarningLevel.APPROACHING, result)
    }

    @Test
    fun `calculateWarningLevel returns APPROACHING at exact boundary`() {
        val result = BatteryUiState.calculateWarningLevel(
            voltage = 3.95f, // exactly threshold - offset
            threshold = 4.00f,
            isValid = true
        )

        assertEquals(WarningLevel.APPROACHING, result)
    }

    @Test
    fun `calculateWarningLevel returns REACHED when voltage equals threshold`() {
        val result = BatteryUiState.calculateWarningLevel(
            voltage = 4.00f,
            threshold = 4.00f,
            isValid = true
        )

        assertEquals(WarningLevel.REACHED, result)
    }

    @Test
    fun `calculateWarningLevel returns REACHED when voltage exceeds threshold`() {
        val result = BatteryUiState.calculateWarningLevel(
            voltage = 4.10f,
            threshold = 4.00f,
            isValid = true
        )

        assertEquals(WarningLevel.REACHED, result)
    }

    @Test
    fun `calculateWarningLevel returns NONE when reading is invalid`() {
        val result = BatteryUiState.calculateWarningLevel(
            voltage = 4.10f, // Would be REACHED if valid
            threshold = 4.00f,
            isValid = false
        )

        assertEquals(WarningLevel.NONE, result)
    }

    @Test
    fun `voltageDisplay formats valid voltage correctly`() {
        val state = BatteryUiState(
            voltageVolts = 4.05f,
            isValid = true
        )

        assertEquals("4.05 V", state.voltageDisplay)
    }

    @Test
    fun `voltageDisplay shows placeholder for invalid voltage`() {
        val state = BatteryUiState(
            voltageVolts = 0f,
            isValid = false
        )

        assertEquals("-- V", state.voltageDisplay)
    }

    @Test
    fun `warningMessage returns null for NONE`() {
        val state = BatteryUiState(warningLevel = WarningLevel.NONE)

        assertEquals(null, state.warningMessage)
    }

    @Test
    fun `warningMessage returns approaching text for APPROACHING`() {
        val state = BatteryUiState(warningLevel = WarningLevel.APPROACHING)

        assertEquals("Approaching threshold", state.warningMessage)
    }

    @Test
    fun `warningMessage returns reached text for REACHED`() {
        val state = BatteryUiState(warningLevel = WarningLevel.REACHED)

        assertEquals("Threshold reached - stop charging", state.warningMessage)
    }
}
