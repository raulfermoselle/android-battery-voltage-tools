package com.example.batteryvoltage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.batteryvoltage.data.BatteryUiState
import com.example.batteryvoltage.data.ThresholdRepository
import com.example.batteryvoltage.service.BatteryMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel for battery monitoring state management.
 */
class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val batteryMonitor = BatteryMonitor(application)
    private val thresholdRepository = ThresholdRepository(application)

    private val _batteryState = MutableStateFlow(
        BatteryUiState(thresholdVolts = thresholdRepository.getThreshold())
    )
    val batteryState: StateFlow<BatteryUiState> = _batteryState.asStateFlow()

    private var monitoringJob: Job? = null

    /**
     * Start monitoring battery state with ~1Hz updates.
     * Should be called from onStart().
     */
    fun startMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = viewModelScope.launch {
            while (isActive) {
                updateBatteryState()
                delay(1000L) // 1-second polling interval
            }
        }
    }

    /**
     * Stop monitoring battery state.
     * Should be called from onStop().
     */
    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }

    /**
     * Update the current battery state from system.
     */
    private fun updateBatteryState() {
        val reading = batteryMonitor.readBatteryState()
        val threshold = _batteryState.value.thresholdVolts

        _batteryState.update { currentState ->
            currentState.copy(
                voltageVolts = reading.voltageVolts,
                isCharging = reading.isCharging,
                chargingStatus = reading.chargingStatus,
                isValid = reading.isValid,
                warningLevel = BatteryUiState.calculateWarningLevel(
                    voltage = reading.voltageVolts,
                    threshold = threshold,
                    isValid = reading.isValid
                )
            )
        }
    }

    /**
     * Update the warning threshold.
     */
    fun setThreshold(threshold: Float) {
        thresholdRepository.setThreshold(threshold)
        val voltage = _batteryState.value.voltageVolts
        val isValid = _batteryState.value.isValid

        _batteryState.update { currentState ->
            currentState.copy(
                thresholdVolts = threshold,
                warningLevel = BatteryUiState.calculateWarningLevel(
                    voltage = voltage,
                    threshold = threshold,
                    isValid = isValid
                )
            )
        }
    }

    /**
     * Reset threshold to default value.
     */
    fun resetThreshold() {
        setThreshold(thresholdRepository.resetToDefault())
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}
