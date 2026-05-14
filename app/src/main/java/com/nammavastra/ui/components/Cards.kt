package com.nammavastra.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.nammavastra.model.Saree
import com.nammavastra.model.Trend
import com.nammavastra.model.WeaverProfile
import com.nammavastra.model.WeaverStory
import com.nammavastra.ui.theme.AppSurface
import com.nammavastra.ui.theme.AppSurfaceAlt
import com.nammavastra.ui.theme.CardShadow
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedGreen
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.PaperWhite
import com.nammavastra.ui.theme.PlaceholderMaroon
import com.nammavastra.ui.theme.RobotoMonoFamily
import com.nammavastra.ui.theme.SilkMaroon
import com.nammavastra.ui.theme.SoftGold
import com.nammavastra.ui.theme.ZariGold

@Composable
fun TrendCard(trend: Trend, modifier: Modifier = Modifier) {
    val cardColors = trend.colors.map {
        runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrDefault(ZariGold)
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = cardColors.ifEmpty { listOf(ZariGold, SilkMaroon) }
                    )
                )
                .height(304.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x22000000),
                                Color(0x12000000),
                                Color(0x88000000)
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = trend.month.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = SoftGold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = trend.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = PaperWhite
                    )
                    Text(
                        text = trend.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PaperWhite.copy(alpha = 0.94f),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        trend.colors.take(3).forEach { hex ->
                            val swatch = runCatching {
                                Color(android.graphics.Color.parseColor(hex))
                            }.getOrDefault(Color.White)
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(swatch)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.Black.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "Boutique saree direction",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftGold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                PlaceholderImage(
                    model = trend.imageUrl,
                    contentDescription = trend.name,
                    modifier = Modifier
                        .width(110.dp)
                        .height(156.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun SareeCard(
    saree: Saree,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface)
    ) {
        PlaceholderImage(
            model = saree.imageUrl,
            contentDescription = saree.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(14.dp)
                .heightIn(min = 150.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = if (saree.fabricType.equals("Silk", true)) SilkMaroon else MutedGreen
                ) {
                    Text(
                        text = saree.fabricType,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = SilkMaroon)
                    Text(saree.location, style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
            }
            Text(
                saree.weaverName,
                style = MaterialTheme.typography.titleMedium,
                color = DeepCharcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = saree.priceRange,
                fontFamily = RobotoMonoFamily,
                color = SilkMaroon,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun StoryCard(story: WeaverStory, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PlaceholderImage(
                model = story.imageUrl,
                contentDescription = story.title,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(story.title, style = MaterialTheme.typography.titleMedium, color = DeepCharcoal)
                Text(
                    story.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WeaverProfileCard(weaver: WeaverProfile, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.widthInOrFill(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlaceholderImage(
            model = weaver.imageUrl,
            contentDescription = weaver.name,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(12.dp))
        Text(weaver.name, style = MaterialTheme.typography.titleMedium, color = DeepCharcoal)
        AssistChip(
            onClick = { },
            label = { Text(weaver.village) },
            colors = AssistChipDefaults.assistChipColors(containerColor = ZariGold.copy(alpha = 0.15f)),
            border = BorderStroke(0.dp, Color.Transparent)
        )
        Text(
            weaver.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
            fontStyle = FontStyle.Italic
        )
    }
}

private fun Modifier.widthInOrFill(): Modifier = this.padding(horizontal = 4.dp)

@Composable
fun PlaceholderImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    icon: ImageVector = Icons.Filled.Image
) {
    if (model == null || (model is String && model.isBlank())) {
        PlaceholderBox(
            contentDescription = contentDescription,
            modifier = modifier,
            icon = icon
        )
    } else {
        SubcomposeAsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        ) {
            when (painter.state) {
                is coil.compose.AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                else -> PlaceholderBox(
                    contentDescription = contentDescription,
                    modifier = Modifier.matchParentSize(),
                    icon = icon
                )
            }
        }
    }
}

@Composable
private fun PlaceholderBox(
    contentDescription: String?,
    modifier: Modifier,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .background(AppSurfaceAlt)
            .padding(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppSurfaceAlt,
                            CardShadow.copy(alpha = 0.18f)
                        )
                    )
                )
        )
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = PlaceholderMaroon,
            modifier = Modifier.size(40.dp)
        )
    }
}
