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
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nammavastra.model.StorySubmission
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.AuthViewModel
import com.nammavastra.viewmodel.SubmissionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorySubmissionScreen(
    authViewModel: AuthViewModel,
    viewModel: SubmissionViewModel,
    snackbarHostState: SnackbarHostState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var weaverName by remember { mutableStateOf(authState.resolvedDisplayName) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("history") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Share Your Story", style = MaterialTheme.typography.displayMedium)
        Text(
            "Submit your story and profile image for admin review. Approved stories will appear in Weaver Stories and Meet the Weavers.",
            style = MaterialTheme.typography.bodyLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, ZariGold), RoundedCornerShape(18.dp))
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Icon(Icons.Filled.PhotoCamera, null, tint = ZariGold)
                    Text("Tap to add profile/story image", modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected story image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        OutlinedTextField(weaverName, { weaverName = it }, label = { Text("Weaver Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(subtitle, { subtitle = it }, label = { Text("Short Tagline") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(village, { village = it }, label = { Text("Village / Cluster") }, modifier = Modifier.fillMaxWidth())
        DropdownSectionField(section = section, onSelected = { section = it })
        OutlinedTextField(title, { title = it }, label = { Text("Story Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Story Content") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )

        Button(
            onClick = {
                val selectedUri = imageUri
                if (selectedUri == null) {
                    return@Button
                }
                viewModel.submit(
                    submission = StorySubmission(
                        title = title,
                        content = content,
                        section = section,
                        weaverName = weaverName,
                        village = village,
                        subtitle = subtitle,
                        submittedBy = authState.resolvedEmail
                    ),
                    imageUri = selectedUri,
                    contentResolver = context.contentResolver,
                    onSuccess = {
                        onDone()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Send to Admin Review")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSectionField(
    section: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("history", "heritage", "craft", "village")
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = section,
            onValueChange = { },
            readOnly = true,
            label = { Text("Story Section") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
