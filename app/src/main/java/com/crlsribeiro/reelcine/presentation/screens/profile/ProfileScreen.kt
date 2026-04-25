package com.crlsribeiro.reelcine.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.crlsribeiro.reelcine.R
import com.crlsribeiro.reelcine.presentation.theme.Violet

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estados para os Dialogs
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadProfile()
        }
    }

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) onSignOut()
    }

    LaunchedEffect(uiState.isAccountDeleted) {
        if (uiState.isAccountDeleted) onSignOut()
    }

    // --- DIALOGS ---

    // 1. Dialog de Atribuição TMDB (Obrigatório para Release)
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About ReelCine", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // 2. Dialog de Confirmação de Deleção
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This action is permanent and cannot be undone. All your data, recommendations and watchlist will be deleted.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Loading e Erro (States)
    if (uiState.isDeletingAccount) {
        AlertDialog(onDismissRequest = {}, title = { Text("Deleting account...") },
            text = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Violet) } },
            confirmButton = {}
        )
    }

    uiState.deleteError?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearDeleteError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = { TextButton(onClick = { viewModel.clearDeleteError() }) { Text("OK") } }
        )
    }

    // --- CONTEÚDO PRINCIPAL ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar
        Surface(
            shape = CircleShape,
            color = Violet.copy(alpha = 0.2f),
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.user?.name?.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Violet
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiState.user?.name ?: stringResource(R.string.profile_default_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = uiState.user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        uiState.user?.bio?.takeIf { it.isNotBlank() }?.let { bio ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(label = stringResource(R.string.profile_movies), value = uiState.movieCount.toString())
            StatCard(label = stringResource(R.string.profile_recommendations), value = uiState.reviewCount.toString())
            StatCard(label = stringResource(R.string.profile_groups), value = uiState.groupCount.toString())
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Menu Itens
        ProfileMenuItem(
            icon = { Icon(Icons.Default.Bookmarks, contentDescription = null, tint = Violet) },
            title = stringResource(R.string.profile_watchlist),
            onClick = onWatchlistClick
        )
        ProfileMenuItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Violet) },
            title = stringResource(R.string.profile_edit),
            onClick = onEditProfileClick
        )

        // NOVO ITEM: ABOUT (TMDB Attribution)
        ProfileMenuItem(
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Violet) },
            title = "About ReelCine",
            onClick = { showAboutDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        OutlinedButton(
            onClick = { viewModel.signOut() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.profile_sign_out))
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Account", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Violet)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ProfileMenuItem(
    icon: @Composable () -> Unit,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}