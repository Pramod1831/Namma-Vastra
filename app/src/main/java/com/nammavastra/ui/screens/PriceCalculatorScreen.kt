package com.nammavastra.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.nammavastra.ui.theme.RobotoMonoFamily
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.PriceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceCalculatorScreen(
    viewModel: PriceViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Fair Price Calculator", style = MaterialTheme.typography.displayMedium)
        Text(
            "Transparent, artisan-first valuation for boutique markets.",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = state.materialCost,
            onValueChange = viewModel::updateMaterialCost,
            label = { Text("Silk/Cotton cost per metre (Rs.)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = state.zariCost,
            onValueChange = viewModel::updateZariCost,
            label = { Text("Zari / thread cost (Rs.)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.labourHours,
                onValueChange = viewModel::updateLabourHours,
                label = { Text("Labour hours") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = state.overhead,
                onValueChange = viewModel::updateOverhead,
                label = { Text("Overhead (Rs.)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf("Silk", "Cotton").forEachIndexed { index, type ->
                SegmentedButton(
                    selected = state.fabricType == type,
                    onClick = { viewModel.updateFabricType(type) },
                    shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(index, 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surface,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    label = { Text("$type ${if (type == "Silk") "2.5x" else "2.0x"}") }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = viewModel::calculatePrice,
                modifier = Modifier.weight(1f)
            ) {
                Text("Calculate Fair Price")
            }
            OutlinedButton(
                onClick = viewModel::reset,
                modifier = Modifier.weight(0.55f)
            ) {
                Text("Reset")
            }
        }

        if (state.suggestedPrice != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Suggested Retail Price", style = MaterialTheme.typography.labelLarge)
                    Text(
                        formatter.format(state.suggestedPrice),
                        style = MaterialTheme.typography.displayMedium,
                        fontFamily = RobotoMonoFamily,
                        color = ZariGold
                    )
                    if (state.isLoading) {
                        Text("Generating AI rationale...", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Text(
                            state.rationale,
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}
