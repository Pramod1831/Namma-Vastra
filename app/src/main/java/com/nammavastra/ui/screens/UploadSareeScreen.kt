package com.nammavastra.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nammavastra.model.Saree
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.AuthViewModel
import com.nammavastra.viewmodel.GalleryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadSareeScreen(
    viewModel: GalleryViewModel,
    snackbarHostState: SnackbarHostState,
    onDone: () -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uploading by viewModel.uploading.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var fabricType by remember { mutableStateOf("Silk") }
    var priceRange by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Ilkal") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("List Your Saree", style = MaterialTheme.typography.displayMedium)
        Text(
            "Share your handcrafted work with the gallery and boutique buyers.",
            style = MaterialTheme.typography.bodyLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, ZariGold), RoundedCornerShape(16.dp))
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Icon(Icons.Filled.PhotoCamera, null, tint = ZariGold)
                    Text("Tap to add photo", modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected saree image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        OutlinedTextField(name, { name = it }, label = { Text("Saree Name") }, modifier = Modifier.fillMaxWidth())
        DropdownField("Fabric Type", fabricType, listOf("Silk", "Cotton")) { fabricType = it }
        OutlinedTextField(priceRange, { priceRange = it }, label = { Text("Price Range") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(whatsapp, { whatsapp = it }, label = { Text("WhatsApp Number") }, modifier = Modifier.fillMaxWidth())
        DropdownField("Location", location, listOf("Ilkal", "Molakalmuru")) { location = it }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Artisan Story") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Button(
            onClick = {
                val selectedUri = imageUri
                if (selectedUri == null) {
                    scope.launch { snackbarHostState.showSnackbar("Please choose an image first.") }
                    return@Button
                }
                viewModel.uploadSaree(
                    saree = Saree(
                        name = name,
                        weaverName = authState.resolvedDisplayName.ifBlank { "Artisan Listing" },
                        fabricType = fabricType,
                        priceRange = priceRange,
                        location = location,
                        whatsappNumber = whatsapp,
                        description = description
                    ),
                    imageUri = selectedUri,
                    contentResolver = context.contentResolver,
                    onSuccess = {
                        scope.launch { snackbarHostState.showSnackbar("Saree posted successfully.") }
                        onDone()
                    },
                    onError = { message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uploading) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Post to Gallery")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
