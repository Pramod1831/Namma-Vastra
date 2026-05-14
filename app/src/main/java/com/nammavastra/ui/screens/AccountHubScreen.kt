package com.nammavastra.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nammavastra.ui.theme.DeepCharcoal
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.AuthViewModel

@Composable
fun AccountHubScreen(
    authViewModel: AuthViewModel,
    cartCount: Int,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = authViewModel.uiState.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Account & Inquiry", style = MaterialTheme.typography.displayMedium, color = ZariGold)
        Text(
            "Quick access to your inquiry shortlist and visible profile details.",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Inquiry Cart", style = MaterialTheme.typography.titleLarge, color = DeepCharcoal)
                Text(
                    "Open your saved shortlist and continue buyer follow-up. Current items: $cartCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                Button(onClick = onOpenCart) {
                    Text("Open Inquiry Cart")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Profile Visibility", style = MaterialTheme.typography.titleLarge, color = DeepCharcoal)
                Text(
                    text = if (state.resolvedDisplayName.isBlank()) "Name not available" else state.resolvedDisplayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepCharcoal
                )
                Text(
                    text = if (state.resolvedEmail.isBlank()) "Email not available" else state.resolvedEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedText
                )
            }
        }
    }
}
