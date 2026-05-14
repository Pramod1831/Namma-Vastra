package com.nammavastra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.navigation.BottomDestination
import com.nammavastra.ui.theme.BottomBarBrown
import com.nammavastra.ui.theme.CardShadow
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.SoftBorder
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.ui.theme.AppSurface

@Composable
fun BottomNavigationBar(
    current: BottomDestination,
    onSelected: (BottomDestination) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BottomBarBrown
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp)
                .background(BottomBarBrown)
                .border(1.dp, SoftBorder.copy(alpha = 0.7f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomDestination.entries.forEach { destination ->
                val icon = when (destination) {
                    BottomDestination.Trend -> Icons.Filled.AutoAwesome
                    BottomDestination.Gallery -> Icons.Filled.GridView
                    BottomDestination.Calculator -> Icons.Filled.Calculate
                    BottomDestination.Story -> Icons.Filled.MenuBook
                }
                val selected = current == destination
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelected(destination) }
                        .padding(horizontal = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selected) DeepCharcoal else Color.Transparent,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected) DeepCharcoal else Color.Transparent,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = destination.label,
                            tint = if (selected) AppSurface else DeepCharcoal.copy(alpha = 0.82f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) DeepCharcoal else DeepCharcoal.copy(alpha = 0.68f),
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.padding(top = 7.dp)
                    )
                }
            }
        }
    }
}
