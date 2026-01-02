package com.example.batteryvoltage

import android.content.Context
import android.content.SharedPreferences
import com.example.batteryvoltage.data.BatteryUiState
import com.example.batteryvoltage.data.ThresholdRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ThresholdRepositoryTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPrefs: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var repository: ThresholdRepository

    @Before
    fun setUp() {
        `when`(mockContext.getSharedPreferences("battery_voltage_prefs", Context.MODE_PRIVATE))
            .thenReturn(mockPrefs)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putFloat(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyFloat()))
            .thenReturn(mockEditor)

        repository = ThresholdRepository(mockContext)
    }

    @Test
    fun `getThreshold returns default when not set`() {
        `when`(mockPrefs.getFloat("threshold_voltage", BatteryUiState.DEFAULT_THRESHOLD))
            .thenReturn(BatteryUiState.DEFAULT_THRESHOLD)

        val result = repository.getThreshold()

        assertEquals(BatteryUiState.DEFAULT_THRESHOLD, result, 0.001f)
    }

    @Test
    fun `getThreshold returns stored value when valid`() {
        val storedValue = 4.20f
        `when`(mockPrefs.getFloat("threshold_voltage", BatteryUiState.DEFAULT_THRESHOLD))
            .thenReturn(storedValue)

        val result = repository.getThreshold()

        assertEquals(storedValue, result, 0.001f)
    }

    @Test
    fun `setThreshold stores value within range`() {
        val threshold = 4.15f

        repository.setThreshold(threshold)

        verify(mockEditor).putFloat("threshold_voltage", threshold)
        verify(mockEditor).apply()
    }

    @Test
    fun `setThreshold clamps value below minimum`() {
        val belowMin = 3.00f

        repository.setThreshold(belowMin)

        verify(mockEditor).putFloat("threshold_voltage", BatteryUiState.MIN_THRESHOLD)
    }

    @Test
    fun `setThreshold clamps value above maximum`() {
        val aboveMax = 5.00f

        repository.setThreshold(aboveMax)

        verify(mockEditor).putFloat("threshold_voltage", BatteryUiState.MAX_THRESHOLD)
    }

    @Test
    fun `resetToDefault stores default value`() {
        val result = repository.resetToDefault()

        verify(mockEditor).putFloat("threshold_voltage", BatteryUiState.DEFAULT_THRESHOLD)
        verify(mockEditor).apply()
        assertEquals(BatteryUiState.DEFAULT_THRESHOLD, result, 0.001f)
    }
}
