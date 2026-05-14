package com.nammavastra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.theme.SoftBorder
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.ui.theme.AppSurface
import com.nammavastra.ui.theme.DeepCharcoal

@Composable
fun AppTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(width = 1.dp, color = SoftBorder.copy(alpha = 0.55f)),
        color = AppSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepCharcoal
                        )
                    }
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic,
                color = ZariGold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (showBack) 6.dp else 14.dp)
            )
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                if (!showBack) {
                    IconButton(onClick = { onMenuClick?.invoke() }) {
                        Icon(Icons.Outlined.Menu, contentDescription = null, tint = DeepCharcoal)
                    }
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
