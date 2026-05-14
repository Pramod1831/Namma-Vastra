package com.nammavastra.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.components.EmptyState
import com.nammavastra.ui.components.FullScreenLoading
import com.nammavastra.ui.components.SareeCard
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.GalleryViewModel

@Composable
fun LoomGalleryScreen(
    viewModel: GalleryViewModel,
    onOpenDetail: (String) -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.startObserving()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading && state.data.isEmpty() -> FullScreenLoading()
            state.errorMessage != null && state.data.isEmpty() -> EmptyState(
                title = "Gallery unavailable",
                body = state.errorMessage ?: "Please try again.",
                actionLabel = "Retry",
                onAction = viewModel::startObserving
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Text(
                        text = "Loom Gallery",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(state.data) { saree ->
                    SareeCard(
                        saree = saree,
                        onClick = { onOpenDetail(saree.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onUpload,
            containerColor = ZariGold,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Upload")
        }
    }
}
