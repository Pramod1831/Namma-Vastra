package com.nammavastra.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nammavastra.model.Trend
import com.nammavastra.ui.components.EmptyState
import com.nammavastra.ui.components.FullScreenLoading
import com.nammavastra.ui.components.InsightChipRow
import com.nammavastra.ui.components.MarketPulsePanel
import com.nammavastra.ui.components.TrendCard
import com.nammavastra.viewmodel.TrendViewModel

@Composable
fun TrendBoardScreen(
    viewModel: TrendViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val featuredTrends: List<Trend> = state.data.take(4)
    val colorBankTrends: List<Trend> = state.data.drop(4)

    when {
        state.isLoading && state.data.isEmpty() -> FullScreenLoading()
        state.errorMessage != null && state.data.isEmpty() -> {
            EmptyState(
                title = "Trends unavailable",
                body = state.errorMessage ?: "Please try again.",
                actionLabel = "Retry",
                onAction = viewModel::refresh
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "AI CURATED FOR ARTISANS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "This Month's Trends",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Discover boutique-facing colour stories tailored for Ilkal and Molakalmuru weavers.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        InsightChipRow(
                            chips = listOf("Boutique demand", "Wedding edit", "Urban cotton"),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    MarketPulsePanel(
                        title = "Market Pulse",
                        body = "A quick buying brief to help weavers decide what to weave next before they commit time and yarn.",
                        bullets = listOf(
                            "Soft festive sarees are moving faster in pastel and muted jewel combinations.",
                            "Boutique buyers want one hero statement saree for every 3 wearable everyday pieces.",
                            "Detailed borders and premium pallus are outperforming all-over heavy ornamentation."
                        )
                    )
                }

                item {
                    MarketPulsePanel(
                        title = "Artisan Spotlight",
                        body = "Ilkal and Molakalmuru makers can turn trend boards into stronger sell-through by pairing local weaving signatures with modern finishing.",
                        bullets = listOf(
                            "Use contrast pallus to make boutique photos pop instantly.",
                            "Name each saree around mood and occasion, not only weave type.",
                            "Keep one premium piece in every upload set to elevate the full collection."
                        )
                    )
                }

                if (featuredTrends.isNotEmpty()) {
                    item {
                        Text(
                            text = "Trend Directions",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(featuredTrends) { trend ->
                        TrendCard(trend = trend, modifier = Modifier.fillMaxWidth())
                    }
                }

                if (colorBankTrends.isNotEmpty()) {
                    item {
                        Text(
                            text = "Color Bank",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    items(colorBankTrends) { trend ->
                        TrendCard(trend = trend, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
