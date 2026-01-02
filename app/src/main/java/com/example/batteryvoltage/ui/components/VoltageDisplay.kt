package com.example.batteryvoltage.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.batteryvoltage.data.BatteryUiState
import com.example.batteryvoltage.data.WarningLevel
import com.example.batteryvoltage.ui.theme.WarningApproaching
import com.example.batteryvoltage.ui.theme.WarningNormal
import com.example.batteryvoltage.ui.theme.WarningReached

/**
 * Main voltage display component showing the current battery voltage.
 * Text size is optimized for readability at arm's length (~50cm).
 */
@Composable
fun VoltageDisplay(
    uiState: BatteryUiState,
    modifier: Modifier = Modifier
) {
    val voltageColor = getWarningColor(uiState.warningLevel)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Large voltage readout (readable at arm's length)
        Text(
            text = uiState.voltageDisplay,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold
            ),
            color = voltageColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Voltage label
        Text(
            text = "Battery Voltage",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Warning message (if any)
        uiState.warningMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = voltageColor
            )
        }
    }
}

/**
 * Get the appropriate color for the current warning level.
 */
@Composable
private fun getWarningColor(warningLevel: WarningLevel): Color {
    return when (warningLevel) {
        WarningLevel.NONE -> WarningNormal
        WarningLevel.APPROACHING -> WarningApproaching
        WarningLevel.REACHED -> WarningReached
    }
}
