package com.nammavastra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.theme.AppSurface
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.SoftBorder
import com.nammavastra.ui.theme.ZariGold

@Composable
fun EmptyState(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(AppSurface, shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .border(1.dp, SoftBorder.copy(alpha = 0.12f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = ZariGold)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        Button(onClick = onAction) {
            Text(actionLabel)
        }
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = ZariGold)
    }
}

@Composable
fun LoadingScrim() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = ZariGold)
    }
}

@Composable
fun MarketPulsePanel(
    title: String,
    body: String,
    bullets: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppSurface,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = DeepCharcoal
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
            bullets.forEach { point ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(ZariGold, CircleShape)
                    )
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightChipRow(
    chips: List<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chips.forEach { chip ->
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color.White.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder.copy(alpha = 0.5f))
            ) {
                Text(
                    text = chip,
                    style = MaterialTheme.typography.labelMedium,
                    color = DeepCharcoal,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
