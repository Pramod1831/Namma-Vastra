package com.nammavastra.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.nammavastra.model.Saree
import com.nammavastra.ui.components.PlaceholderImage
import com.nammavastra.ui.theme.RobotoMonoFamily
import com.nammavastra.ui.theme.WhatsAppGreen
import com.nammavastra.ui.theme.ZariGold
import kotlinx.coroutines.launch

@Composable
fun SareeDetailScreen(
    saree: Saree,
    isInCart: Boolean,
    onToggleCart: (Saree) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        PlaceholderImage(
            model = saree.imageUrl,
            contentDescription = saree.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(saree.weaverName, style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { }, label = { Text(saree.fabricType) })
                AssistChip(
                    onClick = { },
                    label = { Text(saree.location) },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.LocationOn, null) },
                    colors = AssistChipDefaults.assistChipColors()
                )
            }
            Text(
                text = saree.priceRange,
                style = MaterialTheme.typography.displayMedium,
                fontFamily = RobotoMonoFamily,
                color = ZariGold
            )
            Button(
                onClick = { onToggleCart(saree) },
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Icon(
                    imageVector = if (isInCart) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = null
                )
                Text(
                    text = if (isInCart) "Remove from Inquiry Cart" else "Add to Inquiry Cart",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(saree.description, style = MaterialTheme.typography.bodyLarge)
            Button(
                onClick = {
                    val uri = Uri.parse(
                        "https://wa.me/${saree.whatsappNumber}?text=Hello! I saw your ${saree.name} on Namma-Vastra and would like to inquire about it."
                    )
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (_: ActivityNotFoundException) {
                        scope.launch {
                            snackbarHostState.showSnackbar("WhatsApp unavailable. Contact: ${saree.whatsappNumber}")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Icon(Icons.Filled.Send, contentDescription = null)
                Text("Inquire via WhatsApp", modifier = Modifier.padding(start = 8.dp))
            }
            Text(
                text = "Handloom detail curated for boutique discovery.",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
