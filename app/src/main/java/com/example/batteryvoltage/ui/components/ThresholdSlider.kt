package com.example.batteryvoltage.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.batteryvoltage.data.BatteryUiState

/**
 * Slider component for adjusting the warning threshold.
 */
@Composable
fun ThresholdSlider(
    threshold: Float,
    onThresholdChange: (Float) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Label and current value
        Text(
            text = "Warning Threshold",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Current threshold value display
        Text(
            text = "%.2f V".format(threshold),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Slider with range labels
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "%.1f".format(BatteryUiState.MIN_THRESHOLD),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = threshold,
                onValueChange = onThresholdChange,
                valueRange = BatteryUiState.MIN_THRESHOLD..BatteryUiState.MAX_THRESHOLD,
                steps = 19, // 0.05V increments: (4.50 - 3.50) / 0.05 - 1 = 19 steps
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "%.1f".format(BatteryUiState.MAX_THRESHOLD),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset button
        OutlinedButton(onClick = onResetClick) {
            Text(text = "Reset to Default")
        }
    }
}
