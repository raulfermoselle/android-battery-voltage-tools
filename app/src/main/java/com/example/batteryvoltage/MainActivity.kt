package com.example.batteryvoltage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.batteryvoltage.data.BatteryUiState
import com.example.batteryvoltage.ui.components.ChargingStatusDisplay
import com.example.batteryvoltage.ui.components.ThresholdSlider
import com.example.batteryvoltage.ui.components.VoltageDisplay
import com.example.batteryvoltage.ui.theme.BatteryVoltageTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BatteryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryVoltageTheme {
                val uiState by viewModel.batteryState.collectAsStateWithLifecycle()
                BatteryMonitorScreen(
                    uiState = uiState,
                    onThresholdChange = viewModel::setThreshold,
                    onResetThreshold = viewModel::resetThreshold
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startMonitoring()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopMonitoring()
    }
}

@Composable
fun BatteryMonitorScreen(
    uiState: BatteryUiState,
    onThresholdChange: (Float) -> Unit,
    onResetThreshold: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main voltage display
            VoltageDisplay(uiState = uiState)

            Spacer(modifier = Modifier.height(32.dp))

            // Charging status
            ChargingStatusDisplay(uiState = uiState)

            Spacer(modifier = Modifier.height(48.dp))

            // Threshold slider
            ThresholdSlider(
                threshold = uiState.thresholdVolts,
                onThresholdChange = onThresholdChange,
                onResetClick = onResetThreshold
            )
        }
    }
}
