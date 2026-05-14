package com.nammavastra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nammavastra.model.WeaverStory
import com.nammavastra.ui.components.EmptyState
import com.nammavastra.ui.components.FullScreenLoading
import com.nammavastra.ui.components.PlaceholderImage
import com.nammavastra.ui.components.StoryCard
import com.nammavastra.ui.components.WeaverProfileCard
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.SilkMaroon
import com.nammavastra.viewmodel.StoryViewModel

@Composable
fun WeaverStoryScreen(
    viewModel: StoryViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val state by viewModel.uiState.collectAsState()
    val weaverImageByKey = state.weavers.associateBy { "${it.name}|${it.village}" }
    val resolvedStories = state.storiesState.data.map { story ->
        if (story.imageUrl.isBlank()) {
            val key = "${story.weaverName}|${story.village}"
            story.copy(imageUrl = weaverImageByKey[key]?.imageUrl.orEmpty())
        } else {
            story
        }
    }
    val banner = resolvedStories.firstOrNull()?.imageUrl
        ?: state.weavers.firstOrNull()?.imageUrl
        .orEmpty()
    val expandedIds = remember { mutableStateListOf<String>() }

    when {
        state.storiesState.isLoading && state.storiesState.data.isEmpty() -> FullScreenLoading()
        state.storiesState.errorMessage != null && state.storiesState.data.isEmpty() -> EmptyState(
            title = "Stories unavailable",
            body = state.storiesState.errorMessage ?: "Please try again.",
            actionLabel = "Retry",
            onAction = viewModel::refresh
        )

        else -> LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                PlaceholderImage(
                    model = banner,
                    contentDescription = "Weaver banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Weaver Stories", style = MaterialTheme.typography.displayMedium, color = SilkMaroon)
                    Text(
                        "Tap a story card to read the full note from the curated archive of techniques, villages, and artisan histories.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MutedText
                    )
                }
            }
            items(resolvedStories) { story ->
                StoryStoryCard(
                    story = story,
                    expanded = expandedIds.contains(story.id),
                    onToggle = {
                        if (expandedIds.contains(story.id)) expandedIds.remove(story.id) else expandedIds.add(story.id)
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Meet the Weavers", style = MaterialTheme.typography.titleLarge, color = DeepCharcoal)
                    Text(
                        "Profile cards for featured artisans and clusters currently highlighted in the app.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.weavers) { weaver ->
                            WeaverProfileCard(weaver = weaver)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryStoryCard(
    story: WeaverStory,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StoryCard(story = story, modifier = Modifier.fillMaxWidth())
        Text(
            text = if (expanded) story.content else story.content,
            style = MaterialTheme.typography.bodyMedium,
            color = DeepCharcoal,
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
        Text(
            text = if (expanded) "Tap to collapse" else "Tap to read more",
            style = MaterialTheme.typography.labelMedium,
            color = SilkMaroon,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}
