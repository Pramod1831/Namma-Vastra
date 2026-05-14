package com.nammavastra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.navigation.AppRoutes
import com.nammavastra.ui.theme.AppSurface
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.ZariGold

data class DrawerItem(
    val label: String,
    val badgeCount: Int = 0,
    val route: String? = null,
    val action: String? = null
)

@Composable
fun AppDrawerContent(
    items: List<DrawerItem>,
    currentRoute: String?,
    onItemClick: (DrawerItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.78f)
            .background(AppSurface)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Namma-Vastra",
            style = MaterialTheme.typography.displayMedium,
            color = ZariGold
        )
        Text(
            text = "Explore the marketplace",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText
        )
        items.forEach { item ->
            val selected = item.route != null && currentRoute == item.route
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) ZariGold else DeepCharcoal
                )
                if (item.badgeCount > 0) {
                    Text(
                        text = item.badgeCount.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = ZariGold
                    )
                }
            }
        }
    }
}

fun defaultDrawerItems(isAdmin: Boolean, cartCount: Int): List<DrawerItem> = buildList {
    add(DrawerItem(label = "Profile", route = AppRoutes.Account))
    add(DrawerItem("Inquiry Cart", badgeCount = cartCount, route = AppRoutes.Cart))
    add(DrawerItem(label = "List Your Saree", route = AppRoutes.Upload))
    add(DrawerItem(label = "Share Your Story", route = AppRoutes.StorySubmission))
    if (isAdmin) {
        add(DrawerItem(label = "Admin Dashboard", route = AppRoutes.Admin))
    }
    add(DrawerItem(label = "Sign Out", action = "sign_out"))
}
