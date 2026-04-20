package com.crlsribeiro.reelcine.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crlsribeiro.reelcine.R
import com.crlsribeiro.reelcine.presentation.theme.Gold
import com.crlsribeiro.reelcine.presentation.theme.Violet
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private val BgTop = Color(0xFF1A1530)
private val BgMid = Color(0xFF0D0B18)
private val BgBottom = Color(0xFF080810)
private val SurfaceField = Color(0xFF0F0D1E)
private val BorderField = Color(0xFF242038)
private val BorderDivider = Color(0xFF1E1C30)
private val TextHint = Color(0xFF6A5FAA)
private val TextMuted = Color(0xFF3E3A58)
private val TextLabel = Color(0xFF5A547A)
private val BtnGoogle = Color(0xFF16142A)
private val BtnGoogleBorder = Color(0xFF2E2A4A)
private val BtnPrimary = Color(0xFF6C4DE0)
private val BtnPrimaryDisabled = Color(0xFF3D2A8A)

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateToHome()
    }

    fun signInWithGoogle() {
        scope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    viewModel.signInWithGoogle(googleIdTokenCredential.idToken)
                }
            } catch (e: Exception) {
                viewModel.onGoogleSignInError(e.message ?: "Google Sign-In failed")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(BgTop, BgMid, BgBottom)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_clapperboard),
                contentDescription = "ReelCine",
                tint = Color.Unspecified,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Black, color = Color.White, fontSize = 48.sp)) { append("Reel") }
                    withStyle(SpanStyle(fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic, color = Violet, fontSize = 48.sp)) { append("Cine") }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = TextLabel,
                letterSpacing = 2.5.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { signInWithGoogle() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnGoogle,
                    contentColor = Color(0xFFD0CCE8),
                    disabledContainerColor = BtnGoogle.copy(alpha = 0.5f),
                    disabledContentColor = Color(0xFFD0CCE8).copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BtnGoogleBorder),
                enabled = !uiState.isLoading
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_google), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(stringResource(R.string.login_google), fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderDivider)
                Text(" E-MAIL ", style = MaterialTheme.typography.labelSmall, color = TextMuted, letterSpacing = 2.5.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderDivider)
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail", color = TextHint.copy(alpha = 0.6f), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceField, unfocusedContainerColor = SurfaceField,
                    focusedBorderColor = Violet, unfocusedBorderColor = BorderField,
                    focusedTextColor = Color(0xFFC8C4E8), unfocusedTextColor = Color(0xFFC8C4E8), cursorColor = Violet
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(stringResource(R.string.login_password), color = TextHint.copy(alpha = 0.6f), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = TextMuted)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceField, unfocusedContainerColor = SurfaceField,
                    focusedBorderColor = Violet, unfocusedBorderColor = BorderField,
                    focusedTextColor = Color(0xFFC8C4E8), unfocusedTextColor = Color(0xFFC8C4E8), cursorColor = Violet
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                Text(stringResource(R.string.login_forgot_password), color = Violet, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.signInWithEmail(email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BtnPrimary, contentColor = Color.White, disabledContainerColor = BtnPrimaryDisabled, disabledContentColor = Color.White.copy(alpha = 0.5f))
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.login_button), color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.login_no_account), color = TextMuted, fontSize = 14.sp)
                TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                    Text(stringResource(R.string.login_create_account), color = Gold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                LaunchedEffect(error) { viewModel.clearError() }
            }
        }
    }
}
