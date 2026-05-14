package com.nammavastra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.components.EmptyState
import com.nammavastra.ui.components.PlaceholderImage
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.RobotoMonoFamily
import com.nammavastra.ui.theme.SilkMaroon
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.CartViewModel

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onOpenDetail: (String) -> Unit,
    onBrowseGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.items.collectAsState()

    if (items.isEmpty()) {
        EmptyState(
            title = "Inquiry Cart",
            body = "Shortlisted sarees will appear here. Add pieces from the gallery or detail screen to keep buyer inquiries organized.",
            actionLabel = "Browse Gallery",
            onAction = onBrowseGallery
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Inquiry Cart",
                style = MaterialTheme.typography.displayMedium,
                color = ZariGold
            )
        }
        item {
            Text(
                text = "Use this shortlist while talking to buyers or before opening WhatsApp inquiries.",
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText
            )
        }
        item {
            OutlinedButton(onClick = viewModel::clear) {
                Text("Clear Cart")
            }
        }
        items(items) { saree ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlaceholderImage(
                        model = saree.imageUrl,
                        contentDescription = saree.name,
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = saree.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepCharcoal
                        )
                        Text(
                            text = saree.weaverName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = saree.priceRange,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = RobotoMonoFamily,
                            color = SilkMaroon
                        )
                        Text(
                            text = saree.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            fontStyle = FontStyle.Italic
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onOpenDetail(saree.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(min = 0.dp)
                            ) {
                                Text("Open", maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                            }
                            OutlinedButton(
                                onClick = { viewModel.remove(saree.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(min = 0.dp)
                            ) {
                                Text("Remove", maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
