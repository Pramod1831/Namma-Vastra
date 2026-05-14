package com.nammavastra.ui.screens

import android.app.Activity
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.nammavastra.BuildConfig
import com.nammavastra.ui.theme.AppSurface
import com.nammavastra.ui.theme.MutedText
import com.nammavastra.ui.theme.SilkMaroon
import com.nammavastra.ui.theme.ZariGold
import com.nammavastra.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val googleClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()
    )
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            viewModel.reportGoogleError("Google sign-in was cancelled.")
            return@rememberLauncherForActivityResult
        }
        runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
                .idToken
        }.onSuccess { token ->
            if (!token.isNullOrBlank()) {
                viewModel.signInWithGoogle(token)
            } else {
                viewModel.reportGoogleError(
                    "Google sign-in returned no ID token. Check Firebase Google Auth setup and SHA fingerprints."
                )
            }
        }.onFailure { throwable ->
            val message = if (throwable is ApiException) {
                when (throwable.statusCode) {
                    CommonStatusCodes.NETWORK_ERROR -> "Network error during Google sign-in."
                    CommonStatusCodes.SIGN_IN_REQUIRED -> "Google sign-in was not completed."
                    10 -> "Google sign-in is misconfigured. Add SHA-1/SHA-256 for this Android app in Firebase."
                    else -> "Google sign-in failed with code ${throwable.statusCode}."
                }
            } else {
                throwable.message ?: "Google sign-in failed."
            }
            viewModel.reportGoogleError(message)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Namma-Vastra",
            style = MaterialTheme.typography.displayLarge,
            color = ZariGold
        )
        Text(
            text = if (state.isSignUp) "Create your artisan account" else "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (state.isSignUp) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::updateName,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Name") },
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::updateEmail,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::updatePassword,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (state.isSignUp) {
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::updateConfirmPassword,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
                state.errorMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SilkMaroon
                    )
                }
                Button(
                    onClick = viewModel::submit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    }
                    Text(if (state.isSignUp) "Create Account" else "Sign In")
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                AndroidView(
                    factory = { viewContext ->
                        SignInButton(viewContext).apply {
                            setSize(SignInButton.SIZE_WIDE)
                            setColorScheme(SignInButton.COLOR_LIGHT)
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            setOnClickListener {
                                launcher.launch(googleClient.signInIntent)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    onClick = viewModel::toggleMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (state.isSignUp) "Already have an account? Sign in" else "New here? Create an account",
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}
