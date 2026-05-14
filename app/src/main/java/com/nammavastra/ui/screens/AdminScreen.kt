package com.nammavastra.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nammavastra.model.Saree
import com.nammavastra.model.StorySubmission
import com.nammavastra.model.WeaverProfile
import com.nammavastra.model.WeaverStory
import com.nammavastra.ui.components.EmptyState
import com.nammavastra.ui.components.FullScreenLoading
import com.nammavastra.ui.components.PlaceholderImage
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.SilkMaroon
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.AdminViewModel

private enum class AdminTab(val label: String) {
    Requests("Accept Requests"),
    Gallery("Loom Gallery"),
    Stories("Stories"),
    Weavers("Weavers")
}

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(AdminTab.Requests) }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    when {
        state.isLoading -> FullScreenLoading()
        else -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Admin Dashboard", style = MaterialTheme.typography.displayMedium, color = ZariGold)
                Text(
                    "Manage approvals, loom listings, published stories, and visible weaver profiles.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedText
                )
                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SilkMaroon
                    )
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp
            ) {
                AdminTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.label) }
                    )
                }
            }

            when (selectedTab) {
                AdminTab.Requests -> RequestsTab(
                    submissions = state.submissions,
                    onRefresh = viewModel::refresh,
                    onApprove = viewModel::approveSubmission,
                    onReject = { viewModel.rejectSubmission(it) }
                )
                AdminTab.Gallery -> GalleryTab(
                    sarees = state.sarees,
                    onRefresh = viewModel::refresh,
                    onRemove = { viewModel.removeSaree(it) }
                )
                AdminTab.Stories -> StoriesTab(
                    stories = state.publishedStories,
                    onRefresh = viewModel::refresh,
                    onRemove = { viewModel.removeStory(it) }
                )
                AdminTab.Weavers -> WeaversTab(
                    weavers = state.publishedWeavers,
                    onRefresh = viewModel::refresh,
                    onRemove = { viewModel.removeWeaver(it) }
                )
            }
        }
    }
}

@Composable
private fun RequestsTab(
    submissions: List<StorySubmission>,
    onRefresh: () -> Unit,
    onApprove: (StorySubmission) -> Unit,
    onReject: (String) -> Unit
) {
    if (submissions.isEmpty()) {
        EmptyState(
            title = "No pending requests",
            body = "New weaver submissions will appear here for review.",
            actionLabel = "Refresh",
            onAction = onRefresh
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(submissions) { submission ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlaceholderImage(
                        model = submission.imageUrl,
                        contentDescription = submission.title,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(submission.title, style = MaterialTheme.typography.titleMedium, color = DeepCharcoal)
                    Text("${submission.weaverName} • ${submission.village}", color = SilkMaroon)
                    Text(
                        submission.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onApprove(submission) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Approve")
                        }
                        OutlinedButton(
                            onClick = { onReject(submission.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryTab(
    sarees: List<Saree>,
    onRefresh: () -> Unit,
    onRemove: (Saree) -> Unit
) {
    if (sarees.isEmpty()) {
        EmptyState(
            title = "No loom listings",
            body = "Gallery items fetched from Supabase will appear here for removal.",
            actionLabel = "Refresh",
            onAction = onRefresh
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sarees) { saree ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlaceholderImage(
                        model = saree.imageUrl,
                        contentDescription = saree.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(saree.name, style = MaterialTheme.typography.titleMedium, color = DeepCharcoal)
                    Text("${saree.weaverName} • ${saree.location}", color = MutedText)
                    Text("ID: ${saree.id}", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    OutlinedButton(onClick = { onRemove(saree) }) {
                        Text("Remove Post")
                    }
                }
            }
        }
    }
}

@Composable
private fun StoriesTab(
    stories: List<WeaverStory>,
    onRefresh: () -> Unit,
    onRemove: (String) -> Unit
) {
    if (stories.isEmpty()) {
        EmptyState(
            title = "No published stories",
            body = "Approved stories will appear here after publication.",
            actionLabel = "Refresh",
            onAction = onRefresh
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(stories) { story ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlaceholderImage(
                        model = story.imageUrl,
                        contentDescription = story.title,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Text(story.title, style = MaterialTheme.typography.titleMedium, color = DeepCharcoal)
                    Text("${story.weaverName} • ${story.village}", color = SilkMaroon)
                    Text(
                        story.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedButton(onClick = { onRemove(story.id) }) {
                        Text("Remove Story")
                    }
                }
            }
        }
    }
}

@Composable
private fun WeaversTab(
    weavers: List<WeaverProfile>,
    onRefresh: () -> Unit,
    onRemove: (String) -> Unit
) {
    if (weavers.isEmpty()) {
        EmptyState(
            title = "No weaver profiles",
            body = "Approved weaver profiles will appear here after publication.",
            actionLabel = "Refresh",
            onAction = onRefresh
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(weavers) { weaver ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlaceholderImage(
                        model = weaver.imageUrl,
                        contentDescription = weaver.name,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Text(weaver.name, style = MaterialTheme.typography.titleMedium, color = DeepCharcoal)
                    Text(weaver.village, color = SilkMaroon)
                    Text(
                        weaver.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedButton(onClick = { onRemove(weaver.id) }) {
                        Text("Remove Weaver")
                    }
                }
            }
        }
    }
}
