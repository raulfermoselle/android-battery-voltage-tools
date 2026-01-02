package com.example.batteryvoltage.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.batteryvoltage.data.BatteryUiState

/**
 * Display component showing the current charging status.
 */
@Composable
fun ChargingStatusDisplay(
    uiState: BatteryUiState,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Charging indicator icon (using Unicode for simplicity)
        Text(
            text = if (uiState.isCharging) "\u26A1" else "\uD83D\uDD0B",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = uiState.chargingStatusDisplay,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
